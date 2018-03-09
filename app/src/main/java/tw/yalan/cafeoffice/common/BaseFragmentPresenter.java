package tw.yalan.cafeoffice.common;

import android.os.Bundle;

import com.grasea.grandroid.mvp.GrandroidPresenter;

import org.json.JSONException;

/**
 * 主要父類別 Presenter
 * Created by Alan Ding on 2016/5/26.
 */
public abstract class BaseFragmentPresenter<C extends BaseFragment> extends GrandroidPresenter<C> {

    public abstract void onFragmentCreate(Bundle extras, Bundle savedInstanceState);

    public abstract void onFragmentResume();

    public abstract void onFragmentPause();

    public abstract void onFragmentDestroy();

    public void onSaveInstanceState(Bundle outState) {

    }

    public void onRestoreInstanceState(Bundle outState) {

    }

    public void onRequestCountiesFinish(String receiveString) throws JSONException {

    }

    public void onRequestBranchFinish(String receiveString) throws JSONException {

    }

    public void onRequestFailed(String type) {

    }

    public void onRequestFailed(String methodName, Throwable e) {

    }

}
