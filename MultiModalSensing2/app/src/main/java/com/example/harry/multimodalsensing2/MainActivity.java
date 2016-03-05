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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private double STEP_LENGTH = 1.0d; //in units
    private boolean liveDisplayMode = true;

    private SensorManager sensorManager;
    String mBearing;
    double degrees = 0.0d;
    TextView BearingValueView;
    String dir = "N";
    String lastCardinalDir = "";
    String cardinalDir = "N";
    //ArrayList<String> step_strings = new ArrayList<String>();
    double NS_Manhattan = 0.0d;
    double EW_Manhattan = 0.0d;
    double currDisplacement = 0.0d;
    double totalRotationDegrees = 0.0d;
    TextView DisplacementValueView;
    TextView TotalRotationValueView;

    float accelX = 0.0f;
    float accelY = 0.0f;
    float accelZ = 0.0f;
    TextView AccelXValueView;
    TextView AccelYValueView;
    TextView AccelZValueView;
    float[] mAccelerometer;

    float gyroX = 0.0f;
    float gyroY = 0.0f;
    float gyroZ = 0.0f;
    TextView GyroXValueView;
    TextView GyroYValueView;
    TextView GyroZValueView;
    float[] mGeomagnetic;

    float magnetX = 0.0f;
    float magnetY = 0.0f;
    float magnetZ = 0.0f;
    TextView MagnetXValueView;
    TextView MagnetYValueView;
    TextView MagnetZValueView;

    float lightValue = 0.0f;
    TextView LightValueView;
    private String timestampFineFormat = "dd-MM-yy_HH:mm:ss:SSS";
    private SimpleDateFormat sdfFine = new SimpleDateFormat(timestampFineFormat, Locale.US);
    String data = "";

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
        TotalRotationValueView=(TextView)findViewById(R.id.TotalRotationView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            // assign directions
            mAccelerometer = event.values;
            accelX=event.values[0];
            accelY=event.values[1];
            accelZ=event.values[2];

            AccelXValueView.setText("X: "+accelX);
            AccelYValueView.setText("Y: "+accelY);
            AccelZValueView.setText("Z: "+accelZ);
        }

        else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            gyroX=event.values[0];
            gyroY=event.values[1];
            gyroZ=event.values[2];

            GyroXValueView.setText("X: "+gyroX);
            GyroYValueView.setText("Y: "+gyroY);
            GyroZValueView.setText("Z: "+gyroZ);
        }

        else if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
            magnetX=event.values[0];
            magnetY=event.values[1];
            magnetZ=event.values[2];

            MagnetXValueView.setText("X: "+magnetX);
            MagnetYValueView.setText("Y: "+magnetY);
            MagnetZValueView.setText("Z: "+magnetZ);
        }

        else if(event.sensor.getType()==Sensor.TYPE_LIGHT) {
            lightValue=event.values[0];

            LightValueView.setText("Light: "+lightValue);
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
                degrees = normalizeDegree(azimuth);
                mBearing = String.format("%.3f", degrees);
                if ((degrees > 0 && degrees <= 22.5) || degrees > 337.5) {
                    dir = "N";
                    cardinalDir = "N";
                } else if (degrees > 22.5 && degrees <= 67.5) {
                    dir = "NE";
                } else if (degrees > 67.5 && degrees <= 112.5) {
                    dir = "E";
                    cardinalDir = "E";
                } else if (degrees > 112.5 && degrees <= 157.5) {
                    dir = "SE";
                } else if (degrees > 157.5 && degrees <= 222.5) {
                    dir = "S";
                    cardinalDir = "S";
                } else if (degrees > 222.5 && degrees <= 247.5) {
                    dir = "SW";
                } else if (degrees > 247.5 && degrees <= 292.5) {
                    dir = "W";
                    cardinalDir = "W";
                } else if (degrees > 292.5 && degrees <= 337.5) {
                    dir = "NW";
                }
                //mBearingDialog.setMessage("Dir: " + dir + " Bearing: " + mBearing);
                BearingValueView.setText("Dir: " + dir + " Bearing: " + mBearing);
            }
        }

        //row data entry
        if(liveDisplayMode) {
            data = sdfFine.format(new Date())/*.toString()*/ + "," + //timestamp
                    //.substring(3) to get rid of "X: "
                    accelX + "," + accelY + "," + accelZ + "," +
                    gyroX + "," + gyroY + "," + gyroZ + "," +
                    magnetX + "," + magnetY + "," + magnetZ + "," +
                    lightValue + "," +
                    currDisplacement + "," +
                    totalRotationDegrees + "," +
                    mBearing + "," +
                    dir + "," +
                    cardinalDir + "\n";
        }
        System.out.println(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onStep(View view) {

        //total rotation
        if(!cardinalDir.equals(lastCardinalDir) && !lastCardinalDir.isEmpty()) {
            switch(lastCardinalDir) {
                case "N":
                    switch(cardinalDir) {
                        case "N":
                            //same direction, do nothing
                            break;
                        case "E":
                            totalRotationDegrees += 90.0;
                            break;
                        case "S":
                            totalRotationDegrees += 180.0;
                            break;
                        case "W":
                            totalRotationDegrees += 90.0;
                            break;
                        default:
                            //Do nothing
                            break;
                    }
                    break;
                case "E":
                    switch(cardinalDir) {
                        case "E":
                            //same direction, do nothing
                            break;
                        case "S":
                            totalRotationDegrees += 90.0;
                            break;
                        case "W":
                            totalRotationDegrees += 180.0;
                            break;
                        case "N":
                            totalRotationDegrees += 90.0;
                            break;
                        default:
                            //Do nothing
                            break;
                    }
                    break;
                case "S":
                    switch(cardinalDir) {
                        case "S":
                            //same direction, do nothing
                            break;
                        case "W":
                            totalRotationDegrees += 90.0;
                            break;
                        case "N":
                            totalRotationDegrees += 180.0;
                            break;
                        case "E":
                            totalRotationDegrees += 90.0;
                            break;
                        default:
                            //Do nothing
                            break;
                    }
                    break;
                case "W":
                    switch(cardinalDir) {
                        case "W":
                            //same direction, do nothing
                            break;
                        case "N":
                            totalRotationDegrees += 90.0;
                            break;
                        case "E":
                            totalRotationDegrees += 180.0;
                            break;
                        case "S":
                            totalRotationDegrees += 90.0;
                            break;
                        default:
                            //Do nothing
                            break;
                    }
                    break;
                default:
                    //Do nothing
                    break;
            }
            TotalRotationValueView.setText("Total Rotation: " + Double.toString(totalRotationDegrees));
        }
        lastCardinalDir = cardinalDir;

        //dir is the current direction
        //step_strings.add(dir);//adds direction to steps
        switch(dir) {
            case "N":
                NS_Manhattan += 1.0d * STEP_LENGTH;
                break;
            case "NE":
                NS_Manhattan += Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                EW_Manhattan += Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                break;
            case "E":
                EW_Manhattan += 1.0d * STEP_LENGTH;
                break;
            case "SE":
                NS_Manhattan -= Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                EW_Manhattan += Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                break;
            case "S":
                NS_Manhattan -= 1.0d * STEP_LENGTH;
                break;
            case "SW":
                NS_Manhattan -= Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                EW_Manhattan -= Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                break;
            case "W":
                EW_Manhattan -= 1.0d * STEP_LENGTH;
                break;
            case "NW":
                NS_Manhattan += Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                EW_Manhattan -= Math.sqrt(2.0d)/2.0d * STEP_LENGTH;
                break;
            default:
                //Do nothing
                break;
        }

        //hypotenuse = sqrt(x^2+y^2)
        currDisplacement = Math.sqrt(Math.pow(NS_Manhattan, 2.0d) + Math.pow(EW_Manhattan, 2.0d));
        DisplacementValueView.setText("Displacement: " + Double.toString(currDisplacement));
    }

    private double normalizeDegree(double value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }
}
