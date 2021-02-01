package kr.coursemos.yonsei.a99util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings.Secure;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static kr.coursemos.yonsei.a99util.StaticClass.isReal;

@SuppressLint("SimpleDateFormat")
public class Env {

	public static void debug(Context context, String msg) {
			if(isReal){

			}else {
				StringHashMap param = new StringHashMap();
				param.put("isdebug", "true");
				param.put("projectName", "cosmosAndroid");
//			param.put("projectName", "cosmosAndroidProduct");
				param.put("errorType", "2");
				if(context instanceof Context){
					try{
						param.put("message", URLEncoder.encode("_" + Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) + "\n" + msg + "\n",StaticClass.charset));
					}catch (Exception e){
						param.put("message", "_" + Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) + "\n" + msg + "\n");
					}
//				TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

				}else{
					try{
						param.put("message",URLEncoder.encode("\n"+msg+"\n",StaticClass.charset));
					}catch (Exception e){
						param.put("message", "\n"+msg+"\n");
					}
				}
				param.put("log",param.getString("message"));


				CMThread CMTh = new CMThread(null, param, "mydebug", null) {
					public void run() {
						try {
							new CMCommunication(StaticClass.mydebug, "UTF-8", shm_param, "POST",null).getHTML();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				CMTh.start();
			}

	}

	public static void fileReSize(String path,String rename) throws Exception {
		File outFile = new File(rename);
		OutputStream out = null;
		out = new FileOutputStream(outFile);
		File inFile = new File(path);
		FileInputStream is = new FileInputStream(inFile);
		BitmapFactory.decodeStream(is, null, StaticClass.getBitmapOption(480, 800, path)).compress(Bitmap.CompressFormat.PNG, 100, out);
		is.close();
		is = null;
		out.close();
		out = null;
		outFile=null;
		inFile=null;
//		outFile.renameTo(inFile);
	}
	public static String SEEDEncode(String msg,boolean isBase64,String charset) throws Exception{
		byte pbCipher[] = SEED.Encrypt(msg, charset,SEED.iv,SEED.key);
		if(isBase64){
			return new String(encodeBase64(pbCipher),charset);
		}else{
			return new String(pbCipher,charset);
		}
	}
	public static String SEEDDecode(String msg,boolean isBase64,String charset) throws Exception{
		try{
			byte pbCipher[]=null;

			if(isBase64){
				pbCipher=decodeBase64(msg.getBytes(charset));
			}else{
				pbCipher=msg.getBytes(charset);
			}


			return SEED.Decrypt(pbCipher, charset,SEED.iv,SEED.key);
		}catch (ArrayIndexOutOfBoundsException e) {
			return "";
		}


	}
	public static void error(String msg, Exception e) {
		if(isReal){

		}else {
			StringHashMap param = new StringHashMap();
			param.put("isdebug", "false");
			param.put("projectName", "cosmosAndroid");
//			param.put("projectName", "cosmosAndroidProduct");
			param.put("errorType", "2");
			param.put("message", msg + "\n" + error(e));
			param.put("log",param.getString("message"));
			CMThread CMTh = new CMThread(null, param, "mydebug", null) {
				public void run() {
					try {
						new CMCommunication(StaticClass.mydebug, "UTF-8", shm_param, "POST",null).getHTML();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			CMTh.start();
		}


	}

	public static String error(Exception e) {
		StackTraceElement[] errormsg = e.getStackTrace();
		StringBuffer errorString = new StringBuffer("\n" + e + "\n");
		for (int i = 0; i < errormsg.length; i++) {
			errorString.append("\nFileName   : " + errormsg[i].getFileName() + "(LineNumber : " + errormsg[i].getLineNumber() + ")" + "\nClassName  : " + errormsg[i].getClassName() + "\nMethodName : " + errormsg[i].getMethodName() + "\n");
		}
		return errorString.toString();
	}
	public static String HexToString(byte[] digest) {
		StringBuilder str = new StringBuilder();
		String temp=null;
		for (int i = 0; i < digest.length; i++) {
			temp=Integer.toHexString(digest[i] & 0xFF);
			if(temp.length()==0){
				str.append("00");
			}else if(temp.length()==1){
				str.append("0");
			}
			str.append(temp);
		}
		return str.toString();
	}

	public static String SHA256(String str) {
		String SHA = "";
		try {
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();


		} catch (Exception e) {
			Env.error("SHA256", e);
			SHA = null;
		}
		return SHA;

	}
	public static String encryptAES256(String msg) throws Exception {
		String key="0Wo717BlySXSSPwVZN9j8GAi2uuikC7j";
		String iv="F196F9B26S9e49d4";
		SecretKey secureKey=new SecretKeySpec(key.getBytes(),"AES");
		Cipher c=Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE,secureKey,new IvParameterSpec(iv.getBytes()));
		byte[] encrypted=c.doFinal(msg.getBytes());
		return new String(encodeBase64(encrypted));
	}

	public static String decryptAES256(String msg) throws Exception {
		String key="0Wo717BlySXSSPwVZN9j8GAi2uuikC7j";
		String iv="F196F9B26S9e49d4";
		SecretKey secureKey=new SecretKeySpec(key.getBytes(),"AES");
		Cipher c=Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE,secureKey,new IvParameterSpec(iv.getBytes()));
		byte[] encrypted=c.doFinal(decodeBase64(msg.getBytes()));
		return new String(encrypted);
	}
	public static byte[] encodeBase64(byte[] binaryData) throws Exception {
		return Base64.encode(binaryData,Base64.DEFAULT);
	}// encodeBase64

	public static byte[] decodeBase64(byte[] base64Data) throws Exception {
		return Base64.decode(base64Data,Base64.DEFAULT);
	}// decodeBase64
	public static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	public static String EncodeAES(String msg,String key, String charset) throws Exception {
		AlgorithmParameterSpec ivps = new IvParameterSpec(ivBytes);
		SecretKeySpec sks = new SecretKeySpec(key.getBytes(charset), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, sks, ivps);
		byte[] enc2 = cipher.doFinal(msg.getBytes(charset));
		return new String(encodeBase64(enc2),charset);
	}
	public static String DecodeAES(String msg,String key, String charset) throws Exception{

		AlgorithmParameterSpec ivps = new IvParameterSpec(ivBytes);
		SecretKeySpec sks = new SecretKeySpec(key.getBytes(charset),  "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, sks, ivps);
		return  new String(cipher.doFinal(decodeBase64(msg.getBytes(charset))),charset);
	}
	public static String getAddedDay(int year,int month,int day,int addDay) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		cal.add(Calendar.DATE, addDay);

		return new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
	}
	public static String formatTime(String format, Date date) {
		return formatTime(format, date.getTime());
	}

	public static String formatTime(String format, long time) {
		return formatTime(format, time, TimeZone.getDefault());
	}

	public static String formatTime(String format, Date date, TimeZone timeZone) {
		return formatTime(format, date.getTime(), timeZone);
	}

	public static String formatTime(String format, long time, TimeZone timeZone) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		sdf.setTimeZone(timeZone);
		return sdf.format(time);
	}
	public static String getyyyyMMddToLong(String datetime) throws Exception{
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		Date date=df.parse(datetime);
		return Long.toString(date.getTime() / 1000);
	}
	public static String getLongToMMDDHHmm(Context context,String longTime) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("MM.dd HH:mm", Locale.KOREA);
		return sdfFormat.format(new Date(Long.parseLong(longTime+"000")));
	}
	public static String getLongToDate(Context context,String longTime) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
		return sdfFormat.format(new Date(Long.parseLong(longTime+"000")));
	}
	public static String getLongToDay(String longTime) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		return sdfFormat.format(new Date(Long.parseLong(longTime+"000")));
	}
	public static String getLongToDay(String longTime,String div) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy"+div+"MM"+div+"dd", Locale.KOREA);
		return sdfFormat.format(new Date(Long.parseLong(longTime+"000")));
	}
	public static String getLongToTIME(String longTime) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
		return sdfFormat.format(new Date(Long.parseLong(longTime+"000")));
	}

	public static String getTodateAddDay(String pattern,int day ) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat(pattern, Locale.KOREA);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, day);
		return sdfFormat.format(cal.getTime());
	}

	public static String getTodateAddSecond(String pattern,int second ) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat(pattern, Locale.KOREA);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.SECOND, second);
		return sdfFormat.format(cal.getTime());
	}

	public static String getTodate() {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
		Date da = new Date();
		return sdfFormat.format(da);
	}
	public static String getToday() {
		return getToday("");
	}
	public static String getYYMMDD() {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("yyMMdd", Locale.KOREA);
		Date da = new Date();
		return sdfFormat.format(da);
	}
	public static String getToday(String sdv) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy" + sdv + "MM" + sdv + "dd", Locale.KOREA);
		Date da = new Date();
		return sdfFormat.format(da);
	}


	public static String getTodate(String sdv) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("HH" + sdv + "mm" + sdv + "ss", Locale.KOREA);
		Date da = new Date();
		return sdfFormat.format(da);
	}
}
