package tw.yalan.cafeoffice.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import tw.yalan.cafeoffice.R;

/**
 * Created by Alan Ding on 2017/4/9.
 */

public class DetailsHeaderButtonLayout extends LinearLayout {
    public DetailsHeaderButtonLayout(Context context) {
        super(context);
    }

    public DetailsHeaderButtonLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailsHeaderButtonLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DetailsHeaderButtonLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        inflate(getContext(), R.layout.view_details_header, this);
    }
}
