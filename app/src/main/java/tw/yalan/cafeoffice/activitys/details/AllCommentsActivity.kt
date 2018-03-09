package tw.yalan.cafeoffice.activitys.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import butterknife.ButterKnife
import com.grasea.grandroid.mvp.UsingPresenter
import com.mcxiaoke.koi.ext.dpToPx
import kotlinx.android.synthetic.main.activity_all_commnet.*
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.adapter.CafeCommentRecyclerAdapter
import tw.yalan.cafeoffice.adapter.base.ColorfulDividerItemDecoration
import tw.yalan.cafeoffice.api.model.Comment
import tw.yalan.cafeoffice.common.BaseActivity
import tw.yalan.cafeoffice.common.kt.bindColor
import tw.yalan.cafeoffice.common.kt.onSafeClick

@UsingPresenter(value = AllCommentsPresenter::class, singleton = false)
class AllCommentsActivity : BaseActivity<AllCommentsPresenter>() {
    companion object {
        fun newBundle(cafeId: String, comments: ArrayList<Comment>? = null): Bundle {
            var bundle = Bundle()
            if (comments != null)
                bundle.putSerializable(Config.EXTRA_DATA, comments)
            bundle.putString(Config.EXTRA_ID, cafeId)

            return bundle
        }
    }

    lateinit var adapter: CafeCommentRecyclerAdapter
    override fun initViews() {
        setContentView(R.layout.activity_all_commnet)
        ButterKnife.bind(this)
        initToolbar(toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar?.setNavigationOnClickListener { finish() }
        toolbar?.setTitle(R.string.activity_all_comment_title)
        btnPost.onSafeClick {
            getPresenter().onClickSubmit()
        }
        adapter = object : CafeCommentRecyclerAdapter(this, ArrayList(), CafeCommentRecyclerAdapter.SimpleItemConfig()) {
            override fun onClickDownVote(isCancel: Boolean, commentId: String?) {
                super.onClickDownVote(isCancel, commentId)
                getPresenter().onClickDownVote(isCancel, commentId)

            }

            override fun onClickUpVote(isCancel: Boolean, commentId: String?) {
                super.onClickUpVote(isCancel, commentId)
                getPresenter().onClickUpVote(isCancel, commentId)

            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
                ColorfulDividerItemDecoration(1.dpToPx(), R.color.colorLineGrey2.bindColor(this)))
    }

    fun updateComment(comments: ArrayList<Comment> = ArrayList()) {
        adapter.getList().clear()
        for (comment in comments) {
            adapter.getList().add(CafeCommentRecyclerAdapter.ItemObject(comment))
        }
        adapter.notifyDataSetChanged()
    }

    fun goToSubmitAComment(cafeId: String, comment: Comment?) {
        if (comment == null)
            changeToActivityForResult(PostCommentActivity::class.java, PostCommentActivity.newBundle(cafeId, false), 123)
        else
            changeToActivityForResult(PostCommentActivity::class.java, PostCommentActivity.newBundle(cafeId, true, comment), 123)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                getPresenter().loadComments()
            }
        }
    }
}