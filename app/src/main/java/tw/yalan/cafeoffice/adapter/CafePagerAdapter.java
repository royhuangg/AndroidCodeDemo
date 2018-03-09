package tw.yalan.cafeoffice.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.grasea.grandroid.adapter.OnClickable;

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
 * Created by Alan Ding on 2017/3/30.
 */

public class CafePagerAdapter extends PagerAdapter implements OnClickable<CafePagerAdapter.ItemObject, CafePagerAdapter.ViewHolder> {
    Context context;
    List<ItemObject> list;
    int size;
    ArrayList<Tag> tagArrayList = new ArrayList<>();

    public CafePagerAdapter(Context context, List<ItemObject> list) {
        this.context = context;
        this.list = list;
        size = (int) Utility.dpToPixel(context, 64);
        tagArrayList = ModelManager.get().getTagList();
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
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.pager_cafe, null);
        ViewHolder holder = new ViewHolder(view);
        ItemObject itemObject = list.get(position);
        Cafe cafe = itemObject.dataObject;
        view.setOnClickListener(v -> onItemClick((ViewHolder) v.getTag(), position, itemObject));
        view.setTag(holder);

        holder.tvCafeName.setText(itemObject.dataObject.getName());
        holder.tvCafeName.setSelected(true);
        holder.tvDistance.setText(itemObject.dataObject.getDisplayDistance());
        if(itemObject.dataObject.getRateAverage()!=null){
            holder.tvRatingAvg.setText(FormatUtils.DEFAULT_FORMATER.format(itemObject.dataObject.getRateAverage()));
        }else{
            holder.tvRatingAvg.setText("0.0");
        }
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
        int tagCount = 0;
        for (int i = 0; i < cafe.getTags().size(); i++) {
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
//            else if (tagCount == 2) {
//                Config.loge("Add Tag3:" + tag.getTag_name());
//                holder.tag3.setTagText(tag.getTag_name());
//                holder.tag3.setVisibility(View.VISIBLE);
//
//                break;
//            }
            tagCount += 1;
        }
        int i = (int) Utility.dpToPixel(context, 6);

        ImageHelper.loadSquaredImage(context, cafe.getCovers().getLargePic(), R.drawable.loading, R.drawable.no_image, 0, i, holder.ivCafe);

        container.setClipToPadding(false);
        container.setClipChildren(false);
        container.addView(view);
        return view;
    }

    private Tag findTag(String tagId) {
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

    public static class ViewHolder {
        @BindView(R.id.tv_rating_avg)
        AppCompatTextView tvRatingAvg;
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
        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.tag1)
        TagView tag1;
        @BindView(R.id.tag2)
        TagView tag2;
        @BindView(R.id.tag3)
        TagView tag3;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}
