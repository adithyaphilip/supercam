package tech.app.supercam;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class LaundryActivity extends Activity {
	int w,h;
	GenericImageChooserLayout il;
	RelativeLayout mrl;
	Button laundry;
	Intent resultIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		resultIntent = this.getIntent();
		setResult(RESULT_OK,resultIntent);
		
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        w = size.x;
        h = size.y;		
		
		mrl = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_laundry, null);
		setContentView(mrl);
		
		final Uri[] paths = LaundryManager.getLaundryFilePaths(PathFinder.dir);
		
		il = new GenericImageChooserLayout(this, paths, w/2, h/4);
		RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		il.setLayoutParams(ilp);
		ilp.addRule(RelativeLayout.BELOW,R.id.titleban);
		ilp.addRule(RelativeLayout.ABOVE,R.id.removelaundry);
		mrl.addView(il);
		
		laundry = (Button)findViewById(R.id.removelaundry);
		refreshList(paths);
	}
	public void refreshList(final Uri[] paths)
	{
		il.refreshLayout(paths);
		laundry.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				removeLaundry(paths,il.getChosen());
				refreshList(LaundryManager.getLaundryFilePaths(PathFinder.dir));
			}
		});	
	}
	public void removeLaundry(Uri[] paths, int[] chosen)
	{
		for(int i=0;i<paths.length;i++)
		{
			if(chosen[i]==1)//selected for removal
			{
				resultIntent.putExtra("deleted", true);
				File f = new File(paths[i].getEncodedPath());
				String num = f.getName().substring(0,f.getName().indexOf("."));
				LaundryManager.removeFromLaundryList(num);
			}
		}
	}
}
