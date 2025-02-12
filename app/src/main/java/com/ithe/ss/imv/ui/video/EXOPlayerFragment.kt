package com.ithe.ss.imv.ui.video

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.ithe.ss.imv.R
import com.ithe.ss.imv.net.IMVCenter
import com.ithe.ss.imv.player.PlayerProvider
import com.ithe.ss.imv.player.VideoPlayer


class EXOPlayerFragment(
    val position: Int,
    val type: String?
) : Fragment() {

    private var playerView: PlayerView? = null
    private var videoPlayer: VideoPlayer? = null
    private var videoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("iMV", "EXOPlayerFragment onCreate")
        videoPlayer = PlayerProvider.get(context)
        IMVCenter.get(type)?.getVideo(position, type) { url ->
            videoUrl = url
            videoPlayer?.let {
                it.setDataSource(url)
                it.prepare()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("iMV", "EXOPlayerFragment onCreateView")
        return inflater.inflate(R.layout.fragment_exo_player, container, false)
    }

    @UnstableApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("iMV", "EXOPlayerFragment onViewCreated")
        playerView = view.findViewById(R.id.player_view)
        playerView?.setShowNextButton(false)
        playerView?.setShowPreviousButton(false)
        videoPlayer?.setSurfaceView(playerView)
        videoUrl?.let {
            videoPlayer?.setDataSource(it)
            videoPlayer?.prepare()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("iMV", "EXOPlayerFragment onResume")
        videoPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        Log.d("iMV", "EXOPlayerFragment onPause")
        videoPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("iMV", "EXOPlayerFragment onDestroy")
        videoPlayer?.release()
    }
}