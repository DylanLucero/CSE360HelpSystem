package cse360helpsystem;

import java.util.Random;


public class OTP{
	public static Random rand = new Random();
	public static String stringOTP;

	// Generating an OTP
	public static String generateOTP() {
		int userOTP = rand.nextInt(100,9999);
		if(userOTP<1000) {
			stringOTP = "0" + userOTP;
		} else {
			stringOTP = Integer.toString(userOTP);
		}
		return stringOTP;
	} 

}


	
