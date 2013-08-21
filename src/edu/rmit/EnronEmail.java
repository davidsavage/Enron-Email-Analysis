package edu.rmit;

/**
 * Created with IntelliJ IDEA.
 * User: davidsavage
 * Date: 21/08/13
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnronEmail {
	public final String to;
	public final String from;
	public final String sendDate;
	public final String sendTime;
	public final String length;

	public EnronEmail(String[] fields) {
		to = fields[0];
		from = fields[1];
		sendDate = fields[2];
		sendTime = fields[3];
		length = fields[4];//Integer.parseInt(fields[4]);
	}
}
