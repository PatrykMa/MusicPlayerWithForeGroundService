package com.memory.mdm.musicplayer

import android.content.ContentUris
import android.content.Context

class SongManager(var context: Context) {

        var songs:Array<Song> = arrayOf()
            private set

        init {
            getSongs()
        }

        private fun getSongs()
        {
            val musicResolver = context.contentResolver
            val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val musicCursor = musicResolver.query(musicUri, null, null, null, null)
            var songList= mutableListOf<Song>()
            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get columns
                val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
                val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
                val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
                val duration= musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION)

                //add songs to list
                do {
                    val thisId = musicCursor.getLong(idColumn)
                    val thisTitle = musicCursor.getString(titleColumn)
                    val thisArtist = musicCursor.getString(artistColumn)
                    val thisDur= musicCursor.getString(duration)
                    val uri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,thisId)
                    songList.add(Song(thisId, thisTitle, thisArtist,uri,convertTimeFromMiliToString(thisDur.toInt())))
                } while (musicCursor.moveToNext())
            }
            musicCursor.close()
            songs=songList.toTypedArray()
        }
        fun refresh()
        {
            getSongs()
        }
    private fun convertTimeFromMiliToString(time:Int):String
    {
        var w=""
        w+= if(time/360000>0)(time/3600_000).toInt().toString() + ":" else ""
        w+= ((time%3600_000)/60_000).toString() + ":"
        w+= (((time%3600_000)%60_000)/1000).toString()
        return w

    }


    }
