package kr.coursemos.yonsei

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError
import kr.coursemos.myapplication.a99util.htmlToString
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [A01_04Z02MenuAdapter.newInstance] factory method to
 * create an instance of this fragment.
 */
class A01_04Z02MenuAdapter : Fragment() {
	// TODO: Rename and change types of parameters
	private var param1: String? = null
	private var param2: String? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			param1 = it.getString(ARG_PARAM1)
			param2 = it.getString(ARG_PARAM2)
		}
	}

	private var subjectID: String? = null
	private var listPosition_menu = 0
	private var positionPaddingY_menu = 0
	lateinit var list: ListView
	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		var layout = inflater.inflate(R.layout.fragment_a01_04_z02_menu, container, false)

		try {
			subjectID = A00_00Activity.me.getEnv(StaticClass.KEY_SUBJECTID, "")
			list = layout.findViewById<ListView>(R.id.a01_04menulist)
			list.adapter = adapter
			list.setOnScrollListener(object : AbsListView.OnScrollListener {
				override fun onScroll(view: AbsListView?,
				                      firstVisibleItem: Int,
				                      visibleItemCount: Int,
				                      totalItemCount: Int) {
					try {
						if (view!!.getChildAt(0) == null) {
							positionPaddingY_menu = 0
						} else {
							val tempPaddingY = view.getChildAt(0).top
							positionPaddingY_menu = tempPaddingY
						}
						listPosition_menu = firstVisibleItem
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
				}

				override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
				}
			})
			showList()
		} catch (e: java.lang.Exception) {
			EnvError(e)
		}


		return layout
	}

	override fun onDestroyView() {
		try {
			A00_00Activity.me.setEnvInt(StaticClass.KEY_MENUCACHEPOSITIONY, listPosition_menu)
			A00_00Activity.me.setEnvInt(StaticClass.KEY_MENUCACHEPADDINGY, positionPaddingY_menu)
		} catch (e: java.lang.Exception) {
			EnvError(e)
		}
		super.onDestroyView()
	}

	companion object {
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param param1 Parameter 1.
		 * @param param2 Parameter 2.
		 * @return A new instance of fragment A01_04Z02MenuAdapter.
		 */
		// TODO: Rename and change types and number of parameters
		@JvmStatic
		fun newInstance(param1: String, param2: String) = A01_04Z02MenuAdapter().apply {
			arguments = Bundle().apply {
				putString(ARG_PARAM1, param1)
				putString(ARG_PARAM2, param2)
			}
		}
	}

	@Throws(java.lang.Exception::class)
	private fun showList() {
		var jo: JSONObject? = null
		val isProfessor: String = A00_00Activity.me.getEnv(StaticClass.KEY_MYPOSITION, "")
		val menuList =
			JSONObject(A00_00Activity.me.getEnv(StaticClass.KEY_SELECTSCHOOLMENULIST, "{}"))
		val courseMenuList =
			JSONObject(A00_00Activity.me.getEnv(StaticClass.KEY_SELECTCOURSEMENULIST,
				"{\"grade\":0,\"attendance\":0,\"onlineattendance\":0,\"notification\":0,\"syllabus\":0}}"))
		var i = 0
		adapter.idList.clear()
		adapter.jsonList.clear()
		adapter.idList.add(StaticClass.HEADER)
		jo = JSONObject()
		jo.put("name", getString(R.string.a01_04activitydefaultinfo))
		jo.put("id", "")
		adapter.jsonList.add(jo.toString())
		if (menuList.isNull("professor")) {
		} else {
			adapter.idList.add(Integer.toString(adapter.ID_professorInfo))
			jo.put("name", getString(R.string.a01_04activityProfessorInfo))
			jo.put("id", adapter.ID_professorInfo)
			adapter.jsonList.add(jo.toString())
		}
		if (isProfessor == "3" || isProfessor == "9" || isProfessor == "1") {
			adapter.idList.add(Integer.toString(adapter.ID_studentInfo))
			jo.put("name", getString(R.string.a01_04activityStudentInfo))
			jo.put("id", adapter.ID_studentInfo)
			adapter.jsonList.add(jo.toString())
		} else {
			if (menuList.isNull("professor")) {
			} else {
				adapter.idList.add(Integer.toString(adapter.ID_studentInfo))
				jo.put("name", getString(R.string.a01_04activityStudentInfo))
				jo.put("id", adapter.ID_studentInfo)
				adapter.jsonList.add(jo.toString())
			}
		}
		adapter.idList.add(StaticClass.HEADER)
		jo.put("name", getString(R.string.a01_04activityBoard))
		jo.put("id", "")
		adapter.jsonList.add(jo.toString())
		i = A00_00Activity.me.selectDB(4,
			subjectID,
			"menu",
			CMDB.TABLE01_04z01BoardInfo,
			adapter.idList,
			adapter.jsonList)
		adapter.idList.add(StaticClass.HEADER)
		jo.put("name", "")
		jo.put("id", "")
		adapter.jsonList.add(jo.toString())
		if (isProfessor == "3" || isProfessor == "9" || isProfessor == "1") {
			//			layout.quickmenu.setVisibility(View.VISIBLE);
			//			if(courseMenuList.getInt("attendance")==1){
			if (menuList.isNull("patt")) {
			} else {
				adapter.idList.add(Integer.toString(adapter.ID_professorAttendance))
				jo.put("name", getString(R.string.a01_04activityProfessorAttendance))
				jo.put("id", adapter.ID_professorAttendance)
				adapter.jsonList.add(jo.toString())
			}
			if (menuList.isNull("pautoatt")) {
			} else {
				adapter.idList.add(Integer.toString(adapter.ID_professorAttendance))
				jo.put("name", getString(R.string.a01_04activityProfessorAutoAttendance))
				jo.put("id", adapter.ID_professorAutoAttendance)
				adapter.jsonList.add(jo.toString())
			}
			//			}
		} else if (isProfessor == "5" || isProfessor == "10" || isProfessor == "11") {
			if (menuList.isNull("oatt")) {
			} else {
				if (courseMenuList.getInt("onlineattendance") == 1) {
					adapter.idList.add(Integer.toString(adapter.ID_onlineAttendance))
					jo.put("name", getString(R.string.a01_04activityOnlineAttendance))
					jo.put("id", adapter.ID_onlineAttendance)
					adapter.jsonList.add(jo.toString())
				}
			}
			//			Env.debug(context,courseMenuList.toString());
			//			Env.debug(context,menuList.toString());
			//			if(courseMenuList.getInt("grade")==1){
			if (menuList.isNull("grade")) {
			} else {
				adapter.idList.add(Integer.toString(adapter.ID_studentGrade))
				jo.put("name", getString(R.string.a01_04activityStudentGrade))
				jo.put("id", adapter.ID_studentGrade)
				adapter.jsonList.add(jo.toString())
			}
			//			}
			if (menuList.isNull("syllabus")) {
			} else {
				if (courseMenuList.isNull("syllabus")) {
				} else {
					if (courseMenuList.getInt("syllabus") == 1) {
						adapter.idList.add(Integer.toString(adapter.ID_syllabus))
						jo.put("name", getString(R.string.a01_04activitysyllabus))
						jo.put("id", adapter.ID_syllabus)
						adapter.jsonList.add(jo.toString())
					}
				}
			}
			//			if(courseMenuList.getInt("attendance")==1){
			if (menuList.isNull("att")) {
			} else {
				adapter.idList.add(Integer.toString(adapter.ID_studentAttendance))
				jo.put("name", getString(R.string.a01_04activityStudentAttendance))
				jo.put("id", adapter.ID_studentAttendance)
				adapter.jsonList.add(jo.toString())
			}
			if (menuList.isNull("autoatt")) {
			} else {
				adapter.idList.add(Integer.toString(adapter.ID_studentAutoAttendance))
				jo.put("name", getString(R.string.a01_04activityStudentAutoAttendance))
				jo.put("id", adapter.ID_studentAutoAttendance)
				adapter.jsonList.add(jo.toString())
			}
			//			}
		}
		//		menuAdapter.idList.add(Integer.toString(menuAdapter.ID_QRCode));
		//		jo.put("name", "QR");
		//		jo.put("id", menuAdapter.ID_QRCode);
		//		menuAdapter.jsonList.add(jo.toString());
		adapter.notifyDataSetChanged()

		if (A00_00Activity.me.isBackKey) {
			list.setSelectionFromTop(A00_00Activity.me.getEnvInt(StaticClass.KEY_MENUCACHEPOSITIONY,
				0), A00_00Activity.me.getEnvInt(StaticClass.KEY_MENUCACHEPADDINGY, 0))
		}
	}

	val adapter = thisAdapter()

	inner class thisAdapter() : BaseAdapter() {
		public var idList: ArrayList<String> = ArrayList()
		public var jsonList: ArrayList<String> = ArrayList()
		var ID_professorInfo = View.generateViewId()
		var ID_studentInfo = View.generateViewId()
		var ID_professorAttendance = View.generateViewId()
		var ID_professorAutoAttendance = View.generateViewId()
		var ID_studentAttendance = View.generateViewId()
		var ID_studentAutoAttendance = View.generateViewId()
		var ID_studentGrade = View.generateViewId()
		var ID_onlineAttendance = View.generateViewId()
		var ID_syllabus = View.generateViewId()
		var ID_QRCode = View.generateViewId()
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			lateinit var layout: View
			try {
				if (idList[position] == StaticClass.HEADER) {
					layout = LayoutInflater.from(context)
						.inflate(R.layout.list_a01_04_z02menuheader, null, false)
				} else {
					layout = LayoutInflater.from(context)
						.inflate(R.layout.list_a01_04_z02menu, null, false)
					layout.setOnTouchListener(onTouch)
					layout.setOnClickListener(onClick)
					layout.setTag(position.toString());
				}
				var a01_04menutext = layout.findViewById<TextView>(R.id.a01_04menutext)
				var jo = JSONObject(jsonList[position])
				a01_04menutext.setText(jo.getString("name").htmlToString())
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

	var onClick: View.OnClickListener = object : View.OnClickListener {
		override fun onClick(view: View) {
			try {
				val id = view.id
				val jo = JSONObject(adapter.jsonList.get((view.tag as String).toInt()))
				val type: Int = jo.getInt("id")
				if (type == adapter.ID_professorInfo) {
					A00_00Activity.me.startActivity(A01_05ProfessorList::class.java, 0)
				} else if (type == adapter.ID_studentInfo) {
					A00_00Activity.me.startActivity(A01_06StudentList::class.java, 0)
				} else if (type == adapter.ID_professorAttendance) {
					A00_00Activity.me.startActivity(A01_07AttendanceTeacher::class.java, 0)
				} else if (type == adapter.ID_professorAutoAttendance) {
					A00_00Activity.me.startActivity(A01_07Z91AutoAttendanceTeacher::class.java, 0)
				} else if (type == adapter.ID_studentAttendance) {
					A00_00Activity.me.startActivity(A01_08AttendanceStudent::class.java, 0)
				} else if (type == adapter.ID_studentAutoAttendance) {
					A00_00Activity.me.startActivity(A01_08Z91AutoAttendanceStudent::class.java, 0)
				} else if (type == adapter.ID_studentGrade) {
					A00_00Activity.me.startActivity(A01_18MyReport::class.java, 0)
				} else if (type == adapter.ID_onlineAttendance) {
					if (StaticClass.API == null) {
						StaticClass.API = A00_00Activity.me.getEnv(StaticClass.KEY_SELECTSCHOOLURL,
							"http://edu3.moodledev.net/webservice/rest/server.php")
					}
					val url = "http://" + StaticClass.API.split("//".toRegex())
						.toTypedArray()[1].split("/".toRegex())
						.toTypedArray()[0] + "/report/ubcompletion/progress.php?id=" + subjectID
					if (StaticClass.isDebug) {
						Env.debug(context, url)
					}
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBURL, url)
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBVIEWNAME,
						getString(R.string.a01_04activityOnlineAttendance))
					A00_00Activity.me.startActivity(A01_13WebView::class.java, 0)
				} else if (type == adapter.ID_syllabus) {
					if (StaticClass.API == null) {
						StaticClass.API = A00_00Activity.me.getEnv(StaticClass.KEY_SELECTSCHOOLURL,
							"http://edu3.moodledev.net/webservice/rest/server.php")
					}
					val url = "http://" + StaticClass.API.split("//".toRegex())
						.toTypedArray()[1].split("/".toRegex())
						.toTypedArray()[0] + "/local/ubion/setting/syllabus.php?id=" + subjectID
					if (StaticClass.isDebug) {
						Env.debug(context, url)
					}
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBURL, url)
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBVIEWNAME,
						getString(R.string.a01_04activitysyllabus))
					A00_00Activity.me.startActivity(A01_13WebView::class.java, 0)
				} else if (type == adapter.ID_QRCode) {
					IntentIntegrator(A00_00Activity.me).initiateScan()
				} else {
					val boardInfo = JSONObject(adapter.jsonList.get((view.tag as String).toInt()))
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBURL, boardInfo.getString("url"))
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBVIEWNAME,
						boardInfo.getString("name"))
					A00_00Activity.me.startActivity(A01_13WebView::class.java, 0)
					//						edit.putString(StaticClass.KEY_BOARDID, Integer.toString(type));
					//						edit.commit();
					//						startActivity(A01_10Board.class, 0);
				}
			} catch (e: java.lang.Exception) {
				Env.error("", e)
			}
		}
	} // OnClickListener onClick
	var onTouch = View.OnTouchListener { view, event ->
		try {
			val id = view.id
			if (event.action == MotionEvent.ACTION_DOWN) {
				if (id == R.id.a01_04menulayout) {
					//                        Drawable draw=context.getResources().getDrawable(R.drawable.calendar_list_bar_push);
					//                        draw.setColorFilter(null);
					//                        view.findViewById(ID_background).setBackground(draw);
					view.setBackgroundColor(A901Color.LTGRAY)
					//                    a01_03listmainText.setTextColor(A901Color.getIhwaColor())
				}
			} else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
				if (id == R.id.a01_04menulayout) {
					view.setBackgroundResource(R.drawable.bottom_line_box)
					//                    a01_03listmainText.setTextColor(A901Color.GRAY)
				}
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
		false
	} // OnTouchListener onTouch
}