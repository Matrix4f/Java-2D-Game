package bz.x10.matrix4f.launcher.auth;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;

import security.EncryptedString;
import security.Security;

public class AuthVar {

	public static String Username = "";
	public static char[] Password = new char[] {};
	public static String SessionID = "";
	public static boolean Valid = false;
	
	public static String genObfuscatedPassword(char echo) {
		String str = "";
		for(int i = 0; i < Password.length; i++)
			str += echo;
		return str;
	}
	
	@SuppressWarnings("unchecked")
	public static void flushData() {
		JSONObject array = new JSONObject();
		array.put("username", Username);
		array.put("password", new String(Password));

		EncryptedString encryptedData = Security.encrypt(array.toJSONString(), 10, 153,
				3, 2835, 2);

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("auth.dat"));
			bw.write(encryptedData.toString());
			bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
