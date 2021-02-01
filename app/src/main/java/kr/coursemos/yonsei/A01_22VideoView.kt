package kr.coursemos.yonsei

import android.app.AlertDialog
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.text.Html
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.android.synthetic.main.activity_a01_22_video_view.*
import kotlinx.android.synthetic.main.activity_a01_22control.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.myapplication.a99util.EnvDebug
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMCommunication
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import java.net.URI
import java.net.URLEncoder
import java.util.*
import kotlin.random.Random

class A01_22VideoView : A00_00Activity() {

    //	private SurfaceHolder surfaceHolder=null;
    private val speedString =
        arrayOf("x0.5", "x1.0", "x1.2", "x1.4", "x1.6", "x1.8", "x2.0")
    private val speedFloat =
        floatArrayOf(0.5f, 1.0f, 1.2f, 1.4f, 1.6f, 1.8f, 2.0f)
    private var speedPosition = 1
    lateinit var mediaPlayer: MediaPlayer
    private var audioManager: AudioManager? = null

    private val blockPlayStart = -1
    private val blockPlayEnd = -1

    private var trackID: String? = null
    private var lastDuration = "0"
    private var beforePosition = "0"
    private var lastposition = 0
    private var totalDuration = 0

    private var isContinue = false
    private var isStart = false
    private var isIgnore = false
    private var isCompletion = false
    private var isNormal = false
    private var isProgress = true
    var seekTo = 0
    private var isFirst = true
    private var isAlive = false
    private var isLockOn = false
    private var isMultiPlay = false


    val standardTouchSpeed = 50.0f
    var isComplete = false
    var isTouchAction = false
    var isFirstTouch = false
    var isRightMove = false
    var isVolume = false
    var touchMoveX = 0.0f
    var touchMoveY = 0.0f
    var touchDragPosition = -1
    var controlLayoutCount = 0

    lateinit var a01_19SpeedNext: View
    lateinit var a01_19SpeedPrev: View
    var websocket: WebSocketClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a01_22_video_view)
        try {
            viewWidth = windowManager.defaultDisplay.width
            a01_19SpeedNext = View(this)
            a01_19SpeedNext.id = View.generateViewId()
            a01_19SpeedPrev = View(this)
            a01_19SpeedPrev.id = View.generateViewId()

            a01_19Touch.setOnTouchListener(onTouch)
            a01_19Touch.setOnClickListener(onClick)
            a01_19ControlLayout.visibility = View.INVISIBLE
            a01_19ControlTopLayout.setOnClickListener(onClick)
            a01_19ControlBottomLayout.setOnClickListener(onClick)

            a01_19SpeedButton.setOnTouchListener(onTouch)
            a01_19SpeedButton.setOnClickListener(onClick)
            a01_19PlayButton.setOnTouchListener(onTouch)
            a01_19PlayButton.setOnClickListener(onClick)
            a01_19PrevButton.setOnTouchListener(onTouch)
            a01_19PrevButton.setOnClickListener(onClick)
            a01_19NextButton.setOnTouchListener(onTouch)
            a01_19NextButton.setOnClickListener(onClick)
            a01_19VolumeButton.setOnTouchListener(onTouch)
            a01_19VolumeButton.setOnClickListener(onClick)
            a01_19LockButton.setOnTouchListener(onTouch)
            a01_19LockButton.setOnClickListener {
                a01_19LockLayout.setOnClickListener(onClick)
                a01_19LockLayout.setImageResource(R.drawable.layer_lock)
                a01_19LockLayout.setVisibility(View.VISIBLE)
                val anim = getAlphaAnimation(true)
                a01_19LockLayout.startAnimation(anim)
                onClick.onClick(a01_19Touch)
                isLockOn = true
            }


            a01_19VolumeLayout.setOnClickListener(onClick)
            a01_19VolumeLayout.visibility = View.INVISIBLE
            a01_19VolumeUpText.setOnTouchListener(onTouch)
            a01_19VolumeUpText.setOnClickListener(onClick)
            a01_19VolumeUpArrow.setOnTouchListener(onTouch)
            a01_19VolumeUpArrow.setOnClickListener(onClick)
            a01_19VolumeDownArrow.setOnTouchListener(onTouch)
            a01_19VolumeDownArrow.setOnClickListener(onClick)
            a01_19VolumeDownText.setOnTouchListener(onTouch)
            a01_19VolumeDownText.setOnClickListener(onClick)
            a01_19LockLayout.setOnClickListener(onClick)



            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
            trackID = getEnv(StaticClass.KEY_VIDEOTRACKID, "")
            isComplete = getEnvBoolean(StaticClass.KEY_VIDEOISCOMPLETE, false)
            isProgress = getEnvBoolean(StaticClass.KEY_VIDEOISPROGRESS, true)
            isNormal = getEnvBoolean(StaticClass.KEY_VIDEOISNORMAL, false)
            if (isNormal) {
                isComplete = true
            }
            if (isComplete) {
            } else {
                lastposition = getEnv(StaticClass.KEY_VIDEOLASTPOSTION, "0").toInt() * 1000
            }

            Env.debug(applicationContext, "Last Position : $lastposition")

            playVideo()
            setVolume()
            dismiss()
            a01_19LockLayout.visibility = View.INVISIBLE
            a01_19TouchNotiPic.visibility = View.GONE
            a01_19TouchNotiText.setText("")
            val power = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (power.isScreenOn) {
                if (lastposition == 0 || isComplete) {
                    success(Integer.toString(ID_confirmCancel), null)
                } else {
                    val msecTosec = lastposition / 1000.toLong()
                    val hour = msecTosec / 3600
                    val min = msecTosec % 3600 / 60
                    val sec = msecTosec % 60
                    isContinue = true
                    confirm(String.format("""
                                ${getString(R.string.a01_19activityContinue)}
                                %02d:%02d:%02d
                                """.trimIndent(), hour, min, sec))
                }
            } else {
                return
            }
            StaticClass.history.removeAt(StaticClass.history.size - 1)
        } catch (e: Exception) {
            EnvError(e)
        }
    }

    override fun onPause() {
        try {
            if (a01_19VideoView != null) {
                isFirst = true
                if (getEnvInt(StaticClass.KEY_VIDEOPAUSEPOSITION, 0) > 0) {
                } else {
                    setEnvInt(StaticClass.KEY_VIDEOPAUSEPOSITION, lastDuration.toInt() * 1000)
                    setEnvBoolean(StaticClass.KEY_VIDEOPAUSEISSTART, a01_19VideoView.isPlaying())
                }
                setEnv(StaticClass.KEY_VIDEOLASTPOSTION, lastDuration)
                a01_19VideoView.pause()
                a01_19VideoView.stopPlayback()
            }
            isAlive = false
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        super.onPause()
    }

    override fun finish() {
        try {
            isIgnore = true
            if (a01_19VideoView != null) {
                var lastDuration = lastDuration
                if (isCompletion) {
                    lastDuration = (a01_19VideoView.getDuration().toLong() / 1000).toString()
                } else {
                }
                setState("99", lastDuration, lastDuration)
                a01_19VideoView.stopPlayback()
            }
            isAlive = false
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        super.finish()
    }

    @Throws(java.lang.Exception::class)
    fun playVideo() {
        prd(false)
        a01_19VideoView.setOnPreparedListener(OnPreparedListener { mp ->
            try {
                mediaPlayer = mp
                mediaPlayer.setOnTimedTextListener(OnTimedTextListener { mp, text ->
                    Env.debug(applicationContext, "" + text + System.currentTimeMillis())
                })
                mediaPlayer.setOnBufferingUpdateListener(OnBufferingUpdateListener { mp, percent ->
                    try {
                        if (percent == 100) {
                            bufferComplete()
                        } else {
                            if (prdDialog == null) {
                                //										prd(false);
                            }
                            //						prd.setMessage(getString(R.string.a00_00activityWait) + "..." + percent + "%");
                        }
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                })
                mediaPlayer.setOnSeekCompleteListener(OnSeekCompleteListener {
                    try {
                        updateTime()
                        if (isFirst) {
                        } else {
                            if (a01_19VideoView.isPlaying()) {
                                start(lastDuration, lastDuration)
                            } else {
                                dismiss()
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Env.error("", e)
                    }
                })
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.0f))
                totalDuration = a01_19VideoView.getDuration()
                a01_19Seekbar.setMax(totalDuration)
                a01_19TotalDuration.setText(getHHMMSS(totalDuration.toLong()))
                var diffPosition: Int = getEnvInt(StaticClass.KEY_VIDEOPAUSEPOSITION, 0) - 1000
                if (diffPosition < 0) {
                    diffPosition = 0
                }
                if (isContinue) {
                    isContinue = false
                    diffPosition = lastposition
                }
                if (diffPosition > 0) {
                    //						success(Integer.toString(ID_confirmCancel), null);
                    a01_19VideoView.seekTo(diffPosition)
                    prd(false)
                }
                bufferComplete()
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
        })
        a01_19VideoView.setOnCompletionListener(OnCompletionListener {
            try {
                isAlertAction = true
                val lastDuration =
                    java.lang.Long.toString(a01_19VideoView.getDuration().toLong() / 1000 + 1)
                isCompletion = true
                setState("10", beforePosition, lastDuration)
                alert(getString(R.string.a01_19activityComplete))
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
        })
        a01_19VideoView.setOnErrorListener(OnErrorListener { mp, what, extra ->
            try {
                if (StaticClass.isDebug) {
                    Env.debug(applicationContext, "$mp!!$what!!$extra")
                }
                onKeyDown(KeyEvent.KEYCODE_BACK, null)
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
            false
        })
        a01_19VideoView.setOnInfoListener(OnInfoListener { mp, what, extra ->
            try {
                if (what == MEDIA_INFO_VIDEO_RENDERING_START) {
                }
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
            false
        })
        a01_19Seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                try {
                    move(seekBar.progress)
                } catch (e: java.lang.Exception) {
                    Env.error("", e)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            }
        })
        //		layout.a01_19VideoView.setVideoURI(Uri.parse("http://147.47.106.152:8080/streams/79a7c58a-c923-4be5-bf31-fa08864007d5/2014/05/22/ba41524e-9029-45af-a373-39b74373c802/ddd646af-43da-42a8-89aa-9eb979d69e14.mp4"));
        //		a01_19VideoView.setVideoURI(Uri.parse(getEnv(StaticClass.KEY_VIDEOURL, "")));
        //		layout.a01_19VideoView.requestFocus();
    }

    var mProgressHandler: Handler = object : Handler() {
        private var statCount = 0
        var secCount = 0
        var lockOnCount = 0
        override fun handleMessage(msg: Message) {
            try {
                secCount++
                if (secCount == 5) {
                    statCount++
                    secCount = 0
                    if (isLockOn) {
                        if (a01_19LockLayout.getVisibility() === View.VISIBLE) {
                            lockOnCount++
                            if (lockOnCount == 3) {
                                val anim: Animation = getAlphaAnimation(false)
                                anim.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation) {}
                                    override fun onAnimationRepeat(animation: Animation) {}
                                    override fun onAnimationEnd(animation: Animation) {
                                        a01_19LockLayout.setVisibility(View.INVISIBLE)
                                    }
                                })
                                a01_19LockLayout.startAnimation(anim)
                                lockOnCount = 0
                            }
                        }
                    } else {
                        lockOnCount = 0
                    }
                    if (a01_19ControlLayout.getVisibility() === View.VISIBLE) {
                        controlLayoutCount++
                        if (controlLayoutCount === 5) {
                            isFirstTouch = true
                            onClick.onClick(a01_19Touch)
                        }
                    } else {
                        controlLayoutCount = 0
                    }
                }
                if (a01_19VideoView != null) {
                    updateTime()
                }
                if (statCount >= 60) {
                    setState("8", lastDuration, lastDuration)
                    statCount = 0
                }
                if (isMultiPlay) {
                    toast("다중 동영상 플레이가 감지되었습니다.\n현재 창의 동영상 플레이를 중단합니다.")
                    isAlive = false
                    if (websocket == null || websocket!!.isClosed) {
                    } else {
                        websocket!!.close();
                    }
                    onKeyDown(KeyEvent.KEYCODE_BACK, null)
                } else if (isAlive) {
                    sendEmptyMessageDelayed(0, 200)
                } else {
                    if (websocket == null || websocket!!.isClosed) {
                    } else {
                        websocket!!.close();
                    }
                }
            } catch (e: java.lang.Exception) {
                Env.error("", e)
            }
        }
    }

    @Throws(java.lang.Exception::class)
    fun updateTime() {
        val duration: Int = a01_19VideoView.getCurrentPosition()
        if (lastposition < duration) {
            lastposition = duration
        }
        val tempLastDuration = lastDuration.toLong()
        val currentDuration = duration / 1000.toLong()
        lastDuration = java.lang.Long.toString(currentDuration)
        if (tempLastDuration != currentDuration) {
            dismiss()
            if (isIgnore) {
                if (a01_19VideoView.isPlaying()) {
                    start(lastDuration, lastDuration)
                }
            }
        }
        if (isTouchAction) {
            val hhmmss: String = getHHMMSS(touchDragPosition.toLong())
            var currenthhmmss: String = getHHMMSS(duration.toLong())
            a01_19Seekbar.setProgress(touchDragPosition as Int)
            a01_19CurrentPosition.setText(hhmmss)
            currenthhmmss = if (touchDragPosition > duration) {
                "+" + getHHMMSS(touchDragPosition.toLong() - duration)
            } else {
                "-" + getHHMMSS(duration - touchDragPosition.toLong())
            }
            a01_19TouchNotiText.setText(Html.fromHtml("<big>$hhmmss</big><br><small>[$currenthhmmss]</small>"))
        } else {
            a01_19Seekbar.setProgress(duration)
            a01_19CurrentPosition.setText(getHHMMSS(duration.toLong()))
        }
        if (blockPlayEnd > -1) {
            if (duration >= blockPlayEnd) {
                toast("반복시작")
                move(blockPlayStart)
            }
        }
    }

    @Throws(java.lang.Exception::class)
    fun start(from: String, to: String) {
        setState("3", from, to)
        dismiss()
        a01_19PlayButton.setImageResource(R.drawable.pause)
        isIgnore = false
    }

    @Throws(java.lang.Exception::class)
    fun nextSpeed() {
        speedPosition++
        if (speedPosition >= speedFloat.size) {
            speedPosition = speedFloat.size - 1
        } else {
            setSpeed()
        }
    }

    @Throws(java.lang.Exception::class)
    fun prevSpeed() {
        speedPosition--
        if (speedPosition < 0) {
            speedPosition = 0
        } else {
            setSpeed()
        }
    }

    @Throws(java.lang.Exception::class)
    fun setSpeed() {
        a01_19SpeedButton.setText(speedString[speedPosition])
        a01_19TouchNotiText.setText(speedString[speedPosition])
        mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speedFloat[speedPosition])
    }

    @Throws(java.lang.Exception::class)
    fun move(selectPosition: Int) {
        var selectPosition = selectPosition
        if (lastposition >= selectPosition || isComplete) {
        } else {
            alert(getHHMMSS(lastposition.toLong()) + getString(R.string.a01_19activityhutch))
            selectPosition = lastposition
        }
        stop(lastDuration, java.lang.Long.toString(selectPosition / 1000.toLong()))
        prd(false)
        a01_19VideoView.seekTo(selectPosition)
    }

    @Throws(java.lang.Exception::class)
    fun stop(from: String, to: String) {
        isIgnore = true
        setState("2", from, to)
        a01_19PlayButton.setImageResource(R.drawable.play)
    }

    @Throws(java.lang.Exception::class)
    fun getHHMMSS(ms: Long): String {
        val hour = ms / 3600000
        val min = ms % 3600000 / 60000
        val sec = ms % 60000 / 1000
        return String.format("%02d:%02d:%02d", hour, min, sec)
    }

    @Throws(java.lang.Exception::class)
    fun setState(state: String, form: String, to: String) {
        if (isNormal) {
            return
        }
        beforePosition = to
        if (isComplete) {
            if (state == "3") {
                if (isStart) {
                    return
                }
                isStart = true
            } else if (state == "99") {
            } else {
                return
            }
        }
        var isClose = true;
        when (websocket) {
            null -> {
            }
            else -> {
                if (websocket!!.isOpen) {
                    isClose = false
                    websocket!!.send("2")
                }
            }
        }
        if(state == "99"){}
        else if (isClose) {
            try {
                val temp="${getEnv(StaticClass.KEY_USERSEQ)}.${Calendar.getInstance().timeInMillis / 1000}"
                var encode = URLEncoder.encode(Env.encryptAES256(temp),"UTF-8")
                //				encode = Env.encryptAES256(temp)

                var origin = getEnv(StaticClass.KEY_SELECTSCHOOLURL).split("/webservice/rest/server.php")[0]
                var port = 28000 + Random(System.currentTimeMillis() % 1000).nextInt(50)

                EnvDebug("${temp}\n wss://lc.coursemos.kr:${port}/socket.io/checker/?token=${encode.trim()}&origin=${origin}&EIO=3&transport=websocket")
                websocket = object :
                    WebSocketClient(URI("wss://lc.coursemos.kr:${port}/socket.io/checker/?token=${encode.trim()}&origin=${origin}&EIO=3&transport=websocket")) {
                    override fun onOpen(handshakedata: ServerHandshake?) {
                        EnvDebug("onOpen : !!!!!!!!!!!!!!!!!!!", applicationContext)
                    }

                    override fun onClose(code: Int, reason: String?, remote: Boolean) {
                        EnvDebug("onClose : ${reason}", applicationContext)
                    }

                    override fun onMessage(message: String?) {
                        when (message) {
                            null -> {
                            }
                            "41" -> {
                                websocket!!.close()
                                websocket = null
                            }
                            else -> {
                                if (message.indexOf("42") == 0) {
                                    var ja = JSONArray(message.substring(2))
                                    var jo = ja.getJSONObject(1);
                                    if (jo.getString("action").equals("pause")) {
                                        websocket!!.close()
                                        websocket = null
                                        isMultiPlay = true
                                    }
                                }
                            }
                        }
                        EnvDebug("onMessage : $message", applicationContext)
                    }

                    override fun onError(ex: java.lang.Exception?) {
                        EnvDebug("onError : ${ex.toString()}")
                    }
                }
                websocket!!.connect()
            } catch (e: java.lang.Exception) {
                EnvError(e)
            }
        }
        val shm = StringHashMap()
        shm["trackid"] = trackID
        shm["state"] = state
        shm["positionfrom"] = form
        shm["positionto"] = to
        if (isProgress) {
            object : A903LMSTask(applicationContext, API_VIDEOCONTROL, shm) {
                @Throws(java.lang.Exception::class)
                override fun success() {
                }

                @Throws(java.lang.Exception::class)
                override fun fail(errmsg: String) {
                    if (StaticClass.isDebug) {
                        Env.error(errmsg, java.lang.Exception())
                    }
                }
            }
        }
    }

    @Throws(java.lang.Exception::class)
    fun bufferComplete() {
        if (isFirst) {
            if (isAlive) {
            } else {
                isAlive = true
                mProgressHandler.sendEmptyMessageDelayed(0, 200)
            }
            isFirst = false
            val pausePosition: Int = getEnvInt(StaticClass.KEY_VIDEOPAUSEPOSITION, 0) / 1000
            if (pausePosition > 0) {
                dismiss()
                if (getEnvBoolean(StaticClass.KEY_VIDEOPAUSEISSTART, false)) {
                    a01_19PlayButton.setImageResource(R.drawable.pause)
                    isIgnore = false
                } else {
                    start(java.lang.Long.toString(pausePosition.toLong()),
                        java.lang.Long.toString(pausePosition.toLong()))
                }
            } else {
                start(java.lang.Long.toString(lastposition / 1000.toLong()),
                    java.lang.Long.toString(lastposition / 1000.toLong()))
            }
        } else {
            dismiss()
        }
        setEnvInt(StaticClass.KEY_VIDEOPAUSEPOSITION, 0)
        setEnvBoolean(StaticClass.KEY_VIDEOPAUSEISSTART, false)
    }

    fun setVolume() {
        val volume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        a01_19VolumeText.setText(getVolumePercent(volume))
        a01_19TouchNotiText.setText(getVolumePercent(volume))
        if (volume == audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            a01_19VolumeUpText.setText(" ")
        } else {
            a01_19VolumeUpText.setText(getVolumePercent(volume + 1))
        }
        if (volume == 0) {
            a01_19VolumeDownText.setText(" ")
        } else {
            a01_19VolumeDownText.setText(getVolumePercent(volume - 1))
        }
    }

    private fun getVolumePercent(volume: Int): String? {
        return Math.round(volume.toDouble() / 15.toDouble() * 100).toString() + "%"
    }

    fun volumeUpClicked() {
        audioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
        setVolume()
    }

    fun volumeDownClicked() {
        audioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
        setVolume()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            if (isLockOn) {
                onClick.onClick(a01_19Touch)
                return true
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        return super.onKeyDown(keyCode, event)
    }

    protected var tempView: View? = null
    protected var tempEvent: MotionEvent? = null
    var viewWidth: Int = 0
    var onClick: View.OnClickListener = View.OnClickListener {
        try {
            val id: Int = it.getId()
            if (isLockOn) {
                if (id == R.id.a01_19LockLayout) {
                    a01_19LockLayout.setOnClickListener(null)
                    a01_19LockLayout.setImageResource(R.drawable.layer_unlock)
                    val anim = getAlphaAnimation(false, 500)
                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationRepeat(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            a01_19LockLayout.setVisibility(View.INVISIBLE)
                            a01_19LockLayout.requestLayout()
                            isLockOn = false
                        }
                    })
                    a01_19LockLayout.startAnimation(anim)
                } else {
                    if (a01_19LockLayout.getVisibility() === View.INVISIBLE) {
                        val anim = getAlphaAnimation(true)
                        a01_19LockLayout.setVisibility(View.VISIBLE)
                        a01_19LockLayout.startAnimation(anim)
                    }
                }
            } else {
                if (id == R.id.a01_19PlayButton) {
                    if (a01_19VideoView.isPlaying()) {
                        stop(lastDuration, lastDuration)
                        a01_19VideoView.pause()
                    } else {
                        start(lastDuration, lastDuration)
                        a01_19VideoView.start()
                    }
                } else if (id == R.id.a01_19Touch) {
                    if (isFirstTouch) {
                        if (a01_19ControlLayout.getVisibility() === View.VISIBLE) {
                            a01_19VolumeLayout.setVisibility(View.INVISIBLE)
                            a01_19ControlLayout.setVisibility(View.INVISIBLE)
                        } else {
                            a01_19ControlLayout.setVisibility(View.VISIBLE)
                        }
                    }
                } else if (id == R.id.a01_19SpeedButton) {
                    if (isComplete) {
                        AlertDialog.Builder(me).setItems(speedString) { dialog, which ->
                            try {
                                speedPosition = which
                                setSpeed()
                            } catch (e: java.lang.Exception) {
                                Env.error("", e)
                            }
                        }.show()
                    } else {
                        alert(getString(R.string.a01_19activityNotComplete))
                    }
                } else if (id == R.id.a01_19PrevButton) {
                    if (isTouchAction) {
                        if (touchDragPosition === -1) {
                            touchDragPosition = lastDuration.toInt() * 1000
                        } else {
                            touchDragPosition -= 10000
                        }
                        if (touchDragPosition <= 0) {
                            touchDragPosition = 0
                        }
                    } else {
                        move((lastDuration.toInt() - 10) * 1000)
                    }
                } else if (id == R.id.a01_19NextButton) {
                    if (isTouchAction) {
                        if (touchDragPosition === -1) {
                            touchDragPosition = lastDuration.toInt() * 1000
                        } else {
                            touchDragPosition += 10000
                        }
                        if (touchDragPosition >= totalDuration - 2000) {
                            touchDragPosition = totalDuration - 2000
                        }
                    } else {
                        if (touchDragPosition === -1) {
                            move((lastDuration.toInt() + 10) * 1000)
                        } else {
                            move(touchDragPosition)
                            touchDragPosition = -1
                        }
                    }
                } else if (id == R.id.a01_19LockButton) {
                } else if (id == R.id.a01_19VolumeButton) {
                    if (a01_19VolumeLayout.getVisibility() === View.VISIBLE) {
                        a01_19VolumeLayout.setVisibility(View.INVISIBLE)
                    } else {
                        a01_19VolumeLayout.setVisibility(View.VISIBLE)
                    }
                } else if (id == R.id.a01_19VolumeUpArrow || id == R.id.a01_19VolumeUpText) {
                    volumeUpClicked()
                } else if (id == R.id.a01_19VolumeDownArrow || id == R.id.a01_19VolumeDownText) {
                    volumeDownClicked()
                } else if (id == a01_19SpeedNext.id) {
                    nextSpeed()
                } else if (id == a01_19SpeedPrev.id) {
                    prevSpeed()
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
    }
    var onTouch = OnTouchListener { view, event ->
        try {
            val id = view.id
            controlLayoutCount = 0
            if (event.action == MotionEvent.ACTION_DOWN) {
                tempView = view
                tempEvent = event
                if (id == R.id.a01_19Touch) {
                    touchMoveX = event.rawX
                    touchMoveY = event.rawY
                    isFirstTouch = true
                    isVolume = touchMoveX > viewWidth / 2
                } else if (id == R.id.a01_19PlayButton) {
                    if (a01_19VideoView.isPlaying()) {
                        a01_19PlayButton.setImageResource(R.drawable.pause_on)
                    } else {
                        a01_19PlayButton.setImageResource(R.drawable.play_on)
                    }
                } else if (id == R.id.a01_19SpeedButton) {
                    a01_19SpeedButton.setBackgroundResource(R.drawable.speed_bg_on)
                } else if (id == R.id.a01_19PrevButton) {
                    a01_19PrevButton.setImageResource(R.drawable.pre_on)
                } else if (id == R.id.a01_19NextButton) {
                    a01_19NextButton.setImageResource(R.drawable.next_on)
                } else if (id == R.id.a01_19LockButton) {
                    a01_19LockButton.setImageResource(R.drawable.lock_on)
                } else if (id == R.id.a01_19VolumeButton) {
                    a01_19VolumeButton.setImageResource(R.drawable.volume_on)
                } else if (id == R.id.a01_19VolumeUpArrow || id == R.id.a01_19VolumeUpText) {
                    a01_19VolumeUpArrow.setImageResource(R.drawable.arrow_up_on)
                } else if (id == R.id.a01_19VolumeDownArrow || id == R.id.a01_19VolumeDownText) {
                    a01_19VolumeDownArrow.setImageResource(R.drawable.arrow_down_on)
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (id == R.id.a01_19Touch) {
                    isTouchAction = false
                    a01_19TouchNotiText.setText("")
                    a01_19TouchNotiPic.visibility = View.GONE
                    a01_19TouchNotiLayout.requestLayout()
                    if (touchDragPosition > -1) {
                        onClick?.onClick(a01_19NextButton)
                    }
                } else if (id == R.id.a01_19PlayButton) {
                    if (a01_19VideoView.isPlaying()) {
                        a01_19PlayButton.setImageResource(R.drawable.pause)
                    } else {
                        a01_19PlayButton.setImageResource(R.drawable.play)
                    }
                } else if (id == R.id.a01_19SpeedButton) {
                    a01_19SpeedButton.setBackgroundResource(R.drawable.speed_bg)
                } else if (id == R.id.a01_19PrevButton) {
                    a01_19PrevButton.setImageResource(R.drawable.pre)
                } else if (id == R.id.a01_19NextButton) {
                    a01_19NextButton.setImageResource(R.drawable.next)
                } else if (id == R.id.a01_19LockButton) {
                    a01_19LockButton.setImageResource(R.drawable.lock)
                } else if (id == R.id.a01_19VolumeButton) {
                    a01_19VolumeButton.setImageResource(R.drawable.volume)
                } else if (id == R.id.a01_19VolumeUpArrow || id == R.id.a01_19VolumeUpText) {
                    a01_19VolumeUpArrow.setImageResource(R.drawable.arrow_up)
                } else if (id == R.id.a01_19VolumeDownArrow || id == R.id.a01_19VolumeDownText) {
                    a01_19VolumeDownArrow.setImageResource(R.drawable.arrow_down)
                }
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                if (id == R.id.a01_19Touch) {
                    val tempMoveX = event.rawX
                    val tempMoveY = event.rawY
                    val speedX: Float = Math.abs(touchMoveX - tempMoveX)
                    val speedY: Float = Math.abs(touchMoveY - tempMoveY)
                    if (isFirstTouch) {
                        if (speedX > standardTouchSpeed / 2) {
                            isTouchAction = true
                            isRightMove = true
                            isFirstTouch = false
                        } else if (speedY > standardTouchSpeed) {
                            isRightMove = false
                            isFirstTouch = false
                        }
                    }
                    if (isFirstTouch) {
                    } else {
                        if (isRightMove) {
                            if (speedX > standardTouchSpeed / 2) {
                                if (touchMoveX < tempMoveX) {
                                    onClick.onClick(a01_19NextButton)
                                } else {
                                    onClick.onClick(a01_19PrevButton)
                                }
                                touchMoveX = tempMoveX
                                touchMoveY = tempMoveY
                            }
                        } else {
                            if (speedY > standardTouchSpeed) {
                                if (isVolume) {
                                    a01_19TouchNotiPic.setImageResource(R.drawable.layer_volum)
                                    a01_19TouchNotiPic.visibility = View.VISIBLE
                                    a01_19TouchNotiLayout.requestLayout()
                                    if (touchMoveY < tempMoveY) {
                                        onClick?.onClick(a01_19VolumeDownArrow)
                                    } else {
                                        onClick?.onClick(a01_19VolumeUpArrow)
                                    }
                                } else {
                                    if (isComplete) {
                                        a01_19TouchNotiPic.setImageResource(R.drawable.layer_speed)
                                        a01_19TouchNotiPic.visibility = View.VISIBLE
                                        a01_19TouchNotiLayout.requestLayout()
                                        if (touchMoveY < tempMoveY) {
                                            onClick?.onClick(a01_19SpeedPrev)
                                        } else {
                                            onClick?.onClick(a01_19SpeedNext)
                                        }
                                    }
                                }
                                touchMoveX = tempMoveX
                                touchMoveY = tempMoveY
                            }
                        }
                    }
                    //Env.debug(context, Math.abs(touchMoveX-tempMoveX)+"!!"+Math.abs(touchMoveY-tempMoveY));
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error("", e)
        }
        false
    } //OnTouchListener onTouch

    override fun success(what: String?, jyc: CMCommunication?) {
        try {
            if (jyc == null) {
                val id = what!!.toInt()
                if (id == ID_confirmOK) {
                    prd(false)
                    a01_19VideoView.setVideoURI(Uri.parse(getEnv(StaticClass.KEY_VIDEOURL, "")))
                    //					layout.a01_19VideoView.setVideoURI(Uri.parse("http://stream.ewha.ac.kr:1935/streams/_definst_/520c3ff9-d98c-438d-b819-3834680f0d7f/2016/03/01/8bcad4dc-692c-4bde-98b1-e00f522012e5/3d004301-91d2-4d19-a993-97b4a221699b.mp4/playlist.m3u8"));
                    //					Env2.debug("111111\n" + sp.getString(StaticClass.KEY_VIDEOURL, ""));
                    //					layout.a01_19VideoView.seekTo(lastposition);
                } else if (id == ID_confirmCancel) {
                    prd(false)
                    isContinue = false
                    a01_19VideoView.setVideoURI(Uri.parse(getEnv(StaticClass.KEY_VIDEOURL, "")))
                    //					layout.a01_19VideoView.setVideoURI(Uri.parse("http://stream.ewha.ac.kr:1935/streams/_definst_/520c3ff9-d98c-438d-b819-3834680f0d7f/2016/03/01/8bcad4dc-692c-4bde-98b1-e00f522012e5/3d004301-91d2-4d19-a993-97b4a221699b.mp4/playlist.m3u8"));
                    //					Env2.debug("111111\n" + sp.getString(StaticClass.KEY_VIDEOURL, ""));
                } else if (id == ID_alertOK && isAlertAction) {
                    onKeyDown(KeyEvent.KEYCODE_BACK, null)
                }
            }
        } catch (e: java.lang.Exception) {
            Env.error(e)
        }
    }
}