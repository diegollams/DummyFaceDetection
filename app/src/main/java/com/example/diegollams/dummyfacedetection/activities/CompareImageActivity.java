package com.example.diegollams.dummyfacedetection.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diegollams.dummyfacedetection.R;

import helpers.BitmapHistogramer;
import helpers.BitmapTrasformer;

public class CompareImageActivity extends AppCompatActivity {

    private static final int RIGHT_IMAGE_CAPTURE = 42;
    private static final int LEFT_IMAGE_CAPTURE = 43;
    private ImageView rightImage;
    private ImageView rightHistogramImageView;
    private ImageView leftImage;
    private ImageView leftHistogramImageView;
    private TextView infoTextView;
    private BitmapHistogramer rightHistogramer = null;
    private BitmapHistogramer leftHistogramer = null;
    private Button compareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rightImage = (ImageView) findViewById(R.id.right_image);
        rightHistogramImageView = (ImageView) findViewById(R.id.right_histogram);

        leftImage = (ImageView) findViewById(R.id.left_image);
        leftHistogramImageView = (ImageView) findViewById(R.id.left_histogram);

        final FloatingActionButton rightActionButton = (FloatingActionButton) findViewById(R.id.right_capture);
        rightActionButton.setOnClickListener(rightCaptureClickListener);

        final FloatingActionButton leftActionButton = (FloatingActionButton) findViewById(R.id.left_capture);
        leftActionButton.setOnClickListener(leftCaptureClickListener);

        leftHistogramer = new BitmapHistogramer();
        rightHistogramer = new BitmapHistogramer();


        compareButton = (Button) findViewById(R.id.compare_button);
        compareButton.setOnClickListener(compareClickListener);
        infoTextView = (TextView) findViewById(R.id.result_textView);


        buttonCheckEnable();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RIGHT_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            setBitmaps(photo, rightImage, rightHistogramImageView, rightHistogramer);
            buttonCheckEnable();
        }
        else if(requestCode == LEFT_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
           setBitmaps(photo, leftImage, leftHistogramImageView,leftHistogramer);
            buttonCheckEnable();
        }
    }

    private void buttonCheckEnable() {
        boolean equalSizes = leftHistogramer.getPixelCount() == rightHistogramer.getPixelCount();
        boolean histogramsReady = (leftHistogramer.isWorking() && rightHistogramer.isWorking());
        if(histogramsReady && equalSizes){
            compareButton.setEnabled(true);
            compareButton.setText(R.string.compare_string);
            infoTextView.setText(R.string.compare_string);
        }else{
            compareButton.setEnabled(false);
            if(!histogramsReady){
                infoTextView.setText(R.string.missing_image_string);
            }
            else if(!equalSizes){
                infoTextView.setText(R.string.diferent_sizes_string);
            }else{
                infoTextView.setText(R.string.invalid_images_string);
            }
        }

    }

    private void setBitmaps(Bitmap photo, ImageView originalImageView, ImageView histogramImageView,BitmapHistogramer histogram) {
        histogram.setBitmap(photo);
        originalImageView.setImageBitmap(photo);
        histogramImageView.setImageBitmap(histogram.getHistogramBitmap());
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

    View.OnClickListener compareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float diference = rightHistogramer.compareHistograms(leftHistogramer);
            Toast.makeText(CompareImageActivity.this, "" + diference, Toast.LENGTH_SHORT).show();
            infoTextView.setText(Float.toString(diference));
        }
    };

}
