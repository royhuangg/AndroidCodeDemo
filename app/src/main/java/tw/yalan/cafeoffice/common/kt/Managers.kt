package tw.yalan.cafeoffice.common.kt

import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.api.model.User

interface ToolbarManagers {

    val toolbar: Toolbar

    var toolbarTitle: String
        get() = toolbar.title.toString()
        set(value) {
            toolbar.title = value
        }

    fun enableBackArrow(up: () -> Unit) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { up() }
    }

    fun enableHomeAsUp(up: () -> Unit) {
        toolbar.navigationIcon = createUpDrawable()
        toolbar.setNavigationOnClickListener { up() }
    }

//    fun enableMenu(id: Int = R.menu.menu_home, up: () -> Unit) {
//        toolbar.inflateMenu(id)
//        toolbar.onMenuItemClick {
//            when (it!!.itemId) {
//                R.id.index -> toolbar.ctx.startActivity<MainActivity>()
//                else -> up()
//            }
//            true
//        }
//    }

    fun hiddenMenu(redId: Int) {
        toolbar.menu.findItem(redId).isVisible = false
    }

    private fun createUpDrawable() = DrawerArrowDrawable(toolbar.context).apply { progress = 1f }

}

interface UserManager {

    val user: User


}