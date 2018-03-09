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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.grasea.grandroid.adapter.ItemConfig;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.adapter.base.BetterRecyclerAdapter;
import tw.yalan.cafeoffice.views.TypeFaceTextView;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class FastFilterRecyclerAdapter extends BetterRecyclerAdapter<FastFilterRecyclerAdapter.ItemObject, FastFilterRecyclerAdapter.ViewHolder> {

    @ItemConfig(id = R.layout.row_fast_filter, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {


    }


    public FastFilterRecyclerAdapter(Context context, List<ItemObject> list, RecyclerItemConfig recyclerItemConfigClass) {
        super(context, list, recyclerItemConfigClass);
        setChooseMode(ChooseMode.MULTIPLE);
    }

    @Override
    public void onItemClick(ViewHolder holder, int index, ItemObject item) {

    }

    @Override
    public void onItemChoose(ViewHolder holder, int position, boolean isChoosed) {
        super.onItemChoose(holder, position, isChoosed);
        if (position != getItemCount() - 1)
            holder.buttonLayout.setAlpha(isChoosed ? 1f : 0.4f);
        else
            holder.buttonLayout.setAlpha(1f);

    }

    @Override
    public void fillItem(ViewHolder holder, int position, ItemObject data) {
        holder.tvText.setText(data.name);
        holder.tvText.setTextColor(data.textColor);
        holder.buttonLayout.setBackgroundResource(data.backgroundResId);
        if (data.iconResId == 0) {
            holder.ivIcon.setVisibility(View.GONE);
        } else {
            holder.ivIcon.setVisibility(View.VISIBLE);
            holder.ivIcon.setImageResource(data.iconResId);
        }
    }

    public static class ItemObject {
        int iconResId;
        int backgroundResId;
        int textColor;
        String name;

        public ItemObject(int iconResId, int backgroundResId, int textColor, String name) {
            this.iconResId = iconResId;
            this.backgroundResId = backgroundResId;
            this.textColor = textColor;
            this.name = name;
        }

        public int getIconResId() {
            return iconResId;
        }

        public int getBackgroundResId() {
            return backgroundResId;
        }

        public String getName() {
            return name;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_text)
        TypeFaceTextView tvText;
        @BindView(R.id.button_layout)
        LinearLayout buttonLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
