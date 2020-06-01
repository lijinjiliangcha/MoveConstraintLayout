package com.lijinjiliangcha.moveconstraintlayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

class MoveConstraintLayout : ConstraintLayout {

    //判断移动误差范围
    private val moveDis = 5
    //可移动控件存储集合
    private val moveViewList = ArrayList<View>()
    //当前移动的View
    private var moveView: View? = null

    //可移动边距
    var movePaddingLeft = 0
    var movePaddingTop = 0
    var movePaddingRight = 0
    var movePaddingBottom = 0

    //点下或移动时前一次坐标
    private var dx = 0f
    private var dy = 0f
    //是否正在移动view，true-正在移动
    private var isMove = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.MoveConstraintLayout)
        if (attr.hasValue(R.styleable.MoveConstraintLayout_movePadding)) {
            val movePadding = attr.getDimensionPixelSize(R.styleable.MoveConstraintLayout_movePadding, 0)
            movePaddingLeft = movePadding
            movePaddingTop = movePadding
            movePaddingRight = movePadding
            movePaddingBottom = movePadding
        } else {
            movePaddingLeft = attr.getDimensionPixelSize(R.styleable.MoveConstraintLayout_movePaddingLeft, 0)
            movePaddingTop = attr.getDimensionPixelSize(R.styleable.MoveConstraintLayout_movePaddingTop, 0)
            movePaddingRight = attr.getDimensionPixelSize(R.styleable.MoveConstraintLayout_movePaddingRight, 0)
            movePaddingBottom = attr.getDimensionPixelSize(R.styleable.MoveConstraintLayout_movePaddingBottom, 0)
        }
        attr.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.i("测试", "onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        moveViewList.forEach {
            val layoutParams = it.layoutParams as LayoutParams
            val viewWidget = getViewWidget(it)
            Log.i("测试", "l = ${it.left}，t = ${it.top}")
            Log.i("测试", "viewWidget.x = ${viewWidget.x}，viewWidget.y = ${viewWidget.y}")
            val x: Int
            val y: Int
            if (layoutParams.skipMeasure) {
                x = it.left
                y = it.top
            } else {
                x = checkHorizontallyBorder(viewWidget.x, viewWidget.width)
                y = checkVerticallyBorder(viewWidget.y, viewWidget.height)

            }
            Log.i("测试", "x = $x，y = $y")
            viewWidget.setOrigin(x, y)
            viewWidget.updateDrawPosition()
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        if (params is LayoutParams) {
            if (params.move)
                moveViewList.add(child)
        }
        super.addView(child, index, params)
    }

    override fun removeAllViews() {
        moveViewList.clear()
        super.removeAllViews()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i("测试", "onTouchEvent")
        return moveView != null
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Log.i("测试", "dispatchTouchEvent")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dx = event.x
                dy = event.y
                moveView = getMoveView(dx, dy)
            }
            MotionEvent.ACTION_MOVE -> {
                if (moveView != null) {
                    val mx = event.x
                    val my = event.y
                    //判断是否已开始移动
                    if (isMove) {
                        val deltaX = mx - dx
                        val deltaY = my - dy
                        moveView?.let { moveViewBy(it, deltaX.toInt(), deltaY.toInt()) }
                        dx = mx
                        dy = my
                        return true
                    } else if (Math.abs(my - dy) > moveDis || Math.abs(mx - dx) > moveDis) {//位移大于25moveDis，认为开始移动
                        isMove = true
                        (moveView?.layoutParams as LayoutParams).skipMeasure = true
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                moveView = null
                if (isMove) {
                    isMove = false
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun moveViewBy(view: View, x: Int, y: Int) {
//        val params = view.layoutParams as LayoutParams
        val l = moveByHorizontally(view, x)
        val t = moveByVertically(view, y)
        view.layout(l, t, l + view.width, t + view.height)
        val viewWidget = getViewWidget(view)
        viewWidget.setOrigin(l, t)
        viewWidget.updateDrawPosition()
    }

    private fun moveByHorizontally(view: View, x: Int): Int {
        val params = view.layoutParams as LayoutParams
        var l = view.left + if (params.canMoveHorizontally) x else 0
        if (!params.beyondTheBorder) {
            l = checkHorizontallyBorder(l, view.height)
        }
        return l
    }

    private fun moveByVertically(view: View, y: Int): Int {
        val params = view.layoutParams as LayoutParams
        var t = view.top + if (params.canMoveVertically) y else 0
        if (!params.beyondTheBorder) {
            t = checkVerticallyBorder(t, view.width)
        }
        return t
    }

    //检测view是否超出边界
    private fun checkHorizontallyBorder(left: Int, viewWidth: Int): Int {
        Log.i("测试", "width = $width，measuredWidth = $measuredWidth")
        val w = if (width != 0) width else measuredWidth
        if (w != 0)
            if (left < movePaddingLeft)
                return movePaddingLeft
            else if (left > w - movePaddingRight - viewWidth)
                return w - movePaddingRight - viewWidth
        return left
    }

    //检测view是否超出边界
    private fun checkVerticallyBorder(top: Int, viewHeight: Int): Int {
        Log.i("测试", "height = ${height}，measuredHeight = $measuredHeight")
        val h = if (height != 0) height else measuredHeight
        if (h != 0)
            if (top < movePaddingTop)
                return movePaddingTop
            else if (top > h - movePaddingBottom - viewHeight)
                return h - movePaddingBottom - viewHeight
        return top
    }

    //获取可移动view
    private fun getMoveView(x: Float, y: Float): View? {
        moveViewList.forEach {
            val rect = Rect(it.left, it.top, it.right, it.bottom)
            if (rect.contains(x.toInt(), y.toInt()))
                return it
        }
        return null
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.i("测试", "onLayout")

//        moveViewList.forEach {
//            val viewWidget = getViewWidget(it)
////            viewWidget.drawX
//            Log.i("测试", "viewWidget.getX() = ${viewWidget.x}，viewWidget.getY() = ${viewWidget.y}")
//        }

        super.onLayout(changed, left, top, right, bottom)
        moveViewList.forEach {
            //            val params = it.layoutParams as LayoutParams
//            //不能超出屏幕边界时
//            if (!params.beyondTheBorder) {
//                val l = checkHorizontallyBorder(it, it.left)
//                val t = checkVerticallyBorder(it, it.top)
//                it.layout(l, t, l + it.width, t + it.height)
//            }
            Log.i("测试", "view.left = ${it.left}，view.top = ${it.top}")
        }

    }

    class LayoutParams : ConstraintLayout.LayoutParams {

        //是否可以移动
        var move: Boolean = false
        //能否垂直移动
        var canMoveVertically = true
        //能否水平移动
        var canMoveHorizontally = true
        //能否超出布局边界
        var beyondTheBorder = false
        //是否跳过测量定位
        var skipMeasure = false


        constructor(source: ConstraintLayout.LayoutParams?) : super(source)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams?) : super(source) {
            if (source is LayoutParams) {
                move = source.move
                canMoveVertically = source.canMoveVertically
                canMoveHorizontally = source.canMoveHorizontally
            }
        }

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs) {
            val attr = c?.obtainStyledAttributes(attrs, R.styleable.MoveConstraintLayout_Layout)
            move = attr?.getBoolean(R.styleable.MoveConstraintLayout_Layout_layout_constraint_move, false) ?: false
            canMoveVertically = attr?.getBoolean(R.styleable.MoveConstraintLayout_Layout_layout_canMoveVertically, true) ?: true
            canMoveHorizontally = attr?.getBoolean(R.styleable.MoveConstraintLayout_Layout_layout_canMoveHorizontally, true) ?: true
            beyondTheBorder = attr?.getBoolean(R.styleable.MoveConstraintLayout_Layout_layout_beyond_the_border, false) ?: false
            attr?.recycle()

        }

    }

}