package com.example.diegollams.dummyfacedetection.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.diegollams.dummyfacedetection.R;

import helpers.BitmapTrasformer;

public class CompareImageActivity extends AppCompatActivity {

    private static final int RIGHT_IMAGE_CAPTURE = 42;
    private static final int LEFT_IMAGE_CAPTURE = 43;
    private ImageView rightImage;
    private ImageView rightHistogram;
    private ImageView leftImage;
    private ImageView leftHistogram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rightImage = (ImageView) findViewById(R.id.right_image);
        rightHistogram = (ImageView) findViewById(R.id.right_histogram);

        leftImage = (ImageView) findViewById(R.id.left_image);
        leftHistogram = (ImageView) findViewById(R.id.left_histogram);

        final FloatingActionButton rightActionButton = (FloatingActionButton) findViewById(R.id.right_capture);
        rightActionButton.setOnClickListener(rightCaptureClickListener);

        final FloatingActionButton leftActionButton = (FloatingActionButton) findViewById(R.id.left_capture);
        leftActionButton.setOnClickListener(leftCaptureClickListener);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RIGHT_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            setBitmaps(photo, rightImage, rightHistogram);

        }
        else if(requestCode == LEFT_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
           setBitmaps(photo, leftImage, leftHistogram);
        }
    }

    private void setBitmaps(Bitmap photo, ImageView originalImageView, ImageView histogramImageView) {
        Bitmap histogram = BitmapTrasformer.histogramAllChannels(photo);
        originalImageView.setImageBitmap(photo);
        histogramImageView.setImageBitmap(histogram);
    }

    View.OnClickListener rightCaptureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, RIGHT_IMAGE_CAPTURE);
            }
        }
    };
    View.OnClickListener leftCaptureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, LEFT_IMAGE_CAPTURE);
            }
        }
    };

}
