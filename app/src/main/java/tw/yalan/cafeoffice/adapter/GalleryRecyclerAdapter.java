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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.grasea.grandroid.adapter.ItemConfig;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.adapter.base.BaseRecyclerAdapter;
import tw.yalan.cafeoffice.api.model.Photo;
import tw.yalan.cafeoffice.utils.ImageHelper;
import tw.yalan.cafeoffice.utils.Utility;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class GalleryRecyclerAdapter extends BaseRecyclerAdapter<Photo, GalleryRecyclerAdapter.ViewHolder> {


    @ItemConfig(id = R.layout.row_gallery, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {


    }

    @ItemConfig(id = R.layout.row_gallery_small, viewHolder = ViewHolder.class)
    public static class SmallSizeItemConfig extends RecyclerItemConfig<ViewHolder> {


    }

    private boolean thumbnailMode = false;
    private int radius = 2;

    public GalleryRecyclerAdapter(Context context, ArrayList<Photo> list, RecyclerItemConfig<? extends RecyclerView.ViewHolder> recyclerItemConfig) {
        super(context, list, recyclerItemConfig);
    }


    @Override
    public void onItemClick(ViewHolder holder, int index, Photo item) {
        super.onItemClick(holder, index, item);
    }

    @Override
    public void fillItem(ViewHolder holder, int position, Photo data) {
        ViewCompat.setTransitionName(holder.ivPhoto, data.getThumbnail());
//        holder.ivPhoto.setTransitionName("Photo_" + position);
        if (thumbnailMode) {
            ImageHelper.loadSquaredImage(getContext(),  data.getThumbnail(), R.drawable.loading, R.drawable.fail, 0, (int) Utility.dpToPixel(getContext(), radius), holder.ivPhoto);

//            ImageHelper.loadSquaredThumbnailImage(getContext(), data.getThumbnail(), R.drawable.loading, R.drawable.fail, (int) Utility.dpToPixel(getContext(), radius), holder.ivPhoto);
        } else {
            ImageHelper.loadSquaredImage(getContext(), data.getUrl(), R.drawable.loading, R.drawable.fail, 0, (int) Utility.dpToPixel(getContext(), radius), holder.ivPhoto);
        }
//        Picasso.with(getContext()).load(data).error(R.drawable.fail).transform(new RoundedCornersTransformation((int) Utility.dpToPixel(getContext(), 5), 0)).into(holder.ivPhoto);
    }

    public int getRadius() {
        return radius;
    }

    public GalleryRecyclerAdapter setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public boolean isThumbnailMode() {
        return thumbnailMode;
    }

    public GalleryRecyclerAdapter setThumbnailMode(boolean thumbnailMode) {
        this.thumbnailMode = thumbnailMode;
        return this;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            this.root = itemView;
            ButterKnife.bind(this, itemView);
        }

        public ImageView getIvPhoto() {
            return ivPhoto;
        }
    }
}
