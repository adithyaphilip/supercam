package tech.app.supercam;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import tech.app.supercam.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DateBox extends RelativeLayout implements OnClickListener{
	TextView dnum;
	ImageView img;//picture of dress worn that day
	Uri imglist[];//array of paths of images worn on the date represented by the datebox
	Date d;
	Calendar c;
	Context context;
	DisplayMetrics metrics;
	int w;
	int h;
	public DateBox(Context context, Date d, int w, int h) {
		super(context);
		this.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
		this.setBackgroundResource(R.drawable.dbbg);
		if(d!=null)//this means a date has been supplied and it is not a blank date box
		{
			this.context=context;
			this.w=w;
			this.h=h;
			metrics = context.getResources().getDisplayMetrics();
			c=Calendar.getInstance();
			this.d=d;
			c.setTime(d);
			dnum = new TextView(context);
			dnum.setTypeface(null,Typeface.BOLD);
			this.addView(dnum);
			img = new ImageView(context);
			setNumber();
			imglist = DateManager.getWornOnDate(c);//gets the dress worn on the supplied date
			setImage();
		}
	}
	public DateBox(Context context, Date d, int w, int h, int color) {
		//alternate constructor which does the same thing as the other constructor, but sets the color of the text if required (red for sundays)
		super(context);
		this.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
		this.setBackgroundResource(R.drawable.dbbg);
		if(d!=null)
		{
			this.context=context;
			this.w=w;
			this.h=h;
			metrics = context.getResources().getDisplayMetrics();
			c=Calendar.getInstance();
			this.d=d;
			c.setTime(d);
			dnum = new TextView(context);
			dnum.setTypeface(null,Typeface.BOLD);
			this.addView(dnum);
			img = new ImageView(context);
			setNumber();
			imglist = DateManager.getWornOnDate(c);
			setImage();

			dnum.setTextColor(color);
		}
	}
	public void setNumber()
	{
		dnum.setText(""+c.get(Calendar.DAY_OF_MONTH));//sets the day of the month as a number
		dnum.setTextSize(22);
	}
	public void setImage()
	{
		if(imglist.length!=0)
		{	//if one or more images were worn on this date, sets the latest image to be worn as the background	
			img.setImageBitmap(ImageModder.getMiniBitmap(w, 0, imglist[0]));
			img.setScaleType(ScaleType.CENTER_CROP);
			this.addView(img);
			RelativeLayout.LayoutParams imglp = (LayoutParams)img.getLayoutParams();
			imglp.height=h;
			imglp.width=w;
			this.setOnClickListener(this);
		}
	}
	@Override
	public void onClick(View v)
	{//if one or more image are present, allows the user to view those images by clicking on the datebox
		Intent i = new Intent(context,ImageViewerActivity.class);
		i.putExtra("paths", imglist);
		String title = (String) DateFormat.format("MMMM dd, yyyy",c.getTime());
		Log.d("title",title);
		i.putExtra("title", title);
		context.startActivity(i);
	}
	public Date getDate()
	{//simply returns the date assigned to this datebox, if ever it is required
		return d;
	}
}
