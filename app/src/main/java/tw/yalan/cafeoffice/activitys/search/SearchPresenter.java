package tw.yalan.cafeoffice.activitys.search;

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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.reflect.TypeToken;
import com.grasea.grandroid.api.RemoteProxy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.yalan.cafeoffice.CafeOfficeApplication;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.api.SelfAPI;
import tw.yalan.cafeoffice.api.model.CafeResponse;
import tw.yalan.cafeoffice.api.model.DefaultResult;
import tw.yalan.cafeoffice.common.BasePresenter;
import tw.yalan.cafeoffice.model.Cafe;
import tw.yalan.cafeoffice.model.LocalCafeSearchHistory;
import tw.yalan.cafeoffice.model.firebase.CafeSearchHistory;
import tw.yalan.cafeoffice.model.firebase.FirebaseDatabaseMapping;
import tw.yalan.cafeoffice.task.SearchCafeByHotItemTask;
import tw.yalan.cafeoffice.utils.GsonUtils;

/**
 * Created by Alan Ding on 2017/2/7.
 */
public class SearchPresenter extends BasePresenter<SearchActivity> {

    DecimalFormat df = new DecimalFormat("0.0");
    Call<CafeResponse> searchCall;
    SearchCafeByHotItemTask searchHotTask;
    AsyncTask asyncLocalHistoryTask;

    List<LocalCafeSearchHistory> localCafeSearchHistories;
    ArrayList<CafeSearchHistory> hotList = new ArrayList<>();
    boolean showAd = true;
    SelfAPI api;
    String searchKey = "";

    @Override
    public void onActivityCreate(Bundle extras) {
        api = RemoteProxy.reflect(SelfAPI.class, this);
        LatLng myLocation = extras.getParcelable(Config.EXTRA_DATA);
        getContract().initViews(myLocation);
        String history = ModelManager.Companion.get().getUserModel().getSearchHistory();
        if (TextUtils.isEmpty(history)) {
            localCafeSearchHistories = new ArrayList<>();
        } else {
            localCafeSearchHistories = GsonUtils.toList(history, new TypeToken<ArrayList<LocalCafeSearchHistory>>() {
            });
        }
        showAd = PreferenceManager.getDefaultSharedPreferences(CafeOfficeApplication.getInstance()).getBoolean("ad_switch", true);
        getContract().initRecyclerView(localCafeSearchHistories, new ArrayList<>());

        DatabaseReference searchHistory = FirebaseDatabase.getInstance()
                .getReference(FirebaseDatabaseMapping.DataBase.ALL)
                .child(FirebaseDatabaseMapping.Table.SearchHistory)
                .child(String.valueOf(getUserCityIndex()));
        searchHistory.orderByChild("count")
                .limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CafeSearchHistory value = snapshot.getValue(CafeSearchHistory.class);
                    hotList.add(value);
                }
                getContract().updateHistoryList(localCafeSearchHistories, hotList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Config.loge("error:" + databaseError);
            }
        });
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();

    }


    public void searchCafe(String newText, int page) {
        if (newText != null) {
            searchKey = newText;
        }
        if (searchCall != null) {
            searchCall.cancel();
        }
        searchCall = api.getCafesByKeyWord(ModelManager.get().getUserModel().getToken(), searchKey, page == -1 ? 1 : page, 50);

        searchCall.enqueue(new Callback<CafeResponse>() {
            @Override
            public void onResponse(Call<CafeResponse> call, Response<CafeResponse> response) {
                searchCall = null;
                if (response.body() != null && response.body().isSuccess()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchKey);
                    FirebaseAnalytics.getInstance(CafeOfficeApplication.getInstance())
                            .logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
                    getContract().onSearchResultGet(showAd, searchKey, response.body().getData(), response.body().getNext());
                } else {
                    getContract().onSearchResultGet(showAd, searchKey, new ArrayList<>(), -1);
                }
            }

            @Override
            public void onFailure(Call<CafeResponse> call, Throwable t) {
                searchCall = null;
            }
        });
    }


    public synchronized void onClickSearchResultItem(Cafe cafe) {
        //Save Local History
        if (asyncLocalHistoryTask != null && !asyncLocalHistoryTask.isCancelled()) {
            asyncLocalHistoryTask.cancel(true);
        }
        asyncLocalHistoryTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                asyncLocalHistoryTask = null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                asyncLocalHistoryTask = null;
            }

            @Override
            protected Void doInBackground(Void... params) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "search_click");
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, cafe.getName());
                FirebaseAnalytics.getInstance(CafeOfficeApplication.getInstance())
                        .logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                List<LocalCafeSearchHistory> localCafeSearchHistories = GsonUtils.toList(ModelManager.Companion.get().getUserModel().getSearchHistory(), new TypeToken<ArrayList<LocalCafeSearchHistory>>() {
                });
                Iterator<LocalCafeSearchHistory> iterator = localCafeSearchHistories.iterator();
                int userChooseCity = getUserCityIndex();
                while (iterator.hasNext()) {
                    LocalCafeSearchHistory history = iterator.next();
                    if (history.getQueryText().equals(cafe.getName())) {
                        iterator.remove();
                        break;
                    }
                }
                localCafeSearchHistories.add(0, new LocalCafeSearchHistory(cafe.getName(), System.currentTimeMillis(), userChooseCity));
                if (localCafeSearchHistories.size() >= 4) {
                    List<LocalCafeSearchHistory> subList = localCafeSearchHistories.subList(0, 4);
                    ModelManager.Companion.get().getUserModel().putSearchHistory(GsonUtils.toJson(subList));
                } else {
                    ModelManager.Companion.get().getUserModel().putSearchHistory(GsonUtils.toJson(localCafeSearchHistories));
                }
                return null;
            }
        }.execute();
        String id = cafe.getId();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference allOfUser = database.getReference(FirebaseDatabaseMapping.DataBase.ALL);
        DatabaseReference child = allOfUser.child(FirebaseDatabaseMapping.Table.SearchHistory)
                .child(String.valueOf(getUserCityIndex())).child(cafe.getId());
        child.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MutableData count = mutableData.child("count");
                Integer countValue = count.getValue(Integer.class);
                if (countValue == null) {
                    return Transaction.success(mutableData);
                } else {
                    mutableData.child("count").setValue(++countValue);
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commit, DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    CafeSearchHistory history = new CafeSearchHistory(id, cafe.getName()
                            , cafe.getRateAverage() != null ? df.format(cafe.getRateAverage()) : "0.0"
                            , Double.valueOf(cafe.getLatitude())
                            , Double.valueOf(cafe.getLongitude()));
                    history.setCountryIndex(getUserCityIndex());
                    child.setValue(history);
                }
                Config.loge("postTransaction:onComplete:" + databaseError + ", dataSnapshot:" + dataSnapshot);
            }
        });
    }

    public int getUserCityIndex() {
        return ModelManager.Companion.get().getUserModel().getUserChooseCity() - 1;
    }

    public void searchCafeByHotItem(CafeSearchHistory dataObject) {
        if (searchCall != null) {
            searchCall.cancel();
        }
        if (searchHotTask != null) {
            searchHotTask.cancel(true);
        }
        searchHotTask = new SearchCafeByHotItemTask(dataObject) {
            @Override
            protected void onPostExecute(Cafe cafe) {
                if (cafe != null) {
                    Config.loge("Do Search:" + cafe.getName());
                }
                int userChooseCity = getUserCityIndex();
                if (dataObject.getCountryIndex() != userChooseCity) {
                    getContract().onSearchHotCafeAtNotSameCity(dataObject.getCountryIndex(), userChooseCity);
                    return;
                }
                getContract().onSearchHotCafeDone(dataObject, cafe);

            }

            @Override
            protected void onCancelled() {
                searchHotTask = null;
            }
        };
        searchHotTask.execute(ModelManager.Companion.get().getUserModel().getCafeList());
    }

    public void onLoadMoreResult(int page) {
        searchCafe(null, page);
    }
}
