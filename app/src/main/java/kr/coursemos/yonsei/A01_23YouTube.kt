package kr.coursemos.yonsei

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_a01_23_you_tube.*
import kr.co.okins.kc.A00_00Activity
import kr.co.prnd.YouTubePlayerView
import kr.coursemos.yonsei.a99util.CMCommunication
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.myapplication.a99util.EnvError

class A01_23YouTube : A00_00Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_23_you_tube)
        try{
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
            a01_23youtube.play(getEnv(StaticClass.KEY_YOUTUBE, ""),
                object : YouTubePlayerView.OnInitializedListener {
                    override fun onInitializationSuccess(provider: YouTubePlayer.Provider,
                                                         youTubePlayer: YouTubePlayer,
                                                         b: Boolean) {
                        Env.debug(applicationContext, "재생 시작")
                    }
                    override fun onInitializationFailure(provider: YouTubePlayer.Provider,
                                                         youTubeInitializationResult: YouTubeInitializationResult) {
                        Env.debug(applicationContext, "재생 초기화실패")
                    }
                })
        }catch (e:Exception){
            EnvError(e)
        }
    }

    override fun success(what: String?, jyc: CMCommunication?) {
        try {
            if (jyc == null) {
                val id = what!!.toInt()
                if (id == ID_alertOK && isAlertAction) {
                    onKeyDown(KeyEvent.KEYCODE_BACK, null)
                } else if (id == ID_confirmOK) {
                } else if (id == ID_confirmCancel) {
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }
}