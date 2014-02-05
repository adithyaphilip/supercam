package tech.app.supercam;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import android.net.Uri;
import android.util.Log;

public class PathFinder {
	/*This class serves as the handle to the filesystem for the application. All file 
	 * manipulation is performed by this class. Every image taken is stored as num.t, 
	 * where num is an integer. Pictures are filled in serial order Eg:-1.t,2.t, 3.t and so on.
	 * Corresponding tag files of a picture are 1.t1, 1.t2 2.t1, 2.t2, 3.t1, 3.t2 and so on. 
	 * The file t1 holds the “What” tag while t2 holds the “color” tag. When a file is deleted, 
	 * the files are rearranged so that there is no gap in the number of the file names. 
	 * The various functions in this class handle the organisation of files, deletion of an image,
	 *  creation of a file, sorting of files and the like. The function names explain what each 
	 *  function does. Most functions require an image path (a Uri object) to be passed to it.*/
	
	/*The usage of the serial order helps the application to order pictures according
	to the date they were taken without using another details file*/
	
	/*If file ends with:
	 * .t - Image
	 * .t1 (or) .t2 - Tag file of an image for tagset 1 and 2 respectively
	 * .ts - Tagset file
	 * .td - dates file
	 */
	final static String helpfile = "/help.th";
	final static String datesfile = "/dates.td";
	final static String laundryfile = "/laundry.tl";
	static Uri dir;
	public static Uri getNextPath(Uri directory)
	{//gets path to save new image
		renewList(directory);//renews the list i.e rearranges in case of image delete causing a gap in the serial order of the images
		renewAllTags(directory);
		File[] fl = getFileList(directory);//gets total list of image files present in directory
		Uri newUri = Uri.parse(directory.toString()+"/"+fl.length+".t");//gets path of new image
		return newUri;
	}
	public static void renewList(Uri directory)
	{//A file's position in the array is the same as its name if all the images are correctly arranged
		clearBackup();
		File flist[] = getFileList(directory);
		for(int i =0;i<flist.length;i++)
		{
			if(getIntName(flist[i])!=i)
			{//if its position is not the same as its name, an image has been deleted/is missing and so the next image is renamed
				//rename code below
				File newPath = new File(flist[i].getAbsolutePath().replaceAll(flist[i].getName(), i+".t"));
				flist[i].renameTo(newPath);
			}
		}
	}
	public static String getFileName(File f)
	{//just gets the name of any file - image, tag or tagset
		return f.getName().substring(0,f.getName().indexOf(".t"));
	}
	public static String getFileName(Uri path)
	{//just gets the name of any file - image, tag or tagset - using a Uri as a parameter
		File f = new File(path.getEncodedPath());
		return f.getName().substring(0,f.getName().indexOf(".t"));
	}
	public static int getIntName(File f)
	{//gets the integer name of the file Eg:- 1.t or 1.t1 or 1.t2 returns 1
		return Integer.parseInt(f.getName().substring(0,f.getName().indexOf(".t")));
	}
	public static int getIntName(Uri path)
	{//same as above function, except Uri is passed a parameter
		File f = new File(path.getEncodedPath());
		return getIntName(f);
	}
	public static File[] getFileList(Uri directory)
	{//Retrieves all files which are images
		clearBackup();
		FileFilter filefilter = new FileFilter(){
			public boolean accept(File f)
			{//the filfilter ensures only image files are picked using the endWith(.t) check
				if(f.getName().endsWith(".t"))
				{
					return true;
				}
				else
					return false;
			}
			//if this function returns true, the file will be added to the filelist, else not
		};
		File[] fl= new File(directory.getEncodedPath()).listFiles(filefilter);//filefilter filters and returns only image files
		sortList(fl);
		return fl;
	}
	public static void sortList(File[] flist)//Ascending order using selection sort
	{
		if(flist.length>0)
		{
			for(int i =0;i<flist.length-1;i++)
			{
				File temp = flist[i];
				int index = i; //index of position of smallest(which is the temp file)
				for(int j = i+1;j<flist.length;j++)
				{
					if(getIntName(temp)>getIntName(flist[j]))
					{
						temp = flist[j];//reassigns the smallest file
						index = j;
					}
				}
				flist[index]=flist[i];//rearranges teh files in the array
				flist[i]=temp;
			}
		}
	}
	public static Uri getDirectory(Uri path)//Returns teh parent directory of the supplied Uri
	{//Remember to check if new File(path.toString()) works - CHECKED: It does not work. Files are to be declared using system-specific encoded url
		File f = new File(path.getEncodedPath());
		Uri d = path;
		//Uri d = Uri.parse(s.substring(0,s.indexOf(getFileName(path)))); // try with -1 in second argument if error - you may ignore this
		if(f.getName().contains("."))
		{
			d = Uri.parse(f.getParent());
		}
		else//if the supplied Uri is not a file, it must be a directory. Hence no change required.
		{
			d = path;
		}
		return d;
	}
	public static boolean deleteImage(Uri path)
	{
		File f = new File(path.getEncodedPath());
		DateManager.deleteImageDates(path);//deletes the records of teh dates when this image was worn
		LaundryManager.onImageDelete(path);
		//deletes both tags of the image
		deleteTag(path,1);
		deleteTag(path,2);		
		boolean result = f.delete();//if delete was successful, result will be true
		if(result)
			renewList(getDirectory(path));//renews the imagelist
		return result;
	}
	public static boolean deleteTag(Uri path, int num)
	{
		File f = new File(TagManager.getTagPath(path, num).getEncodedPath());
		boolean result =  f.delete();
		renewTagList(getDirectory(path),num);//renews the tag list
		return result;
	}
	public static void renewTagList(Uri d, int num)
	{
		//Uses the same logic as the renewImageList, except for image tags
		File[] flist = getTagFileList(d,num);
		for(int i =0;i<flist.length;i++)
		{
			if(getIntName(flist[i])!=i)
			{
				//renames the files if necessary 
				Uri npath = Uri.parse(dir.toString()+"/"+i+".t");
				File newPath = new File(TagManager.getTagPath(npath, num).getEncodedPath());
				flist[i].renameTo(newPath);
			}
		}
	}
	public static File[] getTagFileList(Uri d, final int num)
	{//same as getFileList for images, this function is for tags
		clearBackup();
		FileFilter filter = new FileFilter(){
			public boolean accept(File f)
			{
				if(f.getName().endsWith(".t"+num))//filefilter identifiees tag files, i.e 1.t1 or 3.t2 etc.
				{
					return true;
				}
				else
					return false;
			}
		};
		File f = new File(d.getEncodedPath());
		File[] flist = null;
		sortList(flist = f.listFiles(filter));//Sorts the list same as an image list
		return flist;
	}
	public static void renewAllTags(Uri path)
	{//convenience function
		renewTagList(path, 1);
		renewTagList(path, 2);
	}
	public static boolean isTagSetPresent(Uri directory)
	{
		Uri path = getDirectory(directory);//in case not directory
		boolean result = false;
		if(new File(TagManager.getTagSetPath(path, 1).getEncodedPath()).exists())//checks if the tagset file exists
			result = true;
		return result;
	}
	public static boolean emptyDirectory(Uri directory)
	{//cleans out the directory (images, dates, tags, tagsets) to revert to a clean-install state
		boolean result=false;
		File d= new File(directory.getEncodedPath());
		File f[] = d.listFiles();
		for(int i =0;i<f.length;i++)
		{
			f[i].delete();
		}
		return result;
	}
	public static boolean createDatesFile(Uri directory)
	{//Creates a file to store the dates of the images worn
		boolean result = false;
		Uri path = Uri.parse(directory.toString()+datesfile);//datesfile is defined at the start
		File f = new File(path.getEncodedPath());
		try {
			result = f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(result)//new file has been created
		{
			DateManager.writeToDatesFile(dir, " ");//so that datesfile isnt empty
		}
		return result;
	}
	public static Uri getDatesFile(Uri directory)
	{
		Uri result = null;
		result = Uri.parse(directory.toString()+datesfile);
		return result;
	}
	public static boolean createLaundryFile(Uri directory)
	{//Creates a file to store the dates of the images worn
		boolean result = false;
		Uri path = Uri.parse(directory.toString()+laundryfile);//datesfile is defined at the start
		File f = new File(path.getEncodedPath());
		try {
			result = f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(result)//new file has been created
		{
			LaundryManager.writeToLaundryFile(dir, "");//so that datesfile isnt empty
		}
		return result;
	}
	public static Uri getLaundryFile(Uri directory)
	{
		Uri result = null;
		result = Uri.parse(directory.toString()+laundryfile);
		return result;
	}
	public static Uri getImageFilePath(String name)
	{
		Uri path = Uri.parse(dir+"/"+name+".t");
		return path;
	}
	public static Uri getHelpFilePath()
	{
		Uri path = Uri.parse(dir+helpfile);
		return path;
	}
	public static String readFromFile(File f)
	{
		//simply reads the data inside a file and returns the corresponding string
		String s = "";
		BufferedReader br = null;
		try{
		br = new BufferedReader(new FileReader(f));
		s = br.readLine();
		Log.d("PathFinder",s);//testing purposes
		}catch(Exception e){
			Log.d("EXCEPTION!","failed readFromFile");
		}
		finally{
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	public static boolean writeFile(String s, File nf)
	{
		boolean flag= false;
		FileWriter writer = null;
		try {
			writer = new FileWriter(nf);
			writer.write(s);
			writer.close();
			flag=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	public static boolean writeFile(InputStream is, File nf)
	{
		boolean flag = true;
		FileOutputStream writer;
		BufferedOutputStream bs = null;
		try {
			writer = new FileOutputStream(nf);
			bs = new BufferedOutputStream(writer);
			for(;;)
			{
				int b = is.read();
				if(b!=-1)
					bs.write(b);
				else
					break;
			}
		} catch (IOException e) {
			flag=false;
			e.printStackTrace();
		}
		finally{
			try {
				bs.flush();
				bs.close();
			} catch (IOException e) {
				flag=false;
				e.printStackTrace();
			}
		}
		return flag;
	}
	public static boolean writeFile(File f, File nf)
	{
		FileInputStream is=null;
		try {
			is= new FileInputStream(nf);
		} catch (FileNotFoundException e) {
			Log.d("writeFile","filenotfound");
			e.printStackTrace();
		}
		boolean flag = writeFile(is,f);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	public static void clearBackup()
	{
		File b[] = getBackupFiles();
		for(int i =0;i<b.length;i++)
		{
			b[i].delete();
		}
	}
	public static void restoreBackup()
	{
		File b[] = getBackupFiles();
		Log.d("restoreBackup","length="+b.length);
		for(int i =0;i<b.length;i++)
		{
			String name = b[i].getName();
			String tname = name.replace("!#", "");
			Log.d("restoreBackup","replaced name:"+tname);
			File temp = new File(b[i].getPath().replace(name, tname));
			Log.d("restoreBackup","temp path:"+b[i].getPath().replace(name, tname));
			if(temp.exists())
			{
				temp.delete();
				Log.d("restoreBackup","file exists");
				System.out.println(b[i].renameTo(temp));
			}
			System.out.println(b[i].renameTo(temp));
			//writeFile(temp,b[i]);
		}
	}
	public static File[] getBackupFiles()
	{
		FileFilter filter = new FileFilter(){
			@Override
			public boolean accept(File test) {
				if(test.getName().startsWith("!#"))
					return true;
				return false;
			}};
			File f = new File(dir.getEncodedPath());
			File b[] = f.listFiles(filter);
			return b;
	}
}
