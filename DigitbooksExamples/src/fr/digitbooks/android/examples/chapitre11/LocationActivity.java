/*
 * Copyright (C) 2010   Cyril Mottier & Ludovic Perrier
 *              (http://www.digitbooks.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.digitbooks.android.examples.chapitre11;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.util.Config;

public class LocationActivity extends Activity implements LocationListener {

    private static final String LOG_TAG = LocationActivity.class.getSimpleName();

    private static final int MIN_TIME = 5000;
    private static final float MIN_DISTANCE = 3.0f;
    private static final float DISTANCE_LIMIT = 100.0f;

    private static final int NO_GPS_DIALOG = 0xbeef;

    private static final float RESULTS[] = new float[1];

    private LocationManager mLocationManager;
    private Location mLocation;
    private Location mStartLocation;
    private boolean mIsGPSEnabled = true;

    private Button mStartButton;
    private View mDistanceImage;
    private TextView mDistanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            /*
             * Le GPS n'est pas activé : affichons une boite de dialogue aidant
             * l'utilisateur dans son activation.
             */
            mIsGPSEnabled = false;
            showDialog(NO_GPS_DIALOG);
        } else {

            setContentView(R.layout.location);

            mStartButton = (Button) findViewById(R.id.start_button);
            mDistanceImage = findViewById(R.id.distance_hud_image);
            mDistanceText = (TextView) findViewById(R.id.distance_hud_text);
            
            mStartButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {

                    /*
                     * On cache le bouton de définition de la position de départ
                     * et on affiche le HUD permettant d'avoir les informations
                     * de distance
                     */
                    mStartButton.setVisibility(View.GONE);
                    findViewById(R.id.distance_hud).setVisibility(View.VISIBLE);

                    mStartLocation = mLocation;
                }
            });

            /*
             * On tente d'utiliser la dernière position connue. Si cette
             * dernière n'est pas nulle on active le bouton de démarrage.
             */
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mLocation != null) {
                mStartButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsGPSEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case NO_GPS_DIALOG:
                return new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(R.string.no_gps_title)
                        .setMessage(R.string.no_gps_content)
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }).create();

            default:
                return super.onCreateDialog(id);
        }
    }

    public void onLocationChanged(Location location) {

        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "New location received: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
        }

        if (mStartLocation != null) {

            final double startLatitude = mStartLocation.getLatitude();
            final double startLongitude = mStartLocation.getLongitude();
            final double endLatitude = location.getLatitude();
            final double endLongitude = location.getLongitude();

            Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, RESULTS);

            /*
             * Affichage des nouvelles informations sous forme textuelle
             */
            final String text = getResources().getString(R.string.distance_hud_text_format,
                    endLatitude,
                    endLongitude,
                    RESULTS[0]);
            mDistanceText.setText(text);

            /*
             * Affichage des nouvelles informations sous forme graphique
             */
            int red = (int) (255 - Math.min(RESULTS[0], DISTANCE_LIMIT) * 255 / DISTANCE_LIMIT);
            mDistanceImage.setBackgroundColor(Color.rgb(red, 0, 0));

        } else {
            /*
             * La position de départ n'a pas été initialisée jusqu'à maintenant.
             * On enregistre donc cette position comme possible position de
             * départ.
             */
            mLocation = location;
            mStartButton.setEnabled(true);
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
