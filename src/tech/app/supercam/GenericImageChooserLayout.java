package tech.app.supercam;

import java.util.LinkedList;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
/**
 * Accepts context, Uri[] paths, width and height and creates a GridView with checkboxes
 * The getChosen() function returns an int array of checked GridViews. 1 represents checked,
 * 0 unchecked
 * @author USER
 *
 */
public class GenericImageChooserLayout extends RelativeLayout {
	
	GridView gv;
	Uri[] paths;
	int checked[];
	ImageAdapter ivAdapter;
	int w;
	int h;
	public GenericImageChooserLayout(Context context, Uri[] paths, int w, int h) {
		super(context);
		this.paths = paths;
		checked = new int[paths.length];
		this.w=w;
		this.h=h;
		System.out.println(paths.length);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		gv = (GridView)inflater.inflate(R.layout.gridview, null);
		this.addView(gv);
		populateGridView();
		System.out.println("paths"+paths.length);
	}
	/**
	 * Performs actions required to populate the layout	 * 
	 */
	public void refreshLayout(Uri paths[])
	{
		this.paths=paths;
		checked = new int[paths.length];
		populateLayout();
	}
	public void populateLayout()
	{
		populateGridView();
	}
	/**
	 * Performs actions required to populate GridView object used by layout
	 */
	public void populateGridView()
	{
		ivAdapter = new ImageAdapter(paths,getContext(),w,h);
		gv.setAdapter(ivAdapter);
		gv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				((CheckBoxLayout)v).ch.toggle();
				System.out.println("clicked");
				if(checked[pos]==0)
				checked[pos]=1;
				else
				checked[pos]=0;
			}			
		});
	}
	/**
	 * Returns an array containing positions of chosen images
	 * @return array of chosen image positions
	 */
	public int[] getChosen()
	{
		return checked;
	}
	class ImageAdapter extends BaseAdapter
	{
		Uri[] paths;
		Context context;
		int w,h;
		public ImageAdapter(Uri[] paths,Context context, int w, int h) {
			//this.checked=checked;
			System.out.println("W: "+w + " H: "+h);
			this.paths = paths;
			System.out.println("paths length: "+this.paths.length);
			this.context=context;
			this.w=w;
			this.h=h;
		}

		@Override
		public int getCount() {
			return paths.length;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//ImageView temp = new ImageView(context);
			//temp.setImageResource(R.drawable.bbtn);
			System.out.println("Position :"+position+" Uri: "+paths[position].getEncodedPath()+ " W " + w +" H " + h);
			if(convertView==null)//set it up
			{
				CheckBoxLayout trl = getCheckBoxLayout();
				trl.iv.setImageBitmap((ImageModder.getMiniBitmap(0, h, paths[position])));
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(w,h);//Why?
				trl.setPadding(10, 10, 10, 10);
				trl.setLayoutParams(lp);
				convertView = trl;
			}
			else
			{
				((CheckBoxLayout)convertView).iv.setImageBitmap((ImageModder.getMiniBitmap(0, h, paths[position])));
			}
			if(isChecked(position))
				((CheckBoxLayout)convertView).ch.setChecked(true);
			else
				((CheckBoxLayout)convertView).ch.setChecked(false);
			return convertView;		
		}

		public boolean isChecked(int i)
		{
			if(checked[i]==0)
				return false;
			return true;
		}
		/**
		 * Returns layout which contains Image and checkbox
		 * @return the final layout
		 */
		public CheckBoxLayout getCheckBoxLayout()
		{
			CheckBoxLayout rl = new CheckBoxLayout(context);
			return rl;
		}
	}
}
