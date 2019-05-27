package com.example.aula3;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = findViewById(R.id.tv_username);
                password = findViewById(R.id.tv_password);

                if (username.getText().toString().equals(password.getText().toString())) {
                    ((TextView) findViewById(R.id.tv_info)).setText("sucess");
                    Intent intent = new Intent(MainActivity.this,
                            AfterloginActivity.class );
                    startActivity(intent);
                    finish();
                } else {
                   new AlertDialog.Builder(MainActivity.this)
                           .setTitle("Login")
                           .setMessage("Invalid credentials")
                           .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                               }
                           })
                           .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                               }
                           })
                           .show();
                }
            }
        });


    }
}
