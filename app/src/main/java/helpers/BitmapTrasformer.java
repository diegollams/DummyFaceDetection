package helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by diegollams on 2/6/16.
 */
public class BitmapTrasformer {

	public static final int HALF_PIXEL_VALUE = 175;
	public static final int MAX_PIXEL_VALUE = 255;
	public static final int MIN_PIXEL_VALUE = 0;
	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

    public static Bitmap decodeSampledBitmap(File res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res.getPath(), options);


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(res.getPath(), options);

    }
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
														 int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);


		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}


	public static Bitmap getBitmapFromUri(Uri uri,int reqWidth, int reqHeight){
		File file = new File(uri.getPath());
		return BitmapFactory.decodeFile(file.getAbsolutePath(), getOptions(reqWidth, reqHeight));
	}

	/**
	 *
	 * @param reqWidth
	 * @param reqHeight
	 * @return {@link BitmapFactory.Options } object with mutable
	 */
	@NonNull
	public static BitmapFactory.Options getOptions(int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
		options.inMutable  = true;
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return options;
	}

	/**
	 * Method that decode a bitmap form a byte[] with dimension that fit reqHeight and reqWidth
	 * @param data the byte array the bitmap will be create
	 * @param reqWidth the max value of width that the bitmap can be
	 * @param reqHeight the max value of height that the bitmap can be
	 * @return a bitmap with dimension that fits the reqWidth and reqHeight
	 */
	public static Bitmap decodeSampledBitmapFromData(byte[] data
														 ,int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeByteArray(data, 0, data.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inMutable = true;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	/**
	 *
	 * @param value a value of a pixel to be evaluate
	 * @return
	 */
	private static int boundPixelValue(int value){
		if(value >= MAX_PIXEL_VALUE){
			return MAX_PIXEL_VALUE-2;
		}
		else if(value < MIN_PIXEL_VALUE){
			return MIN_PIXEL_VALUE;
		}
		else{
			return value;
		}

	}
    public static Bitmap scaleBitmap(Bitmap bitmap, int reqWidth, int reqHeight){
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, reqWidth, reqHeight), Matrix.ScaleToFit.FILL);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

	/**
	 * will transform bitmap so every pixel will be in grayscale form
	 * @param bitmap a bitmap will all channels in the same value
	 */
	public static void grayScale(Bitmap bitmap){
		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
				int pixel = bitmap.getPixel(x,y);
				bitmap.setPixel(x, y,RGBHelper.getBetterGrayScaleColor(pixel));
			}
		}
	}

	/**
	 * will transform the bitmap so every pixel will be inverse, first will get the max value in the image for every  color channel
	 * then apply {@link #inverseMethod(Bitmap, int, int, int)} with the max value of every channel
	 * @param bitmap the bitmap that will be inverse
	 */
	@Deprecated
	public static void inverseMaxCanal(Bitmap bitmap){
		int maxRed = 0,maxGreen = 0,maxBlue = 0;
		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
				RGBHelper pixel = new RGBHelper(bitmap.getPixel(x,y));
				if(pixel.getRed() > maxRed) maxRed = pixel.getRed();
				if(pixel.getGreen() > maxGreen) maxGreen= pixel.getGreen();
				if(pixel.getBlue() > maxBlue) maxBlue = pixel.getBlue();
			}
		}
		inverseMethod(bitmap, maxRed, maxGreen, maxBlue);
	}

	/**
	 * get the max channel value and substract every pixel channel so we get the inverse of every pixel
	 * @param bitmap the bitmap that will be inverse
	 * @param maxRed value that the red channel of every pixel will be substract
	 * @param maxGreen value that the green channel of every pixel will be substract
	 * @param maxBlue value that the green channel of every pixel will be substract
	 */
	private static void inverseMethod(Bitmap bitmap, int maxRed, int maxGreen, int maxBlue) {
		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
				RGBHelper pixel = new RGBHelper(bitmap.getPixel(x,y));
				int newPixel = RGBHelper.createPixel(maxRed - pixel.getRed(), maxGreen - pixel.getGreen(), maxBlue - pixel.getBlue(), pixel.getAlpha());
				bitmap.setPixel(x,y,newPixel);
			}
		}
	}

	/**
	 * will transform the bitmap so every pixel will be inverse,
	 * then apply {@link #inverseMethod(Bitmap, int, int, int)} with the {@value #MAX_PIXEL_VALUE} for  every channel
	 * @param bitmap the bitmap that will be covert
	 */
	public static void inverseNormal(Bitmap bitmap){
		inverseMethod(bitmap,MAX_PIXEL_VALUE,MAX_PIXEL_VALUE,MAX_PIXEL_VALUE);
	}

	/**
	 * transform bitmap to black and white pixels
	 * @param bitmap the bitmap that will be covert
	 */
	public static void binarization(Bitmap bitmap){
		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
				int grayScalePixel = RGBHelper.getBetterGrayScaleColor(bitmap.getPixel(x, y));
				if(RGBHelper.getRed(grayScalePixel) > HALF_PIXEL_VALUE){
					bitmap.setPixel(x,y,RGBHelper.createGrayPixel(MAX_PIXEL_VALUE));
				}
				else{
					bitmap.setPixel(x,y,RGBHelper.createGrayPixel(MIN_PIXEL_VALUE));
				}
			}
		}
	}

	/**
	 *
	 * @param bitmap
	 * @param brightness
	 */
	public static void changeBrightness(Bitmap bitmap,int brightness){
		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
//				RGBHelper pixel = new RGBHelper(bitmap.getPixel(x, y));
				int pixel  = bitmap.getPixel(x,y);
				int red  = boundPixelValue(RGBHelper.getRed(pixel) + brightness);
				int green = boundPixelValue(RGBHelper.getGreen(pixel) + brightness);
				int blue = boundPixelValue(RGBHelper.getBlue(pixel) + brightness);
				bitmap.setPixel(x,y,RGBHelper.createPixel(red,green,blue,RGBHelper.getAlpha(pixel)));
			}
		}
	}

	/**
	 *
	 * @param bitmap
	 * @param contrast
	 */
	public static void changeContrast(@NonNull Bitmap bitmap,float contrast){
		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
//				RGBHelper pixel = new RGBHelper(bitmap.getPixel(x, y));
				int pixel  = bitmap.getPixel(x,y);
				int red  = boundPixelValue((int) (RGBHelper.getRed(pixel) * contrast));
				int green = boundPixelValue((int) (RGBHelper.getGreen(pixel) * contrast));
				int blue = boundPixelValue((int) (RGBHelper.getBlue(pixel) * contrast));
				bitmap.setPixel(x,y,RGBHelper.createPixel(red,green,blue,RGBHelper.getAlpha(pixel)));
			}
		}
	}

	/**
	 *
	 * @param bitmap
	 * @param contrast
	 */
	public static void changeContrastBetter(@NonNull Bitmap bitmap,int contrast){
		final float newContrast = (259 * (contrast + 255)) / (255 * (259 - contrast));
		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
//				RGBHelper pixel = new RGBHelper(bitmap.getPixel(x, y));
				int pixel  = bitmap.getPixel(x,y);
				int red  = boundPixelValue((int) ((RGBHelper.getRed(pixel) * newContrast)-128) + 128);
				int green = boundPixelValue((int)((RGBHelper.getGreen(pixel) * newContrast)-128) + 128);
				int blue = boundPixelValue((int) ((RGBHelper.getBlue(pixel) * newContrast) - 128) + 128);
				bitmap.setPixel(x,y,RGBHelper.createPixel(red,green,blue,RGBHelper.getAlpha(pixel)));
			}
		}
	}

	/**
	 *
	 * @param original
	 * @return
	 */
	public static Bitmap histogram(@NonNull Bitmap original){
		HashMap<Integer,Integer> values = new HashMap<>();
		int maxValue = 0;
		for (int x = 0; x < original.getWidth(); x++) {
			for (int y = 0; y < original.getHeight(); y++) {
//				get arbitrary pixel image should be in grayscale
				int pixel = RGBHelper.getRed(original.getPixel(x, y));
				Integer count = values.get(pixel);
				if(count == null) {
					count = 0;
				}
				count++;
				if(count > maxValue){
					maxValue = count;
				}
				values.put(pixel,count);
			}
		}
		Log.e("shit","" + maxValue);
		Bitmap histogram = Bitmap.createBitmap(MAX_PIXEL_VALUE, 100, Bitmap.Config.ARGB_8888);
		for (Map.Entry<Integer, Integer> x :values.entrySet()) {
			Log.e("shit"," "+ x.getValue()* 100 / maxValue);
			for (int y = 0; y < x.getValue()* 100 / maxValue; y++) {
				histogram.setPixel(x.getKey(),y, RGBHelper.BLUE);
			}
		}

		return histogram;

	}

	/**
	 *
	 * @param original
	 * @return
	 */
	public static Bitmap histogramAllChannels(@NonNull Bitmap original){
		int[] redCounts = new int[MAX_PIXEL_VALUE + 1],greenCounts = new int[MAX_PIXEL_VALUE + 1],blueCounts = new int[MAX_PIXEL_VALUE + 1];
		int maxRed = 0,maxBlue = 0,maxGreen = 0;
		for (int x = 0; x < original.getWidth(); x++) {
			for (int y = 0; y < original.getHeight(); y++) {
				int pixel = original.getPixel(x,y);
				int red = RGBHelper.getRed(pixel);
				int green = RGBHelper.getGreen(pixel);
				int blue = RGBHelper.getBlue(pixel);
				maxRed = Math.max(++redCounts[red ],maxRed);
				maxBlue = Math.max(++blueCounts[blue],maxBlue);
				maxGreen = Math.max(++greenCounts[green],maxGreen);
			}
		}
		Bitmap histogram = Bitmap.createBitmap(MAX_PIXEL_VALUE * 3, 100, Bitmap.Config.ARGB_8888);
		for (int x = 0; x < MAX_PIXEL_VALUE; x++) {
			for (int y = 0; y < (redCounts[x] *100) /maxRed; y++) {
				histogram.setPixel(x,y, Color.RED);
			}
			for (int y = 0; y < (blueCounts[x] *100) /maxBlue; y++) {
				histogram.setPixel(x + MAX_PIXEL_VALUE,y, Color.BLUE);
			}
			for (int y = 0 * 2; y < (greenCounts[x] *100) /maxGreen; y++) {
				histogram.setPixel(x + MAX_PIXEL_VALUE * 2 ,y, Color.GREEN);
			}
		}
		return histogram;
	}

	/**
	 *
	 * @param original
	 */
	public static void EQ(Bitmap original){
		int  max = 0,min = 0;
//		grayScale(original);
		for (int x = 0; x < original.getWidth(); x++) {
			for (int y = 0; y < original.getHeight(); y++) {
				int pixel = RGBHelper.getGrayScaleColor(original.getPixel(x, y));
//				original.setPixel(x,y,pixel);
				max = Math.max(RGBHelper.getRed(pixel), max);
				min  = Math.min(RGBHelper.getRed(pixel), min);
			}
		}
		Log.e("shit",max  +" "+min);
		int f = (MAX_PIXEL_VALUE - MIN_PIXEL_VALUE) / (max - min);
		for (int x = 0; x < original.getWidth(); x++) {
			for (int y = 0; y < original.getHeight(); y++) {
				int pixel = RGBHelper.getRed(original.getPixel(x, y));
				int newPixel = boundPixelValue((pixel - min) * f);
				int color =Color.rgb(newPixel, newPixel, newPixel);
				original.setPixel(x,y,color);
			}
		}
	}

	public static void stadisticEQ(@NonNull Bitmap original) {


		int[] counts = new int[MAX_PIXEL_VALUE + 1];
		float[] normalizeCount = new float[MAX_PIXEL_VALUE + 1];
		final int total = original.getWidth() * original.getHeight();
		for (int x = 0; x < original.getWidth(); x++) {
			for (int y = 0; y < original.getHeight(); y++) {
				int pixel = RGBHelper.getGrayScaleColor(original.getPixel(x, y));
				pixel = RGBHelper.getRed(pixel);
				++counts[pixel];

			}
		}
		for (int i = 0; i < MAX_PIXEL_VALUE + 1; i++) {
			normalizeCount[i] = (float)counts[i] / (float)total;
		}
		float[] normalizeSum = new float[MAX_PIXEL_VALUE + 1];
		normalizeSum[0] = normalizeCount[0];
		for (int i = 1; i < MAX_PIXEL_VALUE + 1; i++) {
			normalizeSum[i] += normalizeCount[i-1];
		}
		normalizeSum[MAX_PIXEL_VALUE] = 1;
		for (int x = 0; x < original.getWidth(); x++) {
			for (int y = 0; y < original.getHeight(); y++) {

			}
		}
	}

	/**
	 * Warning only work with positive values
	 * @param bitmap
	 * @param xOffset number for pixel to traslate the bitmap in x axis
	 * @param yOffset number for pixel to traslate the bitmap in y axis
	 */
	public static void translate(Bitmap bitmap,int xOffset, int yOffset){
		if(xOffset < 0 || xOffset > bitmap.getWidth() || yOffset <0 || yOffset >bitmap.getHeight()){
			return;
		}
		for (int x = bitmap.getWidth() -1; x >= 0; x--) {
			for (int y = bitmap.getHeight() -1; y >= 0 ; y--) {
				int newX = x + xOffset,newY = y + yOffset;
				if(newX < bitmap.getWidth() && newY < bitmap.getHeight()){
					int currentPixel = bitmap.getPixel(x , y);
					bitmap.setPixel(x + xOffset,y + yOffset,currentPixel);
				}
				if(x <= xOffset || y <= yOffset){
					bitmap.setPixel(x,y,Color.WHITE);
				}
			}
		}
	}
	public static Bitmap rotateImage(Bitmap originalBitmap,int degrees){
		int width = originalBitmap.getWidth(),heigth = originalBitmap.getHeight();
		Bitmap rotatedBitmap = Bitmap.createBitmap(width, heigth, Bitmap.Config.ARGB_8888);
		int x0 = (width -1 ) /2,y0  = (heigth -1) /2;
		 double radians=  Math.toRadians(degrees);
		for (int x = 0; x < originalBitmap.getWidth(); x++) {
			for (int y = 0; y < originalBitmap.getHeight(); y++) {
				int newX = (int)( (x - x0) * Math.cos(radians) - (y - y0)  * Math.sin(radians) + x0);
				int newY = (int)( (x - x0) * Math.sin(radians) + (y - y0) *  Math.cos(radians) + y0);
				if(newX< 0 || newY < 0 || newX >=rotatedBitmap.getWidth() || newY >= rotatedBitmap.getHeight()){
					continue;
				}
				rotatedBitmap.setPixel(newX,newY,originalBitmap.getPixel(x,y));
			}
		}
		return rotatedBitmap;
	}

	public static Bitmap nearestNeighborInterpolation(Bitmap  originalBitmap, int scaleFactor){
		int width = originalBitmap.getWidth(),heigth = originalBitmap.getHeight() ;
		Bitmap zoomImage = Bitmap.createBitmap(width, heigth, Bitmap.Config.ARGB_8888);
		for (int x = 0; x < (originalBitmap.getWidth() / scaleFactor) - scaleFactor; x += scaleFactor) {
			for (int y = 0; y < (originalBitmap.getHeight() / scaleFactor) - scaleFactor; y += scaleFactor) {
				int pixel = originalBitmap.getPixel(x,y);
				for (int localX = x * scaleFactor; localX <= (x * scaleFactor)  + scaleFactor; localX++) {
					for (int localY = y * scaleFactor; localY <= ( y  * scaleFactor) + scaleFactor; localY++) {
						zoomImage.setPixel(localX,localY,pixel);
					}
				}
			}
		}
		return zoomImage;
	}

	public static Bitmap inclinate(Bitmap originalBitmap, int degrees){
		int width = originalBitmap.getWidth(),heigth = originalBitmap.getHeight();
		Bitmap rotatedBitmap = Bitmap.createBitmap(width, heigth, Bitmap.Config.ARGB_8888);
		for (int x = 0; x < originalBitmap.getWidth(); x++) {
			for (int y = 0; y < originalBitmap.getHeight(); y++) {
				int newX = (int) (x + (y * 0.5));
				int newY = (int) (y + (x * 0.5));
				if(newX< 0 || newY < 0 || newX >=rotatedBitmap.getWidth() || newY >= rotatedBitmap.getHeight()){
					continue;
				}
				rotatedBitmap.setPixel(newX,newY,originalBitmap.getPixel(x,y));
			}
		}
		return rotatedBitmap;
	}

	public static Bitmap edge(Bitmap image){
		 return applyFilter(image, new int[][]{
				 {0, 1, 0},
				 {1, -4, 1},
				 {0	, 1, 0}
		 });
	}
	public static Bitmap edgeEnhancement(Bitmap image){
		return applyFilter(image, new int[][]{
				{-1, -1, -1},
				{-1, 8, -1},
				{-1, -1, -1}
		});
	}

	public static Bitmap sharpen(Bitmap image){
		return applyFilter(image, new int[][]{
				{0, -1, 0},
				{0, 1, 0},
				{0	, 0, 0}
		});
	}

	public static Bitmap applyFilter(Bitmap image,int[][] mask){
		int width = image.getWidth(),height = image.getHeight();
		Bitmap rotatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int sumatory = 0;
				int[] xy = getValidCordinate(x - 1, y - 1,width,height);
				sumatory += (mask[0][0] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
				xy = getValidCordinate(x ,y - 1,width,height);
				sumatory += (mask[0][1] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
				xy = getValidCordinate(x + 1,y - 1,width,height);
				sumatory += (mask[0][2] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
//
				xy = getValidCordinate(x -1, y ,width,height);
				sumatory += (mask[1][0] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
				xy = getValidCordinate(x , y ,width,height);
				sumatory += (mask[1][1] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
				xy = getValidCordinate(x + 1, y ,width,height);
				sumatory += (mask[1][2] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
//
				xy = getValidCordinate(x -1, y + 1,width,height);
				sumatory += (mask[2][0] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
				xy = getValidCordinate(x , y + 1,width,height);
				sumatory += (mask[2][1] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
				xy = getValidCordinate(x + 1, y + 1 ,width,height);
				sumatory += (mask[2][2] * RGBHelper.getBlue(image.getPixel(xy[0], xy[1])));
				rotatedBitmap.setPixel(x, y, RGBHelper.createGrayPixel(boundPixelValue(sumatory)));
			}
		}
		return rotatedBitmap;
	}

	public static Bitmap calculateEigenFace(ArrayList<Bitmap> imagesBitmaps, int reqSize) {
        Bitmap eigenBitmap = Bitmap.createBitmap(reqSize, reqSize, Bitmap.Config.ARGB_8888);
        int[][] imageValues = new int[500][500];
        eigenBitmap.setHasAlpha(true);
        for (int i = 0; i < imagesBitmaps.size(); i++) {
            for (int x = 0; x < reqSize; x++) {
                for (int y = 0; y < reqSize; y++) {
                    imageValues[x][y] += RGBHelper.getRed(imagesBitmaps.get(i).getPixel(x, y));
                    if(i == imagesBitmaps.size() - 1){
                        int pixel = RGBHelper.createGrayPixel(imageValues[x][y] / imagesBitmaps.size());
                        eigenBitmap.setPixel(x, y, pixel);
                    }
                }
            }
        }
        return eigenBitmap;
    }

	private static int[] getValidCordinate(int x, int y, int width, int height) {
		int[] xy = new int[2];
		xy[0] = x;
		xy[1]= y;
		if(x < 0 ) xy[0] = width -1;
		if(y < 0) xy[1] = height -1;
		if(x >= width) xy[0] = 0;
		if(y >= height) xy[1] = 0;
		return xy;
	}
}
