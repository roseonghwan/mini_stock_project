package com.example.stockproject

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stockproject.databinding.LayoutRecyclerItemBinding

class MyRecyclerViewAdapter(context: Context, stock_price: Long, stock_price2: Long) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {
    var dataList = ArrayList<MyModel>()
    lateinit var context: Context
    var stock_price: Long = 0
    var stock_price2: Long = 0

    init {
        this.context = context
        this.stock_price = stock_price
        this.stock_price2 = stock_price2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position], context)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // View Holder
    inner class MyViewHolder(private val binding: LayoutRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(myModel: MyModel, context: Context) {
            binding.dataNameTv.text = myModel.data_name
            binding.dataTv.text = myModel.data
            binding.data2Tv.text = myModel.data2
            makeColor(myModel.data_name, myModel.data, myModel.data2)
            binding.infoBtn.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("${myModel.data_name}이란?")
                    .setMessage(sendMsg(myModel.data_name))
                builder.show()
            }
        }

        private fun makeColor(title: String, left: String, right: String) {
            if (title == "EPS" || title == "BPS" || title == "ROE" || title == "ROA"){
                if (left.toDouble() > right.toDouble()) {
                    binding.dataTv.setTextColor(Color.parseColor("#FF0000"))
                    binding.data2Tv.setTextColor(Color.parseColor("#0000FF"))
                } else {
                    binding.data2Tv.setTextColor(Color.parseColor("#FF0000"))
                    binding.dataTv.setTextColor(Color.parseColor("#0000FF"))
                }
            }
            else if (title == "PER" || title == "PBR") {
                if (left.toDouble() > right.toDouble()) {
                    binding.dataTv.setTextColor(Color.parseColor("#0000FF"))
                    binding.data2Tv.setTextColor(Color.parseColor("#FF0000"))
                } else {
                    binding.data2Tv.setTextColor(Color.parseColor("#0000FF"))
                    binding.dataTv.setTextColor(Color.parseColor("#FF0000"))
                }
            }
            else if (title == "목표주가"){
                Log.e("left and right", "$left and $right")
                if (left.toDouble() > stock_price) {
                    if (right.toDouble() > stock_price2){
                        Log.e("this???", "${right.toDouble()} and $stock_price2")
                        binding.dataTv.setTextColor(Color.parseColor("#FF0000"))
                        binding.data2Tv.setTextColor(Color.parseColor("#FF0000"))
                    } else {
                        binding.dataTv.setTextColor(Color.parseColor("#FF0000"))
                        binding.data2Tv.setTextColor(Color.parseColor("#0000FF"))
                    }
                }
                else {
                    if (right.toDouble() > stock_price2) {
                        binding.dataTv.setTextColor(Color.parseColor("#0000FF"))
                        binding.data2Tv.setTextColor(Color.parseColor("#FF0000"))
                    } else {
                        binding.dataTv.setTextColor(Color.parseColor("#0000FF"))
                        binding.data2Tv.setTextColor(Color.parseColor("#0000FF"))
                    }
                }
            }
        }

        private fun sendMsg(content: String): String {
            return when (content) {
                "현재가격" -> "현재 주가를 나타냅니다."

                "EPS" -> "Earning Per Share, '주당순이익'이라고 합니다.\n\n" +
                        "계산법은 당기순이익 / 발행주식 수로, 회사가 1주당 얼마의 순이익을 내는지 의미하므로, 수치가 높을수록 경영실적이 양호하고 배당 여력도 많다는 뜻으로 볼 수 있습니다.\n\n" +
                        "더불어 EPS가 우상향하고 있으면 더욱 좋습니다:)"

                "PER" -> "Price Earning Ratio, '주가수익비율'이라고 합니다.\n\n" +
                        "계산법은 현재가 / 주당순이익(EPS)로, 주식 1주가 순이익의 몇배가 되는지 나타내는지 의미하므로, 수치가 낮을수록 주식가격 상승확률이 높아 저평가되어 있다는 뜻으로 볼 수 있습니다.\n\n" +
                        "PER을 비교할 때 같은 산업군끼리 비교하는것이 더욱 효과적입니다:)"

                "BPS" -> "Book-Value Per Share, '주당순자산가치' 혹은 '청산가치'라고 합니다.\n\n" +
                        "계산법은 순자산 / 발행주식 수로, 1주당 순자산이 얼마인지를 의미하므로, 수치가 높을수록 회사 가치가 높다는 뜻으로 볼 수 있습니다.\n\n" +
                        "BPS는 주가 정보가 고려되지 않았으므로 회사의 평가기준은 PBR을 통해 확인합니다:)"

                "PBR" -> "Price Book-Value Ration, '주가순자산비율'이라고 합니다.\n\n" +
                        "계산법은 현재가 / 주당순자산가치(BPS)로, 현재가가 자산가치에 비해 얼마나 부풀려있는지 의미하므로, 1보다 작으면 주식의 가치가 자산의 가치보다 저평가, 크면 고평가되어 있다는 뜻으로 볼 수 있습니다.\n\n" +
                        "PBR을 비교할 때 역시 같은 산업군끼리 비교하는 것이 더욱 효과적입니다:)"

                "ROE" -> "Return On Enquity, '자기자본이익률'이라고 합니다.\n\n" +
                        "계산법은 당기순이익 / 자본총액 * 100으로, 자본 대비 얼마나 많은 수익을 내는지 의미하므로, 수치가 높을수록 높은 수익을 냈다는 뜻으로 볼 수 있습니다.\n" +
                        "ROE가 10 이상이고 우상향 중이라면, 적정 투자 대상이라고 평가할 수 있습니다:)"

                "ROA" -> "Return On Assets, '총자산이익률'이라고 합니다.\n\n" +
                        "계산법은 당기순이익 / 자산총액 * 100으로, 자산 대비 얼마나 많은 수익을 내는지 의미하므로, 수치가 높을수록 자산을 효율적으로 운용했다는 뜻으로 볼 수 있습니다.\n" +
                        "기업이 레버리지를 이용했을 경우, ROE가 높아질 수 있으므로 ROA도 같이 비교해보면 더욱 효과적입니다:)"

                else -> "계산법은 PER * EPS로, 특정 주식이 도달 가능할 것이라 예상한 가격을 의미하므로, 현재 가격과 비교해볼 수 있습니다:)"
            }
        }
    }

}