package com.rl.ff_face_detection_terload.ui.fargment

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rl.ff_face_detection_terload.R

abstract class BaseFragment : Fragment() {

    private val dialog by lazy {
        context?.let {
            Dialog(it, R.style.DialogStyle).apply {
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

    open fun showProgress(message: String) {
        dialog?.show()
    }

    open fun dismissProgress() {
        dialog?.dismiss()
    }

    abstract fun getLayoutResID(): Int
}