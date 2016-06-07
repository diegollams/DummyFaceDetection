package com.example.diegollams.dummyfacedetection.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.example.diegollams.dummyfacedetection.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import adapters.GalleryAdapter;
import helpers.BitmapTrasformer;

public class EigenGalleryActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String SUBFIX_IMAGE_NAME = "EIGEN_";
    public static final int REQ_SIZE = 500;
    public static final String EIGEN_FILE_NAME = "MASTER";
    private ArrayList<Bitmap> imagesBitmaps;
    private GalleryAdapter galleryAdapter;
    private GridView imagesGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eigen_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagesBitmaps = new ArrayList<>();


        this.imagesGridView = (GridView) findViewById(R.id.gallery_grid_view);
        galleryAdapter = new GalleryAdapter(this,this.imagesBitmaps);
        this.imagesGridView.setAdapter(galleryAdapter);
        loadGalleryArray();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.capture);
        fab.setOnClickListener(fabClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.eigen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_calculate_eigen) {
            new EigenCalculation().execute(imagesBitmaps);
            return true;
        }
        else if (id == R.id.action_clean_gallery) {
            cleanGallery();
            return true;
        }
        else if (id == R.id.action_load_eigen_images) {
            loadEigenArray();
            return true;
        }
        else if (id == R.id.action_load_gallery_images) {
            loadGalleryArray();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private File[] getImagesFiles(){
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
        return path.listFiles();
    }

    private void cleanGallery(){
        imagesBitmaps.clear();
        for (File file : getImagesFiles()) {
            file.delete();
        }
        galleryAdapter.notifyDataSetChanged();
    }

    private void loadGalleryArray(){
        imagesBitmaps.clear();
        for (File file : getImagesFiles()) {
            if (file.getName().contains(SUBFIX_IMAGE_NAME)) {
                Bitmap bitmap = null;
                bitmap = BitmapTrasformer.decodeSampledBitmap(file, REQ_SIZE, REQ_SIZE);
                imagesBitmaps.add(bitmap);
            }
        }
        galleryAdapter.notifyDataSetChanged();
    }

    private void loadEigenArray(){
        imagesBitmaps.clear();
        for (File file : getImagesFiles()) {
            if (file.getName().contains(EIGEN_FILE_NAME)) {
                Bitmap bitmap = null;
                bitmap = BitmapTrasformer.decodeSampledBitmap(file, REQ_SIZE, REQ_SIZE);
                imagesBitmaps.add(bitmap);
            }
        }
        galleryAdapter.notifyDataSetChanged();
    }


    private  File createImageFile(String imageFileName) throws IOException {
        // Create an image file name

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
    private File createGalleryFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = SUBFIX_IMAGE_NAME + timeStamp;
        try {
            return createImageFile(imageFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File createEigenFile()  {
        try {
            return createImageFile(EIGEN_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            photo = photo.copy(Bitmap.Config.ARGB_8888, true);
            BitmapTrasformer.grayScale(photo);
            photo = BitmapTrasformer.scaleBitmap(photo, REQ_SIZE, REQ_SIZE);
            addBitmapToGallery(photo);


        }
    }

    private void addBitmapToGallery(Bitmap photo) {
        createJPGImage(photo,createGalleryFile());
        galleryAdapter.notifyDataSetChanged();
        imagesBitmaps.add(photo);
    }

    private void createJPGImage(Bitmap photo,File photoFile) {
        try {
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    };

    private class EigenCalculation extends AsyncTask<ArrayList<Bitmap>, Void, Bitmap>{
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            createJPGImage(bitmap,createEigenFile());
            loadEigenArray();
            Toast.makeText(getApplicationContext(), "Termino", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Bitmap doInBackground(ArrayList<Bitmap>... params) {
            return BitmapTrasformer.calculateEigenFace(params[0],REQ_SIZE);
        }
    }

}
