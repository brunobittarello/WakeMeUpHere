package com.example.secondemptytest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarAdapter(private val myDataset: ArrayList<Car>) : RecyclerView.Adapter<CarAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder : RecyclerView.ViewHolder
    {
        var textView: TextView

        constructor(view: View) : super(view) {
            this.textView = view.findViewById(R.id.textView1)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_car, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(view)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.text = myDataset[position].name
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}