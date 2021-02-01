package kr.coursemos.yonsei;

import android.app.Application;
import android.content.Context;

import kr.coursemos.yonsei.a99util.Env;

public class DefaultApp extends Application {
    private static Context appContext=null;
    public static Context getAppContext(){
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContext=getApplicationContext();
//        ColorDrawable aaa= new ColorDrawable();
//        aaa.setColor(A901Color.get6a6c73());
//        StateListDrawable states = new StateListDrawable();
//        states.addState(new int[] {android.R.attr.state_pressed},
//                aaa);
//        states.addState(new int[] {android.R.attr.state_focused},
//                getResources().getDrawable(R.drawable.focused));
//        states.addState(new int[] { },
//                getResources().getDrawable(R.drawable.normal));
//        imageView.setImageDrawable(states);
        Env.debug(null,appContext.getPackageName());
    }
}
