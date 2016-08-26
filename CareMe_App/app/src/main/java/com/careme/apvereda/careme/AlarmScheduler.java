package com.careme.apvereda.careme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import db.RoutineDB;
import entities.Routine;
import entities.RoutineFreq;

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

/**
 * Creates alarms for the daily routines to remember them to the user
 */
public class AlarmScheduler extends BroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    RoutineDB db;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Creamos Alarmas", Toast.LENGTH_LONG).show();
        db = RoutineDB.getInstance();
        db.init(context);
        List<RoutineFreq> routines = db.getAllRoutineFreqs();
        Calendar calendar, aux;
        calendar = Calendar.getInstance();
        aux = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        for(RoutineFreq r : routines){
            if(calendar.get(Calendar.DAY_OF_WEEK) == r.getFrequency().getId()){
                Intent intent1 = new Intent(context, DailyAlarm.class);
                intent1.setData(Uri.parse("timer:" + r.getId()));
                alarmIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
                aux.setTimeInMillis(r.getRoutine().getStart().getTime() - 1800000);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, aux.getTimeInMillis(), alarmIntent);
            }
        }
/*
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 39);
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(context, DailyAlarm.class);
        int _id = 1;
        intent2.setData(Uri.parse(""+_id));
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent2, 0);
        //aux.setTimeInMillis(calendar.getTimeInMillis());
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        _id = 2;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        intent2 = new Intent(context, DailyAlarm.class);
        intent2.setData(Uri.parse(""+_id));
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent2, 0);
        //aux.setTimeInMillis(calendar.getTimeInMillis() );
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 60000, alarmIntent);*/
        Toast.makeText(context, "Alarmas Creadas", Toast.LENGTH_LONG).show();
    }
}
