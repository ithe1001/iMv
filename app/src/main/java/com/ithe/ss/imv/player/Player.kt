package com.ithe.ss.imv.player

import android.content.Context
import android.util.Log
import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ithe.ss.imv.player.ErrorListener.ErrorType
import com.ithe.ss.imv.player.PlayerStateListener.PlayerState

class PlayerProvider {
    companion object {
        fun get(context: Context?): VideoPlayer? {
            return EXOPlayerFactory().createPlayer(context)
        }
    }
}

interface PlayerFactory {
    fun createPlayer(context: Context?): VideoPlayer?
}

//class IjkPlayerFactory : PlayerFactory {
//    override fun createPlayer(context: Context?): VideoPlayer? {
//        return IjkPlayerWrapper(context).also { it.init(context) }
//    }
//}

class EXOPlayerFactory : PlayerFactory {
    override fun createPlayer(context: Context?): VideoPlayer? {
        return EXOPlayer(context).also { it.init() }
    }
}

// 播放器核心接口
interface VideoPlayer {
    // 状态获取
    val currentPosition: Long
    val duration: Long
    val isPlaying: Boolean
    val state: PlayerState?

    // 基础控制
    fun init()
    fun setDataSource(url: String?)
    fun prepare()
    fun start()
    fun pause()
    fun stop()
    fun release()

    // 播放控制
    fun seekTo(positionMs: Long)
    fun setSpeed(speed: Float)
    fun setLooping(looping: Boolean)
    fun setVolume(volume: Float)

    // 视图
    fun setSurfaceView(surfaceView: View?)

    // 监听器管理
    fun addStateListener(listener: PlayerStateListener?)
    fun removeStateListener(listener: PlayerStateListener?)
    fun addErrorListener(listener: ErrorListener?)
    fun removeErrorListener(listener: ErrorListener?)
}

// 状态监听接口
interface PlayerStateListener {

    // 播放器状态枚举
    enum class PlayerState {
        IDLE,  // 初始状态
        INITIALIZED,  // 初始化完成
        PREPARING,  // 准备中
        PREPARED,  // 准备完成
        BUFFERING,// 缓冲中
        STARTED,  // 播放中
        PAUSED,  // 暂停
        STOPPED,  // 停止
        COMPLETED,  // 播放完成
        ERROR // 错误状态
    }

    fun onStateChanged(state: PlayerState?)
    fun onBufferingUpdate(percent: Int)
    fun onVideoSizeChanged(width: Int, height: Int)
    fun onPositionChanged(position: Long)
}

// 错误监听接口
interface ErrorListener {
    // 错误类型枚举
    enum class ErrorType {
        NETWORK_ERROR, DECODE_ERROR, RENDER_ERROR, UNKNOWN_ERROR
    }

    fun onError(type: ErrorType?, message: String?)
}


class EXOPlayer(
    val context: Context?
) : VideoPlayer {

    private var mediaPlayer: ExoPlayer? = null
    private val stateListeners = ArrayList<PlayerStateListener>()
    private val errorListeners = ArrayList<ErrorListener>()
    private var currentState = PlayerState.IDLE

    override val currentPosition: Long
        get() = mediaPlayer?.currentPosition ?: 0
    override val duration: Long
        get() = mediaPlayer?.duration ?: 0
    override val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true
    override val state: PlayerState?
        get() = currentState

    override fun init() {
        context?.let {
            mediaPlayer = ExoPlayer.Builder(it).build()
            mediaPlayer?.repeatMode = Player.REPEAT_MODE_ALL
            mediaPlayer?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_BUFFERING -> updateState(PlayerState.BUFFERING)
                        Player.STATE_READY -> updateState(PlayerState.PREPARED)
                        Player.STATE_ENDED -> updateState(PlayerState.COMPLETED)
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        updateState(PlayerState.STARTED)
                    } else {
                        updateState(PlayerState.PAUSED)
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    updateState(PlayerState.ERROR)
                    notifyError(ErrorType.UNKNOWN_ERROR, error.message)
                }
            })
        }
    }

    // 状态更新方法
    private fun updateState(newState: PlayerState) {
        Log.d("IMV", "updateState $newState")
        currentState = newState
        for (listener in stateListeners) {
            listener.onStateChanged(newState)
        }
    }

    private fun notifyError(errorType: ErrorType, errorMsg: String?) {
        Log.d("IMV", "notifyError $errorType,$errorMsg")
        for (listener in errorListeners) {
            listener.onError(errorType, errorMsg)
        }
    }

    override fun setDataSource(url: String?) {
        url ?: return
        mediaPlayer?.setMediaItem(MediaItem.fromUri(url))
        mediaPlayer?.prepare()
    }

    override fun prepare() {
        Log.d("IMV", "prepare")
        mediaPlayer?.let {
            it.prepare()
            updateState(PlayerState.PREPARING)
        }
    }

    override fun start() {
        Log.d("IMV", "start")
        mediaPlayer?.let {
            it.play()
            updateState(PlayerState.STARTED)
        }
    }

    override fun pause() {
        Log.d("IMV", "pause")
        mediaPlayer?.let {
            it.pause()
            updateState(PlayerState.PAUSED)
        }
    }

    override fun stop() {
        Log.d("IMV", "stop")
        mediaPlayer?.let {
            it.stop()
            updateState(PlayerState.STOPPED)
        }
    }

    override fun release() {
        Log.d("IMV", "release")
        mediaPlayer?.let {
            it.release()
            updateState(PlayerState.IDLE)
        }
    }

    override fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs)
    }

    override fun setSpeed(speed: Float) {
        mediaPlayer?.setPlaybackSpeed(speed)
    }

    override fun setLooping(looping: Boolean) {

    }

    override fun setVolume(volume: Float) {
        mediaPlayer?.volume = volume
    }

    override fun setSurfaceView(playview: View?) {
        (playview as? PlayerView)?.let {
            it.player = mediaPlayer
        }
    }

    override fun addStateListener(listener: PlayerStateListener?) {
        listener?.let { stateListeners.add(listener) }
    }

    override fun removeStateListener(listener: PlayerStateListener?) {
        listener?.let { stateListeners.remove(listener) }
    }

    override fun addErrorListener(listener: ErrorListener?) {
        listener?.let { errorListeners.add(listener) }
    }

    override fun removeErrorListener(listener: ErrorListener?) {
        listener?.let { errorListeners.remove(listener) }
    }
}


//class IJKPlayerConfig {
//
//    companion object {
//        // 缓冲配置
//        var maxBufferSizeKB = 1024 * 15 // 15MB
//        var probeSizeKB = 1024 * 128 // 预加载128KB数据
//
//        // 超时配置（毫秒）
//        var prepareTimeout = 5000
//        var readTimeout = 30000
//
//        fun applyConfig(mediaPlayer: IjkMediaPlayer?) {
//            mediaPlayer?.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1) // 开启硬解码
//            mediaPlayer?.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1L)
//            // 设置缓冲参数
//            mediaPlayer?.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", maxBufferSizeKB.toLong())
//            mediaPlayer?.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", probeSizeKB.toLong())
//
//            // 设置超时
//            mediaPlayer?.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", prepareTimeout.toLong())
//            mediaPlayer?.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0)
//            mediaPlayer?.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "http-detect-range-support", 0)
//        }
//    }
//}
//
//
//class IjkPlayerWrapper(context: Context?) : VideoPlayer {
//
//    private var mediaPlayer: IjkMediaPlayer? = null
//    private val stateListeners = ArrayList<PlayerStateListener>()
//    private val errorListeners = ArrayList<ErrorListener>()
//    private var currentState = PlayerState.IDLE
//
//    override fun init(context: Context?) {
//        if (mediaPlayer == null) {
//            mediaPlayer = IjkMediaPlayer()
//            IJKPlayerConfig.applyConfig(mediaPlayer)
//            setupListeners()
//            updateState(PlayerState.INITIALIZED)
//        }
//    }
//
//    private fun setupListeners() {
//        mediaPlayer?.setOnPreparedListener { mp: IMediaPlayer ->
//            Log.d("IMV", "setOnPreparedListener")
//            updateState(PlayerState.PREPARED)
//            mp.start()
//            updateState(PlayerState.STARTED)
//        }
//        mediaPlayer?.setOnInfoListener { mp: IMediaPlayer?, what: Int, extra: Int ->
//            when (what) {
//                IjkMediaPlayer.MEDIA_INFO_BUFFERING_START -> updateState(PlayerState.BUFFERING)
//                IjkMediaPlayer.MEDIA_INFO_BUFFERING_END -> updateState(PlayerState.PREPARED)
//            }
//            true
//        }
//        mediaPlayer?.setOnErrorListener { mp: IMediaPlayer?, what: Int, extra: Int ->
//            var errorType = ErrorType.UNKNOWN_ERROR
//            val msg = "Error code: $what"
//            if (what == IjkMediaPlayer.MEDIA_ERROR_IO) {
//                errorType = ErrorType.NETWORK_ERROR
//            } else if (what == IjkMediaPlayer.MEDIA_ERROR_MALFORMED) {
//                errorType = ErrorType.DECODE_ERROR
//            }
//            notifyError(errorType, msg)
//            true
//        }
//        mediaPlayer?.setOnCompletionListener { mp: IMediaPlayer? ->
//            updateState(PlayerState.COMPLETED)
//        }
//    }
//
//    // 状态更新方法
//    private fun updateState(newState: PlayerState) {
//        Log.d("IMV", "updateState $newState")
//        currentState = newState
//        for (listener in stateListeners) {
//            listener.onStateChanged(newState)
//        }
//    }
//
//    private fun notifyError(errorType: ErrorType, errorMsg: String?) {
//        Log.d("IMV", "notifyError $errorType,$errorMsg")
//        for (listener in errorListeners) {
//            listener.onError(errorType, errorMsg)
//        }
//    }
//
//    override fun setDataSource(url: String?) {
//        Log.d("IMV", "setDataSource $url")
//        try {
//            val headers = mutableMapOf<String, String>()
//            headers["User-Agent"] = "Mozilla/5.0";
//            mediaPlayer?.setDataSource(url)
//            updateState(PlayerState.INITIALIZED)
//        } catch (e: IOException) {
//            notifyError(ErrorType.NETWORK_ERROR, e.message)
//        }
//    }
//
//    override fun prepare() {
//        Log.d("IMV", "prepare")
//        mediaPlayer?.let {
//            it.prepareAsync()
//            updateState(PlayerState.PREPARING)
//        }
//    }
//
//    override fun start() {
//        Log.d("IMV", "start")
//        mediaPlayer?.let {
//            if (!it.isPlaying) {
//                it.start()
//                updateState(PlayerState.STARTED)
//            }
//        }
//    }
//
//    override fun pause() {
//        Log.d("IMV", "pause")
//        mediaPlayer?.let {
//            it.pause()
//            updateState(PlayerState.PAUSED)
//        }
//    }
//
//    override fun stop() {
//        Log.d("IMV", "stop")
//        mediaPlayer?.let {
//            it.stop()
//            updateState(PlayerState.STOPPED)
//        }
//    }
//
//    override fun release() {
//        Log.d("IMV", "release")
//        mediaPlayer?.let {
//            it.release()
//            updateState(PlayerState.IDLE)
//        }
//    }
//
//    override fun seekTo(positionMs: Long) {
//        Log.d("IMV", "seekTo $positionMs")
//        mediaPlayer?.seekTo(positionMs)
//    }
//
//    override fun setSpeed(speed: Float) {
//        Log.d("IMV", "setSpeed $speed")
//        mediaPlayer?.setSpeed(speed)
//    }
//
//    override fun setLooping(looping: Boolean) {
//        Log.d("IMV", "setLooping $looping")
//        mediaPlayer?.isLooping = looping
//    }
//
//    override fun setVolume(volume: Float) {
//        Log.d("IMV", "setVolume $volume")
//        mediaPlayer?.setVolume(volume, volume)
//    }
//
//    override val currentPosition: Long
//        get() = mediaPlayer?.currentPosition ?: 0
//    override val duration: Long
//        get() = mediaPlayer?.duration ?: 0
//    override val isPlaying: Boolean
//        get() = mediaPlayer?.isPlaying == true
//    override val state: PlayerState?
//        get() = currentState
//
//    override fun setDisplay(holder: SurfaceHolder?) {
//        Log.d("IMV", "setDisplay $holder")
//        mediaPlayer?.setDisplay(holder)
//    }
//
//    override fun setSurfaceView(surfaceView: SurfaceView?) {
//        surfaceView?.let {
//            val holder: SurfaceHolder = it.holder
//            holder.addCallback(object : SurfaceHolder.Callback {
//                override fun surfaceCreated(holder: SurfaceHolder) {
//                    setDisplay(holder); // 设置显示
//                    Log.d("IMV","surfaceCreated $holder")
//                }
//
//                override fun surfaceChanged(
//                    holder: SurfaceHolder,
//                    format: Int,
//                    width: Int,
//                    height: Int
//                ) {
//                    // empty
//                }
//
//                override fun surfaceDestroyed(holder: SurfaceHolder) {
//                    Log.d("IMV", "surfaceDestroyed $holder")
//                    stop() // 停止播放
//                    release(); // 释放资源
//                }
//            })
//        }
//
//    }
//
//    override fun addStateListener(listener: PlayerStateListener?) {
//        listener?.let { stateListeners.add(listener) }
//    }
//
//    override fun removeStateListener(listener: PlayerStateListener?) {
//        listener?.let { stateListeners.remove(listener) }
//    }
//
//    override fun addErrorListener(listener: ErrorListener?) {
//        listener?.let { errorListeners.add(listener) }
//    }
//
//    override fun removeErrorListener(listener: ErrorListener?) {
//        listener?.let { errorListeners.remove(listener) }
//    }
//
//}
