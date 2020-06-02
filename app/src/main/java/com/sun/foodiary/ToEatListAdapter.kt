package com.sun.foodiary

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.sun.foodiary.Class.ToEat
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.item_toeat.view.*


//private val context 로 context 받아옴
class ToEatListAdapter(private val realmResult: OrderedRealmCollection<ToEat>, private val context:Context,
                       private val click: (Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toeat, parent, false))

    override fun getItemCount(): Int = realmResult.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = realmResult[position]
        holder.itemView.d_text1.text = item.title
        holder.itemView.d_text2.text = DateFormat.format("yyyy/MM/dd", item.date)

        holder.itemView.findViewById<AppCompatImageButton>(R.id.delete_button).setOnClickListener {
            EatlistEditActivity().deleteTodo(item.id)


            //Context 를 안들고있어서 anko 라이브러리 사용 불가
            //Context 를 EatlistActivity 로부터 받아와서 toast 메세지 출력
            Toast
                .makeText(context, "successfully deleted!", Toast.LENGTH_SHORT)
                .apply {
                    show()
                }
        }
        holder.itemView.setOnClickListener { click(item.id) }

    }



    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}