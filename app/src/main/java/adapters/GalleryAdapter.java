package adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.diegollams.dummyfacedetection.R;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by diegollams on 5/27/16.
 */
public class GalleryAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Bitmap> imagesBitmap;

    public GalleryAdapter(Context context, ArrayList<Bitmap> imagesUris) {
        this.context = context;
        this.imagesBitmap = imagesUris;
    }

    @Override
    public int getCount() {
        return this.imagesBitmap.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        if(convertView == null){
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            imageView.setBackgroundResource(R.drawable.image_border);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else{
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(imagesBitmap.get(position));
        return imageView;
    }
}
