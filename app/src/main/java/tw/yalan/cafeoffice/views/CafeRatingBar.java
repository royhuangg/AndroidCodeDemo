package tw.yalan.cafeoffice.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import tw.yalan.cafeoffice.R;

/**
 * Created by Alan Ding on 2017/5/7.
 */

public class CafeRatingBar extends LinearLayout {
    private String mText;
    private boolean mEnableText = true;
    private int mDrawableLeftId = 0;
    private float mStepSize;
    private int mNumStars;
    private float mRating;
    private RatingBar mRatingBar;
    private TextView textView;
    private RatingBar.OnRatingBarChangeListener mOnRatingBarChangeListener;

    public CafeRatingBar(Context context) {
        super(context);
        init(context, null);
    }

    public CafeRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CafeRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CafeRatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.CafeRatingBar);
            mText = a.getString(R.styleable.CafeRatingBar_cr_text);
            if (TextUtils.isEmpty(mText)) {
                mEnableText = false;
            }
            mDrawableLeftId = a.getResourceId(R.styleable.CafeRatingBar_cr_drawable, 0);
            mNumStars = a.getInt(R.styleable.CafeRatingBar_cr_numStarts, 5);
            mStepSize = a.getFloat(R.styleable.CafeRatingBar_cr_stepSize, 0.5f);
            mRating = a.getFloat(R.styleable.CafeRatingBar_cr_rating, 0f);
            a.recycle();
        }
        LayoutInflater.from(getContext()).inflate(R.layout.view_rating_dialog_cell, this);
        textView = (TextView) findViewById(R.id.tv_text);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingBar.setNumStars(mNumStars);
        mRatingBar.setStepSize(mStepSize);
        mRatingBar.setRating(mRating);
        if (mDrawableLeftId != 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeftId, 0, 0, 0);
        }
        if (textView != null && mEnableText)
            textView.setText(mText);
        else {
            textView.setVisibility(View.GONE);
        }
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    mRating = rating;
                }
                if (mOnRatingBarChangeListener != null) {
                    mOnRatingBarChangeListener.onRatingChanged(ratingBar, rating, fromUser);
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        mRatingBar.setEnabled(enabled);
        textView.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public void setRating(float mRating) {
        this.mRating = mRating;
        mRatingBar.setRating(mRating);
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
        if (textView != null) {
            textView.setText(mText);
        }
    }

    public boolean isEnableText() {
        return mEnableText;
    }

    public void setEnableText(boolean mEnableText) {
        this.mEnableText = mEnableText;
        if (textView != null) {
            textView.setVisibility(mEnableText ? View.VISIBLE : View.GONE);
        }
    }

    public int getDrawableLeftId() {
        return mDrawableLeftId;
    }

    public void setDrawableLeftId(int mDrawableLeftId) {
        this.mDrawableLeftId = mDrawableLeftId;
        if (textView != null) {
            textView.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeftId, 0, 0, 0);
        }
    }

    public float getStepSize() {
        return mStepSize;
    }

    public void setStepSize(float mStepSize) {
        this.mStepSize = mStepSize;
        mRatingBar.setStepSize(mStepSize);
    }

    public int getNumStars() {
        return mNumStars;
    }

    public void setNumStars(int mNumStars) {
        this.mNumStars = mNumStars;
        mRatingBar.setNumStars(mNumStars);
    }

    public float getRating() {
        return mRating;
    }

    public RatingBar getRatingBar() {
        return mRatingBar;
    }

    public RatingBar.OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return mOnRatingBarChangeListener;
    }

    public void setOnRatingBarChangeListener(RatingBar.OnRatingBarChangeListener mOnRatingBarChangeListener) {
        this.mOnRatingBarChangeListener = mOnRatingBarChangeListener;
    }
}
