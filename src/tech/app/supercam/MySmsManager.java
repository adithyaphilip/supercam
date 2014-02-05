package tech.app.supercam;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.telephony.SmsManager;

public class MySmsManager {
	public static void sendSms(String to, String centre, String message, ArrayList<PendingIntent> sentIntent, ArrayList<PendingIntent> deliveryIntent)
	{
		SmsManager manager = SmsManager.getDefault();
		System.out.println("to:"+to);
		System.out.println("msg:"+message);
		ArrayList<String> as = manager.divideMessage(message);
		manager.sendMultipartTextMessage(to, centre, as, sentIntent, deliveryIntent);
	}
}
