package tech.app.supercam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class NewCameraActivity extends Activity implements OnClickListener{

	TextView error;
	Camera mCamera;
	Uri fr;
	Handler mHandler;
	Uri npath;
	int w;
	int h;
	private PictureCallback mPicCall = new PictureCallback(){

		@Override
		public void onPictureTaken(byte[] data, Camera arg1) {
			FileOutputStream fo = null;
			mCamera.stopPreview();//stops displaying the camera view
			npath = PathFinder.getNextPath(fr);//gets the path to store newly taken image
			File f = new File(npath.getEncodedPath());
			try{
				fo = new FileOutputStream(f);
				fo.write(data);//writes the image data to the file
				TagManager.writeTag(fr, "", 1);
				TagManager.writeTag(fr, "", 2);		
				mHandler.sendMessage(mHandler.obtainMessage());
			}catch(Exception e)
			{
				
			}
			finally{
				try {
					fo.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		w=p.x;
		h=p.y;
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		error = new TextView(this);//used in case an error crops up which needs to be displayed
		RelativeLayout mrl = new RelativeLayout(this);
		setContentView(mrl);
		mrl.addView(error);
		fr = Uri.fromFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
		mCamera = getCameraInstance();//user-defined function, see below
		mHandler = new Handler(){
			public void handleMessage(Message msg)
			{
				Intent i = new Intent(getBaseContext(),ImageModActivity.class);
				i.putExtra("path", npath).putExtra("just_taken",true);
				TagManager.writeTag(npath, "", 1);
				TagManager.writeTag(npath, "", 2);
				//above two lines create tags for newly taken image
				startActivity(i);//starts the ImageViewer activity
				closeActivity();//ends the take picture screen
			}
		};
		if(mCamera==null)
		{
			error.setText("Camera is being used by another application.");
		}
		else//camera is available for the application to use
		{
			CameraViewer cv = new CameraViewer(this,mCamera);//see the CameraViewer class. It is a SurfaceView to display what the camera sees
			mrl.addView(cv);//Adds this surfaceview to the layout
			ImageButton tp = new ImageButton(this);//The take picture button
			tp.setImageResource(R.drawable.cam);
			mrl.addView(tp);
			RelativeLayout.LayoutParams tplp = (LayoutParams) tp.getLayoutParams();
			tplp.height=h;
			tplp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			tplp.addRule(RelativeLayout.CENTER_VERTICAL);
			tp.setId(1);
			tp.setOnClickListener(this);
		}
	}
	Camera getCameraInstance()
	{
		Camera c = null;
		try{
			c = Camera.open();
		}catch(Exception e)
		{
			
		}
		return c;//if camera opening failed, returns null
	}
	public void closeActivity()
	{
		this.onBackPressed();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case 1:
		{
			mCamera.takePicture(null, null,mPicCall);//we only require to catch the jpeg image, hence only third argument is required	
			break;
		}		
		}	
	}
}
