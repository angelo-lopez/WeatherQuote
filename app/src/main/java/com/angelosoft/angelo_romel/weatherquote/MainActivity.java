package com.angelosoft.angelo_romel.weatherquote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, FavLocationActivity.class);
        startActivity(intent);
        finish();
    }

}
