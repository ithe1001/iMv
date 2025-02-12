package com.ithe.ss.imv.ui.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.donkingliang.labels.LabelsView
import com.ithe.ss.imv.R
import com.ithe.ss.imv.label.LabelCacheProvider


class MineFragment : Fragment() {

    private var labelsView: LabelsView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mine_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labelsView = view.findViewById(R.id.mine_labelsView)

        val labels = LabelCacheProvider.get().getLabels()
        labelsView?.setLabels(labels.map { it.name })

        val selectedPosition = mutableListOf<Int>()
        labels.forEachIndexed { index, label ->
            if (label.selected) {
                selectedPosition.add(index)
            }
        }
        labelsView?.setSelects(selectedPosition)
        labelsView?.setOnLabelSelectChangeListener { _, _, isSelect, position ->
            val label = labels[position]
            LabelCacheProvider.get().setLabelState(label.id, isSelect)
            if (labelsView?.selectLabels?.isEmpty() == true) {
                labelsView?.setSelects(0)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LabelCacheProvider.get().saveLabel()
    }
}