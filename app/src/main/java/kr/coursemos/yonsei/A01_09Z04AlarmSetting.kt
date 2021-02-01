package kr.coursemos.yonsei

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_a01_09_z04_alarm_setting.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError

class A01_09Z04AlarmSetting : A00_00Activity() {
    var isPreviewOn = false
    var isAlarmOn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_09_z04_alarm_setting)
        try{
            initBottom(getString(R.string.a01_09z04layoutTitle))

            isAlarmOn =
                getEnvBoolean(StaticClass.KEY_ALARMONOFF, true)
            isPreviewOn =
                getEnvBoolean(StaticClass.KEY_ALARMPREVIEWONOFF, true)

            if (isAlarmOn) {
                a01_0904rowpic1.setVisibility(View.VISIBLE)
            } else {
                a01_0904rowpic1.setVisibility(View.GONE)
            }
            if (isPreviewOn) {
                a01_0904rowpic2.setVisibility(View.VISIBLE)
            } else {
                a01_0904rowpic2.setVisibility(View.GONE)
            }
            a01_0904row1.setOnTouchListener(onTouch)
            a01_0904row1.setOnClickListener {
                    try{


                        isAlarmOn = !isAlarmOn
                        setEnvBoolean(
                            StaticClass.KEY_ALARMONOFF,
                            isAlarmOn
                        )
                        if (isAlarmOn) {

                            a01_0904rowpic1.setVisibility(View.VISIBLE)
                            a01_0904row2.setVisibility(View.VISIBLE)
                            if (isPreviewOn) {
                                a01_0904rowpic2.setVisibility(View.VISIBLE)
                            } else {
                                a01_0904rowpic2.setVisibility(View.GONE)
                            }
                        } else {
                            a01_0904rowpic1.setVisibility(View.GONE)
                            a01_0904row2.setVisibility(View.GONE)
                            a01_0904rowpic2.setVisibility(View.GONE)
                        }

                        object : A903LMSTask(applicationContext, API_PUSHSET, null) {
                            @Throws(java.lang.Exception::class)
                            override fun success() {
                            }

                            @Throws(java.lang.Exception::class)
                            override fun fail(errmsg: String) {
                                Env.error(errmsg, java.lang.Exception())
                            }
                        }

                    }catch (e:java.lang.Exception){
                        EnvError(e)
                    }
            }
            a01_0904row2.setOnTouchListener(onTouch)
            a01_0904row2.setOnClickListener {
                try{
                    isPreviewOn = !isPreviewOn

                    setEnvBoolean(
                        StaticClass.KEY_ALARMPREVIEWONOFF,
                        isPreviewOn
                    )

                    if (isPreviewOn) {
                        a01_0904rowpic2.setVisibility(View.VISIBLE)
                    } else {
                        a01_0904rowpic2.setVisibility(View.GONE)
                    }
                    object : A903LMSTask(applicationContext, API_PUSHSET, null) {
                        @Throws(java.lang.Exception::class)
                        override fun success() {
                        }

                        @Throws(java.lang.Exception::class)
                        override fun fail(errmsg: String) {
                            Env.error(errmsg, java.lang.Exception())
                        }
                    }
                }catch (e:java.lang.Exception){
                    EnvError(e)
                }
            }
            }catch (e:java.lang.Exception){
            EnvError(e)
        }
    }

    var onTouch = View.OnTouchListener { view, event ->
        try {
            val id = view.id
            if (event.action == MotionEvent.ACTION_DOWN) {
                view.setBackgroundResource(R.drawable.line_box_push)
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
                view.setBackgroundResource(R.drawable.line_box)
            }
        } catch (e: Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch
}