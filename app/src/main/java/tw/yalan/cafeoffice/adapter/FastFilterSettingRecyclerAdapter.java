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
import android.widget.TextView;

import com.grasea.grandroid.adapter.GrandroidRecyclerAdapter;
import com.grasea.grandroid.adapter.ItemConfig;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class FastFilterSettingRecyclerAdapter extends GrandroidRecyclerAdapter<FastFilterSettingRecyclerAdapter.ItemObject, FastFilterSettingRecyclerAdapter.ViewHolder> {
    int currentSelectedPosition = -1;

    @ItemConfig(id = R.layout.row_filter_setting, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {
    }


    Context context;
    ViewHolder selectedHolder = null;

    public FastFilterSettingRecyclerAdapter(Context context, ArrayList<ItemObject> list, RecyclerItemConfig recyclerItemConfigClass) {
        super(list, recyclerItemConfigClass);
        this.context = context;
    }

    @Override
    public void onItemClick(ViewHolder holder, int position, ItemObject data) {
        if (currentSelectedPosition == position) {
            holder.itemView.setActivated(false);
            selectedHolder = null;
            currentSelectedPosition = -1;
        } else {
            if (selectedHolder != null) {
                selectedHolder.itemView.setActivated(false);
            }
            selectedHolder = holder;
            currentSelectedPosition = position;
            holder.itemView.setActivated(true);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if (selectedHolder == holder) {
            selectedHolder = null;
        }
    }

    public int getCurrentSelectedPosition() {
        return currentSelectedPosition;
    }

    @Override
    public void fillItem(ViewHolder holder, int position, ItemObject data) {
        if (currentSelectedPosition == position) {
            holder.itemView.setActivated(true);
        } else {
            holder.itemView.setActivated(false);
        }
        holder.tvName.setText(data.getSetting().getItemName());
    }

    public boolean hasSelectedItem() {
        return currentSelectedPosition != -1;
    }

    public static class ItemObject {
        FastFilterSetting setting;

        public ItemObject(FastFilterSetting area) {
            this.setting = area;
        }

        public FastFilterSetting getSetting() {
            return setting;
        }

        public ItemObject setSetting(FastFilterSetting setting) {
            this.setting = setting;
            return this;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
