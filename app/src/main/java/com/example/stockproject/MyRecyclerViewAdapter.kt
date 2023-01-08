package com.example.stockproject

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stockproject.databinding.LayoutRecyclerItemBinding

class MyRecyclerViewAdapter(context: Context) : RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {
    var dataList = ArrayList<MyModel>()
    lateinit var context: Context

    init {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LayoutRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position], context)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // View Holder
    inner class MyViewHolder(private val binding: LayoutRecyclerItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(myModel: MyModel, context: Context){
            binding.dataNameTv.text = myModel.data_name
            binding.dataTv.text = myModel.data
            binding.data2Tv.text = myModel.data2
            binding.infoBtn.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                    builder.setTitle(myModel.data_name)
                        .setMessage(sendMsg(myModel.data_name))
                builder.show()
            }
        }

        private fun sendMsg(content: String): String{
            return when (content) {
                "PBR" -> "PBR"
                "EPS" -> "EPS"
                "PER" -> "PER"
                "BPS" -> "BPS"
                else -> "ROE"
            }
        }
    }

}