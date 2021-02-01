package kr.coursemos.yonsei.a99util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class StaticClass {

	public final static String[] param = { "kimchi","isBackKey","beforeClassName"};



	public static String memoryCheck = null;
	public final static String SFPLAYER_PACKAGE_NAME = "air.co.kr.ubion.NHSFPlayer";
	public final static String SFPLAYER_SCHEME = "typeFourSFPlayer://";
	public static String pathExternalStorage = null;
	public static String version = "0";
	public final static String charset = "utf-8";
	public final static String YES = "YES";
	public final static String NO = "NO";
	public final static String IP = "222.121.122.93";
	public final static int PORT = 7192;
	public static String mydebug = "http://"+IP+":7444/dalbit/debug";
	public final static String DalbitAPI = "https://api2.coursemos.kr/";
	public final static String PushAPI = "https://push2.coursemos.kr/device";

	public static ArrayList<Class<?>> history = null;
	public static Boolean isReal = true;
	public static Boolean isDebug = true;
	public static CMDB cmdb=null;

	public static String API = null;
	public static String APIWEBVIEW = null;
	public static String APItoken = null;
	public final static String APIformat = "json";
	public static BitmapFactory.Options getBitmapOption(int displayWidth, int displayHeight, String imgFilePath) throws Exception, OutOfMemoryError {

		// 읽어들일 이미지의 사이즈를 구한다.
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgFilePath, options);

		// 화면 사이즈에 가장 근접하는 이미지의 리스케일 사이즈를 구한다.
		// 리스케일의 사이즈는 짝수로 지정한다. (이미지 손실을 최소화하기 위함.)

		float widthScale = options.outWidth / displayWidth;
		float heightScale = options.outHeight / displayHeight;
		float scale = widthScale > heightScale ? widthScale : heightScale;
		if (scale >= 16) {
			options.inSampleSize = 16;
		} else if (scale >= 8) {
			options.inSampleSize = 8;
		} else if (scale >= 6) {
			options.inSampleSize = 6;
		} else if (scale >= 4) {
			options.inSampleSize = 4;
		} else if (scale >= 2) {
			options.inSampleSize = 2;
		} else {
			options.inSampleSize = 1;
		}
		options.inJustDecodeBounds = false;
		return options;
	}

	public final static String KEY_SharedPreferences = "dalbitSocial";
	public final static String KEY_ACTIVITYALLKILL = "KEY_ACTIVITYALLKILL";
	public final static String KEY_STATUSBARHEIGHT = "KEY_STATUSBARHEIGHT";
	public final static String KEY_DEVICESEQ = "KEY_DEVICESEQ";

	// 스키마 인덱스
	public final static String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
	// 스키마 인덱스
	public final static String KEY_SCHEMEINDEX = "KEY_SCHEMEINDEX";
	// 스키마 파라메터
	public final static String KEY_ISSNU = "KEY_ISSNU";

	// 디버그URL
	public final static String KEY_DEBUGURL = "KEY_DEBUGURL";
	//디버그 모드
	public final static String KEY_DEBUGLASTTIME = "KEY_DEBUGLASTTIME";
	// analytics
	public final static String KEY_ANALYTICS = "KEY_ANALYTICS";

	//멀티로그인
	public final static String KEY_ACCOUNT = "KEY_ACCOUNT";

	// 메인탭 위치
	public final static String KEY_SELECTMAINTAB = "KEY_SELECTMAINTAB";
	/**
	 * A01_02Login
	 */

	// 리뷰 요청 카운트
	public final static String KEY_REVIEWALERTCOUNT = "KEY_REVIEWALERTCOUNT";

	// 리뷰 확인
	public final static String KEY_REVIEWCHECK = "KEY_REVIEWCHECK";

	// 리뷰 요청 카운트
	public final static String KEY_REVIEWCOUNTDELETE = "KEY_REVIEWCOUNTDELETE";

	// 학교별 메뉴리스트
	public final static String KEY_SELECTSCHOOLMENULIST = "KEY_SELECTSCHOOLMENULIST";

	// 강좌별 메뉴리스트
	public final static String KEY_SELECTCOURSEMENULIST = "KEY_SELECTCOURSEMENULIST";

	// 학교 선택
	public final static String KEY_SELECTSCHOOL = "KEY_SELECTSCHOOL";
	// 선택 학교 URL
	public final static String KEY_SELECTSCHOOLURL = "KEY_SELECTSCHOOLURL";
	// 선택 학교 웹뷰URL
	public final static String KEY_SELECTSCHOOLWEBVIEWURL = "KEY_SELECTSCHOOLWEBVIEWURL";
	// 선택 학교 토큰값
	public final static String KEY_SELECTSCHOOLTOKEN = "KEY_SELECTSCHOOLTOKEN";

	// 전체 공지사항
	public final static String KEY_SELECTSCHOOLNOTICE = "KEY_SELECTSCHOOLNOTICE";
	// 전체 QnA
	public final static String KEY_SELECTSCHOOLQNA = "KEY_SELECTSCHOOLQNA";
	// 전체 이용자 설문조사
	public final static String KEY_SELECTSCHOOLSURVEY = "KEY_SELECTSCHOOLSURVEY";



	//얼굴인식 등록
	public final static String KEY_FACEREG = "KEY_FACEREG";
	// 학교별 생체인증
	public final static String KEY_SELECTSCHOOLAUTH = "KEY_SELECTSCHOOLAUTH";
	// 학교별 메인색상
	public final static String KEY_SELECTSCHOOLMAINCOLOR = "KEY_SELECTSCHOOLMAINCOLOR";

	// 학교별 로고
	public final static String KEY_SELECTSCHOOLLOGO = "KEY_SELECTSCHOOLLOGO";

	// PUSH 인증번호
	public final static String KEY_REGID = "KEY_REGID";
	//sso로그인여부
	public final static String KEY_SSOLOGIN = "KEY_SSOLOGIN";
	// 아이디
	public final static String KEY_ID = "KEY_ID";
	// 비밀번호
	public final static String KEY_PW = "KEY_PW";
	// 아이디 기억
	public final static String KEY_REMEMBERID = "KEY_REMEMBERID";
	// 개인토큰값
	public final static String KEY_USERTOKEN = "KEY_USERTOKEN";
	// 사용자 고유키값
	public final static String KEY_USERSEQ = "KEY_USERSEQ";
	// 성
	public final static String KEY_FIRSTNAME = "KEY_FIRSTNAME";
	// 이름
	public final static String KEY_LASTNAME = "KEY_LASTNAME";
	// 학번/사번
	public final static String KEY_IDNUMBER = "KEY_IDNUMBER";
	// 이메일
	public final static String KEY_EMAIL = "KEY_EMAIL";
	// 소속기관
	public final static String KEY_INSTITUTION = "KEY_INSTITUTION";
	// 소속과
	public final static String KEY_DEPARTMENT = "KEY_DEPARTMENT";
	// 전화번호
	public final static String KEY_PHONE1 = "KEY_PHONE1";
	// 휴대폰번호
	public final static String KEY_PHONE2 = "KEY_PHONE2";
	// 사용언어
	public final static String KEY_LANG = "KEY_LANG";
	// 이미지
	public final static String KEY_PICURL = "KEY_PICURL";

	// 모든학기
	public final static String KEY_SEMESTERS = "KEY_SEMESTERS";
	// LMS시작 정보
	public final static String KEY_LMSSTART = "KEY_LMSSTART";
	// 현재 학기
	public final static String KEY_CURRENTSEMESTER = "KEY_CURRENTSEMESTER";


	/**
	 * A01_03SubjectList
	 */

	// 강좌 스크롤위치1
	public final static String KEY_SUBJECTCACHEPOSITIONY = "KEY_SUBJECTCACHEPOSITIONY";
	// 강좌 스크롤위치2
	public final static String KEY_SUBJECTCACHEPADDINGY = "KEY_SUBJECTCACHEPADDINGY";

	// 과거강좌 연도
	public final static String KEY_SUBJECTCACHEYEAR = "KEY_SUBJECTCACHEYEAR";
	// 과거강좌 학기
	public final static String KEY_SUBJECTCACHESEMESTER = "KEY_SUBJECTCACHESEMESTER";

	// 강좌이름
	public final static String KEY_SUBJECTNAME = "KEY_SUBJECTNAME";
	// 강좌이름(영문)
	public final static String KEY_SUBJECTNAMEE = "KEY_SUBJECTNAMEE";
	// 권한
	public final static String KEY_MYPOSITION = "KEY_MYPOSITION";
	// 강좌ID
	public final static String KEY_SUBJECTID = "KEY_SUBJECTID";

	/**
	 * A01_04SubjectDetail
	 */

	// 상단탭위치 저장
	public final static String KEY_SUBJECTTAB = "KEY_SUBJECTTAB";

	// 펼치기 위치 저장
	public final static String KEY_SUBJECTPOSITION = "KEY_SUBJECTPOSITION";
	// 주차정보 스크롤위치1
	public final static String KEY_WEEKLYCACHEPOSITIONY = "KEY_WEEKLYCACHEPOSITIONY";
	// 주차정보 스크롤위치2
	public final static String KEY_WEEKLYCACHEPADDINGY = "KEY_WEEKLYCACHEPADDINGY";

	// 메뉴 스크롤위치1
	public final static String KEY_MENUCACHEPOSITIONY = "KEY_MENUCACHEPOSITIONY";
	// 메뉴 스크롤위치2
	public final static String KEY_MENUCACHEPADDINGY = "KEY_MENUCACHEPADDINGY";

	// 파일 위치
	public final static String KEY_FILEPATH = "KEY_FILEPATH";

	/**
	 *A01_06Z01StudyLogWrite
	 */
	// 학습관찰일지 학생 정보
	public final static String KEY_STUDYLOGINFO = "KEY_STUDYLOGINFO";

	/**
	 * A01_09Setting
	 */
	// 스크롤위치
	public final static String KEY_SETTINGSCROLLY = "KEY_SETTINGSCROLLY";

	/**
	 * A01_09Z04AlarmSetting
	 */

	// 소리알림
	public final static String KEY_ALARMONOFF = "KEY_ALARMONOFF";
	// 소리알림
	public final static String KEY_ALARMSOUNDONOFF = "KEY_ALARMSOUNDONOFF";
	// 진동알림
	public final static String KEY_ALARMVIBRATEONOFF = "KEY_ALARMVIBRATEONOFF";
	// 메시지 미리보기
	public final static String KEY_ALARMPREVIEWONOFF = "KEY_ALARMPREVIEWONOFF";

	//바이오
	public final static String KEY_ISBIOFACE = "KEY_ISBIOFACE";
	/**
	 * A01_09Z05LockOnOff
	 */
	// 암호잠금화면
	public final static String KEY_ISLOCKSCREEN = "KEY_ISLOCKSCREEN";
	// 암호잠금설정
	public final static String KEY_LOCKSCREENONOFF = "KEY_LOCKSCREENONOFF";
	// 암호화면 타입
	public final static String KEY_LOCKSCREENTYPE = "KEY_LOCKSCREENTYPE";
	// 인증번호
	public final static String KEY_LOCKSCREENNUMBER = "KEY_LOCKSCREENNUMBER";
	// 인증번호시간
	public final static String KEY_LOCKSCREENTIME = "KEY_LOCKSCREENTIME";
	// 암호확인
	public final static String KEY_LOCKSCREENNUMBERCHECK = "KEY_LOCKSCREENNUMBERCHECK";


	/**
	 * A01_09Z09Language
	 */

	// 선택아이디정보
	public final static String KEY_SELECTUSERSEQ = "KEY_SELECTUSERSEQ";
	public final static String KEY_SELECTUSERID = "KEY_SELECTUSERID";
	// 언어설정
	public final static String KEY_LANGUAGE = "KEY_LANGUAGE";

	/**
	 * A01_10Board
	 */

	// 게시판 ID
	public final static String KEY_BOARDID = "KEY_BOARDID";
	//게시판 스크롤위치1
	public final static String KEY_BOARDPOSITIONY = "KEY_BOARDPOSITIONY";
	//게시판 스크롤위치2
	public final static String KEY_BOARDPADDINGY = "KEY_BOARDPADDINGY";
	//게시판 페이지
	public final static String KEY_BOARDPAGE = "KEY_BOARDPAGE";


	// 게시판 이름
	public final static String KEY_BOARDNAME = "KEY_BOARDNAME";

	// 게시물 세부 ID
	public final static String KEY_BOARDDETAILID = "KEY_BOARDDETAILID";
	// 게시물 생성시간
	public final static String KEY_BOARDCREATETIME = "KEY_BOARDCREATETIME";
	// 게시물 세부 ID
	public final static String KEY_ISBOARDDETAILCOMMENT = "KEY_ISBOARDDETAILCOMMENT";

	/**
	 * A01_10z02BoardWrite
	 */
	// 게시판 ID
	public final static String KEY_BOARDWRITE = "KEY_BOARDWRITE";
	/**
	 * A01_10z03BoardWriteFile
	 */
	// 파일등록 제목
	public final static String KEY_BOARDWRITEISFILE = "KEY_BOARDWRITEISFILE";


	/**
	 * A01_11Chatting
	 */
	// 대화 상대 정보
	public final static String KEY_CHATTINGINFO = "KEY_CHATTINGINFO";
	public final static String KEY_ISNOTI = "KEY_ISNOTI";
	/**
	 * A01_13WebView
	 */
	// webViewURL
	public final static String KEY_WEBURL = "KEY_WEBURL";
	// webViewName
	public final static String KEY_WEBVIEWNAME = "KEY_WEBVIEWNAME";
	/**
	 * A01_15Schedule
	 */
	// 일정 타입
	public final static String KEY_SCHEDULETYPE = "KEY_SCHEDULETYPE";
	// 일정 ID
	public final static String KEY_SCHEDULEID = "KEY_SCHEDULEID";


	/**
	 * A01_16ChattingList
	 */
	// 메시지리스트 스크롤위치1
	public final static String KEY_CHATLISTCACHEPOSITIONY = "KEY_CHATLISTCACHEPOSITIONY";
	// 메시지리스트 스크롤위치2
	public final static String KEY_CHATLISTCACHEPADDINGY = "KEY_CHATLISTCACHEPADDINGY";

	/**
	 * A01_19VideoView
	 */

	// 유투브
	public final static String KEY_YOUTUBE = "KEY_YOUTUBE";
	// 스트리밍 주소
	public final static String KEY_VIDEOURL = "KEY_VIDEOURL";
	//가이드 다시 보지 않기
	public final static String KEY_ISGUIDEINVISIBLE = "KEY_ISGUIDEINVISIBLE";

	// 트랙아이디
	public final static String KEY_VIDEOTRACKID = "KEY_VIDEOTRACKID";

	// 마지막 포지션
	public final static String KEY_VIDEOLASTPOSTION = "KEY_VIDEOLASTPOSTION";
	// 트랙킹유무
	public final static String KEY_VIDEOISPROGRESS = "KEY_VIDEOISPROGRESS";
	// 동영상 재생완료
	public final static String KEY_VIDEOISCOMPLETE = "KEY_VIDEOISCOMPLETE";

	// 동영상 일반재생
	public final static String KEY_VIDEOISNORMAL = "KEY_VIDEOISNORMAL";

	// 다른 화면 이동전 포지션
	public final static String KEY_VIDEOPAUSEPOSITION = "KEY_VIDEOPAUSEPOSITION";
	// 다른화면 이동전 시작 상태
	public final static String KEY_VIDEOPAUSEISSTART = "KEY_VIDEOPAUSEISSTART";

	// 채팅 카운트
	public final static String KEY_CHATCOUNT = "KEY_CHATCOUNT";

	// 알림 카운트
	public final static String KEY_NOTICOUNT = "KEY_NOTICOUNT";
	// 알림 스크롤위치1
	public final static String KEY_NOTICACHEPOSITIONY = "KEY_NOTICACHEPOSITIONY";
	// 알림 스크롤위치2
	public final static String KEY_NOTICACHEPADDINGY = "KEY_NOTICACHEPADDINGY";


	//생체 인식 푸시
	public final static String KEY_BIOPASSPUSHURL = "KEY_BIOPASSPUSHURL";
	//생체 인식 마지막 인증
	public final static String KEY_BIOPASSLASTTIME = "KEY_BIOPASSLASTTIME";

	//변수 초기화시 초기화면
	public final static String KEY_PUSHSCREEN = "KEY_PUSHSCREEN";


	public final static String KEY_PUBLICKEY = "KEY_PUBLICKEY";

	public final static int ID_BOTTOMTAB1 = 1;
	public final static int ID_BOTTOMTAB2 =2;
	public final static int ID_BOTTOMTAB3 =3;
	public final static int ID_BOTTOMTAB4 =4;
	public final static int ID_BOTTOMTAB5 =5;
	static public final String HEADER="HEADER";
	static public final String ROW="ROW";
}
