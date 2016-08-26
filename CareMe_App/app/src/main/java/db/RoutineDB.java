package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

/**
 * Methods to maintain and operate with the db. Implements a Singleton
 */
public class RoutineDB {
    private SQLiteDatabase db;

    private static class SingletonHolder {
        private static final RoutineDB INSTANCE = new RoutineDB();
    }

    public void init(Context context) {
        if (db == null) {
            DBHelper helper = new DBHelper(context);
            db = helper.getWritableDatabase();
        }
    }

    public static RoutineDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private RoutineDB() {
    }


    /* -----------------------------------HISTORY METHODS---------------------------------------*/

    public List<History> getHistory() {
        String[] columns = {DBContract.History.COLUMN_NAME_DATE,
                DBContract.History.COLUMN_NAME_LATITUDE, DBContract.History.COLUMN_NAME_LONGITUDE};

        //It's important to put a space before the String DESC to avoid problems on the query construction
        String order = DBContract.History.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(DBContract.History.TABLE_NAME,
                columns, null, null, null, null, order);

        List<History> result = new ArrayList<History>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            double latitude = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.History.COLUMN_NAME_LATITUDE));
            double longitude = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.History.COLUMN_NAME_LONGITUDE));
            Date date = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(DBContract.History.COLUMN_NAME_DATE)));
            History history = new History(latitude, longitude, date);
            result.add(history);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    // Get the last history entries to analyze them
    public List<History> getLastHistory(Date start, Date end) {
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        start = cal.getTime();*/
        String[] columns = {DBContract.History.COLUMN_NAME_LATITUDE,
                DBContract.History.COLUMN_NAME_LONGITUDE, DBContract.History.COLUMN_NAME_DATE};
        String order = DBContract.History.COLUMN_NAME_DATE + " ASC";
        String where = DBContract.History.COLUMN_NAME_DATE + " BETWEEN ? AND ?";
        String[] whereArgs = {getDateTime(start), getDateTime(end)};

        Cursor cursor = db.query(DBContract.History.TABLE_NAME,
                columns, where, whereArgs, null, null, order);

        List<History> result = new ArrayList<History>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            double lat = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.History.COLUMN_NAME_LATITUDE));
            double lon = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.History.COLUMN_NAME_LONGITUDE));
            Date date = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(DBContract.History.COLUMN_NAME_DATE)));
            History h = new History(lat, lon, date);
            result.add(h);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    //Insert history entry
    public boolean insertHistory(History h) {
        ContentValues values = new ContentValues();
        values.put(DBContract.History.COLUMN_NAME_DATE, getDateTime(h.getDate()));
        values.put(DBContract.History.COLUMN_NAME_LATITUDE, h.getLatitude());
        values.put(DBContract.History.COLUMN_NAME_LONGITUDE, h.getLongitude());
        return db.insert(DBContract.History.TABLE_NAME, null, values) != -1;
    }



    /* -------------------------------------PLACES METHODS---------------------------------------*/

    public List<Place> getPlaces() {
        String[] columns = {DBContract.KnownPlace.COLUMN_NAME_DESCRIPTION,
                DBContract.KnownPlace.COLUMN_NAME_LATITUDE,
                DBContract.KnownPlace.COLUMN_NAME_LONGITUDE,
                DBContract.KnownPlace.COLUMN_NAME_ASSIDUITY,
                DBContract.KnownPlace.COLUMN_NAME_ID};

        String order = DBContract.KnownPlace.COLUMN_NAME_ASSIDUITY + " DESC";

        Cursor cursor = db.query(DBContract.KnownPlace.TABLE_NAME,
                columns, null, null, null, null, order);

        List<Place> result = new ArrayList<Place>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            double latitude = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_LATITUDE));
            double longitude = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_LONGITUDE));
            String description = cursor.getString(cursor
                    .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_DESCRIPTION));
            int assiduity = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_ASSIDUITY));
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_ID));
            Place place = new Place(longitude, latitude, description, assiduity);
            place.setId(id);
            result.add(place);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public Place getPlace(int rid) {
        String[] columns = {DBContract.KnownPlace.COLUMN_NAME_DESCRIPTION,
                DBContract.KnownPlace.COLUMN_NAME_ASSIDUITY,
                DBContract.KnownPlace.COLUMN_NAME_LATITUDE,
                DBContract.KnownPlace.COLUMN_NAME_LONGITUDE,
                DBContract.KnownPlace.COLUMN_NAME_ID};

        String where = DBContract.KnownPlace.COLUMN_NAME_ID + " LIKE ?";
        String[] whereArgs = {String.valueOf(rid)};
        Cursor cursor = db.query(DBContract.KnownPlace.TABLE_NAME,
                columns, where, whereArgs, null, null, null);

        cursor.moveToFirst();
        double latitude = cursor
                .getDouble(cursor
                        .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_LATITUDE));
        double longitude = cursor
                .getDouble(cursor
                        .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_LONGITUDE));
        String description = cursor.getString(cursor
                .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_DESCRIPTION));
        int assiduity = cursor.getInt(cursor
                .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_ASSIDUITY));
        int id = cursor.getInt(cursor
                .getColumnIndexOrThrow(DBContract.KnownPlace.COLUMN_NAME_ID));
        Place result = new Place(longitude, latitude, description, assiduity);
        result.setId(id);
        cursor.close();
        return result;
    }

    public boolean insertPlace(Place p) {
        ContentValues values = new ContentValues();
        values.put(DBContract.KnownPlace.COLUMN_NAME_ASSIDUITY,
                p.getAssiduity());
        String where = DBContract.KnownPlace.COLUMN_NAME_ID + " LIKE ?";
        String[] whereArgs = {String.valueOf(p.getId())};
        boolean insert = false;
        if (db.update(DBContract.KnownPlace.TABLE_NAME, values, where,
                whereArgs) == 0) {
            values.put(DBContract.KnownPlace.COLUMN_NAME_LATITUDE,
                    p.getLatitude());
            values.put(DBContract.KnownPlace.COLUMN_NAME_LONGITUDE,
                    p.getLongitude());
            values.put(DBContract.KnownPlace.COLUMN_NAME_DESCRIPTION,
                    p.getDescription());
            db.insert(DBContract.KnownPlace.TABLE_NAME, null, values);
            insert = true;
        }
        return insert;
    }




    /* -----------------------------PLACE INSTANCE METHODS------------------------------------*/

    public List<PlaceInstance> getPlaceInstances() {
        String[] columns = {DBContract.PlaceInstance.COLUMN_NAME_START,
                DBContract.PlaceInstance.COLUMN_NAME_LATITUDE,
                DBContract.PlaceInstance.COLUMN_NAME_LONGITUDE,
                DBContract.PlaceInstance.COLUMN_NAME_END, DBContract.PlaceInstance.COLUMN_NAME_ID};

        String order = DBContract.PlaceInstance.COLUMN_NAME_END + " DESC";

        Cursor cursor = db.query(DBContract.PlaceInstance.TABLE_NAME,
                columns, null, null, null, null, order);

        List<PlaceInstance> result = new ArrayList<PlaceInstance>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            double latitude = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_LATITUDE));
            double longitude = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_LONGITUDE));
            Date start = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_START)));
            Date end = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_END)));
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_ID));
            PlaceInstance place = new PlaceInstance(latitude, longitude, start, end);
            place.setId(id);
            result.add(place);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public List<PlaceInstance> getLastPlaceInstances(Date start, Date end) {
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        start = cal.getTime();*/
        String[] columns = {DBContract.PlaceInstance.COLUMN_NAME_LATITUDE,
                DBContract.PlaceInstance.COLUMN_NAME_LONGITUDE,
                DBContract.PlaceInstance.COLUMN_NAME_START,
                DBContract.PlaceInstance.COLUMN_NAME_END};
        String order = DBContract.PlaceInstance.COLUMN_NAME_START + " ASC";
        String where = DBContract.PlaceInstance.COLUMN_NAME_START + " BETWEEN ? AND ?";
        String[] whereArgs = {getDateTime(start), getDateTime(end)};

        Cursor cursor = db.query(DBContract.PlaceInstance.TABLE_NAME,
                columns, where, whereArgs, null, null, order);

        List<PlaceInstance> result = new ArrayList<PlaceInstance>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            double lat = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_LATITUDE));
            double lon = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_LONGITUDE));
            Date startDate = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_START)));
            Date endDate = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(DBContract.PlaceInstance.COLUMN_NAME_END)));
            PlaceInstance instance = new PlaceInstance(lat, lon, startDate, endDate);
            result.add(instance);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public boolean insertPlaceInstance(PlaceInstance p) {
        ContentValues values = new ContentValues();
        values.put(DBContract.PlaceInstance.COLUMN_NAME_START, getDateTime(p.getStart()));
        values.put(DBContract.PlaceInstance.COLUMN_NAME_END, getDateTime(p.getEnd()));
        values.put(DBContract.PlaceInstance.COLUMN_NAME_LATITUDE, p.getLatitude());
        values.put(DBContract.PlaceInstance.COLUMN_NAME_LONGITUDE, p.getLongitude());
        return db.insert(DBContract.PlaceInstance.TABLE_NAME, null, values) != -1;
    }



    /* -----------------------------ROUTINE INSTANCES METHODS------------------------------------*/

    public List<RoutineInstance> getRoutineInstances() {
        String[] columns = {DBContract.RoutineInstance.COLUMN_NAME_DEPARTURE,
                DBContract.RoutineInstance.COLUMN_NAME_LATITUDE1,
                DBContract.RoutineInstance.COLUMN_NAME_LATITUDE2,
                DBContract.RoutineInstance.COLUMN_NAME_LONGITUDE1,
                DBContract.RoutineInstance.COLUMN_NAME_LONGITUDE2,
                DBContract.RoutineInstance.COLUMN_NAME_ARRIVAL,
                DBContract.RoutineInstance.COLUMN_NAME_ID};

        String order = DBContract.RoutineInstance.COLUMN_NAME_DEPARTURE + " ASC";

        Cursor cursor = db.query(DBContract.RoutineInstance.TABLE_NAME,
                columns, null, null, null, null, order);

        List<RoutineInstance> result = new ArrayList<RoutineInstance>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            double latitude1 = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineInstance.COLUMN_NAME_LATITUDE1));
            double longitude1 = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineInstance.COLUMN_NAME_LONGITUDE1));
            double latitude2 = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineInstance.COLUMN_NAME_LATITUDE2));
            double longitude2 = cursor
                    .getDouble(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineInstance.COLUMN_NAME_LONGITUDE2));
            Date departure = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineInstance.COLUMN_NAME_DEPARTURE)));
            Date arrival = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineInstance.COLUMN_NAME_ARRIVAL)));
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.RoutineInstance.COLUMN_NAME_ID));
            RoutineInstance r = new RoutineInstance(longitude1, longitude2, latitude1, latitude2,
                    arrival, departure);
            r.setId(id);
            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public boolean insertRoutineInstance(RoutineInstance r) {
        ContentValues values = new ContentValues();
        values.put(DBContract.RoutineInstance.COLUMN_NAME_LATITUDE1, r.getLatitude1());
        values.put(DBContract.RoutineInstance.COLUMN_NAME_LATITUDE2, r.getLatitude2());
        values.put(DBContract.RoutineInstance.COLUMN_NAME_LONGITUDE1, r.getLongitude1());
        values.put(DBContract.RoutineInstance.COLUMN_NAME_LONGITUDE2, r.getLongitude2());
        values.put(DBContract.RoutineInstance.COLUMN_NAME_DEPARTURE, getDateTime(r.getDeparture()));
        values.put(DBContract.RoutineInstance.COLUMN_NAME_ARRIVAL, getDateTime(r.getArrival()));
        return db.insert(DBContract.RoutineInstance.TABLE_NAME, null, values) != -1;
    }


    /* --------------------------------------ROUTINE METHODS--------------------------------------*/

    public List<Routine> getRoutines() {
        String[] columns = {DBContract.Routine.COLUMN_NAME_DESCRIPTION,
                DBContract.Routine.COLUMN_NAME_INITIAL_DATE,
                DBContract.Routine.COLUMN_NAME_FINAL_DATE,
                DBContract.Routine.COLUMN_NAME_ID};

        Cursor cursor = db.query(DBContract.Routine.TABLE_NAME,
                columns, null, null, null, null, null);

        List<Routine> result = new ArrayList<Routine>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String desc = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.Routine.COLUMN_NAME_DESCRIPTION));
            Date init = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.Routine.COLUMN_NAME_INITIAL_DATE)));
            Date end = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.Routine.COLUMN_NAME_FINAL_DATE)));
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.Routine.COLUMN_NAME_ID));
            Routine r = new Routine(init, end, desc);
            r.setId(id);
            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public Routine getRoutine(int rid) {
        String[] columns = {DBContract.Routine.COLUMN_NAME_DESCRIPTION,
                DBContract.Routine.COLUMN_NAME_INITIAL_DATE,
                DBContract.Routine.COLUMN_NAME_FINAL_DATE,
                DBContract.Routine.COLUMN_NAME_ID};

        String where = DBContract.Routine.COLUMN_NAME_ID + " LIKE ?";
        String[] whereArgs = {String.valueOf(rid)};
        Cursor cursor = db.query(DBContract.Routine.TABLE_NAME,
                columns, where, whereArgs, null, null, null);

        cursor.moveToFirst();
        String desc = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(
                                DBContract.Routine.COLUMN_NAME_DESCRIPTION));
        Date start = getDate(cursor
                .getString(cursor
                        .getColumnIndexOrThrow(
                                DBContract.Routine.COLUMN_NAME_INITIAL_DATE)));
        Date end = getDate(cursor
                .getString(cursor
                        .getColumnIndexOrThrow(
                                DBContract.Routine.COLUMN_NAME_FINAL_DATE)));
        int id = cursor.getInt(cursor
                .getColumnIndexOrThrow(DBContract.Routine.COLUMN_NAME_ID));
        Routine result = new Routine(start, end, desc);
        result.setId(id);
        cursor.close();
        return result;
    }

    public int insertRoutine(Date start, Date end) {
        List<Routine> routines = getRoutines();
        ContentValues values = new ContentValues();
        values.put(DBContract.Routine.COLUMN_NAME_INITIAL_DATE, getDateTime(start));
        values.put(DBContract.Routine.COLUMN_NAME_FINAL_DATE, getDateTime(end));
        values.put(DBContract.Routine.COLUMN_NAME_DESCRIPTION, "");
        values.put(DBContract.Routine.COLUMN_NAME_ID, routines.size() + 1);
        db.insert(DBContract.Routine.TABLE_NAME, null, values);
        return routines.size() + 1;
    }



    /* -----------------------------------ROUTINE-PLACE METHODS-----------------------------------*/

    // Get the RoutinePlace instances for a concrete routine rid
    public List<RoutinePlace> getRoutinePlaces(int rid) {
        String[] columns = {DBContract.RoutinePlaces.COLUMN_NAME_PLACE,
                DBContract.RoutinePlaces.COLUMN_NAME_ROUTINE,
                DBContract.RoutinePlaces.COLUMN_NAME_END,
                DBContract.RoutinePlaces.COLUMN_NAME_ID};

        String where = DBContract.RoutinePlaces.COLUMN_NAME_ROUTINE + " LIKE ?";
        String[] whereArgs = {String.valueOf(rid)};
        Cursor cursor = db.query(DBContract.RoutinePlaces.TABLE_NAME,
                columns, where, whereArgs, null, null, null);

        List<RoutinePlace> result = new ArrayList<RoutinePlace>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            int place = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutinePlaces.COLUMN_NAME_PLACE));
            int routine = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutinePlaces.COLUMN_NAME_ROUTINE));
            Date end = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutinePlaces.COLUMN_NAME_END)));
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.RoutinePlaces.COLUMN_NAME_ID));
            RoutinePlace r = new RoutinePlace(getRoutine(routine), getPlace(place), end);
            r.setId(id);
            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public List<RoutinePlace> getAllRoutinePlaces() {
        String[] columns = {DBContract.RoutinePlaces.COLUMN_NAME_PLACE,
                DBContract.RoutinePlaces.COLUMN_NAME_ROUTINE,
                DBContract.RoutinePlaces.COLUMN_NAME_END,
                DBContract.RoutinePlaces.COLUMN_NAME_ID};

        Cursor cursor = db.query(DBContract.RoutinePlaces.TABLE_NAME,
                columns, null, null, null, null, null);

        List<RoutinePlace> result = new ArrayList<RoutinePlace>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            int place = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutinePlaces.COLUMN_NAME_PLACE));
            int routine = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutinePlaces.COLUMN_NAME_ROUTINE));
            Date end = getDate(cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutinePlaces.COLUMN_NAME_END)));
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.RoutinePlaces.COLUMN_NAME_ID));
            RoutinePlace r = new RoutinePlace(getRoutine(routine), getPlace(place), end);
            r.setId(id);
            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public boolean insertRoutinePlace(int rid, Place p, Date d) {
        ContentValues values = new ContentValues();
        values.put(DBContract.RoutinePlaces.COLUMN_NAME_PLACE, p.getId());
        values.put(DBContract.RoutinePlaces.COLUMN_NAME_ROUTINE, rid);
        values.put(DBContract.RoutinePlaces.COLUMN_NAME_END, getDateTime(d));
        return db.insert(DBContract.RoutinePlaces.TABLE_NAME, null, values) != -1;
    }

    /* -----------------------------------FREQUENCY METHODS------------------------------------*/

    // Used to obtain the day that the id rid represents
    public Frequency getFrequency(int rid) {
        String[] columns = {
                DBContract.Frequency.COLUMN_NAME_DAY,
                DBContract.Frequency.COLUMN_NAME_ID};

        String where = DBContract.Frequency.COLUMN_NAME_ID + " LIKE ?";
        String[] whereArgs = {String.valueOf(rid)};
        Cursor cursor = db.query(DBContract.Frequency.TABLE_NAME,
                columns, where, whereArgs, null, null, null);

        cursor.moveToFirst();
        String day = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(
                                DBContract.Frequency.COLUMN_NAME_DAY));
        int id = cursor.getInt(cursor
                .getColumnIndexOrThrow(DBContract.Frequency.COLUMN_NAME_ID));
        Frequency result = new Frequency(day);
        result.setId(id);
        cursor.close();
        return result;
    }

    public List<Frequency> getFrequencies() {
        String[] columns = {DBContract.Frequency.COLUMN_NAME_ID,
                DBContract.Frequency.COLUMN_NAME_DAY};

        Cursor cursor = db.query(DBContract.Frequency.TABLE_NAME,
                columns, null, null, null, null, null);

        List<Frequency> result = new ArrayList<Frequency>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.Frequency.COLUMN_NAME_ID));
            String day = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.Frequency.COLUMN_NAME_DAY));
            Frequency f = new Frequency(day);
            f.setId(id);
            result.add(f);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    /* -----------------------------------ROUTINE-FREQ METHODS------------------------------------*/

    // Get the frequencies associated with the rid routine
    public List<RoutineFreq> getRoutineFreqs(int rid) {
        String[] columns = {DBContract.RoutineFreq.COLUMN_NAME_FREQUENCY,
                DBContract.RoutineFreq.COLUMN_NAME_ROUTINE,
                DBContract.RoutineFreq.COLUMN_NAME_RELIABILITY,
                DBContract.RoutineFreq.COLUMN_NAME_ID};

        String where = DBContract.RoutineFreq.COLUMN_NAME_ROUTINE + " LIKE ?";
        String[] whereArgs = {String.valueOf(rid)};
        Cursor cursor = db.query(DBContract.RoutineFreq.TABLE_NAME,
                columns, where, whereArgs, null, null, null);

        List<RoutineFreq> result = new ArrayList<RoutineFreq>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            int freq = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineFreq.COLUMN_NAME_FREQUENCY));
            int routine = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineFreq.COLUMN_NAME_ROUTINE));
            int rel = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineFreq.COLUMN_NAME_RELIABILITY));
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.RoutineFreq.COLUMN_NAME_ID));
            RoutineFreq r = new RoutineFreq(getFrequency(freq), getRoutine(routine), rel);
            r.setId(id);
            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public List<RoutineFreq> getAllRoutineFreqs() {
        String[] columns = {DBContract.RoutineFreq.COLUMN_NAME_FREQUENCY,
                DBContract.RoutineFreq.COLUMN_NAME_ROUTINE,
                DBContract.RoutineFreq.COLUMN_NAME_RELIABILITY,
                DBContract.RoutineFreq.COLUMN_NAME_ID};

        Cursor cursor = db.query(DBContract.RoutineFreq.TABLE_NAME,
                columns, null, null, null, null, null);

        List<RoutineFreq> result = new ArrayList<RoutineFreq>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            int freq = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineFreq.COLUMN_NAME_FREQUENCY));
            int routine = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineFreq.COLUMN_NAME_ROUTINE));
            int rel = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(
                                    DBContract.RoutineFreq.COLUMN_NAME_RELIABILITY));
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBContract.RoutineFreq.COLUMN_NAME_ID));
            RoutineFreq r = new RoutineFreq(getFrequency(freq), getRoutine(routine), rel);
            r.setId(id);
            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public boolean insertRoutineFreq(int routine, int freq, int rel, int id) {
        ContentValues values = new ContentValues();
        values.put(DBContract.RoutineFreq.COLUMN_NAME_RELIABILITY,
                rel);
        String where = DBContract.RoutineFreq.COLUMN_NAME_ID + " LIKE ?";
        String[] whereArgs = {String.valueOf(id)};
        boolean insert = false;
        if (db.update(DBContract.RoutineFreq.TABLE_NAME, values, where,
                whereArgs) == 0) {
            values.put(DBContract.RoutineFreq.COLUMN_NAME_FREQUENCY,
                    freq);
            values.put(DBContract.RoutineFreq.COLUMN_NAME_ROUTINE,
                    routine);
            db.insert(DBContract.RoutineFreq.TABLE_NAME, null, values);
            insert = true;
        }
        return insert;
    }

    /* ----------------------------------AUXILIARY METHODS---------------------------------------*/

    // Formats a Date for SQLite
    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    // Obtain a Date from a String
    private Date getDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result = new Date();
        try {
            result = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
