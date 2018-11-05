package com.like.view

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView

import com.like.R

import java.util.ArrayList
import java.util.Random

class LikeStar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    private var mStarDrawable: MutableList<Drawable>? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    //定义贝塞尔曲线的数据点和两个控制点
    private var startP: PointF? = null
    private var endP: PointF? = null
    private var pointX: PointF? = null
    private var pointY: PointF? = null
    private val random = Random()

    init {
        init(context)
    }

    private fun init(context: Context) {
        mStarDrawable = ArrayList()
        startP = PointF()
        endP = PointF()
        pointX = PointF()
        pointY = PointF()
        //初始化图片资源
        mStarDrawable!!.add(resources.getDrawable(R.drawable.like_red, null))
        mStarDrawable!!.add(resources.getDrawable(R.drawable.like_blue, null))
        mStarDrawable!!.add(resources.getDrawable(R.drawable.like_green, null))
        mStarDrawable!!.add(resources.getDrawable(R.drawable.like_repple, null))
        //初始化插补器
        val interpolators = ArrayList<Interpolator>()
        interpolators.add(LinearInterpolator())
        interpolators.add(AccelerateDecelerateInterpolator())
        interpolators.add(AccelerateInterpolator())
        interpolators.add(DecelerateInterpolator())

        val image_heard = ImageView(context)
        image_heard.setImageDrawable(mStarDrawable!![0])

        image_heard.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        image_heard.setOnClickListener {
            //点击之后开始动画,添加红心到布局文件并开始动画
            val image_random = ImageView(context)
            image_random.setImageDrawable(mStarDrawable!![random.nextInt(4)])

            image_random.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            addView(image_random)
            invalidate()
            //开始做动画效果
            val endPointRandom = PointF(random.nextInt(mWidth).toFloat(), endP!!.y)
            val bezierTypeEvaluator = BezierTypeEvaluator(PointF(random.nextInt(mWidth).toFloat(), random.nextInt(mHeight).toFloat()), PointF(random.nextInt(mWidth).toFloat(), random.nextInt(mHeight).toFloat()))
            val valueAnimator = ValueAnimator.ofObject(bezierTypeEvaluator, startP, endPointRandom)
            valueAnimator.addUpdateListener { animation ->
                val pointF = animation.animatedValue as PointF
                image_random.x = pointF.x
                image_random.y = pointF.y
            }

            valueAnimator.duration = 2000
            valueAnimator.start()

        }
        addView(image_heard)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        mWidth = measuredWidth
        mHeight = measuredHeight
        // 初始化各个点
        //借用第一个子view控件中的宽高
        val child = getChildAt(0)
        val childW = child.measuredWidth
        val childH = child.measuredHeight

        startP!!.x = (mWidth - childW shr 1).toFloat()
        startP!!.y = (mHeight - childH).toFloat()
        endP!!.x = (mWidth - childW shr 1).toFloat()
        endP!!.y = (0 - childH).toFloat()

        pointX!!.x = random.nextInt(mWidth / 2).toFloat()
        pointX!!.y = (random.nextInt(mHeight / 2) + (mHeight shr 1)).toFloat()

        pointY!!.x = (random.nextInt(mWidth / 2) + (mWidth shr 1)).toFloat()
        pointY!!.y = random.nextInt(mHeight / 2).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        //获取view的宽高测量模式
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        //保存测量高度
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childW = child.measuredWidth
            val childH = child.measuredHeight
            child.layout((mWidth - childW) / 2, mHeight - childH, (mWidth - childW) / 2 + childW, mHeight)
        }
    }

    inner class BezierTypeEvaluator internal constructor(private val pf1: PointF, private val pf2: PointF) : TypeEvaluator<PointF> {

        override fun evaluate(fraction: Float, startValue: PointF, endValue: PointF): PointF {
            val pointCur = PointF()
            pointCur.x = startP!!.x * (1 - fraction) * (1 - fraction) * (1 - fraction) + (3f
                    * pf1.x * fraction * (1 - fraction) * (1 - fraction)) + (3f
                    * pf2.x * (1 - fraction) * fraction * fraction) + endValue.x * fraction * fraction * fraction// 实时计算最新的点X坐标
            pointCur.y = startP!!.y * (1 - fraction) * (1 - fraction) * (1 - fraction) + (3f
                    * pf1.y * fraction * (1 - fraction) * (1 - fraction)) + (3f
                    * pf2.y * (1 - fraction) * fraction * fraction) + endValue.y * fraction * fraction * fraction// 实时计算最新的点Y坐标
            return pointCur
        }
    }
}
