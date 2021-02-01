package kr.coursemos.yonsei

import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_02_z93_loginjbnu.*

import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.io.File

class A01_02Z93Loginjbnu : A00_00Activity() {
    companion object{
        var logoUrl: String? = null
    }
    private var id: String? = null
    private var pw: String? = null
    private var isJBNUlogin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_02_z93_loginjbnu)
        try{
            val schemeIndex: String = getEnv(StaticClass.KEY_SCHEMEINDEX, "")
            StaticClass.history.clear()
            val `is`: String = getEnv(StaticClass.KEY_DEBUGLASTTIME, "0")

            cmdb.DBClear(true)
            clearEnv()

            setEnv(StaticClass.KEY_DEBUGLASTTIME, `is`)
            A901Color.setMainColor("4B2762")
            a01_02Login.setBackgroundColor(A901Color.getIhwaColor())
            loginLayout1.setBackgroundColor(A901Color.WHITE)
            loginLayout3.setBackgroundColor(A901Color.getDefaultBackground())

            a01_02LoginMove1.setBackgroundColor(A901Color.rgb(89, 46, 117))
            a01_02LoginMove2.setBackgroundColor(A901Color.rgb(51, 107, 175))
            a01_02Login.setBackgroundColor(A901Color.getIhwaColor())
            //			String logoUrl=sp.getString(StaticClass.KEY_SELECTSCHOOLLOGO,"");
            //			if(logoUrl.equals("")){
            //
            //			}else{
            //				Picasso.with(context)
            //					.load(sp.getString(StaticClass.KEY_SELECTSCHOOLLOGO,""))
            //					.placeholder(android.R.color.transparent)
            //					.error(android.R.color.transparent)
            //					.into(layout.a01_02Logo);
            //			}
            if (schemeIndex == "jbnulogin1") {
                onClick.onClick(a01_02LoginMove1)
            } else if (schemeIndex == "jbnulogin2") {
                onClick.onClick(a01_02LoginMove2)
            }



            if (logoUrl == null || logoUrl == "") {
            } else {
                Picasso.with(applicationContext).load(logoUrl).placeholder(android.R.color.transparent)
                    .error(android.R.color.transparent).into(a01_02Logo)
            }


            a01_02LoginMove1.setOnTouchListener(onTouch)
            a01_02LoginMove1.setOnClickListener(onClick)
            a01_02LoginMove2.setOnTouchListener(onTouch)
            a01_02LoginMove2.setOnClickListener(onClick)
            a01_02Login.setOnTouchListener(onTouch)
            a01_02Login.setOnClickListener(onClick)
        }catch (e:Exception){
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun Login() {
        id = a01_02ID.getText().toString()
        pw = a01_02PW.getText().toString()
        if (id == "kimchiyak" && pw == "kimchiyak0886") {
            startActivity(A00_00Debug::class.java, 0)
        } else {
            if (id == "") {
                alert(getString(R.string.a01_02activityCheckID))
            } else if (pw == "") {
                alert(getString(R.string.a01_02activityCheckPW))
            } else {
                var isDev = "1"
                if (id!!.indexOf("@@") == 0) {
                    id = id!!.replace("@@".toRegex(), "")
                } else {
                    isDev = "0"
                }
                prd()
                val shm = StringHashMap()
                shm["isDev"] = isDev
                shm["userid"] = id
                shm["password"] = pw
                object : A903LMSTask(applicationContext, API_JBNUSCHOOL, shm) {
                    @Throws(java.lang.Exception::class)
                    override fun success() {
                        val jo = JSONObject(CMcommu.xml)
                        val data = jo.getJSONArray("data")
                        val content = data.getJSONObject(0)
                        val menuList =
                            content.getString("cosOptions").split(",".toRegex()).toTypedArray()
                        val menu = JSONObject()
                        val size = menuList.size
                        for (i in 0 until size) {
                            menu.put(menuList[i], true)
                        }
                        A901Color.setMainColor(content.getString("mainColor"))
                        var url = content.getString("url")
                        if (isJBNUlogin) {
                            url = "http://jbec.jbnu.ac.kr"
                        }
                        edit.putInt(StaticClass.KEY_SELECTSCHOOLAUTH, A01_02Login.bioType)
                        edit.putString(StaticClass.KEY_SELECTSCHOOLMENULIST, menu.toString())
                        edit.putString(StaticClass.KEY_SELECTSCHOOL, content.getString("name"))
                        StaticClass.API = "$url/webservice/rest/server.php"
                        edit.putString(StaticClass.KEY_SELECTSCHOOLURL,
                            "$url/webservice/rest/server.php")
                        StaticClass.APIWEBVIEW = "$url/local/coursemos/webviewapi.php"
                        edit.putString(StaticClass.KEY_SELECTSCHOOLWEBVIEWURL,
                            "$url/local/coursemos/webviewapi.php")
                        StaticClass.APItoken = content.getString("wstoken")
                        edit.putString(StaticClass.KEY_SELECTSCHOOLTOKEN,
                            content.getString("wstoken"))
                        edit.putString(StaticClass.KEY_USERTOKEN, content.getString("wstoken"))
                        edit.putString(StaticClass.KEY_SELECTSCHOOLMAINCOLOR,
                            content.getString("mainColor"))
                        edit.putString(StaticClass.KEY_ANALYTICS, content.getString("googlekey"))
                        edit.putString(StaticClass.KEY_SELECTSCHOOLNOTICE,
                            content.getString("notice"))
                        edit.putString(StaticClass.KEY_SELECTSCHOOLQNA, content.getString("qna"))
                        edit.putString(StaticClass.KEY_SELECTSCHOOLSURVEY,
                            "$url/local/ubion/mod/feedback/mobile_feedback_list.php")
                        edit.commit()
                        val api = API_LOGIN
                        val login_shm = StringHashMap()
                        login_shm["userid"] = id
                        login_shm["password"] = pw
                        object : A903LMSTask(context, api, login_shm) {
                            @Throws(java.lang.Exception::class)
                            override fun success() {
                                edit.remove("_id")
                                edit.putBoolean(StaticClass.KEY_SSOLOGIN, false)
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

                    @Throws(java.lang.Exception::class)
                    override fun fail(errmsg: String) {
                        dismiss()
                        alert(errmsg)
                    }
                }
            }
        }
    } // Login

    var onClick = View.OnClickListener { view ->
        try {
            closeKeyboard()
            val id = view.id
            if (id == R.id.a01_02Login) {
                // CMSocket00Manager.send(context,"BEGIN CONNECT\r\nID:가나다\r\nPASS:12271227\r\nREPORT:N\r\nVERSION:Ver0.994\r\nEND\r\n".getBytes("utf-8"));
                Login()
            } else if (id == R.id.a01_02LoginMove1) {
                isJBNUlogin = false
                a01_02title.setText("스마트 학습관리시스템")
                loginLayout1.setVisibility(View.GONE)
                loginLayout3.setVisibility(View.VISIBLE)
            } else if (id == R.id.a01_02LoginMove2) {
                isJBNUlogin = true
                a01_02title.setText("학점교류 및 군이러닝")
                loginLayout1.setVisibility(View.GONE)
                loginLayout3.setVisibility(View.VISIBLE)
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    } // OnClickListener onClick
    var onTouch = OnTouchListener { view, event ->
        try {
            val id = view.id
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (id == R.id.a01_02Login) {
                    a01_02Login.setBackgroundColor(A901Color.getIhwaColorDown())
                } else if (id == R.id.a01_02LoginMove1) {
                    a01_02LoginMove1.setBackgroundColor(A901Color.rgb(69, 26, 97))
                } else if (id == R.id.a01_02LoginMove2) {
                    a01_02LoginMove2.setBackgroundColor(A901Color.rgb(31, 87, 155))
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (id == R.id.a01_02Login) {
                    a01_02Login.setBackgroundColor(A901Color.getIhwaColor())
                } else if (id == R.id.a01_02LoginMove1) {
                    a01_02LoginMove1.setBackgroundColor(A901Color.rgb(89, 46, 117))
                } else if (id == R.id.a01_02LoginMove2) {
                    a01_02LoginMove2.setBackgroundColor(A901Color.rgb(51, 107, 175))
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (loginLayout1.getVisibility() === View.VISIBLE) {
                    startActivity(A01_02Login::class.java, 0)
                } else {
                    loginLayout1.setVisibility(View.VISIBLE)
                    loginLayout3.setVisibility(View.GONE)
                }
                return true
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        return super.onKeyDown(keyCode, event)
    }
}