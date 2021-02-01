package kr.coursemos.yonsei.a99util;


import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;

import org.json.JSONObject;

import kr.coursemos.yonsei.A01_11Chatting;
import kr.coursemos.yonsei.a51custometc.A903LMSTask;

public class A99_CycleManager extends Thread{
	private static A99_CycleManager cycleManager=null;
	private boolean isCycle=false;
	private Context context=null;
	private SharedPreferences sp=null;
	public A99_CycleManager(Context context) {
		this.context=context;
		sp=context.getSharedPreferences(StaticClass.KEY_SharedPreferences, Service.MODE_PRIVATE);
	}
	static public void setSelect(Context context) throws Exception{
		if(cycleManager==null){
			cycleManager=new A99_CycleManager(context);
			cycleManager.start();
		}else{
			if(cycleManager.isAlive()){
				
			}else{
				cycleManager=new A99_CycleManager(context);
				cycleManager.start();
			}
		}
		cycleManager.isCycle=true;
	}
	@Override
	public void run() {
		try{
			StringHashMap chattingSelectShm=new StringHashMap();
			String currentID=null;
			JSONObject chattingUser=null;



			for(; A01_11Chatting.Companion.getHandler() !=null;){
				chattingUser=new JSONObject(sp.getString(StaticClass.KEY_CHATTINGINFO, "[]"));
//				Env.debug(context, "1111111111111111111111111"+isCycle);
				if(isCycle){
					isCycle=false;
					chattingSelectShm.put("historymonth", "3");
					chattingSelectShm.put("userid" ,chattingUser.getString("userid"));
					chattingSelectShm.put("idnumber" ,chattingUser.getString("idnumber"));
					String api= A903LMSTask.API_CHATTINGSELECT;
					if(chattingUser.getBoolean("is_groupmessage")){
						api= A903LMSTask.API_CHATTINGSELECT_GROUP;
						if(chattingUser.isNull("touserid")){
						}else{
							chattingSelectShm.put("muid" ,chattingUser.getString("touserid"));
						}
						if(chattingUser.isNull("muid")){
						}else{
							chattingSelectShm.put("muid" ,chattingUser.getString("muid"));
						}
					}
					new A903LMSTask(context,api,chattingSelectShm) {
						@Override
						public void success() throws Exception {
							if(A01_11Chatting.Companion.getHandler()!=null){
								Message msg=new Message();
								A01_11Chatting.Companion.getHandler().sendMessage(msg);
							}
						}
						@Override
						public void fail(String errmsg) throws Exception {
							Env.error(errmsg+"",new Exception());
						}
					};
				}
				sleep(1000L);
			}
		}catch(Exception e){
			Env.error("", e);
		}
	}

}
