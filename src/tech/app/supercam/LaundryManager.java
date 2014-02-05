package tech.app.supercam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import android.net.Uri;
import android.util.Log;


public class LaundryManager {
	public static void removeFromLaundryList(String num)
	{
		Uri dir = PathFinder.dir;
		String s = getLaundryString(dir);
		String newS = s.replace(num+"#", "");
		writeToLaundryFile(dir,newS);
	}
	public static Uri[] getLaundryFilePaths(Uri dir)
	{
		String s[] = getLaundryString(dir).split("#");
		if(s[0].equals(""))
			s = new String[0];
		Uri[] paths = new Uri[s.length];
		for(int i=0;i<s.length;i++)
		{
			paths[i] = Uri.fromFile(new File(PathFinder.dir.getEncodedPath()+"/"+s[i]+".t"));
		}
		return paths;
	}
	public static void addToLaundryList(Uri path)//imperative to ensure no duplicates due to working of onImageDelete function
	{
		File f= new File(path.getEncodedPath());
		int num = Integer.parseInt(f.getName().substring(0, f.getName().indexOf(".")));
		String s =getLaundryString(PathFinder.dir);
		if(s.indexOf(""+num)==-1)//not present
		{
			s+=num+"#";
		}
		writeToLaundryFile(PathFinder.dir,s);
	}
	public static String getLaundryString(Uri dir)
	{
		String s = "";
		Scanner reader = null;
		try {
			reader = new Scanner(new File(PathFinder.getLaundryFile(dir).getEncodedPath()));
		} catch (FileNotFoundException e) {
			Log.d("File not found:",PathFinder.getLaundryFile(dir).getEncodedPath());
		}
		while(reader.hasNext())
		{
			s += reader.nextLine();
		}
		reader.close();
		return s;
	}
	public static void writeToLaundryFile(Uri dir, String string) {
		Uri path = PathFinder.getLaundryFile(dir);
		FileWriter fw = null;
		try {
			fw = new FileWriter(path.getEncodedPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fw.write(string);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//imperative to ensure no duplicates for below function to work. Else "0.t" will get added
	public static void onImageDelete(Uri path) {
		File f= new File(path.getEncodedPath());
		
		int ele = Integer.parseInt(f.getName().substring(0,f.getName().indexOf(".")));
		String s = getLaundryString(PathFinder.dir);
		Log.d("onImageDelete - Laundry",s);
		String[] sp = s.split("#");
		int[] num = new int[sp.length];
		if(sp[0]!="")
		{
			
			for(int i =0;i<sp.length;i++)
			{
				num[i]=Integer.parseInt(sp[i]);
			}
			Arrays.sort(num);
			int[] newnum = new int[num.length-1];
			if(Arrays.binarySearch(num, ele)<0)//nothing to remove
				newnum = new int[num.length];
			
			for(int i=0,j=0;i<num.length;i++,j++)
			{
				if(num[i]>ele)
				{
					newnum[j]=num[i]-1;
				}
				else if(num[i]==ele)
				{
					j--;
				}
				else
				{
					newnum[j]=num[i];
				}
			}
			s ="";
			for(int n:newnum)
			{
				s+=n+"#";
			}
			writeToLaundryFile(PathFinder.dir,s);
		}
	}
}
