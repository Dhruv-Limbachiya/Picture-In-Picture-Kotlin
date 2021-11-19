package com.example.pipmodesample

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.pipmodesample.databinding.ActivityVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.snackbar.Snackbar


class VideoActivity : AppCompatActivity(), Player.Listener {

    private lateinit var mBinding: ActivityVideoBinding

    private lateinit var mExoPlayer: ExoPlayer

    private var mPictureInPictureParams: PictureInPictureParams.Builder? = null

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "exoplayer-sample")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPictureInPictureParams = PictureInPictureParams.Builder()
        }

        initializePlayer()

        mBinding.imageviewPipIcon.setOnClickListener {
            playInPictureInPictureMode()
        }
    }

    /**
     * initializes the exoplayer.
     */
    private fun initializePlayer() {
        mExoPlayer = ExoPlayer.Builder(this).build()

        mBinding.exoplayerView.player = mExoPlayer

        getVideoUrl(intent)?.let {
            preparePlayer(it)
        }
        mExoPlayer.playWhenReady = true
        mExoPlayer.addListener(this)
    }


    /**
     * prepare exo player to play media file.
     */
    private fun preparePlayer(url: String) {
        // Create a media source
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))
        mExoPlayer.setMediaSource(mediaSource)
    }

    /**
     * Plays video in PIP mode.
     */
    private fun playInPictureInPictureMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Aspect ratio for PIP mode screen.
            val aspectRatio = Rational(mBinding.exoplayerView.width, mBinding.exoplayerView.height)

            mPictureInPictureParams?.setAspectRatio(aspectRatio)?.build()

            // Enter in PIP mode
            mPictureInPictureParams?.let {
                enterPictureInPictureMode(it.build())
            }

        } else {
            Snackbar.make(
                mBinding.root,
                "Your device doesn't Picture In Picture mode",
                Snackbar.LENGTH_LONG
            ).show()
            Log.i(TAG, "playInPictureInPictureMode: not supported ")
        }
    }


    // Trigger when user presses home button
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onUserLeaveHint() {
        if (!isInPictureInPictureMode) {
            playInPictureInPictureMode()
            Log.i(TAG, "onUserLeaveHint: ")
        }
    }


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        // handle ui on picture in picture mode change.
        if (isInPictureInPictureMode) {
            supportActionBar?.hide()
            mBinding.imageviewPipIcon.isVisible = false
        } else {
            supportActionBar?.show()
            mBinding.imageviewPipIcon.isVisible = true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Play new video on PIP mode when user select new video.
        intent?.let { getVideoUrl(it) }
    }

    /**
     * Get the extras data from the intent.
     */
    private fun getVideoUrl(intent: Intent): String? {
        val bundle = intent.extras
        return bundle?.getString(MainActivity.KEY_VIDEO_URL)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Log.e(TAG, "onPlayerError: ", error)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            mBinding.progressBar.isVisible = true
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            mBinding.progressBar.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mExoPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        // PIP mode cancel button will trigger onStop() callback. So release the exo player here
        mExoPlayer.release()
    }

    companion object {
        const val TAG = "VideoActivity"
    }

}