package entities;

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

public class Frequency {
    int id;
    String day;

    public Frequency(String day) {
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
