package com.example.speedometer

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val speedometer = findViewById<SpeedometerView>(R.id.speedometer)
        val increaseButton = findViewById<Button>(R.id.increaseButton)
        increaseButton.setOnClickListener {
            speedometer.increaseSpeed()
        }

        increaseButton.setOnLongClickListener {
            speedometer.on()
            true
        }


        val decreaseButton = findViewById<Button>(R.id.decreaseButton)
        decreaseButton.setOnClickListener {
            speedometer.decreaseSpeed()
        }

        decreaseButton.setOnLongClickListener {
            speedometer.off()
            true
        }

    }
}