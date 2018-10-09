package app.android.com.crashhandlerapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IApiService {
    @POST("event/trackevent")
    fun postCrashData(@Body crashData:HashMap<String,String>): Call<Void>
}