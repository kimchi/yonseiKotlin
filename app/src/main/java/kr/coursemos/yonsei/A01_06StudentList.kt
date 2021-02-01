package kr.coursemos.yonsei

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_06_student_list.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvDebug
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.util.*

class A01_06StudentList : A00_00Activity() {

    private var SubjectID: String? = null
    private var page = 1
    private  var beforeKeyword:String?=null
    private var beforeSize = 0
    private var isSending = false

    var isEnd = false
    var onClick: View.OnClickListener = View.OnClickListener {
        try {
            val id: Int = it.getId()
            if (id == R.id.a01_06StudentList) {
                selectStudent(beforeKeyword)
            }
        } catch(e:java.lang.Exception){
            EnvError(e)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_06_student_list)
        try{
            adapter=thisAdapter()
            initBottom(getString(R.string.a01_06layoutTitle))

            a01_06StudentList.adapter=adapter
            a01_06StudentList.setOnScrollChangeListener ( object : AbsListView.OnScrollListener,
                View.OnScrollChangeListener {

                var isNext = false
                override fun onScroll(
                    view: AbsListView?,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                    isNext = if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        true
                    } else {
                        false
                    }
                }

                override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                    try {
                        if (isEnd) {
                        } else {
                            if ((scrollState == 2 || scrollState == 0) && isNext) {
                                onClick.onClick(a01_06StudentList)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }

                override fun onScrollChange(
                    v: View?,
                    scrollX: Int,
                    scrollY: Int,
                    oldScrollX: Int,
                    oldScrollY: Int
                ) {
                }

            })
            SubjectID = getEnv(StaticClass.KEY_SUBJECTID, "")
            showList("")
            if (adapter.idList.size === 0) {
                prd()
            }
            selectStudent("")

        }catch (e:Exception){
            EnvError(e)
        }
    }

    @Synchronized
    @Throws(java.lang.Exception::class)
    private fun selectStudent(keyword: String?) {
        if (isSending) {
            return
        }
        isSending = true
        val shm = StringHashMap()
        if (beforeKeyword == null) {
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
        shm["page"] = Integer.toString(page)
        shm["ls"] = "20"
        object : A903LMSTask(applicationContext, API_STUDENTLIST, shm) {
            @Throws(java.lang.Exception::class)
            override fun success() {
                isSending = false
                if (pShm.getString("COUNT") == "0") {
                    isEnd = true
                }
                showList(beforeKeyword!!)
                if (isEnd) {
                } else {
                    if (beforeSize == adapter.idList.size || adapter.idList.size < 6) {
                        selectStudent(beforeKeyword!!)
                        return
                    }
                }
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
    private fun showList(name: String) {
        val startIndex = selectDB(
            SubjectID!!,
            CMDB.TABLE01_06StudentList,
            adapter.idList,
            adapter.jsonList,
            adapter.detailList,
            name
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
            cmdb.DBClear(CMDB.TABLE01_06StudentList)
            beforeSize = 0
        }
    } //clear


    @Throws(java.lang.Exception::class)
    protected fun selectDB(
        type: String,
        table: String,
        idList: ArrayList<String>,
        jsonList: ArrayList<String>,
        detailList: ArrayList<Boolean>,
        name: String
    ): Int {
        cmdb = CMDB.open(applicationContext)
        var cursor: Cursor? = null
        var jo: JSONObject? = null
        cursor = cmdb.select(A903LMSTask.API_STUDENTLIST, "stu", null, table)
        val index = cursor.getColumnIndex(CMDB.COLUMNjson)
        var fullName: String? = null
        var startIndex = 0
        while (cursor.moveToNext()) {
            jo = JSONObject(cursor.getString(index))
            fullName = jo.getString("fullname")
            if (fullName.indexOf(name!!) >= 0) {
                if (idList.size > startIndex) {
                    idList[startIndex] = jo.getString("id")
                    jsonList[startIndex] = jo.toString()
                } else {
                    idList.add(jo.getString("id"))
                    jsonList.add(jo.toString())
                    detailList.add(false)
                }
                startIndex++
            }
        }
        return startIndex
    } // selectDB



    lateinit var adapter:thisAdapter
    inner class thisAdapter() : BaseAdapter(){
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()
        public var detailList: ArrayList<Boolean> = ArrayList()
        private var mySeq: String? = null
        private var lang: Int = A01_09Z09Language.ID_row1
        private val onClick: View.OnClickListener? = null
        var isProfessor = false
        init {
            EnvDebug("1111111")
            mySeq = getEnv(StaticClass.KEY_USERSEQ, "0")
            lang = getEnvInt(
                StaticClass.KEY_LANGUAGE,
                A01_09Z09Language.ID_row1
            )

            val professor: String =
                getEnv(StaticClass.KEY_MYPOSITION, "")
            if (professor == "3" || professor == "9" || professor == "1") {
                isProfessor = true
            }

        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout: View
            try{
                val id = idList[position]
                if (id == StaticClass.HEADER) {
                    layout =  LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_06rowheader,null,false)
                    layout.findViewById<TextView>(R.id.a01_06maintext).setText(jsonList.get(position))
                } else {
                    layout = LayoutInflater.from(applicationContext)
                        .inflate(R.layout.list_a01_06row, null, false)


                    val jo = JSONObject(jsonList[position])
                    if (lang == A01_09Z09Language.ID_row2) {
                        layout.findViewById<TextView>(R.id.a01_06maintext)
                            .setText(jo.getString("lastname"))
                    } else {
                        layout.findViewById<TextView>(R.id.a01_06maintext)
                            .setText(jo.getString("fullname"))
                    }
                    if (isProfessor) {
                        layout.findViewById<TextView>(R.id.a01_06subtext)
                            .setText(jo.getString("idnumber"))
                    } else {
                        layout.findViewById<TextView>(R.id.a01_06subtext).setText("")
                    }
                    Picasso.with(applicationContext)
                        .load(jo.getString("profileimageurl"))
                        .transform(RoundTransform())
                        .error(android.R.color.transparent)
                        .into(layout.findViewById<ImageView>(R.id.a01_06pic))
                    if (mySeq == idList[position]) {
                        layout.setOnTouchListener(null)
                        layout.setOnClickListener(null)
                    } else {
                        layout.setOnTouchListener { view, event ->

                            try {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    view.setBackgroundResource(R.drawable.bottomlineltgray)
                                } else if (event.getAction() == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
                                    view.setBackgroundResource(R.drawable.bottomlinewhite)
                                }
                            } catch (e: java.lang.Exception) {
                                Env.error("", e)
                            }
                            false
                        }
                        layout.setOnClickListener {
                            setEnv(
                                StaticClass.KEY_CHATTINGINFO,
                                adapter.jsonList.get(position)
                            )
                            startActivity(A01_11Chatting::class.java, 0)
                        }
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
            return idList.size
        }

    }
}