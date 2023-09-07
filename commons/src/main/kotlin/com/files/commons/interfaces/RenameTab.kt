package com.files.commons.interfaces

import com.files.commons.activities.BaseSimpleScreenActivity

interface RenameTab {
    fun initTab(activity: BaseSimpleScreenActivity, paths: ArrayList<String>)

    fun dialogConfirmed(useMediaFileExtension: Boolean, callback: (success: Boolean) -> Unit)
}
