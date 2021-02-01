package kr.coursemos.yonsei

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.text.TextUtils
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_a01_13_web_view.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A905FileDownLoad
import kr.coursemos.yonsei.a99util.CMCommunication
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import java.io.File
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

open class A01_13WebView : A00_00Activity() {


    protected var vod = ""

    private val TYPE_IMAGE = "image/*"
    private val INPUT_FILE_REQUEST_CODE = 2001

    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_13_web_view)
        try {
            initBottom(getEnv(StaticClass.KEY_WEBVIEWNAME, ""))
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            val sets: WebSettings = a01_13webview.settings
            sets.defaultTextEncodingName = StaticClass.charset
            sets.cacheMode = WebSettings.LOAD_NO_CACHE
            sets.setAppCacheEnabled(false)
            sets.javaScriptEnabled = true
            sets.domStorageEnabled = true
            sets.useWideViewPort = true
            sets.loadWithOverviewMode = true
            sets.javaScriptCanOpenWindowsAutomatically = true
            sets.pluginState = WebSettings.PluginState.ON
            sets.builtInZoomControls = true
            a01_13webview.setWebViewClient(WebViewClient())
            a01_13webview.setBackgroundColor(A901Color.TRANSPARENT)


            //			getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            a01_13webview.setWebChromeClient(object : WebChromeClient() {
                override fun onJsAlert(
                    view: WebView,
                    url: String,
                    message: String,
                    result: JsResult
                ): Boolean {
                    try {
                        if (StaticClass.isDebug) {
                            Env.debug(applicationContext, "onJsAlert\n$url\n$message")
                        }
                        alert(message)
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                    result.confirm()
                    return true
                }

                override fun onCreateWindow(
                    view: WebView,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message
                ): Boolean {
                    if (StaticClass.isDebug) {
                        Env.debug(applicationContext, "onCreateWindow\n$isUserGesture\n$resultMsg")
                    }
                    val settings = view.settings
                    settings.domStorageEnabled = true
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true

//					settings.setJavaScriptCanOpenWindowsAutomatically(true);
//					settings.setAllowFileAccessFromFileURLs(true);
//					settings.setAllowUniversalAccessFromFileURLs(true);
                    view.setWebChromeClient(this)
                    val transport = resultMsg.obj as WebViewTransport
                    transport.webView = view
                    resultMsg.sendToTarget()
                    return false
                    //					return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                }

                // For Android Version 5.0+
                // Ref: https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
//					System.out.println("WebViewActivity A>5, OS Version : " + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3");
                    if (mFilePathCallback != null) {
                        mFilePathCallback!!.onReceiveValue(null)
                    }
                    mFilePathCallback = filePathCallback
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                (me as Activity),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        ) {
//								Env.debug(context,"설명이 필요해");
                            ActivityCompat.requestPermissions(
                                (me as Activity),
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1000
                            )
                        } else {
                            ActivityCompat.requestPermissions(
                                (me as Activity),
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1000
                            )
                        }
                    } else {
                        file()
                    }


//					imageChooser();
                    return true
                }

                private fun imageChooser() {
                    var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                        // Create the File where the photo should go
                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                        } catch (e: java.lang.Exception) {
                            Env.error("", e)
                        }

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            mCameraPhotoPath = "file:" + photoFile.absolutePath
                            takePictureIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile)
                            )
                        } else {
                            takePictureIntent = null
                        }
                    }
                    val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    contentSelectionIntent.type = TYPE_IMAGE
                    val intentArray: Array<Intent?>
                    when (takePictureIntent) {
                        null -> {
                            intentArray = arrayOfNulls<Intent>(0)
                        }
                        else -> {
                            intentArray = arrayOf(takePictureIntent)
                        }
                    }
                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
                }

                @Throws(IOException::class)
                private fun createImageFile(): File? {
                    // Create an image file name
                    val timeStamp =
                        SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val imageFileName = "JPEG_" + timeStamp + "_"
                    val storageDir =
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        )
                    return File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",  /* suffix */
                        storageDir /* directory */
                    )
                }
            })
            a01_13webview.setWebViewClient(object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String,
                    failingUrl: String
                ) {
                    Env.debug(
                        applicationContext,
                        "$errorCode : $description\n$failingUrl"
                    )
                    super.onReceivedError(view, errorCode, description, failingUrl)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    url: String
                ): Boolean {
                    var url = url
                    try {
                        if (StaticClass.isDebug) {
                            Env.debug(
                                applicationContext, """
     
     $url
     """.trimIndent()
                            )
                        }
                        if (url.indexOf("www.youtube.com") >= 0) {
                            var params =
                                url.split("https://".toRegex()).toTypedArray()
                            if (params.size < 2) {
                                params = url.split("http://".toRegex()).toTypedArray()
                            }
                            url = params[1]
                            params = url.split("?").toTypedArray()
                            if (params.size == 1) {
                            } else {
                                url =
                                    url.replaceFirst(params[0] + "?", "")
                            }
                            params = url.split("\\&".toRegex()).toTypedArray()
                            val shm = StringHashMap()
                            val size = params.size
                            var key_value: Array<String>? = null
                            for (i in 0 until size) {
                                key_value = params[i].split("=".toRegex()).toTypedArray()
                                shm[key_value[0]] = key_value[1]
                            }
                            if (StaticClass.isDebug) {
                                Env.debug(
                                    applicationContext, """
     
     $url
     $shm
     """.trimIndent()
                                )
                            }
                            setEnv(
                                StaticClass.KEY_YOUTUBE,
                                shm.getString("v")
                            )
                            StaticClass.history.removeAt(StaticClass.history.size - 1)
                            startActivity(A01_23YouTube::class.java, 0)
                        } else if (url.indexOf("coursemos://") == 0) {
//							String SCHEMAURL=url.replaceFirst("coursemos://", "lmsvideo://www.coursemos.kr/video?")+"&wstoken="+sp.getString(StaticClass.KEY_USERTOKEN, "")+"&url="+sp.getString(StaticClass.KEY_SELECTSCHOOLURL, "");
                            url = url.replaceFirst("coursemos://".toRegex(), "")
                            var params =url.split("?").toTypedArray()
                            if (params.size == 1) {
                            } else {
                                url =url.replaceFirst(params[0] + "?", "")
                            }
                            params = url.split("\\&".toRegex()).toTypedArray()
                            val shm = StringHashMap()
                            val size = params.size
                            var key_value: Array<String>? = null
                            for (i in 0 until size) {
                                key_value = params[i].split("=".toRegex()).toTypedArray()
                                shm[key_value[0]] = key_value[1]
                            }
                            if (StaticClass.isDebug) {
                                Env.debug(
                                    applicationContext, "$url   $shm".trimIndent()
                                )
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                putEnvBoolean(
                                    StaticClass.KEY_VIDEOISNORMAL,
                                    false
                                )
                                putEnv(
                                    StaticClass.KEY_VIDEOTRACKID,
                                    shm.getString("trackid")
                                )
                                setEnv(
                                    StaticClass.KEY_VIDEOLASTPOSTION,
                                    shm.getString("lastposition")
                                )
                                setEnv(
                                    StaticClass.KEY_VIDEOISPROGRESS,
                                    shm.getString("isprogress")
                                )
                                setEnv(
                                    StaticClass.KEY_VIDEOURL,
                                    shm.getString("vod")
                                )
                                setEnvInt(
                                    StaticClass.KEY_VIDEOPAUSEPOSITION,
                                    0
                                )
                                try {
                                    if (shm.getString("iscomplete").toInt() == 1) {
                                        setEnvBoolean(
                                            StaticClass.KEY_VIDEOISCOMPLETE,
                                            true
                                        )
                                    } else {
                                        setEnvBoolean(
                                            StaticClass.KEY_VIDEOISCOMPLETE,
                                            false
                                        )
                                    }
                                } catch (nume: NumberFormatException) {
                                    setEnvBoolean(
                                        StaticClass.KEY_VIDEOISCOMPLETE,
                                        false
                                    )
                                }
                                try {
                                    if (shm.getString("isprogress").toInt() == 1) {
                                        setEnvBoolean(
                                            StaticClass.KEY_VIDEOISPROGRESS,
                                            true
                                        )
                                    } else {
                                        setEnvBoolean(
                                            StaticClass.KEY_VIDEOISPROGRESS,
                                            false
                                        )
                                        setEnvBoolean(
                                            StaticClass.KEY_VIDEOISCOMPLETE,
                                            true
                                        )
                                    }
                                } catch (nume: NumberFormatException) {
                                    setEnvBoolean(
                                        StaticClass.KEY_VIDEOISPROGRESS,
                                        true
                                    )
                                }

//								edit.putBoolean(StaticClass.KEY_VIDEOISNORMAL, false);
//								edit.putString(StaticClass.KEY_VIDEOTRACKID, "2133");
//								edit.putString(StaticClass.KEY_VIDEOLASTPOSTION, "999");
//								edit.putString(StaticClass.KEY_VIDEOURL, "http://stream.ewha.ac.kr:1935/streams/_definst_/520c3ff9-d98c-438d-b819-3834680f0d7f/2016/03/01/8bcad4dc-692c-4bde-98b1-e00f522012e5/3d004301-91d2-4d19-a993-97b4a221699b.mp4/playlist.m3u8");
//								edit.putInt(StaticClass.KEY_VIDEOPAUSEPOSITION, 0);
//								edit.putBoolean(StaticClass.KEY_VIDEOISPROGRESS, false);
//								edit.putBoolean(StaticClass.KEY_VIDEOISCOMPLETE, true);
//								edit.commit();

//								startActivity(A01_19VideoView.class, 0);
                                startActivity(A01_22VideoView::class.java, 0)

//								startActivity(A01_23YouTube.class, 0);
                            } else {
                                a01_13webview.goBack()
                                vod = shm.getString("vod")
                                isAlertAction = true
                                alert(getString(R.string.a01_13activityVideo))
                            }
                            //							PackageManager packageManager =me.getPackageManager();
//							List<ApplicationInfo> packages=packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
//							for(ApplicationInfo packageInfo:packages){
//								if("kr.coursemos.android.video".equals(packageInfo.packageName)){
//									if (StaticClass.isDebug) {
//										Env.debug(context, SCHEMAURL);
//									}
//									Uri uri = Uri.parse(SCHEMAURL);
//									Intent it = new Intent(Intent.ACTION_VIEW, uri);
//									startActivity(it);
//									return true;
//								}
//							}
//							Uri uri = Uri.parse("market://search?q=pname:kr.coursemos.android.video");
//							Intent it = new Intent(Intent.ACTION_VIEW, uri);
//							startActivity(it);
                        } else if (url.indexOf("rtsp://") == 0) {
                            if (StaticClass.isDebug) {
                                Env.debug(applicationContext, url)
                            }
                            setEnv(
                                StaticClass.KEY_VIDEOURL,
                                url
                            )
                            setEnvBoolean(
                                StaticClass.KEY_VIDEOISNORMAL,
                                true
                            )
                            //							startActivity(A01_19VideoView.class, 0);
                            startActivity(A01_22VideoView::class.java, 0)
                        } else if (url.indexOf("https://drive.google.com") == 0) {
                            val uri = Uri.parse(url)
                            val it = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(it)
                        } else if (url.indexOf("wbx://") == 0 || url.indexOf("zoomus://") == 0 || url.indexOf(
                                "market://"
                            ) == 0
                        ) {
                            val uri = Uri.parse(url)
                            val it = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(it)
                        } else {
                            setEnv(
                                StaticClass.KEY_WEBURL,
                                url
                            )
                            view.loadUrl(url)
                        }
                    } catch (e: java.lang.Exception) {
                        Env.error(url, e)
                    }
                    return true
                }
            })
            a01_13webview.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                try {
                    val encodingStartIndex = url.lastIndexOf("/")
                    val encodingLastIndex = url.indexOf("?")
                    val fileName =
                        url.substring(encodingStartIndex + 1, encodingLastIndex)
                    prd()
                    val shm = StringHashMap()
                    shm["token"] = getEnv(
                        StaticClass.KEY_USERTOKEN,
                        ""
                    )
                    object : A905FileDownLoad(
                        applicationContext,
                        url,
                        shm,
                        fileName,
                        CookieManager.getInstance().getCookie(
                            URL(StaticClass.APIWEBVIEW)
                                .host
                        )
                    ) {
                        @Throws(java.lang.Exception::class)
                        override fun success(msg: String) {
                            dismiss()
                            val fileExtension = fileName.substring(
                                fileName.lastIndexOf(".") + 1,
                                fileName.length
                            )
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            var filetype: String? = null
                            filetype = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(fileExtension)
                            if (filetype is String) {
                            } else {
                                filetype = "*/*"
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                val photoURI = FileProvider.getUriForFile(
                                    context,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    File(StaticClass.pathExternalStorage + fileName)
                                )
                                intent.setDataAndType(photoURI, filetype)
                            } else {
                                intent.setDataAndType(
                                    Uri.fromFile(File(StaticClass.pathExternalStorage + fileName)),
                                    filetype
                                )
                            }
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            try {
                                startActivity(intent)
                            } catch (e: java.lang.Exception) {
                                Env.error("", e)
                                alert(getString(R.string.a01_04activityFileOpenFail))
                            }
                        }

                        @Throws(java.lang.Exception::class)
                        override fun fail(errmsg: String) {
                            dismiss()
                            Env.error(errmsg, java.lang.Exception())
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Env.error("", e)
                }
            })

//			CookieManager cookieManager=CookieManager.getInstance();
//			cookieManager.setAcceptCookie(true);
//			cookieManager.removeAllCookie();
//			cookieManager.removeSessionCookie();
//			cookieManager.removeExpiredCookie();
//			CookieSyncManager.createInstance(context).sync();


//			CookieManager cookieManager=CookieManager.getInstance();
//			cookieManager.setAcceptCookie(true);
//			cookieManager.removeAllCookie();
//			cookieManager.removeSessionCookie();
//			cookieManager.removeExpiredCookie();
//			CookieSyncManager.createInstance(context).sync();
            val postData = StringBuilder()
            postData.append("utoken=")
            postData.append(
                getEnv(
                    StaticClass.KEY_USERTOKEN,
                    ""
                )
            )
            postData.append("&modurl=")
            postData.append(
                getEnv(
                    StaticClass.KEY_WEBURL,
                    "http://edu3.moodledev.net"
                )
            )
            if (StaticClass.isDebug) {
                Env.debug(
                    applicationContext,
                    """
                        ${StaticClass.APIWEBVIEW}
                        $postData
                        """.trimIndent()
                )
            }


//			// 웹세팅
//			WebSettings webSettings;
//
///// 웹 세팅
//			webSettings = layout.a01_13WebView.getSettings();
//
//// URL 컨텍스트 내에서 자바스크립트 접근가능여부
//			webSettings.setJavaScriptEnabled(true);
//			webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//			webSettings.setAllowFileAccess(true);
//// 파일 기반 XSS 취약성 문제 해결 (구글지적)
//// true -> false 변경
////1. WebView에 위험한 설정이 포함되지 않도록 설정
//			webSettings.setAllowFileAccessFromFileURLs(true);
//// 다른 도메인의경우에도 허용하는가
//			webSettings.setAllowUniversalAccessFromFileURLs(true);
//// 네트워크를 통한 이미지 리소스 로딩여부 결정 (false -> 이미지로딩 금지)


//			// 웹세팅
//			WebSettings webSettings;
//
///// 웹 세팅
//			webSettings = layout.a01_13WebView.getSettings();
//
//// URL 컨텍스트 내에서 자바스크립트 접근가능여부
//			webSettings.setJavaScriptEnabled(true);
//			webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//			webSettings.setAllowFileAccess(true);
//// 파일 기반 XSS 취약성 문제 해결 (구글지적)
//// true -> false 변경
////1. WebView에 위험한 설정이 포함되지 않도록 설정
//			webSettings.setAllowFileAccessFromFileURLs(true);
//// 다른 도메인의경우에도 허용하는가
//			webSettings.setAllowUniversalAccessFromFileURLs(true);
//// 네트워크를 통한 이미지 리소스 로딩여부 결정 (false -> 이미지로딩 금지)
            a01_13webview.addJavascriptInterface(
                coursemos(),
                "coursemos"
            )
//			layout.a01_13WebView.getSettings().setUserAgentString(StaticClass.isTablet);

            //			layout.a01_13WebView.getSettings().setUserAgentString(StaticClass.isTablet);
            a01_13webview.postUrl(
                StaticClass.APIWEBVIEW,
                postData.toString().toByteArray(charset("utf-8"))
            )
//			layout.a01_13WebView.postUrl(StaticClass.APIWEBVIEW, EncodingUtils.getBytes(postData.toString(), "utf-8"));

//			layout.a01_13WebView.postUrl("http://etl.snu.ac.kr/local/coursemos/webviewapi.php", EncodingUtils.getBytes("utoken=01baf26cb524aea2970c04d67f79828d&modurl=http://etl.snu.ac.kr/mod/url/view.php?id=561933", "utf-8"));
//			 layout.a01_13WebView.loadUrl("http://klms.moodledev.net");


        } catch (e: Exception) {
            EnvError(e)
        }
    }

    inner class coursemos {
        @JavascriptInterface
        fun zoom(params: String?) {
            try {
                val packageName = "us.zoom.videomeetings"

                //크롬브라우저가 설치되어있으면 호출, 없으면 마켓으로 설치유도
//				PackageManager pm = getPackageManager();
//				Intent intent = pm.getLaunchIntentForPackage(packageName);
//				if (intent == null) {
//					Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
//					playStoreIntent.setData(Uri.parse("market://details?id=" + packageName));
//					playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					me.startActivity(playStoreIntent);
//				}else{
//					if(StaticClass.isDebug){
//						Env.debug(context,params);
//					}
////					intent.setData(Uri.parse(params));
//
//					intent=new Intent(Intent.ACTION_VIEW,Uri.parse(params)) ;
//					me.startActivity(intent);
//				}
                if (StaticClass.isDebug) {
                    Env.debug(applicationContext, params)
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(params))
                me.startActivity(intent)
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        try {
            if (requestCode == 1000) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    file()
                } else {
                    alert(getString(R.string.a01_03subjectliststoragepermission))
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }

    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data)
                    return
                }
                val results = arrayOf(getResultUri(data))
                mFilePathCallback!!.onReceiveValue(results)
                mFilePathCallback = null
            } else {
                if (mUploadMessage == null) {
                    super.onActivityResult(requestCode, resultCode, data)
                    return
                }
                val result = getResultUri(data)
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        } else if (requestCode == REQUEST_FILE && resultCode == RESULT_OK) {
            if (mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            val results =
                arrayOf(getResultUri(data))
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else {
            if (mFilePathCallback != null) mFilePathCallback!!.onReceiveValue(null)
            if (mUploadMessage != null) mUploadMessage!!.onReceiveValue(null)
            mFilePathCallback = null
            mUploadMessage = null
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getResultUri(data: Intent?): Uri {
        lateinit var result: Uri
        if (data == null || TextUtils.isEmpty(data.dataString)) {
            // If there is not data, then we may have taken a photo
            if (mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath)
            }
        } else {
            var filePath: String? = ""
            filePath = data.dataString
            result = Uri.parse(filePath)
        }
        return result
    }

    override fun success(what: String?, jyc: CMCommunication?) {
        try {
            if (jyc == null) {
                val id = what!!.toInt()
                if (id == ID_alertOK && isAlertAction) {
                    val uri = Uri.parse(vod)
                    val it = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(it)
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    }
}