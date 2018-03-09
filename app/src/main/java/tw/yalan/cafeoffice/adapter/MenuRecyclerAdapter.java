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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.easing.linear.Linear;
import com.grasea.grandroid.adapter.GrandroidRecyclerAdapter;
import com.grasea.grandroid.adapter.ItemClickable;
import com.grasea.grandroid.adapter.ItemConfig;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.adapter.base.BaseRecyclerAdapter;
import tw.yalan.cafeoffice.common.City;
import tw.yalan.cafeoffice.model.MenuItemObject;
import tw.yalan.cafeoffice.model.MenuSubItemObject;
import tw.yalan.cafeoffice.views.ExpandableLayout;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class MenuRecyclerAdapter extends BaseRecyclerAdapter<MenuRecyclerAdapter.ItemObject, MenuRecyclerAdapter.ViewHolder> {


    private City currentCity = City.UNKNOW;

    public void setCurrentCity(City currentCity) {
        this.currentCity = currentCity;
        notifyDataSetChanged();
    }

    @ItemConfig(id = R.layout.row_slide_menu, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {

    }

    public MenuRecyclerAdapter(Context context, ArrayList<ItemObject> list, RecyclerItemConfig<? extends RecyclerView.ViewHolder> recyclerItemConfig) {
        super(context, list, recyclerItemConfig);
    }

    @Override
    public void onItemClick(ViewHolder holder, int index, ItemObject item) {
        if (item.dataObject.getCanExpand()) {
            holder.getExpandableLayout().toggleExpansion();
        }
    }

    public void onSubItemClick(int mainIndex, SubMenuRecyclerAdapter.ViewHolder holder, int index, SubMenuRecyclerAdapter.ItemObject item) {

    }

    @Override
    public void fillItem(ViewHolder holder, int position, ItemObject data) {
        holder.tvContent.setText(data.dataObject.getTitle());
        if (data.dataObject.getIconDrawableLeft() != 0) {
            holder.ivIcon.setImageResource(data.dataObject.getIconDrawableLeft());
        }
        if (data.dataObject.getCanExpand()) {
            holder.ivRight.setImageResource(R.drawable.selector_ic_expand);
            ArrayList<SubMenuRecyclerAdapter.ItemObject> items = new ArrayList<>();
            for (MenuSubItemObject menuSubItemObject : data.dataObject.getSubItems()) {
                items.add(new SubMenuRecyclerAdapter.ItemObject(menuSubItemObject));
            }
            holder.initSubRecyclerView(getContext(), items, new OnItemClickListener<SubMenuRecyclerAdapter.ItemObject, SubMenuRecyclerAdapter.ViewHolder>() {
                @Override
                public void onItemClick(SubMenuRecyclerAdapter.ViewHolder holder, int index, SubMenuRecyclerAdapter.ItemObject item) {
                    onSubItemClick(position, holder, index, item);
                }
            });
            holder.getExpandableLayout().setOnExpandListener(new ExpandableLayout.OnExpandListener() {
                @Override
                public void onToggle(ExpandableLayout view, View child, boolean isExpanded) {
                    holder.mainLayout.setActivated(isExpanded);
                    data.setExpand(isExpanded);
                }

                @Override
                public void onExpandOffset(ExpandableLayout view, View child, float offset, int measuredHeight, boolean isExpanding) {

                }
            });
            if (data.getExpand() != holder.getExpandableLayout().isExpanded()) {
                holder.getExpandableLayout().toggleExpansion();
            }

            holder.adapter.setCurrentCity(currentCity);
        } else {
            holder.ivRight.setImageResource(0);
        }
    }

    public static class ItemObject {
        MenuItemObject dataObject;
        Boolean expand = false;

        public ItemObject(MenuItemObject dataObject) {
            this.dataObject = dataObject;
        }

        public MenuItemObject getDataObject() {
            return dataObject;
        }

        public Boolean getExpand() {
            return expand;
        }

        public ItemObject setExpand(Boolean expand) {
            this.expand = expand;
            return this;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ExpandableLayout expandableLayout;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.iv_right)
        ImageView ivRight;
        @BindView(R.id.recyclerSub)
        RecyclerView recyclerSub;
        @BindView(R.id.mainLayout)
        LinearLayout mainLayout;
        SubMenuRecyclerAdapter adapter;

        public ViewHolder(View itemView) {
            super(itemView);
            expandableLayout = (ExpandableLayout) itemView;
            ButterKnife.bind(this, itemView);
        }

        public ExpandableLayout getExpandableLayout() {
            return expandableLayout;
        }

        public void initSubRecyclerView(Context context, ArrayList<SubMenuRecyclerAdapter.ItemObject> items, OnItemClickListener<SubMenuRecyclerAdapter.ItemObject, SubMenuRecyclerAdapter.ViewHolder> itemClickListener) {
            recyclerSub.setLayoutManager(new LinearLayoutManager(context));

            adapter = new SubMenuRecyclerAdapter(context, items, new SubMenuRecyclerAdapter.SimpleItemConfig());
            adapter.setItemClickListener(itemClickListener);
            recyclerSub.setAdapter(adapter);
        }
    }
}
