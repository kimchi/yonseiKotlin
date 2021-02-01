package kr.coursemos.yonsei

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_a01_09_z06_version.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError

class A01_09Z06Version : A00_00Activity() {
    private var firstClickTime: Long = 0
    private var clickCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_09_z06_version)
        initBottom("")
        try{
            a01_0906text1.text=
                getString(R.string.a01_09z06layoutCurrentVersion) + " " + StaticClass.version

            a01_0906text2.text=
                getString(R.string.a01_09z06layoutLastVersion) + " " + StaticClass.version
            a01_0906pic.setOnClickListener {
                try{
                    val currentTime = System.currentTimeMillis()
                    if (firstClickTime < currentTime) {
                        firstClickTime = currentTime + 2000
                        clickCount = 1
                    } else {
                        clickCount++
                        if (clickCount > 4) {
                            startActivity(A00_00Debug::class.java, 0)
                        }
                    }
                }catch (e:java.lang.Exception){
                    EnvError(e)
                }

            }
        }catch (e:Exception){
            EnvError(e)
        }
    }
}