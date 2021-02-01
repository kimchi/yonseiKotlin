package kr.coursemos.yonsei

import android.os.Bundle
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap

class A01_01Scheme : A00_00Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_01_scheme)

        try {
            val uri = intent.data
            var url = uri.toString()
            url = url.replaceFirst("coursemos2://".toRegex(), "")
            var params = url.split("\\?".toRegex()).toTypedArray()
            if (params.size == 1) {
            } else {
                url = url.replaceFirst(params[0] + "\\?".toRegex(), "")
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
                Env.debug(applicationContext, "$url $shm".trimIndent())
            }
            if (shm.containsKey("ACCESS_TOKEN")) {
                setEnv(StaticClass.KEY_ACCESS_TOKEN,
                    getEnv(StaticClass.KEY_SUBJECTID, ""))
                startActivity(A01_03SubjectList::class.java, 0)
            } else {
                if (shm.containsKey("index")) {
                    setEnv(StaticClass.KEY_SCHEMEINDEX, shm.getString("index"))
                }
                if (shm.containsKey("param") && shm.getString("param") == "snu") {
                    setEnvBoolean(StaticClass.KEY_ISSNU, true)
                } else {
                    setEnvBoolean(StaticClass.KEY_ISSNU, false)
                }
                startActivity(A01_00Intro::class.java, 0)
            }
        } catch (e: Exception) {
            Env.error("", e)
        }

    }
}