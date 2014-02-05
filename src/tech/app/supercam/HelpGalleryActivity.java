package tech.app.supercam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

/**
 * Returns an intent contained extra "checked" with true or false
 * @author USER
 *
 */
public class HelpGalleryActivity extends Activity {
	Intent ri;
	CheckBox chk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_help_gallery);		
		ri = getIntent();
		chk = (CheckBox)findViewById(R.id.dontaskagain);
		Button done = (Button)findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				Log.d("Help",""+chk.isChecked());
				ri.putExtra("checked", chk.isChecked());
				setResult(RESULT_OK,ri);
				getActivity().onBackPressed();
			}
		});
	}
	public Activity getActivity()
	{
		return this;
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
