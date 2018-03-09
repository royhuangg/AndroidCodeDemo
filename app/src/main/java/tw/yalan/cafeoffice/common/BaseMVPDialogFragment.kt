package tw.yalan.cafeoffice.common

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grasea.grandroid.app.GrandroidApplication
import com.grasea.grandroid.mvp.GrandroidPresenter
import com.grasea.grandroid.mvp.UsingPresenter
import kotlin.reflect.KClass

/**
 * Created by Yalan Ding on 2017/5/7.
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseMVPDialogFragment<P : BaseDialogFragmentPresenter<*>> : DialogFragment() {
    var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val presenterClass: Class<*> = this.javaClass.getAnnotation(UsingPresenter::class.java).value.java
        if (activity.application is GrandroidApplication) {
//            val app = activity.application as GrandroidApplication
//            val singleton = this.javaClass.getAnnotation(UsingPresenter::class.java).singleton
            presenter = null
            if (presenter == null) {
                try {
                    presenter = presenterClass.newInstance() as P
//                    if (singleton) {
//                        app.putPresenter(presenter)
//                    }
                } catch (e: java.lang.InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

            }
        } else {
            try {
                presenter =  presenterClass.newInstance() as P
            } catch (e: java.lang.InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
        presenter?.contract = this
    }

    abstract fun getDialogResourceId(): Int

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(getDialogResourceId(), container)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.onDialogCreateView(arguments, savedInstanceState)
    }
    abstract fun initView(savedInstanceState: Bundle?, vararg args: Any?)

}

abstract class BaseDialogFragmentPresenter<C> : GrandroidPresenter<C>() {
   abstract fun onDialogCreateView(args: Bundle?, savedInstanceState: Bundle?)
}





class TestPresenter : BaseDialogFragmentPresenter<TestDialog>() {
    override fun onDialogCreateView(args: Bundle?, savedInstanceState: Bundle?) {

    }

}

@UsingPresenter(value = TestPresenter::class, singleton = false)
class TestDialog : BaseMVPDialogFragment<TestPresenter>() {
    override fun initView(savedInstanceState: Bundle?, vararg args: Any?) {

    }

    override fun getDialogResourceId(): Int {
        return 0

    }

    fun newInstance(): TestDialog {
        return TestDialog()
    }

}