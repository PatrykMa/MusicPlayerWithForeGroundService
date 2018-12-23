package com.memory.mdm.musicplayer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.content_player.*
import java.lang.Exception

class Player : AppCompatActivity(),IPLayerView {

    override var songs: Array<Song> = arrayOf<Song>()
        get() = field
        set(value) {
            var mAdapter = SongAdapter(value.toList())
            val mLayoutManager = LinearLayoutManager(applicationContext)
            recyclerViewSongList.setLayoutManager(mLayoutManager)
            recyclerViewSongList.setItemAnimator(DefaultItemAnimator())
            recyclerViewSongList.setAdapter(mAdapter)
            mAdapter.notifyDataSetChanged()

        }
    override var author: String
        get() = textView_author.text.toString()
        set(value) {textView_author.text=value}
    override var currentTime: String
        get() = textView_currentTime.text.toString()
        set(value) {textView_currentTime.text=value}
    override var duration: String
        get() = textView_duraton.text.toString()
        set(value) {textView_duraton.text=value}
    override var loop: Boolean
        get() = switch_loop.isChecked
        set(value) {switch_loop.isChecked=value}
    override var randomly: Boolean
        get() = switch_rand.isChecked
        set(value) {switch_rand.isChecked=value}
    override var tiltle: String
        get() = textView_title.text.toString()
        set(value) {textView_title.text=value}

    private lateinit var songChangeReceiver:SongChangerListener
    private lateinit var timeListener:TimeListener
    private lateinit var albumChangerReceiver:ChangeAlbumListener
    private var isStarted=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setSupportActionBar(toolbar)
        startServiceORAskPermission()

        findViewById<FloatingActionButton>(R.id.fab_next).setOnClickListener {
           if(startServiceORAskPermission()){
            next()
           }
        }
        findViewById<FloatingActionButton>(R.id.fab_play).setOnClickListener { if(startServiceORAskPermission()){
            servicePlay()
        } }
        findViewById<FloatingActionButton>(R.id.fab_pause).setOnClickListener { if(startServiceORAskPermission()){
            servicePause()
        } }
        findViewById<FloatingActionButton>(R.id.fab_previous).setOnClickListener { if(startServiceORAskPermission()){
            prev()
        } }
        findViewById<FloatingActionButton>(R.id.fab_stop).setOnClickListener { if(startServiceORAskPermission()){
            serviceStop()
        } }
        switch_loop.setOnCheckedChangeListener { buttonView, isChecked ->
            serviceSetLoop(isChecked)
        }
        switch_rand.setOnCheckedChangeListener { buttonView, isChecked -> serviceSetRand(isChecked) }
    }

    fun initView()
    {
        val dataIntent=registerReceiver(null,IntentFilter(MusicService.ACTION_CHANGE_SONG))
        try{
            author=dataIntent.getStringExtra(MusicService.DATA_AUTHOR)
            duration=dataIntent.getStringExtra(MusicService.DATA_DURATION)
            tiltle=dataIntent.getStringExtra(MusicService.DATA_TITLE)

            val albumIntent=registerReceiver(null,IntentFilter(MusicService.ACTION_CHANGE_ALBUM))
            val data = albumIntent?.getStringArrayExtra(MusicService.ACTION_CHANGE_ALBUM)
            val songs = Array<Song>(data!!.size, { i -> Song(data[i]) })
            this.songs = songs
        }
        catch (e:Exception){}
    }


    override fun onResume() {
        super.onResume()
        initView()
        songChangeReceiver=SongChangerListener(this)
        timeListener= TimeListener(this)
        albumChangerReceiver= ChangeAlbumListener(this)

        val songChangeFilter=IntentFilter(MusicService.ACTION_CHANGE_SONG)
        val timeFilter=IntentFilter(MusicService.ACTION_TIME)
        val albumFilter=IntentFilter(MusicService.ACTION_CHANGE_ALBUM)

        registerReceiver(songChangeReceiver,songChangeFilter)
        registerReceiver(timeListener,timeFilter)
        registerReceiver(albumChangerReceiver,albumFilter)
    }

    override fun onPause() {
        unregisterReceiver(songChangeReceiver)
        unregisterReceiver(timeListener)
        unregisterReceiver(albumChangerReceiver)
        super.onPause()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_player, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startServiceORAskPermission():Boolean
    {
        if(isReadStorageGranted()) {
            if (isStarted)
                return true
            val intent = Intent(this, MusicService::class.java)
            intent.setAction(MusicService.ACTION_START_FOREGROUND_SERVICE)
            startService(intent)
            isStarted = true
            return true
        }
        else requestReadStoragePermission()
        return false
    }

    private fun checkPermission()
    {
        if(!isReadStorageGranted())
            requestReadStoragePermission()
    }

    private fun isReadStorageGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

    fun servicePlay()
    {
        val intent = Intent(this, MusicService::class.java)
        intent.setAction(MusicService.ACTION_PLAY)
        startService(intent)
    }
    fun servicePause()
    {
        val intent = Intent(this, MusicService::class.java)
        intent.setAction(MusicService.ACTION_PAUSE)
        startService(intent)
    }
    fun serviceStop()
    {
        val intent = Intent(this, MusicService::class.java)
        intent.setAction(MusicService.ACTION_STOP_FOREGROUND_SERVICE)
        startService(intent)
        isStarted=false
    }
    fun next()
    {
        val intent = Intent(this, MusicService::class.java)
        intent.setAction(MusicService.ACTION_NEXT)
        startService(intent)
    }
    fun prev()
    {
        val intent = Intent(this, MusicService::class.java)
        intent.setAction(MusicService.ACTION_PREVIOUS)
        startService(intent)
    }

    fun serviceSetRand(to:Boolean)
    {
        val intent = Intent(this, MusicService::class.java)
        intent.setAction(MusicService.ACTION_SET_RAND)
        intent.putExtra(MusicService.FLAG_RAND,to)
        startService(intent)
    }
    fun serviceSetLoop(to:Boolean)
    {
        val intent = Intent(this, MusicService::class.java)
        intent.setAction(MusicService.ACTION_SET_LOOP)
        intent.putExtra(MusicService.FLAG_LOOP,to)
        startService(intent)
    }

}
