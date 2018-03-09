package tw.yalan.cafeoffice.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.grasea.grandroid.adapter.OnClickable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.List;

import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.api.model.Photo;
import tw.yalan.cafeoffice.utils.transition.MediaSharedElementCallback;

/**
 * Created by Alan Ding on 2017/3/30.
 */

public class ImagePagerAdapter extends PagerAdapter implements OnClickable<ImagePagerAdapter.ItemObject, ImagePagerAdapter.ViewHolder> {
    Context context;
    List<ItemObject> list;
    int defaultIndex;
    private boolean mDontAnimate;
    private final MediaSharedElementCallback mSharedElementCallback;

    public ImagePagerAdapter(Context context, List<ItemObject> list, int defaultIndex, MediaSharedElementCallback mSharedElementCallback) {
        this.context = context;
        this.list = list;
        this.defaultIndex = defaultIndex;
        this.mSharedElementCallback = mSharedElementCallback;
    }

    public List<ItemObject> getList() {
        return list;
    }

    @Override
    public void onItemClick(ViewHolder holder, int index, ItemObject item) {

    }

    public void setList(List<ItemObject> list) {
        this.list = list;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView view = null;
//        HackyProblematicViewGroup layout = new HackyProblematicViewGroup(getContext());
        view = new PhotoView(getContext());
//        layout.addView(view);


        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ViewHolder holder = new ViewHolder(view);
        ItemObject itemObject = list.get(position);
        view.setOnClickListener(v -> onItemClick((ViewHolder) v.getTag(2), position, itemObject));
        view.setTag(R.id.tag_glide_id, holder);
        ViewCompat.setTransitionName(view, itemObject.dataObject.getUrl());

//        int i = (int) Utility.dpToPixel(context, 6);

//        ImageHelper.loadSquaredImage(context, itemObject.dataObject, R.drawable.loading, R.drawable.no_image, 0, i, holder.ivPhoto);
        PhotoView finalView = view;
        String path = itemObject.dataObject.getUrl();
        if (path != null && !path.isEmpty()) {
            RequestBuilder<Drawable> requestBuilder;
            if (!path.startsWith("http")) {
                requestBuilder = Glide.with(context).load(Uri.fromFile(new File(path)));

            } else {
                requestBuilder = Glide.with(context).load(path);

            }
            requestBuilder.listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Config.loge(e);
                    if (position == defaultIndex) {
                        Config.loge("startTransitsion:" + position);
                        //初始化当前显示的ImageView时，设置一个callback开启转场动画
                        Config.loge("transitionName:" + itemObject.dataObject);

                        setStartPostTransition(finalView);
                    }
                    return true;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.ivPhoto.setImageDrawable(resource);
                    if (position == defaultIndex) {
                        Config.loge("startTransitsion:" + position);
                        //初始化当前显示的ImageView时，设置一个callback开启转场动画
                        Config.loge("transitionName:" + itemObject.dataObject);

                        setStartPostTransition(finalView);
                    }
                    return true;
                }
            }).into(holder.ivPhoto);

        }


        container.addView(view);
        return view;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof ViewHolder) {
            mSharedElementCallback.setSharedElementViews(((ViewHolder) object).ivPhoto);
        }
    }

    private void setStartPostTransition(final View sharedView) {
        sharedView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    // @Override
                    public boolean onPreDraw() {
                        sharedView.getViewTreeObserver().removeOnPreDrawListener(this);
                        onStartPostponedEnterTransition();

                        return true;
                    }
                });
    }

    public ImagePagerAdapter setmDontAnimate(boolean mDontAnimate) {
        this.mDontAnimate = mDontAnimate;
        return this;
    }

    public void onStartPostponedEnterTransition() {

    }

    public static class ItemObject {
        Photo dataObject;

        public ItemObject(Photo dataObject) {
            this.dataObject = dataObject;
        }

        public Photo getDataObject() {
            return dataObject;
        }
    }

    public static class ViewHolder {
        PhotoView ivPhoto;

        public ViewHolder(PhotoView itemView) {
            this.ivPhoto = itemView;
        }
    }

}
