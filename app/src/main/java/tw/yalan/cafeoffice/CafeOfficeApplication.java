package tw.yalan.cafeoffice;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.amplitude.api.Amplitude;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.grasea.grandroid.app.GrandroidApplication;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import io.objectbox.BoxStore;
import tw.yalan.cafeoffice.common.database.KapiDataBase;
//import tw.yalan.cafeoffice.model.database.MyObjectBox;
import tw.yalan.cafeoffice.playservices.GoogleClientHandler;
import tw.yalan.cafeoffice.playservices.places.PlaceRequestHandler;

/**
 * Created by Alan Ding on 2016/12/19.
 */
public class CafeOfficeApplication extends GrandroidApplication {
    private static CafeOfficeApplication ourInstance = new CafeOfficeApplication();
    private static KapiDataBase dataBase;


    public static CafeOfficeApplication getInstance() {
        return ourInstance;
    }

    public boolean isSignIn() {
        return ModelManager.Companion.get().isSignIn();
    }

    public void signOut() {
        ModelManager.Companion.get().signOut();
    }

    private BoxStore boxStore;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        if (BuildConfig.BUILD_TYPE.equals("debug")) {
        MultiDex.install(this);
//        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        if (BuildConfig.DEBUG)
//            com.goodpatch.feedbacktool.sdk.Balto.init(this);
        ourInstance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

//        dbService.initSubRecyclerView(this);
        Config.loge("id:" + FirebaseInstanceId.getInstance().getId());
        Config.logd("onApplication Create.");
//        FacebookSdk.sdkInitialize(this);

        FirebaseAnalytics.getInstance(this).setUserId(FirebaseInstanceId.getInstance().getId());

//        SyncHandler.getInstance().putConnection(new CafeConnection());

        Fresco.initialize(this);
        GoogleClientHandler.getInstance().init(this,
                Places.GEO_DATA_API,
                Places.PLACE_DETECTION_API,
                LocationServices.API);
        Picasso.setSingletonInstance(new Picasso.Builder(this).loggingEnabled(true)
                .addRequestHandler(new PlaceRequestHandler(GoogleClientHandler.getInstance().getGoogleApiClient())).build());
        Amplitude.getInstance().initialize(this, "4f5f1d32e1e9fde0c7d307c92340944b").enableForegroundTracking(this).trackSessionEvents(true);

        if (BuildConfig.DEBUG) {
            Amplitude.getInstance().setOptOut(true);
        }
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.HTTP_STACK = new OkHttpStack();

        FirebaseInstanceId.getInstance().getToken();

//        if(new File(getDatabasePath(null),"shoparea.db").exists()){
//            Config.loge("DataBase exits");
//        }else{
//            Config.loge("DataBase not exits");
//        }
//        dataBase = RoomAsset.databaseBuilder(this, KapiDataBase.class, "shoparea.db").build();

//        boxStore = MyObjectBox.builder().androidContext(this).build();
    }

    public static KapiDataBase getDataBase() {
        return dataBase;
    }
}
