package kr.coursemos.yonsei.a99util;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CMCommunication {
	public static int errorCount=0;
	private URL url;
	private HttpURLConnection conn;
	public String xml = null;
	public String error = null;
	private String param="";
	private String charset=null;
	private String method="POST";
	private StringHashMap header=null;
	public CMCommunication() {
	}
	//생성자 - 서버로 요청할 URL등록
	public CMCommunication(String purl,String charset,StringHashMap shm,String method,StringHashMap header) {

//		SSLContext sSLContext = new SslCtrl().getSSLContext(A00_00Activity.context);
//		((HttpURLConnection)conn).setSSLSocketFactory(sSLContext.getSocketFactory());
		try {
			this.charset=charset;
			param=getRequestMessage(shm);
			
			this.header=header;
			if(this.header==null){
				this.header=new StringHashMap();
			}


			if(method.equals("GET")){
				url = new URL(purl+"?"+getRequestMessage(shm));
			}else if(method.equals("SOAP")){
				param=getRequestMessageSOAP(shm);
				url = new URL(purl);
			}else{
				param=getRequestMessage(shm);
				url = new URL(purl);
			}
			this.method=method;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//생성자 - 서버로 요청할 URL등록
	public CMCommunication(String purl,String charset,String shm,String method,StringHashMap header) {
		try {
			this.charset=charset;
//			param=URLEncoder.encode(shm, charset);
			param=shm;

			this.header=header;
			if(this.header==null){
				this.header=new StringHashMap();
			}
			if(method.equals("GET")){
				url = new URL(purl+"?"+param);
			}else{
				url = new URL(purl);
			}
			this.method=method;
		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.getMessage());
			e.printStackTrace();
		}
	}
	//서버로 요청할 파라메터와 캐릭터셋
	public  String getRequestMessage(StringHashMap shm) {
		StringBuffer sb = new StringBuffer();
		String array[]=null;
		if(shm instanceof StringHashMap&&shm.size()>0){
			Object[] objs = shm.keySet().toArray();
			try {
				for (int i = 0; i < objs.length; i++) {
					if(i>0){
						sb.append("&");
					}
					if(((String) objs[i]).equals("certifiedkey")){
						sb.append((String) objs[i] + "=" + shm.getString(objs[i]));
					}else{
						
						String key=URLEncoder.encode(((String) objs[i]), charset);
						if(key.equals("message")){
							sb.append(key + "=" + URLEncoder.encode(shm.getString(objs[i]), charset));
						}else{
							array=shm.getString(objs[i]).split(",,");
							int size=array.length;
							if(size>1){
								for(int j=0;j<size;j++){
									if(i>0||j>0){
										sb.append("&");
									}
									sb.append(key + "=" + URLEncoder.encode(array[j], charset));
								}
							}else{
								sb.append(key + "=" + URLEncoder.encode(array[0], charset));
							}
						}
						
						
					}
//					sb.append(URLEncoder.encode(((String) objs[i]), charset) + "=" + URLEncoder.encode(shm.getString(objs[i]), charset));
					
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	
		}
		return sb.toString();
	}
	
	
	//서버로 요청한 XML의 내용중 관련된 태그의 내용을 추출
	public String getXML( String find) {
		if (!(xml instanceof String))
			getHTML();
		String returnstr = "";
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			SaxHandler handler = new SaxHandler(find);
			reader.setContentHandler(handler);
			InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
			reader.parse(new InputSource(istream));
			returnstr = handler.item.toString();
			istream.close();
			istream=null;
		} catch (Exception e) {
			return e.getMessage();
		}
		return returnstr.trim();
	}
	//SAX파서 핸들러
	class SaxHandler extends DefaultHandler {
		public StringBuilder item = new StringBuilder();
		private boolean sw = false;
		private String find = "";
		public void startDocument() {
		}

		public void endDocument() {
		}

		public SaxHandler(String pfind) {
			find = pfind;
		}

		public void startElement(String uri, String localName, String qName, Attributes atts) {
			if (find.equals(localName)) {
				sw = true;
			}
		}

		public void endElement(String uri, String localName, String qName) {
			if (find.equals(localName)) {
				sw = false;
			}
		}

		public void characters(char[] chars, int start, int length) {
			if (sw) {
				item.append(chars, start, length);
			}
		}
	};


	public  String getRequestMessageSOAP(StringHashMap shm) {
		StringBuffer sb = new StringBuffer();
		String array[]=null;
		if(shm instanceof StringHashMap&&shm.size()>0){
			Object[] objs = shm.keySet().toArray();
			try {
				for (int i = 0; i < objs.length; i++) {
					String key=URLEncoder.encode(((String) objs[i]), charset);

					array=shm.getString(objs[i]).split(",,");
					int size=array.length;
					if(size>1){
						for(int j=0;j<size;j++){
							sb.append(key + "=" + URLEncoder.encode(array[j], charset));
						}
					}else{
						sb.append(key + "=" + URLEncoder.encode(array[0], charset));
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		sb= new StringBuffer("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://server.com\">");
		sb.append("<soapenv:Header/>");
		sb.append("<soapenv:Body>");


		sb.append("<ser:verification>");
		sb.append("<cookieValue>");
		sb.append(shm.getString("tokenValue"));
		sb.append("</cookieValue>");
		sb.append("<PrivateKeyStr>");
		sb.append(shm.getString("PrivateKeyStr"));
		sb.append("</PrivateKeyStr>");

		sb.append("<adminVO>");
		sb.append("<adminId></adminId>");
		sb.append("<password></password>");
		sb.append("</adminVO>");
		sb.append("</ser:verification>");
		sb.append("</soapenv:Body>");
		sb.append("</soapenv:Envelope>");


		return sb.toString();
	}

	final HostnameVerifier DO_NOT_VERIFY=new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	public void getHTML() {
		try {

			conn = (HttpURLConnection) url.openConnection();

			if (conn instanceof HttpURLConnection) {
//				if(url.getHost().indexOf("https")==0){
//					((HttpsURLConnection)conn).setHostnameVerifier(DO_NOT_VERIFY);
//				}
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(15000);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				Object[] objs = header.keySet().toArray();
				for (int i = 0; i < objs.length; i++) {
					conn.setRequestProperty((String) objs[i], header.getString((String) objs[i]));
				}
				if(method.equals("GET")){
					conn.setRequestMethod(method);
				}else{
					if(method.equals("SOAP")){
						conn.setRequestMethod("POST");
					}else{
						conn.setRequestMethod(method);
					}

					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(param);
					wr.flush();
					wr.close();
				}

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
					read.close();
					baos.close();
					errorCount=0;
				} else {
//					Log.e(this.getClass().getName(), conn.getResponseMessage()+"\n"+"error:" + conn.getResponseCode());
					try{
						ByteArrayOutputStream baos=new ByteArrayOutputStream();
						InputStream read=conn.getErrorStream();
						byte[] bytes=new byte[1024];
						int len=0;
						for (;(len=read.read(bytes))>=0;) {
							baos.write(bytes, 0, len);
						}
						error = new String(baos.toByteArray(),charset);
						read.close();
						baos.close();
					}catch(Exception streamError){
						error=streamError.getMessage()+"\n"+conn.getResponseMessage()+"\n"+"error:" + conn.getResponseCode();
					}
				}
			}
		} catch (SSLHandshakeException ssl){
			Env.error("",ssl);
			error="SSL 체크가 필요합니다. 관리자에게 문의하세요.";

		} catch (Exception e) {
			if(errorCount<1){
				Env.error("",e);
			}else{
				e.printStackTrace();
				Log.e(this.getClass().getName(), e.getMessage());
			}
			errorCount++;
			return ;
		} finally {
			if(conn instanceof HttpURLConnection){
				conn.disconnect();
			}
		}
	}



}
