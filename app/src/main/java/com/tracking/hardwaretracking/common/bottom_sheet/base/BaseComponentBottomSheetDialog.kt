package com.tracking.hardwaretracking.common.bottom_sheet.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tracking.hardwaretracking.R

abstract class BaseComponentBottomSheetDialog<VB : ViewBinding>(
    @StyleRes private val mTheme: Int = R.style.BottomSheetStyle
) : BottomSheetDialogFragment() {

    protected lateinit var binding: VB
    abstract val inflateLayout: (LayoutInflater) -> VB
    protected abstract fun setupViews()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateLayout(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, mTheme)
    }

    fun safeShow(fragmentManager: FragmentManager) {
        if (isAlreadyAdded(fragmentManager).not()) show(fragmentManager, TAG)
    }

    private fun isAlreadyAdded(fragmentManager: FragmentManager): Boolean {
        return (fragmentManager.findFragmentByTag(TAG))?.isAdded ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    companion object {
        const val TAG = "BaseComponentBottomSheetDialog"
    }
}
