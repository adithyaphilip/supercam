package tech.app.supercam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;

import android.net.Uri;
import android.util.Log;

public class DateManager {
	/*This class handles the Dates file. The Dates file maintains the list of dates on which
	 * images were worn. They are stored in a special format:
	 * ~DD!MM@YYYY#imgnum$ 
	 * Eg:- for image 1.t worn on January 1, 2013: ~1!0@2013#1$
	 * This class converts dates to this format, and decodes dates from this format
	 * This class forms the backbone of the datebox which uses DateManager to determine what
	 * pictures were worn on which day.
	 */
	public static Calendar[] getDatesWorn(Uri path)//path is Uri of image
	{//returns list of dates on which a given image was worn
		Calendar cl[] = new Calendar[countOfDatesWorn(path)];
		String s = getDatesString();
		String name = PathFinder.getFileName(path);
		int beg = 0;
		String temp ="";
		for(int i=0;i<cl.length;i++)
		{//Finds and extracts the date entry of a particular image
			temp = s.substring(beg,s.indexOf("#"+name+"$"));//finds index of imagename
			//gets encoded date
			String enc = s.substring(temp.lastIndexOf("$")+1,temp.indexOf(name));
			cl[i] = getDecodedDate(enc);//gets decoded date
			//sets beg to after the point where this image's first entry was found to look for further entries
			beg=s.indexOf(name)+name.length();
		}		
		return cl;
	}
	public static void addDateWorn(Uri path, Calendar c)
	{//adds an encoded string showing that an image was worn on a given date represented by Calendar c
		String s = getDatesString();
		String cnstrct = getEncodedDate(c)+PathFinder.getFileName(path)+"$";
		if(s.indexOf(cnstrct)==-1)//if an entry for this image does not already exist on this day, add to the datesfile
		{
			cnstrct = cnstrct.concat(s);//concatenates the new entry to the start of the datesstring
			writeToDatesFile(PathFinder.getDirectory(path),cnstrct);//writes the new datesfile to disk
		}
	}
	public static Uri[] getWornOnDate(Calendar c)
	{//gets list of images (their paths) worn on a particular date indicated by Calendar c
		Log.d("entered","getWornOnDate");
		Uri[] list;
		String enc = getEncodedDate(c);
		String s = getDatesString();
		list = new Uri[countOfWornOnDate(c)];
		Log.d("getWornOnDate",""+list.length);
		int beg =0;
		int end = 0;
		int lbeg = 0;
		for(int i =0;i<list.length;i++)
		{//extracts the encoded dates and image names and decodes them
			beg = s.indexOf(enc,lbeg)+enc.length();
			end = s.indexOf("$",beg);
			String temp = s.substring(beg,end);
			list[i] = PathFinder.getImageFilePath(temp);
			lbeg = end;
		}
		if(list.length!=0)
		Log.d("list[0]",""+list[0].toString());
		return list;
	}
	public static int countOfDatesWorn(Uri path)
	{//returns the number of dates an image has been worn
		int count = 0;
		String name = PathFinder.getFileName(path);
		String srchterm = Pattern.quote("#"+name+"$");//convert the string to a regular expression due to presence of $
		//in this case, this function works without Patter.quote() also. It is required only when the replace function is sued
		String s = getDatesString();
		int beg = 0;
		int lbeg = 0;
		for(;;count++)
		{//searches for entries
			Log.d("loop","countOfDatesWorn");
			beg = s.indexOf(srchterm,lbeg);
			if(beg==-1)
				break;
			lbeg = beg+srchterm.length();
		}
		return count;
	}
	public static int countOfWornOnDate(Calendar c)
	{
		//Returns number of dresses worn on a given date. Works similar to the countOfDatesWorn() function
		Log.d("enetered","countOfWornOnDate");
		String s = getDatesString();
		String enc = getEncodedDate(c);
		Log.d("enc",enc);
		int count = 0;
		int index =0;
		int lindex = 0;
		for(;;count++)
		{
			//counts number of times the encoded date string is present in the dates file string
			Log.d("loop","countOfWornOnDate");
			Log.d("s (count)",s);
			Log.d("countOfWornOnDate",""+count);
			index = s.indexOf(enc,lindex);
			Log.d("countOfWornOnDateIndex",""+index);
			if(index==-1)
				break;
			lindex = index+enc.length(); 
		}
		return count;
	}
	public static String getEncodedDate(Calendar c)
	{//Encodes a given date in the manner described at the start of this class
		String result = "";
		result="~"+c.get(Calendar.DAY_OF_MONTH)+"!"+c.get(Calendar.MONTH)+"@"+c.get(Calendar.YEAR)+"#";
		return result;
	}
	public static Calendar getDecodedDate(String enc)
	{//Decodes date from the format described above
		Calendar c = Calendar.getInstance();
		
		//extracts day from encoded string
		String day = enc.substring(enc.indexOf("~")+1, enc.indexOf("!"));
		int daynum = Integer.parseInt(day);
		
		//extracts month from encoded string
		String month = enc.substring(enc.indexOf("!")+1,enc.indexOf("@"));
		int monthnum = Integer.parseInt(month);
		
		//extracts year from encoded string
		String year = enc.substring(enc.indexOf("@")+1,enc.indexOf("#"));
		int yearnum = Integer.parseInt(year);
		
		//sets the calendar variable to the extracted dates
		c.set(Calendar.DAY_OF_MONTH, daynum);
		c.set(Calendar.MONTH, monthnum);
		c.set(Calendar.YEAR, yearnum);
		
		return c;
	}
	public static void deleteImageDates(Uri path)
	{
		/*/When an image is deleted, it becomes necessary to delete the image's date entries 
		 * in case the image is replaced by another image of the same name during re-assignment
		 * (to maintain serial order of image files as described in PathFinder class)*/
		Uri d =PathFinder.getDirectory(path);
		String s = getDatesString();
		String imgname ="#"+PathFinder.getFileName(path)+"$";
		for(;;)
		{
			//finds an entry
			int end = s.indexOf(imgname);
			if(end==-1)//if no entry of img is remaining, break from loop
				break;
			else
			{
				end=end+imgname.length();
				String sub = s.substring(0, end);
				int beg =sub.lastIndexOf("~");
				sub=s.substring(beg, end);
				Log.d("deletedsub",sub);
				s=s.replace(sub, "");
			}
			Log.d("deleted",s);			
		}
		writeToDatesFile(d,s);
		rearrangeDateListOnDelete(path);//rearranges the date list in the same manner files are rearranged
	}
	public static void writeToDatesFile(Uri directory,String s)
	{
		//Simple function to write a given string to the dates file
		File f= new File(PathFinder.getDatesFile(directory).getEncodedPath());
		FileOutputStream fo = null;
		try{
			fo = new FileOutputStream(f);
			fo.write(s.getBytes());
		}catch(Exception e)
		{
			
		}
		finally{
			try {
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static String getDatesString()
	{
		//Simple function to read the data in the dates file and return it as a String
		Uri directory = PathFinder.dir;
		String s ="";
		
		File f = new File(PathFinder.getDatesFile(directory).getEncodedPath());
		s = PathFinder.readFromFile(f);
		if(s.equals(null))
		{
			Log.d("STRING FAIL!","String null at readFromFile");
		}
		Log.d("datesstring",s);
		return s;
	}
	public static void rearrangeDateListOnDelete(Uri path)
	{//path of image deleted is passed as argument
		//used to rearrange the remaining image entries in the same way that image files are rearranged
		//this is necessary to keep the images and their date entries in sync with each other
		int delnum = PathFinder.getIntName(path); //number of deleted image Eg:- 1 for 1.t
		String s = getDatesString();
		File[] flist = PathFinder.getFileList(PathFinder.dir);
		Log.d("delnum",""+delnum);
		Log.d("flistlngth",""+flist.length);
		for(int i =delnum+1;i<flist.length+1;i++)//flist.length + 1 because this function might have been called after the image was deleted
		{/*replaces all occurrences of image numbers after the deleted image with the 
		 *corresponding new image number after the files have been rearranged*/
			String trm = Pattern.quote("#"+i+"$");//necessary as a regular expression is required as first parameter for String.replace() function
			Log.d("trm",trm);
			String rplc = "#"+(i-1)+"\\$";//the dollar sign has been escaped twice - once for java, another time for regex
			Log.d("rplc",rplc);
			s = s.replaceAll(trm, rplc);
			Log.d("replaced",s);
		}
		writeToDatesFile(PathFinder.dir,s);//writes the new dates file
	}
}
