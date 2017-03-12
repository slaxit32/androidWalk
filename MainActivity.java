package com.example.my.walk2;



        import java.io.FileOutputStream;
        import java.math.BigDecimal;
        import java.security.Timestamp;
        import java.sql.Time;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.Iterator;


        import android.app.Activity;
        import android.content.Context;
        import android.content.pm.ActivityInfo;
        import android.graphics.Color;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.Environment;
        import android.text.method.ScrollingMovementMethod;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.CompoundButton;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.CompoundButton.OnCheckedChangeListener;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;


        import java.io.File;

public class MainActivity extends Activity implements SensorEventListener,OnClickListener {

    private SensorManager sensorManager;
    private Button btnStart, btnStop, btnUpload;
    EditText et1;
    TextView tv1,tvlo;
    private boolean started = false;
    Switch mySwitch;
    ArrayList<String> list = new ArrayList<String>();
    int swi=0,count=0;
    long timestamp = System.currentTimeMillis();

    String fileName;
    private final String filepath = "/mnt/sdcard/";
    private BufferedWriter mBufferedWriter;
    private BufferedReader mBufferedReader;
    private String read_str = "";

    long startTime,endTime;

    String tmpAdd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initVar();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    swi=1;
                }else{
                    swi=0;
                }

            }
        });



    }

    private void initVar() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        tv1 = (TextView) findViewById(R.id.textView);
        mySwitch = (Switch) findViewById(R.id.switch1);
        et1=(EditText)findViewById(R.id.editText);
        tvlo=(TextView)findViewById(R.id.tvlog);

        tvlo.setMovementMethod(new ScrollingMovementMethod());

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        mySwitch.setChecked(false);
        et1.setEnabled(false);
        btnUpload.setEnabled(false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];


            String tempData = Long.toString(event.timestamp/1000000) + "," + Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z)+"\n";

            if(swi==1){
                tv1.setText("X: "+Double.toString(x) + "\nY:" + Double.toString(y) + "\nZ:" + Double.toString(z));
            }

            tmpAdd+=tempData;
            count++;

        }
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started == true) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick (View v){
        switch (v.getId()) {
            case R.id.btnStart:
                startTime = System.currentTimeMillis();
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnUpload.setEnabled(false);
                et1.setEnabled(false);
                started = true;
                Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accel,SensorManager.SENSOR_DELAY_FASTEST);
                log("Data collection started");
                break;
            case R.id.btnStop:
                endTime   = System.currentTimeMillis();
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnUpload.setEnabled(true);
                et1.setEnabled(true);
                started = false;
                sensorManager.unregisterListener(this);
                log("Data collection end");

                break;
            case R.id.btnUpload:
                try {
                    long timeSpent=(endTime - startTime)/1000;
                    int anInt = new BigDecimal(timeSpent).intValueExact();
                    log("Duration "+Long.toString(timeSpent)+" seconds");
                    log("Rate "+count/anInt+"/s");
                }
                catch (Exception e){

                }

                String t=et1.getText().toString();
                if(t.equals("")){
                    log("Enter file name to save");
                }
                else{
                    log("saving "+count+" items");

                    fileName=et1.getText()+"-"+Long.toString(timestamp)+".csv";


                    WriteFile(filepath+fileName,tmpAdd);
                    log("finish saving");
                }
                break;

        }
    }


    public void log(String l){
        tvlo.setText(tvlo.getText()+"\n"+l);
    }



//---------------file handling---------------------------

    public void CreateFile(String path){
        File f = new File(path);
        try {

            f.createNewFile();
            log("Create a File at "+path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log("file create error");
        }
    }

    public void WriteFile(String filepath, String str){

        mBufferedWriter = null;

        if (!FileIsExist(filepath))
            CreateFile(filepath);

        try{
            mBufferedWriter = new BufferedWriter(new FileWriter(filepath, true));
            mBufferedWriter.write(str);
            mBufferedWriter.newLine();
            mBufferedWriter.flush();
            mBufferedWriter.close();
            //log("write to file");
        }
        catch (IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            log("write to file error");

        }
    }

    public boolean FileIsExist(String filepath){
        File f = new File(filepath);

        if (! f.exists()){
            //   log("ACTIVITY File does not exist.");
            return false;
        }
        else
            return true;
    }



//---------------file handling---------------------------
}

