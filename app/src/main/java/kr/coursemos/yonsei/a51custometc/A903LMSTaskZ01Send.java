package kr.coursemos.yonsei.a51custometc;

import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;

import org.json.JSONObject;

import kr.coursemos.yonsei.R;
import kr.coursemos.yonsei.a99util.CMDB;
import kr.coursemos.yonsei.a99util.Env;
import kr.coursemos.yonsei.a99util.StaticClass;
import kr.coursemos.yonsei.a99util.StringHashMap;

import static kr.coursemos.yonsei.a99util.StaticClass.KEY_IDNUMBER;

public abstract class A903LMSTaskZ01Send extends A903LMSTaskZ02Recv {

	public A903LMSTaskZ01Send(Context context, String serviceName, StringHashMap pShm) {
		super(context, serviceName, pShm);
	}

	protected void SEND_API_TEST() throws Exception {
//		URL="http://checkappver.appspot.com/CheckBBVersion.html";
//		URL="http://210.122.3.60:5525/alfresco/wcservice/ubion/smart/ui_001/003";
//		URL="http://192.168.0.123:7444/alfresco/wcservice/ubion/smart/ui_039/001";
		URL="http://222.121.122.93:7444/test.html";

//		shm.put("fullstring",pShm.getString("fullstring"));
//		HEADER.put("Content-Type","application/json;charset=utf-8");
	}

	protected void SEND_API_BIOAUTH() throws Exception {

		URL="http://15.165.111.76/bio/auth";
		copySHM();
//		shm.put("user", uniqueID);

	}

	protected void SEND_API_BIOREG() throws Exception {

		URL="http://15.165.111.76/bio/reg";
		copySHM();
//		shm.put("user", uniqueID);

	}

	protected void SEND_API_MULTIPART() throws Exception {
		if(pShm.getString("filePath").equals("")){
			shm.put("options[0][name]", "deletepicture");
			shm.put("options[0][value]", "1");
		}
//		copySHM();
//		if(pShm.getString("filePath").lastIndexOf(".mp4")>0){
//			shm.put("fileName","androidFile_"+System.currentTimeMillis()+".mp4");
//		}else{
//			shm.put("fileName","androidFile_"+System.currentTimeMillis()+".png");
//		}
//		shm.put("name", "userpicture");
//		shm.put("mimeType", "image/png");

		
	}

	protected void SEND_API_MULTIPARTFILE() throws Exception {

//		URL="http://222.121.122.93:7444/CheckBBVersion.aaa";
		copySHM();
		serviceNames.add(API_SUBJECTDETAIL);
	}
	protected void SEND_API_MULTIPARTVIDEO() throws Exception {
//		URL="http://222.121.122.93:7444/CheckBBVersion.aaa";
		copySHM();
		serviceNames.add(API_SUBJECTDETAIL);
	}

	protected void SEND_API_MULTIPARTFACEREG() throws Exception {

		URL="http://manager.coursemos.kr/face_register";
		copySHM();
	}

	protected void SEND_API_MULTIPARTFACECOMP() throws Exception {

		URL="http://manager.coursemos.kr/face_compare";
		copySHM();
//		serviceNames.add(API_MULTIPARTFACECOMP2);
	}


	protected void SEND_API_MULTIPARTFACECOMP2() throws Exception {

		URL="http://manager.coursemos.kr/face_diff_compare";
		pShm.remove("filePath");
		shm.clear();
		shm.put("method","GET");
		shm.put("face1",sp.getString(StaticClass.KEY_ID,"")+"_"+sp.getString(StaticClass.KEY_SELECTSCHOOLTOKEN,"")+".jpg");
		shm.put("face2",sp.getString(StaticClass.KEY_ID,"")+"_"+sp.getString(StaticClass.KEY_SELECTSCHOOLTOKEN,"")+"_check.jpg");
	}


	protected void API_MARKETVERSION() throws Exception {
		URL = "https://play.google.com/store/apps/details?id=";
		shm.clear();
	}
	protected void SEND_API_VERSION() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_com_update.php";
		shm.put("deviceType", "7");
		shm.put("identity", "kookmin");
		shm.put("version", StaticClass.version.replaceAll("\\.", ""));
	}

	protected void SEND_API_SELECTSCHOOL() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_com_school_with_id.php";
		shm.clear();
		copySHM();
	}
	protected void SEND_API_SNUSCHOOL() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_snu_university.php";
		shm.clear();
		shm.put("dev", pShm.getString("isDev"));

	}

	protected void SEND_API_MOKPOSCHOOL() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_mokpo_university.php";
		shm.clear();
		shm.put("dev", pShm.getString("isDev"));

	}


	protected void SEND_API_SOONCHUNHYANGSCHOOL() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_sch_university.php";
		shm.clear();
		shm.put("dev", pShm.getString("isDev"));

	}

	protected void SEND_API_JBNUSCHOOL() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_jbnu_university.php";
		shm.clear();
		shm.put("dev", pShm.getString("isDev"));
	}


	protected void SEND_API_LOGIN() throws Exception {
//        URL ="http://222.121.122.93:7444/dalbit/debug";
		URL =sp.getString(StaticClass.KEY_SELECTSCHOOLURL,"").split("/webservice/rest/server.php")[0]+"/local/coursemos/login.php";
        shm.put("wstoken",sp.getString(StaticClass.KEY_SELECTSCHOOLTOKEN, ""));
		shm.put("userid", pShm.getString("userid"));
		shm.put("password", pShm.getString("password"));

		serviceNames.add(API_HAKSA);
		serviceNames.add(API_MAINBOARD);
		serviceNames.add(API_NOTIFICATIONCOUNT);
	}

	protected void SEND_API_GETUSERINFO() throws Exception {
		shm.put("username", sp.getString(StaticClass.KEY_IDNUMBER,""));

	}

	protected void SEND_API_SSOLOGIN() throws Exception {
		URL=null;
		serviceNames.add(API_HAKSA);
		serviceNames.add(API_MAINBOARD);
		serviceNames.add(API_NOTIFICATIONCOUNT);

	}
	protected void SEND_API_CHANGEUSER() throws Exception {
		shm.put("userid", pShm.getString("userid"));
		pShm.put("isPeriod", StaticClass.NO);
		serviceNames.add(API_CURRENTSUBJECT);
	}
	private boolean userChange(String id) throws Exception {
		CMDB cmdb = CMDB.open(context);
		String idInfo = cmdb.selectString(A903LMSTask.API_LOGIN, A903LMSTask.API_LOGIN, id, CMDB.TABLE01_common);
		if (idInfo == null) {
			errorMsg = context.getString(R.string.a00_01activityIDChangeFail);
			return false;
		} else {
			cmdb.DBClear(false);
			JSONObject data = new JSONObject(idInfo);
			edit.putString(StaticClass.KEY_ID, data.getString("userid"));
			edit.putString(StaticClass.KEY_PW, data.getString("userpw"));
			edit.putString(StaticClass.KEY_USERTOKEN, data.getString("utoken"));
			edit.putString(StaticClass.KEY_USERSEQ, data.getString("id"));
			edit.putString(StaticClass.KEY_FIRSTNAME, data.getString("firstname"));
			edit.putString(StaticClass.KEY_LASTNAME, data.getString("lastname"));
			edit.putString(KEY_IDNUMBER, data.getString("idnumber"));
			edit.putString(StaticClass.KEY_EMAIL, data.getString("email"));
			edit.putString(StaticClass.KEY_INSTITUTION, data.getString("institution"));
			edit.putString(StaticClass.KEY_DEPARTMENT, data.getString("department"));
			edit.putString(StaticClass.KEY_PHONE1, data.getString("phone1"));
			edit.putString(StaticClass.KEY_PHONE2, data.getString("phone2"));
			edit.putString(StaticClass.KEY_LANG, data.getString("lang"));
			edit.putString(StaticClass.KEY_PICURL, data.getString("profileimageurl"));
			edit.remove(StaticClass.KEY_SUBJECTID);
			edit.commit();
//			StaticClass.history = new ArrayList<Class<?>>();
//			StaticClass.history.add(A01_03SubjectList.class);
//			String className = this.getClass().getName();
//			if (className.equals(A01_09Z02MyInfo.class.getName())) {
//				StaticClass.history.add(A01_09Setting.class);
//			}
//			StaticClass.history.add(this.getClass());
			return true;
		}
	}
	protected void SEND_API_MAINBOARD() throws Exception {
//		shm.put("wstoken", StaticClass.APItoken);
		serviceNames.add(API_INQUIRYLIST);
	}
	protected void SEND_API_HAKSA() throws Exception {
	}
	protected void SEND_API_NOTIFICATIONCOUNT() throws Exception {
	}
	protected void SEND_API_CHATCOUNT() throws Exception {
	}
	protected void SEND_API_INQUIRYLIST() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_cfg_category.php";
		String lang=shm.getString("lang");
		shm.clear();
		shm.put("lang",lang);
//		copySHM();
	}
	protected void SEND_API_INQUIRYSEND() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_cfg_qna_image.php";
		shm.clear();
		copySHM();
	}
	protected void SEND_API_INQUIRYSENDFILE() throws Exception {
		URL = StaticClass.DalbitAPI + "cos_cfg_qna_image.php";
		shm.clear();
		copySHM();
	}

	protected void SEND_API_PUSHSET() throws Exception {
		shm.clear();
		URL= StaticClass.PushAPI;
//		String _id=sp.getString("_id",null);
//		if(_id==null){
//			shm.put("method","POST");
//		}else{
//			shm.put("method","PUT");
//			HEADER.put("If-Match",sp.getString("_etag",""));
//			URL+="/"+_id;
//		}

		shm.put("method","POST");

		shm.put("cosid",sp.getString(StaticClass.KEY_ID, ""));
		shm.put("regid",sp.getString(StaticClass.KEY_REGID, ""));
		shm.put("userid",sp.getString(StaticClass.KEY_USERSEQ, ""));
		shm.put("school",sp.getString(StaticClass.KEY_SELECTSCHOOLTOKEN, ""));
		if(sp.getBoolean(StaticClass.KEY_ALARMONOFF,true)){
			shm.put("devicestate", "login");
		}else{
			shm.put("devicestate", "stop");
		}
		if(sp.getBoolean(StaticClass.KEY_ALARMPREVIEWONOFF,true)){
			shm.put("ispreview","1");
		}else{
			shm.put("ispreview","0");
		}

		shm.put("appversion", StaticClass.version.replaceAll("\\.", ""));
		try{
			shm.put("deviceuid", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)+"");
		}catch (Exception e) {
			shm.put("deviceuid", android.os.Build.DEVICE+"");
		}
		shm.put("devicename",android.os.Build.DEVICE+"");
		shm.put("devicemodel",android.os.Build.MODEL+"");
		shm.put("deviceversion",android.os.Build.VERSION.RELEASE+"");


		if(sp.getString(StaticClass.KEY_ID,"").indexOf("CsMs_")==0){

			Env.debug(context,sp.getString(StaticClass.KEY_REGID, ""));
			URL=null;
		}

	}



	protected void SEND_API_PUSHLOGOUT() throws Exception {

		shm.clear();
		URL= StaticClass.PushAPI;
		String _id=sp.getString("_id",null);
		if(_id==null){
			shm.put("method","POST");
		}else{
			shm.put("method","PUT");
			HEADER.put("If-Match",sp.getString("_etag",""));
			URL+="/"+_id;
		}
		shm.put("regid",sp.getString(StaticClass.KEY_REGID, ""));
		shm.put("userid",sp.getString(StaticClass.KEY_USERSEQ, ""));
		shm.put("school",sp.getString(StaticClass.KEY_SELECTSCHOOLTOKEN, ""));
		shm.put("appversion", StaticClass.version.replaceAll("\\.", ""));
		try{
			shm.put("deviceuid", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)+"");
		}catch (Exception e) {
			shm.put("deviceuid", android.os.Build.DEVICE+"");
		}
		shm.put("devicename",android.os.Build.DEVICE+"");
		shm.put("devicemodel",android.os.Build.MODEL+"");
		shm.put("deviceversion",android.os.Build.VERSION.RELEASE+"");
		shm.put("devicestate", "logout");
	}

	protected void SEND_API_LOGOUT() throws Exception {

		shm.clear();
		URL= StaticClass.PushAPI;
		String _id=sp.getString("_id",null);
		if(_id==null){
			shm.put("method","POST");
		}else{
			shm.put("method","PUT");
			HEADER.put("If-Match",sp.getString("_etag",""));
			URL+="/"+_id;
		}
		shm.put("regid",sp.getString(StaticClass.KEY_REGID, ""));
		shm.put("userid",sp.getString(StaticClass.KEY_USERSEQ, ""));
		shm.put("school",sp.getString(StaticClass.KEY_SELECTSCHOOLTOKEN, ""));
		shm.put("appversion", StaticClass.version.replaceAll("\\.", ""));
		try{
			shm.put("deviceuid", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)+"");
		}catch (Exception e) {
			shm.put("deviceuid", android.os.Build.DEVICE+"");
		}
		shm.put("devicename",android.os.Build.DEVICE+"");
		shm.put("devicemodel",android.os.Build.MODEL+"");
		shm.put("deviceversion",android.os.Build.VERSION.RELEASE+"");
		shm.put("devicestate", "logout");


		String id=pShm.getString("id");
		CMDB cmdb = CMDB.open(context);
		if (id == null) {
			Cursor cursor = cmdb.select(A903LMSTask.API_LOGIN, A903LMSTask.API_LOGIN,null, CMDB.TABLE01_common);
			int index=cursor.getColumnIndex(CMDB.COLUMNjson);
			JSONObject idInfo=null;
			StringBuilder sb=new StringBuilder();
			for(;cursor.moveToNext();){
				idInfo=new JSONObject(cursor.getString(index));
				sb.append(idInfo.getString("id")+",");
			}
			String IdString=sb.toString();
			cmdb.DBClear(true);
		} else {
			cmdb.delete(A903LMSTask.API_LOGIN, A903LMSTask.API_LOGIN, id, CMDB.TABLE01_common);
			cmdb.delete(A903LMSTask.API_LOGINCHECK, A903LMSTask.API_LOGINCHECK, id, CMDB.TABLE01_common);
		}

		cmdb.DBClear(true);
		edit.remove(StaticClass.KEY_ALARMSOUNDONOFF);
		edit.remove(StaticClass.KEY_ALARMVIBRATEONOFF);
		edit.remove(StaticClass.KEY_LOCKSCREENNUMBER);
		edit.remove(StaticClass.KEY_LOCKSCREENNUMBERCHECK);
		edit.putBoolean(StaticClass.KEY_LOCKSCREENONOFF, false);
		edit.putBoolean(StaticClass.KEY_REMEMBERID, false);
		edit.commit();
		pShm.put(API_LOGOUT, StaticClass.YES);

//		Cursor cursor = cmdb.select(A903LMSTask.API_LOGIN,A903LMSTask.API_LOGIN,null, CMDB.TABLE01_common);
//		if (cursor.moveToNext()) {
//			if (sp.getString(StaticClass.KEY_USERSEQ, "").equals(id)) {
//				int index = cursor.getColumnIndex(CMDB.COLUMNtype3);
//				pShm.put("changeid", cursor.getString(index));
//				serviceNames.add(API_CHANGEUSER);
//			}
//			pShm.put(API_LOGOUT, StaticClass.NO);
//		} else {
//			cmdb.DBClear(true);
//			edit.remove(StaticClass.KEY_ALARMSOUNDONOFF);
//			edit.remove(StaticClass.KEY_ALARMVIBRATEONOFF);
//			edit.remove(StaticClass.KEY_LOCKSCREENNUMBER);
//			edit.remove(StaticClass.KEY_LOCKSCREENNUMBERCHECK);
//			edit.putBoolean(StaticClass.KEY_LOCKSCREENONOFF, false);
//			edit.putBoolean(StaticClass.KEY_REMEMBERID, false);
//			edit.commit();
//			pShm.put(API_LOGOUT, StaticClass.YES);
//		}


	}
	protected void SEND_API_CURRENTSUBJECT() {
		if(pShm.getString("isPeriod").equals(StaticClass.YES)){
			serviceNames.add(API_PERIODSUBJECT);
		}
//		serviceNames.add(API_OPENSUBJECT);
	}

	protected void SEND_API_PERIODSUBJECT() {
		shm.put("year", pShm.get("year"));
		String temp=pShm.get("semester_code");
		if(temp!=null){
			shm.put("semester_code", temp);
		}

	}

	protected void SEND_API_OPENSUBJECT() {
		shm.put("page", "0");
		shm.put("ls", "999");
	}

	protected void SEND_API_SUBJECTDETAIL() throws Exception {
		copySHM();
		JSONObject menuList=new JSONObject(sp.getString(StaticClass.KEY_SELECTSCHOOLMENULIST,"{}"));
		if(menuList.isNull("patt")&&menuList.isNull("pautoatt")&&menuList.isNull("oatt")&&menuList.isNull("att")&&menuList.isNull("autoatt")){

		}else{
			serviceNames.add(API_SUBJECTWEEKLY);
		}
		serviceNames.add(API_SUBJECTDETAILMENUINFO);

	}

	protected void SEND_API_SUBJECTDETAILMENUINFO() throws Exception {
		copySHM();
	}

	protected void SEND_API_SUBJECTWEEKLY() throws Exception {
		copySHM();
	}

	protected void SEND_API_ADOBECONNECT() throws Exception {
		// shm.put("wstoken", StaticClass.APItoken);
		copySHM();
	}
	protected void SEND_API_BOARD() throws Exception {
		shm.put("cmid", pShm.getString("cmid"));
		shm.put("caid" ,pShm.getString("caid"));
		shm.put("keyfield" ,pShm.getString("keyfield"));
		shm.put("keyword" ,pShm.getString("keyword"));
		shm.put("ls" ,pShm.getString("ls"));
		shm.put("page", pShm.getString("page"));

		serviceNames.add(API_BOARDAUTHORITY);
	}
	protected void SEND_API_BOARDAUTHORITY() throws Exception {
		shm.put("cmid", pShm.getString("cmid"));
	}
	protected void SEND_API_BOARDDETAIL() throws Exception {
		shm.put("cmid", pShm.getString("cmid"));
		shm.put("bwid" ,pShm.getString("bwid"));
		serviceNames.add(API_COMMENTSELECT);
	}
	protected void SEND_API_COMMENTSELECT() throws Exception {
		shm.put("cmid", pShm.getString("cmid"));
		shm.put("bwid", pShm.getString("bwid"));
	}
	protected void SEND_API_BOARDDELETE() throws Exception {
		copySHM();
	}
	protected void SEND_API_COMMENTWRITE() throws Exception {
		copySHM();
		serviceNames.add(API_BOARDDETAIL);
	}
	protected void SEND_API_BOARDWRITE() throws Exception {
		copySHM();
	}
	protected void SEND_API_STUDENTLIST() throws Exception {
		copySHM();
//		CMDB cmdb = CMDB.open(context);
//		Cursor cursor = cmdb.select(null,null,sp.getString(StaticClass.KEY_SUBJECTID, ""), CMDB.TABLE01_03SubjectList);
//		int index = cursor.getColumnIndex(CMDB.COLUMNjson);
//		cursor.moveToNext();
//		JSONObject jo=new JSONObject(cursor.getString(index));
//		String course_type=jo.getString("course_type");
//		if(course_type.equals("CMS_E")){
//			shm.put("roleid", "5");
//		}

	}

	protected void SEND_API_PROFESSORLIST() throws Exception {
		shm.put("courseid", pShm.getString("courseid"));
	}

	protected void SEND_API_LASTCHATTINGLIST() throws Exception {
		copySHM();
	}

	protected void SEND_API_CHATTINGSELECT() throws Exception {
		shm.put("userid", pShm.getString("userid"));
		shm.put("historymonth", pShm.getString("userid"));
		serviceNames.add(API_CHATCOUNT);
	}
	protected void SEND_API_CHATTINGSELECT_GROUP() throws Exception {
		shm.put("muid", pShm.getString("muid"));
	}
	protected void SEND_API_CHATTINGSEND() throws Exception {
//		Object[] objs = pShm.keySet().toArray();
//		for (int i = 0; i < objs.length; i++) {
//			if ("userid".equals(objs[i])) {
//			} else {
//				shm.put((String) objs[i], pShm.getString((String) objs[i]));
//			}
//		}
		copySHM();
	}
	protected void SEND_API_MYATTENDANCE() throws Exception {
		serviceNames.add(API_SUBJECTWEEKLY);
		copySHM();
	}
	protected void SEND_API_MYAUTOATTENDANCE() throws Exception {
		URL=null;
		serviceNames.add(API_MYATTENDANCE);
		serviceNames.add(API_SUBJECTWEEKLY);
		serviceNames.add(API_ATTENDANCEAUTOCHECK);
	}

	protected void SEND_API_NOTIFICATION() throws Exception {
		copySHM();
	}
	protected void SEND_API_NOTIFICATIONALLUPDATE() throws Exception {
		serviceNames.add(API_NOTIFICATIONCOUNT);
		serviceNames.add(API_NOTIFICATION);
	}
	protected void SEND_API_ALLSUBJECT() throws Exception {
		URL=null;
//		serviceNames.add(API_OPENSUBJECT);
		serviceNames.add(API_SCHEDULESELECT);
	}
	protected void SEND_API_SCHEDULESELECT() throws Exception {
		copySHM();
	}
	protected void SEND_API_ATTENDANCESETTING() throws Exception {
		shm.put("courseid", pShm.getString("courseid"));
		shm.put("status", pShm.getString("status"));
		shm.put("attid", pShm.getString("attid"));
		shm.put("userid", pShm.getString("userid"));
		serviceNames.add(API_STUDENTATTENDANCE);
	}
	protected void SEND_API_STUDENTATTENDANCE() throws Exception {
		shm.put("courseid", pShm.getString("courseid"));
		shm.put("week", pShm.getString("week"));
		shm.put("page", pShm.getString("page"));
		shm.put("ls", pShm.getString("ls"));
	}
	protected void SEND_API_REPORTSELECT() throws Exception {
		copySHM();
	}

	protected void SEND_API_VIDEOCONTROL() throws Exception {
		copySHM();
	}

	protected void SEND_API_ATTENDANCEAUTOCHECK() throws Exception {
		shm.put("courseid", pShm.getString("courseid"));
	}
	protected void SEND_API_ATTENDANCEAUTOCHECKSTART() throws Exception {
		copySHM();
		serviceNames.add(API_ATTENDANCEAUTOCHECK);
	}
	protected void SEND_API_ATTENDANCEAUTOCHECKEND() throws Exception {
		copySHM();
		serviceNames.add(API_ATTENDANCEAUTOCHECK);
	}
	protected void SEND_API_ATTENDANCEAUTOCHECKSTUDENT() throws Exception {
		shm.put("courseid", pShm.getString("courseid"));
		shm.put("attid", pShm.getString("attid"));
	}
	protected void SEND_API_ATTENDANCEAUTOCHECKSTUDENTREQ() throws Exception {
		copySHM();
	}

    protected void SEND_API_BIOPASS() throws Exception {
        URL=pShm.getString("url");
		shm.clear();
		pShm.remove("url");
		copySHM();
//        shm.put("univCode", pShm.getString("univCode"));
//        shm.put("deviceInfo", pShm.getString("deviceInfo"));
    }
	protected void SEND_API_BIOPASSDELETE() throws Exception {
		URL=pShm.getString("url");
		shm.clear();
		pShm.remove("url");
		copySHM();
	}
	protected void SEND_API_BIOPASSPUSH() throws Exception {
		URL=sp.getString(StaticClass.KEY_BIOPASSPUSHURL,"");
		shm.clear();
	}
	protected void SEND_API_STONEPASSREG() throws Exception {
		URL="http://220.69.201.131/stonepass/Get";

		shm.put("fullstring",pShm.getString("fullstring"));
		HEADER.put("Content-Type","application/json;charset=utf-8");
	}
	protected void SEND_API_STONEPASSAUTH() throws Exception {
		URL="http://220.69.201.131/stonepass/Send/"+pShm.getString("op");
		shm.put("fullstring",pShm.getString("fullstring"));
		HEADER.put("Content-Type","application/json;charset=utf-8");
	}
	protected void SEND_API_STONEPASS3() throws Exception {
		URL="https://auth.ssenstone.com/app/svcif/easyauth/req";
		HEADER.put("Content-Type","application/json;charset=utf-8");
		shm.put("fullstring",pShm.getString("fullstring"));
	}
	protected void SEND_API_MOBILECODESEND() throws Exception {
		URL= StaticClass.API.split("/webservice/rest")[0]+"/local/ruauth/apis/sendMobileAuthCode.php";
		shm.clear();
		copySHM();
	}

	public void copySHM() throws Exception {
		Object[] objs = pShm.keySet().toArray();
		for (int i = 0; i < objs.length; i++) {
			shm.put((String) objs[i], pShm.getString((String) objs[i]));
		}
	}


}// sendTask
