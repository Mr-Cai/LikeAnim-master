package com.like

import android.animation.TypeEvaluator
import android.animation.ValueAnimator.ofObject
import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View.MeasureSpec.getSize
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.*
import android.widget.ImageView
import java.lang.Math.pow
import java.util.*

class LikeStar @JvmOverloads constructor(context: Context, attrs: AttributeSet, attr: Int = 0) :
        ViewGroup(context, attrs, attr) {
    private lateinit var starDrawable: MutableList<Drawable>
    private var widthM: Int = 0
    private var heightM: Int = 0
    //定义贝塞尔曲线的数据点和两个控制点
    private lateinit var startP: PointF
    private lateinit var endP: PointF
    private lateinit var pointX: PointF
    private lateinit var pointY: PointF
    private val random = Random()

    init {
        init(context)
    }

    private fun init(context: Context) {
        starDrawable = ArrayList() //初始化图片资源
        startP = PointF()
        endP = PointF()
        pointX = PointF()
        pointY = PointF()
        starDrawable.add(resources.getDrawable(R.drawable.like_red, null))
        starDrawable.add(resources.getDrawable(R.drawable.like_blue, null))
        starDrawable.add(resources.getDrawable(R.drawable.like_green, null))
        starDrawable.add(resources.getDrawable(R.drawable.like_repple, null))
        starDrawable.add(resources.getDrawable(R.drawable.like_orange, null))
        starDrawable.add(resources.getDrawable(R.drawable.like_yellow, null))
        val interpolators = ArrayList<Interpolator>() //初始化插补器
        interpolators.add(LinearInterpolator())
        interpolators.add(AccelerateDecelerateInterpolator())
        interpolators.add(AccelerateInterpolator())
        interpolators.add(DecelerateInterpolator())
        val defaultIcon = ImageView(context)
        defaultIcon.setImageDrawable(starDrawable[0])
        defaultIcon.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        defaultIcon.setOnClickListener {
            val iconRandom = ImageView(context) //点击之后开始动画,添加红心到布局文件并开始动画
            iconRandom.setImageDrawable(starDrawable[random.nextInt(6)])
            iconRandom.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            addView(iconRandom)
            invalidate()
            val bezier = Bezier(
                    PointF(random.nextInt(widthM).toFloat(), random.nextInt(heightM).toFloat()),
                    PointF(random.nextInt(widthM).toFloat(), random.nextInt(heightM).toFloat())
            ) //开启动画效果
            val anim = ofObject(bezier, startP, PointF(random.nextInt(widthM).toFloat(), endP.y))
            anim.addUpdateListener { animation ->
                val pointF = animation.animatedValue as PointF
                iconRandom.x = pointF.x
                iconRandom.y = pointF.y
            }
            anim.duration = 2000
            anim.start()
        }
        addView(defaultIcon)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)  //初始化各点,借用第一个子控件中的宽高
        widthM = measuredWidth
        heightM = measuredHeight
        startP.x = (widthM - getChildAt(0).measuredWidth shr 1).toFloat()
        startP.y = (heightM - getChildAt(0).measuredHeight).toFloat()
        endP.x = (widthM - getChildAt(0).measuredWidth shr 1).toFloat()
        endP.y = (0 - getChildAt(0).measuredHeight).toFloat()
        pointX.x = random.nextInt(widthM / 2).toFloat()
        pointX.y = (random.nextInt(heightM / 2) + (heightM shr 1)).toFloat()
        pointY.x = (random.nextInt(widthM / 2) + (widthM shr 1)).toFloat()
        pointY.y = random.nextInt(heightM / 2).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec) //获取控件宽高测量模式
        setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec)) //保存测量高度
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) getChildAt(i).layout((widthM - getChildAt(i)
                .measuredWidth) / 2, heightM - getChildAt(i).measuredHeight, (widthM - getChildAt(i)
                .measuredWidth) / 2 + getChildAt(i).measuredWidth, heightM)
    }

    inner class Bezier(private val pf1: PointF, private val pf2: PointF) : TypeEvaluator<PointF> {
        override fun evaluate(f: Float, start: PointF, endValue: PointF): PointF {
            val pointCur = PointF()
            pointCur.x = startP.x * pow((1.0 - f), 3.0).toFloat() +
                    (3F * pf1.x * f * (1 - f) * (1 - f)) +
                    (3F * pf2.x * (1 - f) * f * f) +
                    endValue.x * f * f * f //实时计算最新的点X坐标
            pointCur.y = startP.y * pow((1.0 - f), 3.0).toFloat() +
                    (3F * pf1.y * f * (1 - f) * (1 - f)) +
                    (3F * pf2.y * (1 - f) * f * f) +
                    endValue.y * f * f * f //实时计算最新的点Y坐标
            return pointCur
        }
    }
}
