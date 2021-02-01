package kr.coursemos.yonsei

import android.app.AlertDialog
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_18_my_report.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import kr.coursemos.myapplication.a99util.htmlToString
import org.json.JSONObject
import java.util.*

class A01_18MyReport : A00_00Activity() {

    private lateinit var subjectID: String
    private lateinit var subjectName: String
    private lateinit var subjectIDList: Array<String?>
    private lateinit var subjectNameList: Array<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_18_my_report)
        try{
            initBottom(getString(R.string.a01_18layoutTitle))
            a01_18MyInfoLayout.setBackgroundColor(A901Color.getHeaderBackground())
            a01_18ReportList.setBackgroundColor(A901Color.WHITE)
            a01_18SubjectSelectArrow.setImageResource(R.drawable.arrow_view_down)
            a01_18SubjectSelect.setOnTouchListener(onTouch)
            a01_18SubjectSelect.setOnClickListener(onClick)
            a01_18ReportList.adapter=adapter
            if (getEnvInt(
                    StaticClass.KEY_LANGUAGE,
                    A01_09Z09Language.ID_row1
                ) === A01_09Z09Language.ID_row2
            ) {
                a01_18Name.setText(
                    getEnv(
                        StaticClass.KEY_LASTNAME,
                        ""
                    )
                )
            } else {
               a01_18Name.setText(
                    getEnv(
                        StaticClass.KEY_FIRSTNAME,
                        ""
                    )
                )
            }

            a01_18IDNumber.setText(
                getEnv(
                    StaticClass.KEY_IDNUMBER,
                    ""
                )
            )

            Picasso.with(applicationContext)
                .load(getEnv(StaticClass.KEY_PICURL, ""))
                .transform(RoundTransform())
                .error(android.R.color.transparent)
                .into(a01_18Pic)
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
            a01_18SubjectSelect.setText(subjectName)

            showList()
            request()

        }catch (e:java.lang.Exception){
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun showList() {
        a01_18SubjectSelect.setText(subjectName)
        val i = selectDB(
            0,
            A903LMSTask.API_REPORTSELECT,
            subjectID,
            CMDB.TABLE01_18MyReport,
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
    } //showList

    var onClick = View.OnClickListener { view ->
        try {
            val id = view.id
            if (id == R.id.a01_18SubjectSelect) {
                AlertDialog.Builder(me).setItems(
                    subjectNameList
                ) { dialog, which ->
                    try {
                        subjectName = subjectNameList[which]!!
                        subjectID = subjectIDList[which]!!
                        request()
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                }.show()
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    } // OnClickListener onClick


    @Throws(java.lang.Exception::class)
    fun request() {
        val shm = StringHashMap()
        shm["courseid"] = subjectID
        shm["userid"] = "0"
        object : A903LMSTask(applicationContext, API_REPORTSELECT, shm) {
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

    var onTouch = OnTouchListener { view, event ->
        try {
            val id = view.id
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (id == R.id.a01_18SubjectSelect) {
                    val draw: Drawable =
                        applicationContext.getResources().getDrawable(R.drawable.arrow_view_down)
                    draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)
                    a01_18SubjectSelectArrow.setImageDrawable(draw)
                    a01_18SubjectSelect.setTextColor(A901Color.getIhwaColor())
                }
            } else if (event.action == MotionEvent.ACTION_UP||event.action==MotionEvent.ACTION_MOVE) {
                if (id == R.id.a01_18SubjectSelect) {
                    val draw: Drawable =
                        applicationContext.getResources().getDrawable(R.drawable.arrow_view_down)
                    draw.colorFilter = null
                    a01_18SubjectSelectArrow.setImageDrawable(draw)
                    a01_18SubjectSelect.setTextColor(A901Color.WHITE)
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch


    val adapter=thisAdapter()
    inner class thisAdapter() : BaseAdapter(){
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout: View
            try{
                layout=LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_18row,null,false)
                var a01_18title=layout.findViewById<TextView>(R.id.a01_18title)
                var a01_18column1=layout.findViewById<TextView>(R.id.a01_18column1)
                var a01_18column2=layout.findViewById<TextView>(R.id.a01_18column2)
                var a01_18column3=layout.findViewById<TextView>(R.id.a01_18column3)
                var a01_18column4=layout.findViewById<TextView>(R.id.a01_18column4)


                val jo = JSONObject(jsonList[position])
                var tempString = jo.getString("itemname")
                if (tempString == "null") {
                    tempString = ""
                }
                a01_18title.setText(
                    tempString
                )

                tempString = jo.getString("finalgrade")
                if (tempString == "null") {
                    tempString = ""
                }
                a01_18column1.setText(
                    tempString
                )

                a01_18column2.setText(
                    String.format(
                        "%.1f-%.1f",
                        jo.getDouble("grademin"),
                        jo.getDouble("grademax")
                    )
                )

                tempString = jo.getString("percentage")
                if (tempString == "null") {
                    tempString = ""
                }
                a01_18column3.setText(
                    tempString
                )
                tempString = jo.getString("feedback")
                if (tempString == "null") {
                    tempString = ""
                }
                a01_18column4.setText(
                    tempString
                )
                a01_18column4.setOnClickListener {
                    val feedback =
                        JSONObject(adapter.jsonList.get(position))
                    val tempString: String = feedback.getString("feedback")
                    if (tempString == "null") {
                    } else {
                        alert(tempString.htmlToString())
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