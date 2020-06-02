package com.sun.foodiary

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sun.foodiary.Class.Diary
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.item_diary.view.*


class DiaryListAdapter (private val d_realmResult: OrderedRealmCollection<Diary>, private val context: Context,
                        private val click: (Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false))

    override fun getItemCount(): Int = d_realmResult.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val d_item = d_realmResult[position]
        holder.itemView.d_title.text = d_item.d_title
        holder.itemView.d_date.text = DateFormat.format("yyyy/MM/dd", d_item.d_date)
       // holder.itemView.d_imageView.image = d_item.d_src

        holder.itemView.d_contents.text = d_item.d_text

            //여기서 이미지 어떻게 띄우지,,,,
        if(d_item.d_src!=null){
             Glide.with(holder.itemView.context).load(d_item.d_src).into(holder.itemView.findViewById<ImageView>(R.id.d_imageView))
            //Glide.with(context).load(d_item.d_src).into(holder.itemView.d_imageView)

        }






        holder.itemView.setOnClickListener { click(d_item.d_id) }

    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}