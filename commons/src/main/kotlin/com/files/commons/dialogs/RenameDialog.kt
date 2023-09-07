//package com.files.commons.dialogs
//
//import android.view.LayoutInflater
//import android.view.WindowManager
//import androidx.appcompat.app.AlertDialog
//import com.files.commons.R
//import com.files.commons.activities.BaseSimpleScreenActivity
//import com.files.commons.adapters.RenameAdapter
//import com.files.commons.extensions.*
//import com.files.commons.helpers.RENAME_PATTERN
//import com.files.commons.helpers.RENAME_SIMPLE
//import com.files.commons.views.MyViewPager
//import kotlinx.android.synthetic.main.dialog_rename.view.*
//
//class RenameDialog(val activity: BaseSimpleScreenActivity, val paths: ArrayList<String>, val useMediaFileExtension: Boolean, val callback: () -> Unit) {
//    var dialog: AlertDialog? = null
//    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_rename, null)
//    var tabsAdapter: RenameAdapter
//    var viewPager: MyViewPager
//
//    init {
//        view.apply {
//            viewPager = findViewById(R.id.dialog_tab_view_pager)
//            tabsAdapter = RenameAdapter(activity, paths)
//            viewPager.adapter = tabsAdapter
//            viewPager.onPageChangeListener {
//                dialog_tab_layout.getTabAt(it)!!.select()
//            }
//            viewPager.currentItem = activity.baseConfig.lastRenameUsed
//
//            val textColor = context.getProperTextColor()
//            dialog_tab_layout.setTabTextColors(textColor, textColor)
//            dialog_tab_layout.setSelectedTabIndicatorColor(context.getProperPrimaryColor())
//
//            if (activity.baseConfig.isUsingSystemTheme) {
//                dialog_tab_layout.setBackgroundColor(activity.resources.getColor(R.color.you_dialog_background_color))
//            }
//
//            dialog_tab_layout.onTabSelectionChanged(tabSelectedAction = {
//                viewPager.currentItem = when {
//                    it.text.toString().equals(resources.getString(R.string.simple_renaming), true) -> RENAME_SIMPLE
//                    else -> RENAME_PATTERN
//                }
//            })
//        }
//
//        activity.getAlertDialogBuilder()
//            .setPositiveButton(R.string.ok, null)
//            .setNegativeButton(R.string.cancel) { dialog, which -> dismissDialog() }
//            .apply {
//                activity.setupDialogStuff(view, this) { alertDialog ->
//                    alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
//                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
//                        tabsAdapter.dialogConfirmed(useMediaFileExtension, viewPager.currentItem) {
//                            dismissDialog()
//                            if (it) {
//                                activity.baseConfig.lastRenameUsed = viewPager.currentItem
//                                callback()
//                            }
//                        }
//                    }
//                }
//            }
//    }
//
//    private fun dismissDialog() {
//        dialog!!.dismiss()
//    }
//}
