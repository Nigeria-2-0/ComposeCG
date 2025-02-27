package com.dolearnn.cg.os.algorithms.sierpinskygasket

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import com.dolearnn.cg.os.models.Point
import com.dolearnn.cg.os.models.Triangle
import com.dolearnn.cg.os.algorithms.AbstractState
import com.dolearnn.cg.os.utils.LerpUtil
import kotlin.math.sqrt

class SierpinskyGasketState : AbstractState() {

    private var triangleSide = -1f

    private var _plottedTriangles = mutableStateListOf<Triangle>()
    val plottedPoints: List<Triangle>
        get() = _plottedTriangles

    private lateinit var initialTriangle: Triangle

    override fun init(width: Float, height: Float, totalFrames: Float) {
        super.init(width, height, totalFrames)

        triangleSide = (if (viewWidth < viewHeight) viewWidth else viewHeight).times(0.9f)
        iterationAmount = 1f / 6f
        iterationCount = iterationAmount

        val triangleHeight = triangleSide * (sqrt(3.0) / 2).toFloat()
        val triangleHeightHalf = triangleHeight / 2f
        val triangleSideHalf = triangleSide / 2f

        _plottedTriangles.add(
            Triangle(
                Point(0f, -triangleHeightHalf),
                Point(-triangleSideHalf, triangleHeightHalf),
                Point(triangleSideHalf, triangleHeightHalf)
            ).also {
                initialTriangle = it
            }
        )
    }

    override fun onClickPlayPause() {
        super.onClickPlayPause()
        if (_isPlaying.value == true && _plottedTriangles.size > 1) {
            iterationCount = iterationAmount
            _plottedTriangles.clear()
            _plottedTriangles.add(initialTriangle)
        }
    }

    override fun onTouch(touchOffset: Offset): Int = -1

    override fun onDragStart(offset: Offset) {
    }

    override fun onInterpolatedValueChange(t: Float) {
        if (_isPlaying.value != true) return

        if (t >= iterationCount) {
            iterationCount += iterationAmount

            val nextTriangles = mutableListOf<Triangle>()

            _plottedTriangles.forEach { triangle ->
                val t1a = triangle.a
                val t1b = LerpUtil.lerp(triangle.a, triangle.b, 0.5f)
                val t1c = LerpUtil.lerp(triangle.a, triangle.c, 0.5f)

                val t2b = triangle.b
                val t2c = LerpUtil.lerp(triangle.b, triangle.c, 0.5f)

                val t3c = triangle.c

                nextTriangles.addAll(
                    listOf(
                        Triangle(t1a, t1b, t1c),
                        Triangle(t1b, t2b, t2c),
                        Triangle(t1c, t2c, t3c),
                    )
                )
            }

            _plottedTriangles.clear()
            _plottedTriangles.addAll(nextTriangles)
        }

        super.onInterpolatedValueChange(t)
    }
}