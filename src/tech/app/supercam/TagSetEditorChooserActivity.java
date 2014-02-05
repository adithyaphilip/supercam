package tech.app.supercam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class TagSetEditorChooserActivity extends Activity implements OnClickListener {
//IGNORE:- This activity has not been used so far
	Uri imgpath;
	int num = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		imgpath = getIntent().getParcelableExtra("path");
		
		RelativeLayout mrl = new RelativeLayout(this);
		
		final RadioButton ts1 = new RadioButton(this);
		ts1.setId(2);
		
		final RadioButton ts2 = new RadioButton(this);
		ts2.setId(3);
		
		OnClickListener tslstnr = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(v.getId()==2)
				{
					ts2.setChecked(false);
					num=1;
				}
				else if(v.getId()==3)
				{
					ts1.setChecked(false);
					num=2;
				}
			}			
		};
		ts1.setOnClickListener(tslstnr);
		ts2.setOnClickListener(tslstnr);
		
		mrl.addView(ts1);
		mrl.addView(ts2);
		
		RelativeLayout.LayoutParams tslp = (LayoutParams) ts2.getLayoutParams();
		tslp.addRule(RelativeLayout.BELOW,2);
		
		Button b = new Button(this);
		b.setText("Submit");
		mrl.addView(b);
		b.setId(4);
		RelativeLayout.LayoutParams blp = (LayoutParams)b.getLayoutParams();
		blp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		blp.addRule(RelativeLayout.BELOW,3);
		b.setOnClickListener(this);
		setContentView(mrl);
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case 4:
		{
			Intent i = new Intent(this,ImageTagEditorActivity.class);
			i.putExtra("path", imgpath);
			if(num!=0)
			{
				i.putExtra("tagset", num);
				startActivity(i);
			}
			break;
		}
		}
	}

}
