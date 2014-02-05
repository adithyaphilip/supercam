package tech.app.supercam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Accepts "message" String extra from calling intent
 * @author USER
 *
 */
public class EmailActivity extends Activity implements OnClickListener{

	EditText toEt;
	EditText subEt;
	EditText msgEt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_email);
		toEt = (EditText) findViewById(R.id.email_to_et);
		subEt = (EditText) findViewById(R.id.email_subject_et);
		msgEt = (EditText) findViewById(R.id.email_body_et);
		
		Intent i = getIntent();
		String msg = i.getStringExtra("message");
		msgEt.setText(msg);
		Button send = (Button)findViewById(R.id.email_send_button);
		send.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.email_send_button:
			sendEmail();
			break;
		}
	}
	public void sendEmail()
	{
		String to = toEt.getText().toString();
		String sub = subEt.getText().toString();
		String msg = msgEt.getText().toString();
		Log.d("EMAIL MSG",msg);
		if(isEmailValid(to))
		{
			EmailManager.sendEmail(to, sub, msg, this);
		}
		else
		{
			Toast.makeText(this, "Invalid Email Address Entered", Toast.LENGTH_SHORT).show();
		}
	}
	public boolean isEmailValid(String address)
	{
		if(address==null)
			return false;
		else
			return Patterns.EMAIL_ADDRESS.matcher(address).matches();
	}
}
