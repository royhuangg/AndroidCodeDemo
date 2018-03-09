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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.api.model.Comment;
import tw.yalan.cafeoffice.api.model.LikesObject;
import tw.yalan.cafeoffice.model.Cafe;
import tw.yalan.cafeoffice.utils.FormatUtils;
import tw.yalan.cafeoffice.utils.ImageHelper;

/**
 * Created by Alan Ding on 2016/7/25.
 */
public class CafeCommentRecyclerAdapter extends GrandroidRecyclerAdapter<CafeCommentRecyclerAdapter.ItemObject, CafeCommentRecyclerAdapter.ViewHolder> {
    Context context;
    String userId;

    @ItemConfig(id = R.layout.row_cafe_comment, viewHolder = ViewHolder.class)
    public static class SimpleItemConfig extends RecyclerItemConfig<ViewHolder> {


    }


    public CafeCommentRecyclerAdapter(Context context, ArrayList<ItemObject> list, RecyclerItemConfig recyclerItemConfigClass) {
        super(list, recyclerItemConfigClass);
        this.context = context;
        userId = ModelManager.Companion.get().getUserModel().getUserId();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void onClickUpVote(boolean isCancel, String commentId) {

    }

    public void onClickDownVote(boolean isCancel, String commentId) {

    }

    @Override
    public void onItemClick(ViewHolder holder, int index, ItemObject item) {

    }

    public Context getContext() {
        return context;
    }

    @Override
    public void fillItem(ViewHolder holder, int position, ItemObject data) {
        Comment comment = data.dataObject;
        holder.tvComment.setText(comment.getContent());
        holder.tvUserName.setText(comment.getDisplayName());
        holder.tvUpVote.setText(String.valueOf(comment.getLikeCount()));
        holder.tvDownVote.setText(String.valueOf(comment.getDislikeCount()));
        holder.tvSubmitTime.setText(FormatUtils.formatCreateTime(comment.getCreatedTime() * 1000));
        ImageHelper.loadSquaredImage(context, comment.getPhotoUrl(), R.drawable.loading, R.drawable.default_avator, 1, 0, holder.ivPhoto);

        holder.tvUpVote.setOnClickListener(view -> {
            List<LikesObject> likes = data.getDataObject().getLikes();
            if (likes != null) {
                for (LikesObject likesObject : likes) {
                    if (userId.equals(likesObject.getLikerId()) && likesObject.isLike() == 1) {
                        onClickUpVote(true, data.getDataObject().getCommentId());
                        holder.tvUpVote.setText(String.valueOf(Integer.valueOf(holder.tvUpVote.getText().toString()) - 1));
                        return;
                    } else if (userId.equals(likesObject.getLikerId()) && likesObject.isLike() == 0) {
                        onClickUpVote(false, data.getDataObject().getCommentId());
                        holder.tvUpVote.setText(String.valueOf(Integer.valueOf(holder.tvUpVote.getText().toString()) + 1));
                        holder.tvDownVote.setText(String.valueOf(Integer.valueOf(holder.tvDownVote.getText().toString()) - 1));
                        return;
                    }
                }
            }
            onClickUpVote(false, data.getDataObject().getCommentId());
            holder.tvUpVote.setText(String.valueOf(Integer.valueOf(holder.tvUpVote.getText().toString()) + 1));

        });
        holder.tvDownVote.setOnClickListener(view -> {
            List<LikesObject> likes = data.getDataObject().getLikes();
            if (likes != null) {
                for (LikesObject likesObject : likes) {
                    if (userId.equals(likesObject.getLikerId()) && likesObject.isLike() == 0) {
                        onClickDownVote(true, data.getDataObject().getCommentId());
                        holder.tvDownVote.setText(String.valueOf(Integer.valueOf(holder.tvDownVote.getText().toString()) - 1));
                        return;
                    } else if (userId.equals(likesObject.getLikerId()) && likesObject.isLike() == 1) {
                        onClickDownVote(false, data.getDataObject().getCommentId());
                        holder.tvDownVote.setText(String.valueOf(Integer.valueOf(holder.tvDownVote.getText().toString()) + 1));
                        holder.tvUpVote.setText(String.valueOf(Integer.valueOf(holder.tvUpVote.getText().toString()) - 1));
                        return;
                    }
                }
            }
            onClickDownVote(false, data.getDataObject().getCommentId());
            holder.tvDownVote.setText(String.valueOf(Integer.valueOf(holder.tvDownVote.getText().toString()) + 1));

        });
        //        holder.tvCafeName.setText(cafe.getName());
//        holder.tvRatingAvg.setText(FormatUtils.DEFAULT_FORMATER.format(cafe.getRates().getAverage()));
////        holder.ivCafe.setActualImageResource();
//        holder.tvStoreStatus.setEnabled(cafe.isNowOpen() && cafe.getHours() != null);
//        if (cafe.getHours() != null) {
//            holder.tvStoreStatus.setText(getContext().getString(cafe.isNowOpen() ? R.string.text_open : R.string.text_close));
//            holder.tvStoreStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.store_status_color));
//            holder.tvTime.setText(cafe.getOpenTimeString());
//        } else if (Utility.isNotEmptyOrNull(cafe.getOpenTimeFormUser())) {
//            holder.tvStoreStatus.setText(getContext().getString(R.string.text_time_from_user));
//            holder.tvStoreStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
//            holder.tvTime.setText(cafe.getOpenTimeFormUser());
//        } else {
//            holder.tvStoreStatus.setText(getContext().getString(R.string.text_no_time));
//            holder.tvStoreStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
//            holder.tvTime.setText("");
//        }
//        holder.tvDistance.setText(cafe.getDisplayDistance());
//        holder.ivCafe.setBackgroundResource(0);
//        int i = (int) Utility.dpToPixel(context, 3);

    }


    public static class ItemObject {
        Comment dataObject;

        public ItemObject(Comment dataObject) {
            this.dataObject = dataObject;
        }

        public Comment getDataObject() {
            return dataObject;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvSubmitTime)
        TextView tvSubmitTime;
        @BindView(R.id.tvComment)
        TextView tvComment;
        @BindView(R.id.tvUpVote)
        TextView tvUpVote;
        @BindView(R.id.tvDownVote)
        TextView tvDownVote;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
