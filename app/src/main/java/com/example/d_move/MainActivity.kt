package com.example.d_move

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.hardware.SensorManager
import android.util.Log
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener{
    private lateinit var sensorManager: SensorManager;
    private var safehack: Boolean = false
    private lateinit var textView: TextView
    private var mAccelCurrent = SensorManager.GRAVITY_EARTH;
    private var mAccelLast = SensorManager.GRAVITY_EARTH;
    private var threasthold: Float = 5.0f
    private lateinit var textInput: TextInputEditText
    private lateinit var imageView: ImageView
    private var previous_delta = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("create", "new asset")
        setContentView(R.layout.activity_main)
        imageView = findViewById<ImageView>(R.id.working_view)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        textView = findViewById(R.id.debug_rotation)
        textInput = findViewById(R.id.inputted_tasks)

        val oopsImage: Bitmap? = "sozai/working.png".getBitmapFromAsset()
        if (oopsImage != null) {
            imageView.setImageBitmap(oopsImage)
        }
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

    protected override fun onResume() {
        super.onResume()
        var accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
   }

    protected override fun onPause() {
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

                // calculates diffs
                val delta = (mAccelCurrent - mAccelLast).absoluteValue
                Log.d("delta", "%05f".format(delta))
                if (delta > threasthold) {
                    if (previous_delta != 0) {
                        safehack = true
                        intent = Intent(application, OOPsActivity::class.java)
                        startActivity(intent)
                        previous_delta = 0
                    } else {
                        previous_delta += 1
                    }
                }
            }
        }
    }
}
