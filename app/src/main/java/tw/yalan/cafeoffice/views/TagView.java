package tw.yalan.cafeoffice.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import tw.yalan.cafeoffice.R;

/**
 * Created by Alan Ding on 2017/5/7.
 */

public class TagView extends FrameLayout {
    private String mTagText;
    private int mTagTextColor;
    private ColorStateList mTagTextColorStateList;
    private float mTagTextSize = -1f;
    Drawable mBackgroundDrawable;

    TextView mTextView;

    public TagView(Context context) {
        super(context);
        init(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.TagView);
            mTagText = a.getString(
                    R.styleable.TagView_tagText);
            mTagTextColor = a.getColor(
                    R.styleable.TagView_tagTextColor,
                    ContextCompat.getColor(context, R.color.colorPrimary));
            // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
            // values that should fall on pixel boundaries.
            mTagTextSize = a.getDimension(
                    R.styleable.TagView_tagTextSize,
                    mTagTextSize);

            if (a.hasValue(R.styleable.TagView_tagBackground)) {
                mBackgroundDrawable = a.getDrawable(
                        R.styleable.TagView_tagBackground);
            }

            a.recycle();
        }
        LayoutInflater.from(getContext()).inflate(R.layout.view_tag, this);
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setTextColor(mTagTextColor);
        if (mTagTextSize == -1f) {
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        } else {
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTagTextSize);
        }
        mTextView.setText(mTagText);
        if (mBackgroundDrawable != null)
            mTextView.setBackground(mBackgroundDrawable);
    }

    public String getTagText() {
        return mTagText;
    }

    public TagView setTagText(String mTagText) {
        this.mTagText = mTagText;
        mTextView.setText(mTagText);
        return this;
    }

    public int getTagTextColor() {
        return mTagTextColor;
    }

    public TagView setTagTextColor(@ColorRes int mTagTextColor) {
        this.mTagTextColor = ContextCompat.getColor(getContext(), mTagTextColor);
        mTextView.setTextColor(this.mTagTextColor);
        return this;
    }
    public TagView setTagTextColorStateList(@ColorRes int mTagTextColor) {
        this.mTagTextColorStateList = ContextCompat.getColorStateList(getContext(), mTagTextColor);
        mTextView.setTextColor(this.mTagTextColorStateList);

        return this;
    }
    public float getTagTextSize() {
        return mTagTextSize;
    }

    public TagView setTagTextSize(float mTagTextSize) {
        this.mTagTextSize = mTagTextSize;
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.mTagTextSize);
        return this;
    }

    public Drawable getBackgroundDrawable() {
        return mBackgroundDrawable;
    }

    public TagView setTagBackgroundDrawable(Drawable mBackgroundDrawable) {
        this.mBackgroundDrawable = mBackgroundDrawable;
        mTextView.setBackground(mBackgroundDrawable);
        return this;
    }

}
