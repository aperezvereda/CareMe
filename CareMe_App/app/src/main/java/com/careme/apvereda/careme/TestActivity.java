package com.careme.apvereda.careme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

public class TestActivity extends ActionBarActivity {
    TextView log;
    Button placesbtn;
    Button instancesbtn;
    Button clearbtn;
    Button routinesbtn;
    Button rinstancesbtn;
    Button homebtn;
    RoutineDB db;
    File file;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        db = RoutineDB.getInstance();
        db.init(getApplicationContext());
        log = (TextView) findViewById(R.id.txtLog);
        placesbtn = (Button) findViewById(R.id.btnPlaces);
        placesbtn.setOnClickListener(onClickListenerPlaces);
        instancesbtn = (Button) findViewById(R.id.btnInstances);
        instancesbtn.setOnClickListener(onClickListenerInstances);
        clearbtn = (Button) findViewById(R.id.btnClear);
        clearbtn.setOnClickListener(onClickListenerClear);
        rinstancesbtn = (Button) findViewById(R.id.btnRInstances);
        rinstancesbtn.setOnClickListener(onClickListenerRInstances);
        routinesbtn = (Button) findViewById(R.id.btnRoutines);
        routinesbtn.setOnClickListener(onClickListenerRoutines);
        homebtn = (Button) findViewById(R.id.btnHome);
        homebtn.setOnClickListener(onClickListenerHome);
        startService(new Intent(TestActivity.this, AccumulatorService.class));
        //IntentFilter ifilter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        //Intent batteryStatus = getApplicationContext().registerReceiver(new PowerConnectionReceiver(), ifilter);
        File tarjeta = Environment.getExternalStorageDirectory();
        file = new File(tarjeta.getAbsolutePath(), "RoutineLog.txt");

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

    private void enableAlarms() {
        alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
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

    private void printData(){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
            String currentTime = sdf.format(new Date());
            FileWriter fout = new FileWriter(file, true);
            fout.append("\n\n\n==================================================================" +
                    "=========================\n\n" + currentTime+".\n");
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
        }
        catch (Exception ex){
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    Button.OnClickListener onClickListenerPlaces = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Place> result = db.getPlaces();
            if (result.size() == 0) {
                log.append("\nNo hay lugares\n");
            } else {
                log.append("\nLugares:\n");
            }
            for (Place p : result) {
                log.append(p.getDescription());
            }
        }
    };

    Button.OnClickListener onClickListenerInstances = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<PlaceInstance> result = db.getPlaceInstances();
            if (result.size() == 0) {
                log.append("\nNo hay instancias\n");
            } else {
                log.append("\nInstancias:\n");
            }
            for (PlaceInstance p : result) {
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
                log.append(desc);
            }
        }
    };

    Button.OnClickListener onClickListenerRInstances = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<RoutineInstance> result = db.getRoutineInstances();
            if (result.size() == 0) {
                log.append("\nNo hay instancias-rutinas\n");
            } else {
                log.append("\nInstancias-Rutinas:\n");
            }
            for (RoutineInstance p : result) {
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
                log.append(desc);
                try {
                    matches = geocoder.getFromLocation(p.getLatitude2(), p.getLongitude2(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                desc = matches.get(0).getAddressLine(0) + ",  " +
                        matches.get(0).getLocality() + "\n";
                log.append(desc);
            }
        }
    };

    Button.OnClickListener onClickListenerRoutines = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Routine> result = db.getRoutines();
            if (result.size() == 0) {
                log.append("\nNo hay rutinas\n");
            } else {
                log.append("\nRutinas:\n");
            }
            for (Routine r : result) {
                List<RoutinePlace> rp = db.getRoutinePlaces(r.getId());
                Place p = rp.get(0).getPlace();
                log.append(p.getDescription() + "\n a\n");
                p = rp.get(1).getPlace();
                log.append(p.getDescription() + "\n el\n");
                List<RoutineFreq> rf = db.getRoutineFreqs(r.getId());
                for (RoutineFreq rfreq : rf) {
                    log.append(rfreq.getFrequency().getDay() + ", ");
                }
                log.append("\n a las " + r.getStart());
            }
        }
    };

    Button.OnClickListener onClickListenerClear = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            log.setText("");
        }
    };

    Button.OnClickListener onClickListenerHome = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Place> result = db.getPlaces();
            if(result.size() > 0) {
                log.append("\n Casa: " + result.get(0).getDescription());
            }else{
                log.append("\nNo hay datos aun");
            }
        }
    };
}

