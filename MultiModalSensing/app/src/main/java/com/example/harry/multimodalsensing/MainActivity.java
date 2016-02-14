package com.example.harry.multimodalsensing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;

    RadioButton radioButtonWalking;
    RadioButton radioButtonRunning;
    RadioButton radioButtonIdle;
    RadioButton radioButtonStairs;
    RadioButton radioButtonJumping;
    RadioButton radioButtonDDR;

    TextView AccelXValueView;
    TextView AccelYValueView;
    TextView AccelZValueView;

    String AccelXValueString;
    String AccelYValueString;
    String AccelZValueString;

    TextView GyroXValueView;
    TextView GyroYValueView;
    TextView GyroZValueView;

    String GyroXValueString;
    String GyroYValueString;
    String GyroZValueString;

    TextView MagnetXValueView;
    TextView MagnetYValueView;
    TextView MagnetZValueView;

    String MagnetXValueString;
    String MagnetYValueString;
    String MagnetZValueString;

    TextView LightValueView;

    String LightValueString;

    private String baseFolder;
    private String filename;
    private String format = "dd-MM-yy_HH:mm:ss";
    private String timestampFineFormat = "dd-MM-yy_HH:mm:ss:SSS";
    private SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
    private SimpleDateFormat sdfFine = new SimpleDateFormat(timestampFineFormat, Locale.US);
    private Context mContext;
    private File file;
    private File path;
    private FileWriter fWriter;
    private File newFile;

    private String exercise_type;

    private String distance;
    private NumberPicker numberPicker;

    private boolean liveDisplayMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        exercise_type = null;
        distance = "0";
        liveDisplayMode = false;

        setContentView(R.layout.select_activity);

        //Link radio buttons to layout
        radioButtonWalking = (RadioButton)findViewById(R.id.radio_walking);
        radioButtonRunning = (RadioButton)findViewById(R.id.radio_running);
        radioButtonIdle = (RadioButton)findViewById(R.id.radio_idle);
        radioButtonStairs = (RadioButton)findViewById(R.id.radio_stairs);
        radioButtonJumping = (RadioButton)findViewById(R.id.radio_jumping);
        radioButtonDDR = (RadioButton)findViewById(R.id.radio_ddr);

        //Set radio button text
        radioButtonWalking.setText("Walking");
        radioButtonRunning.setText("Running");
        radioButtonIdle.setText("Idle");
        radioButtonStairs.setText("Stairs");
        radioButtonJumping.setText("Jumping");
        radioButtonDDR.setText("DDR");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            // assign directions
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            if(liveDisplayMode) {
                AccelXValueView.setText("X: " + x);
                AccelYValueView.setText("Y: " + y);
                AccelZValueView.setText("Z: " + z);
            }
            else {
                AccelXValueString = Float.toString(x);
                AccelYValueString = Float.toString(y);
                AccelZValueString = Float.toString(z);
            }
        }

        else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            if(liveDisplayMode) {
                GyroXValueView.setText("X: " + x);
                GyroYValueView.setText("Y: " + y);
                GyroZValueView.setText("Z: " + z);
            }
            else {
                GyroXValueString = Float.toString(x);
                GyroYValueString = Float.toString(y);
                GyroZValueString = Float.toString(z);
            }
        }

        else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            if(liveDisplayMode) {
                MagnetXValueView.setText("X: "+x);
                MagnetYValueView.setText("Y: "+y);
                MagnetZValueView.setText("Z: "+z);
            }
            else {
                MagnetXValueString = Float.toString(x);
                MagnetYValueString = Float.toString(y);
                MagnetZValueString = Float.toString(z);
            }
        }

        else if(event.sensor.getType()==Sensor.TYPE_LIGHT) {
            float l=event.values[0];

            if(liveDisplayMode) {
                LightValueView.setText("Light: " + l);
            }
            else {
                LightValueString = Float.toString(l);
            }
        }

        //row data entry
        String data;

        if(liveDisplayMode) {
            data = sdfFine.format(new Date())/*.toString()*/ + "," + //timestamp
                    //.substring(3) to get rid of "X: "
                    AccelXValueView.getText().toString().substring(3) + "," +
                    AccelYValueView.getText().toString().substring(3) + "," +
                    AccelZValueView.getText().toString().substring(3) + "," +
                    GyroXValueView.getText().toString().substring(3) + "," +
                    GyroYValueView.getText().toString().substring(3) + "," +
                    GyroZValueView.getText().toString().substring(3) + "," +
                    MagnetXValueView.getText().toString().substring(3) + "," +
                    MagnetYValueView.getText().toString().substring(3) + "," +
                    MagnetZValueView.getText().toString().substring(3) + "," +
                    LightValueView.getText().toString().substring(7) + "\n";
        }
        else {
            data = sdfFine.format(new Date())/*.toString()*/ + "," + //timestamp
                    //.substring(3) to get rid of "X: "
                    AccelXValueString + "," +
                    AccelYValueString + "," +
                    AccelZValueString + "," +
                    GyroXValueString + "," +
                    GyroYValueString + "," +
                    GyroZValueString + "," +
                    MagnetXValueString + "," +
                    MagnetYValueString + "," +
                    MagnetZValueString + "," +
                    LightValueString + "\n";
        }

        //write to file
        try{
            fWriter = new FileWriter(file, true);
            fWriter.write(data);
            fWriter.flush();
            fWriter.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_walking:
                if (checked)
                    exercise_type = "WALKING";
                    break;
            case R.id.radio_running:
                if (checked)
                    exercise_type = "RUNNING";
                    break;
            case R.id.radio_idle:
                if (checked)
                    exercise_type = "IDLE";
                    break;
            case R.id.radio_stairs:
                if (checked)
                    exercise_type = "STAIRS";
                    break;
            case R.id.radio_jumping:
                if (checked)
                    exercise_type = "JUMPING";
                    break;
            case R.id.radio_ddr:
                if (checked)
                    exercise_type = "DDR";
                    break;
        }
    }


    public void startButtonClick(View view) {

        //Change to data view
        setContentView(R.layout.content_scrolling);

        //Timestamp
        String tStampString = sdf.format(new Date());

        //Default activity
        if(exercise_type.isEmpty())
            exercise_type = "IDLE";

        //name the file
        filename = exercise_type + "_" + tStampString + "_" + distance + ".csv";

        //check if external storage is available
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            file = new File(path, filename);
        }
        //revert to internal storage
        else {
            baseFolder = mContext.getFilesDir().getAbsolutePath();
            file = new File(baseFolder + filename);
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


        //Register sensor manager
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        //Register sensor manager sensros
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void getUserDistanceInput() {

        //Bring numberpicker into view
        setContentView(R.layout.distance_user_input);

        //Retrieve numberpicker object
        numberPicker = (NumberPicker)findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100000); //1km = 100000cm
        numberPicker.setWrapSelectorWheel(false); //no wrapping
        numberPicker.setOnLongPressUpdateInterval(100); //default is 300

        //listen for change of value of numberPicker
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                //set the distance
                distance = String.valueOf(newVal);
                System.out.println("Distance: " + distance);
            }
        });
    }

    public void stopButtonClick(View view) {

        //stop sensors
        sensorManager.unregisterListener(this);

        if(!exercise_type.equals("IDLE")) {
            //gets user input distance
            getUserDistanceInput();
        }
        else {
            //get ready for next activity
            setContentView(R.layout.select_activity);
        }
    }

    public void renameFile(View view) {

        //Change file name
        System.out.println("Old file name: " + filename);
        System.out.println("Read Distance: " + distance);
        String newFileName = filename.substring(0, filename.length()-5) + distance + ".csv";
        System.out.println("New file name: " + newFileName);

        //check if external storage is available
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            newFile = new File(path, newFileName);
        }
        //revert to internal storage
        else {
            baseFolder = mContext.getFilesDir().getAbsolutePath();
            newFile = new File(baseFolder + newFileName);
        }

        //if(file.exists() && newFile.exists()) {
        if(file.exists()) {
            file.renameTo(newFile);
            file.setReadable(true);
            file.setWritable(true);
        }
        else {
            System.out.println("Unable to rename file");
        }

        //prepare for another activity
        setContentView(R.layout.select_activity);
    }

}
