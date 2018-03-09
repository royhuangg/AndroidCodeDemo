package tw.yalan.cafeoffice.activitys.settings;


import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grasea.grandroid.actions.URLAction;
import com.grasea.grandroid.mvp.UsingPresenter;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.BuildConfig;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.common.BaseActivity;

@UsingPresenter(value = SettingPresenter.class, singleton = false)
public class SettingFragmentActivity extends BaseActivity<SettingPresenter> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvVersionName)
    TextView tvVersionName;
    @BindView(R.id.itemWrite)
    LinearLayout itemWrite;
    @BindView(R.id.itemLogout)
    LinearLayout itemLogout;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.btnUnlike)
    AppCompatButton btnUnlike;
    @BindView(R.id.btnLike)
    AppCompatButton btnLike;
    @BindView(R.id.btnClose)
    ImageView btnClose;
    @BindView(R.id.bottomLayout)
    NestedScrollView bottomLayout;
    @BindView(R.id.shadowBottom)
    View shadowBottom;
    @BindView(R.id.lineLogout)
    View line;


    @Override
    public void initViews() {
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initToolbar(toolbar);
        toolbar.setTitle(R.string.title_setting);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        tvVersionName.setText(BuildConfig.VERSION_NAME);

        if (isSignedIn()) {
            RxView.clicks(itemLogout)
                    .throttleFirst(2, TimeUnit.SECONDS)
                    .subscribe(o -> getPresenter().logout());
        } else {
            itemLogout.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        RxView.clicks(itemWrite)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> goToWriteForm());
    }

    private void goToWriteForm() {
        new URLAction(this, "https://goo.gl/forms/sqXWMu3iHigUprbG3").useChrome().execute();
    }


    public void goToLogin() {
        changeToActivity(LoginActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
