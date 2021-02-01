package kr.coursemos.yonsei.a51custometc;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import kr.coursemos.yonsei.a99util.CMDB;
import kr.coursemos.yonsei.a99util.Env;
import kr.coursemos.yonsei.a99util.StaticClass;
import kr.coursemos.yonsei.a99util.StringHashMap;

public abstract class A903LMSTaskZ02Recv extends A903LMSTaskZ03Base {

	public A903LMSTaskZ02Recv(Context context, String serviceName, StringHashMap pShm) {
		super(context, serviceName, pShm);
	}

	protected void RECV_API_TEST(JSONObject jo) throws Exception {
	}
	protected void RECV_API_MULTIPART(JSONObject jo) throws Exception {
	}
	protected void RECV_API_MULTIPARTFILE(JSONObject jo) throws Exception {
	}
	protected void RECV_API_MULTIPARTVIDEO(JSONObject jo) throws Exception {
	}
	protected void RECV_API_VERSION(JSONObject jo) throws Exception {
//		errorMsg="버젼체크 합시다";
		if(jo.getString("result").equals("0000")){

		}else{
			errorMsg=jo.getString("message");
		}
	}

	protected void RECV_API_SELECTSCHOOL(JSONObject jo) throws Exception {
		if(jo.getString("result").equals("0000")){

		}else{
			errorMsg=jo.getString("message");
		}

	}

	protected void RECV_API_SNUSCHOOL(JSONObject jo) throws Exception {
		if(jo.getString("result").equals("0000")){

		}else{
			errorMsg=jo.getString("message");
		}

	}


	protected void RECV_API_MOKPOSCHOOL(JSONObject jo) throws Exception {
		if(jo.getString("result").equals("0000")){

		}else{
			errorMsg=jo.getString("message");
		}

	}



	protected void RECV_API_SOONCHUNHYANGSCHOOL(JSONObject jo) throws Exception {
		if(jo.getString("result").equals("0000")){

		}else{
			errorMsg=jo.getString("message");
		}

	}

	protected void RECV_API_JBNUSCHOOL(JSONObject jo) throws Exception {
		if(jo.getString("result").equals("0000")){

		}else{
			errorMsg=jo.getString("message");
		}

	}

	protected void RECV_API_LOGIN(JSONObject jo) throws Exception {
		JSONObject data = null;
		data = jo.getJSONObject("data");
		data.put("userid", pShm.getString("userid"));
		data.put("userpw", pShm.getString("password"));
		insertDB(API_LOGIN,API_LOGIN, data.getString("id"), data.toString(), CMDB.TABLE01_common);
		insertDB(API_LOGINCHECK,API_LOGINCHECK, data.getString("id"),"", CMDB.TABLE01_common);
		edit.putString(StaticClass.KEY_USERTOKEN, data.getString("utoken"));
		edit.putString(StaticClass.KEY_USERSEQ, data.getString("id"));
		edit.putString(StaticClass.KEY_FIRSTNAME, data.getString("firstname"));
		edit.putString(StaticClass.KEY_LASTNAME, data.getString("lastname"));
		edit.putString(StaticClass.KEY_IDNUMBER, data.getString("idnumber"));
		edit.putString(StaticClass.KEY_EMAIL, data.getString("email"));
		edit.putString(StaticClass.KEY_INSTITUTION, data.getString("institution"));
		edit.putString(StaticClass.KEY_DEPARTMENT, data.getString("department"));
		edit.putString(StaticClass.KEY_PHONE1, data.getString("phone1"));
		edit.putString(StaticClass.KEY_PHONE2, data.getString("phone2"));
		edit.putString(StaticClass.KEY_LANG, data.getString("lang"));
		edit.putString(StaticClass.KEY_PICURL, data.getString("profileimageurl"));
		edit.putString(StaticClass.KEY_ACCOUNT,data.getString("accounts"));
		edit.remove(StaticClass.KEY_SUBJECTID);
		edit.commit();
		String selectSeq=sp.getString(StaticClass.KEY_SELECTUSERSEQ,"");
		if(selectSeq.equals("")||selectSeq.equals(data.getString("id"))){
		}else{
			pShm.put("userid",sp.getString(StaticClass.KEY_SELECTUSERID,""));
			serviceNames.add(API_CHANGEUSER);
		}

		serviceNames.add(API_INQUIRYLIST);

	}

	protected void RECV_API_GETUSERINFO(JSONObject jo) throws Exception {
		JSONObject data = null;
		data = jo.getJSONObject("data");
		edit.putString(StaticClass.KEY_PICURL, data.getString("profileimageurl"));
		edit.commit();
	}

	protected void RECV_API_CHANGEUSER(JSONObject jo) throws Exception {
		JSONObject data = null;
		data = jo.getJSONObject("data");
		insertDB(API_LOGIN,API_LOGIN, data.getString("id"), data.toString(), CMDB.TABLE01_common);
		insertDB(API_LOGINCHECK,API_LOGINCHECK, data.getString("id"),"", CMDB.TABLE01_common);
		edit.putString(StaticClass.KEY_USERTOKEN, data.getString("utoken"));
		edit.putString(StaticClass.KEY_USERSEQ, data.getString("id"));
		edit.putString(StaticClass.KEY_SELECTUSERSEQ, data.getString("id"));
		edit.putString(StaticClass.KEY_FIRSTNAME, data.getString("firstname"));
		edit.putString(StaticClass.KEY_LASTNAME, data.getString("lastname"));
		edit.putString(StaticClass.KEY_IDNUMBER, data.getString("idnumber"));
		edit.putString(StaticClass.KEY_EMAIL, data.getString("email"));
		edit.putString(StaticClass.KEY_INSTITUTION, data.getString("institution"));
		edit.putString(StaticClass.KEY_DEPARTMENT, data.getString("department"));
		edit.putString(StaticClass.KEY_PHONE1, data.getString("phone1"));
		edit.putString(StaticClass.KEY_PHONE2, data.getString("phone2"));
		edit.putString(StaticClass.KEY_LANG, data.getString("lang"));
		edit.putString(StaticClass.KEY_PICURL, data.getString("profileimageurl"));
		edit.putString(StaticClass.KEY_ACCOUNT,data.getString("accounts"));
		edit.remove(StaticClass.KEY_SUBJECTID);
		edit.commit();
	}
	protected void RECV_API_MAINBOARD(JSONObject jo) throws Exception {
		JSONArray ja = jo.getJSONArray("data");
		JSONObject board=null;
		int size=ja.length();
		CMDB cmdb= CMDB.open(context);
		cmdb.delete(API_MAINBOARD, API_MAINBOARD, null, CMDB.TABLE01_common);
		for(int i=0;i<size;i++){
			board=ja.getJSONObject(i);
			insertDB(API_MAINBOARD,API_MAINBOARD,board.getString("id"),board.toString(), CMDB.TABLE01_common);
		}

	}

	protected void RECV_API_HAKSA(JSONObject jo) throws Exception {
		JSONObject data = jo.getJSONObject("data");
		insertDB(API_HAKSA,API_HAKSA, StaticClass.KEY_SEMESTERS, data.getString("semesters"), CMDB.TABLE01_common);
		insertDB(API_HAKSA,API_HAKSA, StaticClass.KEY_LMSSTART,  data.getString("start_semester"), CMDB.TABLE01_common);
		insertDB(API_HAKSA,API_HAKSA, StaticClass.KEY_CURRENTSEMESTER,  data.getString("current_semester"), CMDB.TABLE01_common);
		edit.putString(StaticClass.KEY_SEMESTERS, data.getString("semesters"));
		edit.putString(StaticClass.KEY_LMSSTART, data.getString("start_semester"));
		edit.putString(StaticClass.KEY_CURRENTSEMESTER, data.getString("current_semester"));
		edit.commit();

	}

	protected void RECV_API_NOTIFICATIONCOUNT(JSONObject jo) throws Exception {
		edit.putString(StaticClass.KEY_NOTICOUNT, jo.getString("data"));
		edit.commit();
	}

	protected void RECV_API_CHATCOUNT(JSONObject jo) throws Exception {
		edit.putString(StaticClass.KEY_CHATCOUNT, jo.getString("data"));
		edit.commit();
	}

	protected void RECV_API_INQUIRYLIST(JSONObject jo) throws Exception {
		if(jo.getString("result").equals("0000")){
			CMDB cmdb= CMDB.open(context);
			cmdb.delete(API_INQUIRYLIST,API_INQUIRYLIST,null, CMDB.TABLE01_common);
			insertDB(API_INQUIRYLIST,API_INQUIRYLIST,"cid" , jo.getJSONArray("data"), CMDB.TABLE01_common);
		}else{
			errorMsg=jo.getString("message");
		}

	}
	protected void RECV_API_INQUIRYSEND(JSONObject jo) throws Exception {
		if(jo.getString("result").equals("0000")){
		}else{
			errorMsg=jo.getString("message");
		}
	}

	protected void RECV_API_INQUIRYSENDFILE(JSONObject jo) throws Exception {
		if(jo.getString("result").equals("0000")){
		}else{
			errorMsg=jo.getString("message");
		}
	}

	public void DeleteDir(String path) { 
	    File file = new File(path);
	    if(file.exists()){
	    	 File[] childFileList = file.listFiles();
		     for(File childFile : childFileList){
		         if(childFile.isDirectory()) {
		             DeleteDir(childFile.getAbsolutePath());     //하위 디렉토리 루프 
		         } else {
		             childFile.delete();    //하위 파일삭제
		         }
		     }      
		    file.delete();    //root 삭제
	    }
	}


	protected void RECV_API_PUSHSET(JSONObject jo) throws Exception {
		if(jo.getString("_status").equals("OK")){
//			edit.putString("_etag",jo.getString("_etag"));
//			edit.putString("_id", jo.getString("_id"));
		}else{
//			edit.remove("_etag");
//			edit.remove("_id");
		}
//		edit.commit();
	}
	protected void RECV_API_PUSHLOGOUT(JSONObject jo) throws Exception {
		edit.remove("_etag");
		edit.remove("_id");
		edit.commit();
	}
	protected void RECV_API_LOGOUT(JSONObject jo) throws Exception {
		edit.remove("_etag");
		edit.remove("_id");
		edit.commit();
	}
	protected void RECV_API_BIOAUTH(JSONObject jo) throws Exception {
	}
	protected void RECV_API_BIOREG(JSONObject jo) throws Exception {
	}
	protected void RECV_API_CURRENTSUBJECT(JSONObject jo) throws Exception {
		JSONArray data=jo.getJSONArray("data");
		JSONObject detail=null;
		insertDB(API_CURRENTSUBJECT, API_CURRENTSUBJECT, data, CMDB.TABLE01_03SubjectList);
		int size=data.length();
		CMDB cmdb= CMDB.open(context);
		String id=null;
		String START=null;
		String END=null;
		for(int i=0;i<size;i++){
			detail=data.getJSONObject(i);
			if(detail.getString("course_type").equals("R")&&!detail.isNull("day_cd")&&!detail.getString("day_cd").equals("")&&!detail.getString("hour1").equals("")&&!detail.getString("room_nm").equals("")){
				START= Env.getLongToDay( detail.getString("study_start"),"");
				END= Env.getLongToDay(detail.getString("study_end"), "");
				id=detail.getString("id");
				cmdb.insertORupdate(START,END,id,detail.toString(), CMDB.TABLE01_03z01TimeTable);
			}
		}


//		course_type  TABLE01_03z01TimeTable
	}

	protected void RECV_API_PERIODSUBJECT(JSONObject jo) throws Exception {
		insertDB(API_PERIODSUBJECT, API_PERIODSUBJECT, jo.getJSONArray("data"), CMDB.TABLE01_03SubjectList);
	}
	protected void RECV_API_ALLSUBJECT(JSONObject jo) throws Exception {
	}
	protected void RECV_API_SCHEDULESELECT(JSONObject jo) throws Exception {
		CMDB.open(context).DBClear( CMDB.TABLE01_15Schedule);
		insertDB(API_SCHEDULESELECT, API_SCHEDULESELECT, jo.getJSONObject("data").getJSONArray("events"), CMDB.TABLE01_15Schedule);
	}
	protected void RECV_API_OPENSUBJECT(JSONObject jo) throws Exception {
//		JSONObject data = jo.getJSONObject("data");
//		JSONArray ja=data.getJSONArray("irregular_list");
//		int size = ja.length();
//		CMDB cmdb = CMDB.open(context);
//		for (int i = 0; i < size; i++) {
//			jo = ja.getJSONObject(i);
//			if(jo.isNull("is_guest_enrol")||jo.getInt("is_guest_enrol")==0){
//				cmdb.insertORupdate(open, jo.getString("id"), jo.toString(), CMDB.TABLE01_subjectList);
//			}
//		}
	}
	protected void RECV_API_SUBJECTDETAIL(JSONObject jo) throws Exception {
		JSONArray ja = null;
		String subjectID = pShm.getString("courseid");
		CMDB cmdb = CMDB.open(context);
		cmdb.delete(API_SUBJECTDETAIL,subjectID,null, CMDB.TABLE01_04SubjectDetail);
		insertDB(API_SUBJECTDETAIL,subjectID,jo.getJSONArray("data"), CMDB.TABLE01_04SubjectDetail );
		Cursor cursor = cmdb.select(API_SUBJECTDETAIL,subjectID,null, CMDB.TABLE01_04SubjectDetail);
		int index = cursor.getColumnIndex(CMDB.COLUMNjson);
		JSONObject content = null;
		String modname = null;
		int size = 0;
		for(int j=0; cursor.moveToNext();j++) {
			jo = new JSONObject(cursor.getString(index));
			ja = jo.getJSONArray("modules");
			size = ja.length();
			for (int i = 0; i < size; i++) {
				content = ja.getJSONObject(i);
				modname = content.getString("modname");
				if (modname.equals("ubboard")) {
					if(j==0){
						cmdb.insertORupdate(subjectID,"menu",content.getString("id"), content.toString(), CMDB.TABLE01_04z01BoardInfo);
					}
					cmdb.insertORupdate(subjectID, "all",content.getString("id"), content.toString(), CMDB.TABLE01_04z01BoardInfo);
				}
			}
		}
	}

	protected void RECV_API_SUBJECTDETAILMENUINFO(JSONObject jo) throws Exception {

		edit.putString(StaticClass.KEY_SELECTCOURSEMENULIST,jo.getString("data"));
		edit.commit();
	}

	protected void RECV_API_SUBJECTWEEKLY(JSONObject jo) throws Exception {
		CMDB cmdb= CMDB.open(context);
		cmdb.delete(API_SUBJECTWEEKLY, pShm.getString("courseid"), null, CMDB.TABLE01_weekly);
		insertDB(API_SUBJECTWEEKLY,pShm.getString("courseid"),jo.getJSONArray("data"), CMDB.TABLE01_weekly);
	}

	protected void RECV_API_ADOBECONNECT(JSONObject jo) throws Exception {
		insertDB(API_ADOBECONNECT, API_ADOBECONNECT,API_ADOBECONNECT, jo.getJSONObject("data").toString(), CMDB.TABLE01_common);
	}


	protected void RECV_API_BOARD(JSONObject jo) throws Exception {

		JSONObject data =jo.getJSONObject("data");
		JSONArray notice_list=data.getJSONArray("notice_list");
		JSONArray article_list=data.getJSONArray("article_list");
		pShm.put("COUNT", Integer.toString(article_list.length()));
		insertDB(API_BOARD, "notice_list", notice_list, CMDB.TABLE01_10BoardList);
		insertDB(API_BOARD,"article_list",article_list, CMDB.TABLE01_10BoardList);
	}
	protected void RECV_API_BOARDAUTHORITY(JSONObject jo) throws Exception {
		insertDB(API_BOARDAUTHORITY,API_BOARDAUTHORITY,API_BOARDAUTHORITY,jo.getJSONObject("data").toString(), CMDB.TABLE01_common);
	}
	protected void RECV_API_BOARDDETAIL(JSONObject jo) throws Exception {
		JSONObject data=jo.getJSONObject("data");
		insertDB(API_BOARDDETAIL, API_BOARDDETAIL, data.getString("id"), data.toString(), CMDB.TABLE01_10z01BoardDetail);
	}
	protected void RECV_API_COMMENTSELECT(JSONObject jo) throws Exception {
		JSONObject data=jo.getJSONObject("data");
		CMDB.open(context).delete(API_COMMENTSELECT, API_COMMENTSELECT, null, CMDB.TABLE01_10z01BoardDetail);
		if(data.getBoolean("is")){
			insertDB(API_COMMENTSELECT,API_COMMENTSELECT, data.getJSONArray("comment"), CMDB.TABLE01_10z01BoardDetail);
			edit.putBoolean(StaticClass.KEY_ISBOARDDETAILCOMMENT,true);
		}else{
			edit.putBoolean(StaticClass.KEY_ISBOARDDETAILCOMMENT, false);
		}
		edit.commit();
	}
	protected void RECV_API_BOARDDELETE(JSONObject jo) throws Exception {
	}
	protected void RECV_API_COMMENTWRITE(JSONObject jo) throws Exception {
	}
	protected void RECV_API_BOARDWRITE(JSONObject jo) throws Exception {
	}

	protected void RECV_API_STUDENTLIST(JSONObject jo) throws Exception {
		JSONArray ja = jo.getJSONObject("data").getJSONArray("participants");
		JSONObject data = null;
		int size = ja.length();
		JSONArray roles=null;
		JSONObject role=null;
		int roleSize=0;
		String roleName="";
		String courseid = pShm.getString("courseid");
		pShm.put("COUNT", Integer.toString(size));
		CMDB cmdb = CMDB.open(context);
		for (int i = 0; i < size; i++) {
			data = ja.getJSONObject(i);
			roles=data.getJSONArray("roles");
			roleSize=roles.length();
			for(int j=0;j<roleSize;j++){
				role=roles.getJSONObject(j);
				roleName=role.getString("roleid");
				if (roleName.equals("3")||roleName.equals("1")) {
					cmdb.insertORupdate(API_PROFESSORLIST,"pro", data.getString("id"), data.toString(), CMDB.TABLE01_05ProfessorList);
				}else if (roleName.equals("9")) {
					cmdb.insertORupdate(API_PROFESSORLIST,"assi", data.getString("id"), data.toString(), CMDB.TABLE01_05ProfessorList);
				} else if (roleName.equals("5")||roleName.equals("10")||roleName.equals("11")){
					cmdb.insertORupdate(API_STUDENTLIST, "stu",data.getString("id"), data.toString(), CMDB.TABLE01_06StudentList);
				}
			}

		}
	}
	protected void RECV_API_PROFESSORLIST(JSONObject jo) throws Exception {
		JSONObject info=null;
		JSONObject data=jo.getJSONObject("data");
//		JSONArray main=data.getJSONArray("main");
		JSONArray professors=data.getJSONArray("professors");
		CMDB cmdb = CMDB.open(context);
//		pShm.put("COUNT", Integer.toString(main.length()+professors.length()));
//		int size = main.length();
//		for (int i = 0; i < size; i++) {
//			info=main.getJSONObject(i);
//			cmdb.insertORupdate(API_PROFESSORLIST,"assi", info.getString("id"), info.toString(), CMDB.TABLE01_05ProfessorList);
//		}
		pShm.put("COUNT", Integer.toString(0));
		int size = professors.length();
		for (int i = 0; i < size; i++) {
			info=professors.getJSONObject(i);
			cmdb.insertORupdate(API_PROFESSORLIST,"pro", info.getString("id"), info.toString(), CMDB.TABLE01_05ProfessorList);
		}
	}

	protected void RECV_API_LASTCHATTINGLIST(JSONObject jo) throws Exception {
		JSONObject data=jo.getJSONObject("data");
		JSONArray ja=data.getJSONArray("lists");
		pShm.put("COUNT", Integer.toString(ja.length()));
		if(pShm.getString("page").equals("1")){
			CMDB cmdb= CMDB.open(context);
			cmdb.DBClear(CMDB.TABLE01_16ChattingList);
		}
		insertDB(API_LASTCHATTINGLIST, API_LASTCHATTINGLIST,"muid",ja, CMDB.TABLE01_16ChattingList);
	}
	protected void RECV_API_CHATTINGSELECT(JSONObject jo) throws Exception {
		JSONObject data=jo.getJSONObject("data");
		JSONObject partnerinfo=data.getJSONObject("partnerinfo");
		JSONArray ja=data.getJSONArray("messages");
		int size = ja.length();
		CMDB cmdb = CMDB.open(context);
		JSONObject content = null;
		String type=partnerinfo.getString("id");

		for (int i = 0; i < size; i++) {
			content = ja.getJSONObject(i);
			content.put("profileimageurlsmall",partnerinfo.getString("profileimageurlsmall"));
			cmdb.insertORupdate(type, content.getString("useridto"), content.getString("timecreated"), content.toString(), CMDB.TABLE01_11ChattingDetail);
		}
	}
	protected void RECV_API_CHATTINGSELECT_GROUP(JSONObject jo) throws Exception {

		JSONObject data=jo.getJSONObject("data");
		JSONArray users=data.getJSONArray("users");
		int size = users.length();
		CMDB cmdb = CMDB.open(context);
		JSONObject user = null;

		for (int i = 0; i < size; i++) {
			user = users.getJSONObject(i);
			if(user.getString("idnumber").equals(sp.getString(StaticClass.KEY_IDNUMBER,""))){
				user.put("muid",user.getString("id"));
				user.put("useridfrom",user.getString("id"));
				user.put("fullmessage",data.getString("message"));
				cmdb.insertORupdate(pShm.getString("userid"), user.getString("id"), Integer.toString(i), user.toString(), CMDB.TABLE01_11ChattingDetail);
				break;
			}
		}

	}

	protected void RECV_API_CHATTINGSEND(JSONObject jo) throws Exception {
	}
	protected void RECV_API_NOTIFICATION(JSONObject jo) throws Exception {
		JSONObject data=jo.getJSONObject("data");
		JSONArray ja=data.getJSONArray("lists");
		pShm.put("COUNT", Integer.toString(ja.length()));
		if(pShm.getString("page").equals("1")){
			CMDB cmdb= CMDB.open(context);
			cmdb.DBClear(CMDB.TABLE01_20Alarm);
		}

		insertDB(API_NOTIFICATION, API_NOTIFICATION, ja, CMDB.TABLE01_20Alarm);
	}
	protected void RECV_API_NOTIFICATIONALLUPDATE(JSONObject jo) throws Exception {
	}
	protected void RECV_API_MYATTENDANCE(JSONObject jo) throws Exception {
		JSONObject data=new JSONObject(jo.getString("data"));
		JSONArray ja=data.getJSONArray("attendance");
		String subjectID=pShm.getString("courseid");
		int size = ja.length();
		CMDB cmdb = CMDB.open(context);
		for (int i = 0; i < size; i++) {
			cmdb.insertORupdate(API_MYATTENDANCE, subjectID,Integer.toString(i) ,ja.getJSONObject(i).toString(), CMDB.TABLE01_08AttendanceStudent);
		}
		insertDB(API_MYATTENDANCE,"status_count",subjectID,data.getJSONObject("status_count").toString(), CMDB.TABLE01_08AttendanceStudent);
	}
	protected void RECV_API_MYAUTOATTENDANCE(JSONObject jo) throws Exception {

	}
	protected void RECV_API_ATTENDANCESETTING(JSONObject jo) throws Exception {
	}

	protected void RECV_API_STUDENTATTENDANCE(JSONObject jo) throws Exception {
		JSONArray ja=jo.getJSONObject("data").getJSONArray("users");
		int size = ja.length();
		pShm.put("COUNT", Integer.toString(size));
		String courseid = pShm.getString("courseid");
		String week=pShm.getString("week");
		insertDB(courseid,week,ja, CMDB.TABLE01_07AttendanceTeacher);
	}
	protected void RECV_API_REPORTSELECT(JSONObject jo) throws Exception {
		String courseid=pShm.getString("courseid");
		JSONArray data=jo.getJSONArray("data");
		int size=data.length();
		for(int i=0;i<size;i++){
			RECV_API_REPORTSELECT_INSERDB(data.getJSONObject(i),courseid);
		}
	}
	protected void RECV_API_VIDEOCONTROL(JSONObject jo) throws Exception {
	}

	protected void RECV_API_ATTENDANCEAUTOCHECK(JSONObject jo) throws Exception {
		JSONObject data=jo.getJSONObject("data");
		JSONArray duration=data.getJSONArray("allow_duration");
		JSONArray autoinfo=new JSONArray();
		if(data.getInt("used")==1){
			autoinfo.put(data.getJSONObject("autoinfo"));
		}
		CMDB cmdb= CMDB.open(context);
		cmdb.DBClear(CMDB.TABLE01_07Z91AutoAttendanceTeacher);
		int size=duration.length();
		String tempString=null;
		for(int i=0;i<size;i++){
			tempString=duration.getString(i);
			cmdb.insertORupdate(API_ATTENDANCEAUTOCHECK,"duration",tempString,tempString, CMDB.TABLE01_07Z91AutoAttendanceTeacher);
		}
		size=autoinfo.length();
		for(int i=0;i<size;i++){
			insertDB(API_ATTENDANCEAUTOCHECK, "autoinfo", "attid", autoinfo, CMDB.TABLE01_07Z91AutoAttendanceTeacher);
		}
	}
	protected void RECV_API_ATTENDANCEAUTOCHECKSTART(JSONObject jo) throws Exception {
	}
	protected void RECV_API_ATTENDANCEAUTOCHECKEND(JSONObject jo) throws Exception {
	}
	protected void RECV_API_ATTENDANCEAUTOCHECKSTUDENT(JSONObject jo) throws Exception {
	}
	protected void RECV_API_ATTENDANCEAUTOCHECKSTUDENTREQ(JSONObject jo) throws Exception {
	}

	protected void RECV_API_BIOPASS(JSONObject jo) throws Exception {
	}
	protected void RECV_API_BIOPASSDELETE(JSONObject jo) throws Exception {
	}
	protected void RECV_API_BIOPASSPUSH(JSONObject jo) throws Exception {
	}
	protected void RECV_API_MOBILECODESEND(JSONObject jo) throws Exception {
		if(jo.getInt("code")==100){

		}else{
			errorMsg=jo.getString("message");
		}

	}

	protected void RECV_API_REPORTSELECT_INSERDB(JSONObject jo,String courseid) throws Exception {

		String childrenString=jo.getString("children");
		if(childrenString.equals("")){
		}else{
			JSONObject children=new JSONObject(childrenString);
			JSONArray keyList= children.names();
			int size=keyList.length();
			for(int i=0;i<size;i++){
				RECV_API_REPORTSELECT_INSERDB(children.getJSONObject(keyList.getString(i)),courseid);
			}
		}
		insertDB(API_REPORTSELECT, courseid, jo.getString("id"), jo.toString(), CMDB.TABLE01_18MyReport);
	}

}// sendTask
