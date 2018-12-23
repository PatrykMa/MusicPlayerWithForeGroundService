package com.memory.mdm.musicplayer

import android.net.Uri


class Song() {
    constructor(id:Long,title:String,author:String,URI: Uri?=null,dur:String=""):this()
    {
        this.title=title
        this.ID=id
        this.author=author
        URi=URI
        duration=dur
    }
    constructor(all:String):this()
    {
        var data=all.split('\u0001')
        title=data[0]
        ID=data[1].toLong()
        author=data[2]
        duration=data[4]
    }
    var URi:Uri?=null
    var ID:Long=0
    var title=""
    var author=""
    var duration=""

    override fun toString(): String {
        return title+'\u0001'+ID+'\u0001'+author+'\u0001'+URi+'\u0001'+duration+'\u0001'
    }

}