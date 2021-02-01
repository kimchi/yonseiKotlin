package kr.coursemos.yonsei.a51custometc;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.okins.kc.A00_00Activity;
import kr.coursemos.yonsei.R;
import kr.coursemos.yonsei.a99util.CMCommunication;
import kr.coursemos.yonsei.a99util.CMDB;
import kr.coursemos.yonsei.a99util.CMMultiPart;
import kr.coursemos.yonsei.a99util.Env;
import kr.coursemos.yonsei.a99util.StaticClass;
import kr.coursemos.yonsei.a99util.StringHashMap;

public abstract class A903LMSTaskZ03Base extends AsyncTask<Void, Void, String[]> {
	public static String userAgent = null;
	
	public final static String API_MULTIPART="coursemos_user_update_v2";
	public final static String API_MULTIPARTFILE="coursemos_mod_ubfile_set_upload_v2";
	public final static String API_MULTIPARTVIDEO="coursemos_mod_vod_set_upload_v2";

	//마켓 버젼 체크
	public final static String API_MARKETVERSION = "API_MARKETVERSION";
	//모바일 code전송
	public final static String API_MOBILECODESEND = "API_MOBILECODESEND";

	// 버전체크
	public final static String API_VERSION = "cos_com_update";

	// 학교목록
	public final static String API_SELECTSCHOOL = "cos_com_school";

	// 생체인증 등록
	public final static String API_BIOREG = "API_BIOREG";

	// 생체인증 인증
	public final static String API_BIOAUTH = "API_BIOAUTH";

	// 서울대
	public final static String API_SNUSCHOOL = "API_SNUSCHOOL";

	// 순천향대
	public final static String API_SOONCHUNHYANGSCHOOL = "API_SOONCHUNHYANGSCHOOL";

	// 전북대
	public final static String API_JBNUSCHOOL = "API_JBNUSCHOOL";


	// 목포대
	public final static String API_MOKPOSCHOOL = "API_MOKPOSCHOOL";

	// 테스트
	public final static String API_TEST = "API_TEST";
	// 로그인 체크
	public final static String API_LOGINCHECK = "API_LOGINCHECK";
	// 로그인
	public final static String API_LOGIN = "coursemos_user_login_v2";

	// 사용자정보 조회
	public final static String API_GETUSERINFO = "coursemos_user_get_users_v2";
	// sso 로그인
	public final static String API_SSOLOGIN = "API_SSOLOGIN";

	// 문의하기 리스트
	public final static String API_INQUIRYLIST = "cos_cfg_category";

	// 문의하기전송
	public final static String API_INQUIRYSEND = "cos_cfg_qna";
	// 문의하기전송_파일
	public final static String API_INQUIRYSENDFILE="cos_cfg_qna_image";

	// 계정 전환
	public final static String API_CHANGEUSER = "coursemos_user_change_login_v2";

	// 메인 게시판List 조회
	public final static String API_MAINBOARD = "coursemos_site_get_main_links_v2";
	// 현재 학사 정보 조회
	public final static String API_HAKSA = "coursemos_haksa_get_semester_v2";
	// 알림카운트
	public final static String API_NOTIFICATIONCOUNT = "coursemos_notification_new_count_v2";

	// 알림목록
	public final static String API_NOTIFICATION = "coursemos_notifications_v2";

	// 알림목록 확인
	public final static String API_NOTIFICATIONALLUPDATE = "coursemos_notification_user_all_update_v2";


	// 강좌 리스트(진행강좌)
	public final static String API_CURRENTSUBJECT = "coursemos_course_get_mycourses_v2";
	// 강좌 리스트(지난강좌)
	public final static String API_PERIODSUBJECT = "coursemos_course_get_mycourses_period_v2";
	// 강좌 리스트(공개강좌)
	public final static String API_OPENSUBJECT = "coursemos_course_get_open_courses";

	// 강좌상세
	public final static String API_SUBJECTDETAIL = "coursemos_course_get_contents_v2";

	// 강좌메뉴정보
	public final static String API_SUBJECTDETAILMENUINFO = "coursemos_course_get_configs_v2";

	// 해당과목 주차 조회
	public final static String API_SUBJECTWEEKLY = "coursemos_attendance_get_weeks_periods_v2";

	// adobeconnect
	public final static String API_ADOBECONNECT = "coursemos_mod_adobeconnect_get_meetingtinfo_v2";


	// 교수 목록 조회
	public final static String API_PROFESSORLIST = "coursemos_course_get_professors_v2";
	// 수강생 목록 조회
	public final static String API_STUDENTLIST = "coursemos_course_get_participants_list_v2";

	// 게시물 작성
	public final static String API_BOARDWRITE = "coursemos_mod_ubboard_create_article_v2";
	// 게시물 삭제
	public final static String API_BOARDDELETE = "coursemos_mod_ubboard_delete_article_v2";

	// 게시물권한 조회
	public final static String API_BOARDAUTHORITY = "coursemos_mod_ubboard_get_auth_v2";
	// 게시물목록 조회
	public final static String API_BOARD = "coursemos_mod_ubboard_get_list_v2";


	// 게시물 상세 조회
	public final static String API_BOARDDETAIL = "coursemos_mod_ubboard_get_article_v2";

	// 댓글 작성
	public final static String API_COMMENTWRITE = "coursemos_mod_ubboard_create_comment_v2";

	// 댓글 조회
	public final static String API_COMMENTSELECT = "coursemos_mod_ubboard_get_comments_v2";


	// 안읽은메시지갯수
	public final static String API_CHATCOUNT = "coursemos_message_get_new_count_v2";

	// 마지막 메시지 조회
	public final static String API_LASTCHATTINGLIST = "coursemos_message_get_messages_v2";

	// 메시지 조회
	public final static String API_CHATTINGSELECT = "coursemos_message_get_message_history_v2";

	// 메시지 조회(그룹)
	public final static String API_CHATTINGSELECT_GROUP = "coursemos_message_get_group_message_v2";

	// 메시지 전송
	public final static String API_CHATTINGSEND = "coursemos_message_send_message_multiuser_v2";


	// 로그아웃
	public final static String API_LOGOUT = "API_LOGOUT";

	public final static String API_PUSHLOGOUT = "API_PUSHLOGOUT";


	// 푸쉬 토큰값 등록
	public final static String API_PUSHSET = "API_PUSHSET";

	// 일정조회
	public final static String API_SCHEDULESELECT = "coursemos_calendar_get_events_v2";
	// 모든강좌조회
	public final static String API_ALLSUBJECT = "coursemos_course_get_mycourses_current_v2_,,_2";


	// 동영상 진도관리
	public final static String API_VIDEOCONTROL = "coursemos_mod_vod_set_track_v2";


	// 나의 출석현황 조회
	public final static String API_MYATTENDANCE = "coursemos_attendance_get_mystatus_v2";

	// 나의 자동출석 조회
	public final static String API_MYAUTOATTENDANCE = "coursemos_attendance_get_mystatus_v2_,,_2";

	// 출석 상태 SEND
	public final static String API_ATTENDANCESETTING = "coursemos_attendance_set_status_v2";
	// 수강생 출석현황 조회
	public final static String API_STUDENTATTENDANCE = "coursemos_attendance_get_all_status_v2";
	// 성적 조회
	public final static String API_REPORTSELECT = "coursemos_grade_get_course_grades_user_v2";

	// 자동출결확인
	public final static String API_ATTENDANCEAUTOCHECK = "coursemos_attendance_check_auto_v2";

	// 자동출결시작
	public final static String API_ATTENDANCEAUTOCHECKSTART = "coursemos_attendance_set_auto_start_v2";

	// 자동출결종료
	public final static String API_ATTENDANCEAUTOCHECKEND = "coursemos_attendance_set_auto_end_v2";

	// 자동출결확인 학생
	public final static String API_ATTENDANCEAUTOCHECKSTUDENT = "coursemos_attendance_check_auto_user_v2";

	// 자동출결요청 학생
	public final static String API_ATTENDANCEAUTOCHECKSTUDENTREQ = "coursemos_attendance_set_auto_user_v2";


	// 얼굴인식
	public final static String API_BIOPASS = "API_BIOPASS";

	// 얼굴인식 삭제
	public final static String API_BIOPASSDELETE = "API_BIOPASSDELETE";

	// 얼굴인식푸시
	public final static String API_BIOPASSPUSH = "API_BIOPASSPUSH";

	//얼굴인식 등록
	public final static String API_MULTIPARTFACEREG = "API_MULTIPARTFACEREG";

	//얼굴인식 확인
	public final static String API_MULTIPARTFACECOMP = "API_MULTIPARTFACECOMP";
	//얼굴인식 확인2
	public final static String API_MULTIPARTFACECOMP2 = "API_MULTIPARTFACECOMP2";



	// 스톤패스 1
	public final static String API_STONEPASS1 = "API_STONEPASS1";

	// 스톤패스 2
	public final static String API_STONEPASS2 = "API_STONEPASS2";


	// 스톤패스 3
	public final static String API_STONEPASS3 = "API_STONEPASS3";



	protected String URL = null;
	public Context context = null;
	public ArrayList<String> serviceNames = null;
	public String serviceName = null;
	public String errorMsg = null;
	public String result = null;
	public StringHashMap shm = null;
	public StringHashMap pShm = null;
	public StringHashMap encryption = null;
	public StringHashMap HEADER = null;
	public SharedPreferences sp = null;
	public Editor edit = null;
	public CMCommunication CMcommu = null;
	public CMMultiPart CMmultiPart=null;
	public long priTime = 0;

	public A903LMSTaskZ03Base(Context context, String serviceName, StringHashMap pShm) {
		this.context = context;
		this.serviceName = serviceName;
		this.pShm = pShm;
		sp = context.getSharedPreferences(StaticClass.KEY_SharedPreferences, Service.MODE_PRIVATE);
		edit = sp.edit();
		priTime = System.currentTimeMillis();
		serviceNames = new ArrayList<String>();
		shm = new StringHashMap();
		encryption = new StringHashMap();
		HEADER = new StringHashMap();
		execute();
	}

	@Override
	protected void onPostExecute(String[] result) {
		try {
			if (errorMsg == null) {
				success();
			} else {
				if(errorMsg.equals("invalidtoken")){
					((A00_00Activity) A00_00Activity.Companion.getMe()).setLogout(true);
					((A00_00Activity) A00_00Activity.Companion.getMe()).alert(context.getString(R.string.invalidtoken),"","");
				}else{
					fail(errorMsg);
				}

			}

		} catch (Exception e) {
			Env.error("", e);
		}
	}

	abstract public void success() throws Exception;

	abstract public void fail(String errmsg) throws Exception;


	protected String unicodeConvert(String str) {
		StringBuilder sb = new StringBuilder();
		char ch;
		int len = str.length();
		for (int i = 0; i < len; i++) {
			ch = str.charAt(i);
			if (ch == '\\' && str.charAt(i+1) == 'u') {
				sb.append((char) Integer.parseInt(str.substring(i+2, i+6), 16));
				i+=5;
				continue;
			}
			sb.append(ch);
		}
		return sb.toString();
	}



	public void insertDB(String type1,String type2, JSONArray ja, String table) throws Exception {
		insertDB(type1,type2,"id", ja, table);
	}

	public void insertDB(String type1,String type2,String type3ID, JSONArray ja, String table) throws Exception {
		int size = ja.length();
		CMDB cmdb = CMDB.open(context);
		JSONObject jo = null;
		for (int i = 0; i < size; i++) {
			jo = ja.getJSONObject(i);
			cmdb.insertORupdate(type1,type2, jo.getString(type3ID), jo.toString(), table);
		}
	}

	public void insertDB(String type1, String type2, String type3, String json, String table) throws Exception {
		CMDB cmdb = CMDB.open(context);
		cmdb.insertORupdate(type1, type2, type3, json, table);
	}
}// sendTask
