/*
 * Activity: MainActivity
 *
 * Descrição: Tela principal do app. Permitge ao usuário iniciar novos exercícios e ver o histórico de
 * atividades realizadas, dentre outras tarefas.
 */


package com.simonassi.exercicios.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simonassi.exercicios.ApiAcess;
import com.simonassi.exercicios.Exercise;
import com.simonassi.exercicios.Logged;
import com.simonassi.exercicios.R;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

public class MainActivity extends AppCompatActivity {

    private LinearLayout content;
    private GroupAdapter adapter;
    private RecyclerView rv;
    private ImageView perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        content = findViewById(R.id.layout_content);

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View v = li.inflate(R.layout.layout_novo_exercicio, null);
        content.addView(v);

        TextView title = findViewById(R.id.title_main);
        if (Logged.currentUser != null) {
            String[] name = Logged.currentUser.getName().split(" ");
            title.setText("Olá, " + name[0] + "!");
        }

        perfil = findViewById(R.id.img_perfil_id);
        if(!Logged.currentUser.getPhoto().equals("")){
            Picasso.get().load(Logged.currentUser.getPhoto()).into(perfil);
        }
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // crop image
                i.putExtra("crop", "true");
                i.putExtra("aspectX", 100);
                i.putExtra("aspectY", 100);
                i.putExtra("outputX",512);
                i.putExtra("outputY", 512);

                try {
                    i.putExtra("return-data", true);
                    startActivityForResult(
                            Intent.createChooser(i, "Select Picture"), 0);
                }catch (ActivityNotFoundException ex){
                    ex.printStackTrace();
                }
            }
        });

        final Button newExercise, historic;
        newExercise = findViewById(R.id.new_exercise_btn);
        historic = findViewById(R.id.historic_btn);
        configListener(v);

        newExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content.removeAllViews();
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View v = li.inflate(R.layout.layout_novo_exercicio, null);
                content.addView(v);

                newExercise.setBackgroundColor(getResources().getColor(R.color.background_light));
                newExercise.setTextColor(Color.BLACK);

                historic.setTextColor(Color.WHITE);
                historic.setBackground(getResources().getDrawable(R.drawable.background_login));
                configListener(v);
            }
        });

        historic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content.removeAllViews();
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View v = li.inflate(R.layout.layout_historico_exercicios, null);
                content.addView(v);
                historic.setBackgroundColor(getResources().getColor(R.color.background_light));
                historic.setTextColor(Color.BLACK);

                newExercise.setTextColor(Color.WHITE);
                newExercise.setBackground(getResources().getDrawable(R.drawable.background_login));

                final Button filterExercise0 = v.findViewById(R.id.exercise0_filter);
                final Button filterExercise1 = v.findViewById(R.id.exercise1_filter);
                final Button filterExercise2 = v.findViewById(R.id.exercise2_filter);
                final Button filterExercise3 = v.findViewById(R.id.exercise3_filter);
                final boolean[] filter0 = new boolean[1];
                final boolean[] filter1 = new boolean[1];
                final boolean[] filter2 = new boolean[1];
                final boolean[] filter3 = new boolean[1];
                filter0[0] = filter1[0] = filter2[0] = filter3[0] = false;

                filterExercise0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(filter0[0]){
                            filterExercise0.setBackground(getDrawable(R.drawable.background_buttons));
                            filterExercise0.setTextColor(Color.BLACK);
                            filter0[0] =false;
                            refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
                            return;
                        }
                        filterExercise0.setBackground(getDrawable(R.drawable.background_buttons_dark));
                        filterExercise0.setTextColor(Color.WHITE);
                        filter0[0]=true;

                        filterExercise1.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise1.setTextColor(Color.BLACK);
                        filter1[0] =false;

                        filterExercise2.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise2.setTextColor(Color.BLACK);
                        filter2[0] =false;

                        filterExercise3.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise3.setTextColor(Color.BLACK);
                        filter3[0] =false;
                        refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
                    }
                });

                filterExercise1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(filter1[0]){
                            filterExercise1.setBackground(getDrawable(R.drawable.background_buttons));
                            filterExercise1.setTextColor(Color.BLACK);
                            filter1[0] =false;
                            refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
                            return;
                        }
                        filterExercise0.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise0.setTextColor(Color.BLACK);
                        filter0[0] =false;

                        filterExercise1.setBackground(getDrawable(R.drawable.background_buttons_dark));
                        filterExercise1.setTextColor(Color.WHITE);
                        filter1[0]=true;

                        filterExercise2.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise2.setTextColor(Color.BLACK);
                        filter2[0] =false;

                        filterExercise3.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise3.setTextColor(Color.BLACK);
                        filter3[0] =false;

                        refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
                    }
                });

                filterExercise2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(filter2[0]){
                            filterExercise2.setBackground(getDrawable(R.drawable.background_buttons));
                            filterExercise2.setTextColor(Color.BLACK);
                            filter2[0] =false;
                            refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
                            return;
                        }
                        filterExercise0.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise0.setTextColor(Color.BLACK);
                        filter0[0] =false;

                        filterExercise1.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise1.setTextColor(Color.BLACK);
                        filter1[0] =false;

                        filterExercise2.setBackground(getDrawable(R.drawable.background_buttons_dark));
                        filterExercise2.setTextColor(Color.WHITE);
                        filter2[0]=true;

                        filterExercise3.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise3.setTextColor(Color.BLACK);
                        filter3[0] =false;

                        refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
                    }
                });

                filterExercise3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(filter3[0]){
                            filterExercise3.setBackground(getDrawable(R.drawable.background_buttons));
                            filterExercise3.setTextColor(Color.BLACK);
                            filter3[0] =false;
                            refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
                            return;
                        }
                        filterExercise0.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise0.setTextColor(Color.BLACK);
                        filter0[0] =false;

                        filterExercise1.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise1.setTextColor(Color.BLACK);
                        filter1[0] =false;

                        filterExercise2.setBackground(getDrawable(R.drawable.background_buttons));
                        filterExercise2.setTextColor(Color.BLACK);
                        filter2[0] =false;

                        filterExercise3.setBackground(getDrawable(R.drawable.background_buttons_dark));
                        filterExercise3.setTextColor(Color.WHITE);
                        filter3[0]=true;


                        refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
                    }
                });

                rv = v.findViewById(R.id.recyrcler_view);
                adapter = new GroupAdapter();
                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv.setAdapter(adapter);

                refreshHistoric(filter0[0],filter1[0],filter2[0],filter3[0]);
            }
        });
    }

    private void configListener(View v){
        ImageView caminhada = v.findViewById(R.id.caminhada_exercise),
                corrida = v.findViewById(R.id.corrida_exercise),
                escalada = v.findViewById(R.id.escalada_exercise),
                aerobica = v.findViewById(R.id.aerobica_exercise);

        caminhada.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
                 intent.putExtra("exercise", "0");
                 startActivity(intent);
             }
         }
        );

        corrida.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
                 intent.putExtra("exercise", "1");
                 startActivity(intent);
             }
         }
        );

        escalada.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
                 intent.putExtra("exercise", "2");
                 startActivity(intent);
             }
         }
        );

        aerobica.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
                 intent.putExtra("exercise", "3");
                 startActivity(intent);
             }
         }
        );
    }

    private class ExerciseItem extends Item<com.xwray.groupie.GroupieViewHolder> {

        private final Exercise exercise;

        private ExerciseItem(Exercise exercise){
            this.exercise = exercise;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView exerciseType = viewHolder.itemView.findViewById(R.id.exercise_type_historic);
            TextView timeStamp = viewHolder.itemView.findViewById(R.id.timestamp_text_historic_txt);
            TextView duration = viewHolder.itemView.findViewById(R.id.duration_text_historic_txt);
            TextView distance = viewHolder.itemView.findViewById(R.id.distance_text_historic_txt);
            Button delete = viewHolder.itemView.findViewById(R.id.delete_historic_btn);
            Button edit = viewHolder.itemView.findViewById(R.id.edit_historic_btn);

            timeStamp.setText(exercise.getRegistered_at());
            duration.setText(exercise.getDuration());
            distance.setText(exercise.getDistance());

            if (exercise.getExercise_type().equals("0")) {
                exerciseType.setText(getResources().getString(R.string.exercise_0));
            } else if (exercise.getExercise_type().equals("1")) {
                exerciseType.setText(getResources().getString(R.string.exercise_1));
            } else if (exercise.getExercise_type().equals("2")) {
                exerciseType.setText(getResources().getString(R.string.exercise_2));
            } else {
                exerciseType.setText(getResources().getString(R.string.exercise_3));
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog alerta;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    LayoutInflater li = LayoutInflater.from(MainActivity.this);
                    View viewAlert = li.inflate(R.layout.layout_alertdialog, null);
                    TextView body = viewAlert.findViewById(R.id.body_alert_txt);
                    body.setText(getResources().getText(R.string.delete_body));
                    Button ok = viewAlert.findViewById(R.id.ok_alert_btn);
                    builder.setView(viewAlert);
                    alerta = builder.create();
                    alerta.show();

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ApiAcess.deleteExercise(exercise.getRegistered_at());
                            Log.d("DELETE", ""+exercise.getRegistered_at());
                            alerta.dismiss();
                        }
                    });
                }
            });
        }

        public int getLayout(){

            return R.layout.layout_element_historic;
        }
    }

    private void refreshHistoric(boolean filter0, boolean filter1, boolean filter2, boolean filter3){
        ApiAcess.searchExercises(filter0,filter1,filter2, filter3, new ApiAcess.SearchExercisesCallback() {
            @Override
            public void onSuccess(ArrayList<Exercise> exercicios) {
                adapter.clear();
                for(Exercise exercise : exercicios){
                    adapter.add(new ExerciseItem(exercise));
                }
            }
            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode == Activity.RESULT_OK){
            try {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                perfil.setImageBitmap(bitmap);

                ApiAcess.updateProfilePicture(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
