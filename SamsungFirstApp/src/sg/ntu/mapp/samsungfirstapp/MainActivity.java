package sg.ntu.mapp.samsungfirstapp;

import java.io.ByteArrayOutputStream;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint({ "NewApi" }) 
public class MainActivity extends Activity { 
	protected static final int RESULT_LOAD_IMAGE = 131891;
	protected static final int RESULT_EDIT_IMAGE = 131892;
	private Context mContext;
	private String[] urls = new String[3];
	private GridView gridView;
	//private ImageView[] imageViews = new ImageView[9];
	private int spotPostion;
	private Bitmap[] bitmapImages = new Bitmap[9];
	private ProgressDialog pd;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_CANCELED) {
			if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				bitmapImages[spotPostion] = BitmapFactory.decodeFile(picturePath);
				ViewGroup gridChild = (ViewGroup) gridView.getChildAt(spotPostion);
				((ImageView) gridChild.findViewById(R.id.edit_photo)).setImageBitmap(bitmapImages[spotPostion]);
				((ImageView) gridChild.findViewById(R.id.edit_photo)).setBackgroundDrawable(null);
				spotPostion++;
			} else if (requestCode == RESULT_EDIT_IMAGE && resultCode == RESULT_OK){
				if (data!= null) {
					byte[] imgData = data.getByteArrayExtra("imageData");
					int imgDataID = data.getIntExtra("imageDataID", 0);
					Toast.makeText(mContext, String.valueOf(imgDataID),
							Toast.LENGTH_SHORT).show();

					bitmapImages[imgDataID] = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
					ViewGroup gridChild = (ViewGroup) gridView.getChildAt(imgDataID);
					((ImageView) gridChild.findViewById(R.id.edit_photo)).setImageBitmap(bitmapImages[imgDataID]);
					((ImageView) gridChild.findViewById(R.id.edit_photo)).setBackgroundDrawable(null);
					Toast.makeText(mContext, "update",
							Toast.LENGTH_SHORT).show();
					//}
				}
			}
		}
	}

	OnClickListener editPhotoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent in = new Intent(mContext, EditImageActivity.class);
			Bitmap b;
			Integer i = (Integer) v.getTag();
			b = bitmapImages[i];
			if (b!=null){
				ByteArrayOutputStream bs = new ByteArrayOutputStream();
				b.compress(Bitmap.CompressFormat.PNG, 50, bs);
				in.putExtra("imageData", bs.toByteArray());
				in.putExtra("imageDataID", i);
				startActivityForResult(in, RESULT_EDIT_IMAGE);
			} else {
				Log.d(this.toString(), "b is null");
				Toast.makeText(mContext, "No image here.", Toast.LENGTH_SHORT).show();
			}
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

		gridView = (GridView) findViewById(R.id.gridView);
		
		ImageAdapter adapter = new ImageAdapter(this, bitmapImages);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent in = new Intent(mContext, EditImageActivity.class);
				Bitmap b;
				b = bitmapImages[position];
				if (b!=null){
					ByteArrayOutputStream bs = new ByteArrayOutputStream();
					b.compress(Bitmap.CompressFormat.PNG, 50, bs);
					in.putExtra("imageData", bs.toByteArray());
					in.putExtra("imageDataID", position);
					startActivityForResult(in, RESULT_EDIT_IMAGE);
				} else {
					Log.d(this.toString(), "b is null");
					Toast.makeText(mContext, "No image here.", Toast.LENGTH_SHORT).show();
			}
		}});
		
		new TheTask().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (spotPostion<9){
			// find the first empty position and load picture from gallery
			Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);
			return true;
		} else {
			Toast.makeText(mContext, "Maximum reached, please clear all.",
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
					ViewGroup gridChild = (ViewGroup) gridView.getChildAt(i);
					((ImageView) gridChild.findViewById(R.id.edit_photo)).setImageBitmap(bitmapImages[i]);
					((ImageView) gridChild.findViewById(R.id.edit_photo)).setBackgroundDrawable(null);
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
					ViewGroup gridChild = (ViewGroup) gridView.getChildAt(i);
					((ImageView) gridChild.findViewById(R.id.edit_photo)).setBackgroundDrawable(getResources().getDrawable(R.drawable.defaul_image));
					((ImageView) gridChild.findViewById(R.id.edit_photo)).setImageBitmap(null);
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