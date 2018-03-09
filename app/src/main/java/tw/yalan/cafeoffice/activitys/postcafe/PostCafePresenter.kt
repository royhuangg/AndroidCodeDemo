package tw.yalan.cafeoffice.activitys.postcafe

import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.google.android.gms.common.api.PendingResult
import com.grasea.grandroid.api.Callback
import com.grasea.grandroid.api.RemoteProxy
import tw.yalan.cafeoffice.api.SelfAPI
import tw.yalan.cafeoffice.api.model.BusinessHour
import tw.yalan.cafeoffice.api.model.GetTagListResponse

import tw.yalan.cafeoffice.common.BasePresenter
import tw.yalan.cafeoffice.model.NewCafeObject
import tw.yalan.cafeoffice.model.Tag
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.AutocompletePredictionBuffer
import tw.yalan.cafeoffice.playservices.GoogleClientHandler
import com.google.android.gms.location.places.AutocompleteFilter
import com.luck.picture.lib.entity.LocalMedia
import net.gotev.uploadservice.*
import tw.yalan.cafeoffice.BuildConfig
import tw.yalan.cafeoffice.CafeOfficeApplication
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.api.model.PostCafeResponse
import tw.yalan.cafeoffice.api.model.Rate
import tw.yalan.cafeoffice.common.City


/**
 * Created by Yalan Ding on 2017/7/15.
 */

class PostCafePresenter : BasePresenter<PostCafeActivity>() {

    var api: SelfAPI = RemoteProxy.reflect(SelfAPI::class.java, this)
    lateinit var newCafe: NewCafeObject
    var tempCafeList: PendingResult<AutocompletePredictionBuffer>? = null
    var selectedTagList: ArrayList<String> = ArrayList()
    var tempHour: BusinessHour? = null
    var selectedPhotos: ArrayList<String> = ArrayList()
    var tempSelectList: MutableList<LocalMedia>? = null
    override fun onActivityCreate(extras: Bundle?) {
        newCafe = NewCafeObject()
        getContract().initViews()
        api.getTagList(ModelManager.get().getUserModel().token)
    }

    @Callback("getTagList")
    fun onGetTagListResponse(response: GetTagListResponse) {
        if (response.isSuccess) {
            getContract().updateTagList(response.tags)
        } else {

        }
    }

    override fun onActivityResume() {

    }

    override fun onActivityPause() {

    }

    override fun onActivityDestroy() {

    }

    fun onTagSelectChanged(tag: Tag, selected: Boolean) {
        if (selected) {
            selectedTagList.add(tag.id)
        } else {
            selectedTagList.remove(tag.id)
        }
    }

    fun onTimeSet(data: BusinessHour) {
        tempHour = data
        newCafe.businessHour = data.business_hour
    }

    fun searchPlace(text: String) {
        val typeFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build()

        if (GoogleClientHandler.getInstance().googleApiClient != null && GoogleClientHandler.getInstance().isConnected) {
            tempCafeList = Places.GeoDataApi
                    .getAutocompletePredictions(GoogleClientHandler.getInstance().googleApiClient, text,
                            null, typeFilter)
        }
    }

    fun onRateConfirm(rate: Rate?) {
        rate?.member_id = null
        newCafe.rates = rate
    }

    fun onPlaceChoose(latitude: Double, longitude: Double, city: String?, address: CharSequence?, name: CharSequence?, websiteUri: Uri?) {
        newCafe.latitude = latitude
        newCafe.longitude = longitude
        val cityObj = City.valueOfByShortName(city)
        if (cityObj != City.UNKNOW)
            newCafe.city = cityObj.name.toLowerCase()
        else {
            getContract().cityInvalid()
            return
        }
        newCafe.address = address.toString()
        newCafe.name = name.toString()
        newCafe.fbUrl = websiteUri?.toString()
    }

    fun onRadioTimeCheckedChange(time: Int) {
        newCafe.limitedTime = time.toString()
    }

    fun onRadioSocketCheckedChange(socket: Int) {
        newCafe.socket = socket.toString()
    }

    fun onStandUpCheckedChange(checked: Boolean) {
        newCafe.hasStandingDesk = if (checked) "1" else "4"
    }

    fun onAddressPick(address: String?, latitude: Double, longitude: Double) {
        newCafe.address = address
        newCafe.latitude = latitude
        newCafe.longitude = longitude
    }

    fun onClickSent(cafeName: String, phone: String, city: String) {
        if (cafeName.isNullOrEmpty()) {
            getContract().fieldIsEmpty("Name")
            return
        }
        if (city.isNullOrEmpty()) {
            getContract().fieldIsEmpty("City")
            return
        }
        if (newCafe.address.isNullOrEmpty()) {
            getContract().fieldIsEmpty("Address")
            return
        }
        if (phone.isNullOrEmpty()) {
            getContract().fieldIsEmpty("Phone")
            return
        }
        newCafe.name = cafeName
        newCafe.phone = phone
        newCafe.tags = selectedTagList
        val cityObj = City.valueOfByShortName(city)
        if (cityObj != City.UNKNOW) {
            newCafe.city = cityObj.name.toLowerCase()
            when (cityObj) {
                City.JP -> newCafe.country = cityObj.name.toLowerCase()
                City.GB -> newCafe.country = cityObj.name.toLowerCase()
                City.US -> newCafe.country = cityObj.name.toLowerCase()
                else -> newCafe.country = "tw"
            }
        } else {
            getContract().cityInvalid()
            return
        }
        getContract().showLoadingDialog(null)
        newCafe.userId = ModelManager.get().getUserModel().userId
        api.postCafe(ModelManager.get().getUserModel().token, newCafe)
    }

    @Callback("postCafe")
    fun onPostCafeResponse(response: PostCafeResponse) {
        if (response.isSuccess) {
            Config.loge("Add cafe success.\n" + response.toString())
            uploadPhoto(response.cafe?.id)
            getContract().hideLoadingDialog()
            getContract().postSuccess(response.cafe)
        } else {
            getContract().postFailed(response.message)

        }
    }

    private fun uploadPhoto(cafeId: String?) {
        if (cafeId == null) {
            return
        }
        selectedPhotos.forEach {
            startUpload(cafeId, it)
        }
    }

    fun startUpload(cafeId: String, path: String) {
        try {
            val uploadId = MultipartUploadRequest(CafeOfficeApplication.getInstance(), BuildConfig.API_URL_SELF + "v" + BuildConfig.API_VER_SELF + "/photos")
                    // starting from 3.1+, you can also use content:// URI string instead of absolute file
                    .addFileToUpload(path, "photo1")
                    .addParameter("member_id", ModelManager.get().getUserModel().userId)
                    .addParameter("cafe_id", cafeId)
                    .setNotificationConfig(UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(object : UploadStatusDelegate {

                        override fun onProgress(context: Context, uploadInfo: UploadInfo) {

                        }

                        override fun onError(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse, exception: Exception) {

                        }

                        override fun onCompleted(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse) {
                            val bodyAsString = serverResponse.bodyAsString
                            Config.loge("upload response:" + bodyAsString)
                        }

                        override fun onCancelled(context: Context, uploadInfo: UploadInfo) {

                        }
                    })
                    .startUpload()

        } catch (exc: Exception) {
            Config.loge(exc)
        }

    }

    fun onClickRate() {
        getContract().showRateDialog(newCafe.rates)
    }

    fun onClickPickTime() {
        getContract().showOpenTimePickDialog(tempHour)
    }

    fun onPickPhotos(paths: java.util.ArrayList<String>, selectList: MutableList<LocalMedia>) {
        tempSelectList = selectList
        selectedPhotos = paths
        getContract().updatePickedPhoto(selectedPhotos)
    }

    fun prepareToPickPhoto() {
        getContract().startPickPhoto(tempSelectList)
    }

    fun onSelectPriceRange(key: Int) {
        newCafe.priceRange = key
    }
}
