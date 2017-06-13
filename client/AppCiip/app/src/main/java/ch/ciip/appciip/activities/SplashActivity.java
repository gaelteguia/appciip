package ch.ciip.appciip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ch.ciip.appciip.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread background = new Thread() {
            public void run() {

                try {

                    sleep(2 * 1000);

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {

                }
            }
        };


        background.start();


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

}