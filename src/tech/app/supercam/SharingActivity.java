package tech.app.supercam;

import java.util.LinkedList;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

/**
 * This class takes a Parcelable Array "paths" as an extra from intent
 * This class takes String "via" as extra from intent
 * - sms for SMS
 * - email for Email
 * - gmail for Gmail (under construction in onUploadFinished)
 * @author USER
 *
 */
public class SharingActivity extends Activity {

	final int ACCOUNT_CHOOSER_ACTIVITY=1;
	final int AUTH_EXCEPTION=2;
	final static int SHARE_EMAIL=3;
	final static int SHARE_SMS=4;
	final static int SHARE_GMAIL=5;
	int whichShare;
	RelativeLayout mrl;
	GenericImageChooserLayout chooserLayout;
	Uri[] paths;
	int w;
	int h;
	DisplayMetrics metrics;
	Drive service;
	GoogleAccountCredential credential;
	SharingActivityHandler uploadHandler;
	ProgressDialog dpd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mrl = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_sharing, null);
		setContentView(mrl);
		
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        w = size.x;
        h = size.y;
        
        credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE_FILE);
        startActivityForResult(credential.newChooseAccountIntent(),ACCOUNT_CHOOSER_ACTIVITY);
        
        metrics = getResources().getDisplayMetrics();
        
        uploadHandler = new SharingActivityHandler();
        
		Intent i = getIntent();
		Parcelable[] temp = i.getParcelableArrayExtra("paths");
		populatePaths(temp);
		
		String via = i.getStringExtra("via");
		if(via.equals("sms"))
			whichShare=SHARE_SMS;
		else if(via.equals("email"))
			whichShare=SHARE_EMAIL;
		else if(via.equals("gmail"))
			whichShare=SHARE_GMAIL;
		
		System.out.println("h:"+h);
		chooserLayout = new GenericImageChooserLayout(this,paths,(w-SizeManager.getDip(20, metrics))/2,h/4);
		mrl.addView(chooserLayout);
		RelativeLayout.LayoutParams clp = (LayoutParams) chooserLayout.getLayoutParams();
		clp.addRule(RelativeLayout.ABOVE,R.id.bottombutton);
		
		RelativeLayout sharebtnTemp = (RelativeLayout) getLayoutInflater().inflate(R.layout.bottombutton, mrl);
		Button share = (Button)sharebtnTemp.findViewById(R.id.bottombutton);
		sharebtnTemp.removeView(share);
		share.setBackgroundResource(R.drawable.creamrectbtnxml);
		share.setText("Share");
		share.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onSharePressed(chooserLayout.checked);
			}			
		});
		mrl.addView(share);
	}
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		if(requestCode==ACCOUNT_CHOOSER_ACTIVITY)
		{
			if(resultCode==RESULT_OK)
			{
				credential.setSelectedAccountName(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
				service = BackupActivity.getDriveService(credential);
			}
		}
	}
	//SHARE_GMAIL case under construction in onUploadFinished
	class SharingActivityHandler extends Handler
	{
		int uploaded;
		Uri[] uploadList;
		String[] downloadLinks;
		@Override
		public void handleMessage(Message msg)
		{
			Toast.makeText(getBaseContext(), "Got message", Toast.LENGTH_SHORT);
			if(msg.getData().containsKey("function"))
			{
				if(msg.getData().getString("function").equals("getSharingLink"))
				{
					if(msg.getData().containsKey("error"))
					{
						Toast.makeText(getBaseContext(), "Upload failed", Toast.LENGTH_LONG).show();
						stopUpload();
					}
					else
					{
						downloadLinks[uploaded-1]=msg.getData().getString("url");
						if(isUploadFinished())
						{
							onUploadFinished(downloadLinks);
						}
						else
						{
							uploadNext();
						}
					}
				}
			}
		}
		public void onUploadFinished(String[] downloadLinks)
		{
			dpd.dismiss();
			Intent i=null;
			switch(whichShare)
			{
			case SHARE_EMAIL:
				i = new Intent(getBaseContext(),EmailActivity.class);
				break;
			case SHARE_SMS:
				i = new Intent(getBaseContext(),SmsActivity.class);
				break;
			case SHARE_GMAIL:
				//UNDER CONSTRUCTION
				break;
			}
			if(i==null)
			{
				Log.d("SHARE","No client selected");
			}
			String body = getMessage(downloadLinks);
			i.putExtra("message", body);
			startActivity(i);
		}
		public String getMessage(String[] list)
		{
			String msg ="";
			for(int i=0;i<list.length;i++)
			{
				msg=msg+list[i]+"\n";
			}
			return msg;
		}
		public void stopUpload()//upload failed
		{
			uploaded=0;
			uploadList=null;
			downloadLinks=null;
			dpd.dismiss();
			showFailedDialog();
		}
		public void startUpload(Uri[] list)
		{
			uploadList=list;
			downloadLinks=new String[list.length];
			uploaded=0;
			initialiseProgressBar(list);
			dpd.show();
			uploadNext();
		}
		public boolean isUploadFinished()
		{
			if(uploaded>=uploadList.length)
				return true;
			return false;
		}
		public void uploadNext()
		{
			dpd.setProgress(uploaded);
			java.io.File iContent = new java.io.File(uploadList[uploaded].getEncodedPath());
			File header = new File();
			header.setTitle(iContent.getName());
			header.setMimeType("image/jpeg");
			
			FileContent content = new FileContent("image/jpeg",iContent);
			try{
			SharingManager.getSharingLink(header, content, service, uploadHandler);
			}
			catch(UserRecoverableAuthIOException e)
			{
				startActivityForResult(e.getIntent(),AUTH_EXCEPTION);
			}
			uploaded++;
		}
		public void showFailedDialog()
		{
			AlertDialog ad = new AlertDialog.Builder(SharingActivity.this).setMessage(R.string.upfail)
					.setPositiveButton("Ok", null).create();
			ad.show();
		}
	}
	public void initialiseProgressBar(Uri[] list)
	{
		getApplicationContext();//REMOVE!!!!!!
		dpd = new ProgressDialog(this);
		dpd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dpd.setTitle(R.string.uploading);
		dpd.setProgress(0);
		dpd.setMax(list.length);
		dpd.setCancelable(false);
		dpd.setCanceledOnTouchOutside(false);
	}
	public void onSharePressed(int checked[])
	{
		Uri[] cUris = getCheckedUri(checked);
		if(cUris.length==0)
		{
			Toast.makeText(this, "No pictures selected", Toast.LENGTH_LONG).show();
		}
		else
		{
			uploadHandler.startUpload(cUris);
		}
	}
	public Uri[] getCheckedUri(int checked[])
	{
		LinkedList<Uri> tlist = new LinkedList<Uri>();
		for(int i=0;i<chooserLayout.checked.length;i++)
		{
			if(checked[i]==1)
			{
				tlist.add(paths[i]);
			}
		}
		return tlist.toArray(new Uri[0]);
	}
	public void populatePaths(Parcelable[] f)
	{
		paths= new Uri[f.length];
		for(int i=0;i<paths.length;i++)
		{
			paths[i]=(Uri)f[i];
		}
	}
}
