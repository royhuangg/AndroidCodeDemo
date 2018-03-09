package tw.yalan.cafeoffice.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.grasea.grandroid.adapter.GrandroidRecyclerAdapter;
import com.grasea.grandroid.adapter.OnClickable;
import com.grasea.grandroid.adapter.RecyclerItemConfig;

import java.util.ArrayList;

/**
 * Created by Alan Ding on 2017/7/2.
 */

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends GrandroidRecyclerAdapter<T, VH> {

    public abstract static class OnItemClickListener<T, VH extends RecyclerView.ViewHolder> implements OnClickable<T, VH> {
    }

    private OnItemClickListener<T, VH> itemClickListener;
    private Context context;

    public BaseRecyclerAdapter(Context context, ArrayList<T> list, RecyclerItemConfig<? extends RecyclerView.ViewHolder> recyclerItemConfig) {
        super(list, recyclerItemConfig);
        this.context = context;
    }

    @Override
    public void onItemClick(VH holder, int index, T item) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(holder, index, item);
        }
    }

    public OnItemClickListener<T, VH> getItemClickListener() {
        return itemClickListener;
    }

    public BaseRecyclerAdapter setItemClickListener(OnItemClickListener<T, VH> itemClickListener) {
        this.itemClickListener = itemClickListener;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public BaseRecyclerAdapter setContext(Context context) {
        this.context = context;
        return this;
    }
}
