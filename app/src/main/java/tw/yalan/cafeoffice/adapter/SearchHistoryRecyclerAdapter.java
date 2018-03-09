package tw.yalan.cafeoffice.adapter;

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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.adapter.base.BaseViewHolder;
import tw.yalan.cafeoffice.model.LocalCafeSearchHistory;
import tw.yalan.cafeoffice.model.firebase.CafeSearchHistory;

import static tw.yalan.cafeoffice.adapter.SearchHistoryRecyclerAdapter.DataObject.TYPE_ITEM_HISTORY;
import static tw.yalan.cafeoffice.adapter.SearchHistoryRecyclerAdapter.DataObject.TYPE_ITEM_HOT;
import static tw.yalan.cafeoffice.adapter.SearchHistoryRecyclerAdapter.DataObject.TYPE_TITLE;

/**
 * Created by Alan Ding on 2017/2/10.
 */
public abstract class SearchHistoryRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    Context context;
    ArrayList<SearchHistoryRecyclerAdapter.DataObject> list;

    public SearchHistoryRecyclerAdapter(Context context, ArrayList<SearchHistoryRecyclerAdapter.DataObject> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (list != null) {
            SearchHistoryRecyclerAdapter.DataObject dataObject = list.get(position);
            return dataObject.getType();
        }
        return super.getItemViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_TITLE:
                view = LayoutInflater.from(context).inflate(R.layout.row_search_header, parent, false);
                return new TitleViewHolder(view);
            case TYPE_ITEM_HISTORY:
                view = LayoutInflater.from(context).inflate(R.layout.row_search_local_history, parent, false);
                return new HistoryViewHolder(view);
            case TYPE_ITEM_HOT:
                view = LayoutInflater.from(context).inflate(R.layout.row_search_normal_item, parent, false);
                return new HotViewHolder(view);
        }
        return new BaseViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        onFillItem(holder, list.get(position), position);
    }

    public void onFillItem(BaseViewHolder holder, DataObject dataObject, int position) {
        if (holder instanceof TitleViewHolder) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.tvTitle.setText(((TitleObject) dataObject).getTitle());
        } else if (holder instanceof HotViewHolder) {
            HotViewHolder hotViewHolder = (HotViewHolder) holder;
            HotItemObject data = (HotItemObject) dataObject;
            hotViewHolder.tvCafeName.setText(data.getDataObject().getName());
            hotViewHolder.tvRating.setText(data.getDataObject().getRatting());
            hotViewHolder.tvDistance.setText(data.getDistance());
            hotViewHolder.itemView.setOnClickListener(v -> {
                onClickHotSearchHistory(hotViewHolder, data, position);
            });
        } else if (holder instanceof HistoryViewHolder) {
            HistoryViewHolder historyViewHolder = (HistoryViewHolder) holder;
            HistoryItemObject data = (HistoryItemObject) dataObject;
            historyViewHolder.tvName.setText(data.getDataObject().getQueryText());
            historyViewHolder.itemView.setOnClickListener(v -> {
                onClickLocalSearchHistory(historyViewHolder, data, position);
            });
        }
    }

    public abstract void onClickHotSearchHistory(HotViewHolder hotViewHolder, HotItemObject data, int position);

    public abstract void onClickLocalSearchHistory(HistoryViewHolder hotViewHolder, HistoryItemObject data, int position);

    public ArrayList<DataObject> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class DataObject {
        public static final int TYPE_TITLE = 0;
        public static final int TYPE_ITEM_HISTORY = 1;
        public static final int TYPE_ITEM_HOT = 2;
        private int type = -1;

        public DataObject(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    /**
     * 搜尋記錄標題
     */
    public static class TitleObject extends DataObject {

        private String title;

        public TitleObject(String title) {
            super(TYPE_TITLE);
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class TitleViewHolder extends BaseViewHolder {

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public TitleViewHolder(View itemView) {
            super(itemView);
        }

    }

    /**
     * History Item
     */
    public static class HistoryItemObject extends DataObject {
        private LocalCafeSearchHistory dataObject;

        public HistoryItemObject(LocalCafeSearchHistory dataObject) {
            super(TYPE_ITEM_HISTORY);
            this.dataObject = dataObject;
        }

        public LocalCafeSearchHistory getDataObject() {
            return dataObject;
        }
    }

    public static class HistoryViewHolder extends BaseViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;

        public HistoryViewHolder(View itemView) {
            super(itemView);
        }

    }

    /**
     * Hot Item
     */
    public static class HotItemObject extends DataObject {
        private CafeSearchHistory dataObject;
        private String distance = "";

        public HotItemObject(CafeSearchHistory dataObject, String distance) {
            super(TYPE_ITEM_HOT);
            this.dataObject = dataObject;
            this.distance = distance;
        }

        public CafeSearchHistory getDataObject() {
            return dataObject;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }
    }

    public static class HotViewHolder extends BaseViewHolder {

        @BindView(R.id.tv_cafe_name)
        TextView tvCafeName;
        @BindView(R.id.tv_rating)
        TextView tvRating;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.tv_distance_hint)
        TextView tvDistanceHint;

        public HotViewHolder(View itemView) {
            super(itemView);
        }

    }
}
