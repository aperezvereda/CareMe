package db;

import android.provider.BaseColumns;

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

public class DBContract {
    public DBContract(){}

    // DB tables and columns definition
    public static abstract class Contact implements BaseColumns {
        public static final String TABLE_NAME = "Contact";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_TELEPHONE = "Telephone";
        public static final String COLUMN_NAME_PRIORITY = "Priority";
        public static final String COLUMN_NAME_BT_ADDRESS = "BtAddress";
    }

    public static abstract class History implements BaseColumns {
        public static final String TABLE_NAME = "History";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_DATE = "Date";
    }

    public static abstract class KnownPlace implements BaseColumns {
        public static final String TABLE_NAME = "KnownPlace";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
        public static final String COLUMN_NAME_ASSIDUITY = "Assiduity";
    }

    public static abstract class Arrangement implements BaseColumns {
        public static final String TABLE_NAME = "Arrangement";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_DATE = "Date";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
    }

    public static abstract class Routine implements BaseColumns {
        public static final String TABLE_NAME = "Routine";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_INITIAL_DATE = "InitialDate";
        public static final String COLUMN_NAME_FINAL_DATE = "FinalDate";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
    }

    public static abstract class Frequency implements BaseColumns {
        public static final String TABLE_NAME = "Frequency";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_DAY = "Day";
    }

    public static abstract class RoutineFreq implements BaseColumns {
        public static final String TABLE_NAME = "RoutineFreq";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_ROUTINE = "Routine";
        public static final String COLUMN_NAME_FREQUENCY = "Frequency";
        public static final String COLUMN_NAME_RELIABILITY = "Reliability";
    }

    public static abstract class RoutinePlaces implements BaseColumns {
        public static final String TABLE_NAME = "RoutinePlaces";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_ROUTINE = "Routine";
        public static final String COLUMN_NAME_PLACE = "Place";
        public static final String COLUMN_NAME_END = "End";
    }

    public static abstract class PlaceInstance implements BaseColumns {
        public static final String TABLE_NAME = "PlaceInstance";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_START = "Start";
        public static final String COLUMN_NAME_END = "End";
    }

    public static abstract class RoutineInstance implements BaseColumns {
        public static final String TABLE_NAME = "RoutineInstance";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_LATITUDE1 = "Latitude1";
        public static final String COLUMN_NAME_LONGITUDE1 = "Longitude1";
        public static final String COLUMN_NAME_LATITUDE2 = "Latitude2";
        public static final String COLUMN_NAME_LONGITUDE2 = "Longitude2";
        public static final String COLUMN_NAME_DEPARTURE = "Departure";
        public static final String COLUMN_NAME_ARRIVAL = "Arrival";
    }
}
