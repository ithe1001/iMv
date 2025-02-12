package com.ithe.ss.imv.label

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.ithe.ss.imv.ContextProvider
import java.lang.reflect.Type

class LabelCacheProvider {
    companion object {
        private var labelCache: LabelCache? = null

        fun get(): LabelCache {
            if (labelCache == null) {
                labelCache = LabelCache(ContextProvider.get())
            }
            return labelCache!!
        }
    }
}


class LabelCache(
    context: Context
) {

    private val defaultLabels = mutableListOf(
        Label("JK", "jk", true),
        Label("欲梦", "YuMeng", false),
        Label("女大学生", "NvDa", false),
        Label("女高中生", "NvGao", false),
        Label("热舞", "ReWu", true),
        Label("清纯", "QingCun", false),
        Label("玉足", "YuZu", true),
        Label("蛇姐", "SheJie", true),
        Label("穿搭", "ChuanDa", false),
        Label("高质量小姐姐", "GaoZhiLiangXiaoJieJie", false),
        Label("汉服", "HanFu", false),
        Label("黑丝", "HeiSi", true),
        Label("变装", "BianZhuang", true),
        Label("萝莉", "Luoli", false),
        Label("甜妹", "TianMei", false),
        Label("白丝", "Baisi", true)
    )

    private var labelList = mutableListOf<Label>()
    private val sharedPreferences =
        context.getSharedPreferences("imv_mine_labels", Context.MODE_PRIVATE)

    fun getLabels(): List<Label> {
        if (labelList.isEmpty()) {
            val cacheLabels = sharedPreferences?.getString("imv_mine_labels", null)
            if (!cacheLabels.isNullOrEmpty()) {
                val type: Type = object : TypeToken<ArrayList<Label?>?>() {}.type
                labelList = Gson().fromJson(cacheLabels, type)
            }

            if (labelList.isEmpty()) {
                labelList = defaultLabels
            }
        }
        return labelList
    }

    fun setLabelState(id: String, selected: Boolean) {
        labelList.find { it.id == id }?.selected = selected
    }

    fun saveLabel() {
        sharedPreferences.edit()
            .putString("imv_mine_labels", Gson().toJson(labelList))
            .apply()
    }
}

data class Label(
    val name: String,
    val id: String,
    var selected: Boolean
)