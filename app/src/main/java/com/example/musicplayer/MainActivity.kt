package com.example.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0
    private var currentSongIndex: Int = 0
    //add new songs here
    private val songs = arrayOf(R.raw.song1, R.raw.song2, R.raw.song3)
    private val imageList = arrayOf(R.drawable.image1, R.drawable.image2, R.drawable.image3)
    private val songTitles = arrayOf("Little do you know(Alex & Sierra)", "Night Changes(One Direction)", "Animals(Maroon 5)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val volumeBar = findViewById<SeekBar>(R.id.volumeBar)
        val positionBar = findViewById<SeekBar>(R.id.positionBar)
        val songTitle = findViewById<TextView>(R.id.songTitle)

        mp = MediaPlayer.create(this, songs[currentSongIndex])
        mp.isLooping = true
        mp.setVolume(0.8f, 0.8f)
        totalTime = mp.duration

        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        var volumeNum = progress / 100.0f
                        mp.setVolume(volumeNum, volumeNum)
                    }
                }

                override fun onStartTrackingTouch(seekbar: SeekBar?) {}

                override fun onStopTrackingTouch(seekbar: SeekBar?) {}
            }
        )

        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        // Update positionBar.progress every second
        Thread {
            while (mp != null) {
                try {
                    if (mp.isPlaying) {
                        val msg = android.os.Message()
                        msg.what = mp.currentPosition
                        handler.sendMessage(msg)
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {}
            }
        }.start()


        val playBtn = findViewById<Button>(R.id.playBtn)
        playBtn.setOnClickListener { playBtnClick(it) }

        val nextBtn = findViewById<Button>(R.id.nextBtn)
        nextBtn.setOnClickListener { nextBtnClick(it) }

        val prevBtn = findViewById<Button>(R.id.prevBtn)
        prevBtn.setOnClickListener { prevBtnClick(it) }
    }

    private val handler = android.os.Handler {
        val currentPosition = it.what
        // Update positionBar.progress on the UI thread
        val positionBar = findViewById<SeekBar>(R.id.positionBar)
        positionBar.progress = currentPosition
        true
    }

    fun playBtnClick(v: View) {
        val playBtn = findViewById<Button>(R.id.playBtn)
        if (mp.isPlaying) {
            mp.pause()
            playBtn.setBackgroundResource(R.drawable.play)
        } else {
            mp.start()
            playBtn.setBackgroundResource(R.drawable.pause)
        }
    }

    fun nextBtnClick(v: View) {
        // Stop the current song and release the MediaPlayer
        mp.stop()
        mp.release()

        // Move to the next song index
        currentSongIndex = (currentSongIndex + 1) % songs.size

        // Initialize a new MediaPlayer with the next song
        mp = MediaPlayer.create(this, songs[currentSongIndex])
        updateTitle(songTitles[currentSongIndex])
        mp.isLooping = true
        mp.setVolume(0.8f, 0.8f)
        totalTime = mp.duration

        // Update the ImageView with the new image for the next song
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(imageList[currentSongIndex])

    }

    fun prevBtnClick(v: View) {
        // Stop the current song and release the MediaPlayer
        mp.stop()
        mp.release()

        // Move to the previous song index
        currentSongIndex = (currentSongIndex - 1 + songs.size) % songs.size

        // Initialize a new MediaPlayer with the previous song
        mp = MediaPlayer.create(this, songs[currentSongIndex])
        updateTitle(songTitles[currentSongIndex])
        mp.isLooping = true
        mp.setVolume(0.8f, 0.8f)
        totalTime = mp.duration

        // Update the ImageView with the new image for the previous song
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(imageList[currentSongIndex])
    }
    private fun updateTitle(title: String) {
        val titleTextView = findViewById<TextView>(R.id.songTitle)
        titleTextView.text = title
    }

}
