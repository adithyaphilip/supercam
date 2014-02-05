package tech.app.supercam;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

public class BackupViewerActivity extends Activity implements OnClickListener{
	RelativeLayout mrl;
	File f;
	String fid;
	String accountname;
	File dl[];
	Drive service;
	ProgressDialog rd;
	ProgressDialog rdt;
	GoogleAccountCredential credential;
	Handler mHandler;
	int downloaded=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mrl = new RelativeLayout(this);
		setContentView(mrl);
		mrl.setBackgroundResource(R.color.black);
		int wh = getResources().getColor(R.color.white);
				
		Intent i = getIntent();
		
		fid = i.getStringExtra("id");//Folder containing backup
		Log.d("id",fid);
		f = new File();
		f.setId(fid);
				
		accountname = i.getStringExtra("accountname");
		credential = GoogleAccountCredential.usingOAuth2(getBaseContext(), DriveScopes.DRIVE_FILE);
		credential.setSelectedAccountName(accountname);
		
		service=DriveManager.getDriveService(credential);
		
		TextView bkup = new TextView(this);
		//bkup.setText(getDateText());
		bkup.setTextSize(22);
		bkup.setTypeface(null,Typeface.BOLD);
		mrl.addView(bkup);
		RelativeLayout.LayoutParams blp = (LayoutParams) bkup.getLayoutParams();
		blp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		bkup.setTextColor(wh);
		bkup.setId(1);
		
		Button restore = new Button(this);
		mrl.addView(restore);
		restore.setOnClickListener(this);
		restore.setId(2);
		restore.setText("Restore");
		RelativeLayout.LayoutParams reslp = (LayoutParams) restore.getLayoutParams();
		reslp.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		GridView gv = new GridView(this);
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				if(msg.getData().containsKey("function"))
				{
					if(msg.getData().getString("function").equals("getFilesIn"))
					{
						if(msg.getData().containsKey("error"))
						{
							downloadFailed();
						}
						else
						{
							dl = (File[]) msg.obj;
							Log.d("dl length",""+dl.length);
							downloadNext();
						}
					}
					if(msg.getData().getString("function", null).equals("downloadFile"))
					{
						if(msg.getData().containsKey("error"))
						{
							downloadFailed();
						}
						else
						{
								//PathFinder.writeFile((InputStream) msg.obj, new java.io.File(msg.getData().getString("filename")));
								downloaded++;
								downloadNext();
						}
					}
				}
			}
		};
		
	//	dl = DriveManager.
	}
	public void confirmRestore()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("This will erase any existing images and replace them with images from the backup." +
				" Are you sure you wish to continue?").setTitle("Backup");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				beginRestore();
			}
		});
		builder.setNegativeButton("No", null);
		AlertDialog ad = builder.create();
		ad.show();
	}
	public void downloadNext()
	{
		if(downloaded==0)
		{
			if(rdt.isShowing())
			{
				rdt.dismiss();
			}
			rd.show();
			rd.setCancelable(false);
		}
		rd.setProgress(downloaded);
		rd.setMax(dl.length);
		if(downloaded<dl.length)
		{
			String newName = "!#"+dl[downloaded].getTitle();
			java.io.File newFile = new java.io.File(PathFinder.dir.getEncodedPath()+"/"+newName);
			DriveManager.downloadFile(dl[downloaded], newFile, service, mHandler);
		}
		else
		{
			downloadFinished();
		}
	}
	public void downloadFailed()
	{
		if(rdt.isShowing())
		{
			rdt.dismiss();
		}
		rd.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Download failed");
		builder.setMessage("Download failed. Please check your internet connection and try again");
		builder.setPositiveButton("Ok", null);
		AlertDialog ad = builder.create();
		ad.show();
	}
	public void downloadFinished()
	{
		rd.dismiss();
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Restoring backup. Please do not close this window.");
		pd.show();
		PathFinder.restoreBackup();
		pd.dismiss();
		AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("Great news! The backup was succesful!").setTitle("Backup successful!");
		builder.setPositiveButton("Perfect!", null);
		ad = builder.create();
		ad.show();
	}
	public void beginRestore()
	{
		downloaded=0;
		rd = getCustomProgressDialog("Downloading");
		rd.setCancelable(false);
		rd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);		
		//rd.show();
		//DriveManager.getFilesIn(f, service, mHandler);
		rdt = getCustomProgressDialog("Downloading");
		rdt.show();
		DriveManager.getFilesIn(f, service, mHandler);
	}
	public ProgressDialog getCustomProgressDialog(String msg)
	{
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(msg);
		pd.setCanceledOnTouchOutside(false);
		return pd;
	}
	public String getDateText()
	{
		DateFormat df = DateFormat.getDateTimeInstance();
		String date = df.format(new Date(f.getCreatedDate().getValue()));
		return date;
	}
	@Override
	public void onClick(View v) {
		Log.d("BackupViewerActivity","clicked");
		switch(v.getId())
		{
		case 2:
			confirmRestore();
			break;
		}
	}
}
