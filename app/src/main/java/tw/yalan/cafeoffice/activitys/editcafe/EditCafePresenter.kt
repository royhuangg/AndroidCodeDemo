package tw.yalan.cafeoffice.activitys.editcafe

import android.net.Uri
import android.os.Bundle
import com.grasea.grandroid.api.Callback
import com.grasea.grandroid.api.RemoteProxy
import tw.yalan.cafeoffice.api.SelfAPI
import tw.yalan.cafeoffice.api.model.BusinessHour
import tw.yalan.cafeoffice.api.model.CafeDetails
import tw.yalan.cafeoffice.api.model.PostCafeResponse
import tw.yalan.cafeoffice.common.BasePresenter
import tw.yalan.cafeoffice.common.City
import tw.yalan.cafeoffice.model.NewCafeObject

/**
 * Created by Yalan Ding on 2017/7/15.
 */

class EditCafePresenter : BasePresenter<EditCafeActivity>() {

    var api: SelfAPI = RemoteProxy.reflect(SelfAPI::class.java, this)
    lateinit var newCafe: NewCafeObject
    var tempHour: BusinessHour? = null

    override fun onActivityCreate(extras: Bundle?) {
        var details = extras?.getSerializable(EditCafeActivity.EXTRA_CAFE_DATA) as? CafeDetails?
        newCafe = NewCafeObject()
        getContract().initViews()
        with(newCafe) {
            cafeId = details?.id
            name = details?.name
            city = City.valueOfByEnglishName(details?.city).cityName
            priceRange = details?.priceRange ?: -1
            businessHour = details?.business_hour
            address = details?.address
            fbUrl = details?.fb_url
            hasStandingDesk = details?.standing_desk.toString()
            latitude = details?.latitude
            longitude = details?.longitude
            limitedTime = details?.limited_time.toString()
            mrt = details?.mrt
            phone = details?.phone
            socket = details?.socket.toString()
            country = details?.country
            tags = details?.tags
            if (businessHour != null) {
                tempHour = BusinessHour(cafe_id = details?.id, business_hour = businessHour!!)
            }
            getContract().fillData(this)
        }


        api.getTagList(ModelManager.get().getUserModel().token)
    }


    override fun onActivityResume() {

    }

    override fun onActivityPause() {

    }

    override fun onActivityDestroy() {

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
        val cityObj = City.valueOfByShortName(city)
        if (cityObj != City.UNKNOW)
            newCafe.city = cityObj.name.toLowerCase()
        else {
            getContract().cityInvalid()
            return
        }
        newCafe.userId = ModelManager.get().getUserModel().userId
        api.editCafe(ModelManager.get().getUserModel().token, newCafe)
    }

    @Callback("editCafe")
    fun onEditCafeResponse(response: PostCafeResponse) {
        if (response.isSuccess) {
            getContract().editSuccess()
        } else {
            getContract().editFailed(response.message)

        }
    }

    fun onTimeSet(data: BusinessHour) {
        tempHour = data
        newCafe.businessHour = data.business_hour
    }

    fun onClickPickTime() {
        getContract().showOpenTimePickDialog(tempHour)
    }

    fun onSelectPriceRange(key: Int) {
        newCafe.priceRange = key
    }

}