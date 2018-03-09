package tw.yalan.cafeoffice.views;

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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.adapter.RatingRecyclerAdapter;
import tw.yalan.cafeoffice.model.Cafe;
import tw.yalan.cafeoffice.model.RatingItem;
import tw.yalan.cafeoffice.task.FindNearStationTask;
import tw.yalan.cafeoffice.utils.Utility;

/**
 * Created by Alan Ding on 2016/12/20.
 */
public class CafeDetailLayout extends RelativeLayout {
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.recycler_detail)
    RecyclerView recyclerRating;
    @BindView(R.id.tv_navigation)
    TextView btnNavigation;
    @BindView(R.id.tv_mrt_title)
    TextView tvMrtTitle;
    @BindView(R.id.tv_mrt_name)
    TextView tvMrtName;
    @BindView(R.id.tv_mrt_line)
    TextView tvMrtLine;
    @BindView(R.id.layout_mrt)
    LinearLayout layoutMrt;
    @BindView(R.id.iv_mrt_line)
    ImageView ivMrtLine;
    @BindView(R.id.tv_store_status)
    TextView tvOpenStatus;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_search)
    TextView tvSearchGoogle;
    //    @BindView(R.id.btn_search_google)
//    Button btnSearchGoogle;
    Cafe cafe;
    RatingRecyclerAdapter adapter;

    FindNearStationTask syncNearStationTask;
    boolean showMRT = false;
    @BindView(R.id.tv_check_other_time)
    TextView tvCheckOtherTime;
    @BindView(R.id.tv_street)
    TextView tvStreet;
    @BindView(R.id.detail_container)
    RelativeLayout detailContainer;


    public CafeDetailLayout(Context context) {
        super(context);
        init();
    }

    public CafeDetailLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CafeDetailLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CafeDetailLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_cafe_detail_footer, this, true);
        ButterKnife.bind(this, view);
        recyclerRating.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new RatingRecyclerAdapter(getContext(), new ArrayList<>(), new RatingRecyclerAdapter.SimpleItemConfig());
        recyclerRating.setAdapter(adapter);
    }

    public void setShowMRT(boolean showMRT) {
        this.showMRT = showMRT;
    }

    public void setCafe(Cafe cafe) {
        if (syncNearStationTask != null && !syncNearStationTask.isCancelled()) {
            syncNearStationTask.cancel(true);
            syncNearStationTask = null;
        }
        this.cafe = cafe;
        tvOpenStatus.setEnabled(cafe.isNowOpen() && cafe.getHours() != null);
        if (cafe.getHours() != null) {
            tvOpenStatus.setText(getContext().getString(cafe.isNowOpen() ? R.string.text_open : R.string.text_close));
            tvOpenStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.store_status_color));
            tvTime.setText(cafe.getOpenTimeString());
            tvCheckOtherTime.setVisibility(View.VISIBLE);
        } else if (Utility.isNotEmptyOrNull(cafe.getOpenTimeFormUser())) {
            tvOpenStatus.setText(getContext().getString(R.string.text_time_from_user));
            tvOpenStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
            tvTime.setText(cafe.getOpenTimeFormUser());
            tvCheckOtherTime.setVisibility(View.INVISIBLE);
        } else {
            tvOpenStatus.setText(getContext().getString(R.string.text_no_time));
            tvOpenStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
            tvTime.setText("");
            tvCheckOtherTime.setVisibility(View.INVISIBLE);
        }

        tvName.setText(cafe.getName());
        tvAddress.setText(cafe.getAddress());
        tvDistance.setText(cafe.getDisplayDistance());
        adapter.getList().clear();
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.wifi
                        , getContext().getString(R.string.text_wifi)
                        , String.valueOf(cafe.getWifi()))));
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.quiet
                        , getContext().getString(R.string.text_quiet)
                        , String.valueOf(cafe.getQuiet()))));
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.price
                        , getContext().getString(R.string.text_cheap)
                        , String.valueOf(cafe.getCheap()))));
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.seat
                        , getContext().getString(R.string.text_seat)
                        , String.valueOf(cafe.getSeat()))));
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.coffee
                        , getContext().getString(R.string.text_tasty)
                        , String.valueOf(cafe.getTasty()))));
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.music
                        , getContext().getString(R.string.text_music)
                        , String.valueOf(cafe.getMusic()))));
        //限時
        int limitedTimeIndex = cafe.getLimited_time();
        String timeText = "";
        switch (limitedTimeIndex) {
            case 1:
                timeText = getContext().getString(R.string.text_have);
                break;
            case 2:
                timeText = getContext().getString(R.string.text_do_not_have);
                break;
            case 3:
                timeText = getContext().getString(R.string.text_maybe);
                break;
            case 4:
                timeText = getContext().getString(R.string.text_data_empty);
                break;
        }
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.time
                        , getContext().getString(R.string.text_limited_to_time)
                        , timeText, true)));
        //插座
        int socketIndex = cafe.getSocket();
        String socketText = "";
        switch (socketIndex) {
            case 1:
                socketText = getContext().getString(R.string.text_a_lot);
                break;
            case 2:
                socketText = getContext().getString(R.string.text_a_few);
                break;
            case 3:
                socketText = getContext().getString(R.string.text_middle);
                break;
            case 4:
                socketText = getContext().getString(R.string.text_data_empty);
                break;
        }
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.socket
                        , getContext().getString(R.string.text_socket)
                        , socketText, true)));
        //站立桌
        String standingText = "";
        if (cafe.getStanding_desk() != null) {
            standingText = getContext().getString(cafe.getStanding_desk().equals("yes") ? R.string.text_have : R.string.text_do_not_have);
        } else {
            standingText = getContext().getString(R.string.text_data_empty);
        }
        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
                new RatingItem(R.drawable.stand
                        , getContext().getString(R.string.text_stand)
                        , standingText, true)));
        adapter.notifyDataSetChanged();
//        adapter.getList().add(new RatingRecyclerAdapter.ItemObject(
//                new RatingItem(R.drawable.wifi
//                        , getContext().getString(R.string.text_wifi)
//                        , String.valueOf(cafe.getWifi()))));
        if (showMRT) {
            tvMrtTitle.setVisibility(View.VISIBLE);
            layoutMrt.setVisibility(View.VISIBLE);
            //        tvRatingMusic.setText(String.valueOf(cafe.getMusic()));
            syncNearStationTask = new FindNearStationTask(cafe.getPosition(), cafe.getMrt()) {
                @Override
                protected void onPostExecute(MRTData.MRTStation mrtStation) {
                    if (mrtStation != null) {
                        Config.loge("最近車站:" + mrtStation.getName());
                        String line = mrtStation.getLine();
                        MRTData.MRTLine mrtLine = MRTData.MRTLine.valueOfByName(line);
                        ivMrtLine.setBackgroundResource(mrtLine.lineDrawable);
                        tvMrtLine.setText(mrtLine.lineName);
                        tvMrtName.setText(mrtStation.getName());
                    }
                    syncNearStationTask = null;
                }
            };
            syncNearStationTask.execute();
        } else {
            tvMrtTitle.setVisibility(View.INVISIBLE);
            layoutMrt.setVisibility(View.INVISIBLE);
        }
    }

    public TextView getBtnNavigation() {
        return btnNavigation;
    }

    public TextView getSearchGoogleButton() {
        return tvSearchGoogle;
    }

    public TextView getStreetButton() {
        return tvStreet;
    }

    public TextView getCheckOtherTimeButton() {
        return tvCheckOtherTime;
    }

    @OnClick(R.id.detail_container)
    public void onClick() {
    }

//    public Button getBtnSearchGoogle() {
//        return btnSearchGoogle;
//    }
}
