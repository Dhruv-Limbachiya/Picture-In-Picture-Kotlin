package com.example.pipmodesample

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.annotation.RequiresApi
import com.example.pipmodesample.databinding.ActivityYouTubeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeBaseActivity

class YouTubeActivity : YouTubeBaseActivity() {

    private lateinit var mBinding: ActivityYouTubeBinding

    private var mPictureInPictureParams: PictureInPictureParams.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityYouTubeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPictureInPictureParams = PictureInPictureParams.Builder()
        }

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
            val aspectRatio =
                Rational(mBinding.youtubePlayerView.width, mBinding.youtubePlayerView.height)

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

}