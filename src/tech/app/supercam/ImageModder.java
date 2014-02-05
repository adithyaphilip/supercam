package tech.app.supercam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;

public class ImageModder {
	public static void rotate(float x, Bitmap bitmapOrg, ImageView iv,int newWidth)
	{
		int width = bitmapOrg.getWidth();

		int height = bitmapOrg.getHeight();
		
		Matrix matrix = new Matrix();
		matrix.postRotate(x);

		bitmapOrg = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);
		
		iv.setImageBitmap(bitmapOrg);//the imageview displays the rotated image
	}
	public static void saveNewImage(Bitmap bm,Uri fr) throws FileNotFoundException{
		File file = new File(fr.getEncodedPath());		
		FileOutputStream out = new FileOutputStream(file);		
		bm.compress(CompressFormat.JPEG, 100, out);//writes the file, overwriting an existing file if present				
	}
	
	
	public static Bitmap getMiniBitmap(int reqWidth, int reqHeight, Uri path)
	{
		BitmapFactory.Options size = getRawSize(path);//gets just dimension of bitmap
		BitmapFactory.Options opt = new BitmapFactory.Options();
		if(reqWidth==0)
		{
			reqWidth = Math.round(size.outWidth/(float)size.outHeight*reqHeight);
		}
		else if(reqHeight==0)
		{
			reqHeight = Math.round(size.outHeight/(float)size.outWidth*reqWidth);
		}
		opt.inSampleSize=calcInSampleSize(size, reqWidth, reqHeight);
		Bitmap bm = BitmapFactory.decodeFile(path.getEncodedPath(),opt);//loads a scaled-down version of the bitmap into memory.
		//This was necessary as some phones may not allocate sufficient memory to load the entire image, and loading the full-sized image was not necessary
		return bm;
	}
	
	public static int calcInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{//Raw options using inJustDecodeBounds
		int rwHeight = options.outHeight;
		int rwWidth = options.outWidth;
		
		int inSampleSize=1;
		if(reqHeight!=rwHeight || reqWidth!=rwWidth)
		{
			int hRatio = Math.round(rwHeight/(float)reqHeight);
			int wRatio = Math.round(rwWidth/(float)reqWidth);
			inSampleSize = (hRatio>wRatio)?wRatio:hRatio; //returns whichever ratio is smaller
		}
		return inSampleSize;//Used to load a scaled-down image into memory
	}
	public static BitmapFactory.Options getRawSize(Uri path)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds=true;//does not load image, just loads dimensions
		Bitmap bm = BitmapFactory.decodeFile(path.getEncodedPath(),opt);//loads just dimensions due to above setting
		return opt;//returns the options which now contains the image dimensions
	}
	public static int getCorresponding(int realWidth, int realHeight, int width, int height)
	{
		//Gets corresponding value of height/width to mainatin aspect ratio
		//rheight, rwidth are real height and width
		int result = 0;
		if(width==0)
		{
			result = Math.round(realWidth/(float)realHeight*height);
		}
		else if (height==0)
		{
			result = Math.round(realHeight/(float)realWidth*width);
		}		
		return result;
	}
	public static boolean isPortrait(Uri path)
	{
		BitmapFactory.Options size = new BitmapFactory.Options();
		size.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path.getEncodedPath(),size);
		if(size.outHeight>size.outWidth)
			return true;
		else
			return false;
	}
}

