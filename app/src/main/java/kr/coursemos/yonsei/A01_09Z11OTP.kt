package kr.coursemos.yonsei

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.activity_a01_09_z11_o_t_p.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a99util.CMTimer
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.SEED
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.otp.Totp
import kr.coursemos.myapplication.a99util.EnvError
import kr.coursemos.myapplication.a99util.htmlToString
import java.util.*

class A01_09Z11OTP : A00_00Activity() {

    private var isFinish = false
    private var endTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_09_z11_o_t_p)
        try{
            initBottom("OTP 인증")
            a01_09Z11Number.setTextColor(A901Color.getIhwaColor())
            a01_09Z11ProgressBar.progressTintList = ColorStateList.valueOf(A901Color.getIhwaColor())

            a01_09Z11bottomText.setText(
                "고유번호\n" + Env.SHA256(
                    Env.HexToString(
                        SEED.Encrypt(
                            getEnv(
                                StaticClass.KEY_IDNUMBER,
                                ""
                            ) + getEnv(
                                StaticClass.KEY_SELECTSCHOOLTOKEN,
                                ""
                            ),
                            "utf-8",
                            SEED.iv,
                            SEED.key
                        )
                    )
                )
            )
            setOTP()
            TimeCheck()

        }catch (e:java.lang.Exception){
            EnvError(e)
        }
    }


    override fun finish() {
        isFinish = true
        super.finish()
    }

    @Throws(java.lang.Exception::class)
    private fun setOTP() {
//		Env.SHA256(Env.HexToString(SEED.Encrypt(sp.getString(StaticClass.KEY_IDNUMBER,Env.getTodate())+sp.getString(StaticClass.KEY_SELECTSCHOOLTOKEN,"")+thisTime,"utf-8",SEED.iv,SEED.key)));
        val encode = "GJCTIMJRIY2DEMBUGFDEMNRQHBBUERSGGZBDCMJWGI3DMRBZGIYA"
        a01_09Z11Number.setText(Totp(encode).now())
        endTime =
            System.currentTimeMillis() + (60 - Env.formatTime("ss", Date())
                .toInt()) * 1000
        val sec = ((endTime - System.currentTimeMillis()) / 1000).toInt()
        a01_09Z11AutoCheckTime.setText(String.format("%s<font color='#2da06d'>%02d초</font>","유효 시간 : ",sec).htmlToString())
        a01_09Z11ProgressBar.setProgress(sec * 100 / 60)
    }

    private var timeCheck: CMTimer? = null

    @Throws(java.lang.Exception::class)
    protected fun TimeCheck() {
        if (timeCheck is CMTimer) {
            timeCheck!!.cancel()
            timeCheck = null
        }
        if (isFinish) {
        } else {
            if ( endTime - System.currentTimeMillis() <= 0) {
                setOTP()
            }
            timeCheck = object : CMTimer(1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    try {
                        val sec =
                            ((endTime - System.currentTimeMillis()) / 1000).toInt()
                        //							int min = sec / 60;
//							int hour = min / 60;
//							sec %= 60;
//							min %= 60;//0x2d, 0xa0, 0x6d
//
//							if (sec < 0) {
//								sec = 0;
//								min = 0;
//								hour = 0;
//							}
//							String test=(String)Build.class.getField("SERIAL").get(null);
                        if (isFinish) {
                        } else {
                            a01_09Z11AutoCheckTime.setText(String.format("%s<font color='#2da06d'>%02d초</font>", "유효 시간 : ", sec).htmlToString())
                            a01_09Z11ProgressBar.setProgress(sec * 100 / 60)
                            TimeCheck()
                        }
                    } catch (e: java.lang.Exception) {
                        if (StaticClass.isDebug) {
                            Env.error("hey!!!!!!!!", e)
                        }
                    }
                }
            }
            timeCheck!!.start()
        }
    }

    var onTouch = OnTouchListener { view, event ->
        try {
        } catch (e: Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch

}