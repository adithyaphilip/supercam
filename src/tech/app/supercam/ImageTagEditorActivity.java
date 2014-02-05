package tech.app.supercam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageTagEditorActivity extends Activity implements OnClickListener{
	Uri path;
	String[] tagset1;
	String[] tagset2;
	String[] tagset;
	String tag;
	int num;
	boolean both = false;
	TextView tv;
	OnClickListener rblstnr;
	RelativeLayout arl;
	int w;
	int h;
	ScrollView sc;
	RelativeLayout mrl;
	RelativeLayout rl;//layout in which radio buttons are contained
	LinearLayout bl;//layout to hold buttons
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        w = size.x;
        h = size.y;         
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
       
        
		path = getIntent().getParcelableExtra("path");
		num = getIntent().getIntExtra("tagset",0);
		both =  getIntent().getBooleanExtra("both", false);
		mrl = new RelativeLayout(this);
		RelativeLayout.LayoutParams mrlp = new RelativeLayout.LayoutParams(w,h/2);		
		mrl.setLayoutParams(mrlp);
		mrl.setBackgroundResource(R.drawable.creamban);
		setContentView(mrl);
		//Learn ListView
		sc = new ScrollView(this);
		mrl.addView(sc);
		sc.setId(10001);
		RelativeLayout.LayoutParams sclp = (LayoutParams) sc.getLayoutParams();
		sclp.width=w;
		sclp.height=h/2;
		sclp.addRule(RelativeLayout.BELOW,2222);
		
		 tv = new TextView(this);
		 if(num==1)
			 tv.setText("What's in the picture?");
		 else if(num==2)
			 tv.setText("What color is it?");
		 tv.setTextSize(22);
		 tv.setTypeface(null,Typeface.BOLD);
		 tv.setId(2222);
		 tv.setTextColor(getResources().getColor(R.color.black));
		 mrl.addView(tv);
		 
		 rl = new RelativeLayout(this);
		 sc.addView(rl);
			
		 refreshTagList();	
		
		 bl = new LinearLayout(this);
		 mrl.addView(bl);		 
		 RelativeLayout.LayoutParams blp = (LayoutParams) bl.getLayoutParams();
		 blp.width=LinearLayout.LayoutParams.MATCH_PARENT;
		 blp.addRule(RelativeLayout.BELOW,sc.getId());
		 bl.setOrientation(LinearLayout.HORIZONTAL);
		 
		 Button submit = new Button(this);
		 submit.setText("Done");
		 submit.setTextSize(18);
		 submit.setBackgroundResource(R.drawable.creamrectbtnxml);
		 submit.setTextColor(getResources().getColor(R.color.black));
		 submit.setOnClickListener(this);
		 submit.setId(10002);
		 bl.addView(submit);
		 LinearLayout.LayoutParams sblp = (LinearLayout.LayoutParams)submit.getLayoutParams();
		 sblp.weight=1;
		
		 Button at = new Button(this);//Adds tag
		 at.setText(R.string.addtag);
		 at.setBackgroundResource(R.drawable.creamrectbtnxml);
		 at.setTextColor(getResources().getColor(R.color.black));
		 at.setTextSize(18);
		 bl.addView(at);
		 LinearLayout.LayoutParams atlp = (LinearLayout.LayoutParams)at.getLayoutParams();
		 atlp.weight=1;
		 at.setId(10003);
		 at.setOnClickListener(this);
		
		 Button dt = new Button(this);//Deletes Tag
		 dt.setText(R.string.deletetag);
		 dt.setTextColor(getResources().getColor(R.color.black));
		 dt.setBackgroundResource(R.drawable.creamrectbtnxml);
		 bl.addView(dt);
		 LinearLayout.LayoutParams dtlp = (LinearLayout.LayoutParams)dt.getLayoutParams();
		 dtlp.weight=1;
		 dt.setTextSize(18);
		 dt.setId(10004);
		 dt.setOnClickListener(this);
		 tag = TagManager.getTag(path,num);
	}
	public void refreshTagList()
	{
		tag = TagManager.getTag(path, num);
		rl.removeAllViews();
		tagset = TagManager.getTagSet(PathFinder.getDirectory(path), num);
		rblstnr = new OnClickListener(){

			@Override
			public void onClick(View v) {
				int id = v.getId();
				for(int i =0;i<tagset.length;i++)
				{
					if(i+1!=id)
					{
						RadioButton chk = (RadioButton)findViewById(i+1);
						chk.setChecked(false);
					}
				}
				
			}
        	
        };
		for(int i =0; i<tagset.length;i++)
		{			
			RadioButton chk = new RadioButton(this);
			rl.addView(chk);
			RelativeLayout.LayoutParams clp = (LayoutParams) chk.getLayoutParams();
			chk.setId(i+1);
			clp.addRule(RelativeLayout.BELOW,i);
			
			chk.setText(tagset[i]);		
			chk.setTextColor(getResources().getColor(R.color.black));
			Log.d("tag","hey:"+tag);
			if(tag.equals(tagset[i]))
			{
				chk.setChecked(true);
			}
				
			chk.setOnClickListener(rblstnr);
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case 10002:
		{
			if(isAnyChecked())//does not go forward if no radio buttons are checked
			{
				assignTag();
			
				if(both)//starts the tageditoractivity for the second tagset if true
				{
					Intent i = new Intent(this,ImageTagEditorActivity.class);
					i.putExtra("path", path);
					i.putExtra("tagset", 2);
					startActivity(i);
				}
				super.onBackPressed();
			}
			else
			{
				Toast.makeText(this, "Please choose a tag", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case 10003:
		{
			addTag();
			break;
		}
		case 10004:
		{
			deleteTag();
			break;
		}
		}
	}
	public void addTag()
	{//Opens a dialog box to add tags
		arl = new RelativeLayout(this);
		final EditText et = new EditText(this);
		arl.addView(et);
		et.setHint(R.string.entertagname);
		et.setId(20001);
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(arl).setTitle(R.string.entertagname);
		builder.setPositiveButton(R.string.add, new Dialog.OnClickListener(){			
			@Override
			public void onClick(DialogInterface d, int arg1) {
				if(!TagManager.addToTagSet(path,num,et.getText().toString()))//returns false if failed
				{
					Toast.makeText(getBaseContext(), "Could not add", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getBaseContext(), et.getText().toString(), Toast.LENGTH_LONG).show();
					d.dismiss();
					refreshTagList();					
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new Dialog.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface d, int arg1) {
				d.dismiss();
			}
		});
		AlertDialog ad = builder.create();
		ad.show();
	}
	public void deleteTag()//starts the deletetagactivity
	{
		Intent i = new Intent(this,DeleteTagActivity.class);
		i.putExtra("path", path);
		i.putExtra("tagset", num);
		startActivityForResult(i,1);//1 is requestcode, can be anything
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode==1)//means DeleteTagActivity has returned
		{
			if(resultCode==RESULT_OK)
			{
				refreshTagList();
			}
		}
	}
	public boolean isAnyChecked()//checks if any of the radio buttons are checked
	{
		for(int i =0;i<tagset.length;i++)
		{
			RadioButton chk = (RadioButton)findViewById(i+1);
			if(chk.isChecked())
				return true;
		}
		return false;
	}
	public void assignTag()//writes the selected tag to a file
	{
		for(int i =0;i<tagset.length;i++)
		{
			RadioButton chk = (RadioButton)findViewById(i+1);
			if(chk.isChecked())
			{
				switch(num)
				{
				case 1:
				{

					if(!TagManager.writeTag(path, chk.getText().toString(),num))//returns false if an error is encountered
					{
						Toast.makeText(this, "Writing error. Please try again", Toast.LENGTH_LONG).show();
					}
					break;
				}
				case 2:
				{
					if(!TagManager.writeTag(path, chk.getText().toString(),num))//returns false if an error is encountered
					{
						Toast.makeText(this, "Writing error. Please try again", Toast.LENGTH_LONG).show();
					}
					break;
				}
				}
			}
		}		

	}
	
}
