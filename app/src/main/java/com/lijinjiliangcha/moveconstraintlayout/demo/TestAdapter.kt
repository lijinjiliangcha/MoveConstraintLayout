package com.lijinjiliangcha.moveconstraintlayout.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TestAdapter : RecyclerView.Adapter<TestAdapter.Holder> {

    private val context: Context
    private val dataList = ArrayList<Int>()

    constructor(context: Context) {
        this.context = context
        reset()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tv.text = dataList[position].toString()
    }

    fun reset() {
        dataList.clear()
        add()
    }

    fun add() {
        for (i in 0 until 30) {
            dataList.add()
        }
        notifyDataSetChanged()
//        notifyItemRangeChanged(0, 20)
    }

    fun ArrayList<Int>.add() {
        add(size)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: TextView by lazy { itemView.findViewById<TextView>(R.id.tv) }
    }

}