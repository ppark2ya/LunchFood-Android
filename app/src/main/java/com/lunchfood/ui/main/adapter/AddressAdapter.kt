package com.lunchfood.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lunchfood.R
import com.lunchfood.data.model.AddressItem
import com.lunchfood.ui.base.BaseListener
import com.lunchfood.utils.Dlog
import kotlinx.android.synthetic.main.address_item.view.*

class AddressAdapter(private val addressList: ArrayList<AddressItem>): RecyclerView.Adapter<AddressAdapter.DataViewHolder>() {

    private lateinit var mBaseListener: BaseListener

    fun setOnItemClickListener(mBaseListener: BaseListener) {
        this.mBaseListener = mBaseListener
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(address: AddressItem) {
            itemView.apply {
                tvJibunAddr.text = address.jibunAddr
                tvRoadAddr.text = address.roadAddr
            }

            itemView.setOnClickListener {
                mBaseListener.onItemClickListener(it, address, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false))

    override fun getItemCount(): Int = addressList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(addressList[position])
    }

    fun addAddress(addressList: List<AddressItem>) {
        this.addressList.apply {
            clear()
            addAll(addressList)
        }
    }
}