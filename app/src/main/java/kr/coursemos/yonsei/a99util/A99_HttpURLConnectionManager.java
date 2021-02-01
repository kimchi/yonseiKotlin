package kr.coursemos.yonsei.a99util;


import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public abstract class A99_HttpURLConnectionManager {
	private URL url =null ;
	private HttpURLConnection conn;
	private String param=null;
	private String charset=null;
	private String method="POST";
	protected StringHashMap shm_connectInfo=null;
	protected String urlPath=null;
	public String connFilename=null;
	public A99_HttpURLConnectionManager(String purl, String charset, StringHashMap shm, String method, StringHashMap shm_connectInfo) throws Exception {
		this.shm_connectInfo=shm_connectInfo;
		this.charset=charset;
		param=getRequestMessage(shm);
		this.urlPath=purl;
		if(method instanceof String){
			this.method=method;
		}else{
			method=this.method;
		}
		if(method.equals("GET")){
			purl+="?"+param;
		}
		url = new URL(purl);
	}
	public String getRequestMessage(StringHashMap shm) throws Exception{
		StringBuffer sb = new StringBuffer();
		if(shm instanceof StringHashMap&&shm.size()>0){
			Object[] objs = shm.keySet().toArray();
			sb.append(URLEncoder.encode(((String) objs[0]), charset) + "=" + URLEncoder.encode(shm.getString(objs[0]), charset));
			for (int i = 1; i < objs.length; i++) {
				sb.append("&" + URLEncoder.encode(((String) objs[i]), charset) + "=" + URLEncoder.encode(shm.getString(objs[i]), charset));
			}
		}
		return sb.toString();
	}
	public void connect(String cookie) throws Exception{
		try {
			conn = (HttpURLConnection) url.openConnection();
			if (conn instanceof HttpURLConnection) {
				conn.setConnectTimeout(30000);
				conn.setUseCaches(false);
				conn.setRequestMethod(method);
				conn.setRequestProperty("Accept-Language", "ko-KR");
//				conn.setRequestProperty("Accept-Charset", charset);
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset="+charset);
//				conn.setRequestProperty("Conection", "Keep-Alive");
//				conn.setRequestProperty("user-agent", A903LMSTask.userAgent);

				if(cookie!=null){
					conn.setRequestProperty("Cookie", cookie);
				}
				if(method.equals("POST")){
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(param);
					wr.flush();
					wr.close();
				}
				int responseCode=conn.getResponseCode();
				InputStream inputStream=null;
				if (conn instanceof HttpURLConnection && responseCode >=200&&responseCode<300) {
					Map<String,List<String>> map=conn.getHeaderFields();
					Iterator<String> iter= conn.getHeaderFields().keySet().iterator();
					String key=null;
					String value=null;
					List<String> list=null;
					for (;iter.hasNext();){
						key=iter.next();
						list=map.get(key);
						for(int i=0;i<list.size();i++){
							value=list.get(i);
							if(value.indexOf("filename=")>=0){
								connFilename=value.split("filename=")[1].split("\\;")[0];
								break;
							}
						}
					}

					inputStream=conn.getInputStream();
					result(inputStream);
				}else{
					inputStream=conn.getErrorStream();
					resultFail(inputStream,Integer.toString(responseCode));
				}
				if(inputStream!=null){
					inputStream.close();	
				}
			}
		}finally {
			if(conn instanceof HttpURLConnection){
				conn.disconnect();
			}
		}
	}
	public void connect() throws Exception{
		connect(null);
	}
	protected abstract void result(InputStream inputStream) throws Exception;
	protected abstract void resultFail(InputStream inputStream,String errmsg) throws Exception;
}
