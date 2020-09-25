package com.example.torchlighttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MAIN";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent;
    private float distanceFromPhone;
    private Camera mCamera;
    private SurfaceTexture mPreviewTexture;
    private android.hardware.Camera.Parameters mParameters;
    private boolean isFlashLightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            isSensorPresent = true;
        } else {
            isSensorPresent = false;
        }
        initCameraFlashLight();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
    }

    public void initCameraFlashLight() {
        mCamera = android.hardware.Camera.open();
        mParameters = mCamera.getParameters();
        mPreviewTexture = new SurfaceTexture(0);
        try {
            mCamera.setPreviewTexture(mPreviewTexture);
        } catch (IOException ex) {
            Log.e(TAG, ex.getLocalizedMessage());
            Toast.makeText(getApplicationContext(),
                    getResources().getText(R.string.error_message),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager = null;
        mSensor = null;
        mCamera.release();
        mCamera = null;
    }

    public void turnTorchOn(){
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
        isFlashLightOn = true;
    }

    public void turnTorchOff(){
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
        isFlashLightOn = false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        distanceFromPhone = sensorEvent.values[0];
        if (distanceFromPhone < mSensor.getMaximumRange()) {
            if (!isFlashLightOn) {
                turnTorchOn();
            }
        } else {
            if (isFlashLightOn) {
                turnTorchOff();
            } } }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}