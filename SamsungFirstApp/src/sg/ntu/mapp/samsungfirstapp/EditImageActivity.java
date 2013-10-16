package sg.ntu.mapp.samsungfirstapp;


import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.samsung.sdraw.SettingView;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.sec.chaton.clientapi.ChatONAPI;
import com.sec.chaton.clientapi.MessageAPI;
import com.sec.chaton.clientapi.UtilityAPI;

import sg.ntu.mapp.samsungfirstapp.utils.*;

public class EditImageActivity extends Activity {

	 private SCanvasView canvas;
		private Button btnEraser;
		private Bitmap bgImage;
		private int imgDataID;

		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_samsung_s);
	        
	        btnEraser = (Button) findViewById(R.id.button_eraser);
	        
	        Intent intent = getIntent();
	        byte[] imgData = intent.getByteArrayExtra("imageData");
	        imgDataID = intent.getIntExtra("imageDataID", 0);
	        bgImage = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
	       
	        canvas = new SCanvasView(this);

			RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout1);

			rl.addView(canvas);
			RelativeLayout.LayoutParams lp = (LayoutParams) canvas
					.getLayoutParams();
			lp.addRule(RelativeLayout.BELOW, R.id.header);
			lp.addRule(RelativeLayout.ABOVE, R.id.footer);
	    }
		
		@Override
		public void onWindowFocusChanged(boolean hasFocus) {
			// TODO Auto-generated method stub
			super.onWindowFocusChanged(hasFocus);
			if(hasFocus)
				canvas.setBGImage(bgImage);
	    }
		
	    public void undo(View v){
	    	canvas.undo();
	    }
	    
	    public void redo(View v){
	    	canvas.redo();
	    }
	    
	    public void incBrushSize(View v){
	    	if(canvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
	    		SettingStrokeInfo sti = canvas.getSettingStrokeInfo();
	    		sti.setStrokeWidth(sti.getStrokeWidth()+1);
	    		canvas.setSettingStrokeInfo(sti);
	    		System.out.println(sti.getStrokeWidth());
	        }
	    }
	    
	    public void decBrushSize(View v){
	    	if(canvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
	    		SettingStrokeInfo sti = canvas.getSettingStrokeInfo();
	    		sti.setStrokeWidth(sti.getStrokeWidth()-1);
	    		canvas.setSettingStrokeInfo(sti);
	    		System.out.println(sti.getStrokeWidth());
	        }
	    }
	    
	    public void eraser(View v){
	    	if(canvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
	    		canvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
	    		btnEraser.setText("Pen");
	    	}else{
	    		canvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
	    		btnEraser.setText("Eraser");
	    	}
	    }
	    
	    @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.activity_edit_image, menu);
			return true;
		}

	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle item selection
			switch (item.getItemId()) {
			case R.id.menu_save:
				saveImage();
				return true;
			case R.id.menu_share:
				shareImage();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		private void saveImage() {
			// TODO Auto-generated method stub
			Bitmap bitmap = canvas.getBitmap(false);

			if (bitmap != null) {
				Utils.saveImage(bitmap, getContentResolver());
				
				Toast.makeText(this, "Image has been saved.", Toast.LENGTH_SHORT).show();
				
				Intent in = getIntent();
				if (bitmap!=null){
					Toast.makeText(this, "result back.", Toast.LENGTH_SHORT).show();
					ByteArrayOutputStream bs = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
					in.putExtra("imageData", bs.toByteArray());
					in.putExtra("imageDataID", imgDataID);
					setResult(RESULT_OK, in);
					finish();
				}
				
			} else {
				Toast.makeText(this, "Image is not saved.", Toast.LENGTH_SHORT)
						.show();
			}
		}

		private void shareImage() {
			// TODO Auto-generated method stub
			boolean isInstalled = UtilityAPI.isChatONInstalled(this);
			
			// check result
			if (isInstalled) {
				Toast.makeText(this, "ChatON is installed.", Toast.LENGTH_SHORT)
						.show();

				Intent pick1 = new Intent(Intent.ACTION_PICK).setType("image/*");
				startActivityForResult(pick1, 1);
				
			} else {
				Toast.makeText(this, "ChatON is not installed.", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode != Activity.RESULT_OK)
				return;

			Uri uri = data.getData();
			ChatONAPI.MimeType mime = ChatONAPI.MimeType.image;

			// set type of mime
			switch (requestCode) {
			case 1:
				mime = ChatONAPI.MimeType.image;
				break;
			case 2:
				mime = ChatONAPI.MimeType.video;
			}

			// call sendMultiMediaMessage API
			int nResult = MessageAPI.sendMultiMediaMessage(this, uri, mime);

			// check result
			switch (nResult) {
			case ChatONAPI.RESULT_CODE_FAIL_EXCEPTION_ILLEGAL_ARGUMENT:
				Toast.makeText(this, "Illegal Argument!!\nPlease, check argument",
						Toast.LENGTH_SHORT).show();
				break;
			case ChatONAPI.RESULT_CODE_FAIL_EXCEPTION:
				Toast.makeText(this, "Exception!!\nPlease, check argument",
						Toast.LENGTH_SHORT).show();
				break;
			case ChatONAPI.RESULT_CODE_FAIL_MULTIMEDIA_LIMIT_EXCEEDED:
				Toast.makeText(this,
						"File size exceeds maximum upload limit (10 MB)",
						Toast.LENGTH_SHORT).show();
				break;
			case ChatONAPI.RESULT_CODE_FAIL_API_NOT_AVAILABLE:
				Toast.makeText(this,
						"API isn't availble. please check your ChatON version.",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
