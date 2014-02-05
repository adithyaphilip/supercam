package tech.app.supercam;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class CameraViewer extends SurfaceView implements SurfaceHolder.Callback{
	Camera mCamera;
	TextView fail;
	SurfaceHolder holder;
	@SuppressWarnings("deprecation")
	public CameraViewer(Context context, Camera cam) {
		super(context);
		fail = new TextView(this.getContext());
		mCamera=cam;
		if(mCamera!=null)//Camera was successfully accessed
		{
			holder = getHolder();//returns a surface holder which allows us to manipulate the surface view displayed on-screen
			holder.addCallback(this);//So that we know when surfaceview is created and destroyed
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//we need this to prevent an error on earlier android versions
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		//we don't use this as we've set the orientation to landscape
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			mCamera.setPreviewDisplay(holder);//a camera needs a surfaceview to display what it sees
			mCamera.startPreview();//starts displaying what the camera sees through this surfaceview
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		mCamera.stopPreview();//stops displaying what the camera sees
		mCamera.release();//releases the camera so that in case the application is closed, no camera errors are caused when it is used again
	}
}
