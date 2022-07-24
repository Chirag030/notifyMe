package com.example.notifyme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String titleKey = "titleKey";
    public static final String descriptionKey = "descriptionKey";
    public static final String durationKey = "durationKey";

    SharedPreferences sharedpreferences;
    TextInputEditText title, description, duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.notifyTitle);
        description = findViewById(R.id.notifyDescription);
        duration = findViewById(R.id.notifyTime);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Button btnStart = findViewById(R.id.startNotification);
        btnStart.setOnClickListener(view -> {
            if(isValid()) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(titleKey, Objects.requireNonNull(title.getText()).toString().trim());
                editor.putString(descriptionKey, Objects.requireNonNull(description.getText()).toString().trim());
                editor.putInt(durationKey, Integer.parseInt(duration.getText().toString().trim()));
                editor.apply();
                startService(new Intent(this, MyService.class));
            }
        });

        Button btnStop = findViewById(R.id.stopNotification);
        btnStop.setOnClickListener(view -> {
            stopService(new Intent(this, MyService.class));
        });
    }

    private boolean isValid() {
        if (Objects.requireNonNull(title.getText()).toString().isEmpty()){
            title.requestFocus();
            title.setError("This field can not be empty");
            return false;
        }
        if (Objects.requireNonNull(description.getText()).toString().isEmpty()){
            description.requestFocus();
            description.setError("This field can not be empty");
            return false;
        }
        if (Objects.requireNonNull(duration.getText()).toString().isEmpty()){
            duration.requestFocus();
            duration.setError("This field can not be empty");
            return false;
        }
        return true;
    }
}