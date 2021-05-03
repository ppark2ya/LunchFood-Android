package com.lunchfood.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lunchfood.R
import com.lunchfood.data.model.filter.SelectedPlace
import com.lunchfood.ui.base.BaseListener
import kotlinx.android.synthetic.main.myplace_item.view.*

class MyFavoritePlaceAdapter(private val myFavoritePlaceList: ArrayList<SelectedPlace>): RecyclerView.Adapter<MyFavoritePlaceAdapter.DataViewHolder>() {

    private lateinit var mBaseListener: BaseListener

    fun setOnItemClickListener(mBaseListener: BaseListener) {
        this.mBaseListener = mBaseListener
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(selectedPlace: SelectedPlace) {
            itemView.apply {
                tvMyFavPlaceName.text = selectedPlace.placeName
            }

            itemView.findViewById<ImageView>(R.id.ivMyFavoritePlaceDeleteBtn).setOnClickListener {
                mBaseListener.onItemClickListener(it, selectedPlace, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.myplace_item, parent, false))

    override fun getItemCount(): Int = myFavoritePlaceList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(myFavoritePlaceList[position])
    }

    fun addPlaceInfo(selectedPlaceList: List<SelectedPlace>) {
        this.myFavoritePlaceList.apply {
            clear()
            addAll(selectedPlaceList)
        }
    }
}