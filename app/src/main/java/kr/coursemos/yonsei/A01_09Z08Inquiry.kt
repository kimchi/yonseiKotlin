package kr.coursemos.yonsei

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.a00_top_layout.*
import kotlinx.android.synthetic.main.activity_a01_09_z08_inquiry.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.util.*

class A01_09Z08Inquiry : A00_00Activity() {
	private lateinit var inquiryNameList: Array<String?>
	private lateinit var inquiryIDList: Array<String?>
	private lateinit var picTypeDeleteList: Array<String?>
	private lateinit var picTypeList: Array<String?>
	private var position = 0
	private var isSend = false
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_a01_09_z08_inquiry)
		try {
			initBottom(getString(R.string.a01_09z08layoutTitle))
			a00_righttext.text = getString(R.string.a01_09z08layoutSend)
			a00_righttext.visibility = View.VISIBLE
			a00_righttext.setOnTouchListener(onTouch)
			a00_righttext.setOnClickListener(onClick)

			a01_09z08Clear.setOnTouchListener(onTouch)
			a01_09z08Clear.setOnClickListener(onClick)

			a01_09z08InquiryList.setOnTouchListener(onTouch)
			a01_09z08InquiryList.setOnClickListener(onClick)
			a01_09z08Pic.setOnTouchListener(onTouch)
			a01_09z08Pic.setOnClickListener(onClick)


			picTypeDeleteList = arrayOf<String?>(getString(R.string.a01_09z02camera),
				getString(R.string.a01_09z02gallery),
				getString(R.string.a01_10z01layoutDelete))
			picTypeList = arrayOf<String?>(getString(R.string.a01_09z02camera),
				getString(R.string.a01_09z02gallery))
			val cursor = cmdb.select(A903LMSTask.API_INQUIRYLIST,
				A903LMSTask.API_INQUIRYLIST,
				null,
				CMDB.TABLE01_common)
			inquiryNameList = arrayOfNulls(cursor.count)
			inquiryIDList = arrayOfNulls(cursor.count)
			var data: JSONObject? = null
			val isBiopassInit: Boolean = getEnvBoolean("BIOPASSINIT", false)
			val index = cursor.getColumnIndex(CMDB.COLUMNjson)
			var i = 0
			while (cursor.moveToNext()) {
				data = JSONObject(cursor.getString(index))
				inquiryNameList[i] = data!!.getString("categoryCode")
				inquiryIDList[i] = data!!.getString("cid")
				if (isBiopassInit && inquiryIDList[i] == "07") {
					position = i
					a01_09z08Content.setText(getString(R.string.a01_09z08request))
				}
				i++
			}
			a01_09z08InquiryList.setText(inquiryNameList[position])
			a01_09z08Email.setText(getEnv(StaticClass.KEY_EMAIL, ""))
		} catch (e: Exception) {
			EnvError(e)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		try {
			if (resultCode != RESULT_OK) return
			fileName = "test.png"
			if (requestCode == REQUEST_PICTURE) {
				filePath = StaticClass.pathExternalStorage + "/" + fileName

				a01_09z08Pic.setImageDrawable(getDrawable(438, 438, filePath))
			}
			//			if (requestCode == REQUEST_VIDEO) {
			//				filePath=getRealPathFromURI(data.getData());
			//
			//
			//			}
			if (requestCode == REQUEST_PHOTO_ALBUM) {
				filePath = getRealPathFromURI(applicationContext, data!!.data!!, fileName)
				a01_09z08Pic.setImageDrawable(getDrawable(438, 438, filePath))
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int,
	                                        permissions: Array<out String>,
	                                        grantResults: IntArray) {
		try {
			if (requestCode == 1000) {
				if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					photoAlbum()
				} else {
					alert(getString(R.string.a01_03subjectliststoragepermission))
				}
			} else if (requestCode == 2000) {
				if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					camera(fileName)
				} else {
					alert(getString(R.string.a01_03subjectlistcamerapermission))
				}
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	}

	@Throws(java.lang.Exception::class)
	private fun sendMultipart(filePath: String) {
		val shm = StringHashMap()
		shm["filePath"] = filePath
		shm["fileKey"] = "photo"
		shm["cid"] = inquiryIDList[position]
		shm["userEmail"] = a01_09z08Email.getText().toString()
		shm["content"] = a01_09z08Content.getText().toString()
		shm["schooltoken"] = getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "")
		shm["id"] = getEnv(StaticClass.KEY_ID, "")
		shm["token"] = getEnv(StaticClass.KEY_USERTOKEN, "")
		shm["appversion"] = StaticClass.version
		shm["deviceid"] = "2"
		shm["deviceinfo"] = Build.MODEL + "_" + Build.VERSION.RELEASE
		prd()
		object : A903LMSTask(applicationContext, API_INQUIRYSENDFILE, shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				dismiss()
				isAlertAction = true
				alert(getString(R.string.a01_09z08sendcomplete))
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				dismiss()
				errorCheckLogout(result, errorMsg)
				isSend = false
			}
		}
	}

	@Throws(java.lang.Exception::class)
	fun showInquiryList() {
		AlertDialog.Builder(this).setItems(inquiryNameList) { dialog, which ->
				try {
					position = which
					a01_09z08InquiryList.setText(inquiryNameList[position])
					if (inquiryIDList[position] == "07") {
						if (a01_09z08Content.getText().toString().equals("")) {
							a01_09z08Content.setText(getString(R.string.a01_09z08request))
						}
					} else {
						if (a01_09z08Content.getText().toString()
								.equals(getString(R.string.a01_09z08request))
						) {
							a01_09z08Content.setText("")
						}
					}
				} catch (e: java.lang.Exception) {
					Env.error("", e)
				}
			}.show()
	}

	override fun success(what: String?, jyc: CMCommunication?) {
		try {
			if (jyc == null) {
				val id = what!!.toInt()
				if (id == ID_alertOK) {
					if (isAlertAction) {
						onKeyDown(KeyEvent.KEYCODE_BACK, null)
					}
				} else if (id == ID_confirmOK) {
				} else if (id == ID_confirmCancel) {
				}
			}
		} catch (e: java.lang.Exception) {
		}
	}

	var onClick = View.OnClickListener { view ->
		try {
			val id = view.id
			if (id == R.id.a01_09z08Clear) {
				a01_09z08Email.setText("")
			} else if (id == R.id.a01_09z08InquiryList) {
				showInquiryList()
			} else if (id == R.id.a00_righttext) {
				isSend = if (isSend) {
					return@OnClickListener
				} else {
					true
				}
				if (a01_09z08Email.getText().toString().length === 0) {
					alert(getString(R.string.a01_09z08activityemailnot))
					isSend = false
				} else if (a01_09z08Content.getText().toString().length === 0) {
					alert(getString(R.string.a01_09z08activitycontentnot))
					isSend = false
				} else {
					if (filePath == null) {
						prd()
						val shm = StringHashMap()
						shm["cid"] = inquiryIDList[position]
						shm["userEmail"] = a01_09z08Email.getText().toString()
						shm["content"] = a01_09z08Content.getText().toString()
						shm["schooltoken"] = getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "")
						shm["id"] = getEnv(StaticClass.KEY_ID, "")
						shm["token"] = getEnv(StaticClass.KEY_USERTOKEN, "")
						shm["appversion"] = StaticClass.version
						shm["deviceid"] = "2"
						shm["idnumber"] = getEnv(StaticClass.KEY_IDNUMBER, "")
						shm["deviceinfo"] = Build.MODEL + "_" + Build.VERSION.RELEASE
						object : A903LMSTask(applicationContext, API_INQUIRYSEND, shm) {
							@Throws(java.lang.Exception::class)
							override fun success() {
								dismiss()
								isAlertAction = true
								alert(getString(R.string.a01_09z08sendcomplete))
							}

							@Throws(java.lang.Exception::class)
							override fun fail(errmsg: String) {
								dismiss()
								errorCheckLogout(result, errorMsg)
								isSend = false
							}
						}
					} else {
						Env.fileReSize(filePath,
							StaticClass.pathExternalStorage + "/resizeTemp.png")
						sendMultipart(StaticClass.pathExternalStorage + "/resizeTemp.png")
					}
				}
			} else if (id == R.id.a01_09z08Pic) {
				var tempList = picTypeDeleteList
				if (a01_09z08Pic.getDrawable() == null) {
					tempList = picTypeList
				}
				AlertDialog.Builder(me).setItems(tempList) { dialog, which ->
					try {
						fileName = "test.png"
						if (which == 0) {
							val arrayList = ArrayList<String>()
							val permissionCheck = ContextCompat.checkSelfPermission(
								applicationContext,
								Manifest.permission.CAMERA)
							if (permissionCheck == PackageManager.PERMISSION_DENIED) {
								if (ActivityCompat.shouldShowRequestPermissionRationale((me as Activity),
										Manifest.permission.CAMERA)
								) {
									ActivityCompat.requestPermissions((me as Activity),
										arrayOf(Manifest.permission.CAMERA),
										2000)
								} else {
									ActivityCompat.requestPermissions((me as Activity),
										arrayOf(Manifest.permission.CAMERA),
										2000)
								}
							} else {
								camera(fileName)
							}
						} else if (which == 1) {
							val arrayList = ArrayList<String>()
							val permissionCheck = ContextCompat.checkSelfPermission(
								applicationContext,
								Manifest.permission.READ_EXTERNAL_STORAGE)
							if (permissionCheck == PackageManager.PERMISSION_DENIED) {
								if (ActivityCompat.shouldShowRequestPermissionRationale((me as Activity),
										Manifest.permission.READ_EXTERNAL_STORAGE)
								) {
									ActivityCompat.requestPermissions((me as Activity),
										arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
										1000)
								} else {
									ActivityCompat.requestPermissions((me as Activity),
										arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
										1000)
								}
							} else {
								photoAlbum()
							}
						} else {
							a01_09z08Pic.setImageDrawable(null)
							filePath = null
						}
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
				}.show()
			} else {
				closeKeyboard()
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	} // OnClickListener onClick
	var onTouch = OnTouchListener { view, event ->
		try {
			val id = view.id
			if (event.action == MotionEvent.ACTION_DOWN) {
				if (id == R.id.a01_09z08Clear) {
					a01_09z08Clear.setBackgroundResource(R.drawable.qna_mail_box_del_button_push)
				} else if (id == R.id.a01_09z08InquiryList) {
					a01_09z08InquiryList.setBackgroundResource(R.drawable.qna_select_box_push)
				} else if (id == R.id.a01_09z08Pic) {
					val draw: Drawable = a01_09z08Pic.getBackground()
					draw.setColorFilter(A901Color.argb(0x55, A901Color.r, A901Color.g, A901Color.b),
						PorterDuff.Mode.SRC_ATOP)
					a01_09z08Pic.setBackground(draw)
				} else if (id == R.id.a00_righttext) {
					a00_righttext.setTextColor(A901Color.getIhwaColor())
				}
			} else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
				if (id == R.id.a01_09z08Clear) {
					a01_09z08Clear.setBackgroundResource(R.drawable.qna_mail_box_del_button)
				} else if (id == R.id.a01_09z08InquiryList) {
					a01_09z08InquiryList.setBackgroundResource(R.drawable.qna_select_box)
				} else if (id == R.id.a01_09z08Pic) {
					val draw: Drawable = a01_09z08Pic.getBackground()
					draw.colorFilter = null
					a01_09z08Pic.setBackground(draw)
				} else if (id == R.id.a00_righttext) {
					a00_righttext.setTextColor(A901Color.WHITE)
				}
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
		false
	} //OnTouchListener onTouch
}