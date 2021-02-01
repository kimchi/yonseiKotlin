package kr.coursemos.yonsei

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.activity_a01_09_z10_auth.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError
import java.net.URLEncoder

class A01_09Z10Auth : A00_00Activity() {
    var isBioface = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_09_z10_auth)
        try{
            initBottom(getString(R.string.a01_09layoutRow8auth))
            a01_0910row1.setOnTouchListener(onTouch)
            a01_0910row2.setOnTouchListener(onTouch)
            a01_0910row3.setOnTouchListener(onTouch)

            a01_0910row1.setOnClickListener(onClick)
            a01_0910row2.setOnClickListener(onClick)
            a01_0910row3.setOnClickListener(onClick)

            isBioface =
                getEnvBoolean(StaticClass.KEY_ISBIOFACE, false)
            if (isBioface) {
                a01_0910rowpic3.setVisibility(View.VISIBLE)
            } else {
                a01_0910rowpic3.setVisibility(View.INVISIBLE)
            }

        }catch (e:Exception){
            EnvError(e)
        }
    }


    var onClick = View.OnClickListener { view ->
        try {
            val id = view.id
            if (id == R.id.a01_0910row1) {
                setEnvBoolean("BIOPASSINIT", true)
                startActivity(A01_09Z08Inquiry::class.java, 0)
            } else if (id == R.id.a01_0910row2) {
                setEnv(
                    StaticClass.KEY_WEBURL,
                    URLEncoder.encode(
                        "https://www.coursemos.kr/face_authentication.html",
                        StaticClass.charset
                    )
                )
                setEnv(
                    StaticClass.KEY_WEBVIEWNAME,
                    getString(R.string.a01_09layoutRow8_2)
                )
                startActivity(A01_13WebView::class.java, 0)
            } else if (id == R.id.a01_0910row3) {
                isBioface = !isBioface
                setEnvBoolean(
                    StaticClass.KEY_ISBIOFACE,
                    isBioface
                )
                if (isBioface) {
                    a01_0910rowpic3.setVisibility(View.VISIBLE)
                } else {
                    a01_0910rowpic3.setVisibility(View.INVISIBLE)
                }

            }
        } catch (e: Exception) {
            Env.error("", e)
        }
    } // OnClickListener onClick


    var onTouch = OnTouchListener { view, event ->
        try {
            val id = view.id
            if (event.action == MotionEvent.ACTION_DOWN) {
                view.setBackgroundResource(R.drawable.line_box_push)
            } else if (event.action == MotionEvent.ACTION_UP) {
                view.setBackgroundResource(R.drawable.line_box)
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch


}