package com.example.harry.multimodalsensing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
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
    private FileWriter mFileWriter;
    private CSVWriter writer;
    private boolean ready = false;

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
    private String filename;
    private String format = "dd-MM-yy HH:mm:ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
    private Context mContext;
    private File file;
    private FileOutputStream fos;
    private File path;
    private FileWriter fWriter;
    private boolean startClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity);

        mContext = getApplicationContext();

        exercise_type = null;
        startClicked = false;

        while(exercise_type.equals(null));

        filename = exercise_type + sdf.format(new Date())/*.toString()*/ + "_0.csv";
        //file = new File(mContext.getFilesDir(), filename);
        //file.setReadable(true, false);

        //check if external storage is available
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //baseFolder = mContext.getExternalFilesDir(null).getAbsolutePath();
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            file = new File(path, filename);
            path.mkdirs();
            System.out.println("if case");
        }
        //revert to internal storage
        else {
            baseFolder = mContext.getFilesDir().getAbsolutePath();
            file = new File(baseFolder + filename);
            System.out.println("else case");
        }

        while(!startClicked);

        setContentView(R.layout.content_scrolling);
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

        /*
        try{
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

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
        startClicked = true;
    }

    public void stopButtonClick(View view) {
        sensorManager.unregisterListener(this);

    }

}
