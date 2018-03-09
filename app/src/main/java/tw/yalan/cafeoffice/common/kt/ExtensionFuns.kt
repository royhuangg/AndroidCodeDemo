package tw.yalan.cafeoffice.common.kt

import android.content.Context
import android.support.annotation.IntRange
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_post_cafe.*
import java.util.concurrent.TimeUnit

fun CharSequence?.isNotNullOrEmpty(): Boolean = (this != null && this.isNotEmpty())

fun CharSequence.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun Context.getStringByName(name: String): String {
    val res = getResources()
    var resId = res.getIdentifier(name, "string", packageName)
    return if (resId == 0) "" else res.getString(resId)
}

fun View.onSafeClick(@IntRange(from = 1) seconds: Long = 2, onNext: (View) -> Unit) {
    RxView.clicks(this)
            .throttleFirst(seconds, TimeUnit.SECONDS)
            .subscribe({
                onNext.invoke(this)
            })
}

fun Int.bindColor(context: Context): Int = ContextCompat.getColor(context, this)

fun Int.bindDimenFloat(context: Context): Float = context.resources.getDimension(this)
fun Int.bindDimenInt(context: Context): Int = context.resources.getDimensionPixelOffset(this)
fun Int.bindString(context: Context, vararg args: Any): String = context.getString(this, args)
fun Int.bindString(context: Context): String = context.getString(this)
