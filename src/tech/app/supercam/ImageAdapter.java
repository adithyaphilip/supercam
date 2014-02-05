package tech.app.supercam;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter{
	private Context mContext;
	private java.io.File[] f;
	public ImageAdapter(Context c)
	{
		mContext = c;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv;
		if(convertView==null)
		{
			iv = new ImageView(mContext);
			iv.setLayoutParams(new GridView.LayoutParams(85,85));
			iv.setScaleType(ScaleType.CENTER_CROP);
		}
		else
		{
			iv = (ImageView)convertView;
		}
		iv.setImageBitmap(ImageModder.getMiniBitmap(85, 85, Uri.fromFile(f[position])));
		return iv;
	}

}
