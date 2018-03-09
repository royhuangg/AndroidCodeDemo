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
import android.widget.TextView;

import com.grasea.grandroid.adapter.GrandroidRecyclerAdapter;
import com.grasea.grandroid.adapter.ItemConfig;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.adapter.base.BaseRecyclerAdapter;
import tw.yalan.cafeoffice.common.City;
import tw.yalan.cafeoffice.model.MenuItemObject;
import tw.yalan.cafeoffice.model.MenuSubItemObject;
import tw.yalan.cafeoffice.views.ExpandableLayout;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class SubMenuRecyclerAdapter extends BaseRecyclerAdapter<SubMenuRecyclerAdapter.ItemObject, SubMenuRecyclerAdapter.ViewHolder> {


    private City currentCity;

    public void setCurrentCity(City currentCity) {
        this.currentCity = currentCity;
        notifyDataSetChanged();
    }

    @ItemConfig(id = R.layout.row_slide_menu_sub_item, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {


    }

    public SubMenuRecyclerAdapter(Context context, ArrayList<ItemObject> list, RecyclerItemConfig<? extends RecyclerView.ViewHolder> recyclerItemConfig) {
        super(context, list, recyclerItemConfig);
    }


    @Override
    public void onItemClick(ViewHolder holder, int index, ItemObject item) {
        super.onItemClick(holder, index, item);
    }

    @Override
    public void fillItem(ViewHolder holder, int position, ItemObject data) {
        holder.root.setActivated(currentCity == data.dataObject.getCity());
        holder.tvContent.setText(data.dataObject.getTitle());
    }

    public static class ItemObject {
        MenuSubItemObject dataObject;

        public ItemObject(MenuSubItemObject dataObject) {
            this.dataObject = dataObject;
        }

        public MenuSubItemObject getDataObject() {
            return dataObject;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_right)
        ImageView ivRight;

        public ViewHolder(View itemView) {
            super(itemView);
            this.root = itemView;
            ButterKnife.bind(this, itemView);
        }

    }
}
