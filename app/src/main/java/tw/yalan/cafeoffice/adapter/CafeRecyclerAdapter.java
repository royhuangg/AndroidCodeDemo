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
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.grasea.grandroid.adapter.GrandroidRecyclerAdapter;
import com.grasea.grandroid.adapter.ItemConfig;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.model.Cafe;
import tw.yalan.cafeoffice.model.Tag;
import tw.yalan.cafeoffice.utils.FormatUtils;
import tw.yalan.cafeoffice.utils.ImageHelper;
import tw.yalan.cafeoffice.utils.Utility;
import tw.yalan.cafeoffice.views.TagView;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class CafeRecyclerAdapter extends GrandroidRecyclerAdapter<CafeRecyclerAdapter.ItemObject, CafeRecyclerAdapter.ViewHolder> {
    Context context;
    int size;
    ArrayList<Tag> tagArrayList;

    @ItemConfig(id = R.layout.row_cafe_detail, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {


    }


    public CafeRecyclerAdapter(Context context, ArrayList<ItemObject> list, RecyclerItemConfig recyclerItemConfigClass) {
        super(list, recyclerItemConfigClass);
        this.context = context;
        size = (int) Utility.dpToPixel(context, 56);
        tagArrayList = ModelManager.get().getTagList();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onItemClick(ViewHolder holder, int index, ItemObject item) {

    }

    public Context getContext() {
        return context;
    }

    @Override
    public void fillItem(ViewHolder holder, int position, ItemObject data) {
        Cafe cafe = data.dataObject;
        holder.tvCafeName.setText(cafe.getName());
        holder.tvCafeName.setSelected(true);
        if (cafe.getRateAverage() != null) {
            holder.tvRatingAvg.setText(FormatUtils.DEFAULT_FORMATER.format(cafe.getRateAverage()));
        } else {
            holder.tvRatingAvg.setText("0.0");
        }
//        holder.ivCafe.setActualImageResource();
        holder.tvStoreStatus.setEnabled(cafe.isNowOpen() && cafe.getHours() != null);
        if (cafe.getHours() != null) {
            holder.tvStoreStatus.setText(getContext().getString(cafe.isNowOpen() ? R.string.text_open : R.string.text_close));
            holder.tvStoreStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.store_status_color));
            holder.tvTime.setText(cafe.getOpenTimeString());
        } else if (Utility.isNotEmptyOrNull(cafe.getOpenTimeFormUser())) {
            holder.tvStoreStatus.setText(getContext().getString(R.string.text_time_from_user));
            holder.tvStoreStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
            holder.tvTime.setText(cafe.getOpenTimeFormUser());
        } else {
            holder.tvStoreStatus.setText(getContext().getString(R.string.text_no_time));
            holder.tvStoreStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
            holder.tvTime.setText("");
        }
        holder.tvDistance.setText(cafe.getDisplayDistance());
        holder.ivCafe.setBackgroundResource(0);
        int radius = (int) Utility.dpToPixel(context, 3);
        Config.loge("load:" + cafe.getCovers().getLargePic());
        ImageHelper.loadSquaredImage(context, cafe.getCovers().getSmallPic(), R.drawable.loading, R.drawable.no_image, 0, radius, holder.ivCafe);
        List<String> tags = cafe.getTags();
        holder.tag1.setVisibility(View.GONE);
        holder.tag2.setVisibility(View.GONE);
        holder.tag3.setVisibility(View.GONE);
        if (tags != null && tagArrayList != null) {
            int tagCount = 0;
            for (int i = 0; i < tags.size(); i++) {
                Tag tag = findTag(cafe.getTags().get(i));
                if (tag == null) {
                    continue;
                }
                if (tagCount == 0) {
                    Config.loge("Add Tag1:" + tag.getTag_name());
                    holder.tag1.setTagText(tag.getTag_name());
                    holder.tag1.setVisibility(View.VISIBLE);
                } else if (tagCount == 1) {
                    Config.loge("Add Tag2:" + tag.getTag_name());
                    holder.tag2.setTagText(tag.getTag_name());
                    holder.tag2.setVisibility(View.VISIBLE);
                    break;
                }
                tagCount += 1;
            }
        }
    }

    private Tag findTag(String tagId) {
        if (tagId == null) {
            return null;
        }
        if (tagArrayList == null) {
            return null;
        }
        for (Tag tag : tagArrayList) {
            if (tag.getId().equals(tagId)) {
                return tag;
            }
        }
        return null;
    }

    public static class ItemObject {
        Cafe dataObject;

        public ItemObject(Cafe dataObject) {
            this.dataObject = dataObject;
        }

        public Cafe getDataObject() {
            return dataObject;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_cafe)
        ImageView ivCafe;
        @BindView(R.id.tv_cafe_name)
        AppCompatTextView tvCafeName;
        @BindView(R.id.tv_store_status)
        AppCompatTextView tvStoreStatus;
        @BindView(R.id.tv_time)
        AppCompatTextView tvTime;
        @BindView(R.id.tv_distance)
        AppCompatTextView tvDistance;
        @BindView(R.id.tv_rating_avg)
        AppCompatTextView tvRatingAvg;
        @BindView(R.id.tag1)
        TagView tag1;
        @BindView(R.id.tag2)
        TagView tag2;
        @BindView(R.id.tag3)
        TagView tag3;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
