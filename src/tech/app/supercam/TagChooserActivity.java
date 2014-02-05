package tech.app.supercam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import tech.app.supercam.R;

public class TagChooserActivity extends Activity implements OnClickListener{
	/*This class is displayed as a dialog box using the android:theme attribute the AndroidManifest.xml file*/
	Uri directory;//directory where images and tags are stored
	String[] tagset;//list of tags to be displayed
	int num;//1 - "What is it?" tagset, 2 - "What color" tagset
	TextView tv;//title to be displayed Eg:_ "What color"
	int w;//width of display	
	int h;//height of display
	ScrollView sc;
	RelativeLayout mrl;//main layout which is set as the content (setContentView(mrl))
	LinearLayout rl;//layout in which check boxes are contained
	String tag[];//list of checked tags
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        w = size.x;
        h = size.y;         
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
       
        
		directory = getIntent().getParcelableExtra("path");
		num = getIntent().getIntExtra("tagset",0);
		
		mrl = new RelativeLayout(this);
		
		RelativeLayout.LayoutParams mrlp = new RelativeLayout.LayoutParams(w,RelativeLayout.LayoutParams.WRAP_CONTENT);		
		mrl.setLayoutParams(mrlp);
		mrl.setBackgroundResource(R.drawable.oddwoodbg);
		//Learn ListView
		sc = new ScrollView(this);//this view holds rl and allows scrolling
		mrl.addView(sc);
		sc.setId(10001);
		RelativeLayout.LayoutParams sclp = (LayoutParams) sc.getLayoutParams();
		sclp.width=w;
		sclp.height=h/2;
		sclp.addRule(RelativeLayout.BELOW,2222);
		
		 tv = new TextView(this);//title of dialog box
		 if(num==1)
			 tv.setText("What're you looking for?");
		 else if(num==2)
			 tv.setText("What color is it?");
		 tv.setTextSize(22);
		 tv.setTypeface(null,Typeface.BOLD);
		 tv.setId(2222);
		 tv.setTextColor(getResources().getColor(R.color.white));
		 mrl.addView(tv);
		 
		 rl = new LinearLayout(this);//This layout holds the checkboxes
		 rl.setOrientation(LinearLayout.VERTICAL);
		 sc.addView(rl);
			
		 refreshTagList();
		
		 Button submit = new Button(this);//when the user wants to submit his selected tags
		 submit.setText("Submit");
		 submit.setTextSize(18);
		 submit.setOnClickListener(this);
		 submit.setTextColor(getResources().getColor(R.color.white));
		 submit.setId(10002);
		 mrl.addView(submit);
		 RelativeLayout.LayoutParams sblp = (LayoutParams) submit.getLayoutParams();
		 sblp.addRule(RelativeLayout.BELOW,10001);	
		 submit.setBackgroundResource(R.drawable.blckbtnxml);
		 setContentView(mrl);
	}
	public void refreshTagList()
	{	
		rl.removeAllViews();//first clears the views already present
		//gets the taglist for 'num' tagset
		tagset = TagManager.getTagSet(directory, num);
		CheckBox all = new CheckBox(this);
		all.setText("All");
		all.setId(999);
		all.setTextColor(getResources().getColor(R.color.white));
		all.setTypeface(null,Typeface.BOLD);
		rl.addView(all);
		//creates checkboxes with a tag assigned to each
		for(int i =0; i<tagset.length;i++)
		{			
			CheckBox chk = new CheckBox(this);
			rl.addView(chk);
			chk.setId(i+1);			
			chk.setText(tagset[i]);		
			chk.setTextColor(getResources().getColor(R.color.white));
			chk.setTypeface(null,Typeface.BOLD);
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case 10002://when the "Submit" button is pressed
		{
			assignTag();
			break;
		}
		}
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
	public void assignTag()
	{//Checks which checkboxes have been checked, converts the tag names to a string array, and returns the array to the calling activity
		int ctr=0;
		tag = new String[100];
		CheckBox all = (CheckBox)findViewById(999);
		if(all.isChecked())
		{
			tag[ctr]="#!all";
			ctr++;
		}
		else//if all is not checked
		{
			for(int i =0;i<tagset.length;i++)
			{
				CheckBox chk = (CheckBox)findViewById(i+1);
				if(chk.isChecked())
				{
					tag[ctr]=chk.getText().toString();//assigns tag name is checkbox is checked					
					ctr++;				
				}			
			}		
		}
		String temp[] = new String[ctr];//used to remove the empty array spaces in tag[] by assigning only required length
		for(int i=0;i<ctr;i++)
		{
			temp[i]=tag[i];//copies valid tags from tag[] to temp[]
		}

		if(temp.length==0)//if user enters no option, assume he entered all
		{
			temp = new String[1];
			temp[0]="#!all";
		}
		Intent i = new Intent();
		i.putExtra("tags", temp);
		setResult(RESULT_OK,i);//returning to calling activity
		finish();//ends the activity
	}	
}