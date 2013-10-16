package sg.ntu.mapp.samsungfirstapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.provider.MediaStore;

public class Utils {

	private static boolean flag_save = false;
	private static String imageName = "img_";
	private static String imageExtension = ".jpg";

	public static boolean saveImage(Bitmap bitmap, ContentResolver contentResolver) {

		String strFileName = imageName + getCurrentDateAndTime() + imageExtension;

		try {
			String url = MediaStore.Images.Media.insertImage(contentResolver, bitmap, strFileName, null);
			
			if(url != null){
				flag_save = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return flag_save;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDateAndTime() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String formattedDate = df.format(c.getTime());

		return formattedDate;
	}
}
