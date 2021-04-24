package com.lunchfood.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lunchfood.R
import com.lunchfood.data.model.history.PlaceInfo
import com.lunchfood.ui.base.BaseListener
import kotlinx.android.synthetic.main.place_name_item.view.*

class PlaceNameSearchAdapter(private val placeInfoList: ArrayList<PlaceInfo>): RecyclerView.Adapter<PlaceNameSearchAdapter.DataViewHolder>() {

    private lateinit var mBaseListener: BaseListener

    fun setOnItemClickListener(mBaseListener: BaseListener) {
        this.mBaseListener = mBaseListener
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(placeInfo: PlaceInfo) {
            itemView.apply {
                tvPlaceName.text = placeInfo.placeName
                tvRoadAddrName.text = placeInfo.roadAddressName
            }

            itemView.setOnClickListener {
                mBaseListener.onItemClickListener(it, placeInfo, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.place_name_item, parent, false))

    override fun getItemCount(): Int = placeInfoList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(placeInfoList[position])
    }

    fun addPlaceInfo(placeInfoList: List<PlaceInfo>) {
        this.placeInfoList.apply {
            clear()
            addAll(placeInfoList)
        }
    }
}