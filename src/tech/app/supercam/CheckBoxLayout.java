package tech.app.supercam;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class CheckBoxLayout extends RelativeLayout{

	CheckBox ch;
	ImageView iv;
	public CheckBoxLayout(Context context) {
		super(context);
		ch = new CheckBox(context);
		iv = new ImageView(context);
		setUpLayout();
	}
	public CheckBoxLayout(Context context,CheckBox box)
	{
		super(context);
		ch=box;
		iv = new ImageView(context);
		setUpLayout();
	}
	public void setUpLayout()
	{
		setImageViewParams();
		setBoxParams();
		this.addView(iv);
		this.addView(ch);
	}
	public void toggleChecked()
	{
		ch.setChecked(!ch.isChecked());
	}
	public void setImageViewParams()
	{
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setLayoutParams(lp);
		iv.setFocusable(false);
	}
	public void displayCheckBox()
	{
		this.addView(ch);
		ch.setChecked(true);
	}
	public void hideCheckBox()
	{
		this.removeView(ch);
		ch.setChecked(false);
	}
	public void setBoxParams()
	{
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		ch.setLayoutParams(lp);
		ch.setFocusable(false);
	}
}
