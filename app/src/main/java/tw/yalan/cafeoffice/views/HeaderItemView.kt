package tw.yalan.cafeoffice.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView

import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView

import butterknife.BindView
import butterknife.ButterKnife
import tw.yalan.cafeoffice.R

@ModelView(defaultLayout = R.layout.model_search_header_item_view)
class HeaderItemView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    @BindView(R.id.tvTitle)
    lateinit var tvTitle: TextView

    init {
        init()
    }

    private fun init() {
        inflate(context, R.layout.row_search_header, this)
        ButterKnife.bind(this)
    }

    @ModelProp
    fun setTitle(title: String) {
        tvTitle.text = title
    }
}
