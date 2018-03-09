package tw.yalan.cafeoffice.common;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.grasea.grandroid.mvp.GrandroidActivity;
import com.grasea.grandroid.mvp.GrandroidFragment;

import tw.yalan.cafeoffice.R;


public abstract class BaseFragment<P extends BaseFragmentPresenter> extends GrandroidFragment<P> {

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getPresenter() != null)
            getPresenter().onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (getPresenter() != null)
            getPresenter().onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return onFragmentCreate(inflater, container, savedInstanceState);
    }

    @Nullable
    final public GrandroidActivity getGrandroidActivity() {
        if (getActivity() instanceof GrandroidActivity) {
            return ((GrandroidActivity) getActivity());
        } else {
            return null;
        }
    }

    protected abstract View onFragmentCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     *
     */
    public void initViews() {

    }

    /**
     * 此方法在本專案無使用
     *
     * @return
     */
    @Override
    public GrandroidActivity.UISettingEvent getUISetting() {
        return new GrandroidActivity.UISettingEvent() {
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPresenter() != null) {
            getPresenter().onFragmentResume();
        }
    }

    @Override
    public void onDestroy() {
        if (getPresenter() != null) {
            getPresenter().onFragmentDestroy();
        }
        super.onDestroy();
//        FantasySpinApplication.getRefWatcher().watch(this);
    }

    @Override
    public void onPause() {
        if (getPresenter() != null) {
            getPresenter().onFragmentPause();
        }
        super.onPause();
    }

    @Override
    public int[] getFragmentTransitions() {
        return null;
    }


    /**
     * Show Alert.
     *
     * @param message the message
     */
    protected void alert(String message) {
        new AlertDialog.Builder(getActivity()).setMessage(message).setPositiveButton(R.string.alert_btn_confirm, null).show();
    }

    /**
     * Show Alert.
     *
     * @param message          the message
     * @param positiveListener the positive listener
     */
    protected void alert(String message, DialogInterface.OnClickListener positiveListener) {
        new AlertDialog.Builder(getActivity()).setMessage(message).setPositiveButton(R.string.alert_btn_confirm, positiveListener).show();
    }

    /**
     * Show Alert.
     *
     * @param message          the message
     * @param positiveListener the positive listener
     * @param negativeListener the negative listener
     */
    protected void alert(String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(getActivity()).setMessage(message).setPositiveButton(R.string.alert_btn_confirm, positiveListener).setNegativeButton(R.string.alert_btn_cancel, negativeListener).show();
    }

    /**
     * Show Alert with view.
     *
     * @param v                the v
     * @param title            the title
     * @param message          the message
     * @param positiveListener the positive listener
     * @param negativeListener the negative listener
     */
    protected void alertWithView(View v, String title, String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.alert_btn_confirm, positiveListener)
                .setNegativeButton(R.string.alert_btn_cancel, negativeListener).show();
    }



    /**
     * Show Toast.
     *
     * @param message the message
     */
    protected void toast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Network not found.
     */
    public void networkNotFound() {
        alert(getString(R.string.msg_network_not_found));
    }

    /**
     * Hide loading dialog.
     */
    @UiThread
    public void hideLoadingDialog() {
        ((BaseActivity) getGrandroidActivity()).hideLoadingDialog();
    }

    /**
     * Show loading dialog.
     *
     * @param message the message
     */
    @UiThread
    public void showLoadingDialog(String message) {
        showLoadingDialog(message, false);
    }

    /**
     * Show loading dialog.
     *
     * @param message    the message
     * @param cancelable the cancelable
     */
    @UiThread
    public void showLoadingDialog(String message, Boolean cancelable) {
        ((BaseActivity) getGrandroidActivity()).showLoadingDialog(message, cancelable);
    }


//    public void updateBalance(UpdateCurrencyEvent updateCurrencyEvent) {
//        EventBus.getDefault().post(updateCurrencyEvent);
//    }
}
