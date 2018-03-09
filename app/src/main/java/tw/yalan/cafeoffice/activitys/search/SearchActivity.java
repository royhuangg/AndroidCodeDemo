package tw.yalan.cafeoffice.activitys.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.amplitude.api.Amplitude;
import com.google.android.gms.maps.model.LatLng;
import com.grasea.grandroid.mvp.UsingPresenter;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.activitys.details.CafeDetailsActivity;
import tw.yalan.cafeoffice.adapter.SearchHistoryRecyclerAdapter;
import tw.yalan.cafeoffice.adapter.SearchResultController;
import tw.yalan.cafeoffice.adapter.SearchResultRecyclerAdapter;
import tw.yalan.cafeoffice.adapter.base.BaseViewHolder;
import tw.yalan.cafeoffice.adapter.base.ColorfulDividerItemDecoration;
import tw.yalan.cafeoffice.common.BaseActivity;
import tw.yalan.cafeoffice.common.City;
import tw.yalan.cafeoffice.listener.EndlessRecyclerViewScrollListener;
import tw.yalan.cafeoffice.model.Cafe;
import tw.yalan.cafeoffice.model.LocalCafeSearchHistory;
import tw.yalan.cafeoffice.model.firebase.CafeSearchHistory;
import tw.yalan.cafeoffice.utils.MapUtils;
import tw.yalan.cafeoffice.views.CustomSearchView;

@UsingPresenter(value = SearchPresenter.class, singleton = false)
public class SearchActivity extends BaseActivity<SearchPresenter> {
    CustomSearchView searchView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.emptyView)
    RelativeLayout emptyView;
    SearchResultRecyclerAdapter adapter;
    SearchResultController controller;
    SearchHistoryRecyclerAdapter searchHistoryAdapter;
    ColorfulDividerItemDecoration colorfulDividerItemDecoration;
    LatLng myLocation;
    EndlessRecyclerViewScrollListener scrollListener;
    int nextIdx = -1;

    public static Bundle newBundle(LatLng myLocation) {
        Bundle bundle = new Bundle();
        if (myLocation != null)
            bundle.putParcelable(Config.EXTRA_DATA, myLocation);
        return bundle;
    }

    public void initViews(LatLng myLocation) {
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initToolbar(toolbar);
        toolbar.setTitleMargin(0, 0, 0, 0);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        this.myLocation = myLocation;
    }

    public void enableItemDecoration(boolean enable) {
        colorfulDividerItemDecoration.setEnable(enable);
    }

    public void initRecyclerView(List<LocalCafeSearchHistory> localHistoryList, List<CafeSearchHistory> hotHistoryList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        colorfulDividerItemDecoration = new ColorfulDividerItemDecoration(1, ContextCompat.getColor(this, R.color.md_grey_400));
        recycler.addItemDecoration(colorfulDividerItemDecoration);
        adapter = new SearchResultRecyclerAdapter(this, new ArrayList<>(), new SearchResultRecyclerAdapter.SimpleItemConfig()) {
            @Override
            public void onItemClick(BaseViewHolder holder, int index, BaseObject item) {
                if (holder.getItemViewType() == TYPE_ITEM) {
                    onSelectCafe(((ItemObject) item).getDataObject());
                } else {
                    Amplitude.getInstance().logEvent(getString(R.string.event_ad));
                }
            }
        };

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Config.logi("loadmore");
                getPresenter().onLoadMoreResult(nextIdx);
            }
        };
        controller = new SearchResultController(scrollListener, new SearchResultController.CallBack() {
            @Override
            public void onClickAd() {
                Amplitude.getInstance().logEvent(getString(R.string.event_ad));
            }

            @Override
            public void onClickCafe(@Nullable Cafe cafe) {
                onSelectCafe(cafe);
            }
        });
        searchHistoryAdapter = new SearchHistoryRecyclerAdapter(this, new ArrayList<>()) {
            @Override
            public void onClickHotSearchHistory(HotViewHolder hotViewHolder, HotItemObject data, int position) {
                getPresenter().searchCafeByHotItem(data.getDataObject());
                searchView.clearFocus();
            }

            @Override
            public void onClickLocalSearchHistory(HistoryViewHolder hotViewHolder, HistoryItemObject data, int position) {
                searchView.setQuery(data.getDataObject().getQueryText(), true);
            }
        };
        updateHistoryList(localHistoryList, hotHistoryList);
        enableItemDecoration(false);
        recycler.setAdapter(searchHistoryAdapter);

    }

    public void onSelectCafe(Cafe cafe) {
        JSONObject json = new JSONObject();
        try {
            json.putOpt("keyword", searchView.getQuery().toString());
            Amplitude.getInstance().logEvent(getString(R.string.event_search_keyword), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cafe == null) {
            return;
        }
        getPresenter().onClickSearchResultItem(cafe);
        searchView.clearFocus();
        changeToActivity(CafeDetailsActivity.class, CafeDetailsActivity.Companion.newBundle(cafe));
//        Intent intent = new Intent();
//        intent.putExtra(Config.EXTRA_DATA, cafe.getId());
//        setResult(RESULT_OK, intent);
//        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem menuSearchItem = menu.findItem(R.id.my_search);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (CustomSearchView) menuSearchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Assumes current activity is the searchable activity
        if (searchManager != null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        RxSearchView.queryTextChanges(searchView)
                .debounce(300, TimeUnit.MILLISECONDS)
//                .filter(c -> c.length() > 1)
                .observeOn(AndroidSchedulers.mainThread())
                .map(charSequence -> {
                    if (null != charSequence)
                        return charSequence.toString().trim();
                    return "";
                })
                .subscribe(s -> {
                            if (!s.equals("")) {
                                resetListState();
                                getPresenter().searchCafe(s, 1);
                            } else {
                                convertToHistoryList();
                            }
                        }
                        , Config::loge);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchView.clearFocus();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                getPresenter().searchCafe(newText);
//                return false;
//            }
//        });
        // 這邊讓icon可以還原到搜尋的icon
//        searchView.setIconifiedByDefault(false);
        searchView.setOnCloseListener(() -> {
            convertToHistoryList();
            searchView.setQuery("", false);
            return true;
        });
        searchView.setIconified(false);
        searchView.requestFocus();
        return true;
    }

    private void resetListState() {
        scrollListener.setHasLoadMore(false);
        controller.setData(new ArrayList<>());
        nextIdx = -1;
        scrollListener.resetState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getAction() != null)
            if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                Config.logi("Search :" + query);
                if (searchView != null) {
                    searchView.clearFocus();
                }
//            drawer.closeDrawers();
//            getPresenter().searchCafe(query, cafes);
            }
    }

    @Override
    public boolean onBackPress() {
        Config.loge("isIconified:" + (searchView.isIconified()) + " , isIconifiedDefault:" + (searchView.isIconfiedByDefault()));
        if (!searchView.isIconified()) {
            if (TextUtils.isEmpty(searchView.getQuery())) {
                return true;
            }
            searchView.setIconified(true);
            return false;
        }
        return super.onBackPress();
    }

    public void convertToHistoryList() {
        if (recycler.getAdapter() instanceof SearchResultRecyclerAdapter) {
            emptyView.setVisibility(View.GONE);
            recycler.removeOnScrollListener(scrollListener);
            recycler.setAdapter(searchHistoryAdapter);
        }

    }

    public void convertToSearchResultList() {
        if (recycler.getAdapter() instanceof SearchHistoryRecyclerAdapter) {
//            recycler.setAdapter(adapter);
            recycler.setAdapter(controller.getAdapter());
            recycler.addOnScrollListener(scrollListener);
        }

    }

    public void onSearchHotCafeAtNotSameCity(int searchCity, int userCurrentCity) {
        alert(getString(R.string.alert_city_not_same, City.valueOfByIndex(searchCity).getCityName()));
    }

    public synchronized void onSearchHotCafeDone(CafeSearchHistory dataObject, Cafe cafe) {
        onSelectCafe(cafe);
    }

    public synchronized void onSearchResultGet(boolean showAd, String newText, ArrayList<Cafe> filterList, int next) {
        this.nextIdx = next;
        if (newText.isEmpty()) {
            convertToHistoryList();
            return;
        }
        Flowable.fromIterable(filterList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(cafe -> {
                    cafe.setDisplayDistance(getDistanceString(Double.valueOf(cafe.getLatitude()), Double.valueOf(cafe.getLongitude())));
                    cafe.setDistance(getDistance(Double.valueOf(cafe.getLatitude()), Double.valueOf(cafe.getLongitude())));
                    return new SearchResultController.ItemObject(cafe);
                })
//                .toSortedList((itemObject, t1) -> itemObject.getDataObject().getDistance() - t1.getDataObject().getDistance())
//                .toFlowable()
//                .concatMap(itemObjects -> Flowable.fromIterable(itemObjects))
                .collect((Callable<ArrayList<SearchResultController.ItemObject>>) () -> new ArrayList<>(), (newList, itemObject) -> {
                    if (newList.size() != 0 && newList.size() % 10 == 0) {
                        newList.add(new SearchResultController.ItemObject());
                    }
                    newList.add(itemObject);
                })
                .subscribe(itemObjects -> {
                    List<SearchResultController.BaseObject> currentData = (List<SearchResultController.BaseObject>) controller.getCurrentData();
                    if (currentData == null || currentData.isEmpty()) {
                        currentData = new ArrayList<>();
                        currentData.add(new SearchResultController.TitleObject(getString(R.string.text_search_result)));
                    }
                    currentData.addAll(itemObjects);
                    scrollListener.setHasLoadMore(nextIdx != -1);
                    controller.setQueryString(searchView.getQuery().toString());
                    emptyView.setVisibility((currentData.size() - 1 <= 0) ? View.VISIBLE : View.GONE);
                    if (itemObjects.size() != 0 && itemObjects.size() < 10) {
                        itemObjects.add(new SearchResultController.ItemObject());
                    }
                    controller.setData(currentData);
                    convertToSearchResultList();
                }, throwable -> Config.loge(throwable));


    }

    public void updateHistoryList(List<LocalCafeSearchHistory> localHistoryList, List<CafeSearchHistory> hotHistoryList) {
        ArrayList<SearchHistoryRecyclerAdapter.DataObject> list = searchHistoryAdapter.getList();
        list.clear();
        if (localHistoryList != null && !localHistoryList.isEmpty()) {
            list.add(new SearchHistoryRecyclerAdapter.TitleObject(getString(R.string.title_search_local)));
            for (LocalCafeSearchHistory localCafeSearchHistory : localHistoryList) {
                list.add(new SearchHistoryRecyclerAdapter.HistoryItemObject(localCafeSearchHistory));
            }
        }
        if (hotHistoryList != null && !hotHistoryList.isEmpty()) {
            list.add(new SearchHistoryRecyclerAdapter.TitleObject(getString(R.string.title_search_hot)));
            for (CafeSearchHistory cafeSearchHistory : hotHistoryList) {
                list.add(new SearchHistoryRecyclerAdapter.HotItemObject(cafeSearchHistory, getDistanceString(cafeSearchHistory.getLat(), cafeSearchHistory.getLng())));
            }
        }
        searchHistoryAdapter.notifyDataSetChanged();
    }

    public String getDistanceString(double lat, double lng) {
        String distance = "";
        if (myLocation != null) {
            String[] distances = MapUtils.getDistance(myLocation
                    , new LatLng(lat, lng));
            if (Integer.valueOf(distances[0]) >= 1000) {
                distance = distances[1] + " KM";
            } else {
                distance = distances[0] + " M";
            }
        } else {
            distance = getString(R.string.text_distance_unknow);
        }
        return distance;
    }

    public int getDistance(double lat, double lng) {
        int distance = 0;
        if (myLocation != null) {
            String[] distances = MapUtils.getDistance(myLocation
                    , new LatLng(lat, lng));
            distance = Integer.valueOf(distances[0]);
        }
        return distance;
    }
}
