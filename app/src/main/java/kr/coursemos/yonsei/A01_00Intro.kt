package kr.coursemos.yonsei

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.KeyEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.crashlytics.android.Crashlytics
import com.google.firebase.iid.FirebaseInstanceId
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_a01_00_intro.*
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMCommunication
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.myapplication.a99util.EnvDebug
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory

class A01_00Intro : A00_00Activity() {
    protected var URL: String? = null
    protected var loinType: String? = null
    protected var loginMessage: String? = null
    protected var isAutoLogin = false
    protected var isUpdate = false
    protected var isServerCheck = false
    protected var isSNU = false
    protected var schemeIndex: String? = null
    private var content: JSONObject? = null
    private var isNotice = false
    private var isLink = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_00_intro)
        try {
//            setEnv(StaticClass.KEY_DEBUGLASTTIME, Env.getTodateAddDay("yyyyMMddHHmmss", 1));

            Fabric.with(applicationContext, Crashlytics())
            val before = shm_param.getString(StaticClass.param[2])
            when (before) {
                null -> {
                }
                A01_01Scheme::class.java.name -> {
                    isSNU = getEnvBoolean(StaticClass.KEY_ISSNU)
                    schemeIndex = getEnv(StaticClass.KEY_SCHEMEINDEX)
                }
            }
            var anim = AnimationUtils.loadAnimation(me, R.anim.alpha_0to1)
            a01_00introimage.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    try {
                        checkVersion(this@A01_00Intro, A903LMSTask.API_VERSION, null)
                    } catch (e: Exception) {
                        EnvError(e)
                    }
                }
            })
            var manager = applicationContext.packageManager
            var facetID = getFacetID(manager.getPackageInfo(applicationContext.packageName,
                PackageManager.GET_SIGNATURES))
            EnvDebug("facetID - $facetID")
        } catch (e: Exception) {
            Env.error(e)
        }
    }

    fun getFacetID(paramPackageInfo: PackageInfo): String {
        var byteArrayInputStream =
            ByteArrayInputStream(paramPackageInfo.signatures[0].toByteArray())
        var certificate =
            CertificateFactory.getInstance("X509").generateCertificate(byteArrayInputStream)
        var messageDigest = MessageDigest.getInstance("SHA1")
        var facetID =
            "android:apk-key-hash:${Base64.encodeToString(messageDigest.digest(certificate.encoded),
                3)}"
        return facetID
    }

    @Throws(java.lang.Exception::class)
    protected fun updateCheck() {
        if (loinType == "0") {
            continueIntro()
        } else if (loinType == "1") {
            isUpdate = true
            confirm(loginMessage!!)
        } else if (loinType == "2") {
            isUpdate = true
            alert(loginMessage!!)
        } else if (loinType == "3") {
            isServerCheck = true
            alert(loginMessage!!)
        }
    }

    @Throws(java.lang.Exception::class)
    protected fun continueIntro() {
        if (getEnvBoolean(StaticClass.KEY_REMEMBERID, false)) {
            isAutoLogin = true
            autoLogin()
        } else {
            goLogin()
        }
    }

    @Throws(java.lang.Exception::class)
    protected fun goLogin() {
        searchSchool()
    }

    @Throws(java.lang.Exception::class)
    fun searchSchool() {
        val shm = StringHashMap()
        shm["id"] = "120"
        object : A903LMSTask(applicationContext, API_SELECTSCHOOL, shm) {
            @Throws(java.lang.Exception::class)
            override fun success() {
                content = JSONObject(CMcommu.xml).getJSONArray("data").getJSONObject(0)
                setEnv(StaticClass.KEY_SELECTSCHOOLMAINCOLOR, content!!.getString("mainColor"))
                if (content!!.isNull("logoUrl")) {
                } else {
                    putEnv(StaticClass.KEY_SELECTSCHOOLLOGO, content!!.getString("logoUrl"))
                }
                startActivity(A01_02Login::class.java, 0)
            }

            @Throws(java.lang.Exception::class)
            override fun fail(errmsg: String) {
                alert(errmsg)
            }
        }
    }

    protected fun autoLogin() {
        val shm = StringHashMap()
        shm["isDev"] = "0"
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

    @Throws(java.lang.Exception::class)
    fun selectSchool() {
        setEnvBoolean(StaticClass.KEY_ISLOCKSCREEN, true)

        if (content!!.isNull("authSdk")) {
            A01_02Login.bioType = 0
        } else {
            A01_02Login.bioType = content!!.getInt("authSdk")
        }
        val menuList = content!!.getString("cosOptions").split(",".toRegex()).toTypedArray()
        val menu = JSONObject()
        val size = menuList.size
        for (i in 0 until size) {
            menu.put(menuList[i], true)
        }
        var logoUrl: String = ""
        if (content!!.isNull("logoUrl")) {
        } else {
            logoUrl = content!!.getString("logoUrl")
        }

        setEnvInt(StaticClass.KEY_SELECTSCHOOLAUTH, A01_02Login.bioType)
        setEnv(StaticClass.KEY_SELECTSCHOOLMAINCOLOR, content!!.getString("mainColor"))
        setEnv(StaticClass.KEY_ANALYTICS, content!!.getString("googlekey"))
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
        //		edit.putString(StaticClass.KEY_USERTOKEN, content.getString("wstoken"));
        //		edit.putString(StaticClass.KEY_USERTOKEN, content.getString("wstoken"));
        setEnv(StaticClass.KEY_SELECTSCHOOLNOTICE, content!!.getString("notice"))
        setEnv(StaticClass.KEY_SELECTSCHOOLQNA, content!!.getString("qna"))
        val api = A903LMSTask.API_LOGIN
        val shm = StringHashMap()
        shm["userid"] = getEnv(StaticClass.KEY_ID, "")
        shm["password"] = getEnv(StaticClass.KEY_PW, "")
        object : A903LMSTask(applicationContext, api, shm) {
            @Throws(java.lang.Exception::class)
            override fun success() {
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
                val pushScreen = sp.getString(StaticClass.KEY_PUSHSCREEN, "0")
                if (pushScreen == "3") {
                    startActivity(A01_11Chatting::class.java, 0)
                } else if (pushScreen == "4") {
                    startActivity(A01_20Alarm::class.java, 0)
                } else if (pushScreen == "10") {
                    startActivity(A01_24BiopassPUSH::class.java, 0)
                } else {
                    startActivity(A01_03SubjectList::class.java, 0)
                }
            }

            @Throws(java.lang.Exception::class)
            override fun fail(errmsg: String) {
                isAlertAction = true
                alert(errmsg)
            }
        }
    }

    inner class checkVersion(context: Context, serviceName: String, pShm: StringHashMap?) :
        A903LMSTask(context, serviceName, pShm) {
        override fun success() {
            try {
                var jo = JSONObject(CMcommu.xml)
                var data = jo.getJSONArray("data").getJSONObject(0)
                this@A01_00Intro.loinType = data.getString("updateType")
                this@A01_00Intro.loginMessage = data.getString("content")
                this@A01_00Intro.URL = data.getString("updateURL")
                when {
                    data.getInt("logout") == 1 -> {
                        if (getEnvBoolean(StaticClass.KEY_REMEMBERID, false)) {
                            val shm = StringHashMap()
                            shm["id"] = getEnv(StaticClass.KEY_USERSEQ, "")
                            object : A903LMSTask(context, API_LOGOUT, shm) {
                                @Throws(java.lang.Exception::class)
                                override fun success() {
                                    if (pShm.getString(API_LOGOUT) == StaticClass.YES) {
                                        updateCheck()
                                    } else {
                                        updateCheck()
                                    }
                                }

                                @Throws(java.lang.Exception::class)
                                override fun fail(errmsg: String) {
                                    dismiss()
                                    alert(errorMsg)
                                    updateCheck()
                                }
                            }
                        } else {
                            updateCheck()
                        }
                    }
                    else -> {
                        updateCheck()
                    }
                }
            } catch (e: Exception) {
                EnvError(e)
            }
        }

        override fun fail(errmsg: String?) {
            alert(errmsg ?: "");
        }
    }

    override fun success(what: String?, jyc: CMCommunication?) {
        try {
            if (jyc == null) {
                val id = what!!.toInt()
                if (id == ID_alertOK) {
                    if (isAlertAction) {
                        isAlertAction = false
                        if (isSNU) {
                            startActivity(A01_02Z91LoginSeoul::class.java, 0)
                        } else {
                            startActivity(A01_02Login::class.java, 0)
                        }
                    } else if (isUpdate) {
                        val uri = Uri.parse(URL)
                        val it = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(it)
                        finish()
                    } else if (isServerCheck) {
                        finish()
                    } else if (isNotice) {
                        isNotice = false
                        selectSchool()
                    } else if (isLink) {
                        isLink = false
                        selectSchool()
                        val uri = Uri.parse(content!!.getString("notice_link"))
                        val it = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(it)
                    }
                } else if (id == ID_confirmOK) {
                    if (isUpdate) {
                        val uri = Uri.parse(URL)
                        val it = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(it)
                        finish()
                    } else if (isAutoLogin) {
                        autoLogin()
                    } else if (isNotice) {
                        isNotice = false
                        selectSchool()
                        val uri = Uri.parse(content!!.getString("notice_link"))
                        val it = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(it)
                    }
                } else if (id == ID_confirmCancel) {
                    if (isUpdate) {
                        continueIntro()
                    } else if (isAutoLogin) {
                        finish()
                    } else if (isNotice) {
                        isNotice = false
                        selectSchool()
                    }
                } else {
                    finish()
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        super.success(what, jyc)
    }

    inner class test(context: Context, serviceName: String, pShm: StringHashMap?) :
        A903LMSTask(context, serviceName, pShm) {
        override fun success() {
            try {
            } catch (e: Exception) {
                EnvError(e)
            }
        }

        override fun fail(errmsg: String?) {
            alert(errmsg ?: "");
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
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        return super.onKeyDown(keyCode, event)
    }
}