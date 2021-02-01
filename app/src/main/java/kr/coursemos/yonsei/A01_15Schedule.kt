package kr.coursemos.yonsei

import android.app.AlertDialog
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.DatePicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.a00_top2_layout.*
import kotlinx.android.synthetic.main.activity_a01_15_schedule.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.util.*

class A01_15Schedule : A00_00Activity() {
	private val datePicker: DatePicker? = null
	private var searchTime: String? = null
	lateinit var scheduleNameList: Array<String?>
	private lateinit var scheduleIDList: Array<String?>
	private var position = 0
	private var scheduleDBType: String? = null
	private var courseID = ""
	private lateinit var courseIDList: Array<String?>
	private lateinit var courseNameList: Array<String?>
	var year = 0
	var month = 0
	var day = 0
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_a01_15_schedule)
		try {
			StaticClass.history.clear()
			StaticClass.history.add(this.javaClass)
			putEnvInt(StaticClass.KEY_SELECTMAINTAB, StaticClass.ID_BOTTOMTAB4)
			initBottom(getString(R.string.a01_09z08layoutTitle))

			scheduleNameList = arrayOf<String?>(getString(R.string.a01_15layoutList1),
				getString(R.string.a01_15layoutList2),
				getString(R.string.a01_15layoutList3),
				getString(R.string.a01_15layoutList6))
			a00_title.setText(scheduleNameList[0])
			a00_righttext.text = getString(R.string.today)
			a00_righttext.setTextColor(A901Color.getIhwaColor())
			a00_righttext.visibility = View.VISIBLE
			a00_righttext.setOnTouchListener(onTouch)
			a00_righttext.setOnClickListener(onClick)
			a00_title.setOnTouchListener(onTouch)
			a00_title.setOnClickListener(onClick)

			a01_15viewPager.registerOnPageChangeCallback(object :
				ViewPager2.OnPageChangeCallback() {
				override fun onPageScrolled(position: Int,
				                            positionOffset: Float,
				                            positionOffsetPixels: Int) {
				}

				override fun onPageSelected(position: Int) {
					if (position == 0) {
						weekAdapter.currentWeek.add(Calendar.WEEK_OF_YEAR, -1)
					} else if (position == 1) {
					} else if (position == 2) {
						weekAdapter.currentWeek.add(Calendar.WEEK_OF_YEAR, 1)
					}
				}

				override fun onPageScrollStateChanged(state: Int) {
					try {
						if (state == ViewPager.SCROLL_STATE_IDLE) {
							weekAdapter.setApplyWeek()
						}
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
				}
			})


			weekAdapter= WeekAdapter()
			a01_15viewPager.adapter = weekAdapter
            a01_15ScheduleList.adapter=adapter

			val today = Env.getToday()
			year = today.substring(0, 4).toInt()
			month = today.substring(4, 6).toInt()
			day = today.substring(6, 8).toInt()

			scheduleIDList = arrayOf<String?>("all", "site", "course", "user")
			scheduleDBType = getEnv(StaticClass.KEY_SCHEDULETYPE, "")
			val type = scheduleDBType!!.split("_".toRegex()).toTypedArray()
			if (type.size > 1) {
				setDay(Env.getLongToDay(type[1], ""))
			}
			removeEnv(StaticClass.KEY_SCHEDULETYPE)
			if (isBackKey) {
				year = getEnvInt("yyyy", 0)
				month = getEnvInt("mm", 0)
				day = getEnvInt("dd", 0)
				courseID = getEnv("courseID", "")
				position = getEnvInt("position", 0)
				a00_title.setText(scheduleNameList.get(position))
				showList()
				weekAdapter.currentWeek.set(Calendar.YEAR, year)
				weekAdapter.currentWeek.set(Calendar.MONTH, month - 1)
				weekAdapter.currentWeek.set(Calendar.DATE, day)
				weekAdapter.selectIndex = getEnvInt("selectIndex", 0)
				weekAdapter.setApplyWeek()
			} else {
				selectSchedule(true)
			}
		} catch (e: Exception) {
			EnvError(e)
		}
	}

	@Throws(java.lang.Exception::class)
	private fun setDay(yyyymmdd: String) {
		year = yyyymmdd.substring(0, 4).toInt()
		month = yyyymmdd.substring(4, 6).toInt()
		day = yyyymmdd.substring(6, 8).toInt()
	}

	var onTouch = OnTouchListener { view, event ->
		try {
			val id = view.id
			if (event.action == MotionEvent.ACTION_DOWN) {
				if (id == R.id.a00_title) {
					val draw: Drawable =
						applicationContext.getResources().getDrawable(R.drawable.arrow_view_down)
					draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)
					a01_15SelectArrow.setImageDrawable(draw)
					(view as TextView).setTextColor(A901Color.getIhwaColor())
				} else if (id == R.id.a00_righttext) {
					(view as TextView).setTextColor(A901Color.getIhwaColorDown())
				}
			} else if (event.action == MotionEvent.ACTION_UP) {
				if (id == R.id.a00_title) {
					val draw: Drawable =
						applicationContext.getResources().getDrawable(R.drawable.arrow_view_down)
					draw.colorFilter = null
					a01_15SelectArrow.setImageDrawable(draw)
					(view as TextView).setTextColor(A901Color.WHITE)
				} else if (id == R.id.a00_righttext) {
					(view as TextView).setTextColor(A901Color.getIhwaColor())
				}
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
		false
	} //OnTouchListener onTouch
	var onClick = View.OnClickListener { view ->
		try {
			val id = view.id
			if (id == R.id.a00_title) {
				showTypeList()
			} else if (id == adapter.ID_scheduleSelect) {
				val scheduleid = view.tag as String
				setEnv(StaticClass.KEY_SCHEDULETYPE, scheduleDBType!!)
				setEnv(StaticClass.KEY_SCHEDULEID, scheduleid)
				val cmdb = CMDB.open(applicationContext)
				val jsonString = cmdb.selectString(A903LMSTask.API_SCHEDULESELECT,
					A903LMSTask.API_SCHEDULESELECT,
					scheduleid,
					CMDB.TABLE01_15Schedule)
				val jo = JSONObject(jsonString)
				if (jo.isNull("url")) {
					jo.put("url", "")
				}
				var url = jo.getString("url")
				//박비봉이가 무조건 디테일로 가라고했음
				url = null
				if (url == null || url == "" || url == "null") {
					startActivity(A01_15Z01ScheduleDetail::class.java, 0)
				} else {
					setEnv(StaticClass.KEY_WEBURL, jo.getString("url"))
					setEnv(StaticClass.KEY_WEBVIEWNAME, jo.getString("name"))
					startActivity(A01_13WebView::class.java, 0)
				}
			} else if (id == weekAdapter.ID_ReadyLayout) {
				a01_15viewPager.setCurrentItem(1, false)
				weekAdapter.setApplyWeek()
			} else if (id == R.id.a01_15sunday) {
				if (view.tag == null) {
					weekAdapter.selectIndex = 0
					weekAdapter.setApplyWeek()
				} else {
					selectDay(view)
				}
			} else if (id == R.id.a01_15monday) {
				if (view.tag == null) {
					weekAdapter.selectIndex = 1
					weekAdapter.setApplyWeek()
				} else {
					selectDay(view)
				}
			} else if (id == R.id.a01_15tuesday) {
				if (view.tag == null) {
					weekAdapter.selectIndex = 2
					weekAdapter.setApplyWeek()
				} else {
					selectDay(view)
				}
			} else if (id == R.id.a01_15wednesday) {
				if (view.tag == null) {
					weekAdapter.selectIndex = 3
					weekAdapter.setApplyWeek()
				} else {
					selectDay(view)
				}
			} else if (id == R.id.a01_15thursday) {
				if (view.tag == null) {
					weekAdapter.selectIndex = 4
					weekAdapter.setApplyWeek()
				} else {
					selectDay(view)
				}
			} else if (id == R.id.a01_15friday) {
				if (view.tag == null) {
					weekAdapter.selectIndex = 5
					weekAdapter.setApplyWeek()
				} else {
					selectDay(view)
				}
			} else if (id == R.id.a01_15saturday) {
				if (view.tag == null) {
					weekAdapter.selectIndex = 6
					weekAdapter.setApplyWeek()
				} else {
					selectDay(view)
				}
			} else if (id == R.id.a00_righttext) {
				weekAdapter.currentWeek = Calendar.getInstance()
				weekAdapter.selectIndex = -1
				weekAdapter.setApplyWeek()
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	} // OnClickListener onClick

	@Throws(java.lang.Exception::class)
	private fun showTypeList() {
		AlertDialog.Builder(me)
			.setItems(scheduleNameList, DialogInterface.OnClickListener { dialog, which ->
				try {
					position = which
					cmdb = CMDB.open(applicationContext)
					if (position == 2) {
						//						String[] types=new String[]{A903LMSTask.current,A903LMSTask.past,A903LMSTask.open};
						val cursor = cmdb.select(A903LMSTask.API_CURRENTSUBJECT,
							A903LMSTask.API_CURRENTSUBJECT,
							null,
							CMDB.TABLE01_03SubjectList)
						showCourseList(cursor, cursor.count)
					} else {
						courseID = ""
						a00_title.setText(scheduleNameList.get(position))
						selectSchedule(false)
					}
				} catch (e: java.lang.Exception) {
					Env.error("", e)
				}
			}).show()
	}

	@Throws(java.lang.Exception::class)
	private fun showCourseList(cursor: Cursor, size: Int) {
		courseIDList = arrayOfNulls(size)
		courseNameList = arrayOfNulls(size)
		var jo: JSONObject? = null
		val index = cursor.getColumnIndex(CMDB.COLUMNjson)
		var i = 0
		while (cursor.moveToNext()) {
			jo = JSONObject(cursor.getString(index))
			courseIDList[i] = jo.getString("id")
			courseNameList[i] = jo.getString("fullname")
			i++
		}
		courseID = courseIDList[0]!!
		AlertDialog.Builder(me).setItems(courseNameList) { dialog, which ->
			try {
				a00_title.setText(scheduleNameList.get(position))
				courseID = courseIDList[which]!!
				selectSchedule(false)
			} catch (e: java.lang.Exception) {
				Env.error("", e)
			}
		}.show()
	}

	@Throws(java.lang.Exception::class)
	fun selectDay(view: View) {
		val tag = view.tag as String
		year = tag.substring(0, 4).toInt()
		month = tag.substring(4, 6).toInt()
		day = tag.substring(6, 8).toInt()
		selectSchedule(false)
	}

	@Throws(java.lang.Exception::class)
	private fun selectSchedule(isFirst: Boolean) {
		prd()
		searchTime = Env.getyyyyMMddToLong(java.lang.String.format("%d%02d%02d", year, month, day))
		scheduleDBType = scheduleIDList.get(position).toString() + "_" + searchTime + "_" + courseID
		val shm = StringHashMap()
		if (courseID == "") {
			shm["idvalue"] = "0"
			shm["filter"] = scheduleIDList.get(position)
		} else {
			shm["idvalue"] = courseID
			shm["filter"] = "course"
			scheduleDBType = "course_" + searchTime + "_" + courseID
		}
		shm["timestart"] = searchTime
		shm["periodday"] = "14"
		shm["page"] = "0"
		shm["maxlimit"] = "999"
		//		shm.put("periodday" ,"1");
		var api = A903LMSTask.API_SCHEDULESELECT
		if (isFirst) {
			api = A903LMSTask.API_ALLSUBJECT
		}
		object : A903LMSTask(applicationContext, api, shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {

                if(isFirst){
                    onClick.onClick(a00_righttext)
                }else{
                    dismiss()
                    showList()
                }

			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				dismiss()
				errorCheckLogout(result, errorMsg)
			}
		}
	}

	@Throws(java.lang.Exception::class)
	private fun showList() {
        val startIndex = selectDB(0,
            A903LMSTask.API_SCHEDULESELECT,
            A903LMSTask.API_SCHEDULESELECT,
            CMDB.TABLE01_15Schedule,
            adapter.idList,
            adapter.jsonList)
        deleteList(startIndex,
            adapter.idList,
            adapter.jsonList,
            null)
        adapter.notifyDataSetChanged()
	} // showList();

	override fun finish() {
		super.finish()
		try {
			setEnvInt("yyyy", year)
			setEnvInt("mm", month)
			setEnvInt("dd", day)
			setEnv("courseID", courseID)
			setEnvInt("position", position)
			setEnvInt("selectIndex", weekAdapter.selectIndex)
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	}

	lateinit var weekAdapter:WeekAdapter

	inner class WeekAdapter : RecyclerView.Adapter<WeekAdapter.MyPagerViewHolder>() {
		var currentWeek = Calendar.getInstance()
		var selectIndex = -1
		var prevLayout: View? = null
		var currentLayout: View? = null
		var nextLayout: View? = null
		var ID_ReadyLayout = View.generateViewId()
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagerViewHolder {
			lateinit var layout: View
			try {
				layout = LayoutInflater.from(applicationContext)
					.inflate(R.layout.list_a01_15week, parent, false)

			} catch (e: java.lang.Exception) {
				EnvError(e)
			}
			return MyPagerViewHolder(layout)
		}

		override fun onBindViewHolder(holder: MyPagerViewHolder, position: Int) {
			holder.bind(position)
		}

		override fun getItemCount(): Int {
			return 3
		}

		@Throws(java.lang.Exception::class)
		open fun setApplyWeek() {
			if (a01_15viewPager.getCurrentItem() == 0) {
				if (currentLayout == null) {
				} else {
					updateWeek(currentWeek, currentLayout!!, 0)
				}
				a01_15viewPager.setCurrentItem(1, false)
				if (prevLayout == null) {
				} else {
					updateWeek(currentWeek, prevLayout!!, -1)
				}
				if (nextLayout == null) {
				} else {
					updateWeek(currentWeek, nextLayout!!, 1)
				}
			} else if (a01_15viewPager.getCurrentItem() == 2) {
				if (prevLayout == null) {
				} else {
					updateWeek(currentWeek, prevLayout!!, -1)
				}
				if (currentLayout == null) {
				} else {
					updateWeek(currentWeek, currentLayout!!, 0)
				}
				a01_15viewPager.setCurrentItem(1, false)
				if (nextLayout == null) {
				} else {
					updateWeek(currentWeek, nextLayout!!, 1)
				}
			} else {
				if (prevLayout == null) {
				} else {
					updateWeek(currentWeek, prevLayout!!, -1)
				}
				if (currentLayout == null) {
				} else {
					updateWeek(currentWeek, currentLayout!!, 0)
				}
				if (nextLayout == null) {
				} else {
					updateWeek(currentWeek, nextLayout!!, 1)
				}
			}
		}

		@Throws(java.lang.Exception::class)
		fun updateWeek(currentWeek: Calendar, layout: View, add: Int) {
			val modCal = Calendar.getInstance()
			modCal.time = currentWeek.time
			modCal.add(Calendar.WEEK_OF_YEAR, add)
			val now = Calendar.getInstance()
			lateinit var tempText: TextView
			var gd: GradientDrawable? = null
			for (i in 0..6) {
				if (i == 0) {
					tempText = layout.findViewById<TextView>(R.id.a01_15sunday)
					layout.findViewById<TextView>(R.id.a01_15sundaytext).setText(getString(R.string.sunday))
				} else if (i == 1) {
					tempText = layout.findViewById<TextView>(R.id.a01_15monday)
					layout.findViewById<TextView>(R.id.a01_15mondaytext).setText(getString(R.string.monday))
				} else if (i == 2) {
					tempText = layout.findViewById<TextView>(R.id.a01_15tuesday)
					layout.findViewById<TextView>(R.id.a01_15tuesdaytext).setText(getString(R.string.tuesday))
				} else if (i == 3) {
					tempText = layout.findViewById<TextView>(R.id.a01_15wednesday)
					layout.findViewById<TextView>(R.id.a01_15wednesdaytext).setText(getString(R.string.wednesday))
				} else if (i == 4) {
					tempText = layout.findViewById<TextView>(R.id.a01_15thursday)
					layout.findViewById<TextView>(R.id.a01_15thursdaytext).setText(getString(R.string.thursday))
				} else if (i == 5) {
					tempText = layout.findViewById<TextView>(R.id.a01_15friday)
					layout.findViewById<TextView>(R.id.a01_15fridaytext).setText(getString(R.string.friday))
				} else if (i == 6) {
					tempText = layout.findViewById<TextView>(R.id.a01_15saturday)
					layout.findViewById<TextView>(R.id.a01_15saturdaytext).setText(getString(R.string.saturday))
				}
				modCal.add(Calendar.DAY_OF_WEEK, -(modCal[Calendar.DAY_OF_WEEK] - 1) + i)
				tempText.setText(modCal[Calendar.DATE].toString())
				if (modCal[Calendar.YEAR] == now[Calendar.YEAR] && modCal[Calendar.DAY_OF_YEAR] == now[Calendar.DAY_OF_YEAR]) {
					if (selectIndex == -1) {
						selectIndex = i
					}
					if (i == selectIndex && add == 0) {
						tempText.setBackgroundResource(R.drawable.item_todayandselect)
						gd = tempText.getBackground() as GradientDrawable
						gd!!.setColor(A901Color.getIhwaColor())
						tempText.setTag(String.format("%d%02d%02d",
							modCal[Calendar.YEAR],
							modCal[Calendar.MONTH] + 1,
							modCal[Calendar.DATE]))
						onClick.onClick(tempText)
					} else {
						tempText.setBackgroundResource(R.drawable.item_today)
					}
				} else {
					if (i == selectIndex && add == 0) {
						tempText.setBackgroundResource(R.drawable.item_select)
						gd = tempText.getBackground() as GradientDrawable
						gd!!.setColor(A901Color.getIhwaColor())
						tempText.setTag(String.format("%d%02d%02d",
							modCal[Calendar.YEAR],
							modCal[Calendar.MONTH] + 1,
							modCal[Calendar.DATE]))
						onClick.onClick(tempText)
					} else {
						tempText.setBackgroundResource(0)
					}
				}
				tempText.setTag(null)
			}
		}

		inner class MyPagerViewHolder(val layout: View) : RecyclerView.ViewHolder(layout) {
			fun bind(position: Int) {
				try {


                    var a01_15sunday = layout.findViewById<TextView>(R.id.a01_15sunday)
                    var a01_15monday = layout.findViewById<TextView>(R.id.a01_15monday)
                    var a01_15tuesday = layout.findViewById<TextView>(R.id.a01_15tuesday)
                    var a01_15wednesday = layout.findViewById<TextView>(R.id.a01_15wednesday)
                    var a01_15thursday = layout.findViewById<TextView>(R.id.a01_15thursday)
                    var a01_15friday = layout.findViewById<TextView>(R.id.a01_15friday)
                    var a01_15saturday = layout.findViewById<TextView>(R.id.a01_15saturday)

                    a01_15sunday.setOnClickListener(onClick)
                    a01_15monday.setOnClickListener(onClick)
                    a01_15tuesday.setOnClickListener(onClick)
                    a01_15wednesday.setOnClickListener(onClick)
                    a01_15thursday.setOnClickListener(onClick)
                    a01_15friday.setOnClickListener(onClick)
                    a01_15saturday.setOnClickListener(onClick)
					layout.id = ID_ReadyLayout;

					if (position == 0) {
						prevLayout = layout
						updateWeek(currentWeek, prevLayout!!, -1)
					} else if (position == 1) {
						currentLayout = layout
						updateWeek(currentWeek, currentLayout!!, 0)
						onClick.onClick(layout)
					} else if (position == 2) {
						nextLayout = layout
						updateWeek(currentWeek, nextLayout!!, 1)
					}
				} catch (e: java.lang.Exception) {
					Env.error(e)
				}
			}
		}
	}


    val adapter=thisAdapter()
    inner class thisAdapter() : BaseAdapter(){
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()
	    var ID_scheduleSelect = View.generateViewId()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout:View
            try{
                layout=LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_15row,null,false)
	            var a01_15mainText =layout.findViewById<TextView>(R.id.a01_15mainText)
				var a01_15subText =layout.findViewById<TextView>(R.id.a01_15subText)
	            layout.setOnTouchListener { v, event ->

		            if(event.action==MotionEvent.ACTION_DOWN){
			            v.setBackgroundResource(R.drawable.bottomlineltgray)
		            }else if(event.action==MotionEvent.ACTION_UP||event.action==MotionEvent.ACTION_MOVE){
			            v.setBackgroundResource(R.drawable.bottomlinewhite)
		            }

		            false
	            }
	            layout.id=ID_scheduleSelect
	            layout.setOnClickListener(onClick)
	            val jo = JSONObject(jsonList[position])
	            layout.setTag(jo.getString("id"))
	            a01_15mainText.text = jo.getString("name")
	            val eventtype = jo.getString("eventtype")
	            if (eventtype == "site") {
		            a01_15subText.setTextColor(A901Color.rgb(0x34, 0xad, 0x81))
	            } else if (eventtype == "module") {
		            a01_15subText.setTextColor(A901Color.rgb(0xe3, 0x59, 0x2e))
	            } else if (eventtype == "user") {
		            a01_15subText.setTextColor(A901Color.rgb(0x39, 0xa9, 0xbf))
	            } else if (eventtype == "group") {
		            a01_15subText.setTextColor(A901Color.rgb(0xe5, 0xb8, 0x2e))
	            }
	            a01_15subText.text = Env.getLongToDate(applicationContext,
		            jo.getString("timestart")) + " ~ " + Env.getLongToDate(applicationContext,
		            java.lang.Long.toString(jo.getLong("timestart") + jo.getLong("timeduration")))
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