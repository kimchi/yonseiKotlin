package kr.coursemos.yonsei.a99util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CMMultiPart {

	private URL url;
	private HttpURLConnection conn;
	public String xml = null;
	public String error = null;
	private StringHashMap shm=null;
	private String charset="UTF-8";
	private String method="POST";
	private StringHashMap header=null;
	public String filepath = null;
	public String fileKey = null;
	public String fileName = null;
	private DataOutputStream dataStream = null;
	private String CRLF = "\r\n";
	private String twoHyphens = "--";
	private String boundary = "*****kimchiyak0886gmailcom*****";
	public StringBuffer result=null;
	public long priTime = 0;
	//생성자 - 서버로 요청할 URL등록
	public CMMultiPart(String purl, String charset, StringHashMap shm, String method, StringHashMap header, String filepath, String fileKey) {
		try {
			this.charset=charset;
			this.filepath = filepath;
			this.fileKey = fileKey;
			String [] tempSplit=filepath.split("/");
			this.fileName = tempSplit[tempSplit.length-1];
			this.shm = shm;
			priTime = System.currentTimeMillis();
			this.header=header;
			if(this.header==null){
				this.header=new StringHashMap();
			}
			url = new URL(purl);
			this.method=method;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void getHTML() throws Exception{
		try {
			conn = (HttpURLConnection) url.openConnection();
			if (conn instanceof HttpURLConnection) {
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(15000);
				conn.setRequestMethod(method);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setChunkedStreamingMode(128 * 1024);
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				Object[] objs = header.keySet().toArray();
				for (int i = 0; i < objs.length; i++) {
					conn.setRequestProperty((String) objs[i], header.getString((String) objs[i]));
				}
				dataStream = new DataOutputStream(conn.getOutputStream());


				String mimeType=null;
				if(shm.containsKey("mimeType")){
					mimeType=shm.getString("mimeType");
					shm.remove("mimeType");
				}else{
					if(fileName.lastIndexOf(".jpg")>=0){
						mimeType="image/jpeg";
					}else if(fileName.lastIndexOf(".png")>=0){
						mimeType="image/png";
					}else if(fileName.lastIndexOf(".gif")>=0){
						mimeType="image/gif";
					}else if(fileName.lastIndexOf(".tiff")>=0){
						mimeType="image/tiff";
					}else if(fileName.lastIndexOf(".pdf")>=0){
						mimeType="application/pdf";
					}else if(fileName.lastIndexOf(".vnd")>=0){
						mimeType="application/vnd";
					}else if(fileName.lastIndexOf(".txt")>=0){
						mimeType="text/plain";
					}else {
						mimeType="application/octet-stream";
					}
				}

				objs = shm.keySet().toArray();
				for (int i = 0; i < objs.length; i++) {
					if(((String) objs[i]).equals("certifiedkey")){
						writeFormField((String) objs[i], shm.getString(objs[i]));
					}else{
//						writeFormField(URLEncoder.encode(((String) objs[i]), StaticClass.charset), URLEncoder.encode(shm.getString(objs[i]), StaticClass.charset));
						writeFormField(new String(((String) objs[i]).getBytes(), "8859_1"), new String(shm.getString(objs[i]).getBytes(),"8859_1"));
					}
				}


//				if(filepath.lastIndexOf(".jpg")>=0||filepath.lastIndexOf(".jpeg")>=0||filepath.lastIndexOf(".png")>=0||filepath.lastIndexOf(".gif")>=0){
//					mimeType="image/*";
//				}else{
//					mimeType="video/*";
//				}

				if(filepath.equals("")){
					writeEmptyFileField(fileKey, fileName, mimeType);
				}else{
					writeFileField(fileKey, fileName, mimeType, new FileInputStream(new File(filepath)));
				}

//				writeFileField(fileKey, fileName, mimeType, new FileInputStream(new File(filepath)));
				dataStream.flush();
				dataStream.close();
				dataStream = null;
				int responseCode=conn.getResponseCode();
				if (conn instanceof HttpURLConnection && responseCode >=200&&responseCode<300) {
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					InputStream read=conn.getInputStream();
					byte[] bytes=new byte[1024];
					int len=0;
					for (;(len=read.read(bytes))>=0;) {
						baos.write(bytes, 0, len);
					}
					xml = new String(baos.toByteArray(),charset);
				} else {
					Log.e(this.getClass().getName(), conn.getResponseMessage()+"\n"+"error:" + conn.getResponseCode());
					try{
						ByteArrayOutputStream baos=new ByteArrayOutputStream();
						InputStream read=conn.getErrorStream();
						byte[] bytes=new byte[1024];
						int len=0;
						for (;(len=read.read(bytes))>=0;) {
							baos.write(bytes, 0, len);
						}
						error = new String(baos.toByteArray(),charset);
					}catch(Exception streamError){
						error=streamError.getMessage();
						return ;
					}
				}
			}
		} finally {
			if(conn instanceof HttpURLConnection){
				conn.disconnect();
			}

		}
	}
	private void writeFormField(String fieldName, String fieldValue) throws Exception {
		dataStream.writeBytes(twoHyphens + boundary + CRLF);
		dataStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"" + CRLF);
		dataStream.writeBytes(CRLF);
		dataStream.writeBytes(fieldValue);
		dataStream.writeBytes(CRLF);

		if(StaticClass.isDebug){
			Env.debug(null,twoHyphens + boundary + CRLF+"Content-Disposition: form-data; name=\"" + fieldName + "\"" + CRLF+CRLF+fieldValue+CRLF);
		}
	}
	private void writeEmptyFileField(String fieldName, String fieldValue, String type) throws Exception {
		// opening boundary line
		dataStream.writeBytes(twoHyphens + boundary + CRLF);
		dataStream.writeBytes("Content-Disposition: form-data;  name=\"" + fieldName + "\";fileName=\"" + fieldValue + "\";mimeType=\""+ type + "\"" + CRLF);
		dataStream.writeBytes("Content-Type: " + type + CRLF);
		dataStream.writeBytes(CRLF);

		// create a buffer of maximum size
		// closing CRLF
		dataStream.writeBytes(CRLF);
		dataStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);

		if(StaticClass.isDebug){
			Env.debug(null,twoHyphens + boundary + CRLF+"Content-Disposition: form-data;  name=\"" + fieldName + "\";fileName=\"" + fieldValue + "\";mimeType=\""+ type + "\"" + CRLF+"Content-Type: " + type + CRLF+CRLF+"!!!!!!!!!!EMPTY!!!!!!!!!!!"+CRLF+twoHyphens + boundary + twoHyphens + CRLF);
		}

	}
	private void writeFileField(String fieldName, String fieldValue, String type, FileInputStream fis) throws Exception {
		// opening boundary line
		dataStream.writeBytes(twoHyphens + boundary + CRLF);
		dataStream.writeBytes("Content-Disposition: form-data;  name=\"" + fieldName + "\";fileName=\"" + fieldValue + "\";mimeType=\""+ type + "\"" + CRLF);
		dataStream.writeBytes("Content-Type: " + type + CRLF);
		dataStream.writeBytes(CRLF);

		// create a buffer of maximum size
		int bytesAvailable = fis.available();
		int maxBufferSize = 1024;
		int bufferSize = Math.min(bytesAvailable, maxBufferSize);
		byte[] buffer = new byte[bufferSize];
		// read file and write it into form...
		int bytesRead = fis.read(buffer, 0, bufferSize);
		while (bytesRead > 0) {
			dataStream.write(buffer, 0, bufferSize);
			bytesAvailable = fis.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fis.read(buffer, 0, bufferSize);
		}
		// closing CRLF
		dataStream.writeBytes(CRLF);
		dataStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);

		if(StaticClass.isDebug){
			Env.debug(null,twoHyphens + boundary + CRLF+"Content-Disposition: form-data;  name=\"" + fieldName + "\";fileName=\"" + fieldValue + "\";mimeType=\""+ type + "\"" + CRLF+"Content-Type: " + type + CRLF+CRLF+"!!!!!!!!!!DATA!!!!!!!!!!!"+CRLF+twoHyphens + boundary + twoHyphens + CRLF);
		}

		fis.close();
	}
}
