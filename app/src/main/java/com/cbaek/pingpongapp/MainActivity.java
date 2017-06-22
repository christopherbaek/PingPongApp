package com.cbaek.pingpongapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up toggle button
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggle_button);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(MainActivity.this, "Starting service", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Stopping service", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

}
