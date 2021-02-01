package kr.coursemos.yonsei

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_20_alarm.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a51custometc.A905FileDownLoad
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import kr.coursemos.myapplication.a99util.htmlToString
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class A01_20Alarm : A00_00Activity() {

    var listPosition = 0
    var positionPaddingY = 0
    var isEnd = false
    private var page = 1
    private var beforeKeyword: String? = null
    private var adobeConnectID: String? = null

    private var isFirst = true

    private var isSending = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_20_alarm)
        try{
            removeEnv(StaticClass.KEY_PUSHSCREEN)
            removeEnv(StaticClass.KEY_NOTICOUNT)
            StaticClass.history.clear()
            StaticClass.history.add(this.javaClass)
            putEnvInt(
                StaticClass.KEY_SELECTMAINTAB,
                StaticClass.ID_BOTTOMTAB3
            )
            initBottom(getString(R.string.a01_20layoutTitle))
            a01_20List.setOnScrollListener(object: AbsListView.OnScrollListener{
                var isNext = false
                override fun onScroll(
                    view: AbsListView?,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                    fun onScroll(
                        view: AbsListView,
                        firstVisibleItem: Int,
                        visibleItemCount: Int,
                        totalItemCount: Int
                    ) {
                        try {
                            isNext = firstVisibleItem + visibleItemCount == totalItemCount
                            if (view.getChildAt(0) == null) {
                                positionPaddingY = 0
                            } else {
                                val tempPaddingY = view.getChildAt(0).top
                                positionPaddingY = tempPaddingY
                            }
                            listPosition = firstVisibleItem
                        } catch (e: java.lang.Exception) {
                            Env.error("", e)
                        }
                    }
                }

                override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                    try {
                        if (isEnd) {
                        } else {
                            if ((scrollState == 2 || scrollState == 0) && isNext) {
                                request(beforeKeyword!!)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }

            })
            a01_20List.adapter=adapter

            a01_20refresh.setOnRefreshListener {
                beforeKeyword = null
                isEnd = false
                request("")
            }

            if (isBackKey) {
                showList()
                a01_20List.setSelectionFromTop(
                    getEnvInt(
                        StaticClass.KEY_NOTICACHEPOSITIONY,
                        0
                    ), getEnvInt(StaticClass.KEY_NOTICACHEPADDINGY, 0)
                )
            } else {
                showList()
                if (adapter.idList.size === 0) {
                    prd()
                }
                beforeKeyword = null
                isEnd = false
                request("")
            }

        }catch (e: Exception){
            EnvError(e)
        }
    }


    @Throws(java.lang.Exception::class)
    private fun showList() {
        cmdb = CMDB.open(applicationContext)
        val i = selectDB(
            0,
            A903LMSTask.API_NOTIFICATION,
            A903LMSTask.API_NOTIFICATION,
            CMDB.TABLE01_20Alarm,
            adapter.idList,
            adapter.jsonList
        )
        deleteList(
            i,
            adapter.idList,
            adapter.jsonList,
            null
        )
        adapter.notifyDataSetChanged()

    } // showList();


    @Throws(java.lang.Exception::class)
    private fun clear(isDBClear: Boolean) {
        adapter.idList.clear()
        adapter.jsonList.clear()
        if (isDBClear) {
            cmdb = CMDB.open(applicationContext)
            cmdb.DBClear(CMDB.TABLE01_20Alarm)
        }
    } //clear

    @Synchronized
    @Throws(java.lang.Exception::class)
    private fun request(keyword: String) {
        var api = A903LMSTask.API_NOTIFICATION
        if (isSending) {
            return
        }
        isSending = true
        if (isFirst) {
            isFirst = false
            //			layout.A01_20AlarmList.setRefreshing();
            api = A903LMSTask.API_NOTIFICATIONALLUPDATE
        } else {
        }
        val shm = StringHashMap()
        if (beforeKeyword == null) {
            page = 1
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
            }
        }
        beforeKeyword = keyword
        shm["page"] = Integer.toString(page)
        shm["ls"] = "20"
        shm["courseid"] = "0"
        object : A903LMSTask(applicationContext, api, shm) {
            @Throws(java.lang.Exception::class)
            override fun success() {
                isSending = false
                dismiss()
                if (pShm["COUNT"] == "0") {
                    isEnd = true
                }
                if (page == 1) {
                    adapter.idList.clear()
                    adapter.jsonList.clear()
                }
                showList()
                a01_20refresh.isRefreshing=false
            }

            @Throws(java.lang.Exception::class)
            override fun fail(errmsg: String) {
                isSending = false
                dismiss()
                a01_20refresh.isRefreshing=false
                errorCheckLogout(result, errorMsg)
            }
        }
    } //selectFriend

    override fun finish() {
        super.finish()
        try {
            setEnvInt(
                StaticClass.KEY_NOTICACHEPOSITIONY,
                listPosition
            )
            setEnvInt(
                StaticClass.KEY_NOTICACHEPADDINGY,
                positionPaddingY
            )
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    }

    val onClick=object: View.OnClickListener{
        override fun onClick(view: View?) {
            try{
                val id: Int = view!!.getId()
                if (id == adapter.ID_file) {
                    val url = view.getTag() as String
                    val shm = StringHashMap()
                    shm["token"] = getEnv(StaticClass.KEY_USERTOKEN, "")
                    prd()
                    val splits =
                        url.split("?").toTypedArray()[0].split("/".toRegex())
                            .toTypedArray()
                    object :
                        A905FileDownLoad(applicationContext, url, shm, splits[splits.size - 1], null) {
                        @Throws(java.lang.Exception::class)
                        override fun success(msg: String) {
                            dismiss()
                            val fileExtension =
                                fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            var filetype: String? = null
                            filetype =
                                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
                            if (filetype is String) {
                            } else {
                                filetype = "*/*"
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                val photoURI = FileProvider.getUriForFile(
                                    context,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    File(StaticClass.pathExternalStorage + fileName)
                                )
                                intent.setDataAndType(photoURI, filetype)
                            } else {
                                intent.setDataAndType(
                                    Uri.fromFile(File(StaticClass.pathExternalStorage + fileName)),
                                    filetype
                                )
                            }
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            try {
                                startActivity(intent)
                            } catch (e: java.lang.Exception) {
                                Env.error("", e)
                                alert(getString(R.string.a01_04activityFileOpenFail))
                            }
                        }

                        @Throws(java.lang.Exception::class)
                        override fun fail(errmsg: String) {
                            dismiss()
                            errorCheckLogout(result, errorMsg)
                        }
                    }
                } else if (id == adapter.ID_adobeconnect) {
                    adobeConnectID = view.getTag() as String
                    val shm = StringHashMap()
                    shm["cmid"] = adobeConnectID
                    prd()
                    object : A903LMSTask(applicationContext, API_ADOBECONNECT, shm) {
                        @Throws(java.lang.Exception::class)
                        override fun success() {
                            dismiss()
                            startActivity(A01_04Z01AdobeConnect::class.java, 0)
                        }

                        @Throws(java.lang.Exception::class)
                        override fun fail(errmsg: String) {
                            dismiss()
                            errorCheckLogout(result, errorMsg)
                        }
                    }
                } else if (id == adapter.ID_ubchat) {
                    alert(getString(R.string.a01_04activityRealTimeChatting))
                } else if (id == adapter.ID_boardList) {
                    val jo =
                        JSONObject(adapter.jsonList.get((view.getTag() as String). toInt ()))
                    setEnv(
                        StaticClass.KEY_WEBURL,
                        URLEncoder.encode(
                            jo.getString("url"),
                            StaticClass.charset
                        )
                    )
                    setEnv(
                        StaticClass.KEY_WEBVIEWNAME,
                        jo.getString("course_name")
                    )
                    startActivity(A01_13WebView::class.java, 0)
                } else if (id == adapter.ID_board) {
                    val jo =
                        JSONObject(adapter.jsonList.get((view.getTag() as String). toInt ()))
                    setEnv(
                        StaticClass.KEY_WEBURL,
                        URLEncoder.encode(
                            jo.getString("url"),
                            StaticClass.charset
                        )
                    )
                    setEnv(
                        StaticClass.KEY_WEBVIEWNAME,
                        jo.getString("course_name")
                    )
                    startActivity(A01_13WebView::class.java, 0)

                } else if (id == adapter.ID_else) {
                    val url____name =
                        (view.getTag() as String).split("____".toRegex()).toTypedArray()
                    setEnv(
                        StaticClass.KEY_WEBURL,
                        url____name[0]
                    )
                    setEnv(
                        StaticClass.KEY_WEBVIEWNAME,
                        url____name[1]
                    )
                    startActivity(A01_13WebView::class.java, 0)
                }
            }catch (e:java.lang.Exception){
                EnvError(e)
            }


        }

    }


    val adapter=thisAdapter()
    inner class thisAdapter() : BaseAdapter(){
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()
        var ID_board = View.generateViewId()
        var ID_boardList = View.generateViewId()
        var ID_ubchat = View.generateViewId()
        var ID_book = View.generateViewId()
        var ID_adobeconnect = View.generateViewId()
        var ID_file = View.generateViewId()
        var ID_else = View.generateViewId()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout:View
            try{
                layout=LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_20row,null,false)
                layout.setBackgroundColor(A901Color.WHITE)
                val a01_20pic =layout.findViewById<ImageView>(R.id.a01_20pic)
                val course_name =layout.findViewById<TextView>(R.id.a01_20top)
                val message =layout.findViewById<TextView>(R.id.a01_20middle)
                val time =layout.findViewById<TextView>(R.id.a01_20bottom)
                course_name.setTextColor(A901Color.getIhwaColor())
                message.setTextColor(A901Color.BLACK)
                time.setTextColor(A901Color.LTGRAY)

                layout.setTag(position.toString())
                val jo = JSONObject(jsonList[position])

                Picasso.with(applicationContext)
                    .load(jo.getString("icon"))
                    .transform(RoundTransform())
                    .error(android.R.color.transparent)
                    .into(a01_20pic)

                course_name.setText(jo.getString("course_name"))
                message.setText(jo.getString("message").htmlToString())


                var second =
                    System.currentTimeMillis() / 1000 - jo.getLong("in_date")
                val cal = Calendar.getInstance()
                var tempyyyy =
                    SimpleDateFormat("yyyyMM").format(cal.time).toInt()
                var tempMM = tempyyyy % 100 + 12
                tempyyyy /= 100
                cal.add(Calendar.SECOND, (-second).toInt())
                var postyyyy =
                    SimpleDateFormat("yyyyMM").format(cal.time).toInt()
                val postMM = postyyyy % 100
                postyyyy /= 100

                tempyyyy -= postyyyy
                tempMM = (tempMM - postMM) % 12

                var str: String = getString(R.string.a01_20adapteryear)
                if (tempyyyy > 0) {
                    second = tempyyyy.toLong()
                } else if (tempMM > 0) {
                    str = getString(R.string.a01_20adaptermonths)
                    second = tempMM.toLong()
                } else {
                    if (second >= 60) {
                        second /= 60
                        str = getString(R.string.a01_20adapterminutes)
                        if (second >= 60) {
                            second /= 60
                            str = getString(R.string.a01_20adapterhours)
                            if (second >= 24) {
                                second /= 24
                                str = getString(R.string.a01_20adapterdays)
                                if (second >= 7) {
                                    second /= 7
                                    str = getString(R.string.a01_20adapterweeks)
                                }
                            }
                        }
                    } else {
                        second = 1
                    }
                }


//				return new SimpleDateFormat("yyyyMMdd").format(cal.getTime());


//				return new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
                time.setText(second.toString() + str)
                time.setText(jo.getString("timeago"))

                val modname = jo.getString("modname")
                if (modname == "ubfile") {
                    if (jo.isNull("display")|| jo.getInt("display") == 6) {
                        layout.setId(ID_else)
                        layout.setTag(jo.getString("url") + "____" + jo.getString("course_name"))
                    } else {
                        layout.setId(ID_file)
                        layout.setTag(jo.getString("filedown_url"))
                    }
                } else if (modname == "adobeconnect") {
                    layout.setId(ID_adobeconnect)
                    layout.setTag(jo.getString("cmid"))
                } else if (modname == "ubchat") {
                    layout.setId(ID_ubchat)
                    layout.setTag(jo.getString("cmid"))
                } else if (modname == "ubboard" || modname == "announcement") {
                    layout.setId(ID_board)
                    layout.setTag(Integer.toString(position))
                } else if (modname == "ubboard") {
                    layout.setId(ID_boardList)
                    layout.setTag(Integer.toString(position))
                } else {
                    layout.setId(ID_else)
                    layout.setTag(jo.getString("url") + "____" + jo.getString("course_name"))
                }



                if (jo.getInt("deleted") == 1) {
                    layout.setOnTouchListener(null)
                    layout.setOnClickListener(null)
                    course_name.setPaintFlags(course_name.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    message.setPaintFlags(message.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    time.setPaintFlags(time.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                } else {
                    layout.setOnTouchListener { view, event ->
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            view.setBackgroundColor(A901Color.LTGRAY)
                        } else if (event.getAction() == MotionEvent.ACTION_UP||event.getAction() == MotionEvent.ACTION_MOVE) {
                            view.setBackgroundColor(A901Color.WHITE)
                        }
                        false

                    }
                    layout.setOnClickListener(onClick)
                    if (course_name.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG === Paint.STRIKE_THRU_TEXT_FLAG) {
                        course_name.setPaintFlags(course_name.getPaintFlags() - Paint.STRIKE_THRU_TEXT_FLAG)
                        message.setPaintFlags(message.getPaintFlags() - Paint.STRIKE_THRU_TEXT_FLAG)
                        time.setPaintFlags(time.getPaintFlags() - Paint.STRIKE_THRU_TEXT_FLAG)
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