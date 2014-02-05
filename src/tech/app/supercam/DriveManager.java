package tech.app.supercam;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

public class DriveManager {
	final static int USER_RECOVERABLE_AUTH_IO_EXCEPTION = 1223112;
	final static int GET_FILES_NAMED = 1999211;
	final static int GET_FOLDERS_NAMED = 192330;
	final static int DELETE_FILE = 992112;
	public static void uploadFile(final File parent,final File header, final FileContent content,final Drive service, final Handler mHandler)
	{
		//WORK TO BE DONE!!!!
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("function", "uploadFile");
				List<ParentReference> lpr = new ArrayList<ParentReference>();
				ParentReference pr= new ParentReference();
				pr.setId(null);
				File nHeader = header;
				if(parent!=null)
				{
					pr.setId(parent.getId());
					lpr.add(pr);
					nHeader.setParents(lpr);
				}
				File temp=null;
				try {
					temp = service.files().insert(nHeader, content).execute();
				} catch (IOException e) {
					System.out.println("fail");
					e.printStackTrace();
				}
				if(temp!=null)
				{
					b.putString("fileid", temp.getId());
					b.putString("success","success");
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
				else
				{
					b.putString("error", "failed");
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	public static void createScDirectory(final Drive service,final Handler mHandler)
	{
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				File body = new File();
				body.setTitle("!#SC!#");
				body.setMimeType("application/vnd.google-apps.folder");
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("function", "createScDirectory");
				File t= null;
				try {
					t = service.files().insert(body).execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(t==null)
				{
					b.putString("error", "failed");
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
				else
				{
					b.putString("success", "success");
					msg.obj = t;
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	public static void getFoldersNamed(final String name, final Drive service, final Handler mHandler)
	{
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				Bundle b = new Bundle();
				b.putString("function","getFoldersNamed");
				Message msg = mHandler.obtainMessage();
				msg.setData(b);
				String q = "mimeType='application/vnd.google-apps.folder' and trashed = false and title='"+name+"'";
				FileList fl = null;
				Files.List request = null;
				List<File> lf = new ArrayList<File>();
				try {
					request = service.files().list().setQ(q);
				}
					catch (IOException e1) {
				
					e1.printStackTrace();
				}
				do{
					try {
						fl=request.execute();
						lf.addAll(fl.getItems());
					request.setPageToken(fl.getNextPageToken());
					} catch(UserRecoverableAuthIOException e1)
					{
						b.putInt("error", USER_RECOVERABLE_AUTH_IO_EXCEPTION);
						msg.obj = e1;
						Log.d("thread","User recoverable error");
						mHandler.sendMessage(msg);
						break;
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}while(request.getPageToken()!=null&&request.getPageToken().length()>0);
				Log.d("reached","about to send proper message");
				if(!b.containsKey("error"))
				{	msg.obj = lf;
				mHandler.sendMessage(msg);
				}
			}
		}
		).start();
	}
	
	public static String[] getNamesOfFiles(List<File> lf)
	{
		String[] result = new String[lf.size()];
		for(int i =0;i<result.length;i++)
		{
			result[i] = lf.get(i).getTitle();
		}
		return result;
	}
	public static List<File> getFilesNamed(String name, List<File> lf)
	{
		List<File> files = new ArrayList<File>();//why warning. any effect?
		for(int i =0;i<lf.size();i++)
		{
			if(lf.get(i).getTitle().equals(name))
				files.add(lf.get(i));
		}
		return files;
	}
	public static List<String> getIdsOf(String name, List<File> lf)
	{
		List<String> ids = new ArrayList<String>();//why warning. any effect?
		for(int i =0;i<lf.size();i++)
		{
			if(lf.get(i).getTitle().equals(name))
				ids.add(lf.get(i).getId());
		}
		return ids;
	}
	/** Returns content of given file using handler 
	 * dl is the file to download*/
	public static void downloadContent(final File dl, final Drive service, final Handler mHandler)
	{
		Thread t = new Thread(new Runnable(){
			@Override
			public void run()
			{
				Handler tHandler = new Handler(){
					@Override
					public void handleMessage(Message msg)
					{
						if(msg.getData().containsKey("function"))
						{
							if(msg.getData().getString("function").equals("readFromNetworkStream"))
							{
								Log.d("downloadFile","read from network stream");
								Message rmsg = mHandler.obtainMessage();
								Bundle b = new Bundle();
								b.putString("function", "downloadContent");
								b.putString("content", msg.getData().getString("content"));
								rmsg.setData(b);
								mHandler.sendMessage(rmsg);
							}
						}
					}
				};
				try {
					HttpResponse hr = service.getRequestFactory().buildGetRequest(new GenericUrl(dl.getDownloadUrl())).execute();
					readFromNetworkStream(hr.getContent(),tHandler);
				} catch (IOException e) {
					Message rmsg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("function", "downloadContent");
					b.putString("error", "failed");
					rmsg.setData(b);
					mHandler.sendMessage(rmsg);
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	public static void downloadFile(final File dl, final java.io.File save,final Drive service, final Handler mHandler)
	{
		Thread t = new Thread(new Runnable(){
			@Override
			public void run()
			{
				Looper.prepare();
				Handler tHandler = new Handler(){
					@Override
					public void handleMessage(Message msg)
					{
						if(msg.getData().containsKey("function"))
						{
							if(msg.getData().getString("function").equals("readFromNetworkStream"))
							{
								Log.d("downloadFile","read from network stream");
								Message rmsg = mHandler.obtainMessage();
								Bundle b = new Bundle();
								b.putString("function", "downloadFile");
								b.putString("content", msg.getData().getString("content"));
								rmsg.setData(b);
								mHandler.sendMessage(rmsg);
								PathFinder.writeFile(msg.getData().getString("content"), save);
								
							}
						}
					}
				};
				try {
					Log.d("downloadFile","start download");
					HttpResponse hr = service.getRequestFactory().buildGetRequest(new GenericUrl(dl.getDownloadUrl())).execute();
					Log.d("downloadFile","got stream");

					FileOutputStream fs = new FileOutputStream(save);
					BufferedOutputStream bs = new BufferedOutputStream(fs);
					
					for(;;)
					{
						int b = hr.getContent().read();
						//Log.d("downloadFile","read: "+b);
						if(b==-1)
							break;
						bs.write(b);
					}
					bs.flush();
					bs.close();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("function", "downloadFile");
					msg.setData(b);
					mHandler.sendMessage(msg);
				} catch (IOException e) {
					Message rmsg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("function", "downloadFile");
					b.putString("error", "failed");
					rmsg.setData(b);
					mHandler.sendMessage(rmsg);
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	public static void readFromNetworkStream(final InputStream is, final Handler mHandler)
	{
		Thread t = new Thread(new Runnable(){
			@Override
			public void run()
			{
				Log.d("readFromNetworkStream","entered");
				Bundle b = new Bundle();
				b.putString("function", "readFromNetworkStream");
				try {
					b.putString("contents",""+is.read());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = mHandler.obtainMessage();
				msg.setData(b);
				mHandler.sendMessage(msg);
				Log.d("readFromNetworkStream","read and sent");
			}
		});
		t.start();
	}
	public static void trimArray(Object[] obj)
	{
		
	}
	public static Drive getDriveService(GoogleAccountCredential credential) {
	    return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
	        .build();
	  }
	public static void deleteFile(final File f, final Handler mHandler, final Drive service)
	{
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				Bundle b = new Bundle();
				b.putString("function", "deleteFile");
				Message msg = mHandler.obtainMessage();
				try{
					service.files().delete(f.getId()).execute();
					b.putString("toast", "Deleted successfully");
				}
				catch(Exception e)
				{
					b.putString("error","failed");
				}
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}).start();
	}
	public static void getFilesIn(final File dir, final Drive service, final Handler mHandler)
	{
		Log.d("getFilesIn","entered");
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				Bundle b = new Bundle();
				b.putString("function","getFilesIn");
				Message msg = mHandler.obtainMessage();
				msg.setData(b);
				Log.d("dirtitle",dir.getId());
				String q = "\'"+dir.getId()+"\' in parents";
				FileList fl = null;
				Files.List request = null;
				List<File> lf = new ArrayList<File>();
				try {
					request = service.files().list().setQ(q);
				}
					catch (IOException e1) {
				
					e1.printStackTrace();
				}
				do{
					try {
						fl=request.execute();
						lf.addAll(fl.getItems());
					request.setPageToken(fl.getNextPageToken());
					} catch(UserRecoverableAuthIOException e1)
					{
						b.putInt("error", USER_RECOVERABLE_AUTH_IO_EXCEPTION);
						msg.obj = e1;
						Log.d("thread","User recoverable error");
						mHandler.sendMessage(msg);
						break;
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}while(request.getPageToken()!=null&&request.getPageToken().length()>0);
				Log.d("reached","about to send proper message");
				if(!b.containsKey("error"))
				{	
					File dl[] = new File[0];
					msg.obj = lf.toArray(dl);
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}
}