package tw.yalan.cafeoffice.views

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import tw.yalan.cafeoffice.CafeOfficeApplication
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.adapter.SearchResultController
import tw.yalan.cafeoffice.model.Cafe
import java.text.DecimalFormat

/**
 * Created by Roy on 2017/11/3.
 */
@EpoxyModelClass(layout = R.layout.row_search_normal_item)
abstract class SearchItemView : EpoxyModelWithHolder<SearchItemView.SearchItemHolder>() {
    @EpoxyAttribute
    var cafe: Cafe? = null
    @EpoxyAttribute
    var queryStr: String? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var callback: SearchResultController.CallBack? = null

    val df = DecimalFormat("0.0")

    override fun bind(holder: SearchItemHolder?) {
        val context = CafeOfficeApplication.getInstance()
        holder?.layoutItem?.visibility = View.VISIBLE
        holder?.layoutItem?.setOnClickListener { callback?.onClickCafe(cafe) }
        val tvName = holder?.tvCafeName
        val name = cafe?.name
        val sb = SimplifySpanBuild()
        val indexOf = name?.toLowerCase()?.indexOf(queryStr?.toLowerCase() ?: "") ?: 0
//        Config.loge(name + ": query:" + queryStr + " , indexOf:" + indexOf) 
        if (indexOf != -1) {
            if (indexOf > 1) {
                sb.append(SpecialTextUnit(name?.substring(0, indexOf), ContextCompat.getColor(context, R.color.colorTextBlack)))
            }
            val queryStringLength = queryStr?.length ?: 0
            sb.append(SpecialTextUnit(name?.substring(indexOf, indexOf + queryStringLength), ContextCompat.getColor(context, R.color.colorPrimary)))
                    .append(SpecialTextUnit(name?.substring(indexOf + queryStringLength), ContextCompat.getColor(context, R.color.colorTextBlack)))
            tvName?.text = sb.build()
        } else {
            sb.append(SpecialTextUnit(name, ContextCompat.getColor(context, R.color.colorTextBlack)))
            tvName?.text = sb.build()

        }
        holder?.tvDistance?.text = cafe?.displayDistance
        if (cafe?.rateAverage != null)
            holder?.tvRating?.text = df.format(cafe?.rateAverage ?: 0.0)
        else
            holder?.tvRating?.text = "0.0"

    }

    override fun unbind(holder: SearchItemHolder?) {
//        holder?.itemView?.setOnClickListener(null)
    }


    class SearchItemHolder : BaseEpoxyHolder() {
        @BindView(R.id.tv_cafe_name)
        lateinit var tvCafeName: TextView
        @BindView(R.id.tv_rating)
        lateinit var tvRating: TextView
        @BindView(R.id.tv_distance)
        lateinit var tvDistance: TextView
        @BindView(R.id.tv_distance_hint)
        lateinit var tvDistanceHint: TextView
        @BindView(R.id.layout_item)
        lateinit var layoutItem: RelativeLayout

    }
}
