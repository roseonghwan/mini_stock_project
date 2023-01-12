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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    // 전역변수로 바인딩 객체 선언
    private var mBinding: ActivityMainBinding? = null;
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재선언
    private  val binding get() = mBinding!!
    var date = LocalDate.now()
    var str_date = date.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt() - 1
    var modelList = ArrayList<MyModel>()
    var dangi:Long = 0
    var tot_asset:Long = 0
    var stock_cnt:Long = 0
    var stock_price: Long = 0
    var tot_jasan: Long = 0

    var dangi2:Long = 0
    var tot_asset2:Long = 0
    var stock_cnt2:Long = 0
    var stock_price2: Long = 0
    var tot_jasan2: Long = 0

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
            modelList = ArrayList()
            val corp_txt:String = binding.searchEt.text.toString()
            val corp_txt2: String = binding.search2Et.text.toString()
            // [corp_txt, corp_txt2]
            val corp_name_list = arrayOf(corp_txt, corp_txt2)

            Log.e("click!!", listStore.length().toString() + corp_txt)
            Log.e("click2!!", listStore.length().toString() + corp_txt2)
            for (i in 0..1){
                for (j in 0 until listStore.length()){
                    val iObj = listStore.getJSONObject(j)
                    val iName = iObj.get("corp_name").toString()
                    val iCode = iObj.get("corp_code").toString()
                    if(corp_name_list[i] == iName) {
                        Log.e("CODE", iCode)
                        getFinanceInfo(dartRetrofit, iCode, i+1)
                        getDistributionStockInfo(dartRetrofit, iCode, i+1)
                        getPriceInfo(krxRetrofit, iName, i+1)
                        break
                    }
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                delay(2500)
                Log.e("모든 결과 실행 후...","유통주식수: $stock_cnt, 총자본: $tot_asset, 당기순이익: $dangi, 주식가격: $stock_price")
                Log.e("모든 결과 실행 후2...","유통주식수: $stock_cnt2, 총자본: $tot_asset2, 당기순이익: $dangi2, 주식가격: $stock_price2")
                // EPS 주당 순이익, PER 주가수익 비율, BPS 주당 순가치, PBR 주가 순자산 비율, ROE 자기자본이익률
                val EPS: Double = round((dangi.toDouble() / stock_cnt.toDouble()) * 100) / 100
                val PER: Double = round((stock_price.toDouble() / EPS) * 100) / 100
                val BPS: Double = round((tot_asset.toDouble() / stock_cnt.toDouble()) * 100) / 100
                val PBR: Double = round((stock_price.toDouble() / BPS) * 100) / 100
                val ROE: Double = round((dangi.toDouble() * 100 / tot_asset.toDouble()) * 100) / 100
                val ROA: Double = round((dangi.toDouble() * 100 / tot_jasan.toDouble()) * 100) / 100

                val EPS2: Double = round((dangi2.toDouble() / stock_cnt2.toDouble()) * 100) / 100
                val PER2: Double = round((stock_price2.toDouble() / EPS2) * 100) / 100
                val BPS2: Double = round((tot_asset2.toDouble() / stock_cnt2.toDouble()) * 100) / 100
                val PBR2: Double = round((stock_price2.toDouble() / BPS2) * 100) / 100
                val ROE2: Double = round((dangi2.toDouble() * 100 / tot_asset2.toDouble()) * 100) / 100
                val ROA2: Double = round((dangi2.toDouble() * 100 / tot_jasan2.toDouble()) * 100) / 100

                modelList.add(MyModel("현재주가", "$stock_price", "$stock_price2"))
               modelList.add(MyModel("EPS", "$EPS", "$EPS2"))
                modelList.add(MyModel("PER", "$PER", "$PER2"))
                modelList.add(MyModel("BPS", "$BPS", "$BPS2"))
                modelList.add(MyModel("PBR", "$PBR", "$PBR2"))
                modelList.add(MyModel("ROE", "$ROE", "$ROE2"))
                modelList.add(MyModel("ROA", "$ROA", "$ROA2"))
                modelList.add(MyModel("목표주가", "${round(PER * EPS *100) / 100}", "${round(PER2 * EPS2 * 100) / 100}"))
                makeRecyclerView()
            }
        }
    }
    // 리사이클러뷰 표시
    private fun makeRecyclerView(){
        val adapter = MyRecyclerViewAdapter(this, stock_price, stock_price2)
        adapter.dataList = modelList
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getFinanceInfo(retrofit: Retrofit, code: String, ord: Int) {
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
                    // 총 자산
                    val tot_san = stringToNum(result_list?.get(2)?.thstrm_amount.toString())
                    // 당기순이익
                    val accu_amount = stringToNum(result_list?.get(12)?.thstrm_add_amount.toString())
                    val cur_amout = stringToNum(result_list?.get(12)?.thstrm_amount.toString())
                    // 숫자
                    val accu_cur_amount = accu_amount + cur_amout
                    if(ord == 1){
                        dangi = accu_cur_amount
                        tot_asset = tot_capiital
                        tot_jasan = tot_san
                    }
                    else{
                        dangi2 = accu_cur_amount
                        tot_asset2 = tot_capiital
                        tot_jasan2 = tot_san
                    }
                    Log.e("총자본과 당기순이익", "$tot_capiital and $accu_cur_amount , ${tot_capiital.javaClass.name} and ${accu_cur_amount.javaClass.name}")
                    Log.e("총자본과 당기순이익1", "$tot_asset and $dangi")
                    Log.e("총자본과 당기순이익2", "$tot_asset2 and $dangi2")
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

    private fun getDistributionStockInfo(retrofit: Retrofit, code: String, ord: Int){
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
                    if (ord == 1)
                        stock_cnt = stringToNum(cnt!!)
                    else
                        stock_cnt2 = stringToNum(cnt!!)
                    Log.e("유통주식 ", "$cnt , ${cnt.javaClass.name}")
                    Log.e("유통주식2 ", "$stock_cnt")
                    Log.e("유통주식2 ", "$stock_cnt2")
                }

                override fun onFailure(call: Call<DistributionStockInfo>, t: Throwable) {
                    Log.e("onFailure in Distribution Stock Info", t.message!!)
                    Toast.makeText(this@MainActivity, "유통주식 정보를 가져오는 중 에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun getPriceInfo (retrofit: Retrofit, name: String, ord: Int){
        val priceService: PriceService? = retrofit.create(PriceService::class.java)
        val krx_key = "B508AA1C5D9545478DABB1870B2E49A6A3FA28AD"
        priceService?.getInfo(krx_key, str_date.toString())
            ?.enqueue(object : Callback<PriceInfo>{
                override fun onResponse(call: Call<PriceInfo>, response: Response<PriceInfo>) {
                    Log.e("Date", str_date.toString())
                    // ArrayList
                    val result_list = response.body()?.OutBlock_1
                    for (i in result_list!!) {
                        if (i.ISU_NM == name) {
                            val price = i.TDD_CLSPRC
                            if (ord == 1)
                                stock_price = price.toLong()
                            else
                                stock_price2 = price.toLong()

                            Log.e("Get PRICE", "$price, ${price.javaClass.name}")
                            Log.e("Get PRICE2", "${stock_price}")
                            Log.e("Get PRICE2", "${stock_price2}")
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