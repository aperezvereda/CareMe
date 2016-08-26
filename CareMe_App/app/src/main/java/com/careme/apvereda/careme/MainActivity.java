package com.careme.apvereda.careme;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AnalogClock;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import db.RoutineDB;
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

public class MainActivity extends ActionBarActivity {
    RoutineDB db;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    List<Routine> routines;
    AdapterForListView adapter;
    ListView listRoutines;
    AnalogClock analog;
    FloatingActionButton fabHome;
    FloatingActionButton fabSOS;
    FloatingActionButton fabCall;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences("basicData", Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "");
        String phone = sharedPref.getString("carerphone", "");
        String carermail = sharedPref.getString("careremail", "");
        if ((email.compareTo("")) == 0 || (phone.compareTo("") == 0) || (carermail.compareTo("") == 0)) {
            Intent intent = new Intent(getApplicationContext(), InitializeActivity.class);
            startActivity(intent);
        }else {
            setContentView(R.layout.activity_main);
            analog = (AnalogClock) findViewById(R.id.analogClock1);
            db = RoutineDB.getInstance();
            db.init(getApplicationContext());
            if(!isMyServiceRunning(AccumulatorService.class)) {
                startService(new Intent(MainActivity.this, AccumulatorService.class));
            }
            File tarjeta = Environment.getExternalStorageDirectory();
            file = new File(tarjeta.getAbsolutePath(), "RoutineLog.txt");
            routines = db.getRoutines();
            fabHome = (FloatingActionButton) findViewById(R.id.fabhome);
            fabHome.setBackgroundTintList(getResources().getColorStateList(R.drawable.fab_color));
            fabHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Place> result = db.getPlaces();
                    if (result.size() > 0) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="
                                + result.get(0).getLatitude()
                                + "," + result.get(0).getLongitude() + "&mode=w");
                        //Uri gmmIntentUri = Uri.parse("google.navigation:q=36.714329,-4.313153");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                }
            });
            fabSOS = (FloatingActionButton) findViewById(R.id.fabsos);
            fabSOS.setBackgroundTintList(getResources().getColorStateList(R.drawable.fab_color));
            fabSOS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + "112"));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
            fabCall = (FloatingActionButton) findViewById(R.id.fabcall);
            fabCall.setBackgroundTintList(getResources().getColorStateList(R.drawable.fab_color));
            fabCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPref = getApplicationContext()
                            .getSharedPreferences("basicData", Context.MODE_PRIVATE);
                    String phone = sharedPref.getString("carerphone", "");
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
            adapter = new AdapterForListView(this, routines);
            listRoutines = (ListView) findViewById(R.id.listRoutines);
            listRoutines.setAdapter(adapter);
            listRoutines.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    Place p = db.getPlace(routines.get(position).getId());
                    Uri gmmIntentUri = Uri.parse("geo:" + p.getLatitude() + "," + p.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        }
    }

    // Verifies if the Accumulator is already running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_data_print) {
            printData();
            return true;
        }
        if (id == R.id.action_enable_alarms) {
            enableAlarms();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Prints a txt file with the db data
    private void printData() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
            String currentTime = sdf.format(new Date());
            FileWriter fout = new FileWriter(file, true);
            fout.append("\n\n\n==================================================================" +
                    "=========================\n\n" + currentTime + ".\n");
            List<Place> result = db.getPlaces();
            if (result.size() == 0) {
                fout.append("\nNo hay lugares\n");
            } else {
                fout.append("\nLugares:\n");
            }
            for (Place p : result) {
                fout.append(p.getDescription());
            }
            List<PlaceInstance> result1 = db.getPlaceInstances();
            if (result1.size() == 0) {
                fout.append("\nNo hay instancias\n");
            } else {
                fout.append("\nInstancias:\n");
            }
            for (PlaceInstance p : result1) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> matches = null;
                try {
                    matches = geocoder.getFromLocation(p.getLatitude(), p.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String desc = "Latitud: " + p.getLatitude() + "; Longitud: " + p.getLongitude() +
                        "; " + p.getStart() + " - " + p.getEnd() + "\n" +
                        matches.get(0).getAddressLine(0) + ",  " +
                        matches.get(0).getLocality() + "\n";
                fout.append(desc);
            }
            List<RoutineInstance> result2 = db.getRoutineInstances();
            if (result2.size() == 0) {
                fout.append("\nNo hay instancias-rutinas\n");
            } else {
                fout.append("\nInstancias-Rutinas:\n");
            }
            for (RoutineInstance p : result2) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> matches = null;
                try {
                    matches = geocoder.getFromLocation(p.getLatitude1(), p.getLongitude1(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String desc = p.getDeparture() + " - " + p.getArrival() + "\n" +
                        matches.get(0).getAddressLine(0) + ",  " +
                        matches.get(0).getLocality() + "-->\n";
                fout.append(desc);
                try {
                    matches = geocoder.getFromLocation(p.getLatitude2(), p.getLongitude2(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                desc = matches.get(0).getAddressLine(0) + ",  " +
                        matches.get(0).getLocality() + "\n";
                fout.append(desc);
            }
            List<Routine> result3 = db.getRoutines();
            if (result3.size() == 0) {
                fout.append("\nNo hay rutinas\n");
            } else {
                fout.append("\nRutinas:\n");
            }
            for (Routine r : result3) {
                List<RoutinePlace> rp = db.getRoutinePlaces(r.getId());
                Place p = rp.get(0).getPlace();
                fout.append(p.getDescription() + "\n a\n");
                p = rp.get(1).getPlace();
                fout.append(p.getDescription() + "\n el\n");
                List<RoutineFreq> rf = db.getRoutineFreqs(r.getId());
                for (RoutineFreq rfreq : rf) {
                    fout.append(rfreq.getFrequency().getDay() + ", ");
                }
                fout.append("\n a las " + r.getStart());
            }
            fout.close();
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    private void enableAlarms() {
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmScheduler.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 30);
        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }


}
