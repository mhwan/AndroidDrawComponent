package com.example.drawcomponent.drawer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import com.example.drawcomponent.R
import com.example.drawcomponent.util.ListStack
import java.util.*
import kotlin.math.min

class DrawView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {
    @ColorInt
    var brushColor = ContextCompat.getColor(context, android.R.color.black)
    var brushSize = 0.0f
    var drawMode = DrawMode.DRAW

    @ColorInt
    var drawBackgroundColor = ContextCompat.getColor(context, android.R.color.white)
        set(value) {
            field = value
            setBackgroundColor(field)
        }
    private var touchStatus = TouchStatus.NON_TOUCH
    private val pointerPaint = PointerPaintFactory.createPointer()
    private val pointerPath = Path()
    private val drawStates: ListStack<DrawState> = ListStack()
    var backgroundImage: Bitmap? = null
        private set

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        applyAttribute(attributeSet)
    }

    private fun applyAttribute(attributeSet: AttributeSet?) {
        attributeSet?.let {
            context.theme.obtainStyledAttributes(
                it,
                R.styleable.DrawView,
                defStyleAttr,
                0
            ).use { typedArray ->
                drawBackgroundColor = typedArray.getColor(
                    R.styleable.DrawView_drawBackgroundColor,
                    ContextCompat.getColor(context, android.R.color.white)
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawBackgroundImage(it)
            drawAllPath(it)
            drawPointer(it)
        }
    }

    private fun drawBackgroundImage(canvas: Canvas) {
        backgroundImage?.let {
            val src = Rect(0, 0, it.width, it.height)
            val dest = Rect(0, 0, width, height)
            canvas.drawBitmap(it, src, dest, null)
        }
    }

    private fun drawAllPath(canvas: Canvas) {
        drawStates.list.forEach {
            if (it is DrawState.Show) {
                val pathPaint = it.pathPaint
                canvas.drawPath(pathPaint.path, pathPaint.paint)
            }
        }
    }

    private fun drawPointer(canvas: Canvas) {
        if (touchStatus == TouchStatus.TOUCH) {
            canvas.drawPath(pointerPath, pointerPaint)
        }
    }

    private fun resetPointer() {
        pointerPath.reset()
    }

    private fun refreshPointer(x: Float, y: Float) {
        resetPointer()
        pointerPath.addCircle(x, y, brushSize - 4, Path.Direction.CW)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val newX = it.x
            val newY = it.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStatus = TouchStatus.TOUCH
                    if (drawMode == DrawMode.DRAW || drawMode == DrawMode.ERASE) startDraw(
                        newX,
                        newY
                    )
                }

                MotionEvent.ACTION_MOVE -> {
                    touchStatus = TouchStatus.TOUCH
                    when (drawMode) {
                        DrawMode.DRAW, DrawMode.ERASE -> updateDraw(newX, newY)
                        DrawMode.AUTO_ERASE -> autoEraseDraw(newX, newY)
                    }
                    refreshPointer(newX, newY)
                }

                MotionEvent.ACTION_UP -> {
                    touchStatus = TouchStatus.NON_TOUCH
                    resetPointer()
                }
            }

            invalidate()
        }
        return true
    }


    private fun autoEraseDraw(x: Float, y: Float) {
        if (drawMode == DrawMode.AUTO_ERASE) {
            val comparePath = makeComparePath(x, y)

            var resultIdx = -1
            var resultPath: PathPaint? = null

            drawStates.list.asReversed().forEachIndexed { index, latestDraw ->
                if (latestDraw is DrawState.Show) {
                    val compareResult = Path()
                    val pathPaint = latestDraw.pathPaint

                    if (compareResult.op(comparePath, pathPaint.path, Path.Op.INTERSECT)) {
                        if (!compareResult.isEmpty) {
                            if (latestDraw.mode === DrawMode.ERASE) return
                            else if (latestDraw.mode === DrawMode.DRAW) {
                                resultIdx = drawStates.size - index - 1
                                resultPath = pathPaint
                                return@forEachIndexed
                            }
                        }
                    }

                }
            }

            if (resultIdx >= 0) {
                drawStates.removeAt(resultIdx)
                resultPath?.let {
                    drawStates.addLast(DrawState.Hide(resultIdx, it))
                }
            }
        }
    }

    private fun makeComparePath(x: Float, y: Float) = Path().apply {
        val pointerSize = brushSize / 2
        val compareRectF = RectF(
            x - pointerSize,
            y - pointerSize,
            x + pointerSize,
            y + pointerSize
        )

        moveTo(x, y)
        addRect(compareRectF, Path.Direction.CW)
    }

    private fun makeNewPathPaint() {
        val color = if (drawMode == DrawMode.DRAW) brushColor else drawBackgroundColor
        drawStates.addLast(
            DrawState.Show(drawMode, PathPaintFactory.createPathPaint(color, brushSize))
        )
    }

    private fun setEraseTransparentPaint() {
        if (drawStates.latestValue is DrawState.Show) {
            val paint = (drawStates.latestValue as DrawState.Show).pathPaint.paint
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }
    private fun startDraw(x: Float, y: Float) {
        if (drawMode == DrawMode.DRAW || drawMode == DrawMode.ERASE) {
            makeNewPathPaint()

            val latest = drawStates.latestValue
            if (latest != null && latest is DrawState.Show) {
                val pathPaint = latest.pathPaint
                pathPaint.path.moveTo(x, y)
            }
        }
    }

    private fun updateDraw(x: Float, y: Float) {
        if (drawMode == DrawMode.DRAW || drawMode == DrawMode.ERASE) {
            val latest = drawStates.latestValue

            if (latest != null && latest is DrawState.Show) {
                val pathPaint = latest.pathPaint
                pathPaint.path.lineTo(x, y)
            }
        }
    }

    fun undo(): Boolean {
        if (drawStates.isNotEmptyList) {
            val latest = drawStates.latestValue
            latest?.let {
                when (it) {
                    is DrawState.Hide -> {
                        val hide = drawStates.removeLast() as DrawState.Hide
                        val idx = min(hide.orgIdx, drawStates.size - 1)

                        drawStates.addAt(idx, DrawState.Show(DrawMode.DRAW, hide.pathPaint))
                        drawStates.push(DrawState.Hide(idx, hide.pathPaint))
                    }
                    is DrawState.Show -> {
                        drawStates.push(drawStates.removeLast())
                    }
                }
            }
            invalidate()
            return true
        }
        return false
    }

    fun redo(): Boolean {
        if (drawStates.isNotEmptyStack) {
            when (val restoreLatestDraw = drawStates.pop()) {
                is DrawState.Hide -> {
                    drawStates.removeAt(restoreLatestDraw.orgIdx)
                    drawStates.addLast(
                        DrawState.Hide(
                            restoreLatestDraw.orgIdx,
                            restoreLatestDraw.pathPaint
                        )
                    )
                }
                is DrawState.Show -> drawStates.addLast(restoreLatestDraw)
            }
            invalidate()
            return true
        }

        return false
    }

    fun convertToBitmap(): Bitmap? =
        if (drawStates.isNotEmptyList) {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            Canvas(bitmap).apply {
                if (backgroundImage != null) {
                    drawBackgroundImage(this)
                } else {
                    drawColor(drawBackgroundColor)
                }
                drawStates.list.forEach {
                    if (it is DrawState.Show) {
                        val pathPaint = it.pathPaint
                        drawPath(pathPaint.path, pathPaint.paint)
                    }
                }
            }
            bitmap
        } else null

    fun setBackgroundImage(bitmap: Bitmap) {
        backgroundImage = bitmap
        invalidate()
    }

    fun clearAll() {
        drawStates.clear()
        invalidate()
    }
}