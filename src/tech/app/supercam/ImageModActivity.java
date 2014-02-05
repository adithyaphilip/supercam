package tech.app.supercam;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class ImageModActivity extends Activity implements OnClickListener {

	ImageView img;
	int height;
	int width;
	Uri path;
	Intent ri; //intent to be returned as result on activity completion
	String color;
	boolean jtaken;
	Button back;
	Handler mHandler;
	ProgressDialog cpd;
	RelativeLayout topban;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int w = size.x;
        final int h = size.y;  
        height=h;
        width=w;
        
        DisplayMetrics metrics= getResources().getDisplayMetrics();
        
        final int green = getResources().getColor(R.color.green);
        final int red = getResources().getColor(R.color.red);
        final int wh = getResources().getColor(R.color.white);        
        
        ri = new Intent();
        
        Intent data = getIntent();
        jtaken = data.getBooleanExtra("just_taken",false);
      
		RelativeLayout mrl = new RelativeLayout(this);
		mrl.setBackgroundResource(R.drawable.graybg);
		setContentView(mrl);
		
		path = data.getParcelableExtra("path");
		createTags();
		
		topban = new RelativeLayout(this);
		mrl.addView(topban);
		topban.setBackgroundResource(R.drawable.creamban);
		RelativeLayout.LayoutParams tblp = (LayoutParams) topban.getLayoutParams();
		tblp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tblp.height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
		tblp.width=w;	
		
		back = new Button(this);
		topban.addView(back);
		RelativeLayout.LayoutParams bcklp = (LayoutParams) back.getLayoutParams();
		bcklp.addRule(RelativeLayout.CENTER_IN_PARENT);
		bcklp.height=tblp.height;
		bcklp.width=w;
		bcklp.height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
		back.setBackgroundResource(R.drawable.creamrectbtnxml);
		back.setText("Done");
		//back.setTypeface(null,Typeface.BOLD);
		back.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v)
		{
			checkAndEndActivity();
		}
		});
		
		RelativeLayout bban = new RelativeLayout(this);
		mrl.addView(bban);
		bban.setBackgroundResource(R.drawable.creamban);
		RelativeLayout.LayoutParams bblp = (LayoutParams) bban.getLayoutParams();
		bblp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		bblp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		bblp.height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
		bblp.width=w;

		img = new ImageView(this);
		if(ImageModder.isPortrait(path))
		{
			img.setImageBitmap(ImageModder.getMiniBitmap(width, 0, path));
		}
		else
		{
			img.setImageBitmap(ImageModder.getMiniBitmap(height,0,path));
		}
		mrl.addView(img);
		img.setAdjustViewBounds(true);
		img.setScaleType(ScaleType.CENTER_INSIDE);
		RelativeLayout.LayoutParams imglp = (LayoutParams) img.getLayoutParams();
		imglp.width=w;
		imglp.height=h-(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics())-h/10;
		imglp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		//Bitmap tbm = ((BitmapDrawable)img.getDrawable()).getBitmap();		
		//img.setImageBitmap(ImageModder.getMiniBitmap(height, ImageModder.getCorresponding(tbm.getWidth(), tbm.getHeight(), w, h), path));
		
		ImageView lrot = new ImageView(this);
		lrot.setAdjustViewBounds(true);
		lrot.setScaleType(ScaleType.CENTER_INSIDE);
		lrot.setImageResource(R.drawable.lrotxml);
		lrot.setId(3);
		bban.addView(lrot);
		RelativeLayout.LayoutParams lrlp = (LayoutParams) lrot.getLayoutParams();
		lrlp.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
		lrlp.width=lrlp.height;
		lrlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lrlp.addRule(RelativeLayout.CENTER_VERTICAL);
		lrlp.setMargins(SizeManager.getDip(5, metrics), 0, 0, 0);
		lrot.setOnClickListener(this);

		ImageView rrot = new ImageView(this);
		rrot.setAdjustViewBounds(true);
		rrot.setScaleType(ScaleType.CENTER_INSIDE);
		rrot.setImageResource(R.drawable.rrotxml);
		rrot.setId(4);
		bban.addView(rrot);
		RelativeLayout.LayoutParams rrlp = (LayoutParams) rrot.getLayoutParams();
		rrlp.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
		rrlp.width=rrlp.height;
		rrlp.addRule(RelativeLayout.RIGHT_OF,3);
		rrlp.addRule(RelativeLayout.CENTER_VERTICAL);
		rrlp.setMargins(SizeManager.getDip(10, metrics), 0, 0, 0);
		rrot.setOnClickListener(this);
		
		final Button sbut = new Button(this);	
		sbut.setId(5);
		bban.addView(sbut);
		RelativeLayout.LayoutParams sblp = (LayoutParams) sbut.getLayoutParams();
		sblp.height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
		sblp.addRule(RelativeLayout.RIGHT_OF,4);
		sblp.addRule(RelativeLayout.CENTER_VERTICAL);
		sblp.addRule(RelativeLayout.LEFT_OF,6);
		sbut.setBackgroundResource(R.drawable.creamrectbtnxml);		
		sbut.setText("Edit Tags");
		sbut.setClickable(true);
		sbut.setOnClickListener(this);
		
		ImageView tsh = new ImageView(this);
		bban.addView(tsh);
		tsh.setImageResource(R.drawable.creamcrossxml);
		tsh.setAdjustViewBounds(true);
		tsh.setScaleType(ScaleType.CENTER_INSIDE);
		RelativeLayout.LayoutParams trlp = (LayoutParams) tsh.getLayoutParams();
		trlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		trlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		trlp.height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());;
		trlp.width=trlp.height;
		tsh.setId(6);
		tsh.setOnClickListener(this);
		
		bban.bringToFront();
		
		setResult(RESULT_OK);
		
		if(jtaken)
		{
			sbut.setText("Enter Tags");
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Auto Color Detection").setMessage("Auto-detect color of this image?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d, int which)
				{
					d.dismiss();
					
					autoDetectColor(((BitmapDrawable)img.getDrawable()).getBitmap());
				}
			});
			builder.setNegativeButton("No", null);
			AlertDialog ad = builder.create();
			ad.show();
		}			
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				if(msg.getData().containsKey("function"))
				{
					if(msg.getData().getString("function").equals("determineColor"))
					{	
						cpd.dismiss();
						color = msg.getData().getString("color");
						Log.d("color",color);
						TagManager.writeTag(path, color, 2);
						Toast.makeText(getBaseContext(), "Detected: "+color, Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
		
		topban.bringToFront();
	}
	public void autoDetectColor(Bitmap bm)//detects and writes 2nd tag with detected color
	{
		cpd = new ProgressDialog(this);
		cpd.setMessage("Detecting");
		cpd.setCancelable(false);
		cpd.setCanceledOnTouchOutside(false);
		cpd.show();
		
		ColorDetector.determineColor(bm,mHandler);
	}
	public void createTags()
	{//Checks if TagFile is already present to prevent overwriting
		if(!isTagPresent(1))
		TagManager.writeTag(path, "", 1);
		if(!isTagPresent(2))
		TagManager.writeTag(path, "", 2);
	}
	public boolean isTagPresent(int num)
	{
		try{
		if(TagManager.getTag(path, num).equals(""))
			return false;
		}
		catch(Exception e)
		{
			Log.d("pathforexception",path.toString());
		}
		return true;
	}
	@Override
	public void onClick(View v)
	{
		switch(v.getId()){
		case 3:
		{
			ImageModder.rotate(270,((BitmapDrawable)img.getDrawable()).getBitmap(),img, height);
			saveNewImage();
			topban.bringToFront();
			break;
		}
		case 4:
		{
			ImageModder.rotate(90,((BitmapDrawable)img.getDrawable()).getBitmap(),img,height);
			saveNewImage();
			topban.bringToFront();
			break;
		}
		case 5:
		{
			saveNewImage();
			Intent i = new Intent(this,ImageTagEditorActivity.class);
			i.putExtra("path", path);
			i.putExtra("tagset", 1);
			i.putExtra("both", true);
			startActivity(i);
			break;
		}
		case 6:
		{
			deleteImage();
			break;
		}
			
		}
	}
	@Override
	public void onBackPressed()
	{
		if(!isTagPresent())
		{
			Toast.makeText(this, "Please enter both tags", Toast.LENGTH_LONG).show();
		}
		else
			super.onBackPressed();
	}
	public boolean isTagPresent()
	{
		boolean flag = true;
		try{
		if(!(isTagPresent(1)&&isTagPresent(2)))
			flag=false;
		}
		catch(Exception e)
		{
			
		}
		
		return flag;
	}
	public void endActivity()
	{
		super.onBackPressed();
	}
	public void checkAndEndActivity()
	{
		if(!isTagPresent())
		{
			Toast.makeText(this, "Please enter both tags", Toast.LENGTH_LONG).show();
		}
		else
			super.onBackPressed();
	}
	public void deleteImage()
	{//Opens Dialog box to show confirm dialog to user
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delimgtitle).setMessage(R.string.delimgcontent);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				PathFinder.deleteImage(path);	
				
				deletePerformed();
				endActivity();
			}
		});
		builder.setNegativeButton(R.string.no, null);
		AlertDialog ad = builder.create();
		ad.show();
	}
	public void deletePerformed()
	{
		ri.putExtra("deleted", true);
		setResult(RESULT_OK,ri);//tells the activity which started this that the image has been deleted
	}
	public void saveNewImage()
	{
		
		Uri path = getIntent().getParcelableExtra("path");
		try{
		ImageModder.saveNewImage(((BitmapDrawable)img.getDrawable()).getBitmap(), path);
		}
		catch(FileNotFoundException e)
		{
			Toast.makeText(this, path.getEncodedPath(), Toast.LENGTH_LONG).show();
		}
	}

}
