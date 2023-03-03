package com.main.todo

//this data class is to hold  some  of the data . primary constructor should have at least one parameter.primary constructor should be val or var.data class
// not be abstract,open,sealed or inner.Only implement interfaces
data class Todo(  val todo: String,
                  var isChecked: Boolean= false,
                  val id: String)
