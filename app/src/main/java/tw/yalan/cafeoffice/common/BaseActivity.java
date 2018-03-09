package tw.yalan.cafeoffice.common;

/**
 * Copyright (C) 2016 Alan Ding
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.grasea.grandroid.mvp.GrandroidActivity;

import butterknife.BindView;
import tw.yalan.cafeoffice.CafeOfficeApplication;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;

/**
 * Created by Alan Ding on 2016/9/8.
 */
public class BaseActivity<P extends BasePresenter> extends GrandroidActivity<P> {
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ProgressDialog pd;
    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Config.logi("onAuthStateChanged:signed_in:" + user.getUid());
                onFirebaseUserAuthStateChanged(true, firebaseAuth);
            } else {
                // User is signed out
                Config.logi("onAuthStateChanged:signed_out");
                onFirebaseUserAuthStateChanged(false, firebaseAuth);

            }
            // ...
        }
    };


    public void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new ProgressDialog(this);
        getPresenter().onActivityCreate(getIntent().getExtras());
        if (savedInstanceState != null) {
            getPresenter().onActivityCreateWithState(getIntent().getExtras(), savedInstanceState);
        }
    }


    @Override
    public void onEventMainThread(UISettingEvent event) {
        //TODO 根據currentEvent內的參數，來決定Activity內的UI變化，機制類似於以前的UISetting
    }

    public boolean isSignedIn() {
        return CafeOfficeApplication.getInstance().isSignIn();

    }

    public void onFirebaseUserAuthStateChanged(boolean isSignedIn, @NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
            setScreen(getClass().getName(), getClass().getName());
        }
        getPresenter().onActivityResume();
    }

    public void setScreen(String s1, String s2) {
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, s1, s2);

    }

    @Override
    protected void onPause() {
        getPresenter().onActivityPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        getPresenter().onActivityDestroy();
        super.onDestroy();
    }

    @Nullable
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Init views.
     */
    public void initViews() {

    }

    /**
     * Show Alert.
     *
     * @param message the message
     */
    protected void alert(String message) {
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.alert_btn_confirm, null).show();
    }

    /**
     * Show Alert.
     *
     * @param message          the message
     * @param positiveListener the positive listener
     */
    protected void alert(String message, DialogInterface.OnClickListener positiveListener) {
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.alert_btn_confirm, positiveListener).show();
    }

    /**
     * Show Alert.
     *
     * @param message          the message
     * @param positiveListener the positive listener
     * @param negativeListener the negative listener
     */
    protected void alert(String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.alert_btn_confirm, positiveListener).setNegativeButton(R.string.alert_btn_cancel, negativeListener).show();
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
        new AlertDialog.Builder(this).setView(v)
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hide loading dialog.
     */
    @UiThread
    public void hideLoadingDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    /**
     * Show loading dialog.
     *
     * @param message the message
     */
    @UiThread
    public void showLoadingDialog(@Nullable String message) {
        showLoadingDialog(message, false);
    }

    /**
     * Show loading dialog.
     *
     * @param message    the message
     * @param cancelable the cancelable
     */
    @UiThread
    public void showLoadingDialog(@Nullable String message, Boolean cancelable) {
        try {
            if (pd != null && !pd.isShowing()) {
                if (message == null) {
                    message = getString(R.string.msg_loading);
                }
                pd.setCancelable(cancelable);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onLoadingDialogCanceled();
                    }
                });
                pd.setMessage(message);
                pd.show();
            }
        } catch (Exception e) {
            Config.loge(e);
        }
    }

    /**
     * On loading dialog canceled.
     */
    public void onLoadingDialogCanceled() {
    }

    /**
     * Change to activity.
     *
     * @param activityClass the activity class
     * @param bundle        the bundle
     * @param flag          the flag
     */
    public void changeToActivity(Class activityClass, @Nullable Bundle bundle, int flag) {
        Intent intent = new Intent(this, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (flag != 0) {
            intent.setFlags(flag);
        }
        startActivity(intent);

    }

    /**
     * Change to activity.
     *
     * @param activityClass the activity class
     * @param bundle        the bundle
     */
    public void changeToActivity(Class activityClass, Bundle bundle) {
        changeToActivity(activityClass, bundle, 0);
    }

    /**
     * Change to activity for result.
     *
     * @param activityClass the activity class
     * @param bundle        the bundle
     * @param requestCode   the request code
     */
    public void changeToActivityForResult(Class activityClass, Bundle bundle, int requestCode) {
        changeToActivityForResult(activityClass, bundle, 0, requestCode);
    }

    /**
     * Change to activity for result.
     *
     * @param activityClass the activity class
     * @param bundle        the bundle
     * @param flag          the flag
     * @param requestCode   the request code
     */
    public void changeToActivityForResult(Class activityClass, Bundle bundle, int flag, int requestCode) {
        Intent intent = new Intent(this, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (flag != 0) {
            intent.setFlags(flag);
        }
        startActivityForResult(intent, requestCode);
    }


    /**
     * Network not found.
     */
    public void networkNotFound() {
        alert(getString(R.string.msg_network_not_found));
    }


    public void onRequestFailed(String type) {

    }


}
