package com.memory.mdm.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.lang.Exception

class SongChangerListener(var view:IPLayerView):BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try{
            view.author=intent!!.getStringExtra(MusicService.DATA_AUTHOR)
            view.duration=intent!!.getStringExtra(MusicService.DATA_DURATION)
            view.tiltle=intent!!.getStringExtra(MusicService.DATA_TITLE)
        }
        catch (e:Exception)
        {}
    }
}