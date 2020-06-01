package com.example.d_move

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt


class OOPsActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var mAccelCurrent = SensorManager.GRAVITY_EARTH
    private var mAccelLast = SensorManager.GRAVITY_EARTH
    private var threasthold: Float = 5.0f
    private var back_info_text: String = ""
    private var created_at: Long = 0
    private lateinit var imageView: ImageView
    private lateinit var source_jorno: TextView
    private lateinit var back_info: TextView
    private lateinit var return_button: Button
    private val soruce_jorno_link: String = "https://dic.nicovideo.jp/a/%E3%83%AF%E3%82%B6%E3%83%83%E3%83%97%E3%82%B8%E3%83%A7%E3%83%AB%E3%83%8E"
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oops)
        return_button = findViewById(R.id.return_button)
        return_button.setOnClickListener{
            this.finish()
        }

        back_info_text = resources.getString(R.string.back_info)
        back_info = findViewById(R.id.back_info)
        source_jorno = findViewById(R.id.source_jorno)
        source_jorno.movementMethod = LinkMovementMethod.getInstance()
         source_jorno.setOnClickListener{
           val browserIntent = Intent(Intent.ACTION_VIEW)
           browserIntent.data = Uri.parse(soruce_jorno_link)
            startActivity(browserIntent)
        }
        imageView = findViewById<ImageView>(R.id.oops_view)
        val oopsImage: Bitmap? = "sozai/oops.png".getBitmapFromAsset()
        if (oopsImage != null) {
            imageView.setImageBitmap(oopsImage)
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        created_at = System.currentTimeMillis() / 1000

    }

    private fun String.getBitmapFromAsset(): Bitmap? {
        if (assets != null) {
            try {
                val realPath = assets.open(this)
                return BitmapFactory.decodeStream(realPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }
    override fun onResume() {
        super.onResume()
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val gravities: FloatArray
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                // calculates gravity vector size
                gravities = event.values.clone()
                var size = 0.0
                gravities.forEach {
                    size += it.toDouble().pow(2.0)
                }
                size = sqrt(size)

                // update previous accel
                mAccelLast = mAccelCurrent
                mAccelCurrent = size.toFloat()

                // show diffs
                val delta = (mAccelCurrent - mAccelLast).absoluteValue
                val updated_time =  System.currentTimeMillis() / 1000 - created_at
                if (delta <= threasthold && updated_time > 15.0) {
                    back_info.text = "%s [%s]".format(back_info_text, "OK")
                    this.return_button.visibility = View.VISIBLE
                    this.finish()
                    return
                } else {
                    this.return_button.visibility = View.INVISIBLE

                   back_info.text = "%s [%d]".format(back_info_text, 15 -  updated_time)
                }
                if (delta > threasthold) {
                    created_at = System.currentTimeMillis() / 1000
                }
            }
        }
    }
}
