package com.memory.mdm.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.lang.Exception

class ChangeAlbumListener(var view:IPLayerView) :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val data = intent?.getStringArrayExtra(MusicService.DATA_SOGS)
            val songs = Array<Song>(data!!.size, { i -> Song(data[i]) })
            view.songs = songs
        }
        catch (e:Exception){
            var x=0
        }
    }
}