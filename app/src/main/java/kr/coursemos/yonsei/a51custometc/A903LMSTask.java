package kr.coursemos.yonsei.a51custometc;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import kr.coursemos.yonsei.A01_09Z09Language;
import kr.coursemos.yonsei.R;
import kr.coursemos.yonsei.a99util.CMCommunication;
import kr.coursemos.yonsei.a99util.CMMultiPart;
import kr.coursemos.yonsei.a99util.Env;
import kr.coursemos.yonsei.a99util.StaticClass;
import kr.coursemos.yonsei.a99util.StringHashMap;

public abstract class A903LMSTask extends A903LMSTaskZ01Send {
	
	private boolean isOrigin=false;
	public A903LMSTask(Context context, String serviceName, StringHashMap pShm) {
		super(context, serviceName, pShm);
		
	}

	@Override
	protected String[] doInBackground(Void... params) {
		try {

			JSONObject jo = null;
			serviceNames.add(serviceName);
			String method=null;
			for (; serviceNames.size() > 0;) {
				serviceName = serviceNames.get(0);
				if (StaticClass.API == null) {
					StaticClass.API = sp.getString(StaticClass.KEY_SELECTSCHOOLURL, "http://edu3.moodledev.net/webservice/rest/server.php");
					StaticClass.APIWEBVIEW= sp.getString(StaticClass.KEY_SELECTSCHOOLWEBVIEWURL, "http://edu3.moodledev.net/local/coursemos/webviewapi.php");
					StaticClass.APItoken= sp.getString(StaticClass.KEY_SELECTSCHOOLTOKEN, "04fa29be660b7a538df68020449ee120");
					userAgent = "Android_" + android.os.Build.VERSION.RELEASE + "_" + android.os.Build.MODEL;
				}
				URL = StaticClass.API;
				shm.clear();
				encryption.clear();
				HEADER.clear();
				isOrigin=true;
				shm.put("wstoken",sp.getString(StaticClass.KEY_USERTOKEN, ""));
				shm.put("moodlewsrestformat", StaticClass.APIformat);
				shm.put("wsfunction", serviceName.split("_,,_")[0]);
//				shm.put("certifiedkey", sp.getString(StaticClass.KEY_CERTIFICATION_KEY, ""));



				int language=sp.getInt(StaticClass.KEY_LANGUAGE, A01_09Z09Language.Companion.getID_row1());
				if(language== A01_09Z09Language.Companion.getID_row1()){
					shm.put("lang", "ko");
				}else if(language==A01_09Z09Language.Companion.getID_row2()){
					shm.put("lang", "en");
				}else if(language==A01_09Z09Language.Companion.getID_row3()){
					shm.put("lang", "ja");
				}

				if (serviceName.equals(API_TEST)) {
					SEND_API_TEST();
				}else if (serviceName.equals(API_BIOAUTH)) {
					SEND_API_BIOAUTH();
				}else if (serviceName.equals(API_BIOREG)) {
					SEND_API_BIOREG();
				} else if (serviceName.equals(API_MULTIPART)) {
					SEND_API_MULTIPART();
				} else if (serviceName.equals(API_MULTIPARTFILE)) {
					SEND_API_MULTIPARTFILE();
				} else if (serviceName.equals(API_MULTIPARTVIDEO)) {
					SEND_API_MULTIPARTVIDEO();
				} else if (serviceName.equals(API_VERSION)) {
					SEND_API_VERSION();
				} else if (serviceName.equals(API_SELECTSCHOOL)) {
					SEND_API_SELECTSCHOOL();
				} else if (serviceName.equals(API_SNUSCHOOL)) {
					SEND_API_SNUSCHOOL();
				} else if (serviceName.equals(API_MOKPOSCHOOL)) {
					SEND_API_MOKPOSCHOOL();
				} else if (serviceName.equals(API_SOONCHUNHYANGSCHOOL)) {
					SEND_API_SOONCHUNHYANGSCHOOL();
				} else if (serviceName.equals(API_JBNUSCHOOL)) {
					SEND_API_JBNUSCHOOL();
				} else if (serviceName.equals(API_LOGIN)) {
					SEND_API_LOGIN();
				}else if (serviceName.equals(API_GETUSERINFO)) {
					SEND_API_GETUSERINFO();
				} else if (serviceName.equals(API_SSOLOGIN)) {
					SEND_API_SSOLOGIN();
				} else if (serviceName.equals(API_CHANGEUSER)) {
					SEND_API_CHANGEUSER();
				} else if (serviceName.equals(API_INQUIRYLIST)) {
					SEND_API_INQUIRYLIST();
				}else if (serviceName.equals(API_PUSHSET)) {
					SEND_API_PUSHSET();
				} else if (serviceName.equals(API_PUSHLOGOUT)) {
					SEND_API_PUSHLOGOUT();
				} else if (serviceName.equals(API_LOGOUT)) {
					SEND_API_LOGOUT();
				} else if (serviceName.equals(API_MAINBOARD)) {
					SEND_API_MAINBOARD();
				} else if (serviceName.equals(API_HAKSA)) {
					SEND_API_HAKSA();
				} else if (serviceName.equals(API_CHATCOUNT)) {
					SEND_API_CHATCOUNT();
				} else if (serviceName.equals(API_NOTIFICATIONCOUNT)) {
					SEND_API_NOTIFICATIONCOUNT();
				} else if (serviceName.equals(API_CURRENTSUBJECT)) {
					SEND_API_CURRENTSUBJECT();
				} else if (serviceName.equals(API_PERIODSUBJECT)) {
					SEND_API_PERIODSUBJECT();
				} else if (serviceName.equals(API_OPENSUBJECT)) {
					SEND_API_OPENSUBJECT();
				} else if (serviceName.equals(API_SUBJECTDETAIL)) {
					SEND_API_SUBJECTDETAIL();
				} else if (serviceName.equals(API_SUBJECTDETAILMENUINFO)) {
					SEND_API_SUBJECTDETAILMENUINFO();
				} else if (serviceName.equals(API_SUBJECTWEEKLY)) {
					SEND_API_SUBJECTWEEKLY();
				} else if (serviceName.equals(API_ADOBECONNECT)) {
					SEND_API_ADOBECONNECT();
				} else if (serviceName.equals(API_BOARD)) {
					SEND_API_BOARD();
				} else if (serviceName.equals(API_BOARDAUTHORITY)) {
					SEND_API_BOARDAUTHORITY();
				} else if (serviceName.equals(API_BOARDDETAIL)) {
					SEND_API_BOARDDETAIL();
				} else if (serviceName.equals(API_COMMENTSELECT)) {
					SEND_API_COMMENTSELECT();
				} else if (serviceName.equals(API_BOARDDELETE)) {
					SEND_API_BOARDDELETE();
				} else if (serviceName.equals(API_COMMENTWRITE)) {
					SEND_API_COMMENTWRITE();
				} else if (serviceName.equals(API_BOARDWRITE)) {
					SEND_API_BOARDWRITE();
				} else if (serviceName.equals(API_STUDENTLIST)) {
					SEND_API_STUDENTLIST();
				} else if (serviceName.equals(API_PROFESSORLIST)) {
					SEND_API_PROFESSORLIST();
				}else if (serviceName.equals(API_LASTCHATTINGLIST)) {
					SEND_API_LASTCHATTINGLIST();
				} else if (serviceName.equals(API_CHATTINGSELECT)) {
					SEND_API_CHATTINGSELECT();
				}else if (serviceName.equals(API_CHATTINGSELECT_GROUP)) {
					SEND_API_CHATTINGSELECT_GROUP();
				} else if (serviceName.equals(API_CHATTINGSEND)) {
					SEND_API_CHATTINGSEND();
				}else if (serviceName.equals(API_NOTIFICATION)) {
					SEND_API_NOTIFICATION();
				}else if (serviceName.equals(API_NOTIFICATIONALLUPDATE)) {
					SEND_API_NOTIFICATIONALLUPDATE();
				} else if (serviceName.equals(API_ALLSUBJECT)) {
					SEND_API_ALLSUBJECT();
				} else if (serviceName.equals(API_SCHEDULESELECT)) {
					SEND_API_SCHEDULESELECT();
				} else if (serviceName.equals(API_INQUIRYSEND)) {
					SEND_API_INQUIRYSEND();
				} else if (serviceName.equals(API_INQUIRYSENDFILE)) {
					SEND_API_INQUIRYSENDFILE();
				} else if (serviceName.equals(API_MYATTENDANCE)) {
					SEND_API_MYATTENDANCE();
				} else if (serviceName.equals(API_MYAUTOATTENDANCE)) {
					SEND_API_MYAUTOATTENDANCE();
				}else  if (serviceName.equals(API_ATTENDANCESETTING)) {
					SEND_API_ATTENDANCESETTING();
				} else if (serviceName.equals(API_STUDENTATTENDANCE)) {
					SEND_API_STUDENTATTENDANCE();
				} else if (serviceName.equals(API_REPORTSELECT)) {
					SEND_API_REPORTSELECT();
				} else if (serviceName.equals(API_VIDEOCONTROL)) {
					SEND_API_VIDEOCONTROL();
				}else if (serviceName.equals(API_ATTENDANCEAUTOCHECK)) {
					SEND_API_ATTENDANCEAUTOCHECK();
				}else if (serviceName.equals(API_ATTENDANCEAUTOCHECKSTART)) {
					SEND_API_ATTENDANCEAUTOCHECKSTART();
				}else if (serviceName.equals(API_ATTENDANCEAUTOCHECKEND)) {
					SEND_API_ATTENDANCEAUTOCHECKEND();
				}else if (serviceName.equals(API_ATTENDANCEAUTOCHECKSTUDENT)) {
					SEND_API_ATTENDANCEAUTOCHECKSTUDENT();
				}else if (serviceName.equals(API_ATTENDANCEAUTOCHECKSTUDENTREQ)) {
					SEND_API_ATTENDANCEAUTOCHECKSTUDENTREQ();
				}else if (serviceName.equals(API_BIOPASS)) {
					SEND_API_BIOPASS();
				}else if (serviceName.equals(API_BIOPASSDELETE)) {
					SEND_API_BIOPASSDELETE();
				}else if (serviceName.equals(API_BIOPASSPUSH)) {
					SEND_API_BIOPASSPUSH();
				}else if (serviceName.equals(API_STONEPASS1)) {
					SEND_API_STONEPASSREG();
				}else if (serviceName.equals(API_STONEPASS2)) {
					SEND_API_STONEPASSAUTH();
				}else if (serviceName.equals(API_STONEPASS3)) {
					SEND_API_STONEPASS3();
				}else if (serviceName.equals(API_MOBILECODESEND)) {
					SEND_API_MOBILECODESEND();
				}else if (serviceName.equals(API_MULTIPARTFACEREG)) {
					SEND_API_MULTIPARTFACEREG();
				}else if (serviceName.equals(API_MULTIPARTFACECOMP)) {
					SEND_API_MULTIPARTFACECOMP();
				}else if (serviceName.equals(API_MULTIPARTFACECOMP2)) {
					SEND_API_MULTIPARTFACECOMP2();
				}


				if (URL == null) {

				} else {
					Encryption(isOrigin);
					if (StaticClass.isDebug) {
						Env.debug(context, priTime + " - send : " + serviceName + "\n" + URL + "\n" + shm.toString() + "\n" + encryption.toString());
					}
//					HEADER.put("user-agent", userAgent);
					
//					if (serviceName.equals(API_SFPLAYER)) {
//						HEADER.put("user-agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Tablet PC 2.0; .NET4.0C; .NET4.0E)");
//					}


					if(shm.containsKey("fullstring")){
						method=shm.getString("method");
						if(  method==null){
							CMcommu = new CMCommunication(URL, StaticClass.charset, shm.getString("fullstring"), "POST", HEADER);
						}else{
							CMcommu = new CMCommunication(URL, StaticClass.charset, shm.getString("fullstring"), method, HEADER);
						}
						CMcommu.getHTML();
						result=CMcommu.xml;

					}else{
						if(pShm==null||pShm.getString("filePath")==null){
							method=shm.getString("method");
							if(  method==null){
								CMcommu = new CMCommunication(URL, StaticClass.charset, encryption, "POST", HEADER);
							}else{
								CMcommu = new CMCommunication(URL, StaticClass.charset, encryption, method, HEADER);
							}
							CMcommu.getHTML();
							result=CMcommu.xml;
						}else{
//						CMmultiPart=new CMMultiPart("http://192.168.3.24:7444/asdfafd", StaticClass.charset, encryption, "POST", HEADER,pShm.getString("filePath") , "userpicture");
//						CMmultiPart.getHTML();
							if(pShm.containsKey("fileKey")){
								CMmultiPart=new CMMultiPart(URL, StaticClass.charset, encryption, "POST", HEADER,pShm.getString("filePath") ,pShm.getString("fileKey"));
							}else{
								CMmultiPart=new CMMultiPart(URL, StaticClass.charset, encryption, "POST", HEADER,pShm.getString("filePath") , "userpicture");
							}

							CMmultiPart.getHTML();
							result=CMmultiPart.xml;
						}
					}

					if (StaticClass.isDebug) {
						if(result==null){
							Env.debug(context, priTime + " - recv : " + serviceName + "\n" + result );
						}else{
							Env.debug(context, priTime + " - recv : " + serviceName + "\n" + unicodeConvert(result) );
						}

					}

					if (result == null) {

						ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
						NetworkInfo net = mgr.getActiveNetworkInfo();
						if (net instanceof NetworkInfo) {
							if (net.getType() == ConnectivityManager.TYPE_MOBILE) {
								// toast("3G로 연결됩니다.", 1500);
							} else if (net.getType() == ConnectivityManager.TYPE_WIFI) {
								// toast("WIFI로 연결됩니다.", 1500);
							}
							if(pShm==null||pShm.getString("filePath")==null){
								errorMsg = CMcommu.error;
								if(errorMsg==null){
									errorMsg="{}";
								}
								if (serviceName.equals(API_PUSHSET)) {
									jo = new JSONObject(errorMsg);
									errorMsg=null;
									RECV_API_PUSHSET(jo);
								} else if (serviceName.equals(API_LOGOUT)) {

									jo = new JSONObject(errorMsg);
									errorMsg=null;
									RECV_API_LOGOUT(jo);
								}
							}else{
								errorMsg = CMmultiPart.error;
							}
							
						} else {
							errorMsg = context.getString(R.string.a99InternetError);
						}
					} else {
						boolean isGo = true;
						try {
							jo = new JSONObject(result);
						} catch (JSONException jsonE) {
							isGo = false;
							if(serviceName.equals(API_PUSHSET)||serviceName.equals(API_LOGOUT)){
							}else{
								errorMsg = "json error\n" + CMcommu.xml;
							}
						}
						if (isGo) {
							if(shm.containsKey("fullstring")){

							}else if (serviceName.equals(API_BIOPASS)) {
								RECV_API_BIOPASS(jo);
							}else if (serviceName.equals(API_BIOPASSDELETE)) {
								RECV_API_BIOPASSDELETE(jo);
							}else if (serviceName.equals(API_BIOPASSPUSH)) {
								RECV_API_BIOPASSPUSH(jo);
							} else if (serviceName.equals(API_VIDEOCONTROL)) {
								RECV_API_VIDEOCONTROL(jo);
							}else if (serviceName.equals(API_MOBILECODESEND)) {
								RECV_API_MOBILECODESEND(jo);
							}else if (jo.isNull("errorcode")) {
								if (serviceName.equals(API_LOGIN)) {
									RECV_API_LOGIN(jo);
								} else if (serviceName.equals(API_PUSHSET)) {
									RECV_API_PUSHSET(jo);
								} else if (serviceName.equals(API_PUSHLOGOUT)) {
									RECV_API_PUSHLOGOUT(jo);
								} else if (serviceName.equals(API_LOGOUT)) {
									RECV_API_LOGOUT(jo);
								} else if (serviceName.equals(API_BIOAUTH)) {
									RECV_API_BIOAUTH(jo);
								}else if (serviceName.equals(API_BIOREG)) {
									RECV_API_BIOREG(jo);
								}else if (serviceName.equals(API_MULTIPARTFACEREG)||serviceName.equals(API_MULTIPARTFACECOMP)||serviceName.equals(API_MULTIPARTFACECOMP2)) {
								}else if (jo.getString("result").equals("0000")||jo.getString("result").equals("success")) {
									if (serviceName.equals(API_TEST)) {
										RECV_API_TEST(jo);
									}else if (serviceName.equals(API_GETUSERINFO)) {
										RECV_API_GETUSERINFO(jo);
									}   else if (serviceName.equals(API_CHANGEUSER)) {
										RECV_API_CHANGEUSER(jo);
									}else if (serviceName.equals(API_VERSION)) {
										RECV_API_VERSION(jo);
									} else if (serviceName.equals(API_SELECTSCHOOL)) {
										RECV_API_SELECTSCHOOL(jo);
									} else if (serviceName.equals(API_SNUSCHOOL)) {
										RECV_API_SNUSCHOOL(jo);
									} else if (serviceName.equals(API_MOKPOSCHOOL)) {
										RECV_API_MOKPOSCHOOL(jo);
									} else if (serviceName.equals(API_SOONCHUNHYANGSCHOOL)) {
										RECV_API_SOONCHUNHYANGSCHOOL(jo);
									} else if (serviceName.equals(API_JBNUSCHOOL)) {
										RECV_API_JBNUSCHOOL(jo);
									} else if (serviceName.equals(API_MULTIPART)) {
										RECV_API_MULTIPART(jo);
									}else if (serviceName.equals(API_MULTIPARTFILE)) {
										RECV_API_MULTIPARTFILE(jo);
									} else if (serviceName.equals(API_MULTIPARTVIDEO)) {
										RECV_API_MULTIPARTVIDEO(jo);
									} else if (serviceName.equals(API_INQUIRYLIST)) {
										RECV_API_INQUIRYLIST(jo);
									} else if (serviceName.equals(API_MAINBOARD)) {
										RECV_API_MAINBOARD(jo);
									} else if (serviceName.equals(API_HAKSA)) {
										RECV_API_HAKSA(jo);
									}else if (serviceName.equals(API_NOTIFICATIONCOUNT)) {
										RECV_API_NOTIFICATIONCOUNT(jo);
									} else if (serviceName.equals(API_CHATCOUNT)) {
										RECV_API_CHATCOUNT(jo);
									} else if (serviceName.equals(API_CURRENTSUBJECT)) {
										RECV_API_CURRENTSUBJECT(jo);
									} else if (serviceName.equals(API_PERIODSUBJECT)) {
										RECV_API_PERIODSUBJECT(jo);
									} else if (serviceName.equals(API_OPENSUBJECT)) {
										RECV_API_OPENSUBJECT(jo);
									} else if (serviceName.equals(API_SUBJECTDETAIL)) {
										RECV_API_SUBJECTDETAIL(jo);
									} else if (serviceName.equals(API_SUBJECTDETAILMENUINFO)) {
										RECV_API_SUBJECTDETAILMENUINFO(jo);
									} else if (serviceName.equals(API_SUBJECTWEEKLY)) {
										RECV_API_SUBJECTWEEKLY(jo);
									} else if (serviceName.equals(API_ADOBECONNECT)) {
										RECV_API_ADOBECONNECT(jo);
									} else if (serviceName.equals(API_BOARD)) {
										RECV_API_BOARD(jo);
									} else if (serviceName.equals(API_BOARDAUTHORITY)) {
										RECV_API_BOARDAUTHORITY(jo);
									} else if (serviceName.equals(API_BOARDDETAIL)) {
										RECV_API_BOARDDETAIL(jo);
									} else if (serviceName.equals(API_COMMENTSELECT)) {
										RECV_API_COMMENTSELECT(jo);
									} else if (serviceName.equals(API_BOARDDELETE)) {
										RECV_API_BOARDDELETE(jo);
									}else if (serviceName.equals(API_COMMENTWRITE)) {
										RECV_API_COMMENTWRITE(jo);
									} else if (serviceName.equals(API_BOARDWRITE)) {
										RECV_API_BOARDWRITE(jo);
									} else if (serviceName.equals(API_STUDENTLIST)) {
										RECV_API_STUDENTLIST(jo);
									} else if (serviceName.equals(API_PROFESSORLIST)) {
										RECV_API_PROFESSORLIST(jo);
									} else if (serviceName.equals(API_LASTCHATTINGLIST)) {
										RECV_API_LASTCHATTINGLIST(jo);
									} else if (serviceName.equals(API_CHATTINGSELECT)) {
										RECV_API_CHATTINGSELECT(jo);
									} else if (serviceName.equals(API_CHATTINGSELECT_GROUP)) {
										RECV_API_CHATTINGSELECT_GROUP(jo);
									} else if (serviceName.equals(API_CHATTINGSEND)) {
										RECV_API_CHATTINGSEND(jo);
									}else if (serviceName.equals(API_NOTIFICATION)) {
										RECV_API_NOTIFICATION(jo);
									}else if (serviceName.equals(API_NOTIFICATIONALLUPDATE)) {
										RECV_API_NOTIFICATIONALLUPDATE(jo);
									} else if (serviceName.equals(API_ALLSUBJECT)) {
										RECV_API_ALLSUBJECT(jo);
									} else if (serviceName.equals(API_SCHEDULESELECT)) {
										RECV_API_SCHEDULESELECT(jo);
									} else if (serviceName.equals(API_INQUIRYSEND)) {
										RECV_API_INQUIRYSEND(jo);
									} else if (serviceName.equals(API_INQUIRYSENDFILE)) {
										RECV_API_INQUIRYSENDFILE(jo);
									} else if (serviceName.equals(API_MYATTENDANCE)) {
										RECV_API_MYATTENDANCE(jo);
									} else if (serviceName.equals(API_MYAUTOATTENDANCE)) {
										RECV_API_MYAUTOATTENDANCE(jo);
									} else if (serviceName.equals(API_ATTENDANCESETTING)) {
										RECV_API_ATTENDANCESETTING(jo);
									} else if (serviceName.equals(API_STUDENTATTENDANCE)) {
										RECV_API_STUDENTATTENDANCE(jo);
									} else if (serviceName.equals(API_REPORTSELECT)) {
										RECV_API_REPORTSELECT(jo);
									}else if (serviceName.equals(API_ATTENDANCEAUTOCHECK)) {
										RECV_API_ATTENDANCEAUTOCHECK(jo);
									}else if (serviceName.equals(API_ATTENDANCEAUTOCHECKSTART)) {
										RECV_API_ATTENDANCEAUTOCHECKSTART(jo);
									}else if (serviceName.equals(API_ATTENDANCEAUTOCHECKEND)) {
										RECV_API_ATTENDANCEAUTOCHECKEND(jo);
									}else if (serviceName.equals(API_ATTENDANCEAUTOCHECKSTUDENT)) {
										RECV_API_ATTENDANCEAUTOCHECKSTUDENT(jo);
									}else if (serviceName.equals(API_ATTENDANCEAUTOCHECKSTUDENTREQ)) {
										RECV_API_ATTENDANCEAUTOCHECKSTUDENTREQ(jo);
									}

								} else {
									JSONObject temp=jo.getJSONObject("result");
									errorMsg = "resultMessage : " + temp.getString("resultMessage") + "\nrequestId : " + jo.getString("requestId");
									temp=null;
								}
							}else{
								String errorcode=jo.getString("errorcode");
								if(StaticClass.isDebug){
									Env.debug(context, "\nexception : " + jo.getString("exception") + "\nerrorcode : " + jo.getString("errorcode") + "\nmessage : " + jo.getString("message"));
								}
//								errorMsg = "exception : " + jo.getString("exception") + "\nerrorcode : " + jo.getString("errorcode") + "\nmessage : " + jo.getString("message");
								if(errorcode.equals("notalivekaistssoserver")){
									errorMsg=context.getString(R.string.a00_00activityServerFail);
								}else if(errorcode.equals("usernamenotfound")){
									errorMsg=context.getString(R.string.a903LMSTaskCheckID);
								}else if(errorcode.equals("forcepasswordchangenotice")){
									errorMsg="PC에서 비밀번호를 변경한 후 시도해 주시기 바랍니다.";
								}else if(errorcode.equals("activityiscurrentlyhidden")){
									errorMsg=jo.getString("message");
								}else if(errorcode.equals("not_found_ssouser_loginid")){
									errorMsg=jo.getString("message");
								}else if(errorcode.equals("invalidtoken")){
									errorMsg="invalidtoken";
//									serviceNames.add(API_LOGOUT);
								}else{
									if(jo.isNull("message")){
										errorMsg=context.getString(R.string.a00_00activityServerFail);
									}else{
										errorMsg=jo.getString("message");
									}

								}
							}

						}
					}

				}
				if (errorMsg == null) {
					serviceNames.remove(0);
				} else {
					if (StaticClass.isDebug) {
						Env.debug(context, priTime + " - recv(error) : " + serviceName + "\n" + errorMsg);
					}
					break;
				}
			}

		} catch (Exception e) {
			errorMsg = e.toString();
			Env.error("", e);
		}
		if (errorMsg == null) {
		}else{
			if(errorMsg.equals("{}")){
				errorMsg=context.getString(R.string.a00_00activityServerFail);
			}
		}
		return null;
	}

	private void Encryption(boolean isOrigin) throws Exception {
		
		Object[] objs = shm.keySet().toArray();
		if(isOrigin){
			for (int i = 0; i < objs.length; i++) {
				encryption.put((String) objs[i], shm.getString((String) objs[i]));
			}
		}else{
			String key=sp.getString("COURSEMOSNH","1234567890ubion&nh20131234567890");
			for (int i = 0; i < objs.length; i++) {

				encryption.put((String) objs[i], Env.EncodeAES(shm.getString((String) objs[i]),key, StaticClass.charset));
			}
		}
		
	}
}// sendTask
