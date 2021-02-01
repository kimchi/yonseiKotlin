package kr.coursemos.yonsei

import android.os.Bundle
import android.view.View
import android.widget.TextView
import kr.coursemos.myapplication.a99util.EnvError
import java.lang.Exception

class A01_13Z02WebView : A01_13WebView() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            val title = findViewById<TextView>(R.id.a00_title)
            when (title) {
                null -> {
                }
                else -> {
                    (title.parent as View).visibility=View.GONE
                }
            }
        }catch (e:Exception){
            EnvError(e)
        }
    }
}