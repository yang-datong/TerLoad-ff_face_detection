package com.rl.ff_face_detection_terload.ui.fargment

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rl.ff_face_detection_terload.R

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
        Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_bottom)
            window?.let {
                it.setBackgroundDrawableResource(R.drawable.bottom_dialog_backcolor)
                it.attributes.apply {
                    gravity = Gravity.BOTTOM
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    windowAnimations = R.style.BottomSheetDialogAnimation
                }
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

    open fun inits() {}

    open fun showProgress() {
        dialog.show()
    }

    open fun showBottomDialog(msg: String?, bt_tips: String?, onConfirm: () -> Unit) {
        showBottomDialog(msg, bt_tips, null, onConfirm)
    }

    open fun showBottomDialog(msg: String?, bt_tips: String?, bt_tips_color: Int?, onConfirm: () -> Unit) {
        if (message == null || confirm == null) {
            message = bottomDialog.findViewById(R.id.tv_message)
            confirm = bottomDialog.findViewById(R.id.bt_confirm)
            confirm?.let {
                bt_tips_color?.let { _ ->
                    it.setTextColor(bt_tips_color)
                }
            }
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
        if (bottomDialog.isShowing)
            bottomDialog.dismiss()
    }

    open fun dismissProgress() {
        if (bottomDialog.isShowing)
            dialog.dismiss()
    }

    abstract fun getLayoutResID(): Int
}
