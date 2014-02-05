package tech.app.supercam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.ccxxcjwpavvvdhfv.AdController;
import com.ccxxcjwpavvvdhfv.AdListener;

public class ImageGalleryActivity extends Activity implements OnClickListener{
	LinearLayout mrl;
	RelativeLayout rl1;
	RelativeLayout rl2;
	RelativeLayout topban;
	ImageView img1;
	ImageView img2;
	Uri path1;
	Uri path2;
	Uri list[];
	String tag1[]= {"#!all"};//so that all images are displayed automatically
	String tag2[] ={"#!all"};//so that all images are displayed automatically
	int height;
	int width;
	int imgh;
	int imgw;
	ImageView line;
	Uri fr;
	int index=0;
	ImageButton wear;
	ImageButton wear2;
	ImageButton laun;
	ImageButton laun2;
	final int imgmodreqcode = 4;
	final int RESULT_LAUNDRY_ACTIVITY = 10;
	final int ACTIVITY_HELP=11;
	int img1index;
	int img2index;
	Button n1;
	Button n2;
	Button b1;
	Button b2;
	AdController ad;
	AdController bAd;
	//int tindex;//used in individual image navigation
	
	/*NOTE:- Individual image navigation has its own variables img1index and img2index. In this
	 * way Individual image navigation proceeds independent from the design which displays two 
	 * images at a time. The functions with "SingleImage" in their names are used toward this
	 * purpose. 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Class c = getClass();//ignore this line
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int w = size.x;
        final int h = size.y;
        height = h;
        width = w;        
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        
        //ad = new AdController(this,"584135682");
        //ad.loadAd();
        bAd = new AdController(this,"882560824",new AdListener(){

			@Override
			public void onAdAlreadyCompleted() {				
			}

			@Override
			public void onAdCached() {
		        bAd.loadAd();
			}

			@Override
			public void onAdClicked() {				
			}

			@Override
			public void onAdClosed() {				
			}

			@Override
			public void onAdCompleted() {				
			}

			@Override
			public void onAdFailed() {				
			}

			@Override
			public void onAdLoaded() {				
			}

			@Override
			public void onAdPaused() {				
			}

			@Override
			public void onAdProgress() {				
			}

			@Override
			public void onAdResumed() {
				
			}});
        bAd.loadAdToCache();
       //int wh = getResources().getColor(R.color.white);
        
		mrl = new LinearLayout(this);
		setContentView(mrl);
		mrl.setOrientation(LinearLayout.VERTICAL);
		//mrl.setBackgroundResource(R.drawable.vdarkwoodbg2);
		mrl.setBackgroundResource(R.drawable.creamban);
		
		fr = Uri.fromFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
		
		topban = new RelativeLayout(this);//The banner at the top
		mrl.addView(topban);
		LinearLayout.LayoutParams tlp = (LinearLayout.LayoutParams) topban.getLayoutParams();
		tlp.height=SizeManager.getDip(44, metrics);
		tlp.width=w;
		topban.setBackgroundResource(R.drawable.creamban);
		
		rl1 = new RelativeLayout(this);//holds the first the image
		rl1.setId(10009);
		mrl.addView(rl1);
		LinearLayout.LayoutParams rlp1 = new LinearLayout.LayoutParams(w, 1);
		rl1.setLayoutParams(rlp1);
		rlp1.weight=1;//explained at rlp2.weight=1 below
		
		img1 = new ImageView(this);//for the first image
		rl1.addView(img1);
		img1.setAdjustViewBounds(true);
		img1.setScaleType(ScaleType.CENTER_INSIDE);
		RelativeLayout.LayoutParams img1lp = (LayoutParams) img1.getLayoutParams();
		img1lp.width=w;
		img1lp.height=(h-SizeManager.getDip(44, metrics))/2;
		
		wear = new ImageButton(this);//The Get button for the first image
		rl1.addView(wear);
		RelativeLayout.LayoutParams wlp = (LayoutParams) wear.getLayoutParams();
		wlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		wlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		wlp.width=wlp.height=SizeManager.getDip(60, metrics);
		wear.setScaleType(ScaleType.CENTER_INSIDE);
		wear.setImageResource(R.drawable.shirt);
		wear.setBackgroundResource(R.drawable.creamrectbtnxml);//xml file for the creamy UI
		wear.setId(91);
		wear.setOnClickListener(this);
		wear.setVisibility(RelativeLayout.GONE);
		
		imgh=(h-SizeManager.getDip(44, metrics))/2;
		
		laun = new ImageButton(this);
		rl1.addView(laun);
		RelativeLayout.LayoutParams lalp = (LayoutParams)laun.getLayoutParams();
		lalp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lalp.addRule(RelativeLayout.ABOVE,wear.getId());
		lalp.addRule(RelativeLayout.ALIGN_LEFT,wear.getId());
		laun.setPadding(0,0,0,0);
		laun.setAdjustViewBounds(true);
		laun.setScaleType(ScaleType.CENTER_INSIDE);
		laun.setImageResource(R.drawable.laundrybasket);
		laun.setBackgroundResource(R.drawable.creamrectbtnxml);
		laun.setId(101);
		laun.setVisibility(RelativeLayout.GONE);
		
		line = new ImageView(this);//The dividing line
		line.setBackgroundResource(R.color.black);
		mrl.addView(line);
		LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) line.getLayoutParams();
		llp.height=1;
		llp.width=w;
		llp.weight=0;//to prevent expansion to fill extra space if present
		
		rl2 = new RelativeLayout(this);//layout to hold second image
		rl2.setId(10010);
		mrl.addView(rl2);
		LinearLayout.LayoutParams rlp2 = new LinearLayout.LayoutParams(w, 1);
		rl2.setLayoutParams(rlp2);
		rlp2.weight=1;//same weight as rl1, so they equally fill extra space
		
		img2 = new ImageView(this);//for holding second image
		rl2.addView(img2);
		img2.setAdjustViewBounds(true);
		img2.setScaleType(ScaleType.CENTER_INSIDE);
		RelativeLayout.LayoutParams img2lp = (LayoutParams)img2.getLayoutParams();
		img2lp.addRule(RelativeLayout.CENTER_IN_PARENT);	
		img2lp.width=w;
		img2lp.height=(h-SizeManager.getDip(44, metrics))/2;
		
		wear2 = new ImageButton(this);//the "Get" button for the second layout image
		rl2.addView(wear2);
		RelativeLayout.LayoutParams w2lp = (LayoutParams) wear2.getLayoutParams();
		w2lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		w2lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		w2lp.height=w2lp.width=wlp.width;
		wear2.setScaleType(ScaleType.CENTER_INSIDE);
		wear2.setImageResource(R.drawable.shirt);
		wear2.setBackgroundResource(R.drawable.creamrectbtnxml);
		wear2.setId(92);
		wear2.setOnClickListener(this);
		wear2.setVisibility(RelativeLayout.GONE);	
		
		laun2 = new ImageButton(this);
		rl2.addView(laun2);
		RelativeLayout.LayoutParams lalp2 = (LayoutParams)laun2.getLayoutParams();
		lalp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lalp2.addRule(RelativeLayout.ABOVE,wear2.getId());
		lalp2.addRule(RelativeLayout.ALIGN_LEFT,wear2.getId());
		laun2.setAdjustViewBounds(true);
		laun2.setScaleType(ScaleType.CENTER_CROP);
		laun2.setPadding(0, 0, 0, 0);
		laun2.setImageResource(R.drawable.laundrybasket);
		laun2.setBackgroundResource(R.drawable.creamrectbtnxml);
		laun2.setId(102);
		laun2.setVisibility(RelativeLayout.GONE);	
						
		ImageButton back = new ImageButton(this);
		topban.addView(back);
		RelativeLayout.LayoutParams blp = (LayoutParams) back.getLayoutParams();
		blp.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics());
		blp.width=blp.height;
		blp.addRule(RelativeLayout.CENTER_VERTICAL);
		blp.setMargins(SizeManager.getDip(1, metrics), 0, 0, 0);
		back.setScaleType(ScaleType.CENTER_INSIDE);
		back.setImageResource(R.drawable.leftdouble);
		back.setBackgroundResource(R.drawable.creamrectbtnxml);
		back.setOnClickListener(this);
		back.setId(1);		
		
		RelativeLayout ltrl = new RelativeLayout(this);//to hold laundry button
		topban.addView(ltrl);
		RelativeLayout.LayoutParams ltlp = (LayoutParams) ltrl.getLayoutParams();
		ltlp.addRule(RelativeLayout.RIGHT_OF,back.getId());
		ltlp.addRule(RelativeLayout.LEFT_OF,2);//left of chngtags
		ltlp.addRule(RelativeLayout.CENTER_VERTICAL);
		
		ImageButton lbas = new ImageButton(this);//laundry button
		ltrl.addView(lbas);
		RelativeLayout.LayoutParams lblp = (LayoutParams)lbas.getLayoutParams();
		lblp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lblp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lbas.setImageResource(R.drawable.laundrybasket);
		lbas.setBackgroundResource(R.drawable.creamrectbtnxml);
		lbas.setOnClickListener(this);
		lbas.setPadding(0, 0, 0, 10);
		lbas.setId(103);
		
		Button chngtags = new Button(this);//the button used to enter tags to search for
		topban.addView(chngtags);
		RelativeLayout.LayoutParams rlp = (LayoutParams) chngtags.getLayoutParams();
		rlp.height=SizeManager.getDip(44, metrics);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);	
		chngtags.setId(2);
		chngtags.setBackgroundResource(R.drawable.creamrectbtnxml);
		chngtags.setText("Filter");
		chngtags.setTypeface(null,Typeface.BOLD);
		chngtags.setTextColor(getResources().getColor(R.color.black));
		chngtags.setOnClickListener(this);
		
		ImageButton share = new ImageButton(this);
		topban.addView(share);
		RelativeLayout.LayoutParams shlp = (LayoutParams)share.getLayoutParams();
		shlp.addRule(RelativeLayout.RIGHT_OF,chngtags.getId());
		shlp.addRule(RelativeLayout.LEFT_OF,3);		
		shlp.height=SizeManager.getDip(44, metrics);
		shlp.width=SizeManager.getDip(44, metrics);
		share.setBackgroundResource(R.drawable.creamrectbtnxml);
		share.setImageResource(R.drawable.share);
		share.setScaleType(ScaleType.CENTER_INSIDE);		
		share.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(),ShareChooserActivity.class);
				i.putExtra("paths", list);
				startActivity(i);
			}			
		});
		
		ImageButton next = new ImageButton(this);
		topban.addView(next);
		RelativeLayout.LayoutParams nlp = (LayoutParams) next.getLayoutParams();
		nlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		nlp.height=SizeManager.getDip(44, metrics);
		nlp.width=nlp.height;
		nlp.setMargins(0, 0, SizeManager.getDip(1, metrics), 0);
		nlp.addRule(RelativeLayout.CENTER_VERTICAL);
		next.setImageResource(R.drawable.rightdouble);
		next.setScaleType(ScaleType.CENTER_INSIDE);
		next.setBackgroundResource(R.drawable.creamrectbtnxml);
		next.setOnClickListener(this);	
		next.setId(3);
		
		loadPaths();
		loadImages();
		
		Button b1 = new Button(this);
		rl1.addView(b1);		
		RelativeLayout.LayoutParams b1lp = (LayoutParams) b1.getLayoutParams();
		b1lp.height=SizeManager.getDip(50, metrics);
		b1lp.width=SizeManager.getDip(50, metrics);
		b1.setBackgroundResource(R.drawable.blckleftxml);
		b1.setHeight(SizeManager.getDip(44, metrics));
		b1.setId(4);
		b1.setOnClickListener(this);
		
		Button n1 = new Button(this);
		rl1.addView(n1);
		RelativeLayout.LayoutParams n1lp = (LayoutParams) n1.getLayoutParams();
		n1lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		n1.setBackgroundResource(R.drawable.blckrightxml);
		n1lp.width=SizeManager.getDip(50, metrics);
		n1lp.height=SizeManager.getDip(50, metrics);
		n1.setId(5);
		n1.setOnClickListener(this);
		
		Button b2 = new Button(this);
		rl2.addView(b2);		
		RelativeLayout.LayoutParams b2lp = (LayoutParams) b2.getLayoutParams();
		b2lp.height=SizeManager.getDip(50, metrics);
		b2lp.width=SizeManager.getDip(50, metrics);
		b2.setBackgroundResource(R.drawable.blckleftxml);
		b2.setHeight(SizeManager.getDip(44, metrics));
		b2.setId(6);
		b2.setOnClickListener(this);
		
		Button n2 = new Button(this);
		rl2.addView(n2);
		RelativeLayout.LayoutParams n2lp = (LayoutParams) n2.getLayoutParams();
		n2lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		n2.setBackgroundResource(R.drawable.blckrightxml);
		n2lp.width=SizeManager.getDip(50, metrics);
		n2lp.height=SizeManager.getDip(50, metrics);
		n2.setId(7);
		n2.setOnClickListener(this);
		
		startHelp();
		
	}
	public void startHelp()
	{
		boolean show = true;
		if(new File(PathFinder.getHelpFilePath().getEncodedPath()).exists())
			show = false;
		if(show)
		{
			Intent helpIntent = new Intent(this,HelpGalleryActivity.class);
			startActivityForResult(helpIntent,ACTIVITY_HELP);
		}
	}
	@Override
	public void onResume()
	{
		super.onResume();
	}
	@Override
	public void onClick(View v)
	{
		switch(v.getId()){
		case 1: //back button
		{
			loadPrevImages();
			break;
		}
		case 2://submit tags button
		{
			getTags();
			break;
		}
		case 3://next button
		{
			loadImages();
			break;
		}
		/*case 91://wear button
		{
			if(img1.getDrawable()!=null)
			{
				DateManager.addDateWorn(list[getImgIndex(1)], Calendar.getInstance());
				//refer to DateManager class
			}
			break;
		}
		case 92://wear2 button
		{
			if(img2.getDrawable()!=null)
			{
				DateManager.addDateWorn(list[getImgIndex(2)], Calendar.getInstance());
				//refer to DateManager class
			}
			break;
		}*/
		case 4://b1 button
		{
			loadPreviousSingleImage(img1);
			break;
		}
		case 5://n1 button
		{
			loadSingleImage(img1);
			break;
		}
		case 6://b2 button
		{
			loadPreviousSingleImage(img2);
			break;
		}
		case 7://n2 button
		{
			loadSingleImage(img2);
			break;
		}
		case 103://laundry basket topban button
			Intent i = new Intent(this, LaundryActivity.class);
			startActivityForResult(i,RESULT_LAUNDRY_ACTIVITY);
			break;
		}
	}
	public int getImgIndex(int imgnum)//pass 1 for img1 and 2 for img2
	{
		int result = -1;//returns -1 if required img does not exist presently
		switch(imgnum)
		{
		case 1://img1 index is required
		{
			if(img1.getDrawable()==null)//first image is not displayed
				return result;
			else if(img2.getDrawable()==null)//only first image is displayed
			{
				result = index - 1;//if only the first image is displayed, image's index in the path array can be obtained by reducing the value of index variable by 1
			}
			else {//both images are displayed
				result = index - 2;//if both images are displayed, index of first image is obtained by reducing by 2
			}
			break;
		}
		case 2://img2 index is required
		{
			if(img2.getDrawable()!=null)//img 2 is present
			{
				result = index -1;//if 2 images are displayed, the second image's index can be obtained by just reducing index variable by 1
			}
			break;
		}
		}
		return result;
	}
	public void getTags()//opens up the TagChooserActivity for user to enter tags he wants to search for
	{
		Intent i = new Intent(this,TagChooserActivity.class);
		i.putExtra("path",fr);//directory where TagChooserActivity must look for the tags
		i.putExtra("tagset", 1);//tells TagChooserActviity to first display the "What kind" tagset (which is referred to as 1st tagset)
		startActivityForResult(i,1);//requestcode is 1, this will be used in the OnActivityResult function below
	}
	public void loadPaths()//to be called after onActivityResult
	{
		index=0;//means we're starting afresh
		img1index=0;
		img2index=0;
		clearImages();//clears the images displayed
		File imglist[] = PathFinder.getFileList(fr);//gets total list of images
		//Log.d("imglist_lngth",Integer.valueOf(imglist.length).toString());
		int imgctr = 0;
		for(int i =0;i<imglist.length;i++)//checks if they match the tags
		{
			boolean f1 = false;
			boolean f2 = false;
			Uri imgpath = Uri.parse(imglist[i].getAbsolutePath());
			String imgtag1 = TagManager.getTag(imgpath, 1);
			String imgtag2 = TagManager.getTag(imgpath, 2);
			for(int j =0;j<tag1.length;j++)
			{
					if(tag1[j].equals("#!all"))				
					{
						f1=true;
						break;
					}
					else if(tag1[j].equals(imgtag1))
					{
						f1=true;
						break;
					}
			}
			for(int j =0;j<tag2.length;j++)
			{
					if(tag2[j].equals("#!all"))
					{
						f2=true;
						break;
					}
					else if(tag2[j].equals(imgtag2))
					{
						f2=true;
						break;
					}				
			}
			if(f1&&f2)//if both first tagset and second tagset is matched, add to imglist
			{
				imglist[imgctr]=imglist[i];//replacing the file at imgctr with the file at 'i' where the qualifying img is located
				imgctr++;//imgctr is incremented so that the total qualified image count AND the location to save next image which qualifies both tagsets
			}
		}
		list = new Uri[imgctr];
		for(int i =0;i<imgctr;i++)
		{
			list[i] = Uri.fromFile(imglist[i]); // loaded Uri's of qualified images
		}
		list = removeLaundryImages(list);//remove laundry images
	}
	public void refresh()
	{
		loadPaths();
		loadImages();
	}
	public Uri[] removeLaundryImages(Uri[] t)
	{
		Uri laundry[] = LaundryManager.getLaundryFilePaths(PathFinder.dir);
		ArrayList<Uri> ar = new ArrayList<Uri>();
		for(Uri temp:t)
		{
			boolean equal = false;
			for(Uri ltemp:laundry)
			{
				if(temp.equals(ltemp))
				{
					equal = true;
				}
			}
			if(!equal)
				ar.add(temp);
		}
		return ar.toArray(new Uri[0]);
	}
	public void clearImages()
	{//effectively wipes the images and the "Get" buttons from the screen
		img1.setImageDrawable(null);
		img1.setOnClickListener(null);
		wear.setVisibility(RelativeLayout.GONE);
		laun.setVisibility(RelativeLayout.GONE);
		img2.setImageDrawable(null);
		img2.setOnClickListener(null);
		wear2.setVisibility(RelativeLayout.GONE);
		laun2.setVisibility(RelativeLayout.GONE);
	}
	public void loadImages()
	{
		if(list!=null)//ensures a list is present
		{
			if(index<list.length)//ensures that the index is lesser than the number of images corresponding to the entered tags
			{
				final int tind = index;
				clearImages();//removes both images, if present
				img1.setImageBitmap(ImageModder.getMiniBitmap(0, height/2, list[tind]));
				laun.setVisibility(RelativeLayout.VISIBLE);
				laun.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v)
					{
						LaundryManager.addToLaundryList(list[tind]);
						Log.d("Laundry1",""+tind);
						Toast.makeText(getBaseContext(), "Shifted to laundry", Toast.LENGTH_LONG).show();
						refresh();//to refresh						
					}
				});
				wear.setVisibility(LinearLayout.VISIBLE);
				wear.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v)
					{
						DateManager.addDateWorn(list[tind], Calendar.getInstance());
						Log.d("wear1",""+tind);
						Toast.makeText(getBaseContext(), "Added to calendar", Toast.LENGTH_LONG).show();
					}
				});
				index++;//increments index to next image to be displayed 
				img1index = index;
				img1.setOnClickListener(new OnClickListener(){				
					@Override
					public void onClick(View V)
					{
						Intent i = new Intent(getBaseContext(),ImageModActivity.class);
						i.putExtra("path", list[tind]);
						startActivityForResult(i,imgmodreqcode);
					}
				});
				if(index<list.length)//checks if another image can be displayed (the second image)
				{				
					final int tind2 = index;
					img2.setImageBitmap(ImageModder.getMiniBitmap(0, height/2, list[tind2]));
					index++;//increments index to next image to be displayed 
					img2index=index;
					laun2.setVisibility(RelativeLayout.VISIBLE);
					laun2.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v)
						{
							LaundryManager.addToLaundryList(list[tind2]);
							Log.d("Laundry2",""+tind2);
							Toast.makeText(getBaseContext(), "Shifted to laundry", Toast.LENGTH_LONG).show();
							refresh();
						}
					});
					wear2.setVisibility(LinearLayout.VISIBLE);
					wear2.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v)
						{
							DateManager.addDateWorn(list[tind2], Calendar.getInstance());
							Toast.makeText(getBaseContext(), "Added to calendar", Toast.LENGTH_LONG).show();
							Log.d("wear2",""+tind2);
						}
					});
					img2.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v)
						{
							Intent i = new Intent(getBaseContext(),ImageModActivity.class);
							i.putExtra("path", list[tind2]);
							startActivityForResult(i,imgmodreqcode);
						}
					});
				}
				else
				{
					Toast.makeText(this, R.string.galleryendreached, Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(this, R.string.galleryendreached, Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, R.string.galleryendreached, Toast.LENGTH_SHORT).show();
		}
	}
	public void loadPrevImages()
	{
		if(img1.getDrawable()!=null&&img2.getDrawable()!=null)
		index=index-4;//two images are currently displayed, so index needs to be reduced by 4 to get previous two images
		else
			index = index - 3;//only one image is displayed, which means that the index needs to reduced by only 3
		if(index<0)
		{
			index=0;
			Toast.makeText(this, "End of set", Toast.LENGTH_SHORT).show();
		}
		
		loadImages();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
		case 1:
		{
			if(resultCode==RESULT_OK)//when the first tagset options have been ticked in TagChooserActivity, shows the second tagset options by making another call to TagChooserActivity
			{
				tag1 = data.getStringArrayExtra("tags");
				Intent i = new Intent(this,TagChooserActivity.class);
				i.putExtra("path",fr);
				i.putExtra("tagset", 2);//Tells TagChooserActivity to use the second tagset
				startActivityForResult(i,2);//starts activity with resultcode 2
			}
			break;
		}
		case 2://when TagChooserActivity returns with the tags chosen from the second tagset
		{
			if(resultCode==RESULT_OK)
			{
				tag2 = data.getStringArrayExtra("tags");
				loadPaths();
				loadImages();
			}
			break;
		}
		case 4://When ImageModActivity returns
		{
			if(resultCode==RESULT_OK&&data!=null)
			{
				if(data.getBooleanExtra("deleted", false))//means image has been deleted through ImageModdeActivity
				{//If image has been deleted, we refresh the screen 
					Log.d("recreated","true");
					refresh();
				}
			}
			break;
		}
		case RESULT_LAUNDRY_ACTIVITY:
			if(resultCode==RESULT_OK&&data!=null)
			{
				if(data.getBooleanExtra("deleted", false))
				{
					refresh();
				}
			}
		case ACTIVITY_HELP:
			if(resultCode==RESULT_OK&&data!=null)
			{
				if(data.getBooleanExtra("checked", false))
				{
					Log.d("ImageGallery","preferences accessed");
					File f = new File(PathFinder.getHelpFilePath().getEncodedPath());
					try {
						f.createNewFile();
					} catch (IOException e) {
						Log.d("HELP","Failed creating file:"+e.getMessage());
					}
				}
			}
		}
	}
	public Activity getActivity()
	{
		return this;
	}
	//Newly added functions to facilitate independent image changing
	public void loadSingleImage(ImageView iv)//index is index of image to load
	{//wiv is wear button
		ImageButton wiv = null;
		ImageButton liv = null;
		int tindex = 0;
		if(iv.equals(img1))
		{
			tindex = img1index;
			if(tindex<list.length)
			img1index++;
			wiv=wear;
			liv=laun;
		}
		else if(iv.equals(img2))
		{
			tindex  = img2index;
			if(tindex<list.length)
			img2index++;
			wiv = wear2;
			liv=laun2;
		}
		final int uindex = tindex;//for use inside anonymous inner classes
		if(tindex<list.length)//ensures that the index is lesser than the number of images corresponding to the entered tags
		{
			iv.setImageBitmap(ImageModder.getMiniBitmap(0, height/2, list[tindex]));
			liv.setVisibility(LinearLayout.VISIBLE);
			liv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					LaundryManager.addToLaundryList(list[uindex]);
					Toast.makeText(getBaseContext(), "Shifted to laundry", Toast.LENGTH_LONG).show();
					refresh();
				}
				
			});
			wiv.setVisibility(LinearLayout.VISIBLE);
			wiv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v)
				{
					DateManager.addDateWorn(list[uindex], Calendar.getInstance());
					Toast.makeText(getBaseContext(), "Added to calendar", Toast.LENGTH_LONG).show();
				}
			});
			iv.setOnClickListener(new OnClickListener(){			
				@Override
				public void onClick(View V)
				{
					Intent i = new Intent(getBaseContext(),ImageModActivity.class);
					i.putExtra("path", list[uindex]);
					startActivityForResult(i,imgmodreqcode);
				}
			});
		}
	}
	public void loadPreviousSingleImage(ImageView iv)
	{
		if(iv.equals(img1))//if first image has called this function
		{
			img1index = img1index -2;
			if(img1index<0)//so that index does not fall out of range
				img1index=0;
			loadSingleImage(iv);
		}
		else if(iv.equals(img2))//if second image has called this function
		{
			img2index = img2index - 2;
			if(img2index<0)//so that index does not fall out of range
				img2index=0;
			loadSingleImage(iv);
		}
	}
}
