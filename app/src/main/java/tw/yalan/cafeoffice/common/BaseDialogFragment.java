package tw.yalan.cafeoffice.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by worldexcellent on 2016/9/20.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    public interface onDismissListener {
        void onDismiss(DialogInterface dialog);
    }

    private onDismissListener onDismissListener;

    public BaseDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getContentView(inflater, container, savedInstanceState);
    }

    public abstract View getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public boolean isMatchParent() {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (isMatchParent() && dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    /**
     * Init com.fantasyspin.views.
     */
    public void initViews() {

    }


    /**
     * Show Toast.
     *
     * @param message the message
     */
    protected void toast(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
        super.onDismiss(dialog);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public BaseDialogFragment.onDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(BaseDialogFragment.onDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
