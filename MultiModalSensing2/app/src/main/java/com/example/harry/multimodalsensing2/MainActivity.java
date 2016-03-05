package com.example.harry.multimodalsensing2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private double STEP_LENGTH = 1.0d; //in units

    private SensorManager sensorManager;
    String mBearing;
    TextView BearingValueView;
    String dir = "N";
    ArrayList<String> step_strings = new ArrayList<String>();
    /*
    double Noffset = 0.0f;
    double NEoffset = 0.0f;
    double Eoffset = 0.0f;
    double SEoffset = 0.0f;
    double Soffset = 0.0f;
    double SWoffset = 0.0f;
    double Woffset = 0.0f;
    double NWoffset = 0.0f;
    */
    double NS_Manhattan = 0.0d;
    double EW_Manhattan = 0.0d;
    TextView DisplacementValueView;


    TextView AccelXValueView;
    TextView AccelYValueView;
    TextView AccelZValueView;
    float[] mAccelerometer;

    TextView GyroXValueView;
    TextView GyroYValueView;
    TextView GyroZValueView;
    float[] mGeomagnetic;

    TextView MagnetXValueView;
    TextView MagnetYValueView;
    TextView MagnetZValueView;

    TextView LightValueView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Register sensor manager
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);

        //Link to layout
        AccelXValueView=(TextView)findViewById(R.id.AccelXcoordView);
        AccelYValueView=(TextView)findViewById(R.id.AccelYcoordView);
        AccelZValueView=(TextView)findViewById(R.id.AccelZcoordView);
        GyroXValueView=(TextView)findViewById(R.id.GyroXcoordView);
        GyroYValueView=(TextView)findViewById(R.id.GyroYcoordView);
        GyroZValueView=(TextView)findViewById(R.id.GyroZcoordView);
        MagnetXValueView=(TextView)findViewById(R.id.MagnetXcoordView);
        MagnetYValueView=(TextView)findViewById(R.id.MagnetYcoordView);
        MagnetZValueView=(TextView)findViewById(R.id.MagnetZcoordView);
        LightValueView=(TextView)findViewById(R.id.LightcoordView);
        BearingValueView=(TextView)findViewById(R.id.BearingView);
        DisplacementValueView=(TextView)findViewById(R.id.DisplacementView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            // assign directions
            mAccelerometer = event.values;
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            AccelXValueView.setText("X: "+x);
            AccelYValueView.setText("Y: "+y);
            AccelZValueView.setText("Z: "+z);
        }

        else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            GyroXValueView.setText("X: "+x);
            GyroYValueView.setText("Y: "+y);
            GyroZValueView.setText("Z: "+z);
        }

        else if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            MagnetXValueView.setText("X: "+x);
            MagnetYValueView.setText("Y: "+y);
            MagnetZValueView.setText("Z: "+z);
        }

        else if(event.sensor.getType()==Sensor.TYPE_LIGHT) {
            float l=event.values[0];

            LightValueView.setText("Light: "+l);
        }

        if (mAccelerometer != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // at this point, orientation contains the azimuth(direction),
                // pitch and roll values.
                double azimuth = 180 * orientation[0] / Math.PI;
                // double pitch = 180 * orientation[1] / Math.PI;
                // double roll = 180 * orientation[2] / Math.PI;
                double degrees = normalizeDegree(azimuth);
                mBearing = String.format("%.3f", degrees);
                if ((degrees > 0 && degrees <= 22.5) || degrees > 337.5) {
                    dir = "N";
                } else if (degrees > 22.5 && degrees <= 67.5) {
                    dir = "NE";
                } else if (degrees > 67.5 && degrees <= 112.5) {
                    dir = "E";
                } else if (degrees > 112.5 && degrees <= 157.5) {
                    dir = "SE";
                } else if (degrees > 157.5 && degrees <= 222.5) {
                    dir = "S";
                } else if (degrees > 222.5 && degrees <= 247.5) {
                    dir = "SW";
                } else if (degrees > 247.5 && degrees <= 292.5) {
                    dir = "W";
                } else if (degrees > 292.5 && degrees <= 337.5) {
                    dir = "NW";
                }
                //mBearingDialog.setMessage("Dir: " + dir + " Bearing: " + mBearing);
                BearingValueView.setText("Dir: " + dir + " Bearing: " + mBearing);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onStep(View view) {
        //dir is the current direction
        step_strings.add(dir);//adds direction to steps
        switch(dir) {
            case "N":
                //Noffset++;
                NS_Manhattan += 1.0d * STEP_LENGTH;
                break;
            case "NE":
                //NEoffset++;
                NS_Manhattan += Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                EW_Manhattan += Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                break;
            case "E":
                //Eoffset++;
                EW_Manhattan += 1.0d * STEP_LENGTH;
                break;
            case "SE":
                //SEoffset++;
                NS_Manhattan -= Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                EW_Manhattan += Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                break;
            case "S":
                //Soffset++;
                NS_Manhattan -= 1.0d * STEP_LENGTH;
                break;
            case "SW":
                //SWoffset++;
                NS_Manhattan -= Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                EW_Manhattan -= Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                break;
            case "W":
                //Woffset++;
                EW_Manhattan -= 1.0d * STEP_LENGTH;
                break;
            case "NW":
                //NWoffset++;
                NS_Manhattan += Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                EW_Manhattan -= Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                break;
            default:
                //Do nothing
                break;
        }

        //hypotenuse = sqrt(x^2+y^2)
        double hypotenuse = Math.sqrt(Math.pow(NS_Manhattan, 2.0d) + Math.pow(EW_Manhattan, 2.0d));
        DisplacementValueView.setText("Displacement: " + Double.toString(hypotenuse));
    }

    private double normalizeDegree(double value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }
}
