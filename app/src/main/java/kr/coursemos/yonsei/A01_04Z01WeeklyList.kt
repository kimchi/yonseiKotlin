package kr.coursemos.yonsei

import android.content.Intent
import android.database.Cursor
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.ImageGetter
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a51custometc.A905FileDownLoad
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import kr.coursemos.myapplication.a99util.htmlToString
import org.json.JSONObject
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [A01_04Z01WeeklyList.newInstance] factory method to
 * create an instance of this fragment.
 */
class A01_04Z01WeeklyList : Fragment() {
	// TODO: Rename and change types of parameters
	private var param1: String? = null
	private var param2: String? = null
	private var loadingCount = 0
	private var subjectID: String? = null
	lateinit var list: ListView
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			param1 = it.getString(ARG_PARAM1)
			param2 = it.getString(ARG_PARAM2)
		}
	}

	private var listPosition_weekly = 0
	private var positionPaddingY_weekly = 0
	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		var layout = inflater.inflate(R.layout.fragment_a01_04_z01_weekly_list, container, false)
		try {
			subjectID = A00_00Activity.me.getEnv(StaticClass.KEY_SUBJECTID, "")
			list = layout.findViewById<ListView>(R.id.a01_04weeklylist)
			list.adapter = adapter

			list.setOnScrollListener(object : AbsListView.OnScrollListener {
				override fun onScroll(view: AbsListView?,
				                      firstVisibleItem: Int,
				                      visibleItemCount: Int,
				                      totalItemCount: Int) {
					try {
						if (view!!.getChildAt(0) == null) {
							positionPaddingY_weekly = 0
						} else {
							val tempPaddingY = view.getChildAt(0).top
							positionPaddingY_weekly = tempPaddingY
						}
						listPosition_weekly = firstVisibleItem
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
				}

				override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
				}
			})

			if (A00_00Activity.me.isBackKey) {
				showList()
			} else {
				request()
			}
		} catch (e: java.lang.Exception) {
			EnvError(e)
		}
		return layout
	}

	companion object {
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param param1 Parameter 1.
		 * @param param2 Parameter 2.
		 * @return A new instance of fragment A01_04Z01WeeklyList.
		 */
		// TODO: Rename and change types and number of parameters
		@JvmStatic
		fun newInstance(param1: String, param2: String) = A01_04Z01WeeklyList().apply {
			arguments = Bundle().apply {
				putString(ARG_PARAM1, param1)
				putString(ARG_PARAM2, param2)
			}
		}
	}

	override fun onDestroyView() {
		try {
			A00_00Activity.me.setEnvInt(StaticClass.KEY_WEEKLYCACHEPOSITIONY, listPosition_weekly)
			A00_00Activity.me.setEnvInt(StaticClass.KEY_WEEKLYCACHEPADDINGY,
				positionPaddingY_weekly)

			A00_00Activity.me.setEnvInt(StaticClass.KEY_SUBJECTPOSITION, tempPosition)
		} catch (e: Exception) {
			EnvError(e)
		}

		super.onDestroyView()
	}

	@Throws(java.lang.Exception::class)
	private fun showList() {
		var i = A00_00Activity.me.selectDB(0,
			A903LMSTask.API_SUBJECTDETAIL,
			subjectID,
			CMDB.TABLE01_04SubjectDetail,
			adapter.idList,
			adapter.jsonList)
		var size: Int = adapter.detailList.size
		val current_semester = JSONObject(A00_00Activity.me.cmdb.selectString(A903LMSTask.API_HAKSA,
			A903LMSTask.API_HAKSA,
			StaticClass.KEY_CURRENTSEMESTER,
			CMDB.TABLE01_common))
		var tempPosition = current_semester.getInt("weeks")
		val cursor: Cursor = A00_00Activity.me.cmdb.select(A903LMSTask.API_SUBJECTDETAIL,
			subjectID,
			null,
			CMDB.TABLE01_04SubjectDetail)
		val index = cursor.getColumnIndex(CMDB.COLUMNjson)
		var j = 0
		while (cursor.moveToNext()) {
			val contentJson = JSONObject(cursor.getString(index))
			if (contentJson.getInt("currentsection") == 1) {
				tempPosition = j
				break
			}
			j++
		}
		if (adapter.getCount() <= tempPosition) {
			tempPosition = 0
		}
		adapter.currentWeek = tempPosition
		if (A00_00Activity.me.isBackKey) {
			tempPosition = A00_00Activity.me.getEnvInt(StaticClass.KEY_SUBJECTPOSITION, 0)
		}
		while (size < i) {
			if (size == tempPosition) {
				adapter.detailList.add(true)
			} else {
				adapter.detailList.add(false)
			}
			size++
		}
		A00_00Activity.me.deleteList(i, adapter.idList, adapter.jsonList, adapter.detailList)
		adapter.notifyDataSetChanged()
		if (A00_00Activity.me.isBackKey) {
			list.setSelectionFromTop(A00_00Activity.me.getEnvInt(StaticClass.KEY_WEEKLYCACHEPOSITIONY,
				0), A00_00Activity.me.getEnvInt(StaticClass.KEY_WEEKLYCACHEPADDINGY, 0))
		} else {
			list.setSelectionFromTop(tempPosition, 50)
		}
	}

	@Throws(java.lang.Exception::class)
	fun request() {
		A00_00Activity.me.prd()
		val shm = StringHashMap()
		shm["courseid"] = subjectID
		object : A903LMSTask(context, API_SUBJECTDETAIL, shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				A00_00Activity.me.dismiss()
				showList()
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				if (--loadingCount <= 0) {
					A00_00Activity.me.dismiss()
				}
				A00_00Activity.me.errorCheckLogout(result, errorMsg)
			}
		}
	}

	val adapter = thisAdapter()

	inner class thisAdapter() : BaseAdapter() {
		var idList: ArrayList<String> = ArrayList()
		var detailList: ArrayList<Boolean> = ArrayList()
		var jsonList: ArrayList<String> = ArrayList()
		var currentWeek = 0
		var ID_ubchat = View.generateViewId()
		var ID_book = View.generateViewId()
		var ID_econtents = View.generateViewId()
		var ID_adobeconnect = View.generateViewId()
		var ID_file = View.generateViewId()
		var ID_else = View.generateViewId()
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			lateinit var layout: View
			var joString = ""
			try {
				layout = LayoutInflater.from(context)
					.inflate(R.layout.list_a01_04_z01weekly, null, false)
				val a01_04weeklyheader =
					layout.findViewById<ConstraintLayout>(R.id.a01_04weeklyheader)
				val a01_04weeklyname = layout.findViewById<TextView>(R.id.a01_04weeklyname)
				val a01_04weeklydetailbutton =
					layout.findViewById<ImageView>(R.id.a01_04weeklydetailbutton)
				val a01_04weeklybottom =
					layout.findViewById<ConstraintLayout>(R.id.a01_04weeklybottom)
				val a01_04weeklydetailtext =
					layout.findViewById<TextView>(R.id.a01_04weeklydetailtext)
				a01_04weeklydetailtext.movementMethod = LinkMovementMethod.getInstance()
				var a01_04weeklyiconlist =
					layout.findViewById<LinearLayout>(R.id.a01_04weeklyiconlist)
				var isDetail = false
				var jo = JSONObject(jsonList[position])
				a01_04weeklyname.setText(jo.getString("name"))
				val summary = jo.getString("summary")
				if (summary == "") {
					a01_04weeklydetailtext.setVisibility(View.GONE)
				} else {
					isDetail = true
					a01_04weeklydetailtext.setVisibility(View.VISIBLE)
					a01_04weeklydetailtext.setText(Html.fromHtml(summary,
						ImageGetter { ColorDrawable(A901Color.WHITE) },
						null))
				}
				val ja = jo.getJSONArray("modules")
				val size = ja.length()
				var httpUrl: String? = null
				var params: LinearLayout.LayoutParams? = null
				var modname: String? = null

				if (size > 0) {
					isDetail = true
				}

				if (jo.getInt("visible") == 1) {
				} else {
					isDetail = false
				}

				if (isDetail) {
					if (position == 0) {
						a01_04weeklyheader.setBackgroundColor(A901Color.rgb(0xf5, 0xef, 0xd7))
					} else {
						a01_04weeklyheader.setBackgroundColor(A901Color.rgb(0xe4, 0xf0, 0xeb))
					}
					//                    a01_04weeklyheader.setOnTouchListener(onTouch)
					a01_04weeklyheader.setTag(position.toString())
					a01_04weeklyheader.setOnClickListener(onClick)
					a01_04weeklydetailbutton.setVisibility(View.VISIBLE)
					if (detailList[position]) {
						a01_04weeklybottom.getLayoutParams().height =
							ViewGroup.LayoutParams.WRAP_CONTENT
						val draw = context!!.resources.getDrawable(R.drawable.arrow_view_up)
						draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)
						a01_04weeklydetailbutton.setImageDrawable(draw)
					} else {
						a01_04weeklybottom.getLayoutParams().height = 0
						a01_04weeklydetailbutton.setImageResource(R.drawable.arrow_view_down)
					}
				} else {
					a01_04weeklyheader.setBackgroundColor(A901Color.rgb(0xe1, 0xe1, 0xe6))
					a01_04weeklyheader.setOnTouchListener(null)
					a01_04weeklyheader.setOnClickListener(null)
					layout.setOnClickListener(onClick)
					a01_04weeklydetailbutton.setVisibility(View.GONE)
				}
				lateinit var textLayout: View
				lateinit var imagetextLayout: View
				lateinit var a01_04weeklybottomtext: TextView
				lateinit var a01_04weeklybottomtext2: TextView
				lateinit var a01_04weeklybottomimage: ImageView
				for (i in 0 until size) {
					jo = ja.getJSONObject(i)
					joString = jo.toString()
					httpUrl = jo.getString("modicon")
					modname = jo.getString("modname")
					if (modname == "label") {
						textLayout = LayoutInflater.from(context)
							.inflate(R.layout.list_a01_04_z01bottomtext, null, false)
						a01_04weeklybottomtext =
							textLayout.findViewById<TextView>(R.id.a01_04weeklybottomtext)
						a01_04weeklybottomtext.setMovementMethod(LinkMovementMethod.getInstance())
						a01_04weeklyiconlist.addView(textLayout)
						a01_04weeklybottomtext.setText(Html.fromHtml(jo.getString("description"),
							ImageGetter { ColorDrawable(A901Color.WHITE) },
							null))
					} else {
						imagetextLayout = LayoutInflater.from(context)
							.inflate(R.layout.list_a01_04_z01bottomimagetext, null, false)
						a01_04weeklybottomtext2 =
							imagetextLayout.findViewById<TextView>(R.id.a01_04weeklybottomtext2)
						a01_04weeklybottomimage =
							imagetextLayout.findViewById<ImageView>(R.id.a01_04weeklybottomimage)
						a01_04weeklybottomtext2.setText(jo.getString("name").htmlToString())

						a01_04weeklyiconlist.addView(imagetextLayout)
						if (modname == "ubfile" || modname == "resource") {
							var modicon: Array<String?>? =
								jo.getString("modicon").split("/".toRegex()).toTypedArray()
							var name = modicon!![modicon!!.size - 1]
							modicon = null
							Picasso.with(context).load(httpUrl)
								.placeholder(android.R.color.transparent)
								.into(a01_04weeklybottomimage)
						} else {
							Picasso.with(context).load(httpUrl)
								.placeholder(android.R.color.transparent)
								.into(a01_04weeklybottomimage)
						}
						//					if (modname.equals("ubboard")||modname.equals("announcement")) {
						//						imageText.setId(ID_board);
						//						imageText.setTag(jo.getString("id"));
						//						imageText.setOnClickListener(onClick);
						//					} else
						if (modname == "ubchat") {
							imagetextLayout.setId(ID_ubchat)
							imagetextLayout.setTag(jo.getString("url") + "____" + jo.getString("name"))
							imagetextLayout.setOnClickListener(onClick)
						} else if (modname == "book") {
							imagetextLayout.setId(ID_book)
							imagetextLayout.setTag(jo.getString("url") + "____" + jo.getString("name"))
							imagetextLayout.setOnClickListener(onClick)
							if (jo.isNull("description")) {
							} else {
								textLayout = LayoutInflater.from(context)
									.inflate(R.layout.list_a01_04_z01bottomtext, null, false)
								a01_04weeklybottomtext =
									textLayout.findViewById<TextView>(R.id.a01_04weeklybottomtext)
								a01_04weeklybottomtext.setMovementMethod(LinkMovementMethod.getInstance())
								a01_04weeklyiconlist.addView(textLayout, params)
								a01_04weeklybottomtext.setText(jo.getString("description")
									.htmlToString())
							}
						} else if (modname == "folder") {
							if (jo.isNull("url")) {
								imagetextLayout.getLayoutParams().height = 0
							} else {
								imagetextLayout.setId(ID_else)
								imagetextLayout.setTag(jo.getString("url") + "____" + jo.getString("name") + "____")
								imagetextLayout.setOnClickListener(onClick)
							}
						} else if (modname == "econtents" || modname == "xncommons") {
							imagetextLayout.setId(ID_econtents)
							imagetextLayout.setTag(jo.getString("url") + "____" + jo.getString("name") + "____")
							imagetextLayout.setOnClickListener(onClick)
						} else if (modname == "adobeconnect") {
							imagetextLayout.setId(ID_adobeconnect)
							imagetextLayout.setTag(jo.getString("id"))
							imagetextLayout.setOnClickListener(onClick)
						} else if (modname == "url") {
							imagetextLayout.setId(ID_else)
							imagetextLayout.setTag(jo.getString("url") + "____" + jo.getString("name") + "____")
							imagetextLayout.setOnClickListener(onClick)
						} else {
							var isURL = false
							if (modname == "page" || modname == "folder") {
								isURL = true
							} else if (jo.isNull("contents")) {
								if (jo.isNull("url")) {
								} else {
									isURL = true
								}
							} else {
								if (!jo.isNull("display") && jo.getInt("display") == 6) {
									isURL = true
								} else {
									val contents = jo.getJSONArray("contents")
									var content: JSONObject? = null
									var contentSize = contents.length()
									var fileFullName: String? = null
									contentSize = 1
									for (j in 0 until contentSize) {
										content = contents.getJSONObject(j)
										if (content.getString("type") == "file") {
											fileFullName = content.getString("fileurl")
											isURL = false
											imagetextLayout.setTag(fileFullName + "____" + content.getString(
												"filename"))
											imagetextLayout.setId(ID_file)
											imagetextLayout.setOnClickListener(onClick)
										} else {
											imagetextLayout.setTag(content.getString("fileurl") + "____" + jo.getString(
												"name") + "____")
											imagetextLayout.setId(ID_else)
											imagetextLayout.setOnClickListener(onClick)
											continue
										}
									}
								}
							}
							if (isURL) {
								imagetextLayout.setId(ID_else)
								imagetextLayout.setTag(jo.getString("url") + "____" + jo.getString("name") + "____")
								imagetextLayout.setOnClickListener(onClick)
							}
						}
					}
				}
			} catch (e: Exception) {
				EnvError(e, joString)
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

	var tempPosition = 0
	var onClick: View.OnClickListener = object : View.OnClickListener {
		override fun onClick(view: View) {
			try {
				val id = view.id
				if (id == R.id.a01_04weeklyheader) {
					val position: Int = (view.tag as String)!!.toInt()
					if (tempPosition == position) {
					} else {
						if (tempPosition < adapter.detailList.size) {
							adapter.detailList.set(tempPosition, false)
						}
						adapter.notifyDataSetChanged()
						tempPosition = position
					}
					val parent = view.parent as View
					val tempView = parent.findViewById<View>(R.id.a01_04weeklybottom)
					val tempParams = tempView.layoutParams
					val detailButton: ImageView = parent.findViewById(R.id.a01_04weeklydetailbutton)
					if (tempParams.height == 0) {
						tempParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
						adapter.detailList.set(tempPosition, true)
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
							list.smoothScrollToPositionFromTop(position, 50)
						} else {
							list.setSelectionFromTop(position, 50)
						}
						val draw = context!!.resources.getDrawable(R.drawable.arrow_view_up)
						draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)
						detailButton.setImageDrawable(draw)
					} else {
						tempParams.height = 0
						adapter.detailList.set(tempPosition, false)
						detailButton.setImageResource(R.drawable.arrow_view_down)
					}


					tempView.requestLayout()
				} else if (id == adapter.ID_ubchat) {
					val url____name = (view.tag as String).split("____".toRegex()).toTypedArray()
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBURL, url____name[0])
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBVIEWNAME, url____name[1])
					A00_00Activity.me.startActivity(A01_13WebView::class.java, 0)
				} else if (id == adapter.ID_book || id == adapter.ID_else) {
					val url____name = (view.tag as String).split("____".toRegex()).toTypedArray()
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBURL, url____name[0])
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBVIEWNAME, url____name[1])
					A00_00Activity.me.startActivity(A01_13WebView::class.java, 0)
				} else if (id == adapter.ID_econtents) {
					val url____name = (view.tag as String).split("____".toRegex()).toTypedArray()
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBURL, url____name[0])
					A00_00Activity.me.putEnv(StaticClass.KEY_WEBVIEWNAME, url____name[1])
					A00_00Activity.me.startActivity(A01_13Z02WebView::class.java, 0)
				} else if (id == adapter.ID_adobeconnect) {
					var adobeConnectID = view.tag as String
					val shm = StringHashMap()
					shm["cmid"] = adobeConnectID
					A00_00Activity.me.prd()
					object : A903LMSTask(context, API_ADOBECONNECT, shm) {
						@Throws(java.lang.Exception::class)
						override fun success() {
							A00_00Activity.me.dismiss()
							A00_00Activity.me.startActivity(A01_04Z01AdobeConnect::class.java, 0)
						}

						@Throws(java.lang.Exception::class)
						override fun fail(errmsg: String) {
							A00_00Activity.me.dismiss()
							A00_00Activity.me.errorCheckLogout(result, errorMsg)
						}
					}
				} else if (id == adapter.ID_file) {
					val url____name = (view.tag as String).split("____".toRegex()).toTypedArray()
					val shm = StringHashMap()
					shm["token"] = A00_00Activity.me.getEnv(StaticClass.KEY_USERTOKEN, "")
					A00_00Activity.me.prd()
					object : A905FileDownLoad(context, url____name[0], shm, url____name[1], null) {
						@Throws(java.lang.Exception::class)
						override fun success(msg: String) {
							A00_00Activity.me.dismiss()
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
								val photoURI = FileProvider.getUriForFile(context,
									BuildConfig.APPLICATION_ID + ".provider",
									File(StaticClass.pathExternalStorage + fileName))
								intent.setDataAndType(photoURI, filetype)
							} else {
								intent.setDataAndType(Uri.fromFile(File(StaticClass.pathExternalStorage + fileName)),
									filetype)
							}
							intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
							try {
								startActivity(intent)
							} catch (e: java.lang.Exception) {
								Env.error("", e)
								A00_00Activity.me.alert(getString(R.string.a01_04activityFileOpenFail))
							}
						}

						@Throws(java.lang.Exception::class)
						override fun fail(errmsg: String) {
							A00_00Activity.me.dismiss()
							A00_00Activity.me.alert(result, errorMsg)
						}
					}
				}
			} catch (e: java.lang.Exception) {
				Env.error("", e)
			}
		}
	} // OnClickListener onClick
}