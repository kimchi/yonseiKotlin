package kr.coursemos.yonsei

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_a00_00_debug.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError

class A00_00Debug : A00_00Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a00_00_debug)
        try{
            quick.setOnClickListener(onClick)
            save.setOnClickListener(onClick)
            isReal.setChecked(StaticClass.isReal)
            isDebug.setChecked(StaticClass.isDebug)
            debugURL.setText("http://manager.coursemos.kr/debug")
        }catch (e:Exception){
            EnvError(e)
        }
    }
    var onClick = View.OnClickListener { view ->
        try {
            val id = view.id
            if (id == R.id.save) {
                StaticClass.isReal = false
                StaticClass.isDebug = true
                Env.debug(applicationContext,
                    isReal.isChecked()
                        .toString() + "!!" + isDebug.isChecked() + "!!" + debugURL.getText()
                        .toString())
                if (isDebug.isChecked()) {
                    StaticClass.isDebug = true
                    StaticClass.isReal = false
                    setEnv(StaticClass.KEY_DEBUGURL, debugURL.getText().toString())
                    setEnv(StaticClass.KEY_DEBUGLASTTIME,
                        Env.getTodateAddDay("yyyyMMddHHmmss", 1))
                } else {
                    StaticClass.isDebug = false
                    StaticClass.isReal = true
                    removeEnv(StaticClass.KEY_DEBUGLASTTIME)
                }
                //					StaticClass.isReal=layout.isReal.isChecked();
                //					StaticClass.isDebug=layout.isDebug.isChecked();
                StaticClass.mydebug = debugURL.getText().toString()
                //					onKeyDown(KeyEvent.KEYCODE_BACK, null);
            } else if (id == R.id.quick) {
                isReal.setChecked(false)
                isDebug.setChecked(true)
                debugURL.setText("http://222.121.122.93:7444/dalbit/debug")
            }
        } catch (e: java.lang.Exception) {
            alert(e.message!!)
            Env.error("", e)
        }
    } // OnClickListener onClick
}