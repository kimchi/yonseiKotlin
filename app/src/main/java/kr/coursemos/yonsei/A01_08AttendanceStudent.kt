package kr.coursemos.yonsei

import android.app.AlertDialog
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_08_attendance_student.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class A01_08AttendanceStudent : A00_00Activity() {
    private var attendanceCount = "0"
    private var absenceCount = "0"
    private var perceptionCount = "0"
    private var etcCount = "0"
    private var subjectID: String? = null
    private var subjectName: String? = null
    private lateinit var subjectIDList: Array<String?>
    private lateinit var subjectNameList: Array<String?>
    var onClick: View.OnClickListener = View.OnClickListener {
        try {
            val id: Int = it.getId()
            if (id == R.id.a01_08SubjectSelect) {
                AlertDialog.Builder(me).setItems(
                    subjectNameList
                ) { dialog, which ->
                    try {
                        prd()
                        subjectName = subjectNameList[which]
                        subjectID = subjectIDList[which]
                        val shm = StringHashMap()
                        shm["courseid"] = subjectID
                        object : A903LMSTask(applicationContext, API_MYATTENDANCE, shm) {
                            @Throws(java.lang.Exception::class)
                            override fun success() {
                                dismiss()
                                showList()
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
                }.show()
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_08_attendance_student)

        try{
            initBottom(getString(R.string.a01_08layoutTitle))
            a01_08SubjectSelect.setOnTouchListener { view, event ->
                try {
                    val id: Int = view.getId()
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        if (id == R.id.a01_08SubjectSelect) {
                            val draw: Drawable =
                                applicationContext.getResources().getDrawable(R.drawable.arrow_view_down)
                            draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)
                            a01_08SubjectSelectArrow.setImageDrawable(draw)
                            a01_08SubjectSelect.setTextColor(A901Color.getIhwaColor())

                        }
                    } else if (event.action == MotionEvent.ACTION_UP||event.action == MotionEvent.ACTION_MOVE) {
                        if (id == R.id.a01_08SubjectSelect) {
                            val draw: Drawable =
                                applicationContext.getResources().getDrawable(R.drawable.arrow_view_down)
                            draw.colorFilter = null
                            a01_08SubjectSelectArrow.setImageDrawable(draw)
                            a01_08SubjectSelect.setTextColor(A901Color.WHITE)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Env.error("", e)
                }
                false
            }
            a01_08SubjectSelect.setOnClickListener(onClick)
            a01_08AttendanceStudentList.adapter=adapter
            if (getEnvInt(
                    StaticClass.KEY_LANGUAGE,
                    A01_09Z09Language.ID_row1
                ) === A01_09Z09Language.ID_row2
            ) {
                a01_08Name.setText(
                    getEnv(
                        StaticClass.KEY_LASTNAME,
                        ""
                    )
                )
            } else {
                a01_08Name.setText(
                    getEnv(
                        StaticClass.KEY_FIRSTNAME,
                        ""
                    )
                )
            }

            a01_08IDNumber.setText(
                getEnv(
                    StaticClass.KEY_IDNUMBER,
                    ""
                )
            )

            Picasso.with(applicationContext)
                .load(getEnv(StaticClass.KEY_PICURL, ""))
                .transform(RoundTransform())
                .error(android.R.color.transparent)
                .into(a01_08Pic)
            val cursor = cmdb.select(
                A903LMSTask.API_CURRENTSUBJECT,
                A903LMSTask.API_CURRENTSUBJECT,
                null,
                CMDB.TABLE01_03SubjectList
            )
            val size = cursor.count
            subjectIDList = arrayOfNulls<String>(size)
            subjectNameList = arrayOfNulls<String>(size)
            val index = cursor.getColumnIndex(CMDB.COLUMNjson)
            var jo: JSONObject? = null
            var i = 0
            while (cursor.moveToNext()) {
                jo = JSONObject(cursor.getString(index))
                subjectIDList[i] = jo!!.getString("id")
                if (getEnvInt(StaticClass.KEY_LANGUAGE,A01_09Z09Language.ID_row1) === A01_09Z09Language.ID_row2) {
                    subjectNameList[i] = jo!!.getString("ename")
                } else {
                    subjectNameList[i] = jo!!.getString("fullname")
                }
                i++
            }
            subjectName = if (getEnvInt(
                    StaticClass.KEY_LANGUAGE,
                    A01_09Z09Language.ID_row1
                ) === A01_09Z09Language.ID_row2
            ) {
                getEnv(StaticClass.KEY_SUBJECTNAMEE, "")
            } else {
                getEnv(StaticClass.KEY_SUBJECTNAME, "")
            }

            subjectID = getEnv(StaticClass.KEY_SUBJECTID, "")
//			showList();
            //			showList();
            prd()
            val shm = StringHashMap()
            shm["courseid"] = subjectID
            object : A903LMSTask(applicationContext, API_MYATTENDANCE, shm) {
                @Throws(java.lang.Exception::class)
                override fun success() {
                    dismiss()
                    showList()
                }

                @Throws(java.lang.Exception::class)
                override fun fail(errmsg: String) {
                    dismiss()
                    errorCheckLogout(result, errorMsg)
                }
            }

        }catch (e:Exception){
            EnvError(e)
        }


    }

    @Throws(java.lang.Exception::class)
    private fun showList() {
        a01_08SubjectSelect.setText(subjectName)
        val weeklyJSONList: ArrayList<ArrayList<String>> =
            adapter.weeklyJSONList
        weeklyJSONList.clear()
        var cursor =
            cmdb.select(A903LMSTask.API_SUBJECTWEEKLY, subjectID, null, CMDB.TABLE01_weekly)
        val weeklyArray: ArrayList<String> = adapter.weekList
        weeklyArray.clear()
        var weeklyJSONArray =
            ArrayList<String>()
        var index = cursor.getColumnIndex(CMDB.COLUMNjson)
        var week: String? = null
        var weekJSON: JSONObject? = null
        var i = 0
        while (cursor.moveToNext()) {
            weekJSON = JSONObject(cursor.getString(index))
            week = weekJSON.getString("week")
            if (i == 0) {
                weeklyArray.add(week)
                weeklyJSONList.add(weeklyJSONArray)
            } else {
                if (weeklyArray[weeklyArray.size - 1] == week) {
                } else {
                    weeklyJSONArray = ArrayList()
                    weeklyArray.add(week)
                    weeklyJSONList.add(weeklyJSONArray)
                }
            }
            weeklyJSONArray.add(weekJSON.toString())
            i++
        }
        val jsonList: ArrayList<String> =
            adapter.jsonList
        jsonList.clear()
        cursor = cmdb.select(
            A903LMSTask.API_MYATTENDANCE,
            subjectID,
            null,
            CMDB.TABLE01_08AttendanceStudent
        )
        index = cursor.getColumnIndex(CMDB.COLUMNjson)
        while (cursor.moveToNext()) {
            jsonList.add(cursor.getString(index))
        }
        cursor = cmdb.select(
            A903LMSTask.API_MYATTENDANCE,
            "status_count",
            subjectID,
            CMDB.TABLE01_08AttendanceStudent
        )
        index = cursor.getColumnIndex(CMDB.COLUMNjson)
        if (cursor.moveToNext()) {
            val countJSON = JSONObject(cursor.getString(index))
            attendanceCount = countJSON.getString("1")
            absenceCount = countJSON.getString("2")
            perceptionCount = countJSON.getString("3")
            etcCount = countJSON.getString("4")
            a01_08Count1Count.setText(attendanceCount)
            a01_08Count2Count.setText(absenceCount)
            a01_08Count3Count.setText(perceptionCount)
            a01_08Count4Count.setText(etcCount)
        }
        adapter.notifyDataSetChanged()
    }

    val adapter=thisAdapter()
    inner class thisAdapter() : BaseAdapter(){
        public var weekList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()
        public var weeklyJSONList: ArrayList<ArrayList<String>> = ArrayList()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout: View
            try{
                layout=LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_08row,null,false)
                var a01_08Periods=layout.findViewById<TextView>(R.id.a01_08rowtext)
                var linearLayout=layout.findViewById<LinearLayout>(R.id.a01_08linearlayout)

                val tempWeekly = weekList[position]

                if (tempWeekly.length > 3) {
                    val sdfFormat = SimpleDateFormat(
                        "yyyy" + getString(R.string.a99year) + " MM" + getString(R.string.a99month) + " dd" + getString(
                            R.string.a99day
                        ), Locale.KOREA
                    )
                    a01_08Periods.text = sdfFormat.format(Date((tempWeekly + "000").toLong()))
                } else {
                    a01_08Periods.text = tempWeekly + " " + getString(R.string.a01_07activityWeekly)
                }


                var stat: String? = null
                var size = 0
                size = jsonList.size
                var myStatusJSONObject: JSONObject? = null
                for (i in 0 until size) {
                    myStatusJSONObject = JSONObject(jsonList[i])
                    if (myStatusJSONObject.isNull(tempWeekly)) {
                    } else {
                        myStatusJSONObject = myStatusJSONObject.getJSONObject(tempWeekly)
                        break
                    }
                }

                val iter = myStatusJSONObject!!.keys()
                var key: String? = null
                lateinit var periodlayout:View
                lateinit var periodTextView: TextView
                lateinit var periodStatusTextView: TextView
                while (iter.hasNext()) {
                    key = iter.next()
                    periodlayout=LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_08rowadd,null,false)
                    linearLayout.addView(periodlayout)

                    periodTextView=periodlayout.findViewById(R.id.a01_08periodTextView)
                    periodTextView.setText(key + " " + getString(R.string.a01_08activityPeriod))

                    periodStatusTextView=periodlayout.findViewById(R.id.a01_08periodStatusTextView)
                    stat = myStatusJSONObject!!.getString(key)
                    if (stat == "1") {
                        periodStatusTextView.setText(getString(R.string.a01_08activityStat1))
                        periodStatusTextView.setTextColor(A901Color.getIhwaColor())
                    } else if (stat == "2") {
                        periodStatusTextView.setText(getString(R.string.a01_08activityStat2))
                        periodStatusTextView.setTextColor(A901Color.getAttendanceAbsence())
                    } else if (stat == "3") {
                        periodStatusTextView.setText(getString(R.string.a01_08activityStat3))
                        periodStatusTextView.setTextColor(A901Color.getAttendanceLateness())
                    } else if (stat == "4") {
                        periodStatusTextView.setText(getString(R.string.a01_08activityStat4))
                        periodStatusTextView.setTextColor(A901Color.getAttendanceEtc())
                    }else{
                        periodStatusTextView.setText("")
                    }
                }

            }catch (e:Exception){
                EnvError(e)
            }

            return  layout
        }

        override fun getItem(position: Int): Any {
            return 0

        }

        override fun getItemId(position: Int): Long {

            return 1
        }

        override fun getCount(): Int {
            return weeklyJSONList.size
        }

    }
}