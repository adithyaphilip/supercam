package tech.app.supercam;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ColorDetector {
	/*Below are arbitrarily assigned int variable values. They indicate which color is which 
	 * through an int value.
	 */
	private static final int rval = 0;
	private static final int oval = 1;
	private static final int yval = 2;
	private static final int gval = 3;
	private static final int bval = 4;
	private static final int pval = 5;
	private static final int pival = 6;
	private static final int brval = 7;
	private static final int blval = 8;
	private static final int grval = 9;
	private static final int wval = 10;
	
	private static int red = 0;
	private static int blue =0;
	private static int green = 0;
	
	public static int detectPixelColor(int pixel)
	{
		float hsv[] = new float[3];//hsv[0] holds the hue value, hsv[1] holds saturation value, and hsv[2] holds the "Value" value
		int R = Color.red(pixel);
		int G = Color.green(pixel);
		int B = Color.blue(pixel);
		
		Color.colorToHSV(pixel, hsv);//Converts the rgb values of the picel to HSV, which is simpler to use to check for colors
		hsv[1] = hsv[1]*100;
		hsv[2] = hsv[2]*100;
		//Log.d("RGB",""+R+"/"+G+"/"+B);
		//Log.d("pixelhsv",""+hsv[0]+"/"+hsv[1]+"/"+hsv[2]);
		if(isBlack(hsv))
			return blval;
		if(isGray(hsv))
			return grval;
		if(isWhite(hsv))
			return wval;
		if(isRed(hsv))
			return rval;
		if(isOrange(hsv))
			return oval;
		if(isYellow(hsv))
			return yval;
		if(isGreen(hsv))
			return gval;
		if(isBlue(hsv))
			return bval;
		if(isPurple(hsv))
			return pval;
		if(isPink(hsv))
			return pival;
		if(isBrown(hsv))
			return brval;
		else return -1;//no color detected
	}
	private static boolean isBrown(float[] hsv) {
		if(hsv[0]<60&&hsv[0]>=0)
			return true;
		return false;
	}
	private static boolean isPink(float[] hsv) {
		if(hsv[0]<340&&hsv[0]>289)
			return true;
		return false;
	}
	private static boolean isPurple(float[] hsv) {
		if(hsv[0]<290&&hsv[0]>253)
			return true;
		return false;
	}
	private static boolean isBlue(float[] hsv) {
		if(hsv[0]<254&&hsv[0]>174)
			return true;
		return false;
	}
	private static boolean isGreen(float[] hsv) {
		if(hsv[0]<175&&hsv[0]>65)
			return true;
		return false;
	}
	private static boolean isYellow(float[] hsv) {
		if(hsv[0]<66&&hsv[0]>43&&hsv[2]>30)
			return true;
		return false;
	}
	private static boolean isOrange(float[] hsv) {
		if(hsv[0]<44&&hsv[0]>21&&hsv[2]>30)
			return true;
		return false;
	}
	public static void determineColor(final Bitmap bm, final Handler mHandler)
	{
		Thread t = new Thread(new Runnable(){
			@Override
			public void run()
			{
				String color = determineColor(bm);
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("function", "determineColor");
				b.putString("color", color);
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		});
		t.start();
	}
	public static String determineColor(Bitmap bm)
	{
		String color = "";
		int colors[] = new int[11];
		//The for loop below runs through all the pixels of the bitmap
		for(int i=0;i<bm.getHeight();i++)
		{
			for(int j = 0; j<bm.getWidth();j++)
			{
				int pixel = bm.getPixel(j, i);
				int index = detectPixelColor(pixel);
				//Log.d("val",""+index);
				if(index!=-1)
				colors[index]++;
				//rgbDetect() will return index of color with least deviation
				//we increase the score of that color
			}
		}
		int lindex = 0;//finds the index of color with largest score
		for(int i =1;i<colors.length;i++)
		{
			if(colors[lindex]<colors[i])
				lindex = i;
		}
		switch(lindex)
		{
		case rval:
		{
			color = "Red";
			break;
		}
		case oval:
		{
			color = "Orange";
			break;
		}
		case yval:
		{
			color = "Yellow";
			break;
		}
		case gval:
		{
			color="Green";
			break;
		}
		case bval:
		{
			color = "Blue";
			break;
		}
		case pval:
		{
			color = "Purple";
			break;
		}
		case pival:
		{
			color = "Pink";
			break;
		}
		case brval:
		{
			color = "Brown";
			break;
		}
		case blval:
		{
			color = "Black";
			break;
		}
		case grval:
		{
			color = "Grey";
			break;
		}
		case wval:
		{
			color = "White";
			break;
		}
		}
		return color;
	}
	public static int rgbDetect(int pixel)
	{		
		int colors[] = new int[11];//This array is used to measure the deviation of each color
		
		 getDeviation(pixel,colors);
		 return smallestIndex(colors);//returns index of color with least deviation
	}
	public static void getDeviation(int pixel, int colors[])
	{
		red = Color.red(pixel);
		green = Color.green(pixel);
		blue = Color.blue(pixel);
		Log.d("RGB",""+red+"/"+green+"/"+blue);
		colors[0] = getRedDeviation(pixel);
		Log.d("red",""+colors[0]);
		colors[1] = getOrangeDeviation(pixel);
		Log.d("orange",""+colors[1]);;
		colors[2] = getYellowDeviation(pixel);
		Log.d("yellow",""+colors[2]);
		colors[3] = getGreenDeviation(pixel);
		Log.d("green",""+colors[3]);
		colors[4] = getBlueDeviation(pixel);
		Log.d("blue",""+colors[4]);
		colors[5] = getPurpleDeviation(pixel);
		colors[6] = getPinkDeviation(pixel);
		colors[7] = getBrownDeviation(pixel);
		colors[8] = getBlackDeviation(pixel);
		Log.d("black",""+colors[8]);
		colors[9] = getGreyDeviation(pixel);
		Log.d("grey",""+colors[9]);
		colors[10] = getWhiteDeviation(pixel);
	}
	public static int getRedDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((255-red)*0.299, 2)+Math.pow((0-green)*0.587, 2)+Math.pow((0-blue)*0.114, 2));
		return deviation;
	}
	public static int getOrangeDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((255-red)*0.299, 2)+Math.pow((127-green)*0.587, 2)+Math.pow((0-blue)*0.114, 2));
		return deviation;
	}
	public static int getYellowDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((255-red)*0.299, 2)+Math.pow((255-green)*0.587, 2)+Math.pow((0-blue)*0.114, 2));
		return deviation;
	}
	public static int getGreenDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((0-red)*0.299, 2)+Math.pow((255-green)*0.587, 2)+Math.pow((0-blue)*0.114, 2));
		return deviation;
	}
	public static int getBlueDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((0-red)*0.299, 2)+Math.pow((0-green)*0.587, 2)+Math.pow((255-blue)*0.114, 2));
		return deviation;
	}
	public static int getPurpleDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((128-red)*0.299, 2)+Math.pow((0-green)*0.587, 2)+Math.pow((128-blue)*0.114, 2));
		return deviation;
	}
	public static int getPinkDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((255-red)*0.299, 2)+Math.pow((192-green)*0.587, 2)+Math.pow((203-blue)*0.114, 2));
		return deviation;
	}
	public static int getBrownDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((0-red)*0.299, 2)+Math.pow((0-green)*0.587, 2)+Math.pow((255-blue)*0.114, 2));
		return deviation;
	}
	public static int getBlackDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((red)*0.299, 2)+Math.pow((green)*0.587, 2)+Math.pow((blue)*0.114, 2));
		return deviation;
	}
	public static int getGreyDeviation(int pixel)
	{
		int deviation = (int) (Math.pow((128-red)*0.299, 2)+Math.pow((128-green)*0.587, 2)+Math.pow((128-blue)*0.114, 2));
		return deviation;
	}
	public static int getWhiteDeviation(int pixel)
	{//test performance by making it 255+255+255
		int deviation = (int) (Math.pow((255-red)*0.299, 2)+Math.pow((255-green)*0.587, 2)+Math.pow((255-blue)*0.114, 2));
		return deviation;
	}
	public static int smallestIndex(int arr[])//find index of smallest value in array
	{
		int index =0;
		for(int i =1;i<arr.length;i++)
		{
			if(arr[i]<arr[index])
			{
				index=i;
			}
		}
		return index;
	}
	public static int largestIndex(int arr[])//find index of smallest value in array
	{
		int index =0;
		for(int i =1;i<arr.length;i++)
		{
			if(arr[i]>arr[index])
			{
				index=i;
			}
		}
		return index;
	}
	
	public static boolean isBlack(float hsv[])
	{
		if(hsv[2]<2)
			return true;
		else if(hsv[2]<3 && hsv[0]<51)
			return true;
		else if(hsv[2]<3&&hsv[1]<20)
			return true;
		else
			return false;
	}
	public static boolean isGray(float hsv[])
	{
		if(hsv[2]<16&&hsv[1]<30)
			return true;
		else
			return false;
	}
	public static boolean isWhite(float hsv[])
	{
		if(hsv[2]>90&&hsv[1]<35)
			return true;
		if(hsv[2]>86&&hsv[1]<25)
			return true;
		if(hsv[2]>90&&hsv[1]<20)
			return true;
		else 
			return false;
	}
	public static boolean isRed(float hsv[])
	{
		if(hsv[0]>=0&&hsv[0]<45&&hsv[1]>30)
			return true;
		return false;
	}
}
