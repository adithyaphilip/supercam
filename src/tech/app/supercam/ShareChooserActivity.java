package tech.app.supercam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ShareChooserActivity extends Activity implements OnClickListener{

	Uri[] list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_chooser);
		LinearLayout b = (LinearLayout)findViewById(R.id.sc_sms_layout);
		LinearLayout b2 = (LinearLayout)findViewById(R.id.sc_email_layout);
		b.setOnClickListener(this);
		b2.setOnClickListener(this);
		
		Intent i = getIntent();
		Parcelable[] t = i.getParcelableArrayExtra("paths");
		list = new Uri[t.length];
		for(int j=0;j<t.length;j++)
		{
			list[j]=(Uri)t[j];
		}
	}
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.sc_sms_layout:
			chooseSms();
			break;
		case R.id.sc_email_layout:
			chooseEmail();
			break;
		}
	}
	public void chooseSms()
	{
		Intent i = new Intent(getBaseContext(), SharingActivity.class);
		i.putExtra("via", "sms");
		i.putExtra("paths", list);
		startActivity(i);
	}
	public void chooseEmail()
	{
		Intent i = new Intent(getBaseContext(), SharingActivity.class);
		i.putExtra("via", "email");
		i.putExtra("paths", list);
		startActivity(i);
	}
}
