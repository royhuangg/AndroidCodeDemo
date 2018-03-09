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
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grasea.grandroid.adapter.GrandroidRecyclerAdapter;
import com.grasea.grandroid.adapter.ItemConfig;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.model.RatingItem;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class RatingRecyclerAdapter extends GrandroidRecyclerAdapter<RatingRecyclerAdapter.ItemObject, RatingRecyclerAdapter.ViewHolder> {

    @ItemConfig(id = R.layout.row_rating, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {


    }

    Context context;

    public RatingRecyclerAdapter(Context context, ArrayList<ItemObject> list, RecyclerItemConfig recyclerItemConfigClass) {
        super(list, recyclerItemConfigClass);
        this.context = context;
    }

    @Override
    public void onItemClick(ViewHolder holder, int index, ItemObject item) {

    }

    @Override
    public void fillItem(ViewHolder holder, int position, ItemObject data) {
        holder.tvName.setText(data.dataObject.getName());
        holder.tvRating.setText(data.dataObject.getRate());
        holder.ivIcon.setImageResource(data.dataObject.getResId());
        if (data.dataObject.isSmallTextSize()) {
            holder.tvRating.setTypeface(Typeface.DEFAULT_BOLD);
            holder.tvRating.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        } else {
            holder.tvRating.setTypeface(Typeface.create(context.getString(R.string.font_system_roboto_medium), Typeface.NORMAL));
            holder.tvRating.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

    public static class ItemObject {
        RatingItem dataObject;

        public ItemObject(RatingItem dataObject) {
            this.dataObject = dataObject;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_rating)
        TextView tvRating;
        @BindView(R.id.iv_icon)
        ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
