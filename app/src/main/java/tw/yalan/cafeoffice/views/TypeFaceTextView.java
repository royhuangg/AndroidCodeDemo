package tw.yalan.cafeoffice.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;


import java.util.Hashtable;

import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;

/**
 * Created by worldexcellent on 2016/9/21.
 */

public class TypeFaceTextView extends TextView {
    protected static Hashtable<String, Typeface> tableOfTypeFace = new Hashtable<>();
    protected static Typeface normalTypeFace = null;
    protected String defaultTypeFace = "NotoSansCJKtc-Regular.otf";
    Paint.FontMetricsInt fontMetricsInt;
    private boolean adjustTopForAscent = false;
    private boolean autoMarquee = false;

    public TypeFaceTextView(Context context) {
        super(context);
        init(context, null);
    }

    public TypeFaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TypeFaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TypeFaceTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.TypeFaceTextView);
            String ttfType = a.getString(R.styleable.TypeFaceTextView_text_font);
            if (TextUtils.isEmpty(ttfType)) {
                ttfType = defaultTypeFace;
            }
            Typeface tf = getTypeFace(getContext(), ttfType);
            if (tf != null)
                setTypeface(tf);
            adjustTopForAscent = a.getBoolean(R.styleable.TypeFaceTextView_adjust_top_ascent, false);
            autoMarquee = a.getBoolean(R.styleable.TypeFaceTextView_auto_marquee, false);

            a.recycle();
        }
//        setIncludeFontPadding(false);
    }

    @Override
    public boolean isFocused() {
        if (autoMarquee) {
            return true;
        }
        return super.isFocused();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (autoMarquee) {
            if (focused) {
                super.onFocusChanged(focused, direction, previouslyFocusedRect);
            }
        } else {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (autoMarquee) {
            if (focused) {
                super.onWindowFocusChanged(focused);
            }
        } else {
            super.onWindowFocusChanged(focused);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (adjustTopForAscent) {
            if (fontMetricsInt == null) {
                fontMetricsInt = new Paint.FontMetricsInt();
                getPaint().getFontMetricsInt(fontMetricsInt);
            }
            canvas.translate(0, fontMetricsInt.top - fontMetricsInt.ascent);
        }
        super.onDraw(canvas);
    }

    public static Typeface getTypeFace(Context context, String font) {
        Typeface tf = null;
        try {
            if (font != null && font.length() > 0) {
                if (tableOfTypeFace.containsKey(font)) {
                    tf = tableOfTypeFace.get(font);
                } else {
                    tf = Typeface.createFromAsset(context.getAssets(),
                            "fonts/" + font);
                    tableOfTypeFace.put(font, tf);
                }
            }
        } catch (Exception ex) {
//            Config.loge(ex);
        }
        if (tf == null) {
            return normalTypeFace;
        }
        return tf;
    }
}
