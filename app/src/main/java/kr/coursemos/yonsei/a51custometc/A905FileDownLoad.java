package kr.coursemos.yonsei.a51custometc;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import kr.coursemos.yonsei.a99util.A99_HttpURLConnectionManager;
import kr.coursemos.yonsei.a99util.Env;
import kr.coursemos.yonsei.a99util.StaticClass;
import kr.coursemos.yonsei.a99util.StringHashMap;

public abstract class A905FileDownLoad extends AsyncTask<Void, Void, String[]>   {
	public Context context = null;
	public String URL = null;
	public String result="{}";
	public String errorMsg = null;
	public String fileName = null;
	public StringHashMap shm = null;
	public SharedPreferences sp = null;
	public Editor edit = null;
	public A99_HttpURLConnectionManager CMcommu = null;
	public long priTime = 0;
	public String cookie=null;
	public A905FileDownLoad(Context context, String URL, StringHashMap shm, String fileName, String cookie) {
		try{
			this.context = context;
			this.shm = shm;
			this.URL=URL;
			this.fileName = fileName;
			this.cookie = cookie;
//			String[] urls=URL.split("/");
//			int index=URL.lastIndexOf(urls[urls.length-1]);
//			this.URL=URL.substring(0, index)+URLEncoder.encode(urls[urls.length-1], "utf-8").replaceAll("\\+", "%20");
//			this.URL=URL.substring(0, index)+urls[urls.length-1];


			Env.debug(context,URL);

			String params[] =URL.split("\\?");
			if(params.length==1){
			}else{
				this.URL=params[0];
				URL = URL.replaceFirst(params[0]+"\\?", "");
				params = URL.split("\\&");
				int size = params.length;
				String key_value[] = null;
				for (int i = 0; i < size; i++) {
					key_value = params[i].split("=");
					this.shm.put(URLDecoder.decode(key_value[0], StaticClass.charset).replaceAll("amp;",""), URLDecoder.decode(key_value[1], StaticClass.charset));
				}
			}


//			this.shm.put("file","/747749/mod_ubfile/content/0/심볼형.png");
//			this.shm.put("amp;forcedownload","1");
//			this.URL="http://cyber.ewha.ac.kr/webservice/pluginfile.php";
			if(this.shm.containsKey("file")){
				params=this.shm.getString("file").split("/");
				this.fileName=params[params.length-1];
			}else{
				if(fileName==null){
					this.fileName="test.test";
				}else{
					this.fileName=fileName;
				}
			}

//			this.URL="http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif";

			sp = context.getSharedPreferences(StaticClass.KEY_SharedPreferences, Service.MODE_PRIVATE);
			edit = sp.edit();
			priTime = System.currentTimeMillis();
		}catch(Exception e){
			Env.error("", e);
		}
		execute();
	}

	@Override
	protected String[] doInBackground(Void... params) {
		try {


			if (StaticClass.isDebug) {
				Env.debug(context, priTime + " - send : "+cookie+"\n" + URL + "\n" + shm.toString());
			}
			CMcommu = new A99_HttpURLConnectionManager(URL, StaticClass.charset,shm,"GET",null) {
				@Override
				protected void result(InputStream inputStream) throws Exception {
					if(connFilename==null){
					}else{
						fileName=	URLDecoder.decode(connFilename, StaticClass.charset).replaceAll("\"","").replaceAll("'","");
					}

					File file = new File(StaticClass.pathExternalStorage + fileName);
					int len = 0;
					byte[] raster = new byte[1024];
					FileOutputStream fos = new FileOutputStream(file);
					for (;;) {
						len = inputStream.read(raster);
						if (len <= 0) {
							break;
						}
						fos.write(raster, 0, len);
					}

					inputStream.close();
					fos.close();
					fos = null;
				}
				
				
				@Override
				protected void resultFail(InputStream inputStream,String errmsg) throws Exception {
					StringBuilder sb = new StringBuilder();
					if(inputStream==null){
						sb.append(errmsg);
					}else{
						BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
						for (;;) {
							String line = br.readLine();
							if (line == null)
								break;
							sb.append(line+"\n");
						}
					}
					
					errorMsg=sb.toString();
				}
				
				
			};
			CMcommu.connect(cookie);

			if (StaticClass.isDebug) {
				Env.debug(context, priTime + " - recv : " + URL );
			}

		} catch (Exception e) {
			Env.error("", e);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String[] result) {
		try {
			if (errorMsg == null) {
				success( fileName);
			} else {
				fail(errorMsg);
			}

		} catch (Exception e) {
			Env.error("", e);
		}
	}
	
	abstract public void success(String msg) throws Exception;

	abstract public void fail(String errmsg) throws Exception;

}// sendTask
