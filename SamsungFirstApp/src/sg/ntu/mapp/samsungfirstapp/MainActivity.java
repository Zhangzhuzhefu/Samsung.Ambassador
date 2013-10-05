package sg.ntu.mapp.samsungfirstapp;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("NewApi") 
public class MainActivity extends Activity { 
	protected static final int RESULT_LOAD_IMAGE = 131891;
	private Context mContext;
	private String[] urls = new String[3];
	private Button button_add;
	private ImageView[] imageViews = new ImageView[9];
	private int spotPostion;
	private Bitmap[] bitmapImages = new Bitmap[9];
	private ProgressDialog pd;
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageViews[spotPostion].setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageViews[spotPostion].setBackground(null);
            spotPostion++;
        }
    }
	
	OnClickListener fetchFromCameraListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (spotPostion<9){
				// find the first empty position and load picture from gallery
				Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			} else {
				Toast.makeText(mContext, "Maximum reached",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	OnClickListener editPhotoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(mContext, "Editing Activity. To be implemented...",
					Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		spotPostion = 0;
		pd = new ProgressDialog(this);
		
		setContentView(R.layout.activity_main);
		
		urls[0]=mContext.getResources().getString(R.string.url0);
		urls[1]=mContext.getResources().getString(R.string.url1);
		urls[2]=mContext.getResources().getString(R.string.url2);
		
		button_add = (Button) findViewById(R.id.button_add);
		button_add.setOnClickListener(fetchFromCameraListener);
		
		imageViews[0] = (ImageView) findViewById(R.id.btn_add_photo1);
		imageViews[1] = (ImageView) findViewById(R.id.btn_add_photo2);
		imageViews[2] = (ImageView) findViewById(R.id.btn_add_photo3);
		imageViews[3] = (ImageView) findViewById(R.id.btn_add_photo4);
		imageViews[4] = (ImageView) findViewById(R.id.btn_add_photo5);
		imageViews[5] = (ImageView) findViewById(R.id.btn_add_photo6);
		imageViews[6] = (ImageView) findViewById(R.id.btn_add_photo7);
		imageViews[7] = (ImageView) findViewById(R.id.btn_add_photo8);
		imageViews[8] = (ImageView) findViewById(R.id.btn_add_photo9);
		for (int i=0;i<9;i++){
			imageViews[i].setOnClickListener(editPhotoListener);
		}
		
		new TheTask().execute();    
	}
	
	class TheTask extends AsyncTask<Void,Void,Void>
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.setMessage("Loading..");
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try
			{
				for (int i=0;i<3;i++)
					bitmapImages[i] = downloadBitmap(urls[i]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			if(bitmapImages!=null)
			{
				for (int i=0;i<3;i++){
					imageViews[i].setImageBitmap(bitmapImages[i]);
					imageViews[i].setBackground(null);
					spotPostion++;
				}
			}

		}   
	}
	private Bitmap downloadBitmap(String url) {
		Bitmap bitmapImage = null;
		// initilize the default HTTP client object
		final DefaultHttpClient client = new DefaultHttpClient();

		//forming a HttoGet request 
		final HttpGet getRequest = new HttpGet(url);
		try {

			HttpResponse response = client.execute(getRequest);

			//check 200 OK for success
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode + 
						" while retrieving bitmap from " + url);
				return null;

			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					// getting contents from the stream 
					inputStream = entity.getContent();

					// decoding stream data back into image Bitmap that android understands
						bitmapImage = BitmapFactory.decodeStream(inputStream);


				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// You Could provide a more explicit error message for IOException
			getRequest.abort();
			Log.e("ImageDownloader", "Something went wrong while" +
					" retrieving bitmap from " + url + e.toString());
		} 

		return bitmapImage;
	}
	
	public void clearAll(View v){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		
			// set title
			alertDialogBuilder.setTitle("Clear All");
			
			// set dialog message
			alertDialogBuilder
				.setMessage("Are you sure to clear all images loaded from gallery?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, clear all the images loaded from camera
						for (int i=3;i<spotPostion;i++) {
							imageViews[i].setBackground(getResources().getDrawable(R.drawable.defaul_image));
							imageViews[i].setImageBitmap(null);
						}
						spotPostion = 3;
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just the dialog box and do nothing
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
			}
	
}