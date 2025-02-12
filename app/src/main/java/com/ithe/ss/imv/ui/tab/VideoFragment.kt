package com.ithe.ss.imv.ui.tab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ithe.ss.imv.R
import com.ithe.ss.imv.net.IMVCenter
import com.ithe.ss.imv.ui.video.EXOPlayerFragment

// https://api.mmp.cc/doc/ksvideo.html
// http://api.mmp.cc/api/ksvideo?type=json&id=jk
// https://api.mmp.cc/api/miss?type=json

// boy:https://api.52vmy.cn/api/video/boy
class VideoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IMVCenter.get(null)?.initVideoList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager2 = view.findViewById<ViewPager2>(R.id.video_list_viewpager)
        viewPager2.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager2.adapter = VideoListAdapter(this)
    }

    class VideoListAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = Int.MAX_VALUE
        override fun createFragment(position: Int): Fragment {
            return EXOPlayerFragment(position, null)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("iMV", "VideoFragment onPause")
    }
}