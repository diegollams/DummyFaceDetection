package com.example.diegollams.dummyfacedetection.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.example.diegollams.dummyfacedetection.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import adapters.GalleryAdapter;
import helpers.BitmapTrasformer;

public class EigenGalleryActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String SUBFIX_IMAGE_NAME = "EIGEN_";
    private ArrayList<Bitmap> imagesBitmaps;
    private GridView imagesGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eigen_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagesBitmaps = new ArrayList<>();
        loadImageArray();

        this.imagesGridView = (GridView) findViewById(R.id.gallery_grid_view);
        this.imagesGridView.setAdapter(new GalleryAdapter(this,this.imagesBitmaps));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClickListener);
    }

    private void loadImageArray(){
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
        File[] filesInDirectory = path.listFiles();
        imagesBitmaps.clear();
        for (File file : filesInDirectory) {
            if (file.getName().contains(SUBFIX_IMAGE_NAME)) {
                Bitmap bitmap = null;
                bitmap = BitmapTrasformer.decodeSampledBitmap(file, 100, 100);
                imagesBitmaps.add(bitmap);
            }
        }
        Log.e("shit", imagesBitmaps.toString());
    }

    private  File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = SUBFIX_IMAGE_NAME + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            loadImageArray();
        }
    }

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photo = null;
                try {
                    photo = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(v, "Error", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                if(photo != null){
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    };

}
