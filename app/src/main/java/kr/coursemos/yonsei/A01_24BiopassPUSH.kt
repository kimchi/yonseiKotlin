package kr.coursemos.yonsei

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMCommunication
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError
import kr.smartauth.biopass.sdk.domain.AppConstants.IntentData
import kr.smartauth.biopass.sdk.domain.AuthenticationType
import kr.smartauth.biopass.sdk.main.BioPassMainActivity
import java.util.*

class A01_24BiopassPUSH : A00_00Activity() {
    companion object{
        var isPUSH = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_24_biopass_p_u_s_h)
        try{
            removeEnv(StaticClass.KEY_PUSHSCREEN)
            if (isPUSH) {
                isPUSH = false
                val arrayList = ArrayList<String>()
                val permissionCheck =
                    ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((me as Activity)!!,
                            Manifest.permission.CAMERA)
                    ) {
                        //								Env.debug(context,"설명이 필요해");
                        ActivityCompat.requestPermissions((me as Activity)!!,
                            arrayOf(Manifest.permission.CAMERA),
                            1000)
                    } else {
                        ActivityCompat.requestPermissions((me as Activity)!!,
                            arrayOf(Manifest.permission.CAMERA),
                            1000)
                    }
                } else {
                    getToken()
                }
            }

        }catch (e:Exception){
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun getToken() {
        val mUserId: String = getEnv(StaticClass.KEY_USERSEQ, "kimchi")
        val mUnivCode: String = getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "kimchi")
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
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        try {
            if (requestCode == 1000) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //					alert("카메라체크하자!!");
                    getToken()
                    //					https://ubion.uniid.co/auth/ubion/onboarding/users/1131306/tokens
                } else {
                    alert(getString(R.string.a01_03subjectlistcamerapermission))
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try{
            if (data != null) {
                if (resultCode == RESULT_CANCELED) {
                    val err = data.getStringExtra("ErrorMessage")
                    Env.debug(applicationContext, "ERROR!!$err")
                    alert(err)
                } else if (resultCode == Activity.RESULT_OK) {
                    when (requestCode) {
                        1000 -> {
                            // 아래의 Intent data로 인증 결과 메시지를 받음
                            val authentication = data.getStringExtra(IntentData.AUTHENTICATION)
                            object : A903LMSTask(applicationContext, API_BIOPASSPUSH, null) {
                                @Throws(java.lang.Exception::class)
                                override fun success() {
                                    alert(authentication)
                                    dismiss()
                                }

                                @Throws(java.lang.Exception::class)
                                override fun fail(errmsg: String) {
                                    alert(errmsg)
                                    dismiss()
                                }
                            }
                        }
                        else -> {
                            val aaa = data.getStringExtra(IntentData.AUTHENTICATION)
                            alert(aaa)
                        }
                    }
                }
            }
        }catch (e:java.lang.Exception){
            EnvError(e)
        }
    }
    override fun success(what: String?, jyc: CMCommunication?) {
        try {
            if (jyc == null) {
                val id = what!!.toInt()
                if (id == ID_alertOK) {
                    finish()
                } else if (id == ID_confirmOK) {
                } else if (id == ID_confirmCancel) {
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }

}