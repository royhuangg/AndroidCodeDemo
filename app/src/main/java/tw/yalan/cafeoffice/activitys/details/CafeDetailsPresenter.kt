package tw.yalan.cafeoffice.activitys.details

import android.content.Context
import android.os.Bundle

import com.google.gson.Gson
import com.grasea.grandroid.api.Callback
import com.grasea.grandroid.api.RemoteProxy
import com.grasea.grandroid.api.RequestFail

import net.gotev.uploadservice.MultipartUploadRequest
import net.gotev.uploadservice.ServerResponse
import net.gotev.uploadservice.UploadInfo
import net.gotev.uploadservice.UploadNotificationConfig
import net.gotev.uploadservice.UploadStatusDelegate

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Response
import tw.yalan.cafeoffice.BuildConfig
import tw.yalan.cafeoffice.CafeOfficeApplication
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.api.SelfAPI
import tw.yalan.cafeoffice.api.model.CafeDetails
import tw.yalan.cafeoffice.api.model.CafeDetailsResponse
import tw.yalan.cafeoffice.api.model.Comment
import tw.yalan.cafeoffice.api.model.CustomDataResponse
import tw.yalan.cafeoffice.api.model.DefaultResult
import tw.yalan.cafeoffice.api.model.FavoriteCafeData
import tw.yalan.cafeoffice.api.model.GetCommentResponse
import tw.yalan.cafeoffice.api.model.LikeCommentRequest
import tw.yalan.cafeoffice.api.model.Photo
import tw.yalan.cafeoffice.api.model.PostCommentRequest
import tw.yalan.cafeoffice.api.model.Rate
import tw.yalan.cafeoffice.api.model.User
import tw.yalan.cafeoffice.api.model.UserAndCafeRequest
import tw.yalan.cafeoffice.api.model.VisitCafeData
import tw.yalan.cafeoffice.common.BasePresenter
import tw.yalan.cafeoffice.model.Cafe
import tw.yalan.cafeoffice.model.Tag
import tw.yalan.cafeoffice.utils.Utility


class CafeDetailsPresenter : BasePresenter<CafeDetailsActivity>() {
    var cafe: Cafe? = null
    var details: CafeDetails? = null
    val selfAPI: SelfAPI by lazy { RemoteProxy.reflect(SelfAPI::class.java, this) }
    var myRate: Rate? = null
    /**
     * The All photos.
     */
    internal var allPhotos: ArrayList<Photo>? = null
    internal var commentArrayList: ArrayList<Comment>? = null
    internal var tagList = ArrayList<Tag>()

    internal var currentComment: Comment? = null
    internal var rates: ArrayList<Rate>? = null

    override fun onSaveInstanceState(outState: Bundle?) {
        if (outState != null) {
            outState.putSerializable("tagList", tagList)
            if (details != null)
                outState.putSerializable("detail", details)
            if (cafe != null)
                outState.putSerializable("cafe", cafe)
            if (currentComment != null)
                outState.putSerializable("currentComment", currentComment)
            if (myRate != null)
                outState.putSerializable("myRate", myRate)

        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(outState: Bundle) {
        super.onRestoreInstanceState(outState)
        restoreState(outState)
    }

    fun restoreState(bundle: Bundle?) {
        if (bundle == null) {
            return
        }
        tagList = bundle.getSerializable("tagList") as ArrayList<Tag>
        if (bundle.containsKey("detail"))
            details = bundle.getSerializable("detail") as CafeDetails
        if (bundle.containsKey("cafe"))
            cafe = bundle.getSerializable("cafe") as Cafe
        if (bundle.containsKey("currentComment"))
            currentComment = bundle.getSerializable("currentComment") as Comment
        if (bundle.containsKey("myRate"))
            myRate = bundle.getSerializable("myRate") as Rate
    }

    override fun onActivityCreateWithState(extras: Bundle?, saveState: Bundle?) {
        restoreState(saveState)
    }

    override fun onActivityCreate(extras: Bundle?) {
        cafe = extras?.getSerializable(Config.EXTRA_DATA) as Cafe
        getContract().initViews(cafe)
        val (_, _, _, _, _, _, _, _, _, _, _, favorites) = ModelManager.get().loadUser()
        for (favorite in favorites!!) {
            if (cafe?.id == favorite.id) {
                getContract().updateFavoriteStatus(true)
                break
            }
        }
        tagList = ModelManager.get().tagList
        selfAPI.getCafeDetails(ModelManager.get().getUserModel().token, cafe?.id)
        loadComments()
        loadAllPhotos()
        //        selfAPI.getRates(ModelManager.get().getUserModel().getToken(), cafe.getId(), ModelManager.get().getUserModel().getUserId());
    }

    fun loadComments() {
        selfAPI.getComments(ModelManager.get().getUserModel().token, cafe?.id)

    }

    @Callback("likeComment")
    fun onLikeCommnetResponse(response: DefaultResult) {
        if (response.isSuccess) {
            //            getContract().editSuccess();
            loadComments()
        }
    }

    @Callback("postComment")
    fun onPostCommnetResponse(response: DefaultResult) {
        if (response.isSuccess) {
            getContract().postSuccess()
            loadComments()
        }
    }


    @Callback("getComments")
    fun onGetCommnets(response: GetCommentResponse) {
        if (response.isSuccess) {
            val (id) = ModelManager.get().loadUser()
            commentArrayList = response.data?.comments
            if (id?.isEmpty() == false) {
                for (comment in response.data?.comments!!) {
                    if (comment.memberId == id) {
                        currentComment = comment
                        getContract().updateComments(true, response.data!!)
                        return
                    }
                }
            }
            getContract().updateComments(false, response.data!!)
        }
    }


    @Callback("getCafeDetails")
    fun onGetCafeDetails(response: CafeDetailsResponse) {
        if (response.isSuccess) {
            details = response.details
            rates = details?.rates
            if (Utility.isNotEmptyOrNull(rates)) {
                getContract().updateRateStatus(false, rates?.size ?: 0)
                for (rate in rates!!) {
                    if (rate.member_id == ModelManager.get().getUserModel().userId) {
                        myRate = rate
                        ModelManager.get().setRate(rate)
                        getContract().updateRateStatus(true, rates?.size ?: 0)
                        break
                    }
                }
                val (_, _, _, _, _, _, _, _, _, _, _, favorites, _, visits) = ModelManager.get().loadUser()
                getContract().updateFavoriteStatus(false, details?.favoriteCount ?: 0)
                if (favorites != null) {
                    for (favorite in favorites) {
                        if (favorite.id == details?.id) {
                            getContract().updateFavoriteStatus(true, details?.favoriteCount ?: 0)
                            break
                        }
                    }
                }
                getContract().updateVisitStatus(false, details?.visitCount ?: 0)
                for (visit in visits!!) {
                    if (visit.id == details!!.id) {
                        getContract().updateVisitStatus(true, details?.visitCount ?: 0)
                        break
                    }
                }

            } else {
                getContract().updateRateStatus(false, 0)
            }
            val currentTags = ArrayList<Tag>()
            if (details?.tags != null) {
                for (id in details?.tags!!) {
                    val tag = findTag(id) ?: continue
                    currentTags.add(tag)
                }
            }
            getContract().updateDetails(cafe, details!!, myRate, currentTags)


        } else {

        }
    }

    private fun findTag(tagId: String): Tag? {
        for (tag in tagList) {
            if (tag.id == tagId) {
                return tag
            }
        }
        return null
    }

    @RequestFail
    fun onRequestFailed(methodName: String, t: Throwable) {
        Config.loge(t)
    }

    override fun onActivityResume() {

    }

    override fun onActivityPause() {

    }

    override fun onActivityDestroy() {
        super.onActivityDestroy()
        ModelManager.get().setRate(null)
    }

    fun onClickFbIcon() {
        val url = cafe?.url
        if (Utility.isNotEmptyOrNull(url) && url?.contains("facebook") == true) {
            getContract().goToFacebookPage(url)
        }
    }

    fun onClickWatchStreet() {
        getContract().goToWatchStreetView(cafe)
    }

    fun onClickUpVote(isCancel: Boolean, commentId: String) {
        selfAPI.likeComment(ModelManager.get().getUserModel().token,
                LikeCommentRequest(cafe?.id, ModelManager.get().getUserModel().userId, commentId, if (isCancel) -1 else 1))
    }

    fun onClickDownVote(isCancel: Boolean, commentId: String) {
        selfAPI.likeComment(ModelManager.get().getUserModel().token,
                LikeCommentRequest(cafe?.id, ModelManager.get().getUserModel().userId, commentId, if (isCancel) -1 else 0))
    }

    fun postComment(s: String) {
        selfAPI.postComment(ModelManager.get().getUserModel().token, PostCommentRequest(cafe?.id, ModelManager.get().getUserModel().userId, s))
    }

    fun onClickFavorite(isAdd: Boolean) {
        val user = ModelManager.get().loadUser()
        val customDataResponseCall: Call<CustomDataResponse<FavoriteCafeData>>
        if (isAdd) {
            customDataResponseCall = selfAPI.addFavorite(ModelManager.get().getUserModel().token,
                    UserAndCafeRequest(cafe?.id, user.id))
        } else {
            customDataResponseCall = selfAPI.deleteFavorite(ModelManager.get().getUserModel().token,
                    cafe?.id, user.id)
        }

        customDataResponseCall.enqueue(object : retrofit2.Callback<CustomDataResponse<FavoriteCafeData>> {
            override fun onResponse(call: Call<CustomDataResponse<FavoriteCafeData>>, response: Response<CustomDataResponse<FavoriteCafeData>>) {
                if (response.isSuccessful && response.body() != null && response.body()?.isSuccess == true) {
                    if (isAdd) {
                        user.favorite_count = user.favorite_count + 1
                        user.favorites?.add(cafe!!)
                        ModelManager.get().updateUser(user)
                        if (details != null) {
                            details?.favoriteCount = (details?.favoriteCount ?: 0) + 1
                            getContract().updateFavoriteStatus(true, details?.favoriteCount ?: 0)
                        }

                    } else {
                        user.favorite_count = user.favorite_count - 1
                        val it = user.favorites?.iterator()
                        while (it?.hasNext() == true) {
                            val nextCafe = it.next()
                            if (nextCafe.id == cafe?.id) {
                                it.remove()
                            }
                        }
                        ModelManager.get().updateUser(user)
                        details?.favoriteCount = (details?.favoriteCount ?: 1) - 1
                        getContract().updateFavoriteStatus(false, details?.favoriteCount ?: 0)

                    }
                }
            }

            override fun onFailure(call: Call<CustomDataResponse<FavoriteCafeData>>, t: Throwable) {
                getContract().networkNotFound()
            }
        })
    }

    fun onClickVisit(isAdd: Boolean) {
        val user = ModelManager.get().loadUser()

        var customDataResponseCall: Call<CustomDataResponse<VisitCafeData>>? = null
        if (isAdd) {
            customDataResponseCall = selfAPI.addVisit(ModelManager.get().getUserModel().token,
                    UserAndCafeRequest(cafe?.id, user.id))
        } else {
            customDataResponseCall = selfAPI.deleteVisit(ModelManager.get().getUserModel().token,
                    cafe?.id, user.id)
        }

        customDataResponseCall?.enqueue(object : retrofit2.Callback<CustomDataResponse<VisitCafeData>> {
            override fun onResponse(call: Call<CustomDataResponse<VisitCafeData>>, response: Response<CustomDataResponse<VisitCafeData>>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.isSuccess == true) {
                        if (isAdd) {
                            user.visit_count = user.visit_count + 1
                            user.visits?.add(cafe!!)
                            ModelManager.get().updateUser(user)
                            details?.visitCount = (details?.visitCount ?: 0) + 1
                            getContract().updateVisitStatus(true, details?.visitCount ?: 0)

                        } else {
                            details?.visitCount = (details?.visitCount ?: 0) - 1
                            removeUserVisit()

                            getContract().updateVisitStatus(false, details?.visitCount ?: 0)

                        }
                    } else if (response.body()?.errorCode == 20044) {
                        //Cafe already visit.
                        onClickVisit(false)
                    }
                }
            }

            override fun onFailure(call: Call<CustomDataResponse<VisitCafeData>>, t: Throwable) {
                getContract().networkNotFound()
            }
        })
    }

    private fun removeUserVisit() {
        val user = ModelManager.get().loadUser()
        user.visit_count = user.visit_count - 1
        val it = user.visits?.iterator()
        while (it?.hasNext() == true) {
            val nextCafe = it.next()
            if (nextCafe.id == cafe?.id) {
                it.remove()
            }
        }
        ModelManager.get().updateUser(user)
    }

    fun onClickMorePhoto() {
        if (details == null) {
            return
        }
        getContract().goToGallery(details?.name ?: "", details?.id, allPhotos)
        //        Observable.fromIterable(allPhotos)
        //                .map(photo -> {
        //                    if (photo.getThumbnail() == null) {
        //                        return photo.getUrl() != null ? photo.getUrl() : "";
        //                    }
        //                    return photo.getThumbnail();
        //                })
        //                .toList()
        //                .doOnError(throwable -> Config.loge(throwable))
        //                .subscribe(strings -> {
        //                    getContract().goToGallery(details.getName(), details.getId(), new ArrayList<>(strings));
        //                });
    }


    fun loadAllPhotos() {
        val photosRequest = selfAPI.getPhotos(ModelManager.get().getUserModel().token, cafe?.id)
        photosRequest.enqueue(object : retrofit2.Callback<CustomDataResponse<ArrayList<Photo>>> {
            override fun onResponse(call: Call<CustomDataResponse<ArrayList<Photo>>>, response: Response<CustomDataResponse<ArrayList<Photo>>>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.isSuccess == true) {
                        allPhotos = response.body()?.data
                    }
                }
            }

            override fun onFailure(call: Call<CustomDataResponse<ArrayList<Photo>>>, t: Throwable) {

            }
        })
    }

    fun startUpload(path: String) {
        try {
            val uploadId = MultipartUploadRequest(CafeOfficeApplication.getInstance(), BuildConfig.API_URL_SELF + "v" + BuildConfig.API_VER_SELF + "/photos")
                    // starting from 3.1+, you can also use content:// URI string instead of absolute file
                    .addFileToUpload(path, "photo1")
                    .addParameter("member_id", ModelManager.get().getUserModel().userId)
                    .addParameter("cafe_id", cafe?.id)
                    .setNotificationConfig(UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(object : UploadStatusDelegate {

                        override fun onProgress(context: Context, uploadInfo: UploadInfo) {

                        }

                        override fun onError(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse, exception: Exception) {

                        }

                        override fun onCompleted(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse) {
                            val bodyAsString = serverResponse.bodyAsString
                            val contract = getContract()

                            if (bodyAsString != null) {
                                val gson = Gson()
                                val defaultResult = gson.fromJson(bodyAsString, DefaultResult::class.java)
                                if (defaultResult.isSuccess) {
                                    contract?.uploadSuccess()
                                } else {
                                    contract?.uploadFailed(defaultResult.getMessage())
                                }
                            } else {
                                contract?.uploadFailed("")
                            }

                        }

                        override fun onCancelled(context: Context, uploadInfo: UploadInfo) {

                        }
                    })
                    .startUpload()

        } catch (exc: Exception) {
            Config.loge(exc)
        }

    }

    fun reloadDetails() {
        selfAPI.getCafeDetails(ModelManager.get().getUserModel().token, cafe?.id ?: "")
    }

    fun onClickMoreComment() {
        getContract().goToMoreComment(cafe?.id ?: "", commentArrayList)
    }

    fun closeReport() {
        val close = selfAPI.close(ModelManager.get().getUserModel().token, UserAndCafeRequest(cafe?.id, ModelManager.get().getUserModel().userId))
        close.enqueue(object : retrofit2.Callback<DefaultResult> {
            override fun onResponse(call: Call<DefaultResult>, response: Response<DefaultResult>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.isSuccess == true)
                        getContract().closeReportSuccess()
                }
            }

            override fun onFailure(call: Call<DefaultResult>, t: Throwable) {

            }
        })
    }

    fun updateMyRate(rate: Rate) {
        val noRate = rates?.size == 0
        if (noRate) {
            myRate = rate
            rates?.add(rate)
            getContract().updateRateStatus(true, rates?.size ?: 0)
        } else {
            if (myRate != null) {
                rates?.remove(rate)
                rates?.add(rate)
                myRate = rate
            } else {
                rates?.add(rate)
                myRate = rate
            }
            getContract().updateRateStatus(true, rates?.size ?: 0)
        }
    }

    fun onClickToComment() {
        getContract().goToPostComment(cafe?.id ?: "", currentComment)
    }

    fun onClickEditCafe() {
        getContract().goToEditCafe(details!!)
    }
}
