package com.example.harry.multimodalsensing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    TextView GyroXValueView;
    TextView GyroYValueView;
    TextView GyroZValueView;

    TextView MagnetXValueView;
    TextView MagnetYValueView;
    TextView MagnetZValueView;

    TextView LightValueView;

    private String baseFolder;
    private String exercise_type;
    private String distance;
    private String filename;
    private String format = "dd-MM-yy_HH:mm:ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
    private Context mContext;
    private File file;
    private File path;
    private FileWriter fWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        exercise_type = null;
        distance = "0";

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

        else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
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

        //row data entry
        String data = sdf.format(new Date())/*.toString()*/ + "," + //timestamp
                //.substring(3) to get rid of "X: "
                AccelXValueView.getText().toString().substring(3) + "," +
                AccelYValueView.getText().toString().substring(3)  + "," +
                AccelZValueView.getText().toString().substring(3) + "," +
                GyroXValueView.getText().toString().substring(3) + "," +
                GyroYValueView.getText().toString().substring(3) + "," +
                GyroZValueView.getText().toString().substring(3) + "," +
                MagnetXValueView.getText().toString().substring(3) + "," +
                MagnetYValueView.getText().toString().substring(3) + "," +
                MagnetZValueView.getText().toString().substring(3) + "," +
                LightValueView.getText().toString().substring(7) + "\n";

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
    }

    public void stopButtonClick(View view) {

        //stop sensors
        sensorManager.unregisterListener(this);

        //prepare for another activity
        setContentView(R.layout.select_activity);
    }

}
