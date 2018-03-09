package tw.yalan.cafeoffice.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.NativeExpressAdView

import butterknife.BindView
import butterknife.ButterKnife
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.adapter.SearchResultController

@ModelView(defaultLayout = R.layout.model_ad_item_view)
class AdItemView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    @BindView(R.id.native_ad)
    lateinit var adView: NativeExpressAdView

    internal var callback: SearchResultController.CallBack? = null

    init {
        init()
    }

    private fun init() {
        inflate(context, R.layout.view_native_ad, this)
        ButterKnife.bind(this)
        val adRequest = AdRequest.Builder().addTestDevice("912AF21E051492C22CE5C377D624164C").build()
        adView.loadAd(adRequest)
        adView.setOnClickListener { view ->
            if (callback != null) {
                callback?.onClickAd()
            }
        }
    }

    @ModelProp(options = arrayOf(ModelProp.Option.DoNotHash))
    fun setCallback(callback: SearchResultController.CallBack) {
        this.callback = callback
    }

}
