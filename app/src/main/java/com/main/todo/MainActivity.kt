package com.main.todo

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import java.text.SimpleDateFormat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.todo.Constants.Companion.PREF_ARRAY
import com.main.todo.Constants.Companion.PREF_NAME
import com.main.todo.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding//binding
    private lateinit var todoAdapter: TodoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initUI()


    }


    private fun initUI() { //floating action button working
        getAllTasks()
        binding.floatingActionButton.setOnClickListener {
            showInputAlert()
        }
        setDate()
    }

    private fun getAllTasks() {
        val  sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)//gets saved array of tudos
        val existingArray = sharedPref.getString(PREF_ARRAY,"[]") ?: "[]"
        val array = JSONArray(existingArray)//converting to jsonaaray
        val list = mutableListOf<Todo>()
        for (i in 0 until array.length()){
            val jsonObject =  array.get(i)as? JSONObject
            jsonObject.let{
                val model = Gson().fromJson<Todo>(it.toString(),Todo::class.java)
                list.add(model)
            }
        }

        //instantiate TodoAdapter
        todoAdapter= TodoAdapter(list)
        //set the layout manager of recycler view
        binding.recyclerTasks.layoutManager= LinearLayoutManager(this)
        //set the adapter of recycler view
        binding.recyclerTasks.adapter = todoAdapter
    }


    private fun setDate() {
        val date=Date()
        val dateFormat = SimpleDateFormat("dd,MM,yyy",Locale.getDefault())
        val dateString= dateFormat.format(date)
        binding.dates.text=  dateString
    }


    private fun showInputAlert() { //dialog box with frame layout
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Enter your task")
        val layout = FrameLayout(this)// frame layout object
        layout.setPaddingRelative(45, 15, 45, 15)
        val edittext = EditText(this) //edittext object
        edittext.hint = "enter a task"
        edittext.maxLines = 1
        layout.addView(edittext) //adding the view to the layout
        alert.setView(layout)//set the layout to the alert dialog
        alert.setPositiveButton("Save") { _, _ ->
            val task = edittext.text.toString()
            if (task.isNotEmpty()) {
                saveTask(task)
            }

        }
        alert.setNegativeButton("cancel",null)
        alert.show()

    }
    private fun saveTask(task: String){
        val  todo =Todo (task,false, UUID.randomUUID().toString())
        todoAdapter.addTodo(todo)
        //JSONObject is a modifiable set of name/value mappings.
//        val jsonObject = JSONObject()
//        //put() Maps name to value, clobbering any existing name/value mapping with the same name.
//        jsonObject.put("task", task)
//        jsonObject.put("isCompleted", false)
//        jsonObject.put("id", UUID.randomUUID().toString().replace("-", "").uppercase())

        val sharedPref= getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        //creating a shared pref file or access existing one
        //getString() retrieves string values from a shared preferences file\
        val existingArray = sharedPref.getString(PREF_ARRAY,"[]"?:"[]")
//converting string t json array
        val array = JSONArray(existingArray)
        //converting data class to json to save (use Gson library)
        val todoJSon = JSONObject(Gson().toJson(todo))
        //put() add  element to the end of the array
        array.put(todoJSon)
        //sharedpref.editor
        val editor = sharedPref.edit()
        //putstring() writes string values to a shared pref file
        editor.putString(PREF_ARRAY,array.toString())
        //apply() save the changes to shared pref files
        editor.apply()
    }
}

