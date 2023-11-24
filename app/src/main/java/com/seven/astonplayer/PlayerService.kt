package com.seven.astonplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class PlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    val playerIBinder: IBinder = PlayerIBinder()

    override fun onBind(intent: Intent): IBinder {
        return playerIBinder
    }

    override fun onPrepared(mp: MediaPlayer?) {
        TODO("Not yet implemented")
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("Not yet implemented")
    }

    inner class PlayerIBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService

    }
    companion object {
        const val chanelId = "MYCHANNEL"
    }
}