package tw.yalan.cafeoffice.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import tw.yalan.cafeoffice.model.Cafe;

/**
 * Created by Alan Ding on 2017/4/2.
 */

public abstract class CafeBaseFragment<P extends CafeBaseFragmentPresenter> extends BaseFragment<P> {
    Unbinder unbinder;

    @Override
    protected View onFragmentCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().onFragmentCreate(getArguments(), savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    public abstract int getLayoutResourceId();

    public abstract void onCafeDataComing(Cafe cafe);


    public abstract void updateCafeList(String city, List<Cafe> cafes, boolean hasFilter);

    public void initViews(@Nullable Bundle savedInstanceState) {

    }
}
