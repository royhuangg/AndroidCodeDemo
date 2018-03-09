package tw.yalan.cafeoffice.adapter.base;

import java.util.List;

/**
 * Created by Alan Ding on 2017/8/6.
 */

public interface MultipleSelector {
    void toggleSelection(int position);

    void clearSelections();

    int getSelectedItemCount();

    List<Integer> getSelectedItems();
}
