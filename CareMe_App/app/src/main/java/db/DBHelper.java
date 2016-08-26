package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

import db.DBContract.*;

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

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RoutineDB.db";

    // Scripts to create and maintain the db and it tables
    private static final String TEXT = " TEXT";
    private static final String DATE = " DATETIME";
    private static final String DOUBLE = " REAL";
    private static final String INT = " INTEGER";
    private static final String COMMA = ",";
    private static final String SQL_CREATE_HISTORY =
            "CREATE TABLE " + History.TABLE_NAME + " (" + History.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + History.COLUMN_NAME_DATE + DATE + COMMA + History.COLUMN_NAME_LATITUDE + DOUBLE +
                    COMMA + History.COLUMN_NAME_LONGITUDE + DOUBLE + " );";
    private static final String SQL_CREATE_KNOWNPLACE =
            "CREATE TABLE " + KnownPlace.TABLE_NAME + " (" + KnownPlace.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + KnownPlace.COLUMN_NAME_ASSIDUITY + INT + COMMA + KnownPlace.COLUMN_NAME_DESCRIPTION + TEXT +
                    COMMA + KnownPlace.COLUMN_NAME_LONGITUDE + DOUBLE + COMMA + KnownPlace.COLUMN_NAME_LATITUDE + DOUBLE + " );";
    private static final String SQL_CREATE_ROUTINE =
            "CREATE TABLE " + Routine.TABLE_NAME + " (" + Routine.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + Routine.COLUMN_NAME_DESCRIPTION + TEXT
                    + COMMA + Routine.COLUMN_NAME_INITIAL_DATE + DATE + COMMA + Routine.COLUMN_NAME_FINAL_DATE + DATE + " );";
    private static final String SQL_CREATE_ARRANGEMENT =
            "CREATE TABLE " + Arrangement.TABLE_NAME + " (" + Arrangement.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + Arrangement.COLUMN_NAME_DESCRIPTION + TEXT + COMMA + Arrangement.COLUMN_NAME_DATE +
                    DATE + COMMA + Arrangement.COLUMN_NAME_LATITUDE + DOUBLE + COMMA + Arrangement.COLUMN_NAME_LONGITUDE + DOUBLE + " )";
    private static final String SQL_CREATE_CONTACT =
            "CREATE TABLE " + Contact.TABLE_NAME + " (" + Contact.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + Contact.COLUMN_NAME_NAME + TEXT + COMMA + Contact.COLUMN_NAME_TELEPHONE + TEXT +
                    COMMA + Contact.COLUMN_NAME_PRIORITY + INT + COMMA + Contact.COLUMN_NAME_BT_ADDRESS + TEXT + " );";
    private static final String SQL_CREATE_FREQUENCY =
            "CREATE TABLE " + Frequency.TABLE_NAME + " (" + Frequency.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + Frequency.COLUMN_NAME_DAY + TEXT + " );";
    private static final String SQL_CREATE_ROUTINEPLACES =
            "CREATE TABLE " + RoutinePlaces.TABLE_NAME + " (" + RoutinePlaces.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + RoutinePlaces.COLUMN_NAME_PLACE + INT + COMMA + RoutinePlaces.COLUMN_NAME_ROUTINE + INT + COMMA
                    + RoutinePlaces.COLUMN_NAME_END + DATE + " );";
    private static final String SQL_CREATE_ROUTINEFREQ =
            "CREATE TABLE " + RoutineFreq.TABLE_NAME + " (" + RoutineFreq.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + RoutineFreq.COLUMN_NAME_FREQUENCY + INT + COMMA + RoutineFreq.COLUMN_NAME_ROUTINE + INT + COMMA
                    + RoutineFreq.COLUMN_NAME_RELIABILITY + INT + " );";
    private static final String SQL_CREATE_PLACEINSTANCE =
            "CREATE TABLE " + PlaceInstance.TABLE_NAME + " (" + PlaceInstance.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + PlaceInstance.COLUMN_NAME_LATITUDE + DOUBLE + COMMA + PlaceInstance.COLUMN_NAME_LONGITUDE + DOUBLE + COMMA
                    + PlaceInstance.COLUMN_NAME_START + DATE + COMMA + PlaceInstance.COLUMN_NAME_END + DATE + " );";
    private static final String SQL_CREATE_ROUTINEINSTANCE =
            "CREATE TABLE " + RoutineInstance.TABLE_NAME + " (" + RoutineInstance.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY," + RoutineInstance.COLUMN_NAME_LATITUDE1 + DOUBLE + COMMA + RoutineInstance.COLUMN_NAME_LONGITUDE1 + DOUBLE + COMMA
                    + RoutineInstance.COLUMN_NAME_LATITUDE2 + DOUBLE + COMMA + RoutineInstance.COLUMN_NAME_LONGITUDE2 + DOUBLE + COMMA
                    + RoutineInstance.COLUMN_NAME_DEPARTURE + DATE + COMMA + RoutineInstance.COLUMN_NAME_ARRIVAL + DATE + " );";

    private static final String SQL_DELETE_ROUTINE = "DROP TABLE IF EXISTS " + Routine.TABLE_NAME + ";";
    private static final String SQL_DELETE_ARRANGEMENT = "DROP TABLE IF EXISTS " + Arrangement.TABLE_NAME + ";";
    private static final String SQL_DELETE_CONTACT = "DROP TABLE IF EXISTS " + Contact.TABLE_NAME + ";";
    private static final String SQL_DELETE_HISTORY = "DROP TABLE IF EXISTS " + History.TABLE_NAME + ";";
    private static final String SQL_DELETE_KNOWNPLACE = "DROP TABLE IF EXISTS " + KnownPlace.TABLE_NAME + ";";
    private static final String SQL_DELETE_FREQUENCY = "DROP TABLE IF EXISTS " + Frequency.TABLE_NAME + ";";
    private static final String SQL_DELETE_ROUTINEFREQ = "DROP TABLE IF EXISTS " + RoutineFreq.TABLE_NAME + ";";
    private static final String SQL_DELETE_ROUTINEPLACES = "DROP TABLE IF EXISTS " + RoutinePlaces.TABLE_NAME + ";";
    private static final String SQL_DELETE_PLACEINSTANCE = "DROP TABLE IF EXISTS " + PlaceInstance.TABLE_NAME + ";";
    private static final String SQL_DELETE_ROUTINEINSTANCE = "DROP TABLE IF EXISTS " + RoutineInstance.TABLE_NAME + ";";

    private static final String INSERT_MONDAY = "INSERT INTO " + Frequency.TABLE_NAME + " (" +
            Frequency.COLUMN_NAME_ID + ", " + Frequency.COLUMN_NAME_DAY + ") VALUES (" + Calendar.MONDAY + ", 'Monday');";
    private static final String INSERT_TUESDAY = "INSERT INTO " + Frequency.TABLE_NAME + " (" +
            Frequency.COLUMN_NAME_ID + ", " + Frequency.COLUMN_NAME_DAY + ") VALUES (" + Calendar.TUESDAY + ", 'Tuesday');";
    private static final String INSERT_WEDNESDAY = "INSERT INTO " + Frequency.TABLE_NAME + " (" +
            Frequency.COLUMN_NAME_ID + ", " + Frequency.COLUMN_NAME_DAY + ") VALUES (" + Calendar.WEDNESDAY + ", 'Wednesday');";
    private static final String INSERT_THURSDAY = "INSERT INTO " + Frequency.TABLE_NAME + " (" +
            Frequency.COLUMN_NAME_ID + ", " + Frequency.COLUMN_NAME_DAY + ") VALUES (" + Calendar.THURSDAY + ", 'Thursday');";
    private static final String INSERT_FRIDAY = "INSERT INTO " + Frequency.TABLE_NAME + " (" +
            Frequency.COLUMN_NAME_ID + ", " + Frequency.COLUMN_NAME_DAY + ") VALUES (" + Calendar.FRIDAY + ", 'Friday');";
    private static final String INSERT_SATURDAY = "INSERT INTO " + Frequency.TABLE_NAME + " (" +
            Frequency.COLUMN_NAME_ID + ", " + Frequency.COLUMN_NAME_DAY + ") VALUES (" + Calendar.SATURDAY + ", 'Saturday');";
    private static final String INSERT_SUNDAY = "INSERT INTO " + Frequency.TABLE_NAME + " (" +
            Frequency.COLUMN_NAME_ID + ", " + Frequency.COLUMN_NAME_DAY + ") VALUES (" + Calendar.SUNDAY + ", 'Sunday');";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Scripts to execute on db creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_CREATE_ROUTINE);
        db.execSQL(SQL_CREATE_ARRANGEMENT);
        db.execSQL(SQL_CREATE_CONTACT);
        db.execSQL(SQL_CREATE_FREQUENCY);
        db.execSQL(SQL_CREATE_HISTORY);
        db.execSQL(SQL_CREATE_KNOWNPLACE);
        db.execSQL(SQL_CREATE_ROUTINEFREQ);
        db.execSQL(SQL_CREATE_ROUTINEPLACES);
        db.execSQL(SQL_CREATE_PLACEINSTANCE);
        db.execSQL(SQL_CREATE_ROUTINEINSTANCE);
        db.execSQL(INSERT_MONDAY);
        db.execSQL(INSERT_TUESDAY);
        db.execSQL(INSERT_WEDNESDAY);
        db.execSQL(INSERT_THURSDAY);
        db.execSQL(INSERT_FRIDAY);
        db.execSQL(INSERT_SATURDAY);
        db.execSQL(INSERT_SUNDAY);
    }

    // Scripts to delete the db on a upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_DELETE_ROUTINE);
        db.execSQL(SQL_DELETE_ARRANGEMENT);
        db.execSQL(SQL_DELETE_CONTACT);
        db.execSQL(SQL_DELETE_FREQUENCY);
        db.execSQL(SQL_DELETE_HISTORY);
        db.execSQL(SQL_DELETE_KNOWNPLACE);
        db.execSQL(SQL_DELETE_ROUTINEFREQ);
        db.execSQL(SQL_DELETE_ROUTINEPLACES);
        db.execSQL(SQL_DELETE_PLACEINSTANCE);
        db.execSQL(SQL_DELETE_ROUTINEINSTANCE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
