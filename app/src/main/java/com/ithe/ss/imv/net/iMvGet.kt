package com.ithe.ss.imv.net

import android.text.TextUtils
import android.util.Log
import com.ithe.ss.imv.label.LabelCache
import com.ithe.ss.imv.label.LabelCacheProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random


class RetrofitProvider {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("http://api.mmp.cc/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun get(): Retrofit {
            return retrofit
        }
    }
}

interface VideoApi {

    @GET("api/ksvideo")
    fun getVideoById(
        @Query("type") type: String = "json",
        @Query("id") id: String
    ): Call<VideoBean?>?


    @GET("api/miss?type=json")
    fun getVideoByMiss(): Call<VideoBean?>?
}

data class VideoBean(
    val status: String?,
    val link: String?
)

class IMVCenter {

    companion object {
        private val datacenterMap = mutableMapOf<String, IMVCenter>()

        fun get(type: String?): IMVCenter? {
            val key = type ?: "default"
            if (!datacenterMap.containsKey(key)) {
                datacenterMap[key] = IMVCenter()
            }
            return datacenterMap[key]
        }
    }

    private val PRELOAD_COUNT = 3
    private val DEFAULE_URL =
        "https://alimov2.a.kwimgs.com/upic/2024/01/22/20/BMjAyNDAxMjIyMDA2MTJfMzgyMzQ3MjQ1XzEyMjc2MDIzMjEzOV8xXzM=_b_B46d94e5bc6f667add367442e22fee82f.mp4?clientCacheKey=3xw53em5esqzpiu_b.mp4&tt=b&di=78e498d6&bp=13414"
    private val videoList = CopyOnWriteArrayList<String>()
    private val videoTypeList by lazy {
        LabelCacheProvider.get().getLabels().filter { it.selected }.map { it.id }
    }

    fun initVideoList() {
        for (i in 0..PRELOAD_COUNT) {
            fetchVideo(null) { url: String? ->
                url?.let { videoList.add(it) }
            }
        }
        Log.d("imv_net", "videoList size = ${videoList.size}")
    }

    fun getVideo(
        position: Int,
        id: String?,
        callback: (url: String) -> Unit
    ) {
        if (position < videoList.size) {
            callback.invoke(videoList[position])
        } else {
            fetchVideo(id) { url: String? ->
                if (TextUtils.isEmpty(url)) {
                    getVideo(position, id, callback)
                } else {
                    val videoUrl = url ?: DEFAULE_URL
                    videoList.add(videoUrl)
                    callback.invoke(videoList[position])
                }
            }
        }
        if (position + PRELOAD_COUNT >= videoList.size) {
            fetchVideo(id) { url: String? ->
                url?.let { videoList.add(it) }
            }
        }
        Log.d("imv_net", "videoList size = ${videoList.size}")
    }

    private fun fetchVideo(
        id: String?,
        callback: (url: String?) -> Unit
    ) {
        val videoApi: VideoApi = RetrofitProvider.get().create(VideoApi::class.java)
        val configId = id ?: videoTypeList[Random.nextInt(videoTypeList.size)]
        videoApi.getVideoById(type = "json", id = configId)?.enqueue(object : Callback<VideoBean?> {
            override fun onResponse(
                call: Call<VideoBean?>,
                response: Response<VideoBean?>
            ) {
                val videoBean = response.body()
                Log.d("imv_net", "onResponse status = ${videoBean?.status}")
                if (videoBean?.status == "success") {
                    videoBean.link?.let { callback.invoke(videoBean.link) }
                } else {
                    callback.invoke(null)
                }
            }

            override fun onFailure(call: Call<VideoBean?>, t: Throwable) {
                Log.d("imv_net", "onFailure")
                callback.invoke(null)
            }
        })

//            videoApi.getVideoByMiss()?.enqueue(object : Callback<VideoBean?> {
//                override fun onResponse(
//                    call: Call<VideoBean?>,
//                    response: Response<VideoBean?>
//                ) {
//                    val videoBean = response.body()
//                    Log.d("imv_net", "onResponse status = ${videoBean?.status}")
//                    if (videoBean?.status == "success") {
//                        videoBean.link?.let { callback.invoke(videoBean.link) }
//                    } else {
//                        callback.invoke(null)
//                    }
//                }
//
//                override fun onFailure(call: Call<VideoBean?>, t: Throwable) {
//                    Log.d("imv_net", "onFailure")
//                    callback.invoke(null)
//                }
//            })
    }
}

