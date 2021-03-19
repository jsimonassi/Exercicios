/*
 * Activity: LoginActivity
 *
 * Descrição: Tela que permite ao usuário a autenticação no aplicativo
 */



package com.simonassi.exercicios.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simonassi.exercicios.ApiAcess;
import com.simonassi.exercicios.Config;
import com.simonassi.exercicios.Logged;
import com.simonassi.exercicios.R;
import com.simonassi.exercicios.User;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email_input_txt);
        password = findViewById(R.id.password_input_txt);
        TextView singup = findViewById(R.id.singup_txt);
        Button login = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressBar);
        Config.showProgress(progressBar, false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);

                if(!email.getText().toString().isEmpty())
                    beginSession();
            }
        });

        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SingupActivity.class));
            }
        });

    }

    private void beginSession(){
        Config.showProgress(progressBar,true);
        ApiAcess.searchUser(email.getText().toString().toLowerCase(), new ApiAcess.SearchUserCallback() {
            @Override
            public void onSuccess(User response) {
                Config.showProgress(progressBar,false);
                if(response.getPassword().equals(password.getText().toString())){
                    Logged.currentUser = response;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else
                    onFailure();
            }

            @Override
            public void onFailure() {
                Config.showProgress(progressBar,false);

                final AlertDialog alerta;
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                View view = li.inflate(R.layout.layout_alertdialog, null);
                TextView body = view.findViewById(R.id.body_alert_txt);
                body.setText(getResources().getText(R.string.error_password));
                Button ok = view.findViewById(R.id.ok_alert_btn);
                builder.setView(view);
                alerta = builder.create();
                alerta.show();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alerta.dismiss();
                    }
                });
            }
        });
    }
}
