package tech.app.supercam;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class SizeManager {
public static int getDip(int dip, DisplayMetrics metrics)
{//used to get pixels corresponding to Density Independent Pixels to ensure same size on all devices
	int x = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
	return x;
}
}
