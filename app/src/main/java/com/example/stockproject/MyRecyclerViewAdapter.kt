package com.example.stockproject

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stockproject.databinding.LayoutRecyclerItemBinding

class MyRecyclerViewAdapter: RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {
    var dataList = ArrayList<MyModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LayoutRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // View Holder
    inner class MyViewHolder(private val binding: LayoutRecyclerItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(myModel: MyModel){
            binding.dataNameTv.text = myModel.data_name
            binding.dataTv.text = myModel.data
        }
    }
    
}