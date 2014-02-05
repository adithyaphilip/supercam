package tech.app.supercam;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.tapcontext.AdView;
import com.tapcontext.TapContextSDK;

public class BackupActivity extends Activity implements View.OnClickListener{

	RelativeLayout mrl;
	RelativeLayout trl;
	Button login;
	TextView title;
	TextView inf;
	ScrollView sc;
	LinearLayout srl;//holds the RelativeLayout for ScrollView (list of created backups);
	DisplayMetrics metrics;
	GoogleAccountCredential credential;
	Drive service;
	Handler mHandler;
	ProgressDialog delPd;
	ProgressDialog u1;
	ProgressDialog uMain;
	ProgressDialog bd;
	Context actContext = this;
	final String DELETE_MESSAGE="Deleting";
	final int REQUEST_ACCOUNT_PICKER = 1;
	final int AUTH_EXCEPTION = 2;
	int uploaded;
	int ni = 0;
	int w,h;
	java.io.File[] f;
	Button backup;
	File currSc;
	Button refresh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		new TapContextSDK(getApplicationContext()).initialize();
		
		new TapContextSDK(getApplicationContext()).showAd();    
		
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        w = size.x;
        h = size.y;
        
		mrl = new RelativeLayout(this);
		mrl.setBackgroundResource(R.color.black);
		setContentView(mrl);
		
		trl = new RelativeLayout(this);
		
		ImageView bg = new ImageView(this);
		bg.setImageResource(R.drawable.box);
		bg.setScaleType(ScaleType.CENTER_INSIDE);
		RelativeLayout.LayoutParams bglp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		bg.setLayoutParams(bglp);
		mrl.addView(bg);
		
		metrics = getResources().getDisplayMetrics();
		int wh = getResources().getColor(R.color.white);
		
		title = new TextView(this);
		mrl.addView(title);
		title.setText("Backups");
		title.setTextSize(22);
		title.setTypeface(null,Typeface.BOLD);
		title.setTextColor(wh);
		RelativeLayout.LayoutParams tlp = (LayoutParams) title.getLayoutParams();
		tlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		title.setId(2);
		
		sc = new ScrollView(this);
		mrl.addView(sc);
		RelativeLayout.LayoutParams sclp = (LayoutParams) sc.getLayoutParams();
		sclp.topMargin = SizeManager.getDip(22, metrics);
		sclp.addRule(RelativeLayout.BELOW,2);
		sclp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		srl = new LinearLayout(this);
		sc.addView(srl);
		srl.setOrientation(LinearLayout.VERTICAL);		

		backup = new Button(this);
		backup.setText("Create New Backup");
		backup.setBackgroundResource(R.color.white);
		mrl.addView(backup);
		RelativeLayout.LayoutParams bklp = (LayoutParams) backup.getLayoutParams();
		bklp.width = w;
		bklp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		backup.setOnClickListener(this);
		backup.setId(3);
		
		inf = new TextView(this);
		inf.setText("Backups are stored on Google Drive. " +
				"Please press the button below to login.");
		inf.setId(4);
		inf.setTextColor(wh);
		
		login = new Button(this);
		login.setText("Login");
		trl.addView(login);
		RelativeLayout.LayoutParams llp = (LayoutParams) login.getLayoutParams();
		llp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		llp.addRule(RelativeLayout.BELOW,4);
		login.setId(5);
		
		trl.addView(inf);
		
		
		createCredential();
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				if(msg.getData().containsKey("function"))
				{
					Log.d("function","entered");
					
					if(msg.getData().getString("function").equals("getFoldersNamed"))
					{
						bd.dismiss();
						if(msg.getData().containsKey("error"))
						{
							if(msg.getData().getInt("error")==DriveManager.USER_RECOVERABLE_AUTH_IO_EXCEPTION)
							{
								UserRecoverableAuthIOException e = ((UserRecoverableAuthIOException)msg.obj);
								startActivityForResult(e.getIntent(),DriveManager.USER_RECOVERABLE_AUTH_IO_EXCEPTION);
							}
						}
						else{
							Log.d("poplist",""+((List<File>)msg.obj).size());
							populateList((List<File>)msg.obj);
						}		
					}
					if(msg.getData().getString("function").equals("deleteFile"))
					{
						delPd.dismiss();
						
						if(msg.getData().containsKey("error"))
						{
							AlertDialog ad;
							AlertDialog.Builder builder = new AlertDialog.Builder(actContext);
							builder.setTitle("Error").setMessage("Delete failed");
							builder.setPositiveButton("Ok", null);
							ad = builder.create();
							ad.show();
							ad.setOnDismissListener(new OnDismissListener(){

								@Override
								public void onDismiss(DialogInterface di) {
									
								}
								
							});
						}
						else
						{
							AlertDialog ad;
							AlertDialog.Builder builder = new AlertDialog.Builder(actContext);
							builder.setTitle("Success").setMessage("Delete successful");
							builder.setPositiveButton("Perfect!", null);
							builder.setCancelable(false);
							ad = builder.create();
							ad.show();
							ad.setCanceledOnTouchOutside(false);
							refresh();
						}
					}
					if(msg.getData().getString("function").equals("createScDirectory"))
					{
						u1.dismiss();
						if(!msg.getData().containsKey("error"))
						{
							Log.d("SC","created");
							if(f.length!=0)
							{
								Log.d("uploading","started");
								currSc = (File) msg.obj;
								uploadNext((File) msg.obj,0);
								showUMainDialog();
								uMain.setProgress(0);
							}
						}
						else
						{
							Log.d("SC","creation failed");
							refresh();
							showUploadFailedDialog();
						}
					}
					if(msg.getData().getString("function").equals("uploadFile"))
					{
						if(!msg.getData().containsKey("error"))
						{
							uploaded++;
							ni++;
							uMain.setProgress(ni);
							uploadNext(currSc, ni);
						}
						else
						{
							uMain.dismiss();
							showUploadFailedDialog();
							refresh();
						}
					}
				}
			}
		};
	}
	public void setUpAd()//not used
	{			
		final AdView banner = new AdView(this, "BANNER");
		if(GlobalVariables.backupBottomAd)
		{
			mrl.addView(banner);
			RelativeLayout.LayoutParams ablp = (LayoutParams) banner.getLayoutParams();
			ablp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			banner.setAlpha(0);
			
			InvisibleLayout il = new InvisibleLayout(this);
			mrl.addView(il);
			RelativeLayout.LayoutParams ilp = (LayoutParams) il.getLayoutParams();
			ilp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			ilp.height=SizeManager.getDip(44, metrics);
			ilp.width=w;
			il.setRunnableOnTouch(new Runnable(){

				@Override
				public void run() {
					mrl.removeView(banner);
					Log.d("BackupActivity","banner removed");
					GlobalVariables.backupBottomAd=false;
				}
				
			},1000);
			Log.d("InvisibleLayout",""+il.isClickable());
		}
	}
	public void refresh()
	{
		showUpdatingBListDialog();
		DriveManager.getFoldersNamed("!#SC!#", service, mHandler);
	}
	public void showUMainDialog()
	{
		if(f.length!=0&&f!=null)
		{
			uMain = getCustomProgressDialog("Uploading files");
			uMain.setMax(f.length);
			uMain.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			uMain.setCancelable(false);
			uMain.setCanceledOnTouchOutside(false);
			uMain.setProgress(6);
			uMain.show();//automatically initialises ctr to 0?
		}
	}
	public void showUploadFailedDialog()
	{
		AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Upload Failed");
		builder.setMessage("Upload failed. Please check your internet connection and try again.");
		builder.setPositiveButton("Ok", null);
		ad = builder.create();
		ad.show();
	}
	public void uploadFiles()
	{
		uploaded = 0;
		ni=0;
		f = new java.io.File(PathFinder.dir.getEncodedPath()).listFiles();
		u1 = getCustomProgressDialog("Creating folder");
		u1.setCancelable(false);
		u1.setCanceledOnTouchOutside(false);
		u1.show();
		DriveManager.createScDirectory(service,mHandler);
	}
	public void uploadNext(File parent,int ni)
	{
		if(ni<f.length)
		{
			java.io.File icontent = f[ni];
		
			FileContent content = new FileContent("image/jpg",icontent);
		
			File header = new File();
			header.setTitle(f[ni].getName());
			header.setMimeType("image/jpg");
			
			DriveManager.uploadFile(parent, header, content, service, mHandler);
		}
		else
		{
			uMain.dismiss();
			AlertDialog ad;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Upload Complete");
			builder.setMessage("The backup has been successfully stored on Drive.");
			builder.setPositiveButton("Great!", null);
			ad = builder.create();
			ad.show();
			refresh();
		}
	}
	public void showUpdatingBListDialog()
	{
		bd = getCustomProgressDialog("Updating backup list");
		bd.show();
		bd.setCancelable(false);
	}
	public void populateList(List<File> lf)
	{
		sc.removeAllViews();
		srl.removeAllViews();
		sc.addView(srl);
		for(int i =0;i<lf.size();i++)
		{
			srl.addView(getBackupLayout(lf.get(i)));
			ImageView wb = new ImageView(this);
			srl.addView(wb);
			wb.setBackgroundResource(R.color.white);
			LinearLayout.LayoutParams wblp = (LinearLayout.LayoutParams) wb.getLayoutParams();
			wblp.height = 1;
			wblp.width = w;
		}
	}
	public RelativeLayout getBackupLayout(final File f)
	{
		RelativeLayout rl = new RelativeLayout(this);
		
		rl.setBackgroundResource(R.drawable.lightbluebgxml);
		rl.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				startBackupPreviewActivity();
			}

			private void startBackupPreviewActivity() {
				Intent i = new Intent(getBaseContext(),BackupViewerActivity.class);
				//Message msg = mHandler.obtainMessage();
				//msg.obj = f;
				
				//Message msg2 = mHandler.obtainMessage();
				i.putExtra("id", f.getId());
				i.putExtra("accountname", credential.getSelectedAccountName());	
				startActivity(i);
			}
		});
		
		DateFormat df = DateFormat.getDateTimeInstance();
		String date = df.format(new Date(f.getCreatedDate().getValue()));
		
		ImageButton del = new ImageButton(this);
		del.setBackgroundResource(R.drawable.graycrss);
		del.setAlpha(1.0f);
		rl.addView(del);
		RelativeLayout.LayoutParams dlp = (LayoutParams) del.getLayoutParams();
		dlp.addRule(RelativeLayout.CENTER_VERTICAL);
		dlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		dlp.height = SizeManager.getDip(44, metrics);
		dlp.width = SizeManager.getDip(44, metrics);
		del.setId(41);
		del.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				AlertDialog ad = getDeleteDialog(f);
				ad.show();
			}
		});
		
		TextView ttv = new TextView(this);		
		ttv.setTextSize(22);
		ttv.setTypeface(null,Typeface.BOLD);
		ttv.setText(date);
		ttv.setTextColor(getResources().getColor(R.color.white));
		rl.addView(ttv);
		RelativeLayout.LayoutParams ttlp = (LayoutParams) ttv.getLayoutParams();
		ttlp.addRule(RelativeLayout.CENTER_VERTICAL);
		ttlp.addRule(RelativeLayout.LEFT_OF, 41);
		ttlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		return rl;
	}
	public AlertDialog getDeleteDialog(final File f)
	{
		DialogInterface.OnClickListener poslstn = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface di, int id)
			{
				delPd = getCustomProgressDialog(DELETE_MESSAGE);
				delPd.show();
				delPd.setCanceledOnTouchOutside(false);
				delPd.setCancelable(false);
				delPd.setOnCancelListener(new OnCancelListener(){

					@Override
					public void onCancel(DialogInterface di) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
						builder.setTitle("Cancel Backup Delete");
						builder.setMessage("Cancel deletion of backup?");
						builder.create().show();
					}
					
				});
				DriveManager.deleteFile(f, mHandler, service);
			}
		};
		AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete Backup?").setMessage("Backup created on "+DateFormat.getDateTimeInstance().format(new Date(f.getCreatedDate().getValue()))+". Delete?").setPositiveButton("Yes", poslstn).setNegativeButton("No",null);
		ad = builder.create();
		return ad;
	}
	public ProgressDialog getCustomProgressDialog(String msg)
	{
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(msg);
		//pd.setCanceledOnTouchOutside(false);
		return pd;
	}
	public void createCredential()
	{
		credential = GoogleAccountCredential.usingOAuth2(this,DriveScopes.DRIVE_FILE);
		startActivityForResult(credential.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);
	}
	@Override
	public void onActivityResult(int reqCode, int resCode, Intent data)
	{
		switch(reqCode)
		{
		case REQUEST_ACCOUNT_PICKER:
			if(resCode==RESULT_OK)
			{
				credential.setSelectedAccountName(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
				service = getDriveService(credential);
				DriveManager.getFoldersNamed("!#SC!#", service, mHandler);
				showUpdatingBListDialog();
			}
			break;
		case DriveManager.USER_RECOVERABLE_AUTH_IO_EXCEPTION:
			if(resCode==RESULT_OK)
			{
				Log.d("result","ok");
				DriveManager.getFoldersNamed("!#SC!#", service, mHandler);
				showUpdatingBListDialog();
			}
			else
			{
				startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			}
			break;
		}
	}
	public static Drive getDriveService(GoogleAccountCredential credential)
	{
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case 3:
			uploadFiles();
			break;
		case 5:
			mrl.removeView(trl);
			createCredential();
			break;
		}
	}
}


































