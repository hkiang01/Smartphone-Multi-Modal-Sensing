package com.example.harry.multimodalsensing2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private double STEP_LENGTH = 1.0d; //in units as in PA spec
    private boolean logMode = false;
    private boolean logAnyways = false; //for debugging
    private boolean logGyro = true;

    private SensorManager sensorManager;
    String mBearing;
    double degrees = 0.0d;
    TextView BearingValueView;
    String dir = "";
    String lastCardinalDir = "";
    String cardinalDir = "";
    //ArrayList<String> step_strings = new ArrayList<String>();
    double NS_Manhattan = 0.0d;
    double EW_Manhattan = 0.0d;
    double currDisplacement = 0.0d;
    double totalRotationDegrees = 0.0d;
    TextView DisplacementValueView;
    TextView TotalRotationValueView;

    private static final float NS2S = 1.0f/1000000000.0f;
    public static final float EPSILON = 0.000000001f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    String groundTruthDir = "";
    ImageButton groundTruthButtonNorth;
    ImageButton groundTruthButtonEast;
    ImageButton groundTruthButtonSouth;
    ImageButton groundTruthButtonWest;
    Button stepButton;

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


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String baseFolder;
    private String format = "dd-MM-yy_HH:mm:ss";
    private String timestampFineFormat = "dd-MM-yy_HH:mm:ss:SSS";
    private SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
    private SimpleDateFormat sdfFine = new SimpleDateFormat(timestampFineFormat, Locale.US);
    private Context mContext;
    private File file;
    private File fileGyro;
    private File path;
    private FileWriter fWriter;
    private FileWriter fileWriterGyro;
    private String fileName;
    private String fileNameGyro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        //Register listeners for groundTruthButton[N|E|S|W]
        groundTruthButtonNorth = (ImageButton) findViewById(R.id.imageButtonTop);
        groundTruthButtonEast = (ImageButton) findViewById(R.id.imageButtonRight);
        groundTruthButtonSouth = (ImageButton) findViewById(R.id.imageButtonBottom);
        groundTruthButtonWest = (ImageButton) findViewById(R.id.imageButtonLeft);
        groundTruthButtonNorth.setOnClickListener(groundTruthButtonNorthHandler);
        groundTruthButtonEast.setOnClickListener(groundTruthButtonEastHandler);
        groundTruthButtonSouth.setOnClickListener(groundTruthButtonSouthHandler);
        groundTruthButtonWest.setOnClickListener(groundTruthButtonWestHandler);

        stepButton = (Button) findViewById(R.id.button_step);
        stepButton.setOnClickListener(stepButtonHandler);

        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        fileName = "ACTIVITY_" + sdfFine.format(new Date()) + ".csv";
        file = new File(path, fileName);
        if(!file.exists()) {
            logMode = true;
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println("File: " + file.getAbsolutePath());
        }
        else {
            System.out.println("Failed to create log file!");
        }
        //file.setWritable(true, false);
        file.setReadable(true, false);

        //path=same
        fileNameGyro = "GYRO_" + sdfFine.format(new Date()) + ".csv";
        fileGyro = new File(path, fileNameGyro);
        if(!fileGyro.exists()) {
            logMode = true;
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println("File: " + fileGyro.getAbsolutePath());
        }
        else {
            System.out.println("Failed to create gyro file!");
        }
        //fileGyro.setWritable(true, false);
        fileGyro.setReadable(true, false);

        //header row
        if(logMode || logAnyways) {
            String data = "TimeStamp" + "," + //timestamp
                    "Accelerometer_X" + "," + "Accelerometer_Y" + "," + "Accelerometer_Z" + "," +
                    "Gyroscope_X" + "," + "Gyroscope_Y" + "," + "Gyroscope_Z" + "," +
                    "Magnetometer_X" + "," + "Magnetometer_Y" + "," + "Magnetometer_Z" + "," +
                    "Light" + "," +
                    "Measured Displacement" + "," +
                    "Total Rotation" + "," +
                    "Compass Bearing" + "," +
                    "Compass Direction" + "," +
                    "Compass Cardinal Direction" + "," +
                    "Ground Truth Cardinal Direction" + "\n";

            try{
                fWriter = new FileWriter(file, true);
                fWriter.write(data);
                fWriter.flush();
                fWriter.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        //header row
        if(logGyro) {
            String data = "TimeStamp" + "," +
                    "gyroRotateX" + "," +
                    "gyroRotateY" + "," +
                    "gyroRotateZ" + "\n";
            try{
                fileWriterGyro = new FileWriter(fileGyro, true);
                fileWriterGyro.write(data);
                fileWriterGyro.flush();
                fileWriterGyro.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

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

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                sensorManager.SENSOR_DELAY_NORMAL);

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

            //integrate over the angular speed to get angular offset (rotation)
            if(timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;//seconds
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                float angularSpeed = (float)Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                //Normalize rotation vector
                //EPSILON is largest allowable margin of error
                if(angularSpeed > EPSILON) {
                    axisX /= angularSpeed;
                    axisY /= angularSpeed;
                    axisZ /= angularSpeed;
                }

                //integrate around axis by timestep to get delta rotation over timestep
                float thetaOverTwo = angularSpeed*dT/2.0f;
                float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
                float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;//x points to the right
                deltaRotationVector[1] = sinThetaOverTwo * axisY;//y points forward
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;//z points to sky
                deltaRotationVector[3] = cosThetaOverTwo;

                //interested in rotation about z axis, or deltaRotationVector[2]
            }
            timestamp = event.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            /*System.out.println("gyroRotateX: " + deltaRotationVector[0] +
                                " gyroRotateY: " + deltaRotationVector[1] +
                                " gyroRotateZ: " + deltaRotationVector[2]); */

            //row data entry
            if(logGyro) {
                String data = sdfFine.format(new Date()) + "," + //timestamp
                        deltaRotationVector[0] + "," +
                        deltaRotationVector[1] + "," +
                        deltaRotationVector[2] + "\n";

                try{
                    fileWriterGyro = new FileWriter(fileGyro, true);
                    fileWriterGyro.write(data);
                    fileWriterGyro.flush();
                    fileWriterGyro.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

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

        else if(event.sensor.getType()== Sensor.TYPE_STEP_DETECTOR) {
            stepButton.callOnClick();
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
        if(logMode || logAnyways) {
            String data = sdfFine.format(new Date()) + "," + //timestamp
                    Float.toString(accelX) + "," + Float.toString(accelY) + "," + Float.toString(accelZ) + "," +
                    Float.toString(gyroX) + "," + Float.toString(gyroY) + "," + Float.toString(gyroZ) + "," +
                    Float.toString(magnetX) + "," + Float.toString(magnetY) + "," + Float.toString(magnetZ) + "," +
                    Float.toString(lightValue) + "," +
                    Double.toString(currDisplacement) + "," +
                    Double.toString(totalRotationDegrees) + "," +
                    mBearing + "," +
                    dir + "," +
                    cardinalDir + "," +
                    groundTruthDir + "\n";

            try{
                fWriter = new FileWriter(file, true);
                fWriter.write(data);
                fWriter.flush();
                fWriter.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    View.OnClickListener groundTruthButtonNorthHandler = new View.OnClickListener() {
        public void onClick(View v) {
            groundTruthDir = "N";
        }
    };

    View.OnClickListener groundTruthButtonEastHandler = new View.OnClickListener() {
        public void onClick(View v) {
            groundTruthDir = "E";
        }
    };

    View.OnClickListener groundTruthButtonSouthHandler = new View.OnClickListener() {
        public void onClick(View v) {
            groundTruthDir = "S";
        }
    };

    View.OnClickListener groundTruthButtonWestHandler = new View.OnClickListener() {
        public void onClick(View v) {
            groundTruthDir = "W";
        }
    };

    //light up red when step is detected, when onStep is actuated
    View.OnClickListener stepButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            final int oldColor = stepButton.getCurrentTextColor();
            stepButton.setTextColor(Color.RED);

            new CountDownTimer(300, 50) {

                @Override
                public void onTick(long arg0) {
                    //auto-generated
                }

                @Override
                public void onFinish() {
                    stepButton.setTextColor(oldColor);
                }

            }.start();

            onStep(v);
        }
    };
}
