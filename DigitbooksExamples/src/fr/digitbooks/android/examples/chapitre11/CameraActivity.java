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
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;

public class CameraActivity extends Activity {

    private CameraPreview mCameraPreview;
    private Camera mCamera;
    private int mNumberOfCameras;
    private int mCurrentCameraId;
    
    private boolean mIsPreGingerbread;

    /*
     * La caméra par défaut est celle faisant opposition à l'écran
     */
    private int mDefaultCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mIsPreGingerbread = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD;

        mCameraPreview = new CameraPreview(this);
        setContentView(mCameraPreview);

        if (mIsPreGingerbread) {
            mNumberOfCameras = mDefaultCameraId = 1;
        } else {
            mNumberOfCameras = Camera.getNumberOfCameras();

            // Recherche l'identifiant de la caméra par défaut
            CameraInfo cameraInfo = new CameraInfo();
            for (int i = 0; i < mNumberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    mDefaultCameraId = i;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * On récupère une référence sur l'objet Camera
         */
        mCamera = Camera.open();
        mCurrentCameraId = mDefaultCameraId;
        mCameraPreview.setCamera(mCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
         * L'objet Camera est une ressource partagée pouvant être utilisé par
         * d'autres applications. Il est donc extrêmement important de libérer
         * cette ressource dès que possible.
         */
        if (mCamera != null) {
            mCameraPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.switch_camera:

                if (mNumberOfCameras == 1) {
                    Toast.makeText(this, "Le terminal ne dispose que d'une unique caméra", Toast.LENGTH_LONG).show();
                    return true;
                }

                /*
                 * On arrête la prévisualisation de la caméra courante
                 */
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCameraPreview.setCamera(null);
                    mCamera.release();
                    mCamera = null;
                }

                /*
                 * On démarre la prévisualisation de la caméra "suivante"
                 */
                mCurrentCameraId = (mCurrentCameraId + 1) % mNumberOfCameras;
                mCamera = Camera.open(mCurrentCameraId);
                mCameraPreview.switchCamera(mCamera);
                mCamera.startPreview();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
