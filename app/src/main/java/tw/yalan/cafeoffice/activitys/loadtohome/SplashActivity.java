package tw.yalan.cafeoffice.activitys.loadtohome;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.grasea.grandroid.mvp.UsingPresenter;

import java.util.Timer;
import java.util.TimerTask;

import tw.yalan.cafeoffice.CafeOfficeApplication;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.activitys.cafemap.CafeMapActivity;
import tw.yalan.cafeoffice.common.BaseActivity;

@UsingPresenter(value = SplashPresenter.class, singleton = false)
public class SplashActivity extends BaseActivity<SplashPresenter> {
    Timer timer;

    public void goToSignIn() {

        changeToActivity(LoginActivity.class, null);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.APP_OPEN, null);
    }

    private void startTimer() {
        timer = new Timer("ChangePageTimer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                getPresenter().startLoading(isSignedIn());

            }
        }, 800);
    }

    public void goToMap() {
        changeToActivity(CafeMapActivity.class, null);
        finish();
    }

    @Override
    public void onRequestFailed(String type) {
        super.onRequestFailed(type);
        CafeOfficeApplication.getInstance().signOut();
        changeToActivity(LoginActivity.class, null);
        finish();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
    }
}
