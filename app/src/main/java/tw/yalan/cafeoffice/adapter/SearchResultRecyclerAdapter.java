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
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.grasea.grandroid.adapter.GrandroidRecyclerAdapter;
import com.grasea.grandroid.adapter.ItemConfig;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.adapter.base.BaseViewHolder;
import tw.yalan.cafeoffice.model.Cafe;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class SearchResultRecyclerAdapter extends GrandroidRecyclerAdapter<SearchResultRecyclerAdapter.BaseObject, BaseViewHolder> {

    public void setQueryString(String queryString) {
        this.queryStr = queryString;
    }

    @ItemConfig(id = R.layout.row_search_normal_item, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {

    }

    @ItemConfig(id = R.layout.row_search_header, viewHolder = TitleHolder.class)
    public static class HeaderItemConfig extends RecyclerItemConfig<TitleHolder> {

    }

    Context context;
    String queryStr = "";
    private DecimalFormat df = new DecimalFormat("0.0");
    private static final int TYPE_AD = 60;

    public SearchResultRecyclerAdapter(Context context, ArrayList<BaseObject> list, RecyclerItemConfig recyclerItemConfigClass) {
        super(list, recyclerItemConfigClass);
        this.context = context;
    }

    @Override
    public RecyclerItemConfig createHeaderRecyclerItemConfig(ViewGroup parent, int viewType) {
        return new HeaderItemConfig();
    }

    @Override
    public int getItemViewType(int position) {
        if (getList().get(position) instanceof TitleObject) {
            return TYPE_HEADER;
        } else if (getList().get(position) instanceof ItemObject && ((ItemObject) getList().get(position)).getDataObject() == null) {
            return TYPE_AD;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_AD == viewType) {
            AdViewHolder adViewHolder = new AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_native_ad, parent, false));
            return adViewHolder;
        }
        return super.getViewHolder(parent, viewType);
    }

    @Override
    public void onItemClick(BaseViewHolder holder, int index, BaseObject item) {

    }

    @Override
    public void onViewDetachedFromWindow(BaseViewHolder baseViewHolder) {
        super.onViewDetachedFromWindow(baseViewHolder);
        if (baseViewHolder.getItemViewType() == TYPE_AD) {
            AdViewHolder holder = (AdViewHolder) baseViewHolder;
            if (!holder.adView.isLoading())
                holder.adView.pause();
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder baseViewHolder) {
        super.onViewAttachedToWindow(baseViewHolder);
        if (baseViewHolder.getItemViewType() == TYPE_AD) {
            AdViewHolder holder = (AdViewHolder) baseViewHolder;
            holder.adView.resume();
        }
    }

    @Override
    public void fillItem(BaseViewHolder baseViewHolder, int position, BaseObject data) {
        if (baseViewHolder.getItemViewType() == TYPE_ITEM) {
            ItemObject itemObject = (ItemObject) data;
            ViewHolder holder = (ViewHolder) baseViewHolder;
            Cafe cafe = itemObject.dataObject;
            holder.layoutItem.setVisibility(View.VISIBLE);
            Config.loge("position:" + position);
            TextView tvName = holder.tvCafeName;
            String name = cafe.getName();
            SimplifySpanBuild sb = new SimplifySpanBuild();
            int indexOf = name.toLowerCase().indexOf(queryStr.toLowerCase());
            Config.loge(name + ": query:" + queryStr + " , indexOf:" + indexOf);
            if (indexOf != -1) {
                if (indexOf > 1) {
                    sb.append(new SpecialTextUnit(name.substring(0, indexOf), ContextCompat.getColor(context, R.color.colorTextBlack)));
                }
                sb.append(new SpecialTextUnit(name.substring(indexOf, indexOf + queryStr.length()), ContextCompat.getColor(context, R.color.colorPrimary)))
                        .append(new SpecialTextUnit(name.substring(indexOf + queryStr.length()), ContextCompat.getColor(context, R.color.colorTextBlack)));
                tvName.setText(sb.build());
            } else {
                sb.append(new SpecialTextUnit(name, ContextCompat.getColor(context, R.color.colorTextBlack)));
                tvName.setText(sb.build());

            }
            holder.tvDistance.setText(cafe.getDisplayDistance());
            if (cafe.getRateAverage() != null)
                holder.tvRating.setText(df.format(cafe.getRateAverage()));
            else
                holder.tvRating.setText("0.0");

        } else if (baseViewHolder.getItemViewType() == TYPE_AD) {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("912AF21E051492C22CE5C377D624164C").build();
            ((AdViewHolder) baseViewHolder).adView.loadAd(adRequest);
        } else if (baseViewHolder.getItemViewType() == TYPE_HEADER) {
            TitleObject titleObject = (TitleObject) data;
            TitleHolder holder = (TitleHolder) baseViewHolder;
            holder.tvTitle.setText(titleObject.getTitle());
        }
    }

    public static class BaseObject {

    }

    public static class TitleObject extends BaseObject {
        String title;

        public TitleObject(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class ItemObject extends BaseObject {
        Cafe dataObject;

        public ItemObject() {
        }

        public ItemObject(Cafe dataObject) {
            this.dataObject = dataObject;
        }

        public Cafe getDataObject() {
            return dataObject;
        }
    }

    public static class TitleHolder extends BaseViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public TitleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_cafe_name)
        TextView tvCafeName;
        @BindView(R.id.tv_rating)
        TextView tvRating;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.tv_distance_hint)
        TextView tvDistanceHint;
        @BindView(R.id.layout_item)
        RelativeLayout layoutItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class AdViewHolder extends BaseViewHolder {
        @BindView(R.id.native_ad)
        NativeExpressAdView adView;

        public AdViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
