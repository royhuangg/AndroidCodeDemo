package tw.yalan.cafeoffice.adapter

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyViewHolder
import com.airbnb.epoxy.TypedEpoxyController
import tw.yalan.cafeoffice.listener.EndlessRecyclerViewScrollListener
import tw.yalan.cafeoffice.model.Cafe
import tw.yalan.cafeoffice.views.*

/**
 * Created by Roy on 2017/11/3.
 */
class SearchResultController(listener: EndlessRecyclerViewScrollListener, callback: CallBack) : TypedEpoxyController<List<SearchResultController.BaseObject>>() {

    var queryString: String? = null
    val listener = listener
    val callback = callback

    interface CallBack {
        fun onClickAd()
        fun onClickCafe(cafe: Cafe?)
    }
//    @AutoModel
//    lateinit var headerItemViewModel: HeaderItemViewModel_
//    @AutoModel
//    lateinit var loadMoreViewModel: LoadMoreViewModel_

    override fun buildModels(data: List<SearchResultController.BaseObject>?) {

        data?.forEachIndexed { index, it ->
            when (it) {
                is TitleObject -> {
                    HeaderItemViewModel_()
                            .id(index)
                            .title(it.title)
                            .addTo(this)
                }
                is ItemObject -> {
                    if (it.dataObject == null) {
                        //ad view
                        AdItemViewModel_()
                                .id(index)
                                .callback(callback)
                                .addTo(this)
                    } else {
                        SearchItemView_()
                                .id(index)
                                .cafe(it.dataObject)
                                .queryStr(queryString)
                                .callback(callback)
                                .addTo(this)
                    }
                }
            }
        }

        LoadMoreViewModel_()
                .id(data?.size)
                .addIf(listener.hasMoreToLoad(), this)
    }


    open class BaseObject

    class TitleObject(title: String) : BaseObject() {
        val title: String = title
    }

    class ItemObject(dataObject: Cafe? = null) : BaseObject() {
        val dataObject: Cafe? = dataObject
    }
}