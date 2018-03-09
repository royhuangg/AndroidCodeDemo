package tw.yalan.cafeoffice.activitys.details

import android.os.Bundle
import com.grasea.grandroid.api.Callback
import com.grasea.grandroid.api.RemoteProxy
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.api.SelfAPI
import tw.yalan.cafeoffice.api.model.Comment
import tw.yalan.cafeoffice.api.model.DefaultResult
import tw.yalan.cafeoffice.api.model.GetCommentResponse
import tw.yalan.cafeoffice.api.model.LikeCommentRequest
import tw.yalan.cafeoffice.common.BasePresenter

class AllCommentsPresenter : BasePresenter<AllCommentsActivity>() {
    var cafeId = ""
    var canLoadOnResume = false
    var currentComment: Comment? = null
    lateinit var api: SelfAPI
    override fun onActivityCreate(extras: Bundle?) {
        api = RemoteProxy.reflect(SelfAPI::class.java, this)
        val comments = extras?.getSerializable(Config.EXTRA_DATA) as? ArrayList<Comment>
        cafeId = extras?.getString(Config.EXTRA_ID) ?: ""
        contract.initViews()
        if (comments != null) {
            contract.updateComment(comments)
        } else {
            loadComments()
        }
    }

    fun loadComments() {
        if (cafeId.isNotEmpty())
            api.getComments(ModelManager.get().getUserModel().token, cafeId)
    }

    @Callback("getComments")
    fun onGetCommnets(response: GetCommentResponse) {
        if (response.isSuccess) {
            val commentList = response.data?.comments ?: ArrayList()
            val userId = ModelManager.get().getUserModel().userId
            commentList
                    .filter { it.memberId == userId }
                    .forEach { currentComment = it }

            getContract().updateComment(commentList)
        }
    }

    override fun onActivityResume() {
        if (canLoadOnResume) {
            loadComments()
        } else {
            canLoadOnResume = true
        }
    }

    override fun onActivityPause() {
    }

    override fun onActivityDestroy() {
        super.onActivityDestroy()
    }

    fun onClickSubmit() {

        contract.goToSubmitAComment(cafeId, currentComment)
    }

    fun onClickUpVote(cancel: Boolean, commentId: String?) {
        api.likeComment(ModelManager.get().getUserModel().token,
                LikeCommentRequest(cafeId = cafeId, memberId = ModelManager.get().getUserModel().userId
                        , commentId = commentId, like = if (cancel) -1 else 1))
    }

    fun onClickDownVote(cancel: Boolean, commentId: String?) {
        api.likeComment(ModelManager.get().getUserModel().token,
                LikeCommentRequest(cafeId = cafeId, memberId = ModelManager.get().getUserModel().userId,
                        commentId = commentId, like = if (cancel) -1 else 0))

    }

    @Callback("likeComment")
    fun onLikeCommnetResponse(response: DefaultResult) {
        if (response.isSuccess) {
            //            getContract().postSuccess();
            loadComments()
        }
    }
}