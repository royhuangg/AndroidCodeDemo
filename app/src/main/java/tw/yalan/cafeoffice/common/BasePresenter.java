package tw.yalan.cafeoffice.common;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.grasea.grandroid.api.RemoteProxy;
import com.grasea.grandroid.mvp.GrandroidActivity;
import com.grasea.grandroid.mvp.GrandroidPresenter;

import io.reactivex.disposables.CompositeDisposable;
import tw.yalan.cafeoffice.Config;


/**
 * 主要父類別 Presenter
 * Created by Alan Ding on 2016/5/26.
 */
public abstract class BasePresenter<C extends GrandroidActivity> extends GrandroidPresenter<C> {
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public abstract void onActivityCreate(@Nullable Bundle extras);

    public abstract void onActivityResume();

    public abstract void onActivityPause();

    public void onActivityCreateWithState(@Nullable Bundle extras, @Nullable Bundle saveState) {

    }

    public void onActivityDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            try {
                compositeDisposable.dispose();
            } catch (Exception e) {
                Config.loge(e);
            }
        }
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public void onRestoreInstanceState(Bundle outState) {

    }

    public void onTokenExpired() {

    }

    public void handleErrorCode(int errorCode) {
        switch (errorCode) {
            case 20005:
                //Toke expired.
                onTokenExpired();
                break;
        }
    }

}
