package kr.coursemos.yonsei

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Message
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.net.URLDecoder

class A10_00FCMReceiver : FirebaseMessagingService() {

    companion object {
        var mAudio: MediaPlayer? = null

        fun getmAudio():MediaPlayer?{
            return mAudio
        }
        fun setmAudio(mp:MediaPlayer?){
            mAudio=mp
        }
        fun getNotificationIcon(): Int {
            val useSilhouette = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            return if (useSilhouette) R.drawable.ic_stat_notify_coursemos else R.drawable.icic
        }
    }


    override fun onNewToken(token: String) {
        try{
            val sp = getSharedPreferences(StaticClass.KEY_SharedPreferences, Service.MODE_PRIVATE)
            sp.edit().putString(StaticClass.KEY_REGID, token).apply()
            if (sp.getString(StaticClass.KEY_ID, "") == "") {
            } else {
                object : A903LMSTask(this, API_PUSHSET, null) {
                    @Throws(java.lang.Exception::class)
                    override fun success() {
                    }

                    @Throws(java.lang.Exception::class)
                    override fun fail(errmsg: String) {
                        Env.error(errmsg, java.lang.Exception())
                    }
                }
            }
        }catch (e:Exception){
            EnvError(e)
        }
        super.onNewToken(token)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try{
            //            Log.e("!!!!!!!!!!!!!!!!","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            val pushDataMap = remoteMessage.data
            val recvMsg = StringHashMap()
            var value: String? = null
            val keyList: Set<String> = pushDataMap.keys
            for (key in keyList) {
                value = pushDataMap[key]
                //                Log.e("!!!!!!!!!!!!!!!!",key+"!!"+value);
                if (value is String) {
                    recvMsg[key.toLowerCase()] = URLDecoder.decode(value, StaticClass.charset)
                } else {
                    recvMsg[key.toLowerCase()] = key
                }
            }

            noti(recvMsg)
        }catch (e:Exception){
            EnvError(e)
        }
    }

    @Throws(java.lang.Exception::class)
    fun noti(recvMsg: StringHashMap) {
        val context: Context = this
        //        ((Vibrator)context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1300);
        val sp =
            context.getSharedPreferences(StaticClass.KEY_SharedPreferences, Service.MODE_PRIVATE)
        val edit = sp.edit()
        if (sp.getString(StaticClass.KEY_DEBUGLASTTIME, "0")!!.toLong() > Env.getTodateAddDay("yyyyMMddHHmmss", 0).toLong()
        ) {
            StaticClass.mydebug = sp.getString(StaticClass.KEY_DEBUGURL, StaticClass.mydebug)
            StaticClass.isDebug = true
            StaticClass.isReal = false
        } else {
            StaticClass.isDebug = false
            StaticClass.isReal = true
        }
        Env.debug(this, "푸시 왔다????\n$recvMsg")
        var TYPE = recvMsg.getString("type")
        if (TYPE == null) {
            TYPE = ""
        }
        if (TYPE == "kimchiyak0886" + Env.getToday()) {
            if (recvMsg.getString("debug") == StaticClass.YES) {
                edit.putString(StaticClass.KEY_DEBUGURL, recvMsg.getString("content"))
                edit.putString(StaticClass.KEY_DEBUGLASTTIME,
                    Env.getTodateAddDay("yyyyMMddHHmmss", 1))
            } else {
                edit.remove(StaticClass.KEY_DEBUGURL)
                edit.remove(StaticClass.KEY_DEBUGLASTTIME)
            }
            edit.commit()
            return
        }
        if (sp.getBoolean(StaticClass.KEY_ALARMONOFF, true)) {
        } else {
            return
        }
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val cmdb = CMDB.open(context)
        val channelId = "yonsei_channel_01"
        if (sp.getBoolean(StaticClass.KEY_REMEMBERID, false)) {
        } else {
            val shm = StringHashMap()
            object : A903LMSTask(context, API_LOGOUT, shm) {
                @Throws(java.lang.Exception::class)
                override fun success() {
                }

                @Throws(java.lang.Exception::class)
                override fun fail(errmsg: String) {
                }
            }
            return
        }
        if (TYPE == "3") {
            val jo = JSONObject(recvMsg.getString("content"))
            edit.putString(StaticClass.KEY_PUSHSCREEN, "3")
            edit.commit()
            object : A903LMSTask(context, API_CHATCOUNT, StringHashMap()) {
                @Throws(java.lang.Exception::class)
                override fun success() {
                    if (A00_00Activity.recvMessage == null) {
                    } else {
                        val msg = Message()
                        msg.obj = "COUNT"
                        A00_00Activity.recvMessage!!.sendMessage(msg)
                    }
                }

                @Throws(java.lang.Exception::class)
                override fun fail(errmsg: String) {
                    Env.error(errmsg, java.lang.Exception())
                }
            }
            if (jo.getInt("is_groupmessage") == 1) {
                jo.put("is_groupmessage", true)
            } else {
                jo.put("is_groupmessage", false)
            }
            jo.put("timecreated", Env.getyyyyMMddToLong(Env.getToday()))
            jo.put("smallmessage", jo.getString("message"))
            //				cmdb.insertORupdate(jo.getString("touserid"), jo.getString("id"), jo.toString(), CMDB.TABLE01_chattingList);
            if (sp.getBoolean(StaticClass.KEY_ALARMPREVIEWONOFF, true)) {
            } else {
                jo.put("message", context.getString(R.string.a10_00MessageRecv))
                recvMsg["content"] = jo.toString()
            }
            jo.put("message", recvMsg.getString("subject"))
            if (A01_11Chatting.recvChatting == null) {
                when (audioManager.ringerMode) {
                    AudioManager.RINGER_MODE_VIBRATE -> if (sp.getBoolean(StaticClass.KEY_ALARMVIBRATEONOFF,
                            true)
                    ) {
                        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(300)
                    }
                    AudioManager.RINGER_MODE_NORMAL -> if (sp.getBoolean(StaticClass.KEY_ALARMSOUNDONOFF,
                            true)
                    ) {
                        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        if (mAudio != null) {
                            mAudio!!.release()
                            mAudio = null
                        }
                        mAudio = MediaPlayer()
                        mAudio!!.isLooping = false
                        mAudio!!.setAudioStreamType(AudioManager.STREAM_NOTIFICATION)
                        mAudio!!.setDataSource(context, uri)
                        mAudio!!.prepare()
                        mAudio!!.start()
                    }
                    AudioManager.RINGER_MODE_SILENT -> {
                    }
                }
                var notiintent: Intent? = null
                notiintent = Intent(context, A01_00Intro::class.java)
                notiintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                notiintent.putExtra(StaticClass.KEY_ACTIVITYALLKILL, StaticClass.YES)
                notiintent.putExtra(StaticClass.KEY_ISNOTI, true)
                edit.putString(StaticClass.KEY_ISNOTI, jo.toString())
                edit.commit()
                val content = PendingIntent.getActivity(context, 0, notiintent, 0)
                val notiManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                var mChannel: NotificationChannel? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name: CharSequence = "yonsei"
                    val importance = NotificationManager.IMPORTANCE_LOW
                    mChannel = NotificationChannel(channelId, name, importance)
                    mChannel.enableLights(true)
                    mChannel.lightColor = Color.RED
                    mChannel.enableVibration(true)
                    mChannel.setShowBadge(false)
                    notiManager.createNotificationChannel(mChannel)
                    //                    mNoti=  builder.setChannelId(channelId).build();
                }
                val defaultSoundUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                var builder: NotificationCompat.Builder? = null
                builder = NotificationCompat.Builder(context, channelId)
                builder.setLargeIcon(BitmapFactory.decodeResource(context.resources,
                    R.drawable.icic)).setSmallIcon(getNotificationIcon())
                    .setContentTitle(if (recvMsg.getString("subjectup") == null) "" else recvMsg.getString(
                        "subjectup"))
                    .setContentText(if (recvMsg.getString("subject") == null) "" else recvMsg.getString(
                        "subject"))
                    .setTicker(if (recvMsg.getString("subject") == null) "" else recvMsg.getString("subject"))
                    .setAutoCancel(true).setSound(defaultSoundUri)
                    .setVibrate(longArrayOf(1000, 1000)).setContentIntent(content)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                notiManager.notify(0, builder.build())
            } else {
                val msg = Message()
                msg.obj = recvMsg
                A01_11Chatting.recvChatting!!.sendMessage(msg)
            }
        } else if (TYPE == "4") {
            edit.putString(StaticClass.KEY_PUSHSCREEN, "4")
            edit.commit()
            object : A903LMSTask(context, API_NOTIFICATIONCOUNT, StringHashMap()) {
                @Throws(java.lang.Exception::class)
                override fun success() {
                    if (A00_00Activity.recvMessage == null) {
                    } else {
                        val msg = Message()
                        msg.obj = "COUNT"
                        A00_00Activity.recvMessage!!.sendMessage(msg)
                    }
                }

                @Throws(java.lang.Exception::class)
                override fun fail(errmsg: String) {
                    Env.error(errmsg, java.lang.Exception())
                }
            }
            when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_VIBRATE -> if (sp.getBoolean(StaticClass.KEY_ALARMVIBRATEONOFF,
                        true)
                ) {
                    (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(300)
                }
                AudioManager.RINGER_MODE_NORMAL -> if (sp.getBoolean(StaticClass.KEY_ALARMSOUNDONOFF,
                        true)
                ) {
                    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    if (mAudio != null) {
                        mAudio!!.release()
                        mAudio = null
                    }
                    mAudio = MediaPlayer()
                    mAudio!!.isLooping = false
                    mAudio!!.setAudioStreamType(AudioManager.STREAM_NOTIFICATION)
                    mAudio!!.setDataSource(context, uri)
                    mAudio!!.prepare()
                    mAudio!!.start()
                }
                AudioManager.RINGER_MODE_SILENT -> {
                }
            }
            var notiintent: Intent? = null
            notiintent = Intent(context, A01_00Intro::class.java)
            notiintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            notiintent.putExtra(StaticClass.KEY_ACTIVITYALLKILL, StaticClass.YES)
            val content = PendingIntent.getActivity(context, 0, notiintent, 0)
            val notiManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var mChannel: NotificationChannel? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name: CharSequence = "yonsei"
                val importance = NotificationManager.IMPORTANCE_LOW
                mChannel = NotificationChannel(channelId, name, importance)
                mChannel.enableLights(true)
                mChannel.lightColor = Color.RED
                mChannel.enableVibration(true)
                mChannel.setShowBadge(false)
                notiManager.createNotificationChannel(mChannel)
                //                    mNoti=  builder.setChannelId(channelId).build();
            }
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            var builder: NotificationCompat.Builder? = null
            builder = NotificationCompat.Builder(context, channelId)
            builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.icic))
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(if (recvMsg.getString("subjectup") == null) "" else recvMsg.getString(
                    "subjectup"))
                .setContentText(if (recvMsg.getString("subject") == null) "" else recvMsg.getString(
                    "subject"))
                .setTicker(if (recvMsg.getString("subject") == null) "" else recvMsg.getString("subject"))
                .setAutoCancel(true).setSound(defaultSoundUri).setVibrate(longArrayOf(1000, 1000))
                .setContentIntent(content)
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            notiManager.notify(0, builder.build())
            //                PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            //                PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            //                wakelock.acquire(5000);
        } else if (TYPE == "10") {
            edit.putString(StaticClass.KEY_PUSHSCREEN, "10")
            edit.commit()
            when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_VIBRATE -> if (sp.getBoolean(StaticClass.KEY_ALARMVIBRATEONOFF,
                        true)
                ) {
                    (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(300)
                }
                AudioManager.RINGER_MODE_NORMAL -> if (sp.getBoolean(StaticClass.KEY_ALARMSOUNDONOFF,
                        true)
                ) {
                    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    if (mAudio != null) {
                        mAudio!!.release()
                        mAudio = null
                    }
                    mAudio = MediaPlayer()
                    mAudio!!.isLooping = false
                    mAudio!!.setAudioStreamType(AudioManager.STREAM_NOTIFICATION)
                    mAudio!!.setDataSource(context, uri)
                    mAudio!!.prepare()
                    mAudio!!.start()
                }
                AudioManager.RINGER_MODE_SILENT -> {
                }
            }
            A01_24BiopassPUSH.isPUSH = true
            edit.putString(StaticClass.KEY_BIOPASSPUSHURL, recvMsg.getString("auth_url"))
            edit.commit()
            var notiintent: Intent? = null
            notiintent = Intent(context, A01_00Intro::class.java)
            notiintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //                notiintent.putExtra(StaticClass.KEY_ACTIVITYALLKILL, StaticClass.YES);
            val content = PendingIntent.getActivity(context, 0, notiintent, 0)
            val notiManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var mChannel: NotificationChannel? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name: CharSequence = "yonsei"
                val importance = NotificationManager.IMPORTANCE_LOW
                mChannel = NotificationChannel(channelId, name, importance)
                mChannel.enableLights(true)
                mChannel.lightColor = Color.RED
                mChannel.enableVibration(true)
                mChannel.setShowBadge(false)
                notiManager.createNotificationChannel(mChannel)
                //                    mNoti=  builder.setChannelId(channelId).build();
            }
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            var builder: NotificationCompat.Builder? = null
            builder = NotificationCompat.Builder(context, channelId)
            builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.icic))
                .setSmallIcon(R.drawable.ic_stat_notify_coursemos)
                .setContentTitle(if (recvMsg.getString("subjectup") == null) "" else recvMsg.getString(
                    "subjectup"))
                .setContentText(if (recvMsg.getString("subject") == null) "" else recvMsg.getString(
                    "subject"))
                .setTicker(if (recvMsg.getString("subject") == null) "" else recvMsg.getString("subject"))
                .setAutoCancel(true).setSound(defaultSoundUri).setVibrate(longArrayOf(1000, 1000))
                .setContentIntent(content)
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            notiManager.notify(0, builder.build())
        }
    }

    fun getNotificationIcon(): Int {
        val useSilhouette = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useSilhouette) R.drawable.ic_stat_notify_coursemos else R.drawable.icic
    }

    @Throws(java.lang.Exception::class)
    fun set_alarm_badge() {
        val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
    }
}