package kr.coursemos.yonsei

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.activity_a01_07_z91_auto_attendance_teacher.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import kr.coursemos.myapplication.a99util.htmlToString
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class A01_07Z91AutoAttendanceTeacher : A00_00Activity() {

    private var SubjectID: String? = null
    private lateinit var weeklyList: Array<String?>
    private lateinit var periodTextList: Array<String?>
    private lateinit var durationTextList: Array<String?>
    private lateinit var durationList: Array<Int?>
    private var currentDuration = 0
    private lateinit var weeklyJSONList: ArrayList<ArrayList<String>>
    private lateinit var weeklyArray: ArrayList<String>
    private lateinit var periodJSONList: ArrayList<String>
    private var currentPeriod: String? = null
    private lateinit var autoInfo: JSONArray
    private var isFinish = false
    private var endTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_07_z91_auto_attendance_teacher)
        try{
            initBottom(getString(R.string.a01_07Z91layoutTitle))
            a01_07Z91AutoWeeklySelect.setBackgroundColor(A901Color.getIhwaColor())
            a01_07Z91AutoWeeklySelect.setOnTouchListener(onTouch)
            a01_07Z91AutoWeeklySelect.setOnClickListener(onClick)

            a01_07Z91AutoPeriod.setBackgroundColor(A901Color.getDarkHeaderBackground())
            a01_07Z91AutoPeriod.setOnTouchListener(onTouch)
            a01_07Z91AutoPeriod.setOnClickListener(onClick)

            a01_07Z91AutoPeriod.setOnTouchListener(onTouch)
            a01_07Z91AutoPeriod.setOnClickListener(onClick)
            a01_07Z91ReadyTime.setBackgroundResource(R.drawable.item_autoattendance_timebox)
            a01_07Z91ReadyTime.setOnTouchListener(onTouch)
            a01_07Z91ReadyTime.setOnClickListener(onClick)
            a01_07Z91ReadyStart.setBackgroundColor(A901Color.getIhwaColor())
            a01_07Z91ReadyStart.setOnTouchListener(onTouch)
            a01_07Z91ReadyStart.setOnClickListener(onClick)
            a01_07Z91memo.text = applicationContext.getString(R.string.a01_07z91memo).htmlToString()

            a01_07Z91AutoCheckLayout.visibility = View.GONE
            a01_07Z91AutoCheckEnd.setBackgroundColor(A901Color.getOrange())
            a01_07Z91AutoCheckEnd.setOnTouchListener(onTouch)
            a01_07Z91AutoCheckEnd.setOnClickListener(onClick)

            a01_07Z91AutoCheckManagement.setBackgroundColor(A901Color.getIhwaColor())
            a01_07Z91AutoCheckManagement.setOnTouchListener(onTouch)
            a01_07Z91AutoCheckManagement.setOnClickListener(onClick)

            var cursor = cmdb.select(
                A903LMSTask.API_SUBJECTWEEKLY,
                getEnv(StaticClass.KEY_SUBJECTID, ""),
                null,
                CMDB.TABLE01_weekly
            )
            weeklyArray = ArrayList<String>()
            var weeklyJSONArray: ArrayList<String> =
                ArrayList()
            weeklyJSONList = ArrayList<ArrayList<String>>()
            val index = cursor.getColumnIndex(CMDB.COLUMNjson)
            var week: String? = null
            var weekJSON: JSONObject? = null

            var i = 0
            while (cursor.moveToNext()) {
                weekJSON = JSONObject(cursor.getString(index))
                week = weekJSON!!.getString("week")
                if (i == 0) {
                    weeklyArray.add(week)
                    weeklyJSONList.add(weeklyJSONArray)
                } else {
                    if (weeklyArray.get(weeklyArray.size - 1) == week) {
                    } else {
                        weeklyJSONArray = ArrayList()
                        weeklyArray.add(week)
                        weeklyJSONList.add(weeklyJSONArray)
                    }
                }
                weeklyJSONArray!!.add(weekJSON.toString())
                i++
            }


            var size: Int = weeklyArray.size
            weeklyList = arrayOfNulls<String>(size)
            var tempWeekly: String? = null
            var sdfFormat = SimpleDateFormat(
                "yyyy" + getString(R.string.a99year) + " MM" + getString(R.string.a99month) + " dd" + getString(
                    R.string.a99day
                ), Locale.KOREA
            )
            for (i in 0 until size) {
                tempWeekly = weeklyArray.get(i)
                if (tempWeekly.length > 3) {
                    weeklyList[i] =
                        sdfFormat.format(Date((tempWeekly + "000").toLong()))
                } else {
                    weeklyList[i] = tempWeekly + " " + getString(R.string.a01_07activityWeekly)
                }
            }


            SubjectID = getEnv(StaticClass.KEY_SUBJECTID, "")


            cursor = cmdb.select(
                A903LMSTask.API_ATTENDANCEAUTOCHECK,
                "duration",
                null,
                CMDB.TABLE01_07Z91AutoAttendanceTeacher
            )
            val indexJson = cursor.getColumnIndex(CMDB.COLUMNjson)
            var duration = 0
            size = cursor.count
            durationTextList = arrayOfNulls<String>(size)
            durationList = arrayOfNulls<Int>(size)
            i = 0
            while (cursor.moveToNext()) {
                duration = cursor.getString(indexJson).toInt()
                durationList[i] = duration
                durationTextList[i] =  "${duration / 60}${getString(R.string.a01_07z91activitymin)}"
                i++
            }
            a01_07Z91ReadyTime.setText(durationTextList.get(currentDuration))


            endTime = System.currentTimeMillis()
            TimeCheck()
            refreshAutoAttendance()


            sdfFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            var weekly = 0
            val weeklyTemp: Int =
                getEnvInt(StaticClass.KEY_SUBJECTPOSITION, 1)
            size = weeklyArray.size
            val currentLong =
                sdfFormat.parse(sdfFormat.format(Date(System.currentTimeMillis())))
                    .time
//			currentLong=sdfFormat.parse("2019-01-14").getTime();
            //			currentLong=sdfFormat.parse("2019-01-14").getTime();
            var weeklyLong: Long = 0
            var isDateSelect = false
            for (i in 0 until size) {
                weeklyLong = (weeklyArray.get(i).toString() + "000").toLong()
                weeklyLong = sdfFormat.parse(sdfFormat.format(Date(weeklyLong))).time
                if (weeklyLong > 999000) {
                    if (isDateSelect) {
                    } else if (weeklyLong == currentLong) {
                        weekly = i
                        isDateSelect = true
                    } else if (weeklyLong < currentLong) {
                        weekly = i
                    } else {
                        isDateSelect = true
                    }
                } else {
                    weeklyLong /= 1000
                    if (weeklyTemp.toLong() == weeklyLong) {
                        weekly = i
                        break
                    }
                }
            }

            if (size == 0) {
                isAlertAction = true
                alert("주차정보가 없습니다.")
            } else {
                selectWeekly(weekly)
                autoSelect()
            }

        }catch (e:Exception){
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun autoSelect() {
        val size: Int = autoInfo.length()
        var endTime: Long = 0
        var tempWeekly: String? = null
        for (i in 0 until size) {
            val jo: JSONObject = autoInfo.getJSONObject(i)
            endTime = jo.getLong("timecreated") * 1000 + jo.getLong("duration") * 1000
            if (endTime - System.currentTimeMillis() > 0) {
                tempWeekly = jo.getString("week")
                var weeklySize: Int = weeklyArray.size
                for (j in 0 until weeklySize) {
                    if (tempWeekly == weeklyArray.get(j)) {
                        selectWeekly(j)
                        break
                    }
                }
                tempWeekly = jo.getString("attid")
                weeklySize = periodJSONList.size
                for (j in 0 until weeklySize) {
                    if (tempWeekly == JSONObject(periodJSONList.get(j)).getString("id")) {
                        selectPeriod(j)
                        break
                    }
                }
                break
            }
        }
    }

    @Throws(java.lang.Exception::class)
    private fun refreshAutoAttendance() {
        cmdb = CMDB.open(applicationContext)
        autoInfo = JSONArray()
        val cursor = cmdb.select(
            A903LMSTask.API_ATTENDANCEAUTOCHECK,
            "autoinfo",
            null,
            CMDB.TABLE01_07Z91AutoAttendanceTeacher
        )
        val indexJson = cursor.getColumnIndex(CMDB.COLUMNjson)
        while (cursor.moveToNext()) {
            autoInfo.put(JSONObject(cursor.getString(indexJson)))
        }
    } // showList();


    var onClick = View.OnClickListener { view ->
        try {
            closeKeyboard()
            val id = view.id
            if (id == R.id.a01_07Z91AutoWeeklySelect) {
                AlertDialog.Builder(me)
                    .setItems(
                        weeklyList,
                        DialogInterface.OnClickListener { dialog, which ->
                            try {
                                selectWeekly(which)
                            } catch (e: java.lang.Exception) {
                                Env.error("", e)
                            }
                        }).show()
            } else if (id == R.id.a01_07Z91AutoPeriod) {
                selectPeriodAlert()
            } else if (id == R.id.a01_07Z91ReadyStart) {
                prd()
                val shm = StringHashMap()
                shm["courseid"] = SubjectID
                shm["attid"] = currentPeriod
                shm["duration"] = Integer.toString(durationList.get(currentDuration)!!)
                object : A903LMSTask(applicationContext, API_ATTENDANCEAUTOCHECKSTART, shm) {
                    @Throws(java.lang.Exception::class)
                    override fun success() {
                        dismiss()
                        refreshAutoAttendance()
                        autoCheck()
                    }

                    @Throws(java.lang.Exception::class)
                    override fun fail(errmsg: String) {
                        dismiss()
                        val jo = JSONObject(result)
                        if (jo.getString("errorcode") == "auto_attendance_error_used") {
                            alert(jo.getString("message").htmlToString())
                            autoSelect()
                        } else {
                            errorCheckLogout(result, errorMsg)
                        }
                    }
                }
            } else if (id == R.id.a01_07Z91AutoCheckEnd) {
                confirm(getString(R.string.a01_07z91activityautocheckend))
            } else if (id == R.id.a01_07Z91AutoCheckManagement) {
                startActivity(A01_07AttendanceTeacher::class.java, 0)
            } else if (id == R.id.a01_07Z91ReadyTime) {
                selectDuration()
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    } // OnClickListener onClick


    @Throws(java.lang.Exception::class)
    private fun selectWeekly(which: Int) {
        a01_07Z91AutoWeeklySelect.setText(weeklyList.get(which))
        periodJSONList = weeklyJSONList.get(which)
        val size: Int = periodJSONList.size
        periodTextList = arrayOfNulls<String>(size)
        var periodJSON: JSONObject? = null
        for (i in 0 until size) {
            periodJSON = JSONObject(periodJSONList.get(i))
            periodTextList[i] =
                periodJSON!!.getString("period") + getString(R.string.a01_07activityPeriod)
        }
        if (size > 0) {
            selectPeriod(0)
        } else {
            currentPeriod = ""
            a01_07Z91AutoPeriod.setText("")
        }
    }


    override fun finish() {
        isFinish = true
        super.finish()
    }

    @Throws(java.lang.Exception::class)
    private fun selectPeriodAlert() {
        AlertDialog.Builder(me)
            .setItems(periodTextList,
                DialogInterface.OnClickListener { dialog, which ->
                    try {
                        selectPeriod(which)
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }).show()
    }

    @Throws(java.lang.Exception::class)
    private fun selectPeriod(which: Int) {
        a01_07Z91AutoPeriod.setText(periodTextList.get(which))
        currentPeriod = JSONObject(periodJSONList.get(which)).getString("id")
        autoCheck()
    }

    @Throws(java.lang.Exception::class)
    private fun autoCheck() {
        var isTimeCheck = false
        val size: Int = autoInfo.length()
        for (i in 0 until size) {
            val jo: JSONObject = autoInfo.getJSONObject(i)
            if (currentPeriod == jo.getString("attid")) {
                endTime = jo.getLong("timecreated") * 1000 + jo.getLong("duration") * 1000
                if (endTime - System.currentTimeMillis() > 0) {
                    isTimeCheck = true
                    a01_07Z91AutoCheckNumber.setText(jo.getString("rnumber"))
                    break
                }
            }
        }
        if (isTimeCheck) {
            a01_07Z91ReadyLayout.setVisibility(View.GONE)
            a01_07Z91AutoCheckLayout.setVisibility(View.VISIBLE)
        } else {
            a01_07Z91ReadyLayout.setVisibility(View.VISIBLE)
            a01_07Z91AutoCheckLayout.setVisibility(View.GONE)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun selectDuration() {
        AlertDialog.Builder(me)
            .setItems(durationTextList,
                DialogInterface.OnClickListener { dialog, which ->
                    try {
                        currentDuration = which
                        a01_07Z91ReadyTime.setText(durationTextList.get(currentDuration))
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }).show()
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
            if (a01_07Z91AutoCheckLayout.getVisibility() === View.VISIBLE && endTime - System.currentTimeMillis() <= 0) {
                alert(getString(R.string.a01_07z91activityautoendmessage))
                autoCheck()
            }
            timeCheck = object : CMTimer(1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    try {
                        var sec =(endTime - System.currentTimeMillis()) / 1000
                        var min = sec / 60
                        var hour = min / 60
                        sec %= 60
                        min %= 60 //0x2d, 0xa0, 0x6d
                        if (sec < 0) {
                            sec = 0
                            min = 0
                            hour = 0
                        }
                        if (isFinish) {
                        } else {
                            a01_07Z91AutoCheckTime.setText(
                                Html.fromHtml(
                                    java.lang.String.format(
                                        "%s<font color='#2da06d'>%02d:%02d:%02d</font>",
                                        getString(R.string.a01_07z91layouttime),
                                        hour,
                                        min,
                                        sec
                                    )
                                )
                            )
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
    override fun success(what: String?, jyc: CMCommunication?) {
        try {
            val id = what?.toInt()
            if (id == ID_alertOK) {
                if (isAlertAction) {
                    onKeyDown(KeyEvent.KEYCODE_BACK, null)
                }
            } else if (id == ID_confirmOK) {
                prd()
                val shm = StringHashMap()
                shm["courseid"] = SubjectID
                shm["attid"] = currentPeriod
                object : A903LMSTask(applicationContext, API_ATTENDANCEAUTOCHECKEND, shm) {
                    @Throws(java.lang.Exception::class)
                    override fun success() {
                        dismiss()
                        refreshAutoAttendance()
                        autoCheck()
                    }

                    @Throws(java.lang.Exception::class)
                    override fun fail(errmsg: String) {
                        dismiss()
                        //						JSONObject jo = new JSONObject(result);
                        errorCheckLogout(result, errorMsg)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }


    var onTouch = OnTouchListener { view, event ->
        try {
            val id = view.id
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (id == R.id.a01_07Z91AutoWeeklySelect) {
                    a01_07Z91AutoWeeklySelect.setBackgroundColor(A901Color.getIhwaColorDown())
                } else if (id == R.id.a01_07Z91AutoPeriod) {
                    a01_07Z91AutoPeriod.setBackgroundColor(A901Color.getDarkHeaderBackgroundDown())
                } else if (id == R.id.a01_07Z91ReadyStart) {
                    a01_07Z91ReadyStart.setBackgroundColor(A901Color.getIhwaColorDown())
                } else if (id == R.id.a01_07Z91ReadyTime) {
                    a01_07Z91ReadyTime.setBackgroundResource(R.drawable.item_autoattendance_timebox_down)
                } else if (id == R.id.a01_07Z91AutoCheckManagement) {
                    a01_07Z91AutoCheckManagement.setBackgroundColor(A901Color.getIhwaColorDown())
                } else if (id == R.id.a01_07Z91AutoCheckEnd) {
                    a01_07Z91AutoCheckEnd.setBackgroundColor(A901Color.getOrangeDown())
                }
            } else if (event.action == MotionEvent.ACTION_UP||event.action == MotionEvent.ACTION_MOVE) {
                if (id == R.id.a01_07Z91AutoWeeklySelect) {
                    a01_07Z91AutoWeeklySelect.setBackgroundColor(A901Color.getIhwaColor())
                } else if (id == R.id.a01_07Z91AutoPeriod) {
                    a01_07Z91AutoPeriod.setBackgroundColor(A901Color.getDarkHeaderBackground())
                } else if (id == R.id.a01_07Z91ReadyStart) {
                    a01_07Z91ReadyStart.setBackgroundColor(A901Color.getIhwaColor())
                } else if (id == R.id.a01_07Z91ReadyTime) {
                    a01_07Z91ReadyTime.setBackgroundResource(R.drawable.item_autoattendance_timebox)
                } else if (id == R.id.a01_07Z91AutoCheckManagement) {
                    a01_07Z91AutoCheckManagement.setBackgroundColor(A901Color.getIhwaColor())
                } else if (id == R.id.a01_07Z91AutoCheckEnd) {
                    a01_07Z91AutoCheckEnd.setBackgroundColor(A901Color.getOrange())
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch

}