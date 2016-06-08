package helpers;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

/**
 * Created by diegollams on 6/7/16.
 */
public class BitmapHistogramer {
    private static final int MAX_PIXEL_VALUE = 255;

    private List<Integer> redHistogram;
    private List<Integer> blueHistogram;
    private List<Integer> greenHistogram;
    private int pixelCount;
    private boolean working;

    public boolean isWorking() {
        return working;
    }

    public List<Integer> getRedHistogram() {
        return redHistogram;
    }

    public List<Integer> getBlueHistogram() {
        return blueHistogram;
    }

    public List<Integer> getGreenHistogram() {
        return greenHistogram;
    }

    public int getPixelCount() {
        return pixelCount;
    }

    public void setBitmap(Bitmap image){
        this.redHistogram = getRedHistogram(image);
        this.blueHistogram = getBlueHistogram(image);
        this.greenHistogram = getGreenHistogram(image);
        this.pixelCount = image.getHeight() * image.getHeight();
        this.working = true;
    }

    public BitmapHistogramer() {
        this.working = false;
    }

    public BitmapHistogramer(Bitmap image) {
        setBitmap(image);
    }

    public float compareHistograms(BitmapHistogramer histogramer){
        int[] distances = getDistances(this, histogramer);
        int distance = 0;
        for (int i = 0; i < 3; i++) {
            distance += (distances[i] * 100) / pixelCount;
        }
        return distance / 3;
    }

    private static int[] getDistances(BitmapHistogramer first, BitmapHistogramer second){
        int[] distances = new int[3];
        List<Integer> redsFirst = first.getRedHistogram();
        List<Integer> bluesFirst = first.getBlueHistogram();
        List<Integer> greenFirst = first.getGreenHistogram();

        List<Integer> redsSecond = second.getRedHistogram();
        List<Integer> bluesSecond = second.getBlueHistogram();
        List<Integer> greenSecond= second.getGreenHistogram();

        for (int i = 0; i < MAX_PIXEL_VALUE + 1; i++) {
            distances[0] += Math.abs(redsFirst.get(i) - redsSecond.get(i));
            distances[1] += Math.abs(bluesFirst.get(i) - bluesSecond.get(i));
            distances[2] += Math.abs(greenFirst.get(i) - greenSecond.get(i));
        }
        return distances;
    }


    public int[] getRed8Segment(){
        return get8SegmentCount(redHistogram);
    }

    public int[] getBlue8Segment(){
        return get8SegmentCount(blueHistogram);
    }

    public int[] getGreen8Segment(){
        return get8SegmentCount(greenHistogram);
    }

    public Bitmap getHistogramBitmap(){
        Bitmap histogram = Bitmap.createBitmap(100, MAX_PIXEL_VALUE * 3, Bitmap.Config.ARGB_8888);
        for (int y = 0; y < MAX_PIXEL_VALUE; y++) {
            for(int x = 0; x < redHistogram.get(y) * 100 / Collections.max(redHistogram); x++) {
               histogram.setPixel(x, y, Color.RED);
            }
            for(int x = 0; x < blueHistogram.get(y) * 100 / Collections.max(blueHistogram); x++) {
                histogram.setPixel(x, y + MAX_PIXEL_VALUE, Color.BLUE);
            }
            for(int x = 0; x < greenHistogram.get(y) * 100 / Collections.max(greenHistogram); x++) {
                histogram.setPixel(x, y + (MAX_PIXEL_VALUE * 2), Color.GREEN);
            }
        }
        return histogram;
    }

//TODO change name
    public static List<Integer> getRedHistogram(Bitmap image){
        return getHistogram(image, 16);
    }
    public static List<Integer> getBlueHistogram(Bitmap image){
        return getHistogram(image, 0);
    }
    public static List<Integer> getGreenHistogram(Bitmap image){
        return getHistogram(image, 8);
    }

    private int[] get8SegmentCount(List<Integer> histogram){
        int[] segments = new int[8];
        if(histogram.size() < MAX_PIXEL_VALUE) {
            return segments;
        }
        for (int i = 0; i < MAX_PIXEL_VALUE; i++) {
            int segment;
            if(i < 32) segment = 0;
            else if(i < 64) segment = 1;
            else if(i < 96) segment = 2;
            else if(i < 128) segment = 3;
            else if(i < 160) segment = 4;
            else if(i < 192) segment = 5;
            else if(i < 224) segment = 6;
            else segment = 7;
            segments[segment] += histogram.get(i);
        }
        return segments;
    }

    private static List<Integer> getHistogram(Bitmap image, int mask){
        List<Integer> count = new ArrayList<>(Collections.nCopies(MAX_PIXEL_VALUE + 1, 0));
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getPixel(x, y);
                int position = pixel >> mask & 0xFF;
                int value = count.get(position);
                count.set(position, value + 1);
            }
        }
        return count;
    }

}
