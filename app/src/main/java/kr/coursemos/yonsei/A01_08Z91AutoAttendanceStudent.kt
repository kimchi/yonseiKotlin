package kr.coursemos.yonsei

import android.app.AlertDialog
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.view.MotionEvent
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_08_z91_auto_attendance_student.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import kr.coursemos.myapplication.a99util.htmlToString
import org.json.JSONObject

class A01_08Z91AutoAttendanceStudent : A00_00Activity() {
    private var attendanceCount = "0"
    private var absenceCount = "0"
    private var perceptionCount = "0"
    private var etcCount = "0"
    private var subjectID: String? = null
    private var subjectName: String? = null
    private var attID: String? = null
    private lateinit var subjectIDList: Array<String?>
    private lateinit var subjectNameList: Array<String?>
    private var isFinish = false
    private var endTime: Long = 0

    var onClick: View.OnClickListener = View.OnClickListener {
        try {
            val id: Int = it.getId()
            if (id == R.id.a01_08Z91SubjectSelect) {
                AlertDialog.Builder(me).setItems(
                    subjectNameList
                ) { dialog, which ->
                    try {
                        subjectName = subjectNameList[which]
                        subjectID = subjectIDList[which]
                        getMyinfo()
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }.show()
            } else if (id == R.id.a01_08Z91ReadyStart) {
                if (a01_08Z91ReadyAuth.getText().length === 0) {
                    alert(getString(R.string.a01_08z91activityempty))
                } else {
                    val shm = StringHashMap()
                    shm["courseid"] = subjectID
                    shm["attid"] = attID
                    shm["secretkey"] = a01_08Z91ReadyAuth.getText().toString()
                    object :
                        A903LMSTask(applicationContext, API_ATTENDANCEAUTOCHECKSTUDENTREQ, shm) {
                        @Throws(java.lang.Exception::class)
                        override fun success() {
                            val jo = JSONObject(result).getJSONObject("data")
                            if (jo.getString("success") == "1") {
                                alert(getString(R.string.a01_08z91activitysave))
                                getMyinfo()
                            } else {
                                alert(jo.getString("message").htmlToString())
                            }
                        }

                        @Throws(java.lang.Exception::class)
                        override fun fail(errmsg: String) {
                            dismiss()
                            errorCheckLogout(result, errorMsg)
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    }
    override fun finish() {
        isFinish = true
        super.finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_08_z91_auto_attendance_student)
        try{
            initBottom(getString(R.string.a01_04activityStudentAutoAttendance))


            a01_08Z91SubjectSelect.setOnTouchListener { view, event ->
                try {
                    val id: Int = view.getId()
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        if (id == R.id.a01_08SubjectSelect) {
                            val draw: Drawable =
                                applicationContext.getResources().getDrawable(R.drawable.arrow_view_down)
                            draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)
                            a01_08Z91SubjectSelectArrow.setImageDrawable(draw)
                            a01_08Z91SubjectSelect.setTextColor(A901Color.getIhwaColor())

                        }
                    } else if (event.action == MotionEvent.ACTION_UP||event.action == MotionEvent.ACTION_MOVE) {
                        if (id == R.id.a01_08SubjectSelect) {
                            val draw: Drawable =
                                applicationContext.getResources().getDrawable(R.drawable.arrow_view_down)
                            draw.colorFilter = null
                            a01_08Z91SubjectSelectArrow.setImageDrawable(draw)
                            a01_08Z91SubjectSelect.setTextColor(A901Color.WHITE)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Env.error("", e)
                }
                false
            }
            a01_08Z91SubjectSelect.setOnClickListener(onClick)

            a01_08Z91ReadyStart.setBackgroundColor(A901Color.getIhwaColorDown())
            a01_08Z91ReadyStart.setOnTouchListener { view, event ->

                if (event.action == MotionEvent.ACTION_DOWN) {
                    a01_08Z91ReadyStart.setBackgroundColor(A901Color.getIhwaColorDown())
                }else if (event.action == MotionEvent.ACTION_UP||event.action == MotionEvent.ACTION_MOVE) {
                    a01_08Z91ReadyStart.setBackgroundColor(A901Color.getIhwaColor())
                }

                false
            }
            a01_08Z91ReadyStart.setOnClickListener(onClick)


            a01_08Z91Message.setVisibility(View.GONE)
            a01_08Z91ReadyLayout.setVisibility(View.GONE)

            if (getEnvInt(
                    StaticClass.KEY_LANGUAGE,
                    A01_09Z09Language.ID_row1
                ) === A01_09Z09Language.ID_row2
            ) {
                a01_08Z91Name.setText(
                    getEnv(
                        StaticClass.KEY_LASTNAME,
                        ""
                    )
                )
            } else {
                a01_08Z91Name.setText(
                    getEnv(
                        StaticClass.KEY_FIRSTNAME,
                        ""
                    )
                )
            }

            a01_08Z91IDNumber.setText(
                getEnv(
                    StaticClass.KEY_IDNUMBER,
                    ""
                )
            )

            Picasso.with(applicationContext)
                .load(getEnv(StaticClass.KEY_PICURL, ""))
                .transform(RoundTransform())
                .error(android.R.color.transparent)
                .into(a01_08Z91Pic)
            val cursor = cmdb.select(
                A903LMSTask.API_CURRENTSUBJECT,
                A903LMSTask.API_CURRENTSUBJECT,
                null,
                CMDB.TABLE01_03SubjectList
            )
            val size = cursor.count
            subjectIDList = arrayOfNulls(size)
            subjectNameList = arrayOfNulls(size)
            val index = cursor.getColumnIndex(CMDB.COLUMNjson)
            var jo: JSONObject? = null
            var i = 0
            while (cursor.moveToNext()) {
                jo = JSONObject(cursor.getString(index))
                subjectIDList[i] = jo!!.getString("id")
                if (getEnvInt(
                        StaticClass.KEY_LANGUAGE,
                        A01_09Z09Language.ID_row1
                    ) === A01_09Z09Language.ID_row2
                ) {
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
            getMyinfo()

            endTime = System.currentTimeMillis() + 50
            TimeCheck()


        }catch (e:Exception){
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun showList() {
        a01_08Z91SubjectSelect.setText(subjectName)
        val cursor = cmdb.select(
            A903LMSTask.API_MYATTENDANCE,
            "status_count",
            subjectID,
            CMDB.TABLE01_08AttendanceStudent
        )
        val index = cursor.getColumnIndex(CMDB.COLUMNjson)
        if (cursor.moveToNext()) {
            val countJSON = JSONObject(cursor.getString(index))
            attendanceCount = countJSON.getString("1")
            absenceCount = countJSON.getString("2")
            perceptionCount = countJSON.getString("3")
            etcCount = countJSON.getString("4")
            a01_08Z91Count1Count.setText(attendanceCount)
            a01_08Z91Count2Count.setText(absenceCount)
            a01_08Z91Count3Count.setText(perceptionCount)
            a01_08Z91Count4Count.setText(etcCount)
        }
        refreshAutoAttendance()
    }


    @Throws(java.lang.Exception::class)
    private fun refreshAutoAttendance() {
        cmdb = CMDB.open(applicationContext)
        var cursor =
            cmdb.select(A903LMSTask.API_SUBJECTWEEKLY, subjectID, null, CMDB.TABLE01_weekly)
        val index = cursor.getColumnIndex(CMDB.COLUMNjson)
        val weekJSON = JSONObject()
        while (cursor.moveToNext()) {
            weekJSON.put(JSONObject(cursor.getString(index)).getString("id"), true)
        }
        cursor = cmdb.select(
            A903LMSTask.API_ATTENDANCEAUTOCHECK,
            "autoinfo",
            null,
            CMDB.TABLE01_07Z91AutoAttendanceTeacher
        )
        val indexJson = cursor.getColumnIndex(CMDB.COLUMNjson)
        var isCheck = false
        while (cursor.moveToNext()) {
            val jo = JSONObject(cursor.getString(indexJson))
            attID = jo.getString("attid")
            if (weekJSON.isNull(attID)) {
            } else {
                endTime = jo.getLong("timecreated") * 1000 + jo.getLong("duration") * 1000
                if (endTime - System.currentTimeMillis() > 0) {
                    prd()
                    val shm = StringHashMap()
                    shm["courseid"] = subjectID
                    shm["attid"] = attID
                    object : A903LMSTask(applicationContext, API_ATTENDANCEAUTOCHECKSTUDENT, shm) {
                        @Throws(java.lang.Exception::class)
                        override fun success() {
                            val jo_result = JSONObject(result)
                            if (jo_result.getInt("data") == 1) {
                                a01_08Z91Message.setText(R.string.a01_08z91activitycomplete)
                                a01_08Z91ReadyLayout.setVisibility(View.GONE)
                                a01_08Z91Message.setVisibility(View.VISIBLE)
                            } else {
                                a01_08Z91ReadyLayout.setVisibility(View.VISIBLE)
                                a01_08Z91Message.setVisibility(View.GONE)
                            }
                            dismiss()
                        }

                        @Throws(java.lang.Exception::class)
                        override fun fail(errmsg: String) {
                            dismiss()
                            errorCheckLogout(result, errorMsg)
                        }
                    }
                    isCheck = true
                }
            }
        }
        if (isCheck) {
        } else {
            a01_08Z91Message.setText(R.string.a01_08z91activitynotauto)
            a01_08Z91ReadyLayout.setVisibility(View.GONE)
            a01_08Z91Message.setVisibility(View.VISIBLE)
        }
    } // showList();


    @Throws(java.lang.Exception::class)
    private fun getMyinfo() {
        prd()
        val shm = StringHashMap()
        shm["courseid"] = subjectID
        object : A903LMSTask(applicationContext, API_MYAUTOATTENDANCE, shm) {
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
            if (a01_08Z91ReadyLayout.getVisibility() === View.VISIBLE && endTime - System.currentTimeMillis() <= 0) {
                alert(getString(R.string.a01_07z91activityautoendmessage))
                refreshAutoAttendance()
            }
            timeCheck = object : CMTimer(1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    try {
                        var sec =
                            ((endTime - System.currentTimeMillis()) / 1000).toInt()
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
                            a01_08Z91AutoCheckTime.setText(
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
}