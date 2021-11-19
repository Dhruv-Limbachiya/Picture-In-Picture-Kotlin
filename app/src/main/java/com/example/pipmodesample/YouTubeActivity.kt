package com.example.pipmodesample

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.pipmodesample.databinding.ActivityYouTubeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

class YouTubeActivity : YouTubeBaseActivity() {

    private lateinit var mBinding: ActivityYouTubeBinding
    private var player: YouTubePlayer? = null

    private var mPictureInPictureParams: PictureInPictureParams.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityYouTubeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPictureInPictureParams = PictureInPictureParams.Builder()
        }


        mBinding.ytPlayer.initialize(getString(R.string.api_key),
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    youtubeplayer: YouTubePlayer?,
                    p2: Boolean
                ) {
                    if (youtubeplayer != null) {
                        player = youtubeplayer
                        youtubeplayer.loadVideo("62JB64aQC9A")
                        youtubeplayer.play()
                    }
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                    Toast.makeText(this@YouTubeActivity, "Video player Failed", Toast.LENGTH_SHORT)
                        .show();

                }
            })

    }

    // Trigger when user presses home button
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onUserLeaveHint() {
        if (!isInPictureInPictureMode) {
            playInPictureInPictureMode()
            Log.i(VideoActivity.TAG, "onUserLeaveHint: ")
        }
    }


    /**
     * Plays video in PIP mode.
     */
    private fun playInPictureInPictureMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Aspect ratio for PIP mode screen.
            val aspectRatio = Rational(mBinding.ytPlayer.width, mBinding.ytPlayer.height)

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
            Log.i(VideoActivity.TAG, "playInPictureInPictureMode: not supported ")
        }

    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        // handle ui on picture in picture mode change.
        if (isInPictureInPictureMode) {
            player?.play()
        }
    }

    override fun onPause() {
        super.onPause()
        player?.play()
    }

    override fun onStop() {
        super.onStop()
    }
}