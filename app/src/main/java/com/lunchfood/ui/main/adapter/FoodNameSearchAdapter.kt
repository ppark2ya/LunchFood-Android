package com.lunchfood.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lunchfood.R
import com.lunchfood.ui.base.BaseListener
import kotlinx.android.synthetic.main.food_name_item.view.*

class FoodNameSearchAdapter(private val foodList: ArrayList<String>): RecyclerView.Adapter<FoodNameSearchAdapter.DataViewHolder>() {

    private lateinit var mBaseListener: BaseListener

    fun setOnItemClickListener(mBaseListener: BaseListener) {
        this.mBaseListener = mBaseListener
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(foodName: String) {
            itemView.apply {
                tvFoodName.text = foodName
            }

            itemView.setOnClickListener {
                mBaseListener.onItemClickListener(it, foodName, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.food_name_item, parent, false))

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    fun addPlaceInfo(foodList: List<String>) {
        this.foodList.apply {
            clear()
            addAll(foodList)
        }
    }
}