package org.d3if3106.weather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lat=intent.getStringExtra("lat")
        var long=intent.getStringExtra("long")
        createNotificationChannel()

        window.statusBarColor= Color.parseColor("#1383C3")
        getJsonData(lat,long)
    }



    private fun getJsonData(lat:String?,long:String?)
    {
        val API_KEY="d782c3641dcc9cbcb8528dfb0245b3e4"
        val queue = Volley.newRequestQueue(this)
        val url ="https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&appid=${API_KEY}"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { response ->
                setValues(response)
            },
            Response.ErrorListener { Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show() })


        queue.add(jsonRequest)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "prodman123"
            val channelName = "Perubahan Cuaca"
            val channelDescription = "Weather Application"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setValues(response:JSONObject){
        city.text=response.getString("name")
        var lat = response.getJSONObject("coord").getString("lat")
        var long=response.getJSONObject("coord").getString("lon")
        coordinates.text="${lat} , ${long}"
        weather.text=response.getJSONArray("weather").getJSONObject(0).getString("main")
        var tempr=response.getJSONObject("main").getString("temp")
        tempr=((((tempr).toFloat()-273.15)).toInt()).toString()
        temp.text="${tempr}°C"


        var mintemp=response.getJSONObject("main").getString("temp_min")
        mintemp=((((mintemp).toFloat()-273.15)).toInt()).toString()
        min_temp.text=mintemp+"°C"
        var maxtemp=response.getJSONObject("main").getString("temp_max")
        maxtemp=((ceil((maxtemp).toFloat()-273.15)).toInt()).toString()
        max_temp.text=maxtemp+"°C"

        pressure.text=response.getJSONObject("main").getString("pressure")
        humidity.text=response.getJSONObject("main").getString("humidity")+"%"
        wind.text=response.getJSONObject("wind").getString("speed")
        degree.text="Degree : "+response.getJSONObject("wind").getString("deg")+"°"



        val notificationId = 123
        val channelId = "prodman123"
        val channelName = "Perubahan Cuaca"
        val notificationText = "Temperatur hari ini ${tempr} °C"

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(channelName)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.baseline_cloud_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = this.getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
//        gust.text="Gust : "+response.getJSONObject("wind").getString("gust")
    }
}

