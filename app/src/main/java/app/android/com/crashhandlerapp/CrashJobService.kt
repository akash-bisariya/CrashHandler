package app.android.com.crashhandlerapp

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import kotlinx.coroutines.experimental.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CrashJobService : JobService() {
    override fun onStopJob(p0: JobParameters?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val retrofit:Retrofit = Retrofit.Builder()
                .baseUrl("Any_base_url")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val service = retrofit.create(IApiService::class.java)
        val crashData = HashMap<String,String>()

        val sharedPreferences = getSharedPreferences("CrashData", Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()

        crashData.put("env", "test")
        crashData.put("app","onephoto")
        crashData.put("eventtype","trackEvent")
        crashData.put("category","test")
        crashData.put("action","testevent")
        crashData.put("opt_label","testingwithmobileLive")
        crashData.put("checkwithpoc",sharedPreferences.getString("crashData",""))

        var dataPost=false;
        val callback:Call<Void> = service.postCrashData(crashData)
        callback.enqueue(object : Callback<Void>
        {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("App Crash Handler","Some error occurred while sending the report to server")
                dataPost=false
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                dataPost= true
                jobFinished(params,false)
                edit.putBoolean("isPostData", true)
                edit.apply()
                Log.d("App Crash Handler","Crash report has been sent to server")
            }

        })
        return dataPost
    }
}