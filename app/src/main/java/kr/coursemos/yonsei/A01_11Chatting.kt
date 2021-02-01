package kr.coursemos.yonsei

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.*
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_11_chatting.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.util.*

class A01_11Chatting : A00_00Activity() {

    var listPosition = 0
    var positionPaddingY = 0
    var isEnd = false
    var isLastRow = true



    public companion object {
        var recvChatting: Handler?=null

        fun getHandler():Handler?{
            return recvChatting
        }
        fun setHandler(handler:Handler?){
            recvChatting=handler
        }
    }

    private var chattingUser: JSONObject? = null
    private var addnum = 30
    private var minSeq = 1999999999
    private var maxSeq = 0
    private var isHistoryContinue = true
    private var chattingSelectShm: StringHashMap? = null
    private var isNoti = false
    private var isReady = false
    private lateinit var currentTime: String
    private   var beforeTime: String?=null
    private var isBackKeyDown = false
    lateinit  var mySeq: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_11_chatting)
        try{
            removeEnv(StaticClass.KEY_PUSHSCREEN)

            isNoti =
                intent.getBooleanExtra(StaticClass.KEY_ISNOTI, false)
            if (isNoti) {
                setEnv(
                    StaticClass.KEY_CHATTINGINFO,
                    getEnv(StaticClass.KEY_ISNOTI, "[]")
                )
            }
            chattingUser = JSONObject(
                getEnv(
                    StaticClass.KEY_CHATTINGINFO,
                    "[]"
                )
            )
            if (getEnvInt(
                    StaticClass.KEY_LANGUAGE,
                    A01_09Z09Language.ID_row1
                ) === A01_09Z09Language.ID_row2
            ) {
                chattingUser!!.put("fullname", chattingUser!!.getString("lastname"))
            } else {
                if (chattingUser!!.isNull("fullname")) {
                    chattingUser!!.put("fullname", chattingUser!!.getString("firstname"))
                }
            }
            if (chattingUser!!.isNull("id")) {
                chattingUser!!.put("id", chattingUser!!.getString("userid"))
            } else if (chattingUser!!.isNull("userid")) {
                chattingUser!!.put("userid", chattingUser!!.getString("id"))
            }
            setEnv(
                StaticClass.KEY_CHATTINGINFO,
                chattingUser.toString()
            )
            initBottom(chattingUser!!.getString("fullname"))

            mySeq= getEnv(StaticClass.KEY_USERSEQ, "")

            a01_11SendLayout.setOnClickListener(onClick)
            a01_11SendInputBox.setHintTextColor(A901Color.LTGRAY)
            a01_11SendButton.setOnTouchListener { v, event ->

                try {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                            a01_11SendButton.setTextColor(A901Color.getIhwaColorDown())
                    } else if (event.action == MotionEvent.ACTION_UP||event.action == MotionEvent.ACTION_MOVE) {
                            a01_11SendButton.setTextColor(A901Color.getIhwaColor())
                    }
                } catch (e: java.lang.Exception) {
                    Env.error("", e)
                }
                false

            }
            a01_11SendButton.setOnClickListener(onClick)
            a01_11SendButton.setTextColor(A901Color.getIhwaColor())
            a01_11ChattingList.adapter=adapter
            a01_11ChattingList.divider=null
            a01_11ChattingList.dividerHeight=0
            a01_11ChattingList.setOnScrollListener(object : AbsListView.OnScrollListener {
                var firstNum = 0
                override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                    try {
                        if (isEnd) {
                        } else {
                            if (scrollState == 0 && firstNum == 0) {
                                onClick.onClick(a01_11ChattingList)
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
                    firstNum = firstVisibleItem
                    isLastRow = firstVisibleItem + visibleItemCount > totalItemCount - 2
                    try {
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
            })
            a01_11ChattingList.setBackgroundColor(A901Color.getDefaultBackground())




            cmdb.DBClear(CMDB.TABLE01_11ChattingDetail)
            chattingSelectShm = StringHashMap()
            chattingSelectShm!!["historymonth"] = "9"

            recvChatting = null
            if (chattingUser!!.getBoolean("is_groupmessage")) {
                a01_11SendLayout.setVisibility(View.GONE)
            }

            if (isNoti) {
                isReady = true
                selectChatting()
            } else {
                isReady = true
                selectChatting()
            }
        }catch (e:Exception){
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    fun showList(isHistory: Boolean): Int {
        listPosition = listPosition
        positionPaddingY = positionPaddingY
        cmdb = CMDB.open(applicationContext)
        var cursor: Cursor? = null
        var rowLength: Int = adapter.idList.size
        var i = 0
        while (i < rowLength) {
            if (adapter.idList.get(i).equals("send")) {
                adapter.idList.removeAt(i)
                adapter.jsonList.removeAt(i)
                rowLength--
                i--
            }
            i++
        }
        if (isHistory) {
            cursor = cmdb.selectChattingHistory(chattingUser!!.getString("userid"), minSeq)
            if (adapter.jsonList.size > 0) {
                beforeTime = Env.getLongToDate(
                    applicationContext,
                    JSONObject(adapter.jsonList.get(0)).getString("timecreated")
                ).split(" ".toRegex(), 2).toTypedArray()[0]
            }
        } else {
            cursor = if (rowLength == 0) {
                cmdb.selectChattingFirst(chattingUser!!.getString("userid"))
            } else {
                cmdb.selectChattingNew(chattingUser!!.getString("userid"), maxSeq)
            }
            if (adapter.jsonList.size > 0) {
                beforeTime = Env.getLongToDate(
                    applicationContext,
                    JSONObject(adapter.jsonList.get(adapter.jsonList.size - 1)).getString(
                        "timecreated"
                    )
                ).split(" ".toRegex(), 2).toTypedArray()[0]
            }
        }
        val size = cursor.count
        if (size == 0) {
            return size
        }
        val index = cursor.getColumnIndex(CMDB.COLUMNjson)
        val index_id = cursor.getColumnIndex(CMDB.COLUMN_id)
        var jo: JSONObject? = null
        var seq = 0
        while (cursor.moveToNext()) {
            jo = JSONObject(cursor.getString(index))
            currentTime = if (jo.isNull("timecreated")) {
                "0"
            } else {
                Env.getLongToDate(applicationContext, jo.getString("timecreated")).split(" ".toRegex(), 2)
                    .toTypedArray()[0]
            }
            if (beforeTime == null) {
                beforeTime = currentTime
            }
            if (isHistory || rowLength == 0) {
                if (beforeTime!!.replace("\\.".toRegex(), "")
                        .toInt() > currentTime!!.replace("\\.".toRegex(), "").toInt()
                ) {
                    adapter.idList.add(0, "time")
                    adapter.jsonList.add(0, beforeTime!!)
                }
                adapter.idList.add(0, jo.getString("muid"))
                adapter.jsonList.add(0, jo.toString())
            } else {
                if (beforeTime!!.replace("\\.".toRegex(), "")
                        .toInt() < currentTime!!.replace("\\.".toRegex(), "").toInt()
                ) {
                    adapter.idList.add("time")
                    adapter.jsonList.add(currentTime)
                }
                adapter.idList.add(jo.getString("muid"))
                adapter.jsonList.add(jo.toString())
            }
            seq = cursor.getInt(index_id)
            beforeTime = currentTime
            if (seq < minSeq) {
                minSeq = seq
            }
            if (seq > maxSeq) {
                maxSeq = seq
            }
        }
        val newRowLength: Int = adapter.idList.size
        if (isHistory && rowLength > 0) {
            listPosition += newRowLength - rowLength
            a01_11ChattingList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL)
            adapter.notifyDataSetChanged()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                a01_11ChattingList.setSelectionFromTop(listPosition, positionPaddingY)
            } else {
                a01_11ChattingList.setSelection(listPosition)
            }
        } else {
            if (isLastRow) {
                a01_11ChattingList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL)
                adapter.notifyDataSetChanged()
                a01_11ChattingList.setSelection(adapter.idList.size - 1)
            } else {
                a01_11ChattingList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL)
                adapter.notifyDataSetChanged()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    a01_11ChattingList.setSelectionFromTop(listPosition, positionPaddingY)
                } else {
                    a01_11ChattingList.setSelection(listPosition)
                }
            }
        }
        return size
    }

    @Throws(java.lang.Exception::class)
    private fun selectChatting() {
        if (A01_11Chatting.recvChatting == null) {
            A01_11Chatting.recvChatting = object : Handler() {
                override fun handleMessage(msg: Message) {
                    try {
                        if(msg.obj==null){
                            if (isReady) {
                                showList(false)
                            }
                        }else{
                            val recvMsg = msg.obj as StringHashMap
                            val jo = JSONObject(recvMsg.getString("content"))
                            if (jo.getString("id") == chattingUser!!.getString("userid")) {
                                selectChatting()
                            } else {
                                if (getEnvBoolean(
                                        StaticClass.KEY_ALARMVIBRATEONOFF,
                                        true
                                    )
                                ) {
                                    (applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
                                        300
                                    )
                                }
                                if (getEnvBoolean(
                                        StaticClass.KEY_ALARMSOUNDONOFF,
                                        true
                                    )
                                ) {
                                    val uri =
                                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                                    if (A10_00FCMReceiver.getmAudio() != null) {
                                        A10_00FCMReceiver.getmAudio()?.release()
                                        A10_00FCMReceiver.setmAudio(null)
                                    }
                                    A10_00FCMReceiver.setmAudio(MediaPlayer())
                                    A10_00FCMReceiver.getmAudio()?.isLooping=false
                                    A10_00FCMReceiver.getmAudio()?.setDataSource(applicationContext,uri)
                                    A10_00FCMReceiver.getmAudio()?.prepare()
                                    A10_00FCMReceiver.getmAudio()?.start()
                                }
                                val notiManager =
                                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                var notiintent: Intent? = null
                                notiintent = Intent(applicationContext, A01_11Chatting::class.java)
                                notiintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                notiintent.putExtra(
                                    StaticClass.KEY_ACTIVITYALLKILL,
                                    StaticClass.YES
                                )
                                notiintent.putExtra(
                                    StaticClass.KEY_ISNOTI,
                                    true
                                )
                                setEnv(
                                    StaticClass.KEY_ISNOTI,
                                    jo.toString()
                                )
                                val content =
                                    PendingIntent.getActivity(applicationContext, 0, notiintent, 0)
                                val mNoti =
                                    NotificationCompat.Builder(applicationContext)
                                        .setLargeIcon(
                                            BitmapFactory.decodeResource(
                                                applicationContext.resources,
                                                R.drawable.icic
                                            )
                                        ).setSmallIcon(A10_00FCMReceiver.getNotificationIcon())
                                        .setContentTitle(jo.getString("firstname"))
                                        .setContentText(jo.getString("message"))
                                        .setTicker(jo.getString("message")).setAutoCancel(true)
                                        .setContentIntent(content).build()
                                notiManager.notify(0, mNoti)
                            }

                        }

                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }
            }
        }
        //		minSeq=2000201508;
//		maxSeq=0;
        A99_CycleManager.setSelect(applicationContext)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                isBackKeyDown = true
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun finish() {
        if (this.javaClass.name.equals(A01_11Chatting::class.java.name) && !isBackKeyDown) {
        } else {
            recvChatting = null
        }
        super.finish()
    }


    var onClick = View.OnClickListener { view ->
        try {
            val id = view.id
            if (id == R.id.a01_11SendButton) {
                if (a01_11SendInputBox.getText().toString().equals("")) {
                    alert(getString(R.string.a01_11activityEmptyMessage))
                } else {
//						prd(false);
                    val shm = StringHashMap()
                    shm["touserid[0]"] = chattingUser!!.getString("userid")
                    shm["subject"] = a01_11SendInputBox.getText().toString()
                    shm["content"] = a01_11SendInputBox.getText().toString()
                    shm["notification"] = "0"
                    object : A903LMSTask(applicationContext, API_CHATTINGSEND, shm) {
                        @Throws(java.lang.Exception::class)
                        override fun success() {
                            dismiss()
                            selectChatting()
                        }

                        @Throws(java.lang.Exception::class)
                        override fun fail(errmsg: String) {
                            dismiss()
                            errorCheckLogout(result, errorMsg)
                        }
                    }
                    val jo = JSONObject()
                    jo.put(
                        "useridfrom",
                        getEnv(StaticClass.KEY_USERSEQ, "")
                    )
                    jo.put("fullmessage", a01_11SendInputBox.getText().toString())
                    jo.put(
                        "timecreated",
                        java.lang.Long.toString(System.currentTimeMillis() / 1000)
                    )
                    adapter.idList.add("send")
                    adapter.jsonList.add(jo.toString())
                    a01_11ChattingList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL)
                    adapter.notifyDataSetChanged()
                    a01_11ChattingList.setSelection(adapter.idList.size - 1)
                    a01_11SendInputBox.setText("")
                }
            } else if (id == R.id.a01_11ChattingList) {
                if (isHistoryContinue) {
                    val size = showList(true)
                    if (size == 0) {
                        isHistoryContinue = false
                        prd()
                        chattingSelectShm!!["historymonth"] = "9"
                        chattingSelectShm!!["userid"] = chattingUser!!.getString("userid")
                        chattingSelectShm!!["idnumber"] = chattingUser!!.getString("idnumber")
                        var api = A903LMSTask.API_CHATTINGSELECT
                        if (chattingUser!!.getBoolean("is_groupmessage")) {
                            api = A903LMSTask.API_CHATTINGSELECT_GROUP
                            if (chattingUser!!.isNull("touserid")) {
                            } else {
                                chattingSelectShm!!["muid"] = chattingUser!!.getString("touserid")
                            }
                            if (chattingUser!!.isNull("muid")) {
                            } else {
                                chattingSelectShm!!["muid"] = chattingUser!!.getString("muid")
                            }
                        }
                        object : A903LMSTask(applicationContext, api, chattingSelectShm) {
                            @Throws(java.lang.Exception::class)
                            override fun success() {
                                isHistoryContinue = true
                                dismiss()
                                val size = showList(true)
                                if (size == 0) {
                                    isEnd = true
                                    adapter.idList.add(0, "time")
                                    adapter.jsonList.add(0, beforeTime!!)
                                    adapter.notifyDataSetChanged()
                                }
                            }

                            @Throws(java.lang.Exception::class)
                            override fun fail(errmsg: String) {
                                isHistoryContinue = true
                                dismiss()
                                errorCheckLogout(result, errorMsg)
                            }
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    } // OnClickListener onClick


    val adapter=thisAdapter()
    inner class thisAdapter() : BaseAdapter(){
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()
        public var chattingName: String=""
        public var chattingDraw: Drawable? = null
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout: View
            try{
                layout=LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_11row,null,false)
                layout.setOnClickListener {  }
                var a01_11leftlayout =layout.findViewById<ConstraintLayout>(R.id.a01_11leftlayout)
                var a01_11leftpic =layout.findViewById<ImageView>(R.id.a01_11leftpic)
                var a01_11lefttime =layout.findViewById<TextView>(R.id.a01_11lefttime)
                var a01_11leftname =layout.findViewById<TextView>(R.id.a01_11leftname)
                var a01_11leftcontent =layout.findViewById<TextView>(R.id.a01_11leftcontent)

                var a01_11rightlayout =layout.findViewById<ConstraintLayout>(R.id.a01_11rightlayout)
                var a01_11righttime =layout.findViewById<TextView>(R.id.a01_11righttime)
                var a01_11rightcontent =layout.findViewById<TextView>(R.id.a01_11rightcontent)

                var a01_11linelayout =layout.findViewById<ConstraintLayout>(R.id.a01_11linelayout)
                var a01_11linetime =layout.findViewById<TextView>(R.id.a01_11linetime)
                var a01_11line =layout.findViewById<View>(R.id.a01_11line)


                if (idList[position] == "time") {
                    a01_11leftlayout.visibility=View.GONE
                    a01_11rightlayout.visibility=View.GONE
                    a01_11linelayout.visibility=View.VISIBLE
                    a01_11linetime.setText("   " + jsonList[position] + "   ")
                } else {

                    a01_11linelayout.visibility=View.GONE
                    val jo = JSONObject(jsonList[position])
                    if (mySeq == jo.getString("useridfrom")) {
                        a01_11rightlayout.visibility=View.VISIBLE
                        a01_11leftlayout.visibility=View.GONE
                        a01_11rightcontent.setText(jo.getString("fullmessage"))
                        if (jo.isNull("timecreated")) {
                            a01_11righttime.setText("")
                        } else {
                            a01_11righttime.setText(Env.getLongToTIME(jo.getString("timecreated")))
                        }
                    } else {
                        a01_11rightlayout.visibility=View.GONE
                        a01_11leftlayout.visibility=View.VISIBLE

                        a01_11leftcontent.setText(jo.getString("fullmessage"))
                        a01_11leftname.setText(chattingName)
                        if (jo.isNull("timecreated")) {
                            a01_11lefttime.setText("")
                        } else {
                            a01_11lefttime.setText(Env.getLongToTIME(jo.getString("timecreated")))
                        }
                        a01_11leftpic.setImageDrawable(chattingDraw)
                        Picasso.with(applicationContext)
                            .load(jo.getString("profileimageurlsmall"))
                            .transform(RoundTransform())
                            .error(android.R.color.transparent)
                            .into(a01_11leftpic)
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