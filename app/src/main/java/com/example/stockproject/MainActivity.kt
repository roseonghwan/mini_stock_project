package com.example.stockproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stockproject.databinding.ActivityMainBinding
import com.example.stockproject.distributionStockData.DistributionStockInfo
import com.example.stockproject.financeData.FinanceInfo
import com.example.stockproject.priceData.PriceInfo
import com.google.gson.GsonBuilder
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    // 전역변수로 바인딩 객체 선언
    private var mBinding: ActivityMainBinding? = null;
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재선언
    private  val binding get() = mBinding!!

    val modelList = ArrayList<MyModel>()
    var stock_price:Long = 0
    var dangi:Long = 0
    var tot_asset:Long = 0
    var stock_cnt:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        // 자동 생성된 뷰 바인딩 클래스에서의 inflate 메서드를 활용해 액티비티에서 사용할 바인딩 클래스 인스턴스 선언
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의 인스턴스를 활용해 생성된 뷰를 액티비티에 표시
        setContentView(binding.root)
        // 이제 binding 변수를 활용해 xml 파일 내 뷰 id 접근이 가능해짐!
        val search_btn = binding.searchBtn
        val gson = GsonBuilder().setLenient().create()


        val dartRetrofit = Retrofit.Builder()
            .baseUrl("https://opendart.fss.or.kr")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val krxRetrofit = Retrofit.Builder()
            .baseUrl("https://data-dbg.krx.co.kr")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val corpor_json = assets.open("corpcode.json").reader().readText()
        val code_data = JSONObject(corpor_json).getJSONObject("result")
        val listStore = code_data.getJSONArray("list")

        // 회사 이름 검색 시 회사코드 가져오기
        search_btn.setOnClickListener(){
            val corp_txt:String = binding.searchEt.text.toString()
            Log.e("click!!", listStore.length().toString() + corp_txt)
            for (i in 0 until listStore.length()){
                val iObj = listStore.getJSONObject(i)
                val iName = iObj.get("corp_name").toString()
                val iCode = iObj.get("corp_code").toString()
                if(corp_txt == iName) {
                    Log.e("CODE", iCode)
                    getFinanceInfo(dartRetrofit, iCode)
                    getDistributionStockInfo(dartRetrofit, iCode)
                    getPriceInfo(krxRetrofit, iName)
                    break
                }
            }
            // 음... 왜 안바뀔까...
            Log.e("모든 결과 실행 후...","유통주식수: $stock_cnt, 총자본: $tot_asset, 당기순이익: $dangi, 주식가격: $stock_price")
            // EPS 주당 순이익
            // PER 주가수익 비율
            // BPS 주당 순가치
            // PBR 주가 순자산 비율
            // ROE 자기자본이익률
            modelList.add(MyModel("EPS", "주당 주이익"))
            modelList.add(MyModel("PER", "주가수익 비율"))
            modelList.add(MyModel("BPS", "주당 순가치"))
            modelList.add(MyModel("PBR", "주가 순자산 비율"))
            modelList.add(MyModel("추가1", "추가정보1"))
            modelList.add(MyModel("추가2", "추가정보2"))
            modelList.add(MyModel("추가3", "추가정보3"))
            modelList.add(MyModel("추가4", "추가정보4"))
            makeRecyclerView()
        }
    }
    // 리사이클러뷰 표시
    private fun makeRecyclerView(){
        val adapter = MyRecyclerViewAdapter()
        adapter.dataList = modelList
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getFinanceInfo(retrofit: Retrofit, code: String) {
        val financeService: FinanceService? = retrofit.create(FinanceService::class.java)
        val dart_key = "e94c917f133dd9f8d1e5d62552bf010eba18801c"

        financeService?.getInfo(dart_key, code, "2022", "11014")
            ?.enqueue(object : Callback<FinanceInfo>{
                override fun onResponse(call: Call<FinanceInfo>, response: Response<FinanceInfo>) {
                    Log.e("재무 응답 상태", response.body()?.status.toString())
                    Log.e("재무 응답 메시지", response.body()?.message.toString())

                    val result_list = response.body()?.list
                    // 총 자본
                    val tot_capiital = stringToNum(result_list?.get(8)?.thstrm_amount.toString())
                    // 당기순이익
                    val accu_amount = stringToNum(result_list?.get(12)?.thstrm_add_amount.toString())
                    val cur_amout = stringToNum(result_list?.get(12)?.thstrm_amount.toString())
                    // 숫자
                    val accu_cur_amount = accu_amount + cur_amout
                    dangi = accu_cur_amount
                    tot_asset = tot_capiital
                    Log.e("총자본과 당기순이익", "$tot_capiital and $accu_cur_amount , ${tot_capiital.javaClass.name} and ${accu_cur_amount.javaClass.name}")
                    Log.e("총자본과 당기순이익2", "$tot_asset and $dangi")
                }

                override fun onFailure(call: Call<FinanceInfo>, t: Throwable) {
                    Log.e("onFailure in Finance Info", t.message!!)
                    Toast.makeText(this@MainActivity, "재무 정보를 가져오는 중 에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun stringToNum (s: String): Long {
        val num = s.replace(",", "").toLong()
        return num
    }

    private fun getDistributionStockInfo(retrofit: Retrofit, code: String){
        val distributionStockService: DistributionStockService? = retrofit.create(DistributionStockService::class.java)
        val dart_key = "e94c917f133dd9f8d1e5d62552bf010eba18801c"

        distributionStockService?.getInfo(dart_key, code, "2022", "11012")
            ?.enqueue(object : Callback<DistributionStockInfo>{
                override fun onResponse(call: Call<DistributionStockInfo>, response: Response<DistributionStockInfo>) {
                    Log.e("유통주식 응답 상태", response.body()?.status.toString())
                    Log.e("유통주식 응답 메시지", response.body()?.message.toString())

                    val result_list = response.body()?.list
                    // 유통주식 수, 문자
                    val cnt = result_list?.get(2)?.distb_stock_co
                    stock_cnt = stringToNum(cnt!!)
                    Log.e("유통주식 ", "$cnt , ${cnt.javaClass.name}")
                    Log.e("유통주식2 ", "$stock_cnt")
                }

                override fun onFailure(call: Call<DistributionStockInfo>, t: Throwable) {
                    Log.e("onFailure in Distribution Stock Info", t.message!!)
                    Toast.makeText(this@MainActivity, "유통주식 정보를 가져오는 중 에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun getPriceInfo (retrofit: Retrofit, name: String){
        val priceService: PriceService? = retrofit.create(PriceService::class.java)
        val krx_key = "B508AA1C5D9545478DABB1870B2E49A6A3FA28AD"
        priceService?.getInfo(krx_key, "20230105")
            ?.enqueue(object : Callback<PriceInfo>{
                override fun onResponse(call: Call<PriceInfo>, response: Response<PriceInfo>) {
                    // ArrayList
                    val result_list = response.body()?.OutBlock_1
                    for (i in result_list!!) {
                        if (i.ISU_NM == name) {
                            val price = i.TDD_CLSPRC
                            stock_price = price.toLong()
                            Log.e("Get PRICE", "$price, ${price.javaClass.name}")
                            Log.e("Get PRICE2", "${stock_price}")
                            break
                        }
                    }
                }

                override fun onFailure(call: Call<PriceInfo>, t: Throwable) {
                    Log.e("onFailure in Stock Price Info", t.message!!)
                    Toast.makeText(this@MainActivity, "주식가 정보를 가져오는 중 에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            })
    }
    override fun onDestroy() {
        // onDestroy()에서 binding class 인스턴스 참조를 모두 정리
        mBinding = null
        super.onDestroy()
    }
}
// 재무정보
interface FinanceService {
    @GET("/api/fnlttSinglAcnt.json")
    fun getInfo(
        @Query("crtfc_key") crtfc_key: String,
        @Query("corp_code") corp_code: String,
        @Query("bsns_year") bsns_year: String,
        @Query("reprt_code") reprt_code: String
    ):Call<FinanceInfo>
}
// 유통주식 수
interface DistributionStockService{
    @GET("/api/stockTotqySttus.json")
    fun getInfo(
        @Query("crtfc_key") crtfc_key: String,
        @Query("corp_code") corp_code: String,
        @Query("bsns_year") bsns_year: String,
        @Query("reprt_code") reprt_code: String
    ):Call<DistributionStockInfo>
}
// 주식가격
interface PriceService{
    @GET("/svc/apis/sto/stk_bydd_trd")
    fun getInfo(
        @Header("AUTH_KEY") AUTH_KEY: String,
        @Query("basDd") basDd: String
    ):Call<PriceInfo>
}
