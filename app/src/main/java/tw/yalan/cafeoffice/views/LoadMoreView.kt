package tw.yalan.cafeoffice.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

import com.airbnb.epoxy.ModelView

import butterknife.ButterKnife
import tw.yalan.cafeoffice.R

@ModelView(defaultLayout = R.layout.model_loadmore_item_view)
class LoadMoreView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    init {
        init()
    }

    private fun init() {
        inflate(context, R.layout.view_loadmore_item, this)
        ButterKnife.bind(this)
    }
}
