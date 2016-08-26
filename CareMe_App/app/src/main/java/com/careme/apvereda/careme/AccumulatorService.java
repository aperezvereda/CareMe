package com.careme.apvereda.careme;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nimbees.platform.NimbeesClient;
import com.nimbees.platform.NimbeesException;
import com.nimbees.platform.NimbeesNotificationManager;
import com.nimbees.platform.callbacks.NimbeesCallback;
import com.nimbees.platform.callbacks.NimbeesRegistrationCallback;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import db.RoutineDB;
import entities.History;
import entities.RoutinePlace;
import es.gloin.nimbees.common.beans.notifications.MessageContent;
import es.gloin.nimbees.common.beans.notifications.filters.UserNameListFilter;

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
public class AccumulatorService extends Service {

    LocationManager locManager;
    String provider;
    LocationListener locListener;
    RoutineDB db;
    private final int MIN_DISTANCE = 30;

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Servicio creado", Toast.LENGTH_SHORT).show();
        db = RoutineDB.getInstance(); // Get the DB instance, remember we use a Singleton pattern
        db.init(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences("basicData", Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "");

        //Uncomment to use Nimbees features

        /*if (email.compareTo("") != 0) {
            try {
                // Initialize library calling the init method on the Nimbees Client
                NimbeesClient.init(this);
            } catch (NimbeesException e) {
                e.printStackTrace();
            }
            NimbeesClient.getUserManager().register("email", new NimbeesRegistrationCallback() {
                @Override
                public void onSuccess() {
                    /* Registration was successful!
                    Toast.makeText(getApplicationContext(),
                            "Éxito en el registro", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(NimbeesException failure) {
                    /* Registration failed
                    Toast.makeText(getApplicationContext(),
                            "Fallo en el registro", Toast.LENGTH_LONG).show();
                }
            });
        }
        */
        //Toast.makeText(this,"Servicio arrancado "+ idArranque, Toast.LENGTH_SHORT).show();

        // Obtain a reference to the Location Manager
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.ACCURACY_FINE);
        crit.setPowerRequirement(Criteria.POWER_LOW);
        provider = locManager.getBestProvider(crit, true);

        // And register to obtain current location updates
        locListener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                // Insert a new entry on History
                History history = new History(loc.getLatitude(), loc.getLongitude(), new Date());
                db.insertHistory(history);
                //Uncomment to use Nimbees features
                /*
                try {
                    sendPersonalizado(loc);
                } catch (Exception e) {
                }*/
                /*
                Monitor makes the monitoring of the user to determine if he/she has lost
                */
                monitor(loc);
            }

            @Override
            public void onStatusChanged(String provider, int stat, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        // We want to update the current location every time the user moves MIN_DISTANCE
        locManager.requestLocationUpdates(provider, 0, MIN_DISTANCE, locListener);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        locManager.removeUpdates(locListener);
        //Toast.makeText(this,"Servicio detenido", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendPersonalizado(Location loc) {
        List<Object> filters = new LinkedList<Object>();

        List<Double> coordenates = new LinkedList<Double>();
        coordenates.add(loc.getLatitude());
        coordenates.add(loc.getLongitude());
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences("basicData", Context.MODE_PRIVATE);
        String email = sharedPref.getString("careremail", "");
        if (email.compareTo("") != 0) {
            // User Filter
            List<String> users = new LinkedList<String>();
            users.add(email);
            filters.add(new UserNameListFilter(users));

            // Sends the message to the users in the filter
            NimbeesNotificationManager nimbeesNotificationManager =
                    NimbeesClient.getNotificationManager();
            Gson gson = new Gson();
            nimbeesNotificationManager.sendNotification(gson.toJson(coordenates),
                    MessageContent.NotificationType.CUSTOM, filters, new NimbeesCallback() {
                @Override
                public void onSuccess(Object arg0) {
                    /*Toast.makeText(getApplicationContext(),
                            "Éxito al enviar", Toast.LENGTH_SHORT).show();*/
                }

                @Override
                public void onFailure(NimbeesException arg0) {
                    Log.e("MainActivity",
                            "Excepcion " + arg0.getException() +
                                    " TipoError " + arg0.getErrorType() +
                                    " MensajeError " + arg0.getMessage() +
                                    " Causa " + arg0.getCause()
                    );
                    Toast.makeText(getApplicationContext(),
                            "Error al enviar", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*
       Every five minutes, if the location has updated, we update the list and look for a routine
       the user should be doing or if it is impossible to be following a routine to send an alert.
       If 15 min has passed, and there is no update, we reset the list
     */

    private void monitor(Location loc) {
        // We suppose the user can move 200m in 5 minutes
        SharedPreferences shared = getApplicationContext()
                .getSharedPreferences("monitor", Context.MODE_PRIVATE);
        Date date = new Date();
        date.setTime(shared.getLong("date", 0));
        Date act = new Date();
        if (act.getTime() - date.getTime() > 900000) {
            SharedPreferences.Editor editor = shared.edit();
            editor.putInt("measurements", 0);
            editor.commit();
        }
        if (act.getTime() - date.getTime() > 300000) {
            int measurements = shared.getInt("measurements", 0);
            if (measurements >= 6) {
                // Walking, update the list
                updateList(loc);
            } else {
                // Has stopped, reset the list
                resetList(loc);
            }
            // Update the date
            SharedPreferences.Editor editor = shared.edit();
            act = new Date();
            editor.putLong("date", act.getTime());
            editor.commit();
        } else {
            int measurements = shared.getInt("measurements", 0);
            SharedPreferences.Editor editor = shared.edit();
            editor.putInt("measurements", measurements + 1);
            editor.commit();
        }

    }

    /*
        Resets the list with the routines that departs from the place the user is staying
     */
    private void resetList(Location loc) {
        List<RoutinePlace> routines = db.getAllRoutinePlaces();
        Location l = new Location("Laux");
        Set<String> ids = new HashSet<String>();
        SharedPreferences shared = getApplicationContext()
                .getSharedPreferences("monitor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("probably", Integer.toString(-1));
        editor.commit();
        for (RoutinePlace r : routines) {
            l.setLatitude(r.getPlace().getLatitude());
            l.setLongitude(r.getPlace().getLongitude());
            if (loc.distanceTo(l) < 60) {
                ids.add(r.getRoutine().getId() + "");
                long t1 = new Date().getTime() % 86400000;
                long t2 = r.getRoutine().getStart().getTime() % 86400000;
                if (Math.abs(t1 - t2) < 1800000) {
                    editor.putString("probably", r.getRoutine().getId() + "");
                    editor.commit();
                }
            }
        }
        editor.putStringSet("routines", ids);
        shownot("Reseteamos lista de monitorización");
        editor.commit();
    }

    private void shownot(String s) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_notifications);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_notsmall).setLargeIcon(bitmap)
                .setContentTitle("Cuidado!")
                .setContentText(s)
                .setVibrate(new long[]{100, 250, 100, 500});
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


    /*
        Updates the possibles routines list the user can be following.
        We take how long does it take to the user to complete the routine. With the current
        location of the user we try to infer if the user still can arrive on time. If not,
        we delete the routine from the list
    */
    private void updateList(Location loc) {
        SharedPreferences shared = getApplicationContext()
                .getSharedPreferences("monitor", Context.MODE_PRIVATE);
        Set<String> ids = shared.getStringSet("routines", new HashSet<String>());
        Iterator<String> it = ids.iterator();
        List<RoutinePlace> rlist;
        RoutinePlace r;
        Location l1 = new Location("l1");
        Location l2 = new Location("l2");
        while (it.hasNext()) {
            String id = it.next();
            rlist = db.getRoutinePlaces(Integer.parseInt(id));
            l1.setLongitude(rlist.get(0).getPlace().getLongitude());
            l2.setLongitude(rlist.get(1).getPlace().getLongitude());
            l1.setLatitude(rlist.get(0).getPlace().getLatitude());
            l2.setLatitude(rlist.get(1).getPlace().getLatitude());

            double v1 = (l1.distanceTo(l2) + 500) / (rlist.get(0).getRoutine().getEnd().getTime()
                    - rlist.get(0).getRoutine().getStart().getTime());
            long t1 = new Date().getTime() % 86400000;
            long t2 = rlist.get(0).getRoutine().getEnd().getTime() % 86400000;
            double v2 = (loc.distanceTo(l2)) / (t2 - t1);
            if (v2 > v1) {
                ids.remove(id);
                if (id.compareTo(shared.getString("probably", Integer.toString(-1))) == 0) {
                    shownot("Se ha salido de la rutina más probable!");
                }
            }
        }
        SharedPreferences.Editor editor = shared.edit();
        editor.putStringSet("routines", ids);
        editor.commit();
        if (ids.size() == 0) {
            shownot("Se ha salido de la rutina!");
        }
    }

}
