package tech.app.supercam;

import java.util.Calendar;
import java.util.Locale;
import tech.app.supercam.R;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class CalendarActivity extends Activity implements OnClickListener{

	RelativeLayout mrl;//main layout
	TextView mnthname;//name of month displayed on top
	RelativeLayout topban;//top creamy UI banner which golds monthname and next,back buttons
	Calendar c; //main calendar
	int w;
	int h;
	DisplayMetrics metrics;
	RelativeLayout crl;//layout which holds all the dateboxes
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		w = size.x;
		h = size.y;
		
		metrics = getResources().getDisplayMetrics();
		
		c = Calendar.getInstance();//gets present date
		c.setLenient(true);//allows calendar to accept values beyond its range and automatically resolve it
		mrl = new RelativeLayout(this);
		setContentView(mrl);
		mrl.setBackgroundResource(R.drawable.creamban);
		
		topban = new RelativeLayout(this);
		mrl.addView(topban);
		RelativeLayout.LayoutParams tblp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,SizeManager.getDip(44, metrics));
		topban.setLayoutParams(tblp);
		topban.setBackgroundResource(R.drawable.creamban);
		topban.setId(1);		
		//topban children below
		
		Button lbtn = new Button(this);//back button
		topban.addView(lbtn);
		RelativeLayout.LayoutParams lblp = (LayoutParams)lbtn.getLayoutParams();
		lblp.height=SizeManager.getDip(44, metrics);
		lblp.width=lblp.height;
		lbtn.setId(2);
		lbtn.setBackgroundResource(R.drawable.blckleftxml);
		lbtn.setOnClickListener(this);		

		mnthname = new TextView(this); //monthname
		topban.addView(mnthname);
		mnthname.setTextSize(20);
		mnthname.setTypeface(null,Typeface.BOLD);
		mnthname.setId(3);
		RelativeLayout.LayoutParams mnlp = (LayoutParams) mnthname.getLayoutParams();
		mnlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		Button rbtn = new Button(this);//next button
		topban.addView(rbtn);
		rbtn.setId(4);
		rbtn.setBackgroundResource(R.drawable.blckrightxml);
		RelativeLayout.LayoutParams rblp = (LayoutParams) rbtn.getLayoutParams();
		rblp.height=SizeManager.getDip(44, metrics);
		rblp.width=lblp.height;
		rblp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rbtn.setOnClickListener(this);
		
		crl = new RelativeLayout(this);
		ScrollView sc = new ScrollView(this);//to hold crl to allow the dateboxes to be scrolled in case of insufficient space
		sc.addView(crl);
		mrl.addView(sc);
		RelativeLayout.LayoutParams sclp = (LayoutParams) sc.getLayoutParams();
		sclp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		sclp.addRule(RelativeLayout.BELOW,1);		
		drawCalendar();		
	}
	public void drawCalendar()
	{
		//sets the month name on top
		mnthname.setText(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
		
		crl.removeAllViews();//clears all dateboxes present (if present)
		int dbwidth = w/6;//width of each datebox
		DateBox[][] db = new DateBox[7][6];//7 rows for 7 days of the week. 6 columns.
		c.set(Calendar.DAY_OF_MONTH, 1);//sets calendar to start of the month
		int i =0;
			outer:for(int j=0;;j++)
			{
				if(j+1==c.get(Calendar.DAY_OF_WEEK))//tells calendar to start filling in real boxes instead od blank ones
				{//signifies first day of month has been found ^
					outer2:for(;i<6;i++)
					{//Fills in valid DateBoxes
						for(;j<7;j++)
						{
							if(j==0)
							db[j][i]=new DateBox(this,c.getTime(),dbwidth,dbwidth,getResources().getColor(R.color.Red));
							else
								db[j][i]=new DateBox(this,c.getTime(),dbwidth,dbwidth);
							crl.addView(db[j][i]);
							assignLayoutParams(db[j][i],i,j);
							c.add(Calendar.DAY_OF_MONTH, 1);
							Log.d("day",""+c.get(Calendar.DAY_OF_MONTH));
							if(c.get(Calendar.DAY_OF_MONTH)==1)
							{/*The calendar is set to lenient, which means that when we reach the end of a month
								Eg:- January 31st, and we add a day, it goes back to Jan 1st */		
								j++;
								c.add(Calendar.MONTH,-1);
								break outer2;//valid dateboxes have been filled in. Only empty ones remain
							}
						}
						j=0;
					}
				//fills in remaining dateboxes after valid ones are done
					for(;i<6;i++)
					{
						for(;j<7;j++)
						{
							db[j][i] = new DateBox(this,null,dbwidth,dbwidth);
							crl.addView(db[j][i]);
							assignLayoutParams(db[j][i],i,j);
						}
						j=0;
					}
					break outer;
				}
				else
				{	//Fills in blank DateBoxes before first day is detected				
					db[j][0]=new DateBox(this,null,dbwidth,dbwidth);
					crl.addView(db[j][0]);		//IGNORE:- Reminder to self:- remember to try without addingview to see if getLayoutParams works			
					assignLayoutParams(db[j][0],0,j);//assigns layoutparams so that the datebox knows its postion in the calendar					
				}
				Log.d("Outerloop","Limited");//testing purposes
			}
	}
	public void assignLayoutParams(DateBox db,int i, int j)//same i and j used in loops
	{
		db.setId(100+i*7+j);
		RelativeLayout.LayoutParams lp = (LayoutParams)db.getLayoutParams();
		if(j!=0)//if not first row
			lp.addRule(RelativeLayout.BELOW,db.getId()-1);
		if(i!=0)//if not first column
			lp.addRule(RelativeLayout.RIGHT_OF,db.getId()-7);
	}
	public void generateCalendar()
	{
		//IGNORE:- function serves no purpose as of now
	}
	@Override
	public void onClick(View v)
	{
		switch(v.getId()){		
		case 2://left button
		{
			c.add(Calendar.MONTH, -1);//goes one month back
			drawCalendar();
			break;
		}
		case 4://right button
		{
			c.add(Calendar.MONTH, 1);//goes to the next month
			drawCalendar();
			break;
		}
		}
	}
}
