package com.anksys.mishatodoapptest.activity
import android.app.ProgressDialog
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anksys.mishatodoapptest.adapter.ToDoAdapter
import com.anksys.mishatodoapptest.model.ToDoItem
import com.anksys.mishatodoapptest.databinding.ActivityMainBinding
import com.beust.klaxon.Klaxon
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
class MainActivity :AppCompatActivity(), View.OnClickListener {
    var todos: ArrayList<ToDoItem?> = ArrayList()
    val database = FirebaseDatabase.getInstance()
    lateinit var imgUri: Uri
    lateinit var title:String
    lateinit var desc: String
    val myRef = database.getReference("todo-list")
    val storageRef = Firebase.storage.reference
    val adapter: ToDoAdapter = ToDoAdapter(todos, this, myRef)
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.BOLD.setOnClickListener { binding.editTextTodoDesc.setTypeface(null, Typeface.BOLD) }
        binding.ITALIC.setOnClickListener { binding.editTextTodoDesc.setTypeface(null, Typeface.ITALIC) }
        binding.BOLDITALIC.setOnClickListener { binding.editTextTodoDesc.setTypeface(null, Typeface.BOLD_ITALIC) }
        // set click listener for add item button
        binding.buttonAddTodo.setOnClickListener(this)
        // initialise recycler view
        binding.recyclerViewTodoList.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTodoList.adapter = adapter;
        binding.buttonAddimage.setOnClickListener {
            // PICK INTENT picks item from data
            // and returned selected item
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
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
                desc = binding.editTextTodoDesc.text.toString()
                if (title != "" && desc != "") {
                    val today = Date()
                    val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                    val dateToStr: String = format.format(today)
                    val id: String = myRef.push().key.toString()
                    val todoItem = ToDoItem(id, title,false,desc,dateToStr,imgUri.toString())
                    binding.editTextTodoItem.setText("")
                    binding.editTextTodoDesc.setText("")
                    //save data on firebase
                    val a = myRef.child(id).setValue(todoItem)
                    todos.add(todoItem)
                    adapter.notifyDataSetChanged()
                    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            // this method is called
                            // when the item is moved.
                            return false
                        }
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            // this method is called when we swipe our item to right direction.
                            // on below line we are getting the item at a particular position.
                            val deletedItem: ToDoItem? = todos[viewHolder.adapterPosition]
                            // below line is to get the position
                            // of the item at that position.
                            val position = viewHolder.adapterPosition
                            // this method is called when item is swiped.
                            // below line is to remove item from our array list.
                            todos.removeAt(viewHolder.adapterPosition)
                            adapter.notifyItemRemoved(viewHolder.adapterPosition)
                            // below line is to display our snackbar with action.
                            Snackbar.make(binding.recyclerViewTodoList, "Deleted " + deletedItem!!.title, Snackbar.LENGTH_LONG)
                                .setAction(
                                    "Delete",
                                    View.OnClickListener {
                                        // adding on click listener to our action of snack bar.
                                        // below line is to add our item to array list with a position.
                                        todos.add(position, deletedItem)
                                        // below line is to notify item is
                                        // added to our adapter class.
                                        adapter.notifyItemInserted(position)
                                    }).show()
                        }
                        // at last we are adding this
                        // to our recycler view.
                    }).attachToRecyclerView(binding.recyclerViewTodoList)
                    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            // this method is called
                            // when the item is moved.
                            return false
                        }
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            // this method is called when we swipe our item to right direction.
                            // on below line we are getting the item at a particular position.
                            val deletedItem: ToDoItem? = todos[viewHolder.adapterPosition]
                            // below line is to get the position
                            // of the item at that position.
                            val position = viewHolder.adapterPosition
                            // this method is called when item is swiped.
                            // below line is to remove item from our array list.
                            todos.removeAt(viewHolder.adapterPosition)
                            adapter.notifyItemRemoved(viewHolder.adapterPosition)
                            // below line is to display our snackbar with action.
                            Snackbar.make(binding.recyclerViewTodoList, "Deleted " + deletedItem!!.title, Snackbar.LENGTH_LONG)
                                .setAction(
                                    "Delete",
                                    View.OnClickListener {
                                        // adding on click listener to our action of snack bar.
                                        // below line is to add our item to array list with a position.
                                        todos.add(position, deletedItem)
                                        // below line is to notify item is
                                        // added to our adapter class.
                                        adapter.notifyItemInserted(position)
                                    }).show()
                        }
                        // at last we are adding this
                        // to our recycler view.
                    }).attachToRecyclerView(binding.recyclerViewTodoList)
                }
                    Toast.makeText(this, "Item Added successfully", Toast.LENGTH_LONG).show()
                }
            }
    }
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            uploadImage(uri)
        } else {
            // insert code for toast showing no media selected
        }
    }
    private fun uploadImage(path: Uri) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()
        val ref: StorageReference = storageRef.child("images/" + UUID.randomUUID().toString())
        ref.putFile(path)
            .addOnSuccessListener {
                progressDialog.dismiss()
                imgUri = path
                Toast.makeText(this@MainActivity, "Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "Failed " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
            }
    }
}


