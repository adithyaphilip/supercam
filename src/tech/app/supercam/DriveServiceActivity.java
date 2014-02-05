package tech.app.supercam;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
/**
 * This class obtains a Drive service and returns it
 * @author USER
 *
 */
public class DriveServiceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drive_service);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drive, menu);
		return true;
	}

}
