package tech.app.supercam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import tech.app.supercam.R;

public class DeleteTagActivity extends Activity implements OnClickListener{

	RelativeLayout mrl;//Main layout
	RelativeLayout rl;//Holds the tags
	Uri  path;//directory where tagset is present
	int num;//num to determine which tagset
	String tagset[];//list of tags
	int h;
	int w;
	ScrollView sc;
	OnClickListener splstnr;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        w = size.x;
        h = size.y;
        
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		path = getIntent().getParcelableExtra("path");
		num = getIntent().getIntExtra("tagset", 0);
		
		mrl = new RelativeLayout(this);
		mrl.setBackgroundResource(R.color.AntiqueWhite);
		
		sc = new ScrollView(this);
		mrl.addView(sc);
		RelativeLayout.LayoutParams sclp = (LayoutParams) sc.getLayoutParams();
		sclp.height=h/2;
		sclp.width=w;
		sc.setId(10001);
		
		rl = new RelativeLayout(this);
		sc.addView(rl);
		
		Button done = new Button(this);
		mrl.addView(done);
		done.setId(10002);
		done.setText(R.string.done);
		RelativeLayout.LayoutParams dlp = (LayoutParams) done.getLayoutParams();
		dlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		dlp.addRule(RelativeLayout.BELOW,10001);
		done.setOnClickListener(this);
		setContentView(mrl);		
		refreshTagList();
	}
	public void refreshTagList()
	{//this function is very similar to the refresh taglist function used in ImageTagChooserActivity
		tagset = TagManager.getTagSet(path, num);//gets present tagset
		splstnr = new OnClickListener(){
			@Override
			public void onClick(View v) {
				for(int i =0;i<tagset.length;i++)
				{
					if(v.getId()==i+1)
					{//finds the array index of the tag corresponding to the delete button pressed
						TagManager.deleteFromTagSet(path, num, tagset[i]);//deletes the tag
						refreshTagList();//Displays the new taglist with the deletedtag removed
					}
				}
			}			
		};
		rl.removeAllViews();//clears the relativelayout which holds all srl[] layouts
		RelativeLayout srl[] = new RelativeLayout[tagset.length];
		for(int i =0;i<tagset.length;i++)
		{
			srl[i] = new RelativeLayout(this);//this layout holds the tag name and the delete button of 1 tag
			rl.addView(srl[i]);
			RelativeLayout.LayoutParams srlp = (LayoutParams)srl[i].getLayoutParams();
			srlp.addRule(RelativeLayout.BELOW,9000+i-1);//aligns below previous tag
			srlp.width=w;
			srl[i].setId(9000+i);
			TextView tv = new TextView(this);//the tag name
			tv.setText(tagset[i]);
			tv.setTextColor(getResources().getColor(R.color.black));
			tv.setTextSize(18);
			tv.setTypeface(null,Typeface.BOLD);
			srl[i].addView(tv);
			RelativeLayout.LayoutParams tvlp = (LayoutParams) tv.getLayoutParams();
			tvlp.addRule(RelativeLayout.CENTER_VERTICAL);
			ImageView imgbut = new ImageView(this);//the delete button
			imgbut.setImageResource(R.drawable.tr1);
			srl[i].addView(imgbut);
			imgbut.setAdjustViewBounds(true);
			imgbut.setScaleType(ScaleType.CENTER_INSIDE);
			RelativeLayout.LayoutParams imglp = (LayoutParams) imgbut.getLayoutParams();
			imglp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imglp.addRule(RelativeLayout.CENTER_VERTICAL);
			imglp.height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
			imglp.width=imglp.height;
			imgbut.setId(i+1);
			imgbut.setOnClickListener(splstnr);//sets the onClickListener, which deletes the tag when clicked
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case 10002:{			
			Intent ri = new Intent();//intent to be returned
			setResult(RESULT_OK,ri);
			finish();
			super.onBackPressed();
			break;
		}
		}		
	}	
}
