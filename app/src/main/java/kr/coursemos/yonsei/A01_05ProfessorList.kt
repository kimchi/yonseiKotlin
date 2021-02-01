package kr.coursemos.yonsei

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
import kotlinx.android.synthetic.main.activity_a01_05_professor_list.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvDebug
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.util.*

class A01_05ProfessorList : A00_00Activity() {

    private var SubjectID: String? = null
    private var page = 1
    private  var beforeKeyword:String?=null
    private var beforeSize = 0
    private var isSending = false

    var isEnd = false
    var onClick: View.OnClickListener = View.OnClickListener {
        try {
            val id: Int = it.getId()
            if (id == R.id.a01_05ProfessorList) {
                selectStudent(beforeKeyword)
            }
        } catch(e:java.lang.Exception){
            EnvError(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_05_professor_list)
        try{
            adapter=thisAdapter()
            initBottom(getString(R.string.a01_05layoutTitle))

            a01_05ProfessorList.adapter=adapter
            a01_05ProfessorList.setOnScrollChangeListener ( object : AbsListView.OnScrollListener,
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
                                onClick.onClick(a01_05ProfessorList)
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
            showList()
            if (adapter.idList.size === 0) {
                prd()
            }
            selectStudent("")

        }catch (e:Exception){
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun showList() {
        var pro = selectDB(
            0,
            A903LMSTask.API_PROFESSORLIST,
            "pro",
            CMDB.TABLE01_05ProfessorList,
            adapter.idList,
            adapter.jsonList
        )
        if (pro > 0) {
            adapter.idList.add(0, StaticClass.HEADER)
            adapter.jsonList.add(
                0,
                getString(R.string.a01_05activityProfessor)
            )
            pro++
        }
        var ass = selectDB(
            pro,
            A903LMSTask.API_PROFESSORLIST,
            "assi",
            CMDB.TABLE01_05ProfessorList,
            adapter.idList,
            adapter.jsonList
        )
        if (ass > pro) {
            adapter.idList.add(pro, StaticClass.HEADER)
            adapter.jsonList.add(
                pro,
                getString(R.string.a01_05activityAssistant)
            )
            ass++
        }
        deleteList(
            ass,
            adapter.idList,
            adapter.jsonList,
            null
        )
        adapter.notifyDataSetChanged()
    } // showList();

    @Throws(java.lang.Exception::class)
    private fun clear(isDBClear: Boolean) {
        beforeSize = adapter.idList.size
        if (isDBClear) {
            adapter.idList.clear()
            adapter.jsonList.clear()
            cmdb = CMDB.open(applicationContext)
            cmdb.DBClear(CMDB.TABLE01_05ProfessorList)
            beforeSize = 0
        }
    } //clear

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
        shm["ls"] = "10"
        shm["keyfield"] = ""
        shm["keyword"] = ""
        shm["roleid"] = "0"
        shm["groupid"] = "0"
        object : A903LMSTask(applicationContext, API_STUDENTLIST, shm) {
//        object : A903LMSTask(applicationContext, API_PROFESSORLIST, shm) {
            @Throws(java.lang.Exception::class)
            override fun success() {
                isSending = false
                if (pShm.getString("COUNT") == "0") {
                    isEnd = true
                }
                showList()
                if (isEnd) {
                } else {
                    if (beforeSize == adapter.idList.size || adapter.idList.size < 6) {
                        selectStudent(beforeKeyword)
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


    lateinit var adapter:thisAdapter
    inner class thisAdapter() : BaseAdapter(){
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()

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
                    layout =  LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_05rowheader,null,false)
                    layout.findViewById<TextView>(R.id.a01_05maintext).setText(jsonList.get(position))
                } else {
                    layout =  LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_05row,null,false)


                    val jo = JSONObject(jsonList[position])
                    if (lang == A01_09Z09Language.ID_row2) {
                        layout.findViewById<TextView>(R.id.a01_05maintext).setText(jo.getString("lastname"))
                    } else {
                        layout.findViewById<TextView>(R.id.a01_05maintext).setText(jo.getString("fullname"))
                    }
                    if (isProfessor) {
                        layout.findViewById<TextView>(R.id.a01_05subtext).setText(jo.getString("idnumber"))
                    }else{
                        layout.findViewById<TextView>(R.id.a01_05subtext).setText("")
                    }
                    Picasso.with(applicationContext)
                        .load(jo.getString("profileimageurl"))
                        .transform(RoundTransform())
                        .error(android.R.color.transparent)
                        .into(layout.findViewById<ImageView>(R.id.a01_05pic))
                    if (mySeq == idList[position]) {
                        layout.setOnTouchListener(null)
                        layout.setOnClickListener(null)
                    } else {
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
                        layout.setOnClickListener {
                            setEnv(
                                StaticClass.KEY_CHATTINGINFO,
                                adapter.jsonList.get(position)
                            )
                            startActivity(A01_11Chatting::class.java, 0) }
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