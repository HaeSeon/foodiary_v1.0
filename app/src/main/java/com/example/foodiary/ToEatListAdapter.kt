package com.example.foodiary

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.item_toeat.view.*

class ToEatListAdapter(private val realmResult: OrderedRealmCollection<ToEat>,
                      private val click: (Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toeat, parent, false))

    override fun getItemCount(): Int = realmResult.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = realmResult[position]
        holder.itemView.text1.text = item.title
        holder.itemView.text2.text = DateFormat.format("yyyy/MM/dd", item.date)

        holder.itemView.setOnClickListener { click(item.id) }

    }


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}