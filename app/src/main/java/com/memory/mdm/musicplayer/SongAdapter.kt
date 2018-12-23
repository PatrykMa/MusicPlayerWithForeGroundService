package com.memory.mdm.musicplayer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SongAdapter( val moviesList: List<Song>) : RecyclerView.Adapter<SongAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        return moviesList.size
    }



    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
        var author: TextView
        var duration: TextView

        init {
            title = view.findViewById(R.id.title)
            author = view.findViewById(R.id.author)
            duration = view.findViewById(R.id.duration)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = moviesList[position]
        holder.title.text =song.title
        holder.author.text= song.author
        holder.duration.text = song.duration
    }
}