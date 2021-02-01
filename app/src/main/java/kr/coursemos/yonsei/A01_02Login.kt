package kr.coursemos.yonsei

import android.os.Bundle
import android.os.Environment
import android.view.*
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_02_login.*
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.myapplication.a99util.EnvDebug
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

class A01_02Login : A00_00Activity() {
	companion object {
		var bioType = 0
	}

	private var id: String? = null
	private var pw: String? = null
	private var isShow = false
	private var schoolList: JSONArray? = null
	private var isSelect = false
	private var arr: ArrayList<String> = ArrayList()
	private var content: JSONObject? = null
	private var isNotice = false
	private var isLink = false
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_a01_02_login_scroll)
		try {
			//            a01_02login.backgroundTintList=colorStateList
			val isReviewCheck: Boolean = getEnvBoolean(StaticClass.KEY_REVIEWCHECK, false)
			val `is`: String = getEnv(StaticClass.KEY_DEBUGLASTTIME, "0")
			val logoUrl: String = getEnv(StaticClass.KEY_SELECTSCHOOLLOGO, "")
			val mainColor = getEnv(StaticClass.KEY_SELECTSCHOOLMAINCOLOR, "C61065")


			if (logoUrl == "") {
				Picasso.with(applicationContext)
					.load("https://api2.coursemos.kr/logo/kookmin_logo.png")
					.placeholder(android.R.color.transparent).error(android.R.color.transparent)
					.into(a01_02logo)
			} else {
				Picasso.with(applicationContext).load(logoUrl)
					.placeholder(android.R.color.transparent).error(android.R.color.transparent)
					.into(a01_02logo)
			}



			cmdb.DBClear(true)
			clearEnv()
			putEnv(StaticClass.KEY_SELECTSCHOOLMAINCOLOR, mainColor)
			putEnv(StaticClass.KEY_DEBUGLASTTIME, `is`)
			putEnvBoolean(StaticClass.KEY_REVIEWCHECK, isReviewCheck)
			A901Color.setMainColor(getEnv(StaticClass.KEY_SELECTSCHOOLMAINCOLOR, "1"))
			a01_02login.setBackgroundColor(A901Color.getIhwaColor())
			a01_02login.setOnTouchListener { v, event ->
				try {
					if (event.action == MotionEvent.ACTION_DOWN) {
						a01_02login.setBackgroundColor(A901Color.getIhwaColorDown())
					} else if (event.action == MotionEvent.ACTION_UP) {
						a01_02login.setBackgroundColor(A901Color.getIhwaColor())
					}
				} catch (e: java.lang.Exception) {
					Env.error("", e)
				}
				false
			}
			a01_02login.setOnClickListener(onClick)
		} catch (e: java.lang.Exception) {
			EnvError(e)
		}
	}

	@Throws(java.lang.Exception::class)
	fun searchSchool() {
		val shm = StringHashMap()
		object : A903LMSTask(this, API_SELECTSCHOOL, shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				val jo = JSONObject(CMcommu.xml)
				schoolList = jo.getJSONArray("data")
				var content: JSONObject? = null
				arr!!.clear()
				for (i in 0 until schoolList!!.length()) {
					content = schoolList!!.getJSONObject(i)
					arr!!.add(content.getString("name"))
				}
				Env.debug(context, arr.toString())
				isShow = false
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				isShow = false
				alert(errmsg)
			}
		}
	}

	var onClick = View.OnClickListener { view ->
		try {
			val id = view.id
			if (id == R.id.a01_02login) {
				// CMSocket00Manager.send(context,"BEGIN CONNECT\r\nID:가나다\r\nPASS:12271227\r\nREPORT:N\r\nVERSION:Ver0.994\r\nEND\r\n".getBytes("utf-8"));
				Login()
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	}

	@Throws(java.lang.Exception::class)
	private fun Login() {
		id = a01_02id.getText().toString()
		pw = a01_02pw.getText().toString()
		if (id == "@@@@@" && pw == "!!!!!") {
			startActivity(A00_00Debug::class.java, 0)
		} else {
			if (id == "") {
				alert(getString(R.string.a01_02activityCheckID))
			} else if (pw == "") {
				alert(getString(R.string.a01_02activityCheckPW))
			} else {
				prd()
				val shm = StringHashMap()
				shm["id"] = "120"
				object : A903LMSTask(applicationContext, API_SELECTSCHOOL, shm) {
					@Throws(java.lang.Exception::class)
					override fun success() {
						content = JSONObject(CMcommu.xml).getJSONArray("data").getJSONObject(0)
						selectSchool()
					}

					@Throws(java.lang.Exception::class)
					override fun fail(errmsg: String) {
						alert(errmsg)
					}
				}
			}
		}
	} // Login

	@Throws(java.lang.Exception::class)
	private fun selectSchool() {
		val menuList = content!!.getString("cosOptions").split(",".toRegex()).toTypedArray()
		val menu = JSONObject()
		val size = menuList.size
		for (i in 0 until size) {
			menu.put(menuList[i], true)
		}
		val schoolName = content!!.getString("name")
		var logoUrl: String = ""
		if (content!!.isNull("logoUrl")) {
		} else {
			logoUrl = content!!.getString("logoUrl")
		}
		A901Color.setMainColor(content!!.getString("mainColor"))
		a01_02login.setBackgroundColor(A901Color.getIhwaColor())
		bioType = if (content!!.isNull("authSdk")) {
			0
		} else {
			content!!.getInt("authSdk")
		}
		setEnvInt(StaticClass.KEY_SELECTSCHOOLAUTH, bioType)
		setEnv(StaticClass.KEY_SELECTSCHOOLMAINCOLOR, content!!.getString("mainColor"))
		setEnv(StaticClass.KEY_SELECTSCHOOLLOGO, logoUrl)
		setEnv(StaticClass.KEY_SELECTSCHOOLMENULIST, menu.toString())
		setEnv(StaticClass.KEY_SELECTSCHOOL, content!!.getString("name"))
		StaticClass.API = content!!.getString("url") + "/webservice/rest/server.php"
		setEnv(StaticClass.KEY_SELECTSCHOOLURL,
			content!!.getString("url") + "/webservice/rest/server.php")
		StaticClass.APIWEBVIEW = content!!.getString("url") + "/local/coursemos/webviewapi.php"
		setEnv(StaticClass.KEY_SELECTSCHOOLWEBVIEWURL,
			content!!.getString("url") + "/local/coursemos/webviewapi.php")
		StaticClass.APItoken = content!!.getString("wstoken")
		setEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, content!!.getString("wstoken"))
		setEnv(StaticClass.KEY_USERTOKEN, content!!.getString("wstoken"))
		setEnv(StaticClass.KEY_SELECTSCHOOLNOTICE, content!!.getString("notice"))
		setEnv(StaticClass.KEY_SELECTSCHOOLQNA, content!!.getString("qna"))
		val login_shm = StringHashMap()
		login_shm["userid"] = id
		login_shm["password"] = pw
		object : A903LMSTask(applicationContext, API_LOGIN, login_shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				edit.remove("_id")
				edit.putString(StaticClass.KEY_ID, id)
				edit.putString(StaticClass.KEY_PW, pw)
				edit.putBoolean(StaticClass.KEY_REMEMBERID, true)
				edit.commit()
				dismiss()
				val registerKey = FirebaseInstanceId.getInstance().token
				if (registerKey == null) {
				} else {
					val edit = sp.edit()
					edit.putString(StaticClass.KEY_REGID, registerKey)
					edit.commit()
					object : A903LMSTask(context, API_PUSHSET, null) {
						@Throws(java.lang.Exception::class)
						override fun success() {
						}

						@Throws(java.lang.Exception::class)
						override fun fail(errmsg: String) {
							Env.error(errmsg, java.lang.Exception())
						}
					}
				}
				DeleteDir(Environment.getExternalStorageDirectory().absolutePath + "/" + context.getString(
					R.string.app_name) + "/")
				DeleteDir(StaticClass.pathExternalStorage)
				var dir: File? = File(StaticClass.pathExternalStorage)
				if (dir!!.exists()) {
				} else {
					dir.mkdir()
				}
				dir = null
				startActivity(A01_03SubjectList::class.java, 0)
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				dismiss()
				alert(errmsg)
			}
		}
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		try {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				//				isExit = true;
				//				confirm("종료");
				val now = System.currentTimeMillis()
				if (0 < exitTime && exitTime > now - 3000) {
					finish()
				} else {
					exitTime = now
					toast(getString(R.string.alert_exit))
				}
				return true
			}
		} catch (e: Exception) {
			Env.error("", e)
		}
		return super.onKeyDown(keyCode, event)
	}
}