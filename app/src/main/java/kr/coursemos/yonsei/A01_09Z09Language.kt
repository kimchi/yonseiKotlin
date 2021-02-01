package kr.coursemos.yonsei

import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.activity_a01_09_z09_language.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import java.util.*

class A01_09Z09Language : A00_00Activity() {
    companion object{
        var ID_row1:Int = 12500001
        var ID_row2:Int = 12500002
        var ID_row3:Int = 12500003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_09_z09_language)
        try{
            initBottom(getString(R.string.a01_09z09layoutTitle))

            var langType= getEnvInt(
                StaticClass.KEY_LANGUAGE,
                ID_row1
            )
            val draw: Drawable = resources.getDrawable(R.drawable.check)
            draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)

            when(langType){
                ID_row1->{
                    a01_0909rowpic1.setImageDrawable(draw)
                    a01_0909rowpic1.visibility=View.VISIBLE
                }
                ID_row2->{
                    a01_0909rowpic2.setImageDrawable(draw)
                    a01_0909rowpic2.visibility=View.VISIBLE
                }
                ID_row3->{
                    a01_0909rowpic3.setImageDrawable(draw)
                    a01_0909rowpic3.visibility=View.VISIBLE
                }
            }

            a01_0909row1.setTag(ID_row1.toString())
            a01_0909row2.setTag(ID_row2.toString())
            a01_0909row3.setTag(ID_row3.toString())

            a01_0909row1.setOnTouchListener(onTouch)
            a01_0909row1.setOnClickListener(onClick)
            a01_0909row2.setOnTouchListener(onTouch)
            a01_0909row2.setOnClickListener(onClick)
            a01_0909row3.setOnTouchListener(onTouch)
            a01_0909row3.setOnClickListener(onClick)
        }catch (e:Exception){
            EnvError(e)
        }

    }

    var onClick = View.OnClickListener { view ->
        try {
            val id = view.id
            setEnvInt(StaticClass.KEY_LANGUAGE, (view.tag as String).toInt())
            var locale = Locale("kr")
            val language: Int = getEnvInt(
                StaticClass.KEY_LANGUAGE,
                ID_row1
            )
            if (language == ID_row2) {
                locale = Locale("en")
            } else if (language == ID_row3) {
                locale = Locale("ja")
            }
            Locale.setDefault(locale)
            val config =
                Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            prd()
            val shm = StringHashMap()
            object : A903LMSTask(applicationContext, API_MAINBOARD, shm) {
                @Throws(java.lang.Exception::class)
                override fun success() {
                    startActivity(A01_09Z09Language::class.java, 0)
                    dismiss()
                }

                @Throws(java.lang.Exception::class)
                override fun fail(errmsg: String) {
                    dismiss()
                    errorCheckLogout(result, errorMsg)
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    } // OnClickListener onClick

    var onTouch = OnTouchListener { view, event ->
        try {
            val id = view.id
            if (event.action == MotionEvent.ACTION_DOWN) {
                view.setBackgroundResource(R.drawable.line_box_push)
            } else if (event.action == MotionEvent.ACTION_UP||event.action == MotionEvent.ACTION_MOVE) {
                view.setBackgroundResource(R.drawable.line_box)
            }
        } catch (e: Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch

}