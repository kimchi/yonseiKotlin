package kr.coursemos.myapplication.a99util

import android.content.Context
import android.os.Build
import android.text.Html
import kr.coursemos.yonsei.a99util.Env


fun EnvDebug(msg:String, context:Context?=null){
    Env.debug(context,msg);
}

fun EnvError( e:Exception,msg:String=""){
    Env.error(msg,e);
}





fun String.htmlToString() : String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        return Html.fromHtml(this).toString()
    }
}
