package com.example.speedometer

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.cos
import kotlin.math.sin


class SpeedometerView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var state: String = "disabled"
    private var color: Int = Color.RED
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val filter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)

    private var arrowAngle: Float = 270f
        set(value) {
            field = value
            invalidate()
        }

    init {
        arrowAngle = 270f
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpeedometerView)
        state = typedArray.getString(R.styleable.SpeedometerView_state) ?: state
        color = typedArray.getColor(R.styleable.SpeedometerView_color, color)
        typedArray.recycle()

        if (state != "disabled") {
            val targetAngle = when (state) {
                "low" -> 300f
                "medium" -> 360f
                "high" -> 420f
                else -> 0f
            }
            animateArrow(targetAngle)
        }
    }


    fun increaseSpeed() {
        when (state) {
            "low" -> {
                state = "medium"
                animateArrow(360f)
            }

            "medium" -> {
                state = "high"
                animateArrow(420f)
            }

            "high" -> {
            }
        }
    }

    fun decreaseSpeed() {
        when (state) {
            "low" -> {
            }

            "medium" -> {
                state = "low"
                animateArrow(300f)
            }

            "high" -> {
                state = "medium"
                animateArrow(360f)
            }
        }
    }

    fun off() {
        if (state == "disabled") return
        if (state != "low") return
        state = "disabled"
        animateArrow(270f)
    }

    fun on() {
        if (state == "disabled") {
            state = "low"
            animateArrow(300f)
        }
    }

    private fun animateArrow(targetAngle: Float) {
        Log.d("SpeedometerView", "Animating arrow from $arrowAngle to $targetAngle")
        val animator = ObjectAnimator.ofFloat(this, "arrowAngle", arrowAngle, targetAngle)
        animator.duration = 1000 // Adjust duration as needed
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        var centerY = height.toFloat()
        val radius = width / 2f - paint.strokeWidth / 2

        paint.strokeWidth = 100f

        paint.style = Paint.Style.STROKE

        if (state == "disabled") {
            paint.colorFilter = filter
        } else {
            paint.colorFilter = null
        }

        // Draw the first arc (the leftmost third of the semi-circle)
        paint.color = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            180f,
            60f,
            false,
            paint
        )

        // Draw the second arc (the middle third of the semi-circle)
        paint.color = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color))
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            240f,
            60f,
            false,
            paint
        )

        // Draw the third arc (the rightmost third of the semi-circle)
        paint.color = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            300f,
            60f,
            false,
            paint
        )
        centerY -= 25

        if (state != "disabled") {
            // Draw the arrow
            val arrowHeight = 400f
            val arrowHalfBase = 20f
            paint.color = Color.BLACK
            paint.style = Paint.Style.FILL

            val endX =
                centerX + arrowHeight * cos(Math.toRadians(this.arrowAngle.toDouble() - 90)).toFloat()
            val endY =
                centerY + arrowHeight * sin(Math.toRadians(this.arrowAngle.toDouble() - 90)).toFloat()

            val leftX =
                centerX - arrowHalfBase * cos(Math.toRadians(this.arrowAngle.toDouble())).toFloat()
            val leftY =
                centerY - arrowHalfBase * sin(Math.toRadians(this.arrowAngle.toDouble())).toFloat()

            val rightX =
                centerX + arrowHalfBase * cos(Math.toRadians(this.arrowAngle.toDouble())).toFloat()
            val rightY =
                centerY + arrowHalfBase * sin(Math.toRadians(this.arrowAngle.toDouble())).toFloat()

            path.reset() // Reset the path before reusing it
            path.moveTo(endX, endY)
            path.lineTo(leftX, leftY)
            path.lineTo(rightX, rightY)
            path.close()

            canvas.drawPath(path, paint)
        }

        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, 20f, paint)
    }
}
