package kr.coursemos.yonsei

import android.os.Bundle
import android.text.Html
import kotlinx.android.synthetic.main.activity_a01_15_z01_schedule_detail.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject

class A01_15Z01ScheduleDetail : A00_00Activity() {
	private lateinit var detail: JSONObject
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_a01_15_z01_schedule_detail)

		try{
			initBottom(getString(R.string.a01_15z01layoutTitle))
			val jsonString = cmdb.selectString(A903LMSTask.API_SCHEDULESELECT,
				A903LMSTask.API_SCHEDULESELECT,
				getEnv(StaticClass.KEY_SCHEDULEID, ""),
				CMDB.TABLE01_15Schedule)
			if (jsonString == null) {
				alert(getString(R.string.a01_15Z01activityUnKnowSchedule))
			} else {
				detail = JSONObject(jsonString)
				a01_15Z01Subject.setText(detail.getString("name"))
				a01_15Z01Term.setText(Env.getLongToDate(applicationContext,
					detail.getString("timestart")) + " ~ " + Env.getLongToDate(applicationContext,
					java.lang.Long.toString(detail.getLong("timestart") + detail.getLong("timeduration"))))
				if (detail.isNull("description")) {
				} else {
					a01_15Z01Content.setText(Html.fromHtml(detail.getString("description")))
				}
			}
		}catch (e:Exception){
			EnvError(e)
		}
	}
}