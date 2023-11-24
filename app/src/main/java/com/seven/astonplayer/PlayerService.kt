package com.seven.astonplayer

import android.R
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Random


class PlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    val playerIBinder: IBinder = PlayerIBinder()

    //media player
    private lateinit var player: MediaPlayer

    //song list
    private var songs: List<Song> = listSong

    //current position
    private var songPosn = 0

    //title of current song
    private var songTitle = ""

    //notification id
    private val NOTIFY_ID = 1

    //shuffle flag and random
    private var shuffle = false

    private var rand: Random? = null

    override fun onCreate() {
        super.onCreate()

        //initialize position
        //initialize position
        songPosn = 0
        //random
        //random
        rand = Random()
        //create player
        //create player
        player = MediaPlayer()
        //initialize
        //initialize
        initMusicPlayer()
    }

    fun initMusicPlayer() {
        //set player properties
        //  player?.setWakeMode(
        //       applicationContext,
        //      PowerManager.PARTIAL_WAKE_LOCK
        //   )


        //     player?.setAudioAttributes(
        //         AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        //      )

        player.apply {
            setWakeMode(
                applicationContext,
                PowerManager.PARTIAL_WAKE_LOCK
            )
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()

            )
        }
        //set listeners
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
    }

    //нет необходимости сейчас
    //pass song list
    fun setList(theSongs: List<Song>) {
        songs = theSongs
    }
    override fun onBind(intent: Intent): IBinder {
        return playerIBinder
    }

    //call after reconnect
    // override fun onRebind(intent: Intent?) {
    //    super.onRebind(intent)
    //  }

    override fun onUnbind(intent: Intent?): Boolean {
        //Возможно вызывается при закрытии активности
        //   return super.onUnbind(intent)
        player.stop()
        player.release()
        return false
    }

    fun playSong() {
        //play
        player.reset()
        //get song
        val playSong: Song = songs[songPosn]
        //get title
        songTitle = playSong.title
        //get id
        val currSong: Long = playSong.iD
        //set uri
        //нет необходимости
        val trackUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            currSong
        )
        //set the data source
        var filename = "android.resource://" + this.packageName + "/raw/"
        val afd: AssetFileDescriptor = applicationContext.getResources().openRawResourceFd(playSong.resID)
        try {
          //  player.setDataSource(applicationContext, playSong.resID)
            //Возможно неправильно
            player.setDataSource(afd)
            afd.close()
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }
        player.prepareAsync()
    }

    //set the song
    fun setSong(songIndex: Int) {
        songPosn = songIndex
    }

    override fun onCompletion(mp: MediaPlayer) {
        //check if playback has reached the end of a track
        if (player.currentPosition > 0) {
            mp.reset()
            playNext()
        }
    }


    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.v("MUSIC PLAYER", "Playback Error")
        mp.reset()
        return false
    }


    override fun onPrepared(mp: MediaPlayer) {
        val icon = BitmapFactory.decodeResource(resources, R.drawable.btn_default)


        //start playback
        mp.start()
        //notification
        val notIntent = Intent(this, MainActivity::class.java)
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendInt = PendingIntent.getActivity(
            this, 0,
            notIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        //нужно запомнить setSmallIcon ставится через полный пакет + R.drawable.mynew
        val builder = NotificationCompat.Builder(this, chanelId)
        builder.setContentIntent(pendInt)
            .setSmallIcon(com.seven.astonplayer.R.drawable.mynew)
            .setTicker(songTitle)
            .setOngoing(true)
            .setContentTitle("Playing")
            .setContentText(songTitle)
        val not = builder.build()
        startForeground(NOTIFY_ID, not)
    }

    //playback methods
    fun getPosn(): Int {
        return player.currentPosition
    }

    fun getDur(): Int {
        return player.duration
    }

    fun isPng(): Boolean {
        return player.isPlaying
    }

    fun pausePlayer() {
        player.pause()
    }

    fun seek(posn: Int) {
        player.seekTo(posn)
    }

    fun go() {
        player.start()
    }

    //skip to previous track
    fun playPrev() {
        songPosn--
        if (songPosn < 0) songPosn = songs!!.size - 1
        playSong()
    }


    //skip to next
    fun playNext() {
        if (shuffle) {
            var newSong = songPosn
            while (newSong == songPosn) {
                newSong = rand!!.nextInt(songs.size)
            }
            songPosn = newSong
        } else {
            songPosn++
            if (songPosn >= songs.size) songPosn = 0
        }
        playSong()
    }

    //toggle shuffle
    fun setShuffle() {
        shuffle = !shuffle
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
    }




    inner class PlayerIBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService

    }
    companion object {
        const val chanelId = "MYCHANNEL"
    }
}