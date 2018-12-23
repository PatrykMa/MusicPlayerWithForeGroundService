package com.memory.mdm.musicplayer

import android.content.Context

interface IPLayerView {
    var tiltle:String
    var author:String
    var duration:String
    var currentTime:String
    var randomly:Boolean
    var loop:Boolean
    var songs:Array<Song>
}