package com.files.commons.dialogs

import androidx.appcompat.app.AlertDialog
import com.files.commons.R
import com.files.commons.activities.BaseSimpleScreenActivity
import com.files.commons.extensions.*
import com.files.commons.models.BlockedNumber
import kotlinx.android.synthetic.main.dialog_add_blocked_number.view.*

class AddBlockedNumberDialog(val activity: BaseSimpleScreenActivity, val originalNumber: BlockedNumber? = null, val callback: () -> Unit) {
    init {
        val view = activity.layoutInflater.inflate(R.layout.dialog_add_blocked_number, null).apply {
            if (originalNumber != null) {
                add_blocked_number_edittext.setText(originalNumber.number)
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(view, this) { alertDialog ->
                    alertDialog.showKeyboard(view.add_blocked_number_edittext)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val newBlockedNumber = view.add_blocked_number_edittext.value
                        if (originalNumber != null && newBlockedNumber != originalNumber.number) {
                            activity.deleteBlockedNumber(originalNumber.number)
                        }

                        if (newBlockedNumber.isNotEmpty()) {
                            activity.addBlockedNumber(newBlockedNumber)
                        }

                        callback()
                        alertDialog.dismiss()
                    }
                }
            }
    }
}
