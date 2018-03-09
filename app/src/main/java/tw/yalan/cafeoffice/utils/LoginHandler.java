package tw.yalan.cafeoffice.utils;

import android.app.Activity;
import android.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;

/**
 * Created by Alan Ding on 2017/3/15.
 */

public class LoginHandler {
    CallbackManager callbackManager;
    FacebookCallback callback;

    public LoginHandler() {
    }

    private void setup(FacebookCallback<LoginResult> callback) {
        setCallback(callback);
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        LoginManager.getInstance().registerCallback(callbackManager, getCallback());

    }

    public void login(Fragment fragment, FacebookCallback<LoginResult> callback) {
        setup(callback);
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");
        LoginManager.getInstance().logInWithReadPermissions(fragment, permissions);
    }

    public void login(Activity activity, FacebookCallback<LoginResult> callback) {
        setup(callback);
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");
        LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
    }

    public void setupFacebookLoginButton(LoginButton btnFbLogin, FacebookCallback<LoginResult> callback) {
        setCallback(callback);
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        btnFbLogin.setReadPermissions("email", "public_profile");
        btnFbLogin.registerCallback(callbackManager, getCallback());
    }

    public CallbackManager getCallbackManager() {
        if (callbackManager == null) {
            throw new NullPointerException("Please setupFacebookLoginButton first.");
        }
        return callbackManager;
    }

    public FacebookCallback getCallback() {
        return callback;
    }

    public LoginHandler setCallback(FacebookCallback callback) {
        this.callback = callback;
        return this;
    }


}
