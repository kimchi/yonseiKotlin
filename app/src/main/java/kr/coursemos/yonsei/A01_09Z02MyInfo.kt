package kr.coursemos.yonsei

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_a01_09_z02_my_info.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class A01_09Z02MyInfo : A00_00Activity() {
    private lateinit var picTypeList: Array<String?>
    private lateinit var userListName: Array<String?>
    private lateinit var  userList: JSONArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_09_z02_my_info)
        try{
            initBottom(getString(R.string.a01_09z02layoutTitle))
            a01_09Z02bottomtext.setTextColor(A901Color.getCustomOrange())
            a01_09Z02changeUser.setBackgroundColor(A901Color.getIhwaColor())
            a01_09Z02changeUser.setOnTouchListener(onTouch)
            a01_09Z02changeUser.setOnClickListener(onClick)
            a01_09Z02Pic.setOnTouchListener(onTouch)
            a01_09Z02Pic.setOnClickListener(onClick)


            userList = JSONArray(getEnv(StaticClass.KEY_ACCOUNT, "[]"))
            val size = userList.length()
            val arrayList = ArrayList<String>()
            val currentUser: String = getEnv(StaticClass.KEY_USERSEQ, "")
            var jo: JSONObject? = null
            for (i in 0 until size) {
                jo = userList.getJSONObject(i)
                if (currentUser == jo.getString("id")) {
                    arrayList.add(jo.getString("idnumber") + "(본인)")
                } else {
                    arrayList.add(jo.getString("idnumber"))
                }
            }
            if (arrayList.size == 0) {
                a01_09Z02changeUser.setVisibility(View.GONE)
            } else {
                userListName = arrayList.toTypedArray<String?>()
            }

            cmdb = CMDB.open(applicationContext)
            picTypeList = arrayOf<String?>(getString(R.string.a01_09z02camera),
                getString(R.string.a01_09z02gallery),
                getString(R.string.a01_09z03delete))
            val idInfo = cmdb.selectString(A903LMSTask.API_LOGIN,
                A903LMSTask.API_LOGIN,
                getEnv(StaticClass.KEY_SELECTUSERSEQ, ""),
                CMDB.TABLE01_common)
            if (idInfo == null) {
                onKeyDown(KeyEvent.KEYCODE_BACK, null)
            } else {
                jo = JSONObject(idInfo)
                Picasso.with(applicationContext).load(getEnv(StaticClass.KEY_PICURL, ""))
                    .transform(RoundTransform()).placeholder(android.R.color.transparent)
                    .error(android.R.color.transparent).into(a01_09Z02Pic)
                if (getEnvInt(StaticClass.KEY_LANGUAGE,
                        A01_09Z09Language.ID_row1) === A01_09Z09Language.ID_row2
                ) {
                    a01_09Z02Row1Text.setText(jo.getString("lastname"))
                } else {
                    a01_09Z02Row1Text.setText(jo.getString("firstname"))
                }
                a01_09Z02Row2Text.setText(jo.getString("userid"))
                a01_09Z02Row3Text.setText(jo.getString("institution"))
                a01_09Z02Row3Text.visibility=View.GONE
                if (jo.getString("idnumber").trim().length == 0) {
                    a01_09Z02Row4Text.visibility=View.GONE
                } else {
                    a01_09Z02Row4Text.setText(jo.getString("idnumber"))
                }
                a01_09Z02Row5Text.setText(jo.getString("institution") + " / " + jo.getString(
                    "department"))
                if (jo.isNull("phone2") || jo.getString("phone2") == "") {
                    if (jo.isNull("phone1")) {
                        a01_09Z02Row6Text.setText("")
                    } else {
                        a01_09Z02Row6Text.setText(jo.getString("phone1"))
                    }
                } else {
                    a01_09Z02Row6Text.setText(jo.getString("phone2"))
                }
                a01_09Z02Row7Text.setText(jo.getString("email"))
            }
        }catch (e:Exception){
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
                var myBitmap = BitmapFactory.decodeFile(filePath)
                var exif: ExifInterface? = null
                try {
                    exif = ExifInterface(filePath)
                } catch (e: IOException) {
                    Env.error("", e)
                }
                val exifOrientation: Int
                val exifDegree: Int
                if (exif == null) {
                    exifDegree = 0
                } else {
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)
                    exifDegree = exifOrientationToDegrees(exifOrientation)
                }
                myBitmap = rotate(myBitmap, exifDegree.toFloat())
                val out = FileOutputStream(filePath)
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.close()
                //				layout.a01_09Z02Pic.setImageBitmap(BitmapFactory.decodeFile(filePath));
                //				layout.a01_09Z02Pic.setImageResource(R.drawable.logo_mokpo);
                sendMultipart(filePath)
            }
            //
            //			if (requestCode == REQUEST_VIDEO) {
            //				filePath=getRealPathFromURI(data.getData());
            //			}
            if (requestCode == REQUEST_PHOTO_ALBUM) {
                filePath = getRealPathFromURI(applicationContext, data!!.data!!, fileName)
                var myBitmap = BitmapFactory.decodeFile(filePath)
                var exif: ExifInterface? = null
                try {
                    exif = ExifInterface(filePath)
                } catch (e: IOException) {
                    Env.error("", e)
                }
                val exifOrientation: Int
                val exifDegree: Int
                if (exif == null) {
                    exifDegree = 0
                } else {
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)
                    exifDegree = exifOrientationToDegrees(exifOrientation)
                }
                myBitmap = rotate(myBitmap, exifDegree.toFloat())
                val out = FileOutputStream(StaticClass.pathExternalStorage + "/" + fileName)
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.close()
                sendMultipart(StaticClass.pathExternalStorage + "/" + fileName)
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }

    }
    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }
    private fun rotate(bitmap: Bitmap, degree: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @Throws(java.lang.Exception::class)
    private fun sendMultipart(filePath: String?) {
        val shm = StringHashMap()
        shm["filePath"] = filePath
        prd()
        object : A903LMSTask(applicationContext, API_MULTIPART, shm) {
            @Throws(java.lang.Exception::class)
            override fun success() {
                object : A903LMSTask(context, API_GETUSERINFO, shm) {
                    @Throws(java.lang.Exception::class)
                    override fun success() {
                        dismiss()
                        Picasso.with(context).invalidate(sp.getString(StaticClass.KEY_PICURL, ""))
                        startActivity(A01_09Z02MyInfo::class.java, 0)
                    }

                    @Throws(java.lang.Exception::class)
                    override fun fail(errmsg: String) {
                        dismiss()
                        errorCheckLogout(result, errorMsg)
                    }
                }
                //				Picasso.with(context).invalidate(sp.getString(StaticClass.KEY_PICURL, ""));
                //				startActivity(A01_09Z02MyInfo.class,0);
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
                if (id == R.id.a01_09Z02changeUser) {
                    a01_09Z02changeUser.setBackgroundColor(A901Color.getIhwaColorDown())
                } else if (id == R.id.a01_09Z02Pic) {
                    val imageView: ImageView = view as ImageView
                    val draw: Drawable = imageView.getDrawable()
                    draw.setColorFilter(A901Color.argb(0x55, A901Color.r, A901Color.g, A901Color.b),
                        PorterDuff.Mode.SRC_ATOP)
                    imageView.setImageDrawable(draw)
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (id == R.id.a01_09Z02changeUser) {
                    a01_09Z02changeUser.setBackgroundColor(A901Color.getIhwaColor())
                } else if (id == R.id.a01_09Z02Pic) {
                    val imageView: ImageView = view as ImageView
                    val draw: Drawable = imageView.getDrawable()
                    draw.colorFilter = null
                    imageView.setImageDrawable(draw)
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch

    @Throws(java.lang.Exception::class)
    fun showAlert_changeUser() {
        AlertDialog.Builder(me).setItems(userListName) { dialog, which ->
            try {
                val shm = StringHashMap()
                shm["userid"] = userList.getJSONObject(which).getString("userid")
                prd()
                object : A903LMSTask(applicationContext, API_CHANGEUSER, shm) {
                    @Throws(java.lang.Exception::class)
                    override fun success() {
                        dismiss()
                        edit.putString(StaticClass.KEY_SELECTUSERID, pShm.getString("userid"))
                        edit.commit()
                        //							Picasso.with(context).invalidate(sp.getString(StaticClass.KEY_PICURL, ""));
                        startActivity(A01_09Z02MyInfo::class.java, 0)
                        object : A903LMSTask(context, API_PUSHLOGOUT, StringHashMap()) {
                            @Throws(java.lang.Exception::class)
                            override fun success() {
                                object : A903LMSTask(context, API_PUSHSET, null) {
                                    @Throws(java.lang.Exception::class)
                                    override fun success() {
                                    }

                                    @Throws(java.lang.Exception::class)
                                    override fun fail(errmsg: String) {
                                    }
                                }
                            }

                            @Throws(java.lang.Exception::class)
                            override fun fail(errmsg: String) {
                            }
                        }
                    }

                    @Throws(java.lang.Exception::class)
                    override fun fail(errmsg: String) {
                        dismiss()
                        errorCheckLogout(result, errorMsg)
                    }
                }
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
        }.show()
    }

    var onClick = View.OnClickListener { view ->
        try {
            val id = view.id
            if (id == R.id.a01_09Z02changeUser) {
                showAlert_changeUser()
            } else if (id == R.id.a01_09Z02Pic) {
                if (permissionCheck(Manifest.permission.CAMERA, 1000)) {
                    if (permissionCheck(Manifest.permission.READ_EXTERNAL_STORAGE, 2000)) {
                        showAlert()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    } // OnClickListener onClick

    @Throws(java.lang.Exception::class)
    fun permissionCheck(permissionType: String, requestCode: Int): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(applicationContext, permissionType)
        return if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((me as Activity),
                    permissionType)
            ) {
                //								Env.debug(context,"설명이 필요해");
                ActivityCompat.requestPermissions((me as Activity),
                    arrayOf(permissionType),
                    requestCode)
            } else {
                ActivityCompat.requestPermissions((me as Activity),
                    arrayOf(permissionType),
                    requestCode)
            }
            false
        } else {
            true
        }
    }

    @Throws(java.lang.Exception::class)
    fun showAlert() {
        AlertDialog.Builder(me).setItems(picTypeList) { dialog, which ->
            try {
                fileName = "test.png"
                if (which == 0) {
                    camera(fileName)
                } else if (which == 1) {
                    photoAlbum()
                } else if (which == 2) {
                    sendMultipart("")
                }
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        try {
            if (requestCode == 1000) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //					alert("카메라체크하자!!");
                    if (permissionCheck(Manifest.permission.READ_EXTERNAL_STORAGE, 2000)) {
                        showAlert()
                    }
                } else {
                    alert(getString(R.string.a01_03subjectlistcamerapermission))
                }
            } else if (requestCode == 2000) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAlert()
                } else {
                    alert(getString(R.string.a01_03subjectliststoragepermission))
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    }
}