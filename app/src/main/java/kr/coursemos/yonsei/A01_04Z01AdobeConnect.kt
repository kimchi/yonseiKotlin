package kr.coursemos.yonsei

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.activity_a01_04_z01_adobe_connect.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError
import kr.coursemos.myapplication.a99util.htmlToString
import org.json.JSONObject

class A01_04Z01AdobeConnect : A00_00Activity() {
    private lateinit var detail: JSONObject
    private var isJoin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_04_z01_adobe_connect)
        try{
            initBottom(getString(R.string.a01_04z01layoutTitle))
            a01_04Z01Button.setBackgroundColor(A901Color.getIhwaColor())
            a01_04Z01Button.setOnTouchListener(onTouch)
            a01_04Z01Button.setOnClickListener(onClick)
            val jsonString = cmdb.selectString(A903LMSTask.API_ADOBECONNECT,
                A903LMSTask.API_ADOBECONNECT,
                A903LMSTask.API_ADOBECONNECT,
                CMDB.TABLE01_common)
            if (StaticClass.isDebug) {
                Env.debug(applicationContext, jsonString)
            }
            if (jsonString == null) {
                alert(getString(R.string.a01_04z01activityJoinFail))
            } else {
                detail = JSONObject(jsonString)
                a01_04Z01Subject.setText(detail.getString("name"))
                a01_04Z01StartTime.setText(Env.getLongToDate(applicationContext,
                    detail.getString("starttime")))
                a01_04Z01EndTime.setText(Env.getLongToDate(applicationContext,
                    detail.getString("endtime")))
                a01_04Z01Content.setText(detail.getString("intro").htmlToString())
                isJoin = detail.getBoolean("participants")
                if (isJoin) {
                    val current = System.currentTimeMillis() / 1000
                    val startTime: Long = detail.getLong("starttime")
                    val endTime: Long = detail.getLong("endtime")
                    if (startTime <= current && current <= endTime) {
                        a01_04Z01Button.setText(getString(R.string.a01_04z01activityJoin))
                    } else {
                        isJoin = false
                        a01_04Z01Button.setText(getString(R.string.a01_04z01activityOK))
                    }
                } else {
                    a01_04Z01Button.setText(getString(R.string.a01_04z01activityOK))
                }
            }

        }catch (e:java.lang.Exception){
            EnvError(e)
        }
    }
    var onTouch = OnTouchListener { view, event ->
        try {
            val id = view.id
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (id == R.id.a01_04Z01Button) {
                    a01_04Z01Button.setBackgroundColor(A901Color.getIhwaColorDown())
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (id == R.id.a01_04Z01Button) {
                    a01_04Z01Button.setBackgroundColor(A901Color.getIhwaColor())
                }
            }
        } catch (e: Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch
    var onClick = View.OnClickListener { view ->
        val id = view.id
        try {
            if (id == R.id.a01_04Z01Button) {
                if (isJoin) {
                    val packageManager = me.packageManager
                    val packages =
                        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                    for (packageInfo in packages) {
                        if ("air.com.adobe.connectpro" == packageInfo.packageName) {
                            val uri = Uri.parse("connectpro://" + detail.getString("url"))
                            val it = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(it)
                            return@OnClickListener
                        }
                    }
                    val uri = Uri.parse("market://search?q=pname:air.com.adobe.connectpro")
                    val it = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(it)
                } else {
                    onKeyDown(KeyEvent.KEYCODE_BACK, null)
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    } // OnClickListener onClick
}