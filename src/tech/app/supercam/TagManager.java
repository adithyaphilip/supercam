package tech.app.supercam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.net.Uri;
import android.util.Log;

public class TagManager {

	/*This class in conjunction with the PathFinder class helps determine how and where 
	 * tags are to be saved. It encodes string arrays into our tagset format. Each tag is 
	 * separated from the next tag by a ‘#’. This class can interpret and read the tags into
	 *  a string array.*/
	
	public static String[] getTagSet(Uri path,int num)//num specifies which tag set to retrieve
	{
		String s = getTagSetString(path,num);
		int ctr = getNoOfTags(s);
		String[] tagset = new String[ctr];
		int beg=0, end=0;
		for(int i =0;i<ctr;i++)//interprets and reads the tagset file to retrieve available tags
		{
			end=s.indexOf("#",beg);
			tagset[i] = s.substring(beg,end);
			beg=end+1;
		}
		return tagset;//if null or length == 0 no content or failed reading tagset
	}
	public static String getTagSetString(Uri path, int num)
	{//path is path of an image or tag
		Uri p2 = getTagSetPath(path,num);
		File f = new File(p2.getEncodedPath());
		FileInputStream is = null;
		String s = null;
		byte b[] = new byte[4000];
		try{//just reads a file upto where null character is encountered
			is = new FileInputStream(f);
			is.read(b);
			s = new String(b);
			s=s.substring(0, s.indexOf(0));//s.indexOf(0) gives location of null character
		}catch(Exception e)
		{Log.e("fail", "failed reading", e);}
		finally{
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//NOTE:- Eventually I shall replace this function with a PathFinder function after ensuring there will be no consequent bugs
		return s;
	}
	public static int getNoOfTags(String s)//s is string extracted from tags file
	{
		int ctr=0;
		//determines number of tags by counting the number of "#" characters
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)=='#')
			ctr++;
		}
		return ctr;
	}
	public static String getTag(Uri path, int num)
	{
		//Returns the tag of an image, if present, corresponding to tagset num
		Uri p2 = getTagPath(path,num);//gets location of tag for that image
		File f = new File(p2.getEncodedPath());
		FileInputStream is = null;
		String s = null;
		byte[] b = new byte[100];
		try{
			is = new FileInputStream(f);
			is.read(b);
			s = new String(b);
			s = s.substring(0,s.indexOf(0));
		} catch(Exception e)
		{
			Log.d("EXCEPTION!","");
		}
		finally{try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		if(s==null)
			s="";
		return s;
		//NOTE:- Eventually I shall replace this function with a PathFinder function after ensuring there will be no consequent bugs
	}
	public static boolean writeTag(Uri path, String tag, int num)
	{		
		//path is Uri of image whose tag is being edited
		boolean result = false;
		path = getTagPath(path,num);//gets path of tag file to edit
		File f= new File(path.getEncodedPath());
		FileOutputStream fo = null;
		try{//simply writes the file to the storage device assigned to this app
			fo = new FileOutputStream(f);
			fo.write(tag.getBytes());
			result = true;
		}catch(Exception e){	}
		finally{
		try {
			fo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return result;//true if successful, false otherwise
	}
	public static boolean writeTagSet(String tags[], Uri path, int num)
	{//tags[] is the array of tags available to the user. Returns true if successful, false otherwise
		boolean result = false;
		path = getTagSetPath(path,num);
		File f = new File(path.getEncodedPath());
		FileOutputStream fo = null;
		try {//writes the tagset file
			fo = new FileOutputStream(f);
			for(int i =0;i<tags.length;i++)
			{//converts the tags[] array to the format used for storing tagsets
				fo.write((tags[i]+"#").getBytes());
			}			
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}		
		finally{
			try {
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	public static Uri getTagSetPath(Uri path, int num)
	{
		Uri d = PathFinder.getDirectory(path);
		path = Uri.parse(d.toString()+"/ts"+num+".tags");
		return path;
	}
	public static Uri getTagPath(Uri path, int num)
	{//Retrieves the path of the corresponding Tags file from the image path passed as an argument
		String s = path.toString(); //converts to string
		Uri p2 = Uri.parse(s+num);//Eg:- 1.t gets converted to 1.t1 for tag from tagset 1
		return p2;
	}

	public static void renewAllTags(Uri path)
	{//Convenience function
		PathFinder.renewTagList(path, 1);
		PathFinder.renewTagList(path, 2);
	}
	
	public static boolean addToTagSet(Uri path,int num,String tag)//path is image path
	{
		//adds a new Tag to a tagset (when the user presses the "Add Tag" button)
		boolean result = false;
		
		File f = new File(getTagSetPath(path,num).getEncodedPath());
		String s = getTagSetString(path,num);
		FileOutputStream fo = null;
		try{//writes new encoded tagset string
			fo = new FileOutputStream(f);
			fo.write((s+tag+"#").getBytes());//appends new tag to end of existing tagset string
			result = true;
		}catch (Exception e)
		{
			
		}
		finally{
			try {
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	public static boolean deleteFromTagSet(Uri path, int num, String tag)//path of image
	{//tag is tag to be removed from tagset
		boolean result = false;
		File f = new File(getTagSetPath(path,num).getEncodedPath());
		String s = getTagSetString(path,num);
		s=s.replace(tag+"#", "");//removes the given tag from the taglist
		FileOutputStream fo = null;
		try{//writes the new TagSet file
			fo = new FileOutputStream(f);
			fo.write(s.getBytes());
			result=true;
		}
		catch(Exception e)
		{
			
		}
		finally{
			try {
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
