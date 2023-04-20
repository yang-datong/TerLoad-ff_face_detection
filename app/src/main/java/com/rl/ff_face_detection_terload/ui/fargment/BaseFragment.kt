package com.rl.ff_face_detection_terload.ui.fargment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.ui.activity.BaseActivity

abstract class BaseFragment : Fragment() {
    private var message: TextView? = null
    private var confirm: Button? = null

    private val dialog by lazy {
        Dialog(requireContext()).apply {
            setCancelable(false)
            setContentView(R.layout.dialog_loading)
            window!!.setBackgroundDrawableResource(R.drawable.bg_loading)
            window!!.attributes.apply {
                gravity = Gravity.CENTER
                width = 500
                height = 500
                alpha = 0.7f
            }
        }
    }

    private val bottomDialog by lazy {
//        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle).apply {
//            setContentView(R.layout.dialog_bottom)
//            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        }
        Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_bottom)
            window!!.setBackgroundDrawableResource(R.drawable.bg_loading)
            window!!.attributes.apply {
                gravity = Gravity.BOTTOM
                width = ViewGroup.LayoutParams.MATCH_PARENT
                windowAnimations = R.style.BottomSheetDialogAnimation
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutResID(), null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
    }

    open fun inits() {
    }

    open fun showProgress() {
        dialog.show()
    }

    open fun showBottomDialog(msg: String?, bt_tips: String?, onConfirm: () -> Unit) {
        if (message == null || confirm == null) {
            message = bottomDialog.findViewById(R.id.tv_message)
            confirm = bottomDialog.findViewById(R.id.bt_confirm)
        }
        bottomDialog.apply {
            if (!msg.isNullOrEmpty())
                message!!.text = msg
            if (!bt_tips.isNullOrEmpty())
                confirm!!.text = bt_tips
            confirm!!.setOnClickListener {
                onConfirm()
                dismiss()
            }
            show()
        }
    }

    open fun dismissBottomDialog() {
        bottomDialog.dismiss()
    }

    open fun dismissProgress() {
        dialog.dismiss()
    }

    abstract fun getLayoutResID(): Int
}