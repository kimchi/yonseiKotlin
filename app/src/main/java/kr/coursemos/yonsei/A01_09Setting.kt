package kr.coursemos.yonsei

import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_a01_09_setting.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvDebug
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.util.*

class A01_09Setting : A00_00Activity() {
    private lateinit var mainBoardList: JSONObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_09_setting)
        try {
            StaticClass.history.clear()
            StaticClass.history.add(this.javaClass)
            putEnvInt(
                StaticClass.KEY_SELECTMAINTAB,
                StaticClass.ID_BOTTOMTAB5
            )
            initBottom(getString(R.string.a01_09layoutTitle))
            a01_09list.adapter = adapter



            mainBoardList = JSONObject()
            var cursor = cmdb.select(
                A903LMSTask.API_MAINBOARD,
                A903LMSTask.API_MAINBOARD,
                null,
                CMDB.TABLE01_common
            )
            var mainBoard: JSONObject? = null
            var index = cursor.getColumnIndex(CMDB.COLUMNjson)
            lateinit var jo: JSONObject
            var isnotice = true
            var isqna = true

            jo = JSONObject()
            adapter.idList.add(StaticClass.HEADER)
            adapter.jsonList.add(jo)

            while (cursor.moveToNext()) {
                jo = JSONObject()
                mainBoard = JSONObject(cursor.getString(index))
                mainBoardList.put(mainBoard.getString("id"), mainBoard)

                jo.put("name", mainBoard.getString("name"))
                jo.put("id", mainBoard.getString("id"))
                if (mainBoard.getString("type").equals("notice")) {

                    if (isnotice) {
                        isnotice = false
                        jo.put("icon", R.drawable.ic_m_01)
                        adapter.idList.add("board")
                        adapter.jsonList.add(jo)
                    }

                } else if (mainBoard.getString("type").equals("qna")) {

                    if (isqna) {
                        isqna = false
                        jo.put("icon", R.drawable.ic_m_02)
                        adapter.idList.add("board")
                        adapter.jsonList.add(jo)
                    }

                } else if (mainBoard.getString("type").equals("survey")) {
                    jo.put("icon", R.drawable.ic_m_08)
                    adapter.idList.add("board")
                    adapter.jsonList.add(jo)
                }


            }

            jo = JSONObject(
                getEnv(
                    StaticClass.KEY_SELECTSCHOOLMENULIST,
                    "{}"
                )
            )
            if (jo.isNull("survey")) {
            } else {
//				layout.AddMainBoard(getString(R.string.a01_09layoutSurvey),null,"survey");
            }

            jo = JSONObject()
            adapter.idList.add(StaticClass.HEADER)
            adapter.jsonList.add(jo)
            jo = JSONObject()
            jo.put("icon", R.drawable.ic_m_03)
            jo.put("name", getString(R.string.a01_09layoutRowLanguage))
            jo.put("id", "")
            adapter.idList.add("lang")
            adapter.jsonList.add(jo)


            jo = JSONObject()
            jo.put("icon", R.drawable.ic_m_04)
            jo.put("name", getString(R.string.a01_09layoutRow4))
            jo.put("id", "")
            adapter.idList.add("alarm")
            adapter.jsonList.add(jo)


            jo = JSONObject()
            adapter.idList.add(StaticClass.HEADER)
            adapter.jsonList.add(jo)

            jo = JSONObject()
            jo.put("icon", R.drawable.ic_m_05)
            jo.put("name", getString(R.string.a01_09layoutRow6))
            jo.put("id", "")
            adapter.idList.add("info")
            adapter.jsonList.add(jo)

            jo = JSONObject()
            jo.put("icon", R.drawable.ic_m_06)
            jo.put("name", getString(R.string.a01_09layoutRow8))
            jo.put("id", "")
            adapter.idList.add("qna")
            adapter.jsonList.add(jo)


            jo = JSONObject()
            jo.put("icon", R.drawable.ic_m_09)
            jo.put("name", getString(R.string.a01_09layoutRow8auth))
            jo.put("id", "")
            adapter.idList.add("auth")
            adapter.jsonList.add(jo)




            val menuList = JSONObject(getEnv(StaticClass.KEY_SELECTSCHOOLMENULIST, "{}"))
            if (menuList.isNull("otp")) {
            } else {

                jo = JSONObject()
                adapter.idList.add(StaticClass.HEADER)
                adapter.jsonList.add(jo)

                jo = JSONObject()
                jo.put("icon", R.drawable.ic_m_05)
                jo.put("name", "OTP")
                jo.put("id", "")
                adapter.idList.add("otp")
                adapter.jsonList.add(jo)
            }

            jo = JSONObject()
            adapter.idList.add(StaticClass.HEADER)
            adapter.jsonList.add(jo)


            jo = JSONObject()
            jo.put("icon", R.drawable.ic_m_07)
            jo.put("name", getString(R.string.a01_09layoutRow9))
            jo.put("id", "")
            adapter.idList.add("logout")
            adapter.jsonList.add(jo)


            a01_09list.adapter = adapter

            adapter.notifyDataSetInvalidated()
        } catch (e: Exception) {
            EnvError(e)
        }
    }


    val adapter = thisAdapter()

    inner class thisAdapter() : BaseAdapter() {
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<JSONObject> = ArrayList()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout: View
            try {


                var type = idList[position]
                var jo = jsonList[position]

                if (type.equals(StaticClass.HEADER)) {
                    layout = LayoutInflater.from(applicationContext)
                        .inflate(R.layout.list_a01_09header, null, false)
                } else {
                    layout = LayoutInflater.from(applicationContext)
                        .inflate(R.layout.list_a01_09row, null, false)
                    var text = layout.findViewById<TextView>(R.id.a01_09text)
                    var pic = layout.findViewById<ImageView>(R.id.a01_09pic)
                    layout.tag = type
                    layout.setOnTouchListener { view, event ->

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            view.setBackgroundResource(R.drawable.bottomlineltgray);
                        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE) {
                            view.setBackgroundResource(R.drawable.bottomlinewhite);
                        }
                        false
                    }
                    layout.setOnClickListener {
                        try {
                            EnvDebug(type)
                            if (type.equals("board")) {

                                setEnv(
                                    StaticClass.KEY_WEBURL,
                                    getEnv(
                                        StaticClass.KEY_SELECTSCHOOLURL,
                                        "http://edu3.moodledev.net/webservice/rest/server.php"
                                    ).replace(
                                        "webservice/rest/server.php",
                                        "mod/ubboard/view.php?id=" + jo.getString("id")
                                    )
                                )
                                setEnv(
                                    StaticClass.KEY_WEBVIEWNAME,
                                    jo.getString("name")
                                )
                                startActivity(A01_13WebView::class.java, 0)
                            } else if (type.equals("lang")) {
                                startActivity(A01_09Z09Language::class.java, 0)
                            } else if (type.equals("otp")) {
                                startActivity(A01_09Z11OTP::class.java, 0)
                            } else if (type.equals("alarm")) {
                                startActivity(A01_09Z04AlarmSetting::class.java, 0)
                            } else if (type.equals("info")) {
                                startActivity(A01_09Z06Version::class.java, 0)
                            } else if (type.equals("qna")) {
                                setEnvBoolean("BIOPASSINIT", false)
                                startActivity(A01_09Z08Inquiry::class.java, 0)
                            } else if (type.equals("auth")) {
                                startActivity(A01_09Z10Auth::class.java, 0)
                            } else if (type.equals("logout")) {
                                cmdb = CMDB.open(applicationContext)
                                val idInfo = cmdb.selectString(
                                    A903LMSTask.API_LOGIN,
                                    A903LMSTask.API_LOGIN,
                                    getEnv(
                                        StaticClass.KEY_USERSEQ,
                                        ""
                                    ),
                                    CMDB.TABLE01_common
                                )
                                if (idInfo == null || idInfo == "") {
                                    startActivity(A01_02Login::class.java, 0)
                                } else {
                                    val jo = JSONObject(idInfo)
                                    val shm = StringHashMap()
                                    shm["id"] = jo.getString("id")
                                    prd()
                                    object : A903LMSTask(applicationContext, API_LOGOUT, shm) {
                                        @Throws(java.lang.Exception::class)
                                        override fun success() {
                                            dismiss()
                                            if (pShm.getString(API_LOGOUT) == StaticClass.YES) {
                                                startActivity(A01_02Login::class.java, 0)
                                            } else {
                                                onKeyDown(KeyEvent.KEYCODE_BACK, null)
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
                            EnvError(e)
                        }
                    }
                    pic.setImageResource(jo.getInt("icon"))
                    text.setText(jo.getString("name"))

                }

            } catch (e: Exception) {
                EnvError(e)
            }

            return layout
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