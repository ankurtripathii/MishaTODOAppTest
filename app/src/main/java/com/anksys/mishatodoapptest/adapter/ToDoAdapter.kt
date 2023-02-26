package com.anksys.mishatodoapptest.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.anksys.mishatodoapptest.R
import com.anksys.mishatodoapptest.activity.DialogFragment
import com.anksys.mishatodoapptest.model.ToDoItem
import com.google.firebase.database.DatabaseReference

class ToDoAdapter(val items :MutableList<ToDoItem?>, val context: Context, val ref: DatabaseReference) :
    RecyclerView.Adapter<ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var time : String = ""
        val item: ToDoItem ?= items[position]
        if(item!=null) {
            holder.text.text = item.title
            holder.time.text = item.timestamp
            time = item.timestamp
            holder.checkBox.isChecked = item.status
            holder.checkBox.setOnCheckedChangeListener { _, _ ->
                item.statusChanged()
                //update status on firebase
                ref.child(item.id).setValue(item)
                Toast.makeText(context,"Status Updated to True on Firebase",Toast.LENGTH_LONG).show()
            }
            holder.buttonDelete.setOnClickListener(View.OnClickListener {
                ref.child(item.id).removeValue()
                items.remove(item)
                notifyDataSetChanged()
                Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show()
            })
        }
        holder.layout.setOnClickListener{
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialogFragment = DialogFragment()
            val args = Bundle()
            args.putString("title",item!!.title)
            args.putString("desc", item!!.desc)
            args.putString("time",time)
            args.putString("imgUri",item!!.imgUri)
            dialogFragment.arguments = args
            dialogFragment.show(fm, "Dialog Frag")
        }
    }
    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false))
    }
    override fun getItemCount(): Int {
        return items.size
    }

}
class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    val checkBox: CheckBox = view.findViewById(R.id.checkBox_item)
    val buttonDelete: ImageView = view.findViewById(R.id.button_task_delete)
    val text: TextView = view.findViewById(R.id.todotext)
    val time: TextView = view.findViewById(R.id.time)
    val layout: CardView = view.findViewById(R.id.llitems)
}