package com.anksys.mishatodoapptest.activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.anksys.mishatodoapptest.adapter.ToDoAdapter
import com.anksys.mishatodoapp.model.ToDoItem
import com.anksys.mishatodoapptest.databinding.ActivityMainBinding
import com.beust.klaxon.Klaxon
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
class MainActivity :AppCompatActivity(), View.OnClickListener {
    var todos: ArrayList<ToDoItem?> = ArrayList()
    val database = FirebaseDatabase.getInstance()
    lateinit var title:String
    val myRef = database.getReference("todo-list")
    val adapter: ToDoAdapter = ToDoAdapter(todos, this, myRef)
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        // set click listener for add item button
        binding.buttonAddTodo.setOnClickListener(this)
        // initialise recycler view
        binding.recyclerViewTodoList.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTodoList.adapter = adapter
        // Read from the database only once for initialization
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value
                if (dataSnapshot.exists()) {
                    val todoHash = dataSnapshot.value as HashMap<*, *>
                    for ((k, v) in todoHash) {
                        // converted datasnapshot ot json format
                        val jaon=   JSONObject(v.toString())
                        //parse json to modal class
                        val result = Klaxon()
                            .parse<ToDoItem>(jaon.toString())
                        todos.add(result)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }
    override fun onClick(view: View) {
        when (view) {
            // add item button action
            binding.buttonAddTodo -> {
                 title = binding.editTextTodoItem.text.toString()
                if (title != "") {
                    val today = Date()
                    val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                    val dateToStr: String = format.format(today)
                    val id: String = myRef.push().key.toString()
                    val todoItem = ToDoItem(id, title,false,dateToStr)
                    binding.editTextTodoItem.setText("")
                    //save data on firebase
                    val a = myRef.child(id).setValue(todoItem)
                    todos.add(todoItem)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Item Added successfully", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


