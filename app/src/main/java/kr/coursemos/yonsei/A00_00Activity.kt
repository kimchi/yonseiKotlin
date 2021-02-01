package kr.co.okins.kc

import android.annotation.TargetApi
import android.app.Dialog
import android.content.*
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import kr.coursemos.yonsei.*
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.myapplication.a99util.EnvDebug
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

open class A00_00Activity : AppCompatActivity() {

    companion object {
        lateinit var me: A00_00Activity
        var recvMessage: Handler? = null
        var alarmCount: TextView? = null
        var chatCount: TextView? = null
    }

    lateinit var cmdb: CMDB
    protected var isAlertAction = false
    var isLogout = false
    protected var isExit = false
    protected var ID_confirmOK = 11
    protected var ID_confirmCancel = 12
    protected var ID_alertOK = 13
    var isBackKey = false
    var shm_param: StringHashMap = StringHashMap()
    var language: Int = 0
    lateinit var colorStateList: ColorStateList

    protected var REQUEST_VIDEO = 3
    protected var REQUEST_VIDEO_ALBUM = 4
    protected var REQUEST_PICTURE = 1
    protected var REQUEST_PHOTO_ALBUM = 2
    protected var REQUEST_FILE = 5
    protected var filePath: String? = null
    protected var fileName = "android"
    val finishRecv2 = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when {
                intent?.action == "dalbit.yonsei.finish" -> {
                    EnvDebug(this@A00_00Activity.localClassName + " die  ")
                    unregisterReceiver(this)
                    this@A00_00Activity.finish()
                }

            }
        }
    }

    override fun onResume() {
        try {
            if (StaticClass.memoryCheck == null) {
                StaticClass.memoryCheck = "memoryCheck"
                val className = this.javaClass.name
                if (className == A01_00Intro::class.java.name || className == A01_02Z91LoginSeoul::class.java.name) {
                } else {
                    startActivity(A01_00Intro::class.java, 0)
                    super.onResume()
                    return
                }
            } else {
            }
            if (A00_00Activity.recvMessage == null) {
                A00_00Activity.recvMessage = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        try {
                            val recvMsg = msg.obj as String
                            if (recvMsg == "COUNT") {
                                //                            	edit.putString(StaticClass.KEY_NOTICOUNT,new Random().nextInt(100)+"");
                                //								edit.putString(StaticClass.KEY_CHATCOUNT,new Random().nextInt(100)+"");
                                //								edit.commit();
                                //								Env.debug(context,sp.getString(StaticClass.KEY_NOTICOUNT,"0")+"!!"+sp.getString(StaticClass.KEY_CHATCOUNT,"0"));
                                setCount()
                            }
                        } catch (e: java.lang.Exception) {
                            Env.error("", e)
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        super.onResume()
    }

    @Throws(java.lang.Exception::class)
    private fun setCount() {
        if (alarmCount != null) {


            val notiCount: String = getEnv(StaticClass.KEY_NOTICOUNT, "0")
            if (notiCount == "0") {
                alarmCount?.setVisibility(View.GONE)
            } else {
                alarmCount?.setText(notiCount)
                alarmCount?.setVisibility(View.VISIBLE)
            }
            val chatCountString: String = getEnv(StaticClass.KEY_CHATCOUNT, "0")
            if (chatCountString == "0") {
                chatCount?.setVisibility(View.GONE);
            } else {
                chatCount?.setText(chatCountString)
                chatCount?.setVisibility(View.GONE);
//                chatCount?.setVisibility(View.VISIBLE);
            }

        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        closeKeyboard()
        super.onConfigurationChanged(newConfig)
    }

    fun initBottom(titleString: String = "") {
        try {

            val title = findViewById<TextView>(R.id.a00_title)
            val back = findViewById<ImageView>(R.id.a00_back)
            when (title) {
                null -> {
                }
                else -> {
                    title.setText(titleString)
                    back.setOnClickListener {
                        onKeyDown(KeyEvent.KEYCODE_BACK, null)
                    }


                }
            }
            var btn = findViewById<BottomNavigationView>(R.id.a00bottomlayout)
            when (btn) {
                null -> {
                }
                else -> {
                    btn.itemTextColor = colorStateList
                    btn.itemIconTintList = colorStateList

                    var test = btn.getChildAt(0) as BottomNavigationMenuView
                    var chat = LayoutInflater.from(this).inflate(R.layout.round_badge, test, false)
                    chatCount=chat.findViewById<TextView>(R.id.a00alarmcount)
                    chatCount?.setText("11")
                    var itemView = test.getChildAt(1) as BottomNavigationItemView
                    itemView.addView(chat)




                    if(this.javaClass.name.equals(A01_20Alarm::class.java.name)){
                    }else{
                        chat = LayoutInflater.from(this).inflate(R.layout.round_badge, test, false)
                        alarmCount=chat.findViewById<TextView>(R.id.a00alarmcount)
                        alarmCount?.setText("22")
                        itemView = test.getChildAt(2) as BottomNavigationItemView
                        itemView.addView(chat)
                    }
                    setCount()

                    when(getEnvInt(StaticClass.KEY_SELECTMAINTAB,StaticClass.ID_BOTTOMTAB1)){
                        StaticClass.ID_BOTTOMTAB1 ->{
                            btn.selectedItemId=R.id.a00home
                        }
                        StaticClass.ID_BOTTOMTAB2->{
                            btn.selectedItemId=R.id.a00message
                        }
                        StaticClass.ID_BOTTOMTAB3->{
                            btn.selectedItemId=R.id.a00alarm
                        }
                        StaticClass.ID_BOTTOMTAB4->{
                            btn.selectedItemId=R.id.a00cal
                        }
                        StaticClass.ID_BOTTOMTAB5->{
                            btn.selectedItemId=R.id.a00setting
                        }
                    }

                    btn.setOnNavigationItemSelectedListener {
                        when(it.itemId){
                            R.id.a00home->{
                                if(this.javaClass.name.equals(A01_03SubjectList::class.java.name)){

                                }else{
                                    startActivity(A01_03SubjectList::class.java)
                                }
                            }

                            R.id.a00message->{
                                if(this.javaClass.name.equals(A01_16ChattingList::class.java.name)){

                                }else{
                                    startActivity(A01_16ChattingList::class.java)
                                }
                            }

                            R.id.a00alarm->{
                                if(this.javaClass.name.equals(A01_20Alarm::class.java.name)){

                                }else{
                                    startActivity(A01_20Alarm::class.java)
                                }
                            }

                            R.id.a00cal->{
                                if(this.javaClass.name.equals(A01_15Schedule::class.java.name)){

                                }else{
                                    startActivity(A01_15Schedule::class.java)
                                }
                            }
                            R.id.a00setting->{
                                if(this.javaClass.name.equals(A01_09Setting::class.java.name)){

                                }else{
                                    startActivity(A01_09Setting::class.java)
                                }
                            }

                        }
                        true
                    }
                }
            }
        } catch (e: Exception) {
            EnvError(e)
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            me = this
            FirebaseApp.initializeApp(applicationContext)






            if (getEnv(StaticClass.KEY_DEBUGLASTTIME, "0")
                    .toLong() > Env.getTodateAddDay("yyyyMMddHHmmss", 0).toLong()
            ) {
                StaticClass.mydebug = getEnv(StaticClass.KEY_DEBUGURL, StaticClass.mydebug)
                StaticClass.isDebug = true
                StaticClass.isReal = false
            } else {
                StaticClass.isDebug = false
                StaticClass.isReal = true
            }

            cmdb = CMDB.open(applicationContext)


            val intentFilter = IntentFilter("dalbit.yonsei.finish")
            var kill = intent.getStringExtra(StaticClass.KEY_ACTIVITYALLKILL)
            if (kill is String && kill == StaticClass.YES) {
                sendBroadcast(Intent("dalbit.yonsei.finish"), "dalbit.yonsei.finish")
            }
            registerReceiver(finishRecv2, intentFilter)
            val currentName = this.javaClass.name



            A901Color.setMainColor(
                getEnv(
                    StaticClass.KEY_SELECTSCHOOLMAINCOLOR,
                    ""
                )
            )
            colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ), intArrayOf(
                    A901Color.getIhwaColor(),
                    A901Color.LTGRAY
                )
            )
//            window.statusBarColor=A901Color.TRANSPARENT

            language = getEnvInt(StaticClass.KEY_LANGUAGE, 0)
            softKeyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            for (i in 0..StaticClass.param.size - 1) {
                shm_param.put(StaticClass.param[i], intent.getStringExtra(StaticClass.param[i]))
            }

            //			TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            val isBack = shm_param.getString(StaticClass.param[1])
            shm_param[StaticClass.param[1]] = StaticClass.NO
            if (isBack != null && isBack == StaticClass.YES) {
                isBackKey = true
            }

            Env.debug(
                this,
                "client -> client : " + shm_param.getString(
                    StaticClass.param[2]
                ) + " -> " + this.javaClass.name + "\n\n"
            )


            var locale: Locale? = null

            lateinit var localLang:String
            if (language == A01_09Z09Language.ID_row1) {
//                locale = Locale("ko")
                localLang="ko"
            } else if (language == A01_09Z09Language.ID_row2) {
//                locale = Locale("en")
                localLang="en"
            } else if (language == A01_09Z09Language.ID_row3) {
//                locale = Locale("ja")
                localLang="ja"
            } else {
                locale = Locale.getDefault()
                localLang = locale.language
                if (localLang == "ko") {
                    putEnvInt(
                        StaticClass.KEY_LANGUAGE,
                        A01_09Z09Language.ID_row1
                    )
                } else if (localLang == "en") {
                    putEnvInt(
                        StaticClass.KEY_LANGUAGE,
                        A01_09Z09Language.ID_row2
                    )
                } else if (localLang == "ja") {
                    putEnvInt(
                        StaticClass.KEY_LANGUAGE,
                        A01_09Z09Language.ID_row3
                    )
                }
            }

            setLanguage(applicationContext,localLang)
//            Locale.setDefault(locale)
//            val config = Configuration()
//            config.locale = locale
//            baseContext.resources
//                .updateConfiguration(config, baseContext.resources.displayMetrics)


//            Common.setLanguage(
//                it,
//                PreferenceManager.getPref<String>(Constants.LANGUAGE_PREFERENCE).toString()
//            )

            if (StaticClass.pathExternalStorage == null) {
                StaticClass.pathExternalStorage =
                    getExternalFilesDir(getString(R.string.app_name)).toString() + "/"
                StaticClass.version = packageManager
                    .getPackageInfo(packageName, 0).versionName
                var dir: File? =
                    File(StaticClass.pathExternalStorage)
                if (dir!!.exists()) {
                } else {
                    dir!!.mkdir()
                }
                dir = null
            }

            when (StaticClass.history) {
                null -> {
                    StaticClass.history = ArrayList(mutableListOf<Class<*>>())
                    StaticClass.history.add(this.javaClass)
                }
                else -> {
                    when {
                        StaticClass.history.size == 0 -> {
                            StaticClass.history.add(this.javaClass)
                        }
                        StaticClass.history.get(StaticClass.history.size - 1).name.equals(
                            currentName
                        ) -> {
                        }
                        currentName.equals(A01_03SubjectList::class.java.name) -> {
                            StaticClass.history.clear()
                        }
                        else -> {
                            StaticClass.history.add(this.javaClass)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            EnvError(e)
        }

    }


    fun setLanguage(context: Context, language: String): ContextWrapper {
        var mContext = context
        val localeLang = language.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val locale: Locale
        if (localeLang.size > 1)
            locale = Locale(localeLang[0], localeLang[1])
        else
            locale = Locale(localeLang[0])
        val res = mContext.resources
        var configuration = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
//            configuration.locales = localeList
            mContext = mContext.createConfigurationContext(configuration)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
            mContext = mContext.createConfigurationContext(configuration)
        } else {
            configuration.locale = locale
            res.updateConfiguration(configuration, res.getDisplayMetrics())
        }
        return ContextWrapper(mContext)
    }

    var exitTime: Long = 0;
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                when (StaticClass.history) {
                    null -> {
                        startActivity(A00_01Login::class.java)
                    }
                    else -> {
                        when {
                            StaticClass.history.size > 1 -> {
                                shm_param[StaticClass.param[1]] = StaticClass.YES
                                StaticClass.history.removeAt(StaticClass.history.size - 1)
                                val cls = StaticClass.history.get(StaticClass.history.size - 1)
                                StaticClass.history.removeAt(StaticClass.history.size - 1)
                                startActivity(cls)
                            }
                            else -> {
                                val currentName = this.javaClass.name
                                when {
                                    currentName.equals(A00_01Login::class.java.name) || currentName.equals(
                                        A01_00Intro::class.java.name
                                    ) || currentName.equals(A01_01Scheme::class.java.name) || currentName.equals(
                                        A01_03SubjectList::class.java.name
                                    ) -> {

                                        val now = System.currentTimeMillis()
                                        if (0 < exitTime && exitTime > now - 3000) {
                                            finish();
                                        } else {
                                            exitTime = now;
                                            toast("뒤로 가기 버튼을 한번 더 누르면 앱이 종료됩니다.")
                                        }
                                    }
                                    else -> {
                                        startActivity(A01_03SubjectList::class.java)
                                    }
                                }
                            }
                        }

                    }

                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun alert(
        msg: String,
        type: String = "",
        subject: String = applicationContext.getString(R.string.app_name)
    ) {
        try {
            var alert = AlertDialog.Builder(this)
            alert.setTitle(subject)
            alert.setMessage(msg)
            alert.setPositiveButton(getString(R.string.a00_00activityAlertOK)) { _, _ ->
                try {
                    if (isLogout) {
                        val shm = StringHashMap()
                        shm["id"] = getEnv(
                            StaticClass.KEY_USERSEQ,
                            ""
                        )
                        prd()
                        object :
                            A903LMSTask(this@A00_00Activity, API_LOGOUT, shm) {
                            @Throws(java.lang.Exception::class)
                            override fun success() {
                                dismiss()
                                startActivity(A01_02Login::class.java, 0)
                            }

                            @Throws(java.lang.Exception::class)
                            override fun fail(errmsg: String) {
                                dismiss()
                                errorCheckLogout(result, errorMsg)
                            }
                        }
                    } else {
                        alertResult(Integer.toString(ID_alertOK))
                    }
                } catch (e: java.lang.Exception) {
                    Env.error("", e)
                }
            }
            alert.show()
        } catch (e: java.lang.Exception) {
            EnvError(e)
        }
    }
    open fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    open fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    open fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    open fun isDown(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.photos.contentprovider" == uri.authority || "com.dropbox.android.FileCache" == uri.authority
    }
    open fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    // 사진 촬영
    protected open fun camera(fileName: String?) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file =
            File(StaticClass.pathExternalStorage, fileName)
        val uri = FileProvider.getUriForFile(
            this,
            "kr.coursemos.yonsei.provider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_PICTURE)
    } // takePicture


    protected open fun photoAlbum() {
        val pickIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        val takePicture = Intent(Intent.ACTION_GET_CONTENT)
        takePicture.type = "image/*"
        val dropboxIntent = Intent(Intent.ACTION_GET_CONTENT)
        dropboxIntent.setPackage("com.dropbox.android")
        dropboxIntent.type = "image/*"
        val chooserIntent = Intent.createChooser(pickIntent, "Select File")
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            arrayOf(takePicture, dropboxIntent)
        )
        startActivityForResult(chooserIntent, REQUEST_PHOTO_ALBUM)
    } // photoAlbum

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Throws(java.lang.Exception::class)
    open fun getRealPathFromURI(
        context: Context,
        uri: Uri,
        fileName: String
    ): String? {
        var fileName = fileName
        var isDown = false
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            } else if (isDown(uri)) {
                isDown = true
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            isDown = if (isDown(uri)) {
                true
            } else {
                return getDataColumn(context, uri, null, null)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        if (isDown) {
            fileName = fileName.split("\\.".toRegex()).toTypedArray()[0]
            var filePath: String? = null
            var file: File? = null
            var out: FileOutputStream? = null
            val returnCursor =
                context.contentResolver.query(uri, null, null, null, null)
            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            //            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor!!.moveToFirst()
            fileName = returnCursor!!.getString(nameIndex)
            //            int size=Integer.parseInt(returnCursor.getString(sizeIndex)) ;
            returnCursor!!.close()


//            try{
//                String mimeType=context.getContentResolver().getType(uri);
//                Env.debug(context,"mimeType : "+mimeType);
//                if(mimeType.equals("image/jpeg")){
//                    fileName=fileName+".jpg";
//                }else if(mimeType.equals("image/png")){
//                    fileName=fileName+".png";
//                }else if(mimeType.equals("image/gif")){
//                    fileName=fileName+".gif";
//                }else if(mimeType.equals("image/tiff")){
//                    fileName=fileName+".tiff";
//                }else if(mimeType.equals("application/pdf")){
//                    fileName=fileName+".pdf";
//                }else if(mimeType.equals("application/vnd")){
//                    fileName=fileName+".vnd";
//                }else if(mimeType.equals("text/plain")){
//                    fileName=fileName+".txt";
//                }
//            }catch (Exception e){
//                Env.error("ext!!!!",e);
//            }
            val fd =
                context.contentResolver.openFileDescriptor(uri, "r")!!.fileDescriptor
            //            InputStream in=context.getContentResolver().openInputStream(uri);
            val `in`: InputStream = FileInputStream(fd)
            filePath =
                StaticClass.pathExternalStorage + "/" + fileName
            file = File(filePath)
            out = FileOutputStream(file)
            val b = ByteArray(1024)
            var len = 0
            while (`in`.read(b).also { len = it } >= 0) {
                out.write(b, 0, len)
            }
            `in`.close()
            out.close()
            return filePath
        }
        return null
    }

    protected open fun getDrawable(
        displayWidth: Int,
        displayHeight: Int,
        imgFilePath: String?
    ): Drawable? {
        val file = File(imgFilePath)
        try {
            if (file.isFile) {
                var `is`: FileInputStream? = FileInputStream(file)
                val draw = Drawable.createFromResourceStream(
                    null,
                    null,
                    `is`,
                    "picture",
                    StaticClass.getBitmapOption(
                        displayWidth,
                        displayHeight,
                        imgFilePath
                    )
                )
                `is`!!.close()
                `is` = null
                return draw
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        return null
    } // getDrawable


    fun confirm(
        msg: String,
        type: String = "",
        subject: String = applicationContext.getString(R.string.app_name)
    ) {
        try {
            var alert = AlertDialog.Builder(this)
            alert.setTitle(subject)
            alert.setMessage(msg)
            alert.setPositiveButton(getString(R.string.a00_00activityConfirmOK)) { _, _ ->
                try {
                    if (isExit) {
                        finish()
                    } else {
                        alertResult(Integer.toString(ID_confirmOK))
                    }
                } catch (e: java.lang.Exception) {
                    Env.error("", e)
                }
            }
            alert.setNegativeButton(getString(R.string.a00_00activityConfirmCancel)) { _, _ ->
                try {
                    if (isExit) {
                        isExit = false
                    } else {
                        alertResult(Integer.toString(ID_confirmCancel))
                    }
                } catch (e: java.lang.Exception) {
                    Env.error("", e)
                }
            }
            alert.show()
        } catch (e: java.lang.Exception) {
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    open fun errorCheckLogout(
        result: String?,
        errorMsg: String?
    ) {
        var errorMsg = errorMsg
        val jo = JSONObject(result)
        if (jo.isNull("errorcode")) {
        } else {
            val errorcode = jo.getString("errorcode")
            if (errorcode == "invalidtoken") {
                isLogout = true
                errorMsg = applicationContext.getString(R.string.invalidtoken)
            }
        }
        alert(errorMsg!!)
    }

    @Throws(java.lang.Exception::class)
    open fun alertResult(id: String?) {
        if (isERROR) {
//			logout();
        } else {
            success(id, null)
        }
    } // alertResult


    protected open fun success(what: String?, jyc: CMCommunication?) {
        try {
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    } // success(String what, CMCommunication jyc)


    @Throws(java.lang.Exception::class)
    open fun getAlphaAnimation(isVisible: Boolean): Animation {
        return getAlphaAnimation(isVisible, 300)
    }

    @Throws(java.lang.Exception::class)
    open fun getAlphaAnimation(
        isVisible: Boolean,
        speed: Int
    ): Animation {
        var anim: AlphaAnimation= if (isVisible) {
            AlphaAnimation(0.0f, 1.0f)
        } else {
            AlphaAnimation(1.0f, 0.0f)
        }
        anim.duration = speed.toLong()
        anim.fillAfter = true
        anim.interpolator = AnimationUtils.loadInterpolator(
            this,
            android.R.anim.accelerate_interpolator
        )
        return anim
    }
    val preference by lazy {
        getSharedPreferences(
            StaticClass.KEY_SharedPreferences,
            Context.MODE_PRIVATE
        )
    }

    fun setEnv(key: String, value: String) {

        preference.edit().putString(key, value).apply()
    }

    fun putEnv(key: String, value: String) {
        setEnv(key, value)
    }

    fun getEnv(key: String, default: String = ""): String {
        return preference.getString(key, default) ?: ""
    }


    fun setEnvBoolean(key: String, value: Boolean) {
        preference.edit().putBoolean(key, value).apply()
    }

    fun putEnvBoolean(key: String, value: Boolean) {
        setEnvBoolean(key, value)
    }

    fun getEnvBoolean(key: String, default: Boolean = false): Boolean {
        return preference.getBoolean(key, default)
    }

    fun setEnvInt(key: String, value: Int) {
        preference.edit().putInt(key, value).apply()
    }

    fun putEnvInt(key: String, value: Int) {
        setEnvInt(key, value)
    }

    fun getEnvInt(key: String, default: Int = 0): Int {
        return preference.getInt(key, default)
    }

    fun removeEnv(key: String) {
        preference.edit().remove(key).apply()
    }

    fun clearEnv() {
        preference.edit().clear().apply()
    }

    fun toast(msg: String, time: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(applicationContext, msg, time).show();
    }

    fun startActivity(cls: Class<*>, anim: Int = 0) {
        var intent = Intent(applicationContext, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtra(StaticClass.param[0], this.javaClass.name)
        intent.putExtra(StaticClass.KEY_ACTIVITYALLKILL,StaticClass.YES)
        for (i in 1 until StaticClass.param.size) {
            intent.putExtra(
                StaticClass.param[i],
                shm_param.getString(StaticClass.param[i])
            )
        }
        intent.putExtra(StaticClass.param[2], this.javaClass.name)
        startActivity(intent)
    }


    @Throws(java.lang.Exception::class)
    open fun selectDB(
        startIndex: Int,
        type1: String?,
        type2: String?,
        table: String?,
        idList: java.util.ArrayList<String>,
        jsonList: java.util.ArrayList<String>
    ): Int {
        return selectDB(startIndex, type1, type2, "id", table, idList, jsonList)
    } // selectDB


    @Throws(java.lang.Exception::class)
    protected open fun selectDB(
        startIndex: Int,
        type1: String?,
        type2: String?,
        type3ID: String?,
        table: String?,
        idList: java.util.ArrayList<String>,
        jsonList: java.util.ArrayList<String>
    ): Int {
        var startIndex = startIndex
        cmdb = CMDB.open(applicationContext)
        var cursor: Cursor? = null
        var jo: JSONObject? = null
        cursor =
            cmdb.select(arrayOf(type1), arrayOf(type2), null, table)
        val index = cursor.getColumnIndex(CMDB.COLUMNjson)
        while (cursor.moveToNext()) {
            jo = JSONObject(cursor.getString(index))
            if (idList.size > startIndex) {
                idList[startIndex] = jo.getString(type3ID)
                jsonList[startIndex] = jo.toString()
            } else {
                idList.add(jo.getString(type3ID))
                jsonList.add(jo.toString())
            }
            startIndex++
        }
        return startIndex
    } // selectDB

    @Throws(java.lang.Exception::class)
    open fun deleteList(
        startIndex: Int,
        idList: java.util.ArrayList<String>,
        jsonList: java.util.ArrayList<String>,
        detailList: java.util.ArrayList<Boolean>?
    ) {
        while (startIndex < idList.size) {
            idList.removeAt(startIndex)
            jsonList.removeAt(startIndex)
            detailList?.removeAt(startIndex)
        }
    } // deleteList

    lateinit var softKeyboard: InputMethodManager
    protected open fun closeKeyboard(): Boolean {
        return softKeyboard.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    } // closeKeyboard


    protected open fun file() {
        // photo Album 호출 intent 생성
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_FILE)
    } // file


    @Throws(java.lang.Exception::class)
    open fun prd() {
        prd(getString(R.string.a00_00activityWait), true)
    }

    protected open fun prd(isTimeCheck: Boolean) {
        prd(getString(R.string.a00_00activityWait), isTimeCheck)
    }

    protected open fun prd(string: String?) {
        prd(string, true)
    }


    protected var prdCheckTime: CMTimer? = null
    protected var prdTime: Long = 0
    protected var prdDialog: Dialog? = null
    protected var isERROR = false
    protected var popupLayout: RelativeLayout? = null
    protected open fun prd(string: String?, isTimeCheck: Boolean) {
        prdTime = System.currentTimeMillis()
        dismiss()
        if (prdCheckTime is CMTimer) {
            prdCheckTime!!.cancel()
            prdCheckTime = null
        }
        if (isTimeCheck) {
            prdCheckTime = object : CMTimer(20000, 20000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    try {
                        if (mylong === prdTime) {
                            if (prdDialog != null) {
                                dismiss()
                                isERROR = true
                                prdCheckTime = null
                                alert(getString(R.string.a00_00activityServerFail))
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        if (StaticClass.isDebug) {
                            Env.error("", e)
                        }
                    }
                }
            }
            prdCheckTime!!.setWhat(null, prdTime)
            prdCheckTime!!.start()
        }
        if (prdDialog is Dialog) {
            prdDialog!!.dismiss()
            prdDialog = null
        }
        var params: RelativeLayout.LayoutParams? = null
        popupLayout = RelativeLayout(applicationContext)
        val progress = ProgressBar(applicationContext)
        params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        popupLayout!!.addView(progress, params)
        prdDialog = Dialog(this)
        prdDialog!!.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
            }
            true
        })
        prdDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        prdDialog!!.getWindow()?.setBackgroundDrawable(ColorDrawable(A901Color.TRANSPARENT))
        prdDialog!!.addContentView(
            popupLayout!!,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        prdDialog!!.show()
    } // prd(String string)

    open fun dismiss(): Boolean {

        if (prdDialog != null) {
            prdDialog!!.dismiss()
            prdDialog = null
            return true
        }
        return false
    } // dismiss


//    val finishRecv by  lazy{FinishReceiver()}
//    inner class FinishReceiver : BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?) {
//            when{
//                intent?.action=="dalbit.yonsei.finish"->{
////                    EnvDebug(this@A00_00Activity.localClassName+" die")
//                    unregisterReceiver(this)
//                    this@A00_00Activity.finish()
//                }
//            }
//        }
//    }
}