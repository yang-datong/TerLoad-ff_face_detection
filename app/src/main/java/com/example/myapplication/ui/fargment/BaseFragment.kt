package com.example.myapplication.ui.fargment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    private val progressDialog by lazy { ProgressDialog(context) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutResID(),null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits();
    }


    open fun showProgress(message : String){
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    open fun inits() {
    }

    abstract fun getLayoutResID(): Int
}