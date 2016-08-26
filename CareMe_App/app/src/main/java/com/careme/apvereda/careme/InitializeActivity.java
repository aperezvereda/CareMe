package com.careme.apvereda.careme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
 * Activity for the initialization window to introduce carer's email and phone number
 */

public class InitializeActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button btnAccept;
    EditText txtEmail, txtPhone, txtcareremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getApplicationContext()
                .getSharedPreferences("basicData", Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "");
        String phone = sharedPref.getString("carerphone", "");
        String carermail = sharedPref.getString("careremail", "");
        if ((email.compareTo("")) != 0 && (phone.compareTo("") != 0) && (carermail.compareTo("") != 0)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }else {
            setContentView(R.layout.activity_initialize);
            txtEmail = (EditText) findViewById(R.id.txtemail);
            txtPhone = (EditText) findViewById(R.id.txtphone);
            txtcareremail = (EditText) findViewById(R.id.txtcareremail);
            btnAccept = (Button) findViewById(R.id.btnaccept);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((txtEmail.getText().toString().compareTo("") != 0) &&
                            (txtPhone.getText().toString().compareTo("") != 0) &&
                            (txtcareremail.getText().toString().compareTo("") != 0)) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", txtEmail.getText().toString());
                        editor.putString("carerphone", txtPhone.getText().toString());
                        editor.putString("careremail", txtcareremail.getText().toString());
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_initialize, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
