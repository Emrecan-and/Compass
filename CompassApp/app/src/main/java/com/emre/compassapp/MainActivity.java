package com.emre.compassapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.EventListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView text;
    private ImageView image;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor,magnetometerSensor;
    private float[] lastAcceleroMeter=new float[3];
    private float[] lastMagnetometer=new float[3];
    private float[] rotationMatrix=new float[9];
    private float[] oriantation=new float[3];

    boolean isAcceleroCoppıed=false;
    boolean isMagnetoCoppied=false;
    long lastUpdateTime=0;
    float currentDegree=0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text=findViewById(R.id.textView);
        image=findViewById(R.id.imageView);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor==accelerometerSensor){
            System.arraycopy(sensorEvent.values,0,lastAcceleroMeter,0,sensorEvent.values.length);
            isAcceleroCoppıed=true;
        }else if(sensorEvent.sensor==magnetometerSensor){
            System.arraycopy(sensorEvent.values,0,lastMagnetometer,0,sensorEvent.values.length);
            isMagnetoCoppied=true;
        }
        if(isMagnetoCoppied&&isAcceleroCoppıed&&System.currentTimeMillis()-lastUpdateTime>250){
            SensorManager.getRotationMatrix(rotationMatrix,null,lastAcceleroMeter,lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix,oriantation);
            float azRadians=oriantation[0];
            float azDegree=(float) Math.toDegrees(azRadians);
            RotateAnimation rotateAnimation=
                    new RotateAnimation(currentDegree,-azDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            image.startAnimation(rotateAnimation);
            currentDegree=-azDegree;
            lastUpdateTime=System.currentTimeMillis();
            int x=(int) azDegree;
            text.setText(x+" derece");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,magnetometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,accelerometerSensor);
        sensorManager.unregisterListener(this,magnetometerSensor);
    }
}