package tech.app.supercam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * 
 * @author USER
 * This class accepts the following Bundle values:
 * "to" - String with receiver phone number
 * "message" - String with message to be sent
 */
public class SmsActivity extends Activity{
	RelativeLayout mrl;
	EditText toEt;
	EditText msgEt;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		String to = b.getString("to", "");
		String msg = b.getString("message","");
		
		setContentView(R.layout.activity_sms);
		mrl = (RelativeLayout) findViewById(R.layout.activity_sms);
		toEt = (EditText) findViewById(R.id.toEt);
		if(!to.equals(""))
		{
			toEt.setText(to);
		}
		msgEt = (EditText) findViewById(R.id.msgEt);
		if(!msg.equals(""))
		{
			msgEt.setText(msg);
		}
		Button send = (Button)findViewById(R.id.sendSmsButton);
		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MySmsManager.sendSms(toEt.getText().toString(), null, msgEt.getText().toString(), null, null);		
			}
			
		});
	}	
}
