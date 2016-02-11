package com.example.harry.multimodalsensing;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sensorManager;
    private FileWriter mFileWriter;
    private CSVWriter writer;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);

        //http://stackoverflow.com/questions/17645092/export-my-data-on-csv-file-from-app-android
        //http://stackoverflow.com/questions/27772011/how-to-export-data-to-csv-file-in-android
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "RUNNING.csv";
        String filePath = baseDir + File.separator + fileName;

        //Option 1:
        try {
            writer = new CSVWriter(new FileWriter(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //End of option 1

        /*
        //Option 2:
        File f = new File(filePath );

        //File exists
        if(f.exists() && !f.isDirectory()) {
            try {
                mFileWriter = new FileWriter(filePath, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //above throws IO Exception: http://www.anddev.org/working_with_files-t115.html

            writer = new CSVWriter(mFileWriter);
        }
        else {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //End of option 2
        */

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

        String[] data = {AccelXValueView.getText().toString(),
                         AccelYValueView.getText().toString(),
                         AccelZValueView.getText().toString(),
                         GyroXValueView.getText().toString(),
                         GyroYValueView.getText().toString(),
                         GyroZValueView.getText().toString(),
                         MagnetXValueView.getText().toString(),
                         MagnetYValueView.getText().toString(),
                         MagnetZValueView.getText().toString(),
                         LightValueView.getText().toString()};
        writer.writeNext(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
