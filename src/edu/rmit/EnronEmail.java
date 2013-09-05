package edu.rmit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: davidsavage
 * Date: 21/08/13
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnronEmail {
	public final int emailID;
	public final int toID;
	public final int fromID;
	public final int timeSent;
	public final int length;

	public EnronEmail(int emailID,
		int fromID, int toID, int timeSent, int length) {
		
		this.emailID = emailID;
		this.fromID = fromID;
		this.toID = toID;
		this.timeSent = timeSent;
		this.length = length;
	}
}
