package com.abhi41.runnigappyt.presentation.screen

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.abhi41.runnigappyt.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog: DialogFragment() {

    private var yesListener: (() -> Unit)? = null

    fun setYesListener(listener: () -> Unit){
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                //stopRun()
                yesListener?.let { yes->
                    yes()
                }
            }.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }.create()

    }

}