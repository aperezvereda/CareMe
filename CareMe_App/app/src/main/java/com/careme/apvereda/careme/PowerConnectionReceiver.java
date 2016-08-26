package com.careme.apvereda.careme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.BatteryManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import db.DBContract;
import db.RoutineDB;
import entities.Frequency;
import entities.History;
import entities.Place;
import entities.PlaceInstance;
import entities.Routine;
import entities.RoutineFreq;
import entities.RoutineInstance;
import entities.RoutinePlace;

/**
 * Esta obra está sujeta a la licencia Reconocimiento-CompartirIgual 4.0 Internacional de
 * Creative Commons. Para ver una copia de esta licencia,
 * visite http://creativecommons.org/licenses/by-sa/4.0/.
 *
 * CareMe, creado por Alejandro Perez Vereda el 29/7/15.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International
 * License. To view a copy of this license,
 * visit http://creativecommons.org/licenses/by-sa/4.0/.
 *
 * CareMe, created by Alejandro Perez Vereda on 29/7/15.
 *
 * Contact: aperezvereda@gmail.com
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    RoutineDB db;
    Date end, start;
    History lastSaved;
    private final int MIN_DISTANCE = 60;
    private final int MIN_STAY = 900000;
    private final int MIN_REPS = 3;


    @Override
    public void onReceive(Context context, Intent intent) {
        /*//Toast.makeText(context,"Empezamos a analizar", Toast.LENGTH_LONG).show();
        // Is the phone charging?
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        // Charging by USB or CA
        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        // Get the battery level
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;*/

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        start = cal.getTime();
        SharedPreferences sharedPref = context.getSharedPreferences("analysisStart", Context.MODE_PRIVATE);
        start.setTime(sharedPref.getLong("startDate", cal.getTime().getTime()));

        db = RoutineDB.getInstance();
        db.init(context);
        if (start == null) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -2);
            start = cal.getTime();
        }
        end = new Date();
        //Toast.makeText(context, "vamos a analizar" + (end.getTime() - start.getTime()), Toast.LENGTH_SHORT).show();
        // It is necessary half a day have passed to begin a new analysis
        if((end.getTime() - start.getTime()) > 43200000) {
            Toast.makeText(context, "Empezamos a analizar", Toast.LENGTH_SHORT).show();
            List<History> histories = db.getLastHistory(start, end);
            History h1, h2;
            if (histories.size() > 0) {
                Location li1 = new Location("Li1"), li2 = new Location("Li2");
                // Join the first timestamp with the last one of the last analysis
                if (lastSaved != null) {
                    h2 = histories.get(0);
                    h1 = lastSaved;
                    li1.setLongitude(h1.getLongitude());
                    li1.setLatitude(h1.getLatitude());
                    li2.setLongitude(h2.getLongitude());
                    li2.setLatitude(h2.getLatitude());
                    if (li1.distanceTo(li2) > MIN_DISTANCE) {
                        analyzeHistories(context, h1, h2);
                    } else {
                        histories.set(0, lastSaved);
                    }
                }
                lastSaved = histories.get(histories.size() - 1);
                for (int i = 0; i < histories.size() - 1; i++) {
                    h1 = histories.get(i);
                    li1.setLongitude(h1.getLongitude());
                    li1.setLatitude(h1.getLatitude());
                    h2 = histories.get(i + 1);
                    li2.setLongitude(h2.getLongitude());
                    li2.setLatitude(h2.getLatitude());
                    if(li1.distanceTo(li2) < MIN_DISTANCE){
                        histories.set(i+1, h1);
                    }else {
                        analyzeHistories(context, h1, h2);
                    }
                }
            /*
            We get all the PlaceInstances to join them two by two and extract RoutineInstances.
            Then try to obtain Routines if one Instance repeats on the same week day, for example
            if there is the same Instance on two mondays we create the Routine.
             */
                PlaceInstance p1, p2;
                List<PlaceInstance> instances = db.getLastPlaceInstances(start, end);
                if (instances.size() > 0) {
                    for (int i = 0; i < instances.size() - 1; i++) {
                        p1 = instances.get(i);
                        p2 = instances.get(i + 1);
                        analyzeInstances(context, p1, p2);
                    }
                }
            }
            start = new Date();
            start.setTime(start.getTime() + 60000);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("startDate", start.getTime());
            editor.commit();
            //Delete old data


            Toast.makeText(context, "Terminamos analisis", Toast.LENGTH_SHORT).show();
        }
    }

    private void analyzeInstances(Context context, PlaceInstance p1, PlaceInstance p2) {
        int count = 0;
        if (getInterval(p1.getEnd(), p2.getStart()) > MIN_STAY) {
            createRoutineInstance(p1, p2);
            // Calculate number of same Instance
            count = getRInstancesCount(p1, p2);
            if (count > MIN_REPS) {
                //See if it is already in the db and insert or update the Routine
                createRoutine(context, p1, p2, count);
            }
        }
    }

    private void createRoutine(Context context, PlaceInstance p1, PlaceInstance p2, int count) {
        List<Routine> routines = db.getRoutines();
        List<RoutineFreq> routineFreqs;
        List<RoutinePlace> routinePlaces;
        Routine r;
        RoutineFreq rf;
        boolean found = false;
        Location l1 = new Location("l1"), l2 = new Location("l2"), l3 = new Location("l3"),
                l4 = new Location("l4");
        l1.setLatitude(p1.getLatitude());
        l1.setLongitude(p1.getLongitude());
        l2.setLatitude(p2.getLatitude());
        l2.setLongitude(p2.getLongitude());
        for (int c = 0; c < routines.size() && !found; c++) {
            r = routines.get(c);
            routinePlaces = db.getRoutinePlaces(r.getId());
            if (routinePlaces.size() == 2) {
                l3.setLatitude(routinePlaces.get(0).getPlace().getLatitude());
                l3.setLongitude(routinePlaces.get(0).getPlace().getLongitude());
                l4.setLatitude(routinePlaces.get(1).getPlace().getLatitude());
                l4.setLongitude(routinePlaces.get(1).getPlace().getLongitude());
                if (l1.distanceTo(l3) < MIN_DISTANCE && l2.distanceTo(l4) < MIN_DISTANCE) {
                    routineFreqs = db.getRoutineFreqs(r.getId());
                    for (int i = 0; i < routineFreqs.size() && !found; i++) {
                        rf = routineFreqs.get(i);
                        if (getWeekDay(p1.getStart()).equals(rf.getFrequency().getDay())) {
                            //Update reliability
                            if (rf.getReliability() < 95) {
                                rf.setReliability(rf.getReliability() + 5);
                                db.insertRoutineFreq(rf.getRoutine().getId(),
                                        rf.getFrequency().getId(), rf.getReliability(), rf.getId());
                            }
                            found = true;
                        }
                    }
                }
            }
        }
        if (!found) {
            // Insert in the db
            boolean flag1 = false, flag2 = false;
            Place p, place1 = null, place2 = null;
            List<Place> places = db.getPlaces();
            for (int c = 0; c < places.size() && !(flag1 && flag2); c++) {
                p = places.get(c);
                l3.setLatitude(p.getLatitude());
                l3.setLongitude(p.getLongitude());
                if (l1.distanceTo(l3) < MIN_DISTANCE) {
                    place1 = p;
                    flag1 = true;
                }
                if (l2.distanceTo(l3) < MIN_DISTANCE) {
                    place2 = p;
                    flag2 = true;
                }
            }
            if(place1 != null && place2 != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(p1.getStart());
                int id = db.insertRoutine(p1.getStart(), p2.getEnd());
                db.insertRoutinePlace(id, place1, p1.getEnd());
                db.insertRoutinePlace(id, place2, p2.getEnd());
                db.insertRoutineFreq(id, cal.get(Calendar.DAY_OF_WEEK), 15, -1);
            }
        }
    }

    private String getWeekDay(Date d) {
        String day = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                day = "Sunday";
                break;
            case Calendar.MONDAY:
                day = "Monday";
                break;
            case Calendar.TUESDAY:
                day = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                day = "Wednesday";
                break;
            case Calendar.THURSDAY:
                day = "Thursday";
                break;
            case Calendar.FRIDAY:
                day = "Friday";
                break;
            case Calendar.SATURDAY:
                day = "Saturday";
                break;
        }
        return day;
    }

    private int getRInstancesCount(PlaceInstance p1, PlaceInstance p2) {
        List<RoutineInstance> instances = db.getRoutineInstances();
        RoutineInstance r;
        int count = 0;
        Location l1 = new Location("l1"), l2 = new Location("l2"), l3 = new Location("l3"),
                l4 = new Location("l4");
        l1.setLatitude(p1.getLatitude());
        l1.setLongitude(p1.getLongitude());
        l2.setLatitude(p2.getLatitude());
        l2.setLongitude(p2.getLongitude());
        for (int c = 0; c < instances.size(); c++) {
            r = instances.get(c);
            l3.setLatitude(r.getLatitude1());
            l3.setLongitude(r.getLongitude1());
            l4.setLatitude(r.getLatitude2());
            l4.setLongitude(r.getLongitude2());
            if (l1.distanceTo(l3) < MIN_DISTANCE && l2.distanceTo(l4) < MIN_DISTANCE) {
                count++;
            }
        }
        return count;
    }

    private void createRoutineInstance(PlaceInstance p1, PlaceInstance p2) {
        double lon1 = p1.getLongitude();
        double lon2 = p2.getLongitude();
        double lat1 = p1.getLatitude();
        double lat2 = p2.getLatitude();
        Date departure = p1.getEnd();
        Date arrival = p2.getStart();
        RoutineInstance r = new RoutineInstance(lon1, lon2, lat1, lat2, arrival, departure);
        db.insertRoutineInstance(r);
    }

    private void analyzeHistories(Context context, History h1, History h2) {
        int count = 0;
        //If there is more than 10 minutes between the timestamps it is a place to have into account
        if ((getInterval(h1.getDate(), h2.getDate()) > MIN_STAY)) {
            createPlaceInstance(h1, h2);
            //Number of instances already registered
            count = getInstancesCount(h1);
            if (count > MIN_REPS) {
                //See if it is already in the db and insert or update the Place
                createPlace(context, h1, count);
            }
        }
    }

    private boolean createPlace(Context context, History h1, int count) {
        List<Place> places = db.getPlaces();
        Place p;
        boolean found = false;
        Location l1 = new Location("l1"), l2 = new Location("l2");
        l1.setLatitude(h1.getLatitude());
        l1.setLongitude(h1.getLongitude());
        // if we update the place, we make a mean with the latitude and longitude
        for (int c = 0; c < places.size() && !found; c++) {
            p = places.get(c);
            l2.setLatitude(p.getLatitude());
            l2.setLongitude(p.getLongitude());
            if (l1.distanceTo(l2) < MIN_DISTANCE) {
                // Make the mean to be more accurate each time
                double avlat = ((l1.getLatitude() * p.getAssiduity()) + l2.getLatitude())
                        / (p.getAssiduity() + 1);
                double avlon = ((l1.getLongitude() * p.getAssiduity()) + l2.getLongitude())
                        / (p.getAssiduity() + 1);
                p.setLatitude(avlat);
                p.setLongitude(avlon);
                //uodate assiduity
                p.setAssiduity(p.getAssiduity() + 1);
                db.insertPlace(p);
                found = true;
            }
        }
        // if it does not exists yet...
        if (!found) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> matches = null;
            try {
                matches = geocoder.getFromLocation(h1.getLatitude(), h1.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String desc = matches.get(0).getAddressLine(0) + ",  " + matches.get(0).getLocality() + "\n";
            double latitude = h1.getLatitude();
            double longitude = h1.getLongitude();
            Place place = new Place(longitude, latitude, desc, count);
            place.setId(-1);
            db.insertPlace(place);
        }
        return found;
    }

    private int getInstancesCount(History h1) {
        List<PlaceInstance> instances = db.getPlaceInstances();
        PlaceInstance i;
        int count = 0;
        Location l1 = new Location("l1"), l2 = new Location("l2");
        l1.setLatitude(h1.getLatitude());
        l1.setLongitude(h1.getLongitude());
        for (int c = 0; c < instances.size(); c++) {
            i = instances.get(c);
            l2.setLatitude(i.getLatitude());
            l2.setLongitude(i.getLongitude());
            if (l1.distanceTo(l2) < MIN_DISTANCE) {
                count++;
            }
        }
        return count;
    }

    private void createPlaceInstance(History h1, History h2) {
        double lat = h1.getLatitude();
        double lon = h1.getLongitude();
        Date arrive = h1.getDate();
        Date depart = h2.getDate();
        PlaceInstance placeInstance = new PlaceInstance(lat, lon, arrive, depart);
        db.insertPlaceInstance(placeInstance);
    }

    private long getInterval(Date h1, Date h2) {
        long interval;
        if (h1.before(h2)) {
            interval = h2.getTime() - h1.getTime();
        } else {
            interval = h1.getTime() - h2.getTime();
        }
        return interval;
    }
}
