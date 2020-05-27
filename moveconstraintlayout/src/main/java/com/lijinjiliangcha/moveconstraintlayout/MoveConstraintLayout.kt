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

    private val moveViewList = ArrayList<View>()
    //当前移动的View
    private var moveView: View? = null

    //点下或移动时前一次坐标
    private var dx = 0f
    private var dy = 0f
    //点下时的时间
//    private var clickTime = 0L
    //是否正在移动view，true-正在移动
    private var isMove = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

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
//                clickTime = System.currentTimeMillis()
                moveView = getMoveView(dx, dy)
//                if (moveView != null)
//                    return true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.i("测试", "ACTION_MOVE")
                if (moveView != null) {
                    val mx = event.x
                    val my = event.y
                    //判断是否已开始移动
                    if (isMove) {
                        val deltaX = mx - dx
                        val deltaY = my - dy
                        val l = (moveView?.left ?: 0) + deltaX.toInt()
                        val t = (moveView?.top ?: 0) + deltaY.toInt()
                        moveView?.layout(l, t, l + moveView!!.width, t + moveView!!.height)
                        dx = mx
                        dy = my
                        return true
                    } else if (Math.abs(my - dy) > moveDis || Math.abs(mx - dx) > moveDis) {//位移大于25px，认为开始移动
                        isMove = true
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                moveView = null
                if (isMove) {
                    Log.i("测试", "isMove = $isMove")
                    isMove = false
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun getMoveView(x: Float, y: Float): View? {
        moveViewList.forEach {
            val rect = Rect(it.left, it.top, it.right, it.bottom)
            if (rect.contains(x.toInt(), y.toInt()))
                return it
        }
        return null
    }

    class LayoutParams : ConstraintLayout.LayoutParams {

        var move: Boolean = false

        constructor(source: ConstraintLayout.LayoutParams?) : super(source)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs) {
            val attr = c?.obtainStyledAttributes(attrs, R.styleable.MoveConstraintLayout_Layout)
            move = attr?.getBoolean(R.styleable.MoveConstraintLayout_Layout_layout_constraint_move, false) ?: false
            Log.i("测试", "move = $move")
            attr?.recycle()
        }

    }

}