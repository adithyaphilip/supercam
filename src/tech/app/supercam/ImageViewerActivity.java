package tech.app.supercam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import tech.app.supercam.R;

public class ImageViewerActivity extends Activity implements OnClickListener{

	RelativeLayout mrl;//main layout
	RelativeLayout topban;//top creamy UI layout where date and back,next buttons are displayed
	int h;
	int w;
	Uri imgpaths[];//holds paths converted from obtpaths
	Parcelable obtpaths[];//obtained paths array before it is converted to imgpaths
	String titletxt;//Holds the title's text which is the date
	int index;//index of next image to display from imgpaths[] array
	ImageView img;//holds the image being displayed
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        w = size.x;
        h = size.y;
        
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        
        int black = getResources().getColor(R.color.black);
        
        Intent i = getIntent();
        obtpaths= i.getParcelableArrayExtra("paths");
        setImagePaths(obtpaths);
        
        titletxt = i.getStringExtra("title");
        Log.d("titletext",titletxt);//testing purposes
        
		mrl = new RelativeLayout(this);
		setContentView(mrl);
		mrl.setBackgroundResource(R.drawable.creamban);
		
		topban = new RelativeLayout(this);
		mrl.addView(topban);
		RelativeLayout.LayoutParams tlp = (LayoutParams) topban.getLayoutParams();
		tlp.height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics());	
		tlp.width=w;
		topban.setBackgroundResource(R.drawable.creamban);	
		topban.setId(10);
						
		Button back = new Button(this);
		mrl.addView(back);
		RelativeLayout.LayoutParams blp = (LayoutParams) back.getLayoutParams();
		blp.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics());
		blp.width=blp.height;
		blp.addRule(RelativeLayout.BELOW,topban.getId());
		blp.setMargins(SizeManager.getDip(1, metrics), 0, 0, 0);
		back.setBackgroundResource(R.drawable.blckleftxml);
		back.setTypeface(null,Typeface.BOLD);
		back.setOnClickListener(this);
		back.setId(1);		
		
		TextView title = new TextView(this);
		topban.addView(title);
		RelativeLayout.LayoutParams rlp = (LayoutParams) title.getLayoutParams();
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);	
		title.setTextSize(20);
		title.setTextColor(black);
		//title.setTypeface(null,Typeface.BOLD);
		title.setText(titletxt);
		title.setId(2);
		
		Button next = new Button(this);
		mrl.addView(next);
		RelativeLayout.LayoutParams nlp = (LayoutParams) next.getLayoutParams();
		nlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		nlp.height=SizeManager.getDip(44, metrics);
		nlp.width=nlp.height;
		nlp.setMargins(0, 0, SizeManager.getDip(1, metrics), 0);
		nlp.addRule(RelativeLayout.CENTER_VERTICAL);
		nlp.addRule(RelativeLayout.BELOW,topban.getId());
		next.setBackgroundResource(R.drawable.blckrightxml);
		next.setTypeface(null,Typeface.BOLD);
		next.setOnClickListener(this);	
		next.setId(3);
		
		img = new ImageView(this);//ImageView which holds the image being displayed
		mrl.addView(img);
		RelativeLayout.LayoutParams imglp = (LayoutParams) img.getLayoutParams();
		imglp.addRule(RelativeLayout.BELOW,topban.getId());
		img.setAdjustViewBounds(true);
		img.setScaleType(ScaleType.CENTER_INSIDE);

		back.bringToFront();
		next.bringToFront();
		
		loadImage();
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId()){
		case 1://back button
		{
			goBack();		
			break;
		}
		case 3://next button
		{
			goForward();
			break;
		}
		}		
	}
	public void goForward()
	{
		if(index<imgpaths.length-1)
		{
			index++;//sets index to next image
		}
		loadImage();
	}
	public void goBack()
	{
		if(index!=0)
		{
			index--;//sets index to previous image
			loadImage();
		}
					
	}
	public void loadImage()
	{
		if(imgpaths.length>0)
		img.setImageBitmap(ImageModder.getMiniBitmap(w, 0, imgpaths[index]));
	}
	public void setImagePaths(Parcelable[] obtpaths)
	{//converts obtained parcelable array to Uri array
		imgpaths = new Uri[obtpaths.length];
		for(int i =0;i<obtpaths.length;i++)
		{
			imgpaths[i] = (Uri) obtpaths[i];
		}
	}
}
