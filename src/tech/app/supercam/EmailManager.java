package tech.app.supercam;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class EmailManager
{
	public static void sendEmail(String to, String sub, String body, Context c)
	{
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		String[] s= new String[1];
		s[0]=to;
		i.putExtra(Intent.EXTRA_EMAIL  , s);
		i.putExtra(Intent.EXTRA_SUBJECT, sub);
		i.putExtra(Intent.EXTRA_TEXT   , body);
		try {
		    c.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(c, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
}