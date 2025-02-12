package com.ithe.ss.imv.ui.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ithe.ss.imv.R
import com.ithe.ss.imv.player.PlayerProvider
import com.ithe.ss.imv.player.VideoPlayer


class IJKPlayerFragment : Fragment() {

    private var surfaceView: SurfaceView? = null
    private var videoPlayer: VideoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoPlayer = PlayerProvider.get(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ijk_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        surfaceView = view.findViewById(R.id.ijk_surfaceView)
        videoPlayer?.setSurfaceView(surfaceView)
        videoPlayer?.setDataSource("https://alimov2.a.kwimgs.com/upic/2024/01/22/20/BMjAyNDAxMjIyMDA2MTJfMzgyMzQ3MjQ1XzEyMjc2MDIzMjEzOV8xXzM=_b_B46d94e5bc6f667add367442e22fee82f.mp4?clientCacheKey=3xw53em5esqzpiu_b.mp4&tt=b&di=78e498d6&bp=13414")
        videoPlayer?.prepare()
    }

    override fun onResume() {
        super.onResume()
        
    }
}