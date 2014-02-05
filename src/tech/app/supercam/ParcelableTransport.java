package tech.app.supercam;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTransport <T> implements Parcelable{
	public static final Parcelable.Creator <ParcelableTransport> CREATOR = new Parcelable.Creator<ParcelableTransport>() {

		@Override
		public ParcelableTransport createFromParcel(Parcel source) {
			return new ParcelableTransport(source);
		}

		@Override
		public ParcelableTransport[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		
	}
	private ParcelableTransport(Parcel in)
	{
		//write
	}
}
