package kr.coursemos.yonsei

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Base64
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.appindexing.Action
import com.google.android.gms.appindexing.AppIndex
import com.google.android.gms.common.api.GoogleApiClient
import com.kinda.alert.KAlertDialog
import com.squareup.picasso.Picasso
import com.ssenstone.stonepass.libstonepass_sdk.SSUserManager
import kotlinx.android.synthetic.main.activity_a01_03_subject_list.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A901Color
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.*
import kr.coursemos.yonsei.otp.Totp
import kr.coursemos.myapplication.a99util.EnvError
import kr.smartauth.biopass.sdk.domain.AppConstants.IntentData
import kr.smartauth.biopass.sdk.domain.AuthenticationType
import kr.smartauth.biopass.sdk.main.BioPassMainActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.util.*
import java.util.concurrent.Executor

class A01_03SubjectList : A00_00Activity() {
	lateinit var year: Array<String?>
	private var selectNameList: Array<String?>? = null
	private var selectCodeList: IntArray? = null
	private var semesterNameList: Array<String?>? = null
	private var semesterCodeList: IntArray? = null
	private var selectYear: String? = null
	private var selectCode: String? = null
	private var shm_apiParams: StringHashMap? = null
	private var listPosition = 0
	private var positionPaddingY = 0
	private var isPeriod = false
	private var startCode = 0
	private var currentCode = 0
	private var isAuth = false
	private val mFingerprintDlg: Dialog? = null
	private val mSSUserManager: SSUserManager? = null
	private var isReReg = false
	private var isChangeBio = false
	private var executor: Executor? = null
	private var biometricPrompt: BiometricPrompt? = null
	private var promptInfo: PromptInfo? = null
	private val index = 0

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private var client: GoogleApiClient? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_a01_03_subject_list)

		try {
			StaticClass.history.clear()
			putEnvInt(StaticClass.KEY_SELECTMAINTAB, StaticClass.ID_BOTTOMTAB1)
			initBottom()


			a01_03SubjectList.adapter = adapter

			a01_03refresh.setOnRefreshListener {
				requestSubject()
			}
			a01_13Day.setTextColor(A901Color.getIhwaColor())
			a01_13Month.setTextColor(A901Color.getIhwaColor())
			a01_13myinfo.setOnTouchListener { v, event ->
				if (event.action == MotionEvent.ACTION_DOWN) {
					val draw: Drawable =
						applicationContext.getResources().getDrawable(R.drawable.info_white)
					draw.setColorFilter(A901Color.getIhwaColor(), PorterDuff.Mode.SRC_ATOP)
					a01_13myinfo.setImageDrawable(draw)
				} else if (event.action == MotionEvent.ACTION_UP) {
					val draw: Drawable =
						applicationContext.getResources().getDrawable(R.drawable.info_white)
					draw.colorFilter = null
					a01_13myinfo.setImageDrawable(draw)
				}
				false
			}
			a01_13myinfo.setOnClickListener(onClick)
			val profileUrl: String = getEnv(StaticClass.KEY_PICURL, "")
			if (profileUrl == "") {
			} else {
				Picasso.with(applicationContext).load(profileUrl).transform(RoundTransform())
					.placeholder(android.R.color.transparent).error(android.R.color.transparent)
					.into(a01_13Pic)
			}
			if (getEnvInt(StaticClass.KEY_LANGUAGE,
					A01_09Z09Language.ID_row1) === A01_09Z09Language.ID_row2
			) {
				a01_13Name.setText(getEnv(StaticClass.KEY_LASTNAME, ""))
			} else {
				a01_13Name.setText(getEnv(StaticClass.KEY_FIRSTNAME, ""))
			}
			a01_13IDNumber.setText(getEnv(StaticClass.KEY_INSTITUTION, ""))
			a01_13IDNumber2.setText(getEnv(StaticClass.KEY_DEPARTMENT, ""))
			a01_13Month.setText(Env.formatTime("MMM", Date()))
			a01_13Day.setText(Calendar.getInstance()[Calendar.DATE].toString())


			cmdb = CMDB.open(applicationContext)
			//게시판정보 미리 삭제
			//게시판정보 미리 삭제
			cmdb.DBClear(CMDB.TABLE01_04z01BoardInfo)
			val semesters = JSONArray(cmdb.selectString(A903LMSTask.API_HAKSA,
				A903LMSTask.API_HAKSA,
				StaticClass.KEY_SEMESTERS,
				CMDB.TABLE01_common))
			val start_semester = JSONObject(cmdb.selectString(A903LMSTask.API_HAKSA,
				A903LMSTask.API_HAKSA,
				StaticClass.KEY_LMSSTART,
				CMDB.TABLE01_common))
			val current_semester = JSONObject(cmdb.selectString(A903LMSTask.API_HAKSA,
				A903LMSTask.API_HAKSA,
				StaticClass.KEY_CURRENTSEMESTER,
				CMDB.TABLE01_common))
			val startYear = start_semester.getInt("year")
			val currentYear = current_semester.getInt("year")
			startCode = start_semester.getInt("semester_code")
			currentCode = current_semester.getInt("semester_code")
			year = arrayOfNulls<String>(currentYear - startYear + 1)
			for (i in year.indices) {
				year[i] = Integer.toString(startYear + i)
			}

			semesterNameList = arrayOfNulls(semesters.length())
			semesterCodeList = IntArray(semesters.length())
			var temp: JSONObject? = null
			for (i in 0 until semesters.length()) {
				temp = semesters.getJSONObject(i)
				semesterNameList!![i] = temp.getString("semester_name")
				semesterCodeList!![i] = temp.getInt("semester_code")
			}

			shm_apiParams = StringHashMap()
			setCode(year.size - 1)
			if (selectCodeList!!.size == 0) {
			} else {
				selectCode = Integer.toString(selectCodeList!![selectCodeList!!.size - 1])
			}
			if (isBackKey) {
				selectYear = getEnv(StaticClass.KEY_SUBJECTCACHEYEAR, "")
				selectCode = getEnv(StaticClass.KEY_SUBJECTCACHESEMESTER, "")
				showList()
				a01_03SubjectList.setSelectionFromTop(getEnvInt(StaticClass.KEY_SUBJECTCACHEPOSITIONY,
					0), getEnvInt(StaticClass.KEY_SUBJECTCACHEPADDINGY, 0))
			} else {
				showList()
				if (adapter.idList.size === 0) {
					prd()
				}
				requestSubject()
			}
			val reviewalertCount: Int = getEnvInt(StaticClass.KEY_REVIEWALERTCOUNT, 0)
			val reviewDelete: Int = getEnvInt(StaticClass.KEY_REVIEWCOUNTDELETE, 0)
			if (getEnvBoolean(StaticClass.KEY_REVIEWCHECK, false) || reviewalertCount < 10) {
				setEnvInt(StaticClass.KEY_REVIEWALERTCOUNT, reviewalertCount + 1)
			} else {
				val alert = KAlertDialog(this, KAlertDialog.CUSTOM_IMAGE_TYPE)
				//		alert.setCustomImage(R.drawable.icic);
				alert.titleText = getString(R.string.app_name)
				alert.contentText = "여러분의 별점이 서비스 개선에 큰 힘이 됩니다."
				alert.confirmText = "다음에"
				alert.setConfirmClickListener { kAlertDialog ->
					kAlertDialog.dismiss()
					try {
						setEnvInt(StaticClass.KEY_REVIEWCOUNTDELETE, reviewDelete + 1)
						if (reviewDelete > 4) {
							setEnvBoolean(StaticClass.KEY_REVIEWCHECK, true)
						}
						setEnvInt(StaticClass.KEY_REVIEWALERTCOUNT, 0)
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
				}
				alert.cancelText = "별점주기"
				alert.setCancelClickListener { kAlertDialog ->
					kAlertDialog.dismiss()
					try {
						try {
							setEnvBoolean(StaticClass.KEY_REVIEWCHECK, true)
							val marketUri =
								Uri.parse("market://details?id=" + applicationContext.getPackageName())
							startActivity(Intent(Intent.ACTION_VIEW, marketUri))
						} catch (ex: ActivityNotFoundException) {
							alert("구글스토어 정보를 찾을수 없습니다.")
						}
					} catch (e: java.lang.Exception) {
						Env.error("", e)
					}
				}
				alert.show()
			}
			val ACCESS_TOKEN: String = getEnv(StaticClass.KEY_ACCESS_TOKEN, StaticClass.NO)
			removeEnv(StaticClass.KEY_ACCESS_TOKEN)
			if (ACCESS_TOKEN == StaticClass.NO) {
			} else {
				putEnv(StaticClass.KEY_SUBJECTID, ACCESS_TOKEN)
				startActivity(A01_04SubjectDetail::class.java, 0)
			}
		} catch (e: java.lang.Exception) {
			EnvError(e)
		}
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = GoogleApiClient.Builder(this).addApi(AppIndex.API).build()
		//        startActivity(A01_02Login::class.java)
	}

	var onClick = View.OnClickListener { view ->
		try {
			val id = view.id
			if (R.id.a01_03periodsearch == id || R.id.a01_03periodsearch2 === id) {
				yearCheck()
			} else if (R.id.a01_03timetable === id) {
				startActivity(A01_03Z01TimeTable::class.java, 0)
			} else if (id == R.id.a01_13myinfo) {
				putEnv(StaticClass.KEY_SELECTUSERSEQ, getEnv(StaticClass.KEY_USERSEQ, ""))
				startActivity(A01_09Z02MyInfo::class.java, 0)
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	} // OnClickListener onClick

	fun reg(sign: String, code: String) {
		val shm = StringHashMap()
		val mUserId: String = getEnv(StaticClass.KEY_USERSEQ, "kimchi")
		val mUnivCode: String = getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "kimchi")
		shm["uuid"] = mUnivCode + mUserId
		shm["publickey"] = getEnv(StaticClass.KEY_PUBLICKEY, "")
		shm["sign"] = sign
		shm["code"] = code

		object : A903LMSTask(applicationContext, API_BIOREG, shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				val jo = JSONObject(result)
				val result = jo.getString("result")
				if (result == "0000") {
					//등록완료
					bioTest("지문 인증", "9099")
				} else if (result == "9001" || result == "9003") {
					//재등록필요
					isReReg = true
					alert(jo.getString("message"))
				} else if (result == "9002") {
					//등록필요
					toast("등록이 필요합니다.")
					bioTest("지문 등록", result)
				} else if (result == "9004") {
					alert(jo.getString("message"))
				} else if (result == "9099") {
					//인증
					bioTest("지문 인증", result)
				}
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				Env.error(errmsg, java.lang.Exception())
			}
		}
	}

	fun dalbitauth(sign: String, code: String) {
		val mUserId: String = getEnv(StaticClass.KEY_USERSEQ, "kimchi")
		val mUnivCode: String = getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "kimchi")
		val shm = StringHashMap()
		shm["uuid"] = mUnivCode + mUserId
		shm["sign"] = sign
		shm["code"] = code

		object : A903LMSTask(applicationContext, API_BIOAUTH, shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				val jo = JSONObject(result)
				val result = jo.getString("result")
				if (result == "0000") {
					startActivity(A01_04SubjectDetail::class.java, 0)
				} else {
					alert(jo.getString("message"))
				}
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				Env.error(errmsg, java.lang.Exception())
			}
		}
	}

	fun bioTest(title: String, code: String, isNewKey: Boolean = false) {
		val biometricManager = BiometricManager.from(applicationContext)
		when (biometricManager.canAuthenticate()) {
			BiometricManager.BIOMETRIC_SUCCESS -> Env.debug(applicationContext,
				"BIOMETRIC_SUCCESS  ")
			BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Env.debug(applicationContext,
				"BIOMETRIC_ERROR_NO_HARDWARE")
			BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Env.debug(applicationContext,
				"BIOMETRIC_ERROR_HW_UNAVAILABLE")
			BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Env.debug(applicationContext,
				"BIOMETRIC_ERROR_NONE_ENROLLED")
		}


		executor = ContextCompat.getMainExecutor(applicationContext)

		biometricPrompt = BiometricPrompt(this@A01_03SubjectList,
			executor!!,
			object : BiometricPrompt.AuthenticationCallback() {
				override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
					super.onAuthenticationError(errorCode, errString)
					alert(errString.toString())
					Env.debug(applicationContext, "Authentication error: $errString")
				}

				override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
					super.onAuthenticationSucceeded(result)
					try {
						val signature = result.cryptoObject!!.signature
						signature!!.update("test".toByteArray())
						val b = signature.sign()
						val signatureString = Base64.encodeToString(b, Base64.URL_SAFE)
						if (code == "9002") {
							reg(URLEncoder.encode(signatureString, "utf-8"), "0000")
						} else if (code == "9099") {
							dalbitauth(URLEncoder.encode(signatureString, "utf-8"), "9099")
						} else if (code == "9003") {
							reg(URLEncoder.encode(signatureString, "utf-8"), code)
						}
						//					signature.initVerify(publicKeySt);
						//					signature.update(mToBeSignedMessage.getBytes());
						//					Env.debug(context,""+signature.verify(b));
						//					finish();
					} catch (e: java.lang.Exception) {
						Env.error("", e)
						//					finish();
					}
				}

				override fun onAuthenticationFailed() {
					super.onAuthenticationFailed()
					Env.debug(applicationContext, "Authentication failed!")
				}
			})

		promptInfo = PromptInfo.Builder().setTitle(title).setNegativeButtonText(title).build()
		// Prompt appears when user clicks "Log in".
		// Consider integrating with the keystore to unlock cryptographic operations,
		// if needed by your app.
		// Prompt appears when user clicks "Log in".
		// Consider integrating with the keystore to unlock cryptographic operations,
		// if needed by your app.
		val keyPair =
			generateKeyPair(getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "kimchi"), true, isNewKey)
		val signature = initSignature(getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "kimchi"))
		if (signature == null) {
		} else {
			biometricPrompt!!.authenticate(promptInfo!!, BiometricPrompt.CryptoObject(signature))
		}
	}

	@Throws(java.lang.Exception::class)
	fun requestSubject() {
		cmdb = CMDB.open(applicationContext)
		cmdb.DBClear(CMDB.TABLE01_03SubjectList)
		cmdb.DBClear(CMDB.TABLE01_03z01TimeTable)
		putEnv(StaticClass.KEY_SUBJECTCACHEYEAR, selectYear!!)
		putEnv(StaticClass.KEY_SUBJECTCACHESEMESTER, selectCode!!)
		shm_apiParams!!["year"] = selectYear
		shm_apiParams!!["semester_code"] = selectCode
		if (isPeriod) {
			shm_apiParams!!["isPeriod"] = StaticClass.YES
		} else {
			shm_apiParams!!["isPeriod"] = StaticClass.NO
		}
		isPeriod = false
		object : A903LMSTask(applicationContext, API_CURRENTSUBJECT, shm_apiParams) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				showList()
				dismiss()
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				errorCheckLogout(result, errorMsg)
				dismiss()
			}
		}
	}

	@Throws(java.lang.Exception::class)
	private fun getKeyPair(keyName: String): KeyPair? {
		val keyStore = KeyStore.getInstance("AndroidKeyStore")
		keyStore.load(null)
		if (keyStore.containsAlias(keyName)) {
			// Get public key
			val publicKey = keyStore.getCertificate(keyName).publicKey
			// Get private key
			val privateKey = keyStore.getKey(keyName, null) as PrivateKey
			putEnv(StaticClass.KEY_PUBLICKEY,
				URLEncoder.encode(Base64.encodeToString(publicKey.encoded, Base64.URL_SAFE),
					"UTF-8"))
			return KeyPair(publicKey, privateKey)
		} else {
			Env.debug(applicationContext, "키없음")
		}
		return null
	}

	@Throws(java.lang.Exception::class)
	private fun initSignature(keyName: String): Signature? {
		val keyPair: KeyPair? = getKeyPair(keyName)
		var signature = Signature.getInstance("SHA256withECDSA")
		try {
			signature!!.initSign(keyPair!!.private)
		} catch (e: KeyPermanentlyInvalidatedException) {
			Env.debug(applicationContext, "키 새로 생성 - 지문정보 변경")
			isChangeBio = true
			alert("지문정보가 변경되었습니다.")
			return null
		} catch (e2: UserNotAuthenticatedException) {
			Env.debug(applicationContext, "키 새로 생성 - 유효시간 변경")
			generateKeyPair(keyName, true, true)
			signature = initSignature(keyName)
		}
		return signature
	}

	fun generateKeyPair(keyName: String,
	                    invalidatedByBiometricEnrollment: Boolean,
	                    isNewKey: Boolean): KeyPair? {
		try {
			val keyStore = KeyStore.getInstance("AndroidKeyStore")
			keyStore.load(null)
			if (keyStore.containsAlias(keyName) && !isNewKey) {
				return null
			} else {
				val keyPairGenerator =
					KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
				val builder = KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_SIGN)
					.setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1")).setDigests(
						KeyProperties.DIGEST_SHA256,
						KeyProperties.DIGEST_SHA384,
						KeyProperties.DIGEST_SHA512) // Require the user to authenticate with a biometric to authorize every use of the key
					.setUserAuthenticationRequired(true) //					.setUserAuthenticationValidityDurationSeconds(10)
				if (Build.VERSION.SDK_INT >= 24) {
					builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)
				}
				keyPairGenerator.initialize(builder.build())
				return keyPairGenerator.generateKeyPair()
			}
		} catch (e: java.lang.Exception) {
			EnvError(e)
		}
		return null
	}

	override fun onRequestPermissionsResult(requestCode: Int,
	                                        permissions: Array<out String>,
	                                        grantResults: IntArray) {
		try {
			if (requestCode == 1000) {
				if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//					alert("카메라체크하자!!");
					var myid=getEnv(StaticClass.KEY_ID, "");
					if (getEnv(StaticClass.KEY_SELECTSCHOOL).equals("가천대학교")  &&(myid.equals("t001") || myid.equals("t002")|| myid.equals("t003"))) {
						val intent = Intent(applicationContext, A01_50Camera::class.java)
						intent.putExtra(StaticClass.KEY_ACTIVITYALLKILL,
							StaticClass.NO)
						startActivityForResult(intent, 2000)
					}else{
						getToken()
					}

				} else {
					alert(getString(R.string.a01_03subjectlistcamerapermission))
				}
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}

		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
	}

	@Throws(java.lang.Exception::class)
	private fun getToken() {
		val isbioPass: Int = getEnvInt(StaticClass.KEY_SELECTSCHOOLAUTH, 0)
		//		isbioPass=false;
		val mUserId: String = getEnv(StaticClass.KEY_USERSEQ, "kimchi")
		val mUnivCode: String = getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "kimchi")
		if (isbioPass == 0) {
			val intent = Intent(this, BioPassMainActivity::class.java)
			intent.putExtra(IntentData.USER_ID, mUserId) // 사용자 ID
			intent.putExtra(IntentData.UNIV_CODE, mUnivCode) // 대학교 코드
			val fingerprintManager = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
			var isFingerprint = false
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (fingerprintManager.isHardwareDetected) {
					isFingerprint = true
				}
			}
			if (getEnvBoolean(StaticClass.KEY_ISBIOFACE, false)) {
				isFingerprint = false
			}
			if (isFingerprint) {
				intent.putExtra(IntentData.AUTHENTICATION_TYPE,
					AuthenticationType.FINGERPRINT) // 인증 종류 (얼굴, 지문)
			} else {
				intent.putExtra(IntentData.AUTHENTICATION_TYPE,
					AuthenticationType.FACE) // 인증 종류 (얼굴, 지문)
			}
			startActivityForResult(intent, 1000)
		} else if (isbioPass == 1) {
			isAuth = false
			//			auth("Auth",mUserId+mUnivCode);
			authApi(getEnv(StaticClass.KEY_ID, ""))
			//			auth("Auth","kimchi");
		} else if (isbioPass == 2) {
			val shm = StringHashMap()
			shm["wstoken"] = getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "")
			shm["user_id"] = getEnv(StaticClass.KEY_USERSEQ, "")
			val encode = "GJCTIMJRIY2DEMBUGFDEMNRQHBBUERSGGZBDCMJWGI3DMRBZGIYA"
			putEnv(StaticClass.KEY_LOCKSCREENTIME, Env.getTodateAddSecond("yyyyMMddHHmmss", 120))
			putEnv(StaticClass.KEY_LOCKSCREENNUMBER, Totp(encode).now())
			shm["auth_code"] = getEnv(StaticClass.KEY_LOCKSCREENNUMBER, "")
			prd()
			object : A903LMSTask(applicationContext, API_MOBILECODESEND, shm) {
				@Throws(java.lang.Exception::class)
				override fun success() {
					dismiss()
					//					input("aaaa");
					startActivity(A01_09Z05Z01LockScreen::class.java, 0)
					//					startActivity(A01_04SubjectDetail.class, 0);
				}

				@Throws(java.lang.Exception::class)
				override fun fail(errmsg: String) {
					dismiss()
					errorCheckLogout(result, errorMsg)
				}
			}
		}
	}

	@Throws(java.lang.Exception::class)
	fun authApi(mUserName: String?) {
		val shm = StringHashMap()
		val jo = JSONObject()
		jo.put("OID_KEY", "SCH")
		jo.put("SID", "sch")
		jo.put("USER_ID", mUserName)
		shm["fullstring"] = jo.toString()
		object : A903LMSTask(applicationContext, API_STONEPASS3, shm) {
			@Throws(java.lang.Exception::class)
			override fun success() {
				val jo = JSONObject(result)
				val resultCode = jo.getInt("RESULT_CODE")
				val isInstall = getPackageList("kr.co.stonepass.easyauth")
				Env.debug(context, result)
				if (resultCode == 0) {
					if (isInstall) {
						val url =
							"easyauthapp://action?ACCESS_TOKEN=" + jo.getString("ACCESS_TOKEN") + "&CALLBACK=coursemos2://stonepass"
						val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
						startActivity(i)
					} else {
						toast("앱을 설치해 주세요.")
						val url = "market://details?id=" + "kr.co.stonepass.easyauth"
						val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
						startActivity(i)
					}
				} else {
					if (resultCode == 6) {
						toast("사용자를 등록해 주세요.")
					} else if (resultCode == 17) {
						if (isInstall) {
							toast("사용자정보를 확인해 주세요.")
						} else {
							toast(jo.getString("MESSAGE"))
						}
					} else {
						toast(jo.getString("MESSAGE"))
					}
					if (resultCode == 6 || resultCode == 17) {
						if (isInstall) {
							val intent =
								packageManager.getLaunchIntentForPackage("kr.co.stonepass.easyauth")
							intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
							startActivity(intent)
						} else {
							val url = "market://details?id=" + "kr.co.stonepass.easyauth"
							val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
							startActivity(i)
						}
					}
				}
			}

			@Throws(java.lang.Exception::class)
			override fun fail(errmsg: String) {
				alert(errmsg)
			}
		}
	}

	fun getPackageList(packageName: String?): Boolean {
		var isExist = false
		val pkgMgr = packageManager
		val mApps: List<ResolveInfo>
		val mainIntent = Intent(Intent.ACTION_MAIN, null)
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
		mApps = pkgMgr.queryIntentActivities(mainIntent, 0)
		try {
			for (i in mApps.indices) {
				if (mApps[i].activityInfo.packageName.startsWith(packageName!!)) {
					isExist = true
					break
				}
			}
		} catch (e: java.lang.Exception) {
			isExist = false
		}
		return isExist
	}

	private fun deleteBiopass(msg: String) {
		try {
			val obj = JSONObject(msg)
			val tmp = obj.getString("result")
			if (tmp.equals("SUCCESS", ignoreCase = true)) {
				alert("해당기기에 등록된 아이디가 삭제되었습니다.")
			} else {
				alert("Fail  " + "아이디가 없습니다.")
			}
		} catch (e: JSONException) {
			e.printStackTrace()
			alert("Fail  " + "아이디가 없습니다.")
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		try {
			if (data != null) {
				if (resultCode == RESULT_CANCELED) {
					val err = data.getStringExtra("ErrorMessage")
					alert(err)
				} else if (resultCode == Activity.RESULT_OK) {
					when (requestCode) {
						1000 -> {
							// 아래의 Intent data로 인증 결과 메시지를 받음
							//							String authentication = data.getStringExtra(IntentData.AUTHENTICATION);
							//							Env.debug(context,authentication);
							setEnv(StaticClass.KEY_BIOPASSLASTTIME,
								Env.getTodateAddDay("yyyyMMddHHmmss", 1))
							startActivity(A01_04SubjectDetail::class.java, 0)
						}
						2000 -> {
							StaticClass.history.clear()
							StaticClass.history.add(A01_03SubjectList::class.java)
							startActivity(A01_04SubjectDetail::class.java, 0)
						}
						else -> {
							val aaa = data.getStringExtra(IntentData.AUTHENTICATION)
							alert(aaa)
						}
					}
				}
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
	}

	@Throws(java.lang.Exception::class)
	private fun yearCheck() {
		AlertDialog.Builder(me).setItems(year) { dialog, which ->
			try {
				setCode(which)
				if (selectNameList!!.size == 0) {
					selectCode = null
					requestSubject()
				} else {
					semesterCheck2()
				}
			} catch (e: java.lang.Exception) {
				Env.error("", e)
			}
		}.show()
	}

	@Throws(java.lang.Exception::class)
	private fun semesterCheck2() {
		AlertDialog.Builder(me).setItems(selectNameList) { dialog, which ->
			try {
				selectCode = Integer.toString(selectCodeList!![which])
				isPeriod = true
				prd()
				requestSubject()
			} catch (e: java.lang.Exception) {
				Env.error("", e)
			}
		}.show()
	}

	@Throws(java.lang.Exception::class)
	fun setCode(which: Int) {
		selectYear = year[which]
		var count = semesterCodeList!!.size
		if (which == 0) {
			for (i in semesterCodeList!!.indices) {
				if (semesterCodeList!![i] < startCode) {
					count--
				}
			}
			selectNameList = arrayOfNulls(count)
			selectCodeList = IntArray(count)
			count = 0
			for (i in semesterCodeList!!.indices) {
				if (semesterCodeList!![i] >= startCode) {
					selectNameList!![count] = semesterNameList!![i]!!
					selectCodeList!![count] = semesterCodeList!![i]
					count++
				}
			}
		} else if (which == year.size - 1) {
			for (i in semesterCodeList!!.indices) {
				if (semesterCodeList!![i] >= currentCode) {
					count--
				}
			}
			selectNameList = arrayOfNulls(count)
			selectCodeList = IntArray(count)
			count = 0
			for (i in semesterCodeList!!.indices) {
				if (semesterCodeList!![i] < currentCode) {
					selectNameList!![count] = semesterNameList!![i]!!
					selectCodeList!![count] = semesterCodeList!![i]
					count++
				}
			}
		} else {
			selectNameList = semesterNameList
			selectCodeList = semesterCodeList
		}
	}

	@Throws(java.lang.Exception::class)
	private fun showList() {
		adapter.idList.add(A903LMSTask.API_CURRENTSUBJECT)
		adapter.jsonList.add(A903LMSTask.API_CURRENTSUBJECT)
		var i: Int = selectDB(1,
			A903LMSTask.API_CURRENTSUBJECT,
			A903LMSTask.API_CURRENTSUBJECT,
			CMDB.TABLE01_03SubjectList,
			adapter.idList,
			adapter.jsonList)
		val menuList = JSONObject(getEnv(StaticClass.KEY_SELECTSCHOOLMENULIST, "{}"))
		if (menuList.isNull("pastclass")) {
		} else {
			adapter.idList.add(A903LMSTask.API_PERIODSUBJECT)
			adapter.jsonList.add(A903LMSTask.API_PERIODSUBJECT)
			i++
			i = selectDB(i,
				A903LMSTask.API_PERIODSUBJECT,
				A903LMSTask.API_PERIODSUBJECT,
				CMDB.TABLE01_03SubjectList,
				adapter.idList,
				adapter.jsonList)
		}
		deleteList(i, adapter.idList, adapter.jsonList, null)
		adapter.notifyDataSetChanged()
		a01_03refresh.isRefreshing = false
		//		if(layout.a0103SubjectListAdapter.getCount()==0){
		//			layout.a01_03Empty.setBackgroundResource(R.drawable.pic1);
		//		}else{
		//			layout.a01_03Empty.setBackgroundResource(0);
		//		}

		if(adapter.idList.get(adapter.idList.size-1).equals(A903LMSTask.API_CURRENTSUBJECT) ){
			adapter.idList[adapter.idList.size-1]=A903LMSTask.API_PERIODSUBJECT
		}
	} // showList();

	override fun success(what: String?, jyc: CMCommunication?) {
		try {
			if (jyc == null) {
				val id = what!!.toInt()
				if (id == ID_alertOK) {
					if (isReReg) {
						confirm("재등록 하시겠습니까?")
					} else if (isChangeBio) {
						confirm("재등록 하시겠습니까?")
					}
				} else if (id == ID_confirmOK) {
					if (isReReg) {
						isReReg = false
						//                        bioTest("재등록", "9003")
					} else if (isChangeBio) {
						isChangeBio = false
						//                        bioTest("재등록", "9003", true)
					}
				} else if (id == ID_confirmCancel) {
					if (isReReg) {
						isReReg = false
					} else if (isChangeBio) {
						isChangeBio = false
					}
				}
			}
		} catch (e: java.lang.Exception) {
		}
	}

	override fun onStart() {
		super.onStart()
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client!!.connect()
		val viewAction = Action.newAction(Action.TYPE_VIEW,  // TODO: choose an action type.
			"A01_03SubjectList Page",  // TODO: Define a title for the content shown.
			// TODO: If you have web page content that matches this app activity's content,
			// make sure this auto-generated web page URL is correct.
			// Otherwise, set the URL to null.
			Uri.parse("http://host/path"),  // TODO: Make sure this auto-generated app deep link URI is correct.
			Uri.parse("android-app://kr.coursemos.yonsei/http/host/path"))
		AppIndex.AppIndexApi.start(client, viewAction)
	}

	override fun onBackPressed() {
		if (mFingerprintDlg != null) {
			if (mFingerprintDlg.isShowing) {
				mSSUserManager?.SSCancelFingerprint()
			}
		}
		super.onBackPressed()
	}

	override fun onStop() {
		super.onStop()
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		val viewAction = Action.newAction(Action.TYPE_VIEW,  // TODO: choose an action type.
			"A01_03SubjectList Page",  // TODO: Define a title for the content shown.
			// TODO: If you have web page content that matches this app activity's content,
			// make sure this auto-generated web page URL is correct.
			// Otherwise, set the URL to null.
			Uri.parse("http://host/path"),  // TODO: Make sure this auto-generated app deep link URI is correct.
			Uri.parse("android-app://kr.coursemos.yonsei/http/host/path"))
		AppIndex.AppIndexApi.end(client, viewAction)
		client!!.disconnect()
	}

	val adapter = thisAdapter()

	inner class thisAdapter() : BaseAdapter() {
		public var idList: ArrayList<String> = ArrayList()
		public var jsonList: ArrayList<String> = ArrayList()
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			lateinit var layout: View
			try {
				val id = idList[position]
				if (id == A903LMSTask.API_CURRENTSUBJECT) {
					layout = LayoutInflater.from(applicationContext)
						.inflate(R.layout.list_a01_03subjectheader, null, false)
					var textView = layout.findViewById<TextView>(R.id.a01_03currenttext)
					textView.setText(getString(R.string.current_courses))


					textView = layout.findViewById<TextView>(R.id.a01_03timetable)
					//                    textView.setOnTouchListener(onTouch)
					textView.setText(getString(R.string.a01_03Search3))
					textView.setVisibility(View.GONE)
				} else if (id == A903LMSTask.API_PERIODSUBJECT) {
					layout = LayoutInflater.from(applicationContext)
						.inflate(R.layout.list_a01_03subjectheaderperv, null, false)
					var textView = layout.findViewById<TextView>(R.id.a01_03periodtext)
					textView.setText(getString(R.string.prev_courses))

					textView = layout.findViewById<TextView>(R.id.a01_03periodsearch)
					textView.setText(getString(R.string.a01_03Search1))
					textView.setOnClickListener(onClick)

					textView = layout.findViewById<TextView>(R.id.a01_03periodsearch2)
					textView.setText(getString(R.string.a01_03Search2))
					textView.setTextColor(colorStateList)

					if (position == idList.size - 1) {
						textView.setOnClickListener(onClick)
					} else {
						textView.setVisibility(View.GONE)
					}
				} else {
					layout = LayoutInflater.from(applicationContext)
						.inflate(R.layout.list_a01_03subject, null, false)
					layout.setOnTouchListener(onTouch)
					val jo = JSONObject(jsonList[position])
					val course_type = jo.getString("course_type")
					val type: View = layout.findViewById(R.id.a01_03listleftview)

					layout.setOnClickListener {
						try {
							val jo = JSONObject(adapter.jsonList.get(position))
							if (jo.getInt("cu_visible") == 0) {
								alert("이 교과목은 LMS 강의실을 활용하지 않습니다.")
							} else if (jo.getInt("setting") == 1) {
								val roles: JSONArray = jo.getJSONArray("roles")
								var role: JSONObject? = null
								val size = roles.length()
								var roleName = "guest"
								var tempRoleName = ""
								for (i in 0 until size) {
									role = roles.getJSONObject(i)
									roleName = role.getString("roleid")
									if (roleName == "1" || roleName == "3" || roleName == "9") {
										tempRoleName = roleName
										break
									} else if (roleName == "5" || roleName == "10" || roleName == "11") {
										tempRoleName = roleName
									} else if (tempRoleName == "") {
										tempRoleName = roleName
									}
								}
								putEnvInt(StaticClass.KEY_SUBJECTTAB, 0)
								if (getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, ".").equals(getEnv(
										StaticClass.KEY_USERTOKEN,
										".."))
								) {
									tempRoleName = "1"
								}
								putEnv(StaticClass.KEY_MYPOSITION, tempRoleName)
								putEnv(StaticClass.KEY_SUBJECTID, jo.getString("id"))
								putEnv(StaticClass.KEY_SUBJECTNAME, jo.getString("fullname"))
								putEnv(StaticClass.KEY_SUBJECTNAMEE, jo.getString("ename"))
								putEnvInt(StaticClass.KEY_SUBJECTCACHEPOSITIONY, listPosition)
								putEnvInt(StaticClass.KEY_SUBJECTCACHEPADDINGY, positionPaddingY)



								var myid=getEnv(StaticClass.KEY_ID, "");
								if (getEnv(StaticClass.KEY_SELECTSCHOOL).equals("가천대학교")  &&(myid.equals("t001") || myid.equals("t002")|| myid.equals("t003"))) {
									val permissionCheck = ContextCompat.checkSelfPermission(applicationContext,
										Manifest.permission.CAMERA)
									if (permissionCheck == PackageManager.PERMISSION_DENIED) {
										if (ActivityCompat.shouldShowRequestPermissionRationale((me as Activity)!!,
												Manifest.permission.CAMERA)
										) {
											ActivityCompat.requestPermissions((me as Activity)!!,
												arrayOf(Manifest.permission.CAMERA),
												1000)
										} else {
											ActivityCompat.requestPermissions((me as Activity)!!,
												arrayOf(Manifest.permission.CAMERA),
												1000)
										}
									} else {
										val intent = Intent(applicationContext, A01_50Camera::class.java)
										intent.putExtra(StaticClass.KEY_ACTIVITYALLKILL,
											StaticClass.NO)
										startActivityForResult(intent, 2000)
										//								startActivity(A01_50Camera.class,0);
									}
									//							reg("first","0000");
								} else

								if (tempRoleName == "1" || tempRoleName == "3" || tempRoleName == "9" || jo.isNull("authentication") || jo.getString(
										"authentication") != "1" || getEnv(StaticClass.KEY_BIOPASSLASTTIME,
										"0").toLong() > Env.getTodateAddDay("yyyyMMddHHmmss", 0)
										.toLong()
								) {
									startActivity(A01_04SubjectDetail::class.java, 0)
								} else {
									val permissionCheck = ContextCompat.checkSelfPermission(
										applicationContext,
										Manifest.permission.CAMERA)
									if (permissionCheck == PackageManager.PERMISSION_DENIED) {
										if (ActivityCompat.shouldShowRequestPermissionRationale((me as Activity?)!!,
												Manifest.permission.CAMERA)
										) {
											//								Env.debug(context,"설명이 필요해");
											ActivityCompat.requestPermissions((me as Activity?)!!,
												arrayOf(Manifest.permission.CAMERA),
												1000)
										} else {
											ActivityCompat.requestPermissions((me as Activity?)!!,
												arrayOf(Manifest.permission.CAMERA),
												1000)
										}
									} else {
										getToken()
									}
								}
								//						getToken();
								//						Env.debug(context,"!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								//						reg("first","0000");
							} else {
								alert(getString(R.string.a01_03activitynosetting))
							}
						} catch (e: java.lang.Exception) {
							EnvError(e)
						}
					}

					if (course_type == "R") {
						type.setBackgroundColor(A901Color.rgb(0x4a, 0xad, 0xb4))
					} else if (course_type == "E") {
						type.setBackgroundColor(A901Color.rgb(0xc8, 0x61, 0x62))
					} else if (course_type == "P") {
						type.setBackgroundColor(A901Color.rgb(0xe7, 0xb5, 0x3c))
					} else if (course_type == "CMS_O") {
						type.setBackgroundColor(A901Color.rgb(0xa8, 0x75, 0xae))
					} else if (course_type == "CMS_E") {
						type.setBackgroundColor(A901Color.rgb(0x94, 0xba, 0x43))
					} else if (course_type == "CMS_M") {
						type.setBackgroundColor(A901Color.rgb(0xb3, 0x98, 0x86))
					} else if (course_type == "CMS_ON") {
						type.setBackgroundColor(A901Color.rgb(0x76, 0x84, 0xb5))
					}
					if (jo.isNull("bunban_code")) {
						(layout.findViewById(R.id.a01_03listmainTextBan) as TextView).setText("")
					} else {
						(layout.findViewById(R.id.a01_03listmainTextBan) as TextView).setText(String.format(
							"[%s]",
							jo.getString("bunban_code")))
					}
					if (language == A01_09Z09Language.ID_row2) {
						(layout.findViewById(R.id.a01_03listmainText) as TextView).setText(jo.getString(
							"ename"))
						(layout.findViewById(R.id.a01_03listsubText) as TextView).setText(jo.getString(
							"fullname"))
					} else {
						//                    ((A802TextView) layoutView.findViewById(ID_mainText)).setText(Html.fromHtml(jo.getString("fullname") + "<br><font color='#cccccc'> " + jo.getString("ename") + "</font>"));
						(layout.findViewById(R.id.a01_03listmainText) as TextView).setText(jo.getString(
							"fullname"))
						(layout.findViewById(R.id.a01_03listsubText) as TextView).setText(jo.getString(
							"ename"))
					}
				}
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

	var onTouch = OnTouchListener { view, event ->
		try {
			val id = view.id
			if (event.action == MotionEvent.ACTION_DOWN) {
				if (id == R.id.a01_03subjectrow) {
					//                        Drawable draw=context.getResources().getDrawable(R.drawable.calendar_list_bar_push);
					//                        draw.setColorFilter(null);
					//                        view.findViewById(ID_background).setBackground(draw);
					view.setBackgroundColor(A901Color.LTGRAY)
					//                    a01_03listmainText.setTextColor(A901Color.getIhwaColor())
				} else if (id == R.id.a01_03timetable) {
					(view as TextView).setTextColor(A901Color.getIhwaColor())
				} else if (id == R.id.a01_03periodsearch || id == R.id.a01_03periodsearch2) {
					(view as TextView).setTextColor(A901Color.getIhwaColor())
				}
			} else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
				if (id == R.id.a01_03subjectrow) {
					view.setBackgroundResource(R.drawable.bottom_line_box_solidwhite)
					//                    a01_03listmainText.setTextColor(A901Color.GRAY)
				} else if (id == R.id.a01_03timetable) {
					(view as TextView).setTextColor(A901Color.GRAY)
				} else if (id == R.id.a01_03periodsearch || id == R.id.a01_03periodsearch2) {
					(view as TextView).setTextColor(A901Color.GRAY)
				}
			}
		} catch (e: java.lang.Exception) {
			Env.error("", e)
		}
		false
	} // OnTouchListener onTouch
}