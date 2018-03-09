package tw.yalan.cafeoffice.playservices;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.ref.WeakReference;


public class GoogleClientHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private volatile static GoogleClientHandler instance;
    private boolean inited;
    GoogleApiClient mGoogleApiClient;
    WeakReference<Context> contextWeakReference;

    public static GoogleClientHandler getInstance() {
        if (instance == null) {
            synchronized (GoogleClientHandler.class) {
                if (instance == null) {
                    instance = new GoogleClientHandler();
                }
            }
        }
        return instance;
    }

    private GoogleClientHandler() {
    }

    /**
     * Must to call initSubRecyclerView first.
     *
     * @param context
     * @param apis
     */
    public void init(Context context, Api... apis) {
        if (contextWeakReference == null) {
            contextWeakReference = new WeakReference<>(context);
        } else {
            if (contextWeakReference.get() == null) {
                contextWeakReference = new WeakReference<>(context);
            }
        }
        if (contextWeakReference.get() != null && mGoogleApiClient == null) {

            GoogleApiClient.Builder builder = new GoogleApiClient.Builder(contextWeakReference.get());
            if (apis != null) {
                for (Api api : apis) {
                    builder.addApi(api);
                }
            }
            mGoogleApiClient = builder.addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        }
        inited = true;
    }

    /**
     * Call after super.onStart()
     */
    public void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    /**
     * Call before super.onStop()
     */
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnecting())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean isConnecting() {
        if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnecting();
        }
        return false;
    }

    public boolean isConnected() {
        if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnected();
        }
        return false;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

}
