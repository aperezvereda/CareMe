package com.careme.apvereda.careme;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nimbees.platform.NimbeesNotificationManager;
import com.nimbees.platform.beans.NimbeesBeacon;

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

public class CustomNotificationManager extends NimbeesNotificationManager {

    public CustomNotificationManager(Context context) {
        super(context);
    }

    @Override
    public void handleSimpleMessage(long idNotification, String message,  Map<String, String> additionalContent) {
        // Custom code, or just call the default implementation
        super.handleSimpleMessage(idNotification, message, additionalContent);
    }

    @Override
    public void handleCustomMessage(long idNotification, String content, Map<String, String> additionalContent) {
        // Insert your code here
    }

    @Override
    public void handleScreenTransitionMessage(long idNotification, String content, String screen, Map<String, String> additionalContent) {
        /*if (screen.equals(YOUR_SCREEN_NAME)) {
            showNotification(idNotification, content, additionalContent, YourActivity.class, null);
        } else {
            showNotification(idNotification, content, additionalContent);
        }*/
    }

    @Override
    public void handleBeaconMessage(long idNotification, String content, NimbeesBeacon beacon, String action) {
        // Insert your code here
    }
}