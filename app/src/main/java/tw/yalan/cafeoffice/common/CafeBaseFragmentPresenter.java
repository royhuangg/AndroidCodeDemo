package tw.yalan.cafeoffice.common;

import android.os.Bundle;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import tw.yalan.cafeoffice.model.Cafe;

/**
 * Created by Alan Ding on 2017/4/2.
 */

public class CafeBaseFragmentPresenter<F extends CafeBaseFragment> extends BaseFragmentPresenter<F> {

    @Override
    public void onFragmentCreate(@Nullable Bundle extras,@Nullable Bundle savedInstanceState) {
        getContract().initViews(savedInstanceState);
    }

    @Override
    public void onFragmentResume() {

    }

    @Override
    public void onFragmentPause() {

    }

    @Override
    public void onFragmentDestroy() {

    }
}
