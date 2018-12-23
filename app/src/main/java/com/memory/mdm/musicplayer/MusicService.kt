package com.memory.mdm.musicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.widget.Toast
import org.jetbrains.anko.doAsync
import java.lang.Exception

class MusicService:Service() {
    companion object {
        val ACTION_START_FOREGROUND_SERVICE="start"
        val ACTION_STOP_FOREGROUND_SERVICE = "stop"
        val ACTION_PLAY = "play"
        val ACTION_PAUSE = "pause"
        const val ACTION_CHANGE_ALBUM="CHANGE_ALBUM"
        const val ACTION_CHANGE_SONG="changeSong"
        const val ACTION_TIME="time"
        const val  ACTION_NEXT="next"
        const val  ACTION_PREVIOUS="previous"
        const val  ACTION_SET_LOOP="loop"
        const val  ACTION_SET_RAND="RAND"
        const val  FLAG_RAND="FLAG_RAND"
        const val  FLAG_LOOP="FLAG_LOOP"
        const val DATA_TITLE:String="title"
        const val DATA_AUTHOR:String="author"
        const val DATA_DURATION:String="duration"
        const val DATA_CURTIME:String="currTime"
        const val DATA_SOGS= "SONGS"
        fun convertTimeFromMiliToString(time:Int):String
        {
            var w=""
            w+= if(time/360000>0)(time/3600_000).toInt().toString() + ":" else ""
            w+= ((time%3600_000)/60_000).toString() + ":"
            w+= (((time%3600_000)%60_000)/1000).toString()
            return w

        }

    }
    val CHANNEL_ID="com.example.music"
    val NOTID=1

    lateinit var mBuilder : NotificationCompat.Builder
    var player: MusiPlayer?=null
    var time=0
    var isInti=false

    override fun onBind(intent: Intent?): IBinder? {
        return  null
    }

    fun initPlayer()
    {
        if(player==null) {
            player = MusiPlayer(this)
            player!!.onSetSongs = {
                var changeAlbumIntent=Intent(ACTION_CHANGE_ALBUM)
                changeAlbumIntent.putExtra(DATA_SOGS,
                    Array(player!!.songList.size,{ i-> player!!.songList[i].toString()})

                )
                sendStickyBroadcast(changeAlbumIntent)
            }
            player?.songList = SongManager(this).songs
            player!!.onStart={
                var changeSongIntent=Intent(ACTION_CHANGE_SONG)
                changeSongIntent.putExtra(DATA_AUTHOR,player!!.playedSong!!.author)
                changeSongIntent.putExtra(DATA_TITLE,player!!.playedSong!!.title)
                changeSongIntent.putExtra(DATA_DURATION,convertTimeFromMiliToString(player!!.duration))
                sendStickyBroadcast(changeSongIntent)
                updateotyfication(player!!.playedSong!!.title,player!!.playedSong!!.author)
            }


            //its not best option :|
            doAsync {
                while(player!=null)
                {
                    var timeIntent=Intent(ACTION_TIME)
                    timeIntent.putExtra(DATA_CURTIME,convertTimeFromMiliToString(player!!.currentPosition))
                    sendBroadcast(timeIntent)
                    Thread.sleep(1_000)

                }
            }
        }
    }
    private fun updateotyfication(title:String,context:String)
    {
        mBuilder
            .setContentTitle(title)
            .setContentText(context)
        with(NotificationManagerCompat.from(this)) {
            notify(NOTID, mBuilder.build())
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if(!isInti) {
                initPlayer()
                startForegroundService()
            }
            when (action) {
                ACTION_START_FOREGROUND_SERVICE -> {
                    startForegroundService()
                    createNotificationChannel()

                    Toast.makeText(applicationContext, "Foreground service is started.", Toast.LENGTH_LONG).show()
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopForegroundService()
                    player?.stop()
                    Toast.makeText(applicationContext, "Foreground service is stopped.", Toast.LENGTH_LONG).show()
                }
                ACTION_PLAY -> {
                    Toast.makeText(applicationContext, "You click Play button.", Toast.LENGTH_LONG).show()
                    initPlayer()
                    player?.play()
                }
                ACTION_PAUSE -> {
                    Toast.makeText(applicationContext, "You click Pause button.", Toast.LENGTH_LONG).show()
                    time= try{player!!.currentPosition} catch (e: Exception) {0}
                    player?.pause()
                }
                ACTION_NEXT->{
                    player?.next()
                }
                ACTION_PREVIOUS->{
                    player?.previous()
                }
                ACTION_SET_LOOP->{
                    player?.loop=intent.getBooleanExtra(FLAG_LOOP,true)
                }
                ACTION_SET_RAND->{
                    player?.randomly=intent.getBooleanExtra(FLAG_RAND,true)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopForegroundService() {
        Log.d("servis", "Stop foreground service.")
        player?.stop()
        // Stop foreground service and remove the notification.
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()
    }

    fun startForegroundService()
    {
        Log.d("servis", "Start foreground service.")
        //createIntent To play
        val playIntent = Intent(this, MusicService::class.java)
        playIntent.action = ACTION_PLAY
        val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0)
        // pause
        val pauseIntent = Intent(this, MusicService::class.java)
        pauseIntent.action = ACTION_PAUSE
        val pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
        //stop
        val stopIntent = Intent(this, MusicService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0)

        mBuilder = NotificationCompat.Builder(this, "chanel")
            .setSmallIcon(R.drawable.ic_player)
            .setContentTitle("Tytuł powiadomienia")
            .setContentText("bardzo dlugi tekst który sam wymysliłem i zawiera masę literówek i błędów ")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_play,"Play",pendingPlayIntent)
            .addAction(R.drawable.ic_pause,"Pause",pendingPauseIntent)
            .addAction(R.drawable.ic_stop,"Stop",pendingStopIntent)

    isInti=true
        startForeground(NOTID, mBuilder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}