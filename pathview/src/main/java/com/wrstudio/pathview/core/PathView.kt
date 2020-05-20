package com.wrstudio.pathview.core

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewDebug
import com.wrstudio.pathview.core.R
import com.wrstudio.pathview.util.UiUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    /**
     * The original data provide from {@link PathView#update}
     */
    private var rawData: List<Pair<Double, Double>> = listOf()

    /**
     * The located position for canvas drawing
     */
    private var canvasLeft: Int = 0
    private var canvasTop: Int = 0
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0

    /**
     * The styleable attributes, color of the path
     */
    private var pathColor: Int = Color.BLACK

    /**
     * The styleable attributes, stroke width
     */
    private var strokeWidth: Float = UiUtil.dpToPx(5).toFloat()

    /**
     * The path to draw
     */
    private var path: Path? = null

    private val pathPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = pathColor
            strokeWidth = UiUtil.dpToPx(3).toFloat()
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PathView,
            0, 0
        ).apply {

            try {
                pathColor = getColor(R.styleable.PathView_pathColor, Color.BLACK)
                pathPaint.color = pathColor
                strokeWidth =
                    getDimension(R.styleable.PathView_strokSize, UiUtil.dpToPx(5).toFloat())
                pathPaint.strokeWidth = strokeWidth
            } finally {
                recycle()
            }
        }
    }

    fun update(pairs: List<Pair<Double, Double>>) {
        path = Path()
        rawData = pairs
        if (canvasWidth != 0 && canvasHeight != 0) {
            GlobalScope.launch {
                handleData()
                invalidate()
            }
        }
    }

    private suspend fun handleData() {
        if (rawData.isEmpty()) return

        withContext(Dispatchers.IO) {
            var maxX = rawData[0].first
            var minX = rawData[0].first
            var maxY = rawData[0].second
            var minY = rawData[0].second
            var biasX = 0
            var biasY = 0

            for (pair in rawData) {
                if (pair.first > maxX) maxX = pair.first
                if (pair.first < minX) minX = pair.first
                if (pair.second > maxY) maxY = pair.second
                if (pair.second < minY) minY = pair.second
            }

            if (maxX - minX > maxY - minY) {
                biasX = 0
                biasY = (((minX - minX) - (maxY - minY)) / 2f).toInt()
            } else {
                biasY = 0
                biasX = (((minY - minY) - (maxX - minX)) / 2f).toInt()
            }

            if (maxX > minX && maxY > minY) {
                val scale: Double = if (maxX - minX > maxY - minY) {
                    (canvasWidth.toDouble() / (maxX - minX))
                } else {
                    (canvasHeight.toDouble() / (maxY - minY))
                }
                path = Path()

                for (i in rawData.indices) {
                    val x = ((rawData[i].first - minX) * scale + canvasLeft).toFloat() + biasX
                    val y =
                        ((canvasHeight - (rawData[i].second - minY) * scale) + canvasTop).toFloat() +  biasY
                    if (i == 0) {
                        path!!.moveTo(x, y)
                    } else {
                        path!!.lineTo(x, y)
                    }
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasLeft = paddingLeft
        canvasTop = paddingTop
        canvasWidth = w - paddingLeft - paddingRight
        canvasHeight = h - paddingTop - paddingBottom
        GlobalScope.launch {
            handleData()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (canvasWidth == 0 || canvasHeight == 0) return
        path?.let {
            canvas.drawPath(it, pathPaint)
        }

        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Try for a width based on our minimum
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = View.resolveSizeAndState(minw, widthMeasureSpec, 1)

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        val minh: Int = View.MeasureSpec.getSize(w) + paddingBottom + paddingTop
        val h: Int = View.resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )

        setMeasuredDimension(w, h)
    }

}