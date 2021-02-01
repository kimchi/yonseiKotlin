package kr.coursemos.yonsei

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_a01_04_subject_detail.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject

class A01_04SubjectDetail : A00_00Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_04_subject_detail)
        try{
            initBottom(getEnv(StaticClass.KEY_SUBJECTNAMEE, ""))
            adapter=thisAdapter(this)
            a01_04veiwpager.adapter=adapter
            a01_04listBtn.setOnClickListener {
                a01_04veiwpager.setCurrentItem(0)
                a01_04listBtn.setBackgroundColor(A901Color.getIhwaColor())
                a01_04menuBtn.setBackgroundColor(A901Color.getHeaderBackground())
            }
            a01_04menuBtn.setOnClickListener {
                a01_04veiwpager.setCurrentItem(1)
                a01_04listBtn.setBackgroundColor(A901Color.getHeaderBackground())
                a01_04menuBtn.setBackgroundColor(A901Color.getIhwaColor())
            }

            a01_04veiwpager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    when(position){
                        0->{
                            a01_04listBtn.setBackgroundColor(A901Color.getIhwaColor())
                            a01_04menuBtn.setBackgroundColor(A901Color.getHeaderBackground())
                        }
                        1-> {
                            a01_04listBtn.setBackgroundColor(A901Color.getHeaderBackground())
                            a01_04menuBtn.setBackgroundColor(A901Color.getIhwaColor())
                        }
                    }
                    setEnvInt(StaticClass.KEY_SUBJECTTAB, position)

                    super.onPageSelected(position)
                }
            })

            val menuList = JSONObject(
                getEnv(
                    StaticClass.KEY_SELECTSCHOOLMENULIST,
                    "{}"
                )
            )
            if(isBackKey){
                a01_04veiwpager.setCurrentItem(getEnvInt(StaticClass.KEY_SUBJECTTAB, 0), true);
            }else{
                if (menuList.isNull("pautoatt") && menuList.isNull("autoatt")) {
                } else {

                    prd()
                    val shm = StringHashMap()
                    shm["courseid"] =
                        getEnv(StaticClass.KEY_SUBJECTID, "")
                    object : A903LMSTask(applicationContext, API_ATTENDANCEAUTOCHECK, shm) {
                        @Throws(java.lang.Exception::class)
                        override fun success() {
                            dismiss()
                        }

                        @Throws(java.lang.Exception::class)
                        override fun fail(errmsg: String) {

                            dismiss()
                            errorCheckLogout(result, errorMsg)
                        }
                    }
                }
            }


        }catch (e:Exception){
            EnvError(e)
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (getEnvInt(StaticClass.KEY_SUBJECTTAB, 0) > 0) {
                    a01_04veiwpager.setCurrentItem(0)
                    a01_04listBtn.setBackgroundColor(A901Color.getIhwaColor())
                    a01_04menuBtn.setBackgroundColor(A901Color.getHeaderBackground())
                    return true
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        return super.onKeyDown(keyCode, event)
    }

    lateinit var adapter:thisAdapter
    inner class thisAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa){
        override fun getItemCount(): Int {
            return 2
        }


        override fun createFragment(position: Int): Fragment {
            when(position){
                0 -> {
                    return  A01_04Z01WeeklyList.newInstance("","")
                }
            }



            return  A01_04Z02MenuAdapter.newInstance("","")
        }

    }
}