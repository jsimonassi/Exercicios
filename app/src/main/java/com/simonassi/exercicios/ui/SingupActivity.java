/*
 * Activity: SingupActivity
 *
 * Descrição: Tela de cadastro de um novo usuário.
 */

package com.simonassi.exercicios.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simonassi.exercicios.ApiAcess;
import com.simonassi.exercicios.Config;
import com.simonassi.exercicios.Logged;
import com.simonassi.exercicios.R;
import com.simonassi.exercicios.User;

public class SingupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_singup);

        final EditText name = findViewById(R.id.name_signup_txt3),
                email = findViewById(R.id.email_signup_txt),
                password = findViewById(R.id.password_signup_txt),
                weight = findViewById(R.id.user_size_input);
        final Button cancel = findViewById(R.id.cancel_btn),
                newAccount = findViewById(R.id.new_account_btn);

        final ProgressBar progressBar = findViewById(R.id.progress_bar_new_account);
        Config.showProgress(progressBar, false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config.showProgress(progressBar, true);

                if(!name.getText().toString().isEmpty() &&
                !email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() &&
                !weight.getText().toString().isEmpty()){

                    Config.showProgress(progressBar, true);

                    //(String name, String photo, String email, String password, Float weight)
                    final User newUser = new User();
                    newUser.setName(name.getText().toString());
                    newUser.setEmail(email.getText().toString());
                    newUser.setPhoto("");
                    newUser.setPassword(password.getText().toString());
                    newUser.setWeight(Float.parseFloat(weight.getText().toString()));

                    ApiAcess.addUser(newUser, new ApiAcess.addUserCallback() {
                        @Override
                        public void onSuccess() {
                            Logged.currentUser = newUser;
                            Intent intent = new Intent(SingupActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure() {

                            Config.showProgress(progressBar, false);

                            final AlertDialog alerta;
                            AlertDialog.Builder builder = new AlertDialog.Builder(SingupActivity.this);
                            LayoutInflater li = LayoutInflater.from(SingupActivity.this);
                            View v = li.inflate(R.layout.layout_alertdialog, null);
                            TextView body = v.findViewById(R.id.body_alert_txt);
                            body.setText(getResources().getText(R.string.error_create_user));
                            Button ok = v.findViewById(R.id.ok_alert_btn);
                            builder.setView(v);
                            alerta = builder.create();
                            alerta.show();
                            Config.showProgress(progressBar, false);

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                        }
                    });

                }else{
                    final AlertDialog alerta;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SingupActivity.this);
                    LayoutInflater li = LayoutInflater.from(SingupActivity.this);
                    View v = li.inflate(R.layout.layout_alertdialog, null);
                    TextView body = v.findViewById(R.id.body_alert_txt);
                    body.setText(getResources().getText(R.string.error_singup));
                    Button ok = v.findViewById(R.id.ok_alert_btn);
                    builder.setView(v);
                    alerta = builder.create();
                    alerta.show();
                    Config.showProgress(progressBar, false);

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alerta.dismiss();
                        }
                    });
                }
            }
        });
    }
}
