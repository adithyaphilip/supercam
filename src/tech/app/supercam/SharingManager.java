package tech.app.supercam;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Permissions;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

public class SharingManager {
public static void getSharingLink(final File header, final FileContent content, final Drive service, final Handler mHandler)throws UserRecoverableAuthIOException
{
	System.out.println("Started sharing");
	Thread t = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			Looper.prepare();
			final Looper l = Looper.myLooper();
			Handler lHandler = new Handler(){
				@Override
				public void handleMessage(Message msg)
				{
					if(msg.getData().getString("function").equals("uploadFile"))
					{
						Bundle b = new Bundle();
						b.putString("function", "getSharingLink");
						if(msg.getData().containsKey("success"))
						{
							
							//get download link
							Files fmgr = service.files();//to update metadata of file
							File f=null;
							try {
								f = fmgr.get(msg.getData().getString("fileid")).execute();	
								Permission permission = new Permission();
								permission.setValue("");
								permission.setType("anyone");
								permission.setRole("reader");
								service.permissions().insert(f.getId(), permission).execute();
								String url = f.getAlternateLink();
								System.out.println("Download link:"+url);
								b.putString("url", url);
							} catch (IOException e) {
								b.putString("error", "error");
								e.printStackTrace();
							}
						}
						else
						{
							b.putString("error", "error");
						}
						Message omsg = mHandler.obtainMessage();
						omsg.setData(b);
						mHandler.sendMessage(omsg);
						l.quit();
					}
				}
			};
			DriveManager.uploadFile(null, header, content, service, lHandler);
			System.out.println("First Upload Request sent");
			Looper.loop();
		}
	});
	t.start();
}
}

