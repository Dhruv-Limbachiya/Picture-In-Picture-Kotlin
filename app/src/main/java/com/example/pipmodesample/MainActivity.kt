package com.example.pipmodesample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pipmodesample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnPlayGalleryVideo.setOnClickListener {
            videoLauncher.launch("video/*")
        }

        mBinding.btnPlayYoutubeVideo.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity,YouTubeActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Launcher for choosing video from gallery.
     */
    private val videoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val intent = Intent()
            intent.setClass(this@MainActivity,VideoActivity::class.java)
            intent.putExtra(KEY_VIDEO_URL, it.toString()) // Passes youtube video url
            startActivity(intent)
        }
    }

    companion object {
        const val YT_VIDEO_URL = "https://www.youtube.com/watch?v=62JB64aQC9A"
        const val KEY_VIDEO_URL = "video url"
    }
}