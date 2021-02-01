package kr.coursemos.yonsei

import android.app.AlertDialog
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.text.Html
import android.view.*
import android.view.View.OnTouchListener
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_07_attendance_teacher.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class A01_07AttendanceTeacher : A00_00Activity() {
    private lateinit var SubjectID: String
    private lateinit var weeklyList: Array<String?>
    private lateinit var currentWeekly: String
    private lateinit var periodTextList: Array<String?>
    private lateinit var statTextList: Array<String>
    private lateinit var periodSHM: StringHashMap
    private lateinit var periodTempView: TextView
    private lateinit var weeklyJSONList: ArrayList<ArrayList<String>>
    private var page = 1
    private var beforeKeyword: String?=null
    private var beforeSize = 0
    private var isSending = false
    private lateinit var weeklyArray: ArrayList<String>
    private var tab1Count = 0
    private var tab2Count = 0
    private var tab3Count = 0
    private var tab4Count = 0
    private var currentTab = 0

    var isEnd = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_07_attendance_teacher)
        try{
            initBottom(getString(R.string.a01_07layoutTitle))
            adapter=thisAdapter()
            a01_07WeeklySelect.setOnTouchListener(onTouch)
            a01_07WeeklySelect.setOnClickListener(onClick)
            a01_07WeeklySelect.setBackgroundColor(A901Color.getIhwaColor())
            a01_07Period.setOnTouchListener(onTouch)
            a01_07Period.setOnClickListener(onClick)
            a01_07Period.setBackgroundColor(A901Color.getDarkHeaderBackground())
            a01_07StateTabLayout2.setOnTouchListener(onTouch)
            a01_07StateTabLayout2.setOnClickListener(onClick)
            a01_07StateTabLayout3.setOnTouchListener(onTouch)
            a01_07StateTabLayout3.setOnClickListener(onClick)
            a01_07StateTabLayout4.setOnTouchListener(onTouch)
            a01_07StateTabLayout4.setOnClickListener(onClick)
            a01_07StudentList.adapter=adapter
            a01_07StudentList.setOnScrollListener(object : AbsListView.OnScrollListener {
                var isNext = false
                override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                    try {
                        if (isEnd) {
                        } else {
                            if ((scrollState == 2 || scrollState == 0) && isNext) {
                                onClick.onClick(a01_07StudentList)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }

                override fun onScroll(
                    view: AbsListView,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                    isNext = firstVisibleItem + visibleItemCount == totalItemCount
                }
            })


            currentTab = R.id.a01_07StateTabLayout1

            statTextList = arrayOf(
                getString(R.string.a01_07activityStat1),
                getString(R.string.a01_07activityStat2),
                getString(R.string.a01_07activityStat3),
                getString(R.string.a01_07activityStat4)
            )
            val cursor = cmdb.select(
                A903LMSTask.API_SUBJECTWEEKLY,
                getEnv(StaticClass.KEY_SUBJECTID, ""),
                null,
                CMDB.TABLE01_weekly
            )
            weeklyArray = ArrayList<String>()
            var weeklyJSONArray: ArrayList<String> = ArrayList()
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

            sdfFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            SubjectID = getEnv(StaticClass.KEY_SUBJECTID, "")
            clear(true)
            val dateFormat = SimpleDateFormat("yyyyMMdd")
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
    private fun showList() {
        val startIndex = selectDB(
            SubjectID,
            currentWeekly,
            CMDB.TABLE01_07AttendanceTeacher,
            adapter.idList,
            adapter.jsonList,
            adapter.detailList,
            ""
        )
        deleteList(
            startIndex,
            adapter.idList,
            adapter.jsonList,
            adapter.detailList
        )
        adapter.notifyDataSetChanged()
    } // showList();


    @Throws(java.lang.Exception::class)
    private fun clear(isDBClear: Boolean) {
        beforeSize = adapter.idList.size
        if (isDBClear) {
            adapter.idList.clear()
            adapter.jsonList.clear()
            adapter.detailList.clear()
            cmdb = CMDB.open(applicationContext)
            cmdb.DBClear(CMDB.TABLE01_07AttendanceTeacher)
            beforeSize = 0
        }
    } //clear


    @Throws(java.lang.Exception::class)
    protected fun selectDB(
        subject: String,
        week: String,
        table: String,
        idList: ArrayList<String>,
        jsonList: ArrayList<String>,
        detailList: ArrayList<Boolean>,
        name: String
    ): Int {
        cmdb = CMDB.open(applicationContext)
        var cursor: Cursor? = null
        var jo: JSONObject? = null
        cursor = cmdb.select(subject, week, null, table, " _id ASC ")
        val index = cursor.getColumnIndex(CMDB.COLUMNjson)
        var firstname: String? = null
        var startIndex = 0
        tab1Count = 0
        tab2Count = 0
        tab3Count = 0
        tab4Count = 0
        var isAdd = false
        while (cursor.moveToNext()) {
            jo = JSONObject(cursor.getString(index))
            firstname = jo.getString("firstname")
            if (firstname.indexOf(name!!) >= 0) {
                val userPeriodList = jo.getJSONArray("periods")
                val size = userPeriodList.length()
                isAdd = false
                for (j in 0 until size) {
                    val periodJSON = userPeriodList.getJSONObject(j)
                    if (adapter.currentPeriod.equals(periodJSON.getString("attid"))) {
                        var stat = 0
                        if (periodJSON.isNull("status")) {
                        } else {
                            stat = periodJSON.getInt("status")
                        }
                        tab1Count++
                        if (stat == 1) {
                            if (currentTab == R.id.a01_07StateTabLayout2) {
                                isAdd = true
                            }
                            tab2Count++
                        } else if (stat == 2) {
                            if (currentTab == R.id.a01_07StateTabLayout4) {
                                isAdd = true
                            }
                            tab4Count++
                        } else if (stat == 3) {
                            if (currentTab == R.id.a01_07StateTabLayout3) {
                                isAdd = true
                            }
                            tab3Count++
                        }
                        if (currentTab == R.id.a01_07StateTabLayout1) {
                            isAdd = true
                        }
                        if (isAdd) {
                            if (idList.size > startIndex) {
                                idList[startIndex] = jo.getString("id")
                                jsonList[startIndex] = jo.toString()
                            } else {
                                idList.add(jo.getString("id"))
                                jsonList.add(jo.toString())
                                detailList.add(true)
                            }
                            startIndex++
                        }
                        break
                    }
                }
            }
        }
        a01_07StateTabBottom1.setText(Integer.toString(tab1Count))
        a01_07StateTabBottom2.setText(Integer.toString(tab2Count))
        a01_07StateTabBottom3.setText(Integer.toString(tab3Count))
        a01_07StateTabBottom4.setText(Integer.toString(tab4Count))
        return startIndex
    } // selectDB

    var onClick: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(view: View) {
            try {
                closeKeyboard()
                val id = view.id
                if (id == R.id.a01_07WeeklySelect) {
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
                } else if (id == R.id.a01_07Period) {
                    periodSHM = StringHashMap()
                    periodSHM.put("courseid", SubjectID)
                    periodSHM.put("userid", "0")
                    periodCheck()
                } else if (id == R.id.a01_07status) {
                    periodSHM = StringHashMap()
                    periodSHM.put("courseid", SubjectID)
                    periodSHM.put("attid", adapter.currentPeriod)
                    periodSHM.put("userid", view.tag as String)
                    periodTempView = view as TextView
                    AlertDialog.Builder(me)
                        .setItems(
                            statTextList,
                            DialogInterface.OnClickListener { dialog, which ->
                                try {
                                    PersonalCheck(which)
                                } catch (e: java.lang.Exception) {
                                    Env.error("", e)
                                }
                            }).show()
                } else if (id == R.id.a01_07StudentList) {
                    selectStudent(beforeKeyword)
                } else if (id == R.id.a01_07StateTabLayout1 || id == R.id.a01_07StateTabLayout2 || id == R.id.a01_07StateTabLayout3 || id == R.id.a01_07StateTabLayout4) {
                    currentTab = id
                    if (id == R.id.a01_07StateTabLayout1) {
                        a01_07StateTabLayout1.setBackgroundResource(R.drawable.item_autoattendance_timebox)
                        a01_07StateTabLayout1.setOnTouchListener(null)
                        a01_07StateTabLayout1.setOnClickListener(null)
                    } else {
                        a01_07StateTabLayout1.setBackgroundResource(R.drawable.item_autoattendance_timebox_select)
                        a01_07StateTabLayout1.setOnTouchListener(onTouch)
                        a01_07StateTabLayout1.setOnClickListener(this)
                    }
                    if (id == R.id.a01_07StateTabLayout2) {
                        a01_07StateTabLayout2.setBackgroundResource(R.drawable.item_autoattendance_timebox)
                        a01_07StateTabLayout2.setOnTouchListener(null)
                        a01_07StateTabLayout2.setOnClickListener(null)
                    } else {
                        a01_07StateTabLayout2.setBackgroundResource(R.drawable.item_autoattendance_timebox_select)
                        a01_07StateTabLayout2.setOnTouchListener(onTouch)
                        a01_07StateTabLayout2.setOnClickListener(this)
                    }
                    if (id == R.id.a01_07StateTabLayout3) {
                        a01_07StateTabLayout3.setBackgroundResource(R.drawable.item_autoattendance_timebox)
                        a01_07StateTabLayout3.setOnTouchListener(null)
                        a01_07StateTabLayout3.setOnClickListener(null)
                    } else {
                        a01_07StateTabLayout3.setBackgroundResource(R.drawable.item_autoattendance_timebox_select)
                        a01_07StateTabLayout3.setOnTouchListener(onTouch)
                        a01_07StateTabLayout3.setOnClickListener(this)
                    }
                    if (id == R.id.a01_07StateTabLayout4) {
                        a01_07StateTabLayout4.setBackgroundResource(R.drawable.item_autoattendance_timebox)
                        a01_07StateTabLayout4.setOnTouchListener(null)
                        a01_07StateTabLayout4.setOnClickListener(null)
                    } else {
                        a01_07StateTabLayout4.setBackgroundResource(R.drawable.item_autoattendance_timebox_select)
                        a01_07StateTabLayout4.setOnTouchListener(onTouch)
                        a01_07StateTabLayout4.setOnClickListener(this)
                    }
                    showList()
                }
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
        }
    } // OnClickListener onClick


    @Throws(java.lang.Exception::class)
    private fun selectWeekly(which: Int) {
        a01_07WeeklySelect.setText(weeklyList.get(which))
        adapter.periodJSONList = weeklyJSONList.get(which)
        val size: Int = adapter.periodJSONList.size
        periodTextList = arrayOfNulls<String>(size)
        var periodJSON: JSONObject? = null
        for (i in 0 until size) {
            periodJSON = JSONObject(adapter.periodJSONList.get(i))
            periodTextList[i] =
                periodJSON!!.getString("period") + getString(R.string.a01_07activityPeriod)
        }
        adapter.notifyDataSetChanged()
        currentWeekly = weeklyArray.get(which)
        beforeKeyword = null
        selectPeriod(0)
        selectStudent("")
    }


    @Synchronized
    @Throws(java.lang.Exception::class)
    private fun selectStudent(keyword: String?) {
        if (isSending) {
            return
        }
        isSending = true
        if (prdDialog == null) {
            prd(false)
        }
        val shm = StringHashMap()
        if (beforeKeyword == null) {
            isEnd = false
            page = 1
            clear(true)
        } else {
            if (beforeKeyword == keyword) {
                if (isEnd) {
                    return
                } else {
                    page++
                    clear(false)
                }
            } else {
                isEnd = false
                page = 1
                clear(true)
            }
        }
        beforeKeyword = keyword
        shm["courseid"] = SubjectID
        shm["week"] = currentWeekly
        //		shm.put("period", "all");
        shm["page"] = Integer.toString(page)
        shm["ls"] = "999"
        object : A903LMSTask(applicationContext, API_STUDENTATTENDANCE, shm) {
            @Throws(java.lang.Exception::class)
            override fun success() {
                isSending = false
                if (pShm.getString("COUNT") == "0") {
                    isEnd = true
                }
                showList()

//				if(layout.isEnd){
//				}else{
//					if(beforeSize==layout.a01_07AttendanceAdapter.idList.size()){
//						selectStudent(beforeKeyword);
//						return ;
//					}
//				}
                dismiss()
            }

            @Throws(java.lang.Exception::class)
            override fun fail(errmsg: String) {
                isSending = false
                dismiss()
                errorCheckLogout(result, errorMsg)
            }
        }
    } //selectFriend


    @Throws(java.lang.Exception::class)
    private fun PersonalCheck(which: Int) {
        prd()
        periodSHM.put("status", Integer.toString(which + 1))
        periodSHM.put("week", currentWeekly)
        periodSHM.put("page", "1")
        periodSHM.put("ls", "999")
        periodTempView.setText(statTextList.get(which))
        if (which == 0) {
            periodTempView.setTextColor(A901Color.getIhwaColor())
        } else if (which == 1) {
            periodTempView.setTextColor(A901Color.getAttendanceAbsence())
        } else if (which == 2) {
            periodTempView.setTextColor(A901Color.getAttendanceLateness())
        } else if (which == 3) {
            periodTempView.setTextColor(A901Color.getAttendanceEtc())
        }
        object : A903LMSTask(applicationContext, API_ATTENDANCESETTING, periodSHM) {
            @Throws(java.lang.Exception::class)
            override fun success() {
                showList()
                dismiss()
            }

            @Throws(java.lang.Exception::class)
            override fun fail(errmsg: String) {
                dismiss()
                errorCheckLogout(result, errorMsg)
            }
        }
    }

    @Throws(java.lang.Exception::class)
    private fun periodCheck() {
        AlertDialog.Builder(me)
            .setItems(periodTextList,
                DialogInterface.OnClickListener { dialog, which ->
                    try {
                        selectPeriod(which)
                        showList()
                        //					periodSHM.put("status", Integer.toString(which+1));
                        //					AllCheck2();
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }).show()
    }

    @Throws(java.lang.Exception::class)
    private fun selectPeriod(which: Int) {
        a01_07Period.setText(periodTextList.get(which))
        adapter.currentPeriod =
            JSONObject(adapter.periodJSONList.get(which)).getString("id")
    }

    @Throws(java.lang.Exception::class)
    private fun autoSelect() {
        cmdb = CMDB.open(applicationContext)
        val autoInfo = JSONArray()
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
        val size = autoInfo.length()
        var endTime: Long = 0
        var tempWeekly: String? = null
        for (i in 0 until size) {
            val jo = autoInfo.getJSONObject(i)
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
                weeklySize = adapter.periodJSONList.size
                for (j in 0 until weeklySize) {
                    if (tempWeekly == JSONObject(
                            adapter.periodJSONList.get(
                                j
                            )
                        ).getString("id")
                    ) {
                        selectPeriod(j)
                        showList()
                        break
                    }
                }
                break
            }
        }
    }


    @Throws(java.lang.Exception::class)
    private fun AllCheck2() {
        AlertDialog.Builder(me)
            .setItems(periodTextList,
                DialogInterface.OnClickListener { dialog, which ->
                    try {
                        //					prd();
                        //					JSONObject periodJSON=new JSONObject(layout.a01_07AttendanceAdapter.periodJSONList.get(which));
                        //					periodSHM.put("attid", periodJSON.getString("id"));
                        //					new A903LMSTask(context,A903LMSTask.API_ATTENDANCESETTING,periodSHM) {
                        //						@Override
                        //						public void success() throws Exception {
                        //							beforeKeyword=null;
                        //							selectStudent("");
                        //						}
                        //						@Override
                        //						public void fail(String errmsg) throws Exception {
                        //							dismiss();
                        //							errorCheckLogout(result,errorMsg);
                        //						}
                        //					};
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }).show()
    }



    override fun success(what: String?, jyc: CMCommunication?) {
        try {
            if (jyc == null) {
                val id = what?.toInt()
                if (id == ID_alertOK) {
                    if (isAlertAction) {
                        onKeyDown(KeyEvent.KEYCODE_BACK, null)
                    }
                } else if (id == ID_confirmOK) {
                } else if (id == ID_confirmCancel) {
                } else {
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }
    var onTouch = OnTouchListener { view, event ->
        try {
            val id = view.id
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (id == R.id.a01_07WeeklySelect) {
                    a01_07WeeklySelect.setBackgroundColor(A901Color.getIhwaColorDown())
                } else if (id == R.id.a01_07Period) {
                    a01_07Period.setBackgroundColor(A901Color.getDarkHeaderBackgroundDown())
                } else if (id == R.id.a01_07StateTabLayout1 || id == R.id.a01_07StateTabLayout2 || id == R.id.a01_07StateTabLayout3 || id == R.id.a01_07StateTabLayout4) {
                    view.setBackgroundResource(R.drawable.item_autoattendance_timebox_down)
                }
            } else if (event.action == MotionEvent.ACTION_UP||event.action == MotionEvent.ACTION_MOVE) {
                if (id == R.id.a01_07WeeklySelect) {
                    a01_07WeeklySelect.setBackgroundColor(A901Color.getIhwaColor())
                } else if (id == R.id.a01_07Period) {
                    a01_07Period.setBackgroundColor(A901Color.getDarkHeaderBackground())
                } else if (id == R.id.a01_07StateTabLayout1 || id == R.id.a01_07StateTabLayout2 || id == R.id.a01_07StateTabLayout3 || id == R.id.a01_07StateTabLayout4) {
                    view.setBackgroundResource(R.drawable.item_autoattendance_timebox_select)
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch


    lateinit var adapter:thisAdapter
    inner class thisAdapter() : BaseAdapter(){
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()
        public var detailList: ArrayList<Boolean> = ArrayList()
        public var periodJSONList: ArrayList<String> = ArrayList()
        public lateinit var currentPeriod:String
        private var lang: Int = A01_09Z09Language.ID_row1

        init {
            lang = getEnvInt(
                StaticClass.KEY_LANGUAGE,
                A01_09Z09Language.ID_row1
            )
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout:View
            try{
                layout=LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_07row,null,false)
                val a01_07status=layout.findViewById<TextView>(R.id.a01_07status)
                val a01_07pic=layout.findViewById<ImageView>(R.id.a01_07pic)
                val a01_07maintext=layout.findViewById<TextView>(R.id.a01_07maintext)
                val a01_07subtext=layout.findViewById<TextView>(R.id.a01_07subtext)
                a01_07status.setOnTouchListener(onTouch)
                a01_07status.setOnClickListener(onClick)
                layout.setOnTouchListener { view, event ->

                    try {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            view.setBackgroundResource(R.drawable.bottomlineltgray)
                        } else if (event.getAction() == MotionEvent.ACTION_UP|| event.action == MotionEvent.ACTION_MOVE) {
                            view.setBackgroundResource(R.drawable.bottomlinewhite)
                        }
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                    false
                }
                layout.setOnClickListener(onClick)



                val jo = JSONObject(jsonList[position])
                if (lang == A01_09Z09Language.ID_row2) {
                    a01_07maintext.setText(jo.getString("lastname"))
                } else {
                    a01_07maintext.setText(jo.getString("firstname"))
                }
                a01_07subtext.setText(
                    Html.fromHtml(
                        jo.getString(
                            "idnumber"
                        )
                    )
                )


                var periodJSON: JSONObject? = null
                var userPeriodList: JSONArray? = null

                userPeriodList = jo.getJSONArray("periods")
                val size = userPeriodList.length()

                a01_07status.setTextColor(A901Color.parseColor("#00ffffff"))
                a01_07status.setTag(jo.getString("id"))
                for (j in 0 until size) {
                    periodJSON = userPeriodList.getJSONObject(j)
                    if (currentPeriod == periodJSON.getString("attid")) {
                        if (periodJSON.isNull("status")) {
                        } else {
                            val stat = periodJSON.getInt("status")
                            if (stat == 1) {
                                a01_07status.setText(getString(R.string.a01_08activityStat1))
                                a01_07status.setTextColor(A901Color.getIhwaColor())
                            } else if (stat == 2) {
                                a01_07status.setText(getString(R.string.a01_08activityStat2))
                                a01_07status.setTextColor(A901Color.getAttendanceAbsence())
                            } else if (stat == 3) {
                                a01_07status.setText(getString(R.string.a01_08activityStat3))
                                a01_07status.setTextColor(A901Color.getAttendanceLateness())
                            } else if (stat == 4) {
                                a01_07status.setText(getString(R.string.a01_08activityStat4))
                                a01_07status.setTextColor(A901Color.getAttendanceEtc())
                            }
                        }
                        break
                    }
                }

                Picasso.with(applicationContext)
                    .load(jo.getString("profileimageurl"))
                    .transform(RoundTransform())
                    .error(android.R.color.transparent)
                    .into(a01_07pic)



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
            return idList.size
        }

    }
}