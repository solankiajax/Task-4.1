package com.example.studytimer;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView displayTimeView,time,taskNamePopUp,taskName;
    ImageButton playButton,pauseButton,stopButton;
    Timer timer;
    TimerTask task;
    Double timerTime =00.00;
    boolean timerStarted = false;
    boolean isPaused = false;
    String displayText;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayTimeView = findViewById(R.id.displayTimeView);
        time = findViewById(R.id.time);
        taskNamePopUp = findViewById(R.id.taskNamePopUp);
        taskName = findViewById(R.id.taskName);

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        timer = new Timer();
        checkSharedPreferences();

        sharedPreferences = getSharedPreferences("com.example.studytimer",MODE_PRIVATE);
        checkSharedPreferences();

        if (savedInstanceState!=null){
            timerTime=savedInstanceState.getDouble("timertime");
            timerStarted=savedInstanceState.getBoolean("timerstarted");
            isPaused = savedInstanceState.getBoolean("ispaused");
            displayTimeView.setText(savedInstanceState.getString("textView"));
            taskName.setText(savedInstanceState.getString("taskname"));

            if(timerStarted) {
                startTimer();
            }
            else if((task==null)&&(isPaused)){
                time.setText(getTime());
            }
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPaused = false;
                timerStarted = true;
                startTimer();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (task!=null){
                isPaused = true;
                timerStarted = false;
                task.cancel();}
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(task!=null){
                    task.cancel();

                    if(TextUtils.isEmpty(taskName.getText().toString())){
                        sharedPreferences.edit().putString(displayText,"You spent 00:00 on ... last time.").apply();
                    }
                    else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(displayText,String.format("You spent %s on %s last time.",getTime(),taskName.getText().toString()));
                        editor.apply();
                    }

                    timerStarted = false;
                    isPaused = false;
                    timerTime = 0.0;
                    time.setText(format(0,0,0));
                }
            }
        });
    }


        private void startTimer(){
            task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        public void run(){
                            timerTime++;
                            time.setText(getTime());
                        }
                    });
                }
            };
            timer.schedule(task,0,1000);
        }
        private String getTime() {
            int timeRounded = (int)Math.round(timerTime);

            int seconds = ((timeRounded%86400)%3600)%60;
            int minutes = ((timeRounded%86400)%3600)/60;
            int hours = ((timeRounded%86400)/3600);

            return format(hours,minutes,seconds);
        }

        private String format(int h,int m,int s){
            return String.format("%02d",h) + ":" + String.format("%02d",m) + ":" + String.format("%02d",s);
        }

        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putDouble("timertime",timerTime);
            outState.putBoolean("timerstarted",timerStarted);
            outState.putBoolean("ispaused",isPaused);
            outState.putString("textView",displayTimeView.getText().toString());
            if(!TextUtils.isEmpty(taskName.getText().toString())){
                outState.putString("taskname",taskName.getText().toString());
            }
        }

        public void checkSharedPreferences(){
        try{
            if(sharedPreferences != null){
            String text = sharedPreferences.getString(displayText,"You spent 00:00 on ... last time.");
            displayTimeView.setText(text);}}
            catch(Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

            }
        }


}