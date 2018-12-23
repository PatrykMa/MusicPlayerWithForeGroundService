package com.memory.mdm.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.lang.Exception

class TimeListener(var view:IPLayerView):BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try{
            view.currentTime=intent!!.getStringExtra(MusicService.DATA_CURTIME)
        }
        catch (e:Exception){}
    }
}