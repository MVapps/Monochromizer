package com.monochromizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;



public class PicPage extends Activity implements OnSeekBarChangeListener{
	public int SELECT_IMAGE;
	private Bitmap bmp;
	private int intArray[];
	private int midArray[];
	public int thresh;
	SeekBar mSeekBar;
	TextView threshold;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page2);
		mSeekBar = (SeekBar) findViewById(R.id.seekBar1);
		threshold = (TextView) findViewById(R.id.thresh);
		threshold.setText("50");
		mSeekBar.setOnSeekBarChangeListener(this);
		
		
		startActivityForResult(new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
				SELECT_IMAGE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_IMAGE)
			if (resultCode == Activity.RESULT_OK) {

				Uri selectedImage = data.getData();
				String imagepath = selectedImage.getPath().toString();
				try {
					bmp = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(selectedImage));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
				intArray = new int[bmp.getWidth() * bmp.getHeight()];
				bmp.getPixels(intArray, 0, bmp.getWidth(), 0, 0,
						bmp.getWidth(), bmp.getHeight());
				int maxValue = intArray[0];
				int minValue = intArray[0];

				// Find Max and Min Values
				for (int i = 1; i < intArray.length; i++) {
					if (intArray[i] > maxValue) {
						maxValue = intArray[i];
					}
					if (intArray[i] < minValue) {
						minValue = intArray[i];
					}
				}
				System.gc();
				int monoValue = ((minValue - 0XFF000000 + maxValue - 0xFF000000) / 2) + 0XFF000000;

				// //////////////////////////////////
				for (int i = 0; i < intArray.length; i++) {
					if (intArray[i] >= monoValue) {
						intArray[i] = 0xFFFFFFFF;
					}
					if (intArray[i] < monoValue) {
						intArray[i] = 0xFF000000;
					}
				}
				// //////////////////////////
				bmp = Bitmap.createBitmap(intArray, bmp.getWidth(),
						bmp.getHeight(), Bitmap.Config.ARGB_8888);

				String sdloc = Environment.getExternalStorageDirectory()
						.toString();
				// OutputStream outStream = null;
				File file = new File(sdloc, "test.png");
				try {
					file.delete();
					file.createNewFile();
					boolean canWrite = file.canWrite();
				} catch (IOException e) {
				}
				try {
					FileOutputStream outStream = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
					bmp.recycle();
					outStream.flush();
					outStream.close();
				} catch (IOException e) {
				}
				// refresh Gallery
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));

			}
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean arg2) {
		String what = Integer.toString(progress);
		threshold.setText(what);
		thresh = progress;
		
	}

	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
}
