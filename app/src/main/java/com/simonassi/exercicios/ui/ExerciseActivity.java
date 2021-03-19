/*
 * Activity: ExerciseActivity
 *
 * Descrição: Tela que apresenta o exercício em questão e permite ao usuário iniciar uma nova atividade.
 */



package com.simonassi.exercicios.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.simonassi.exercicios.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExerciseActivity extends AppCompatActivity {

    ImageView title;
    Button start;
    String exerciseType;
    String timestamp;
    private static Double minElevation, maxElevation;
    private static TextView distanceInfo;
    private static TextView altitudeInfo;
    private static TextView section_label;
    private static long initialTime;
    private static Handler handler;
    private static boolean isRunning;
    private static final long MILLIS_IN_SEC = 1000L;
    private static final int SECS_IN_MIN = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_exercise);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapsFragment fragment = new MapsFragment();
        fragmentTransaction.add(R.id.container_id, fragment);
        fragmentTransaction.commit();

        Intent intent = getIntent();
        if(intent==null){
            onBackPressed();
        }
        section_label = findViewById(R.id.time_txt);
        exerciseType = intent.getStringExtra("exercise");
        title = findViewById(R.id.title_image);
        start = findViewById(R.id.start_exercise_btn);
        distanceInfo = findViewById(R.id.info_distance_txt);
        altitudeInfo = findViewById(R.id.info_elevation_txt);

        switch (exerciseType){
            case "0":
                title.setImageResource(R.drawable.caminhada_icon);

                break;

            case "1":
                title.setImageResource(R.drawable.corrida_icon);
                break;

            case "2":
                title.setImageResource(R.drawable.escalada_icon);
                break;

            case "3":
                title.setImageResource(R.drawable.aerobico_icon);
                break;
            default:
                onBackPressed();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRunning) {
                    isRunning = true;
                    MapsFragment.IN_EXERCISE = true;
                    initialTime = System.currentTimeMillis();
                    handler.postDelayed(runnable, MILLIS_IN_SEC);
                    timestamp = getDateTime();
                    minElevation = maxElevation = MapsFragment.getAltitude();
                    start.setText(R.string.end_exercise);
                }else{
                    isRunning = false;
                    MapsFragment.IN_EXERCISE = false;
                    handler.removeCallbacks(runnable);
                    showExercise();
                }
            }
        });

        handler = new Handler();
    }

    private final static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long seconds = (System.currentTimeMillis() - initialTime) / MILLIS_IN_SEC;
                section_label.setText(String.format("%02d:%02d", seconds / SECS_IN_MIN, seconds % SECS_IN_MIN));
                refreshInfos();
                handler.postDelayed(runnable, MILLIS_IN_SEC);
            }
        }
    };

    private static void refreshInfos(){
        Double currentAltitude = MapsFragment.getAltitude();
        distanceInfo.setText(String.format("%.2f", MapsFragment.getDistance()));
        altitudeInfo.setText(String.format("%.2f", currentAltitude));
        if(currentAltitude > maxElevation)
            maxElevation = currentAltitude;
        if(currentAltitude < minElevation)
            minElevation = currentAltitude;

    }

    private void showExercise(){
        Intent intent = new Intent(ExerciseActivity.this, ReviewExerciseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("duration", section_label.getText()+"");
        bundle.putString("distance", distanceInfo.getText()+"");
        bundle.putString("altitude", altitudeInfo.getText()+"");
        bundle.putString("exercise_type", exerciseType);
        bundle.putString("timestamp", timestamp);
        bundle.putString("max_elevation", String.format("%.2f", maxElevation)+"m");
        bundle.putString("min_elevation", String.format("%.2f", minElevation)+"m");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
