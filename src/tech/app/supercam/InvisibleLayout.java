package tech.app.supercam;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class InvisibleLayout extends RelativeLayout{
	Runnable r;
	long delayMillis;

	public InvisibleLayout(Context context) {
		super(context);
		this.setAlpha(0);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		runRunnable();
		return false;
	}
	public void runRunnable()
	{
		Handler handler = new Handler();
		handler.postDelayed(r, delayMillis);
	}
	public void setRunnableOnTouch(Runnable r,long delayMillis)
	{
		this.r=r;
		this.delayMillis=delayMillis;
	}
}
