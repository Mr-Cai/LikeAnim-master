package com.like.view;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.like.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LikeStar extends ViewGroup {
    private List<Drawable> mStarDrawable;
    private int mWidth;
    private int mHeight;
    //定义贝塞尔曲线的数据点和两个控制点
    private PointF startP, endP, pointX, pointY;
    private Random random = new Random();
    public LikeStar(Context context) {
        this(context, null);
    }

    public LikeStar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeStar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        mStarDrawable = new ArrayList<>();
        startP = new PointF();
        endP = new PointF();
        pointX = new PointF();
        pointY = new PointF();
        //初始化图片资源
        mStarDrawable.add(getResources().getDrawable(R.drawable.like_red, null));
        mStarDrawable.add(getResources().getDrawable(R.drawable.like_blue, null));
        mStarDrawable.add(getResources().getDrawable(R.drawable.like_green, null));
        mStarDrawable.add(getResources().getDrawable(R.drawable.like_repple, null));
        //初始化插补器
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        List<Interpolator> interpolators = new ArrayList<>();
        interpolators.add(new LinearInterpolator());
        interpolators.add(new AccelerateDecelerateInterpolator());
        interpolators.add(new AccelerateInterpolator());
        interpolators.add(new DecelerateInterpolator());

        ImageView image_heard = new ImageView(context);
        image_heard.setImageDrawable(mStarDrawable.get(0));

        image_heard.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        image_heard.setOnClickListener(v -> {
            //点击之后开始动画,添加红心到布局文件并开始动画
            final ImageView image_random = new ImageView(context);
            image_random.setImageDrawable(mStarDrawable.get(random.nextInt(4)));

            image_random.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            addView(image_random);
            invalidate();
            //开始做动画效果
            PointF endPointRandom = new PointF(random.nextInt(mWidth), endP.y);
            BezierTypeEvaluator bezierTypeEvaluator = new BezierTypeEvaluator(new PointF(random.nextInt(mWidth), random.nextInt(mHeight)), new PointF(random.nextInt(mWidth), random.nextInt(mHeight)));
            ValueAnimator valueAnimator = ValueAnimator.ofObject(bezierTypeEvaluator, startP, endPointRandom);
            valueAnimator.addUpdateListener(animation -> {
                PointF pointF = (PointF) animation.getAnimatedValue();
                image_random.setX(pointF.x);
                image_random.setY(pointF.y);
            });

            valueAnimator.setDuration(2000);
            valueAnimator.start();

        });
        addView(image_heard);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        // 初始化各个点
        //借用第一个子view控件中的宽高
        View child = getChildAt(0);
        int childW = child.getMeasuredWidth();
        int childH = child.getMeasuredHeight();

        startP.x = (mWidth - childW) >> 1;
        startP.y = mHeight - childH;
        endP.x = (mWidth - childW) >> 1;
        endP.y = 0 - childH;

        pointX.x = random.nextInt(mWidth / 2);
        pointX.y = random.nextInt(mHeight / 2) + (mHeight >> 1);

        pointY.x = random.nextInt(mWidth / 2) + (mWidth >> 1);
        pointY.y = random.nextInt(mHeight / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //获取view的宽高测量模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //保存测量高度
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();
            child.layout((mWidth - childW) / 2, (mHeight - childH), (mWidth - childW) / 2 + childW, mHeight);
        }
    }

    public class BezierTypeEvaluator implements TypeEvaluator<PointF> {
        private PointF pf1, pf2;

        BezierTypeEvaluator(PointF pf1, PointF pf2) {
            this.pf1 = pf1;
            this.pf2 = pf2;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            PointF pointCur = new PointF();
            pointCur.x = startP.x * (1 - fraction) * (1 - fraction) * (1 - fraction) + 3
                    * pf1.x * fraction * (1 - fraction) * (1 - fraction) + 3
                    * pf2.x * (1 - fraction) * fraction * fraction + endValue.x * fraction * fraction * fraction;// 实时计算最新的点X坐标
            pointCur.y = startP.y * (1 - fraction) * (1 - fraction) * (1 - fraction) + 3
                    * pf1.y * fraction * (1 - fraction) * (1 - fraction) + 3
                    * pf2.y * (1 - fraction) * fraction * fraction + endValue.y * fraction * fraction * fraction;// 实时计算最新的点Y坐标
            return pointCur;
        }
    }
}
