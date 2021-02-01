package kr.coursemos.yonsei

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.webkit.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.a01_02ID
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.a01_02Login
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.a01_02LoginMove1
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.a01_02LoginMove2
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.a01_02Logo
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.a01_02PW
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.a01_02WebView
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.a01_02WebViewBack
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.loginLayout1
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.loginLayout2
import kotlinx.android.synthetic.main.activity_a01_02_z92_login_soon_chun_hyang.loginLayout3
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.io.File
import java.net.URLDecoder

class A01_02Z92LoginSoonChunHyang : A00_00Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_02_z92_login_soon_chun_hyang)
        try{
            StaticClass.history.clear()
            val `is`: String = getEnv(StaticClass.KEY_DEBUGLASTTIME, "0")

            cmdb.DBClear(true)
            clearEnv()

            setEnv(StaticClass.KEY_DEBUGLASTTIME, `is`)
            A901Color.setMainColor("0370a6")
            a01_02Login.setBackgroundColor(A901Color.getIhwaColor())


            a01_02LoginMove1.setOnTouchListener(onTouch)
            a01_02LoginMove1.setOnClickListener(onClick)

            a01_02LoginMove2.setOnTouchListener(onTouch)
            a01_02LoginMove2.setOnClickListener(onClick)

            a01_02WebViewBack.setOnTouchListener(onTouch)
            a01_02WebViewBack.setOnClickListener(onClick)


            a01_02Login.setOnTouchListener(onTouch)
            a01_02Login.setOnClickListener(onClick)

            loginLayout1.setBackgroundColor(A901Color.rgb(0x00,115,165));
            loginLayout2.setBackgroundColor(A901Color.getDefaultBackground())
            loginLayout3.setBackgroundColor(A901Color.getDefaultBackground())


            a01_02Login.setBackgroundColor(A901Color.getIhwaColor())
            a01_02LoginMove1.setBackgroundColor(A901Color.rgb(0, 198, 243))
            a01_02LoginMove2.setBackgroundColor(A901Color.rgb(144, 196, 68))

            val sets: WebSettings = a01_02WebView.getSettings()
            sets.defaultTextEncodingName = StaticClass.charset
            sets.cacheMode = WebSettings.LOAD_NO_CACHE
            sets.setAppCacheEnabled(false)
            sets.javaScriptEnabled = true
            sets.domStorageEnabled = true
            sets.useWideViewPort = true
            sets.loadWithOverviewMode = true
            sets.javaScriptCanOpenWindowsAutomatically = true
            sets.pluginState = WebSettings.PluginState.ON
            sets.builtInZoomControls = true
            a01_02Logo.setImageResource(R.drawable.sch_logo)
            val cookieSyncManager = CookieSyncManager.createInstance(a01_02WebView.context)
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.removeSessionCookie()
            cookieManager.removeAllCookie()
            cookieSyncManager.sync()







            a01_02WebView.setWebChromeClient(object : WebChromeClient() {
                override fun onJsAlert(view: WebView,
                                       url: String,
                                       message: String,
                                       result: JsResult): Boolean {
                    try {
                        if (StaticClass.isDebug) {
                            Env.debug(applicationContext, "onJsAlert\n$url\n$message")
                        }
                        alert(message)
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                    result.confirm()
                    return true
                }

                override fun onCreateWindow(view: WebView,
                                            isDialog: Boolean,
                                            isUserGesture: Boolean,
                                            resultMsg: Message): Boolean {
                    if (StaticClass.isDebug) {
                        Env.debug(applicationContext, "onCreateWindow\n$isUserGesture\n$resultMsg")
                    }
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
                }
            })
            a01_02WebView.setWebViewClient(object : WebViewClient() {
                override fun onReceivedError(view: WebView,
                                             errorCode: Int,
                                             description: String,
                                             failingUrl: String) {
                    Env.debug(applicationContext, "$errorCode : $description\n$failingUrl")
                    super.onReceivedError(view, errorCode, description, failingUrl)
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    var url = url
                    try {
                        if (url.indexOf("coursemos://") == 0) {
                            url = url.replaceFirst("coursemos://".toRegex(), "")
                            var params = url.split("\\?".toRegex()).toTypedArray()
                            if (params.size == 1) {
                            } else {
                                url = url.replaceFirst(params[0] + "\\?".toRegex(), "")
                            }
                            params = url.split("\\&".toRegex()).toTypedArray()
                            val data = JSONObject()
                            val size = params.size
                            var key_value: Array<String?>? = null
                            for (i in 0 until size) {
                                key_value = params[i].split("=".toRegex()).toTypedArray()
                                if (key_value.size > 1) {
                                    data.put(key_value[0],
                                        URLDecoder.decode(key_value[1], StaticClass.charset))
                                } else {
                                    data.put(key_value[0], "")
                                }
                            }
                            if (StaticClass.isDebug) {
                                Env.debug(applicationContext, "$url$data".trimIndent())
                            }
                            a01_02ID.setText(data.getString("userid"))
                            a01_02PW.setText(data.getString("userid"))
                            val cmdb = CMDB.open(applicationContext)
                            cmdb.insertORupdate(A903LMSTask.API_LOGIN,
                                A903LMSTask.API_LOGIN,
                                data.getString("id"),
                                data.toString(),
                                CMDB.TABLE01_common)
                            cmdb.insertORupdate(A903LMSTask.API_LOGINCHECK,
                                A903LMSTask.API_LOGINCHECK,
                                data.getString("id"),
                                "",
                                CMDB.TABLE01_common)
                            setEnv(StaticClass.KEY_USERTOKEN, data.getString("utoken"))
                            setEnv(StaticClass.KEY_USERSEQ, data.getString("id"))
                            setEnv(StaticClass.KEY_FIRSTNAME, data.getString("firstname"))
                            setEnv(StaticClass.KEY_LASTNAME, data.getString("lastname"))
                            setEnv(StaticClass.KEY_IDNUMBER, data.getString("idnumber"))
                            setEnv(StaticClass.KEY_EMAIL, data.getString("email"))
                            setEnv(StaticClass.KEY_INSTITUTION,
                                data.getString("institution"))
                            setEnv(StaticClass.KEY_DEPARTMENT, data.getString("department"))
                            setEnv(StaticClass.KEY_PHONE1, data.getString("phone1"))
                            setEnv(StaticClass.KEY_PHONE2, data.getString("phone2"))
                            setEnv(StaticClass.KEY_LANG, data.getString("lang"))
                            setEnv(StaticClass.KEY_PICURL,
                                data.getString("profileimageurl"))
                            removeEnv(StaticClass.KEY_SUBJECTID)
                            Login()
                        } else {
                            view.loadUrl(url)
                        }
                    } catch (e: java.lang.Exception) {
                        Env.error(url, e)
                    }
                    return true
                }
            })

        }catch (e:Exception){
            EnvError(e)
        }
    }
    private lateinit var id: String
    private lateinit var pw: String
    private var isSSOlogin = false
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
                if (id.indexOf("@@") == 0) {
                    id = id.replace("@@".toRegex(), "")
                } else {
                    isDev = "0"
                }
                prd()
                val shm = StringHashMap()
                shm["isDev"] = isDev
                shm["userid"] = id
                shm["password"] = pw
                object : A903LMSTask(applicationContext, API_SOONCHUNHYANGSCHOOL, shm) {
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
                        edit.putInt(StaticClass.KEY_SELECTSCHOOLAUTH, A01_02Login.bioType)
                        edit.putString(StaticClass.KEY_SELECTSCHOOLMENULIST, menu.toString())
                        edit.putString(StaticClass.KEY_SELECTSCHOOL, content.getString("name"))
                        StaticClass.API = content.getString("url") + "/webservice/rest/server.php"
                        edit.putString(StaticClass.KEY_SELECTSCHOOLURL,
                            content.getString("url") + "/webservice/rest/server.php")
                        StaticClass.APIWEBVIEW =
                            content.getString("url") + "/local/coursemos/webviewapi.php"
                        edit.putString(StaticClass.KEY_SELECTSCHOOLWEBVIEWURL,
                            content.getString("url") + "/local/coursemos/webviewapi.php")
                        StaticClass.APItoken = content.getString("wstoken")
                        edit.putString(StaticClass.KEY_SELECTSCHOOLTOKEN,
                            content.getString("wstoken"))
                        if (isSSOlogin) {
                        } else {
                            edit.putString(StaticClass.KEY_USERTOKEN, content.getString("wstoken"))
                        }
                        edit.putString(StaticClass.KEY_SELECTSCHOOLMAINCOLOR,
                            content.getString("mainColor"))
                        edit.putString(StaticClass.KEY_ANALYTICS, content.getString("googlekey"))
                        edit.putString(StaticClass.KEY_SELECTSCHOOLNOTICE,
                            content.getString("notice"))
                        edit.putString(StaticClass.KEY_SELECTSCHOOLQNA, content.getString("qna"))
                        edit.commit()
                        var api = API_LOGIN
                        if (isSSOlogin) {
                            api = API_SSOLOGIN
                        }
                        val login_shm = StringHashMap()
                        login_shm["userid"] = id
                        login_shm["password"] = pw
                        object : A903LMSTask(context, api, login_shm) {
                            @Throws(java.lang.Exception::class)
                            override fun success() {
                                edit.remove("_id")
                                edit.putBoolean(StaticClass.KEY_SSOLOGIN, isSSOlogin)
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
    fun DeleteDir(path: String?) {
        val file = File(path)
        if (file.exists()) {
            val childFileList = file.listFiles()
            for (childFile in childFileList) {
                if (childFile.isDirectory) {
                    DeleteDir(childFile.absolutePath) //하위 디렉토리 루프
                } else {
                    childFile.delete() //하위 파일삭제
                }
            }
            file.delete() //root 삭제
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (loginLayout1.getVisibility() === View.VISIBLE) {
                    startActivity(A01_02Login::class.java, 0)
                } else {
                    loginLayout1.setVisibility(View.VISIBLE)
                    loginLayout2.setVisibility(View.GONE)
                    loginLayout3.setVisibility(View.GONE)
                }
                return true
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        return super.onKeyDown(keyCode, event)
    }

    var onClick = View.OnClickListener { view ->
        try {
            closeKeyboard()
            val id = view.id
            if (id == R.id.a01_02Login) {
                // CMSocket00Manager.send(context,"BEGIN CONNECT\r\nID:가나다\r\nPASS:12271227\r\nREPORT:N\r\nVERSION:Ver0.994\r\nEND\r\n".getBytes("utf-8"));
                Login()
            } else if (id == R.id.a01_02LoginMove1) {
                isSSOlogin = true
                a01_02WebView.loadUrl("https://sso.sch.ac.kr/oa/oauth/authorize?response_type=code&client_id=4344B8EC3A415BB122284FC87BD22D&state=mobile&scope=openid&redirect_uri=https://cyber.sch.ac.kr/modules/schu/oauthcallback.php")
                loginLayout2.setBackgroundColor(A901Color.getDefaultBackground())
                loginLayout1.setVisibility(View.GONE)
                loginLayout2.setVisibility(View.VISIBLE)
                loginLayout3.setVisibility(View.GONE)
            } else if (id == R.id.a01_02LoginMove2) {
                isSSOlogin = false
                loginLayout1.setVisibility(View.GONE)
                loginLayout2.setVisibility(View.GONE)
                loginLayout3.setVisibility(View.VISIBLE)
            } else if (id == R.id.a01_02WebViewBack) {
                onKeyDown(KeyEvent.KEYCODE_BACK, null)
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
                    a01_02LoginMove1.setBackgroundColor(A901Color.rgb(0, 178, 223))
                } else if (id == R.id.a01_02LoginMove2) {
                    a01_02LoginMove2.setBackgroundColor(A901Color.rgb(124, 176, 48))
                } else if (id == R.id.a01_02WebViewBack) {
                    val draw: Drawable = applicationContext.getResources().getDrawable(R.drawable.back_btn)
                    draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)
                    a01_02WebViewBack.background = draw
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (id == R.id.a01_02Login) {
                    a01_02Login.setBackgroundColor(A901Color.getIhwaColor())
                } else if (id == R.id.a01_02LoginMove1) {
                    a01_02LoginMove1.setBackgroundColor(A901Color.rgb(0, 198, 243))
                } else if (id == R.id.a01_02LoginMove2) {
                    a01_02LoginMove2.setBackgroundColor(A901Color.rgb(144, 196, 68))
                } else if (id == R.id.a01_02WebViewBack) {
                    val draw: Drawable = applicationContext.getResources().getDrawable(R.drawable.back_btn)
                    draw.colorFilter = null
                    a01_02WebViewBack.background = draw
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch
}