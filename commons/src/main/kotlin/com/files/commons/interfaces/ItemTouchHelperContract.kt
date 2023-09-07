package com.files.commons.interfaces

import com.files.commons.adapters.MyRecyclerViewAdapter

interface ItemTouchHelperContract {
    fun onRowMoved(fromPosition: Int, toPosition: Int)

    fun onRowSelected(myViewHolder: MyRecyclerViewAdapter.ViewHolder?)

    fun onRowClear(myViewHolder: MyRecyclerViewAdapter.ViewHolder?)
}
