package com.example.diegollams.dummyfacedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.diegollams.dummyfacedetection.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button eigenButton = (Button) findViewById(R.id.eigen_button);
        eigenButton.setOnClickListener(eigenClickListener);
        final Button detectionButton = (Button) findViewById(R.id.detection_button);
        detectionButton.setOnClickListener(detectClickListener);
        final Button compareButton = (Button) findViewById(R.id.compare_button);
        compareButton.setOnClickListener(compareClickListener);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener eigenClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent eigenIntent = new Intent(MainActivity.this, EigenGalleryActivity.class);
            startActivity(eigenIntent);

        }
    };

    View.OnClickListener detectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent detectIntent = new Intent(MainActivity.this, DetectActivity.class);
            startActivity(detectIntent);
        }
    };
    View.OnClickListener compareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent compareIntent = new Intent(MainActivity.this, CompareImageActivity.class);
            startActivity(compareIntent);
        }
    };
}
