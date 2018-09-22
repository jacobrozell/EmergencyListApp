package com.example.magra.projectapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class Menu extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Use the chosen theme
        sharedPreferences = getSharedPreferences("com.example.magra.projectapp", MODE_PRIVATE);
        boolean useDarkTheme = sharedPreferences.getBoolean("darkTheme", false);

        if (useDarkTheme) {
            setTheme(R.style.MainTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView memberTextView = findViewById(R.id.membersTextView);
        TextView daysTextView = findViewById(R.id.daysTextView);

        sharedPreferences = this.getSharedPreferences("com.example.magra.projectapp", Context.MODE_PRIVATE);

        String savedMembers = sharedPreferences.getString("membersInFamily", "");
        String savedDays = sharedPreferences.getString("daysAbsent", "");
        memberTextView.setText(savedMembers);
        daysTextView.setText(savedDays);


        final Switch toggle = findViewById(R.id.themeChangeSwitch);
        toggle.setChecked(useDarkTheme);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                isChecked = toggle.isChecked();
                toggleTheme(isChecked);
            }
        });
    }

    private void toggleTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences("com.example.magra.projectapp", MODE_PRIVATE).edit();
        editor.putBoolean("darkTheme", darkTheme);
        editor.apply();

        Intent intent = getIntent();
        finish();

        startActivity(intent);
    }

    public void submit(View view) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        EditText members = findViewById(R.id.membersEditText);
        EditText days = findViewById(R.id.daysEditText);
        TextView memberTextView = findViewById(R.id.membersTextView);
        TextView daysTextView = findViewById(R.id.daysTextView);

        memberTextView.setText(members.getText().toString());
        daysTextView.setText(days.getText().toString());

        editor.putString("membersInFamily", memberTextView.getText().toString());
        editor.putString("daysAbsent", daysTextView.getText().toString());

        editor.putInt("membersInFamilyAsInt", Integer.valueOf(memberTextView.getText().toString()));
        editor.putInt("daysAbsentAsInt", Integer.valueOf(daysTextView.getText().toString()));

        editor.apply();
    }

    public void toMain(View view) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }
}
