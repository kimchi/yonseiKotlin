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
import kotlinx.android.synthetic.main.activity_a01_16_chatting_list.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.util.*

class A01_16ChattingList : A00_00Activity() {
	var isEnd = false
	var listPosition = 0
	var positionPaddingY = 0
	private var page = 1
	var beforeKeyword: String? = null
	private var beforeSize = 0
	private var isSending = false
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_a01_16_chatting_list)
		try {
			StaticClass.history.clear()
			StaticClass.history.add(this.javaClass)
			putEnvInt(StaticClass.KEY_SELECTMAINTAB, StaticClass.ID_BOTTOMTAB2)
			initBottom(getString(R.string.a01_16layoutTitle))



			adapter.lang = getEnvInt(StaticClass.KEY_LANGUAGE, A01_09Z09Language.ID_row1)
			a01_16List.adapter = adapter
			a01_16List.setOnScrollListener(object : AbsListView.OnScrollListener {
				var isNext = false
				override fun onScroll(view: AbsListView?,
				                      firstVisibleItem: Int,
				                      visibleItemCount: Int,
				                      totalItemCount: Int) {
					try {
						isNext = firstVisibleItem + visibleItemCount == totalItemCount
						positionPaddingY = if (view!!.getChildAt(0) == null) {
							0
						} else {
							val tempPaddingY = view.getChildAt(0).top
							tempPaddingY
						}
						listPosition = firstVisibleItem
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
				}

				override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
					try {
						if (isEnd) {
						} else {
							if ((scrollState == 2 || scrollState == 0) && isNext) {
							}
						}
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
				}
			})


			if (isBackKey) {
				showList()
				a01_16List.setSelectionFromTop(getEnvInt(StaticClass.KEY_CHATLISTCACHEPOSITIONY, 0),
					getEnvInt(StaticClass.KEY_CHATLISTCACHEPADDINGY, 0))
				request("")
			} else {
				showList()
				if (adapter.idList.size === 0) {
					prd()
				}
				request("")
			}

			a01_16refresh.setOnRefreshListener {
				beforeKeyword = null
				isEnd = false
				request("")
			}
		} catch (e: Exception) {
			EnvError(e)
		}
	}

	@Throws(java.lang.Exception::class)
	private fun showList() {
		val i = selectDB(0,
			A903LMSTask.API_LASTCHATTINGLIST,
			A903LMSTask.API_LASTCHATTINGLIST,
			"muid",
			CMDB.TABLE01_16ChattingList,
			adapter.idList,
			adapter.jsonList)
		deleteList(i, adapter.idList, adapter.jsonList, null)
		adapter.notifyDataSetChanged()
	} // showList();

	@Throws(java.lang.Exception::class)
	private fun clear(isDBClear: Boolean) {
		beforeSize = adapter.idList.size
		if (isDBClear) {
			adapter.idList.clear()
			adapter.jsonList.clear()
			cmdb = CMDB.open(applicationContext)
			cmdb.DBClear(CMDB.TABLE01_16ChattingList)
			beforeSize = 0
		}
	} //clear

	@Synchronized
	@Throws(java.lang.Exception::class)
	private fun request(keyword: String?) {
		if (isSending) {
			return
		}
		isSending = true
		val shm = StringHashMap()
		if (beforeKeyword == null) {
			page = 1
			beforeSize = 0
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
				beforeSize = 0
			}
		}
		beforeKeyword = keyword
		shm["courseid"] = ""
		shm["keyfield"] = ""
		shm["keyword"] = ""
		shm["page"] = Integer.toString(page)
		shm["ls"] = "30"
		if (prdDialog == null) {
			//			prd();
		}
		object : A903LMSTask(applicationContext, API_LASTCHATTINGLIST, shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				isSending = false
				if (pShm.getString("COUNT") == "0") {
					isEnd = true
				}
				if (isEnd) {
				} else {
					if (beforeSize == adapter.idList.size || adapter.idList.size < 15) {
						request(beforeKeyword)
						return
					}
				}
				if (page == 1) {
					adapter.idList.clear()
					adapter.jsonList.clear()
				}
				showList()
				a01_16refresh.isRefreshing = false
				dismiss()
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				isSending = false
				a01_16refresh.isRefreshing = false
				dismiss()
				errorCheckLogout(result, errorMsg)
			}
		}
	} //selectFriend

	var onClick: View.OnClickListener = View.OnClickListener {
		try {
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	}
	val adapter = thisAdapter()

	inner class thisAdapter() : BaseAdapter() {
		public var idList: ArrayList<String> = ArrayList()
		public var jsonList: ArrayList<String> = ArrayList()
		public var lang = 0;
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			lateinit var layout: View
			try {
				layout = LayoutInflater.from(applicationContext)
					.inflate(R.layout.list_a01_16row, null, false)
				var a01_16maintext = layout.findViewById<TextView>(R.id.a01_16maintext)
				var a01_16subtext = layout.findViewById<TextView>(R.id.a01_16subtext)
				var a01_16time = layout.findViewById<TextView>(R.id.a01_16time)
				var a01_16pic = layout.findViewById<ImageView>(R.id.a01_16pic)
				var a01_16new = layout.findViewById<ImageView>(R.id.a01_16new)
				a01_16time.setTextColor(A901Color.getIhwaColor())
				a01_16subtext.setTextColor(A901Color.GRAY);
				layout.setOnTouchListener { view, event ->
					try {
						if (event.action == MotionEvent.ACTION_DOWN) {
							view.setBackgroundColor(A901Color.LTGRAY)
						} else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
							view.setBackgroundResource(R.drawable.bottomlinewhite)
						}
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
					false
				}

				layout.setOnClickListener {
					val jo = JSONObject(jsonList.get(position))
					jo.put("message_type", 1)
					cmdb = CMDB.open(applicationContext)
					cmdb.update(A903LMSTask.API_LASTCHATTINGLIST,
						A903LMSTask.API_LASTCHATTINGLIST,
						adapter.idList.get(position),
						jo.toString(),
						CMDB.TABLE01_16ChattingList)
					setEnv(StaticClass.KEY_CHATTINGINFO, jo.toString())
					startActivity(A01_11Chatting::class.java, 0)
				}
				val jo = JSONObject(jsonList[position])
				if (lang == A01_09Z09Language.ID_row2) {
					a01_16maintext.setText(jo.getString("lastname"))
				} else {
					a01_16maintext.setText(jo.getString("fullname"))
				}
				a01_16time.setText(Env.getLongToDay(jo.getString("timecreated")))
				a01_16subtext.setText(jo.getString("fullmessage"))

				if (jo.getInt("message_type") == 0) {
					a01_16new.setImageResource(R.drawable.chattingnew)
					a01_16new.setVisibility(View.VISIBLE)
				} else {
					a01_16new.setVisibility(View.INVISIBLE)
				}

				Picasso.with(applicationContext).load(jo.getString("profileimageurl"))
					.transform(RoundTransform()).error(android.R.color.transparent).into(a01_16pic)

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