package entities;

import java.util.Date;

import db.DBContract;

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

public class RoutinePlace {
    int id;
    Routine routine;
    Place place;
    Date end;

    public RoutinePlace(Routine routine, Place place, Date end) {
        this.routine = routine;
        this.place = place;
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
