package com.example.ittalian.quicksearchroute

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var departStationText: TextView? = null
    var arriveStationText: TextView? = null
    var editBtn: Button? = null

    init {
        departStationText = itemView.findViewById(R.id.departStationText)
        arriveStationText = itemView.findViewById(R.id.arriveStationText)
        editBtn = itemView.findViewById(R.id.editBtn)
    }
}