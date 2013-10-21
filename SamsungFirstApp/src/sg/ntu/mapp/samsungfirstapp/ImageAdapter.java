package sg.ntu.mapp.samsungfirstapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
 
public class ImageAdapter extends BaseAdapter {
	private Context context;
	private final Bitmap[] imgs;
	protected ImageLoader imageLoader;
	private String[] urls = new String[3];
 
	public ImageAdapter(Context context, Bitmap[] imgs) {
		this.context = context;
		this.imgs = imgs;
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
		ImageLoader.getInstance().init(config);
		imageLoader = ImageLoader.getInstance();
		
		urls[0]=context.getResources().getString(R.string.url0);
		urls[1]=context.getResources().getString(R.string.url1);
		urls[2]=context.getResources().getString(R.string.url2);
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
 
		if (convertView == null) {
 
			gridView = new View(context);
 
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.my_image, null);
 
			// set image
			ImageView imageView = (ImageView) gridView.findViewById(R.id.edit_photo);
			Bitmap b = imgs[position];
			imageView.setImageBitmap(b);
			if (position<3){
				imageLoader.displayImage(urls[position], imageView);
				imageView.setBackgroundDrawable(null);
			} 
 
		} else {
			gridView = (View) convertView;
		}
 
		return gridView;
	}
 
	@Override
	public int getCount() {
		return 9;
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
 
}