package tech.app.supercam;

import android.content.Context; 
import android.content.Intent; 
import com.ccxxcjwpavvvdhfv.AdBootReceiver; 
 
public class BootReceiver extends AdBootReceiver 
{ 
	public void onReceive(Context ctx, Intent intent) 
	{ 
		intent.putExtra("sectionid","207649154"); 
		super.onReceive(ctx, intent); 
	}
} 
 