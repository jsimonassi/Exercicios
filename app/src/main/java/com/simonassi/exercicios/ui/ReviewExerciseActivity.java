/*
 * Activity: ReviewExerciseActivity
 *
 * Descrição: Tela que apresenta os detalhes do exercício realizado pelo usuário e solicita sua gravação
 * ao banco de dados.
 */



package com.simonassi.exercicios.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simonassi.exercicios.ApiAcess;
import com.simonassi.exercicios.Config;
import com.simonassi.exercicios.Exercise;
import com.simonassi.exercicios.Logged;
import com.simonassi.exercicios.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewExerciseActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_review_exercise);

        final Bundle bundle = getIntent().getExtras();

        if(bundle != null) {

            final TextView timestamp_info = findViewById(R.id.timestamp_txt);
            final TextView exercise_type_info = findViewById(R.id.exercise_type_txt);
            final TextView duration_info = findViewById(R.id.duration_txt);
            TextView duration_info2 = findViewById(R.id.duration_info2_txt);
            final TextView distance_info = findViewById(R.id.distance_txt);
            final TextView kcal_info = findViewById(R.id.kcal_info_txt);
            final TextView max_elevation_info = findViewById(R.id.max_elevation_txt);
            TextView min_elevation_info = findViewById(R.id.min_elevation_txt);
            TextView steps_info = findViewById(R.id.steps_txt);
            TextView bpm_info = findViewById(R.id.bpm_txt);
            Button save = findViewById(R.id.save_exercise_btn);
            Button cancel = findViewById(R.id.cancel_btn);
            progressBar = findViewById(R.id.progressBar_review);
            Config.showProgress(progressBar, false);


            timestamp_info.setText(bundle.getString("timestamp"));

            switch (bundle.getString("exercise_type")){
                case "0":
                    exercise_type_info.setText(getResources().getString(R.string.exercise_0));
                    break;
                case "1":
                    exercise_type_info.setText(getResources().getString(R.string.exercise_1));
                    break;
                case "2":
                    exercise_type_info.setText(getResources().getString(R.string.exercise_2));
                     break;
                case "3":
                    exercise_type_info.setText(getResources().getString(R.string.exercise_3));
                    break;
                default:
                    exercise_type_info.setText(getResources().getString(R.string.no_content));
                    break;
            }

            duration_info.setText(bundle.getString("duration"));
            duration_info2.setText(bundle.getString("duration"));
            distance_info.setText(bundle.getString("distance") + "km");
            min_elevation_info.setText(bundle.getString("min_elevation"));
            max_elevation_info.setText(bundle.getString("max_elevation"));


            kcal_info.setText(getKcal(bundle.getString("exercise_type"), Float.parseFloat(bundle.getString("duration").replace(":",".")))+"");
            bpm_info.setText(getResources().getString(R.string.no_content));



            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Config.showProgress(progressBar, true);

                    Exercise newExercise = new Exercise(
                            timestamp_info.getText()+"",
                            bundle.getString("exercise_type"),
                            "N/A",
                            "N/A",
                            duration_info.getText()+"",
                            distance_info.getText()+"",
                            max_elevation_info.getText()+"",
                            kcal_info.getText()+"");

                    ApiAcess.addExercise(newExercise, new ApiAcess.addExerciseCallback() {
                        @Override
                        public void onSuccess() {
                            showMessageStatus(0);
                        }

                        @Override
                        public void onFailure() {
                            Config.showProgress(progressBar, false);
                            showMessageStatus(1);
                        }
                    });
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ReviewExerciseActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

        }
        else
            showMessageStatus(-1);

    }

    public void showMessageStatus(int cod){
        final AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewExerciseActivity.this);
        LayoutInflater li = LayoutInflater.from(ReviewExerciseActivity.this);
        View view = li.inflate(R.layout.layout_alertdialog, null);
        TextView body = view.findViewById(R.id.body_alert_txt);
        TextView title = view.findViewById(R.id.title_alert_txt);
        Button ok = view.findViewById(R.id.ok_alert_btn);
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

        if (cod == -1) //Bundle == null
            body.setText(getResources().getText(R.string.recovery_error));
        else if(cod == 0){//200 Ok Save success!
            title.setText(getResources().getString(R.string.save_success));
            body.setText(getResources().getText(R.string.save_success_body));

        }else
            body.setText(getResources().getText(R.string.error_save_exercise_body));


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReviewExerciseActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    private int getKcal(String exerciseType, float duration) {
        Log.d("TAG",Logged.currentUser.getWeight() +" <-peso " + duration+ "Duration");

        if(Logged.currentUser==null)
            return 0;

        int fator;

        if (exerciseType.equals("0"))
            fator = 2;
        else if (exerciseType.equals("3"))
            fator = 4;
        else
            fator = 5;

        return (int) ((Logged.currentUser.getWeight() * duration * fator)/10);
    }
}
