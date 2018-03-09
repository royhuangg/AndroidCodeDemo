package tw.yalan.cafeoffice.activitys.cafemap


import android.location.Location
import android.os.Bundle

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.grasea.grandroid.api.Callback
import com.grasea.grandroid.api.RemoteProxy
import com.grasea.grandroid.api.RequestFail

import java.util.ArrayList
import java.util.Calendar

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.Nullable
import io.reactivex.schedulers.Schedulers
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.api.SelfAPI
import tw.yalan.cafeoffice.api.model.CafeResponse
import tw.yalan.cafeoffice.api.model.LoginRequest
import tw.yalan.cafeoffice.api.model.LoginResponse
import tw.yalan.cafeoffice.api.model.User
import tw.yalan.cafeoffice.common.BasePresenter
import tw.yalan.cafeoffice.common.City
import tw.yalan.cafeoffice.filter.CafeFilter
import tw.yalan.cafeoffice.filter.FilterRule
import tw.yalan.cafeoffice.model.Cafe
import tw.yalan.cafeoffice.utils.MapUtils
import tw.yalan.cafeoffice.utils.Utility

class CafeMapPresenter : BasePresenter<CafeMapActivity>() {

    private var mCurrentLocation: Location? = null
    private var city = ""
    private var selfAPI: SelfAPI? = null
    private var currentCityShortName = ""
    private val time: Long = 0
    lateinit var filter: CafeFilter
    private var reloadCafeOnLogin = false
    internal var leftTop: LatLng? = null
    internal var rightBottom: LatLng? = null

    override fun onActivityCreate(extras: Bundle?) {
        initDefaultValues()
        val cityIndex = ModelManager.get().getUserModel().userChooseCity - 1
        ModelManager.get().getUserModel().putDisplayingCafeList(ArrayList())
        val userId = ModelManager.get().getUserModel().userId
        val isSignIn = Utility.isNotEmptyOrNull(userId)
        val user = ModelManager.get().loadUser()
        getContract().initViews()
        getContract().initMember(isSignIn, user.display_name, user.photo_url)
        getContract().setupCityIndex(City.valueOfByIndex(cityIndex))
    }


    private fun initDefaultValues() {
        filter = CafeFilter()
        val valuesInited = ModelManager.get().getFilterSettingsModel().valuesInited
        if (!valuesInited) {
            ModelManager.get().resetFilterValuesToDefault()
            ModelManager.get().getFilterSettingsModel().putValuesInited(true)
        }
    }

    override fun onActivityResume() {}

    override fun onActivityPause() {

    }

    override fun onActivityDestroy() {
        super.onActivityDestroy()
    }

    fun onCityNameGet(cityName: String) {
        this.currentCityShortName = cityName.replace("市", "").replace("縣", "")
        getContract().onMyCurrentCityGet(currentCityShortName)
        Config.logi("目前城市:" + currentCityShortName)
    }

    fun onSearchResult(cafeId: String) {
        val cafeList = ModelManager.get().getUserModel().cafeList
        for (cafe in cafeList) {
            if (cafe.id == cafeId) {
                getContract().moveBySearch(cafe)
                break
            }
        }
    }

    fun switchToList() {
        getContract().switchToListMode(city, ModelManager.get().getUserModel().displayingCafeList)

    }

    fun switchToMap() {
        getContract().backToMap()
    }


    fun updateCurrentLocationDetail(location: Location) {
        this.mCurrentLocation = location
        ModelManager.get().getUserModel().putLastLat(location.latitude)
        ModelManager.get().getUserModel().putLastLng(location.longitude)

    }

    fun getCafesData(city: City?, leftTop: LatLng?, rightBottom: LatLng?) {
        this.leftTop = leftTop
        this.rightBottom = rightBottom
        if (leftTop == null || rightBottom == null) {
            return
        }
        if (selfAPI == null)
            selfAPI = RemoteProxy.reflect(SelfAPI::class.java, this)
        ModelManager.get().getUserModel().putUserChooseCity(city?.cityIndex ?: 0 + 1)
        val token = ModelManager.get().getUserModel().token

        this.city = city?.name ?: "".toLowerCase()
        selfAPI!!.getCafesByLocation(token, null, "tw",
                leftTop.latitude.toString() + "," + leftTop.longitude.toString(), rightBottom.latitude.toString() + "," + rightBottom.longitude.toString(), null, null, null, null)

    }

    override fun onTokenExpired() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            getContract().showLoadingDialog("重新登入中...")
        }
    }

    @Callback("login")
    fun onRegisterResponse(result: LoginResponse) {
        getContract().hideLoadingDialog()
        if (result.isSuccess) {
            if (result.user != null)
                ModelManager.get().updateUser(result.user)

            val token = result.token
            Config.loge("token:" + token)
            ModelManager.get().getUserModel().putToken(token)
            if (reloadCafeOnLogin) {
                reloadCafeOnLogin = false
                selfAPI!!.getCafesByLocation(token, null, "tw",
                        leftTop!!.latitude.toString() + "," + leftTop!!.longitude.toString(), rightBottom!!.latitude.toString() + "," + rightBottom!!.longitude.toString(), null, null, null, null)
            }
        }
    }

    @Callback("getCafesByLocation")
    fun onCafesResponse(response: CafeResponse?) {
        if (response == null) {
            reloadCafeOnLogin = true
            onTokenExpired()
            return
        }
        if (response.isSuccess) {
            Config.loge("onCafeResponse:" + (System.currentTimeMillis() - time))
            ModelManager.get().getUserModel().putCafeList(response.data)
            filterCafe(null, null, null, 0)

        } else {
            reloadCafeOnLogin = true
            handleErrorCode(response.getErrorCode())
        }
    }


    @RequestFail("getCafes")
    fun onRequestFailed(method: String, t: Throwable) {
        Config.loge("failed" + t)
        getContract().networkNotFound()
    }


    fun searchNearCafe(mCurrentLocation: Location?, filterAll: Boolean) {
        if (mCurrentLocation == null) {
            return
        }
        Config.loge("search " + mCurrentLocation)
        val cafeList = ModelManager.get().getUserModel().cafeList
        var neaiestCafe: Cafe? = null
        var neaiestDistance: Int? = -1
        val myLocation = MapUtils.parseToLatLng(mCurrentLocation)
        if (cafeList != null) {
            for (cafe in cafeList) {
                if (filter.hasFilterRules()) {
                    if (!filterAll && !filter.doFilter(cafe))
                        continue
                }
                val distance = MapUtils.getDistance(myLocation, LatLng(cafe.latitude.toDouble(), cafe.longitude.toDouble()))
                val distanceM = Integer.valueOf(distance[0])
                if (neaiestDistance == -1) {
                    neaiestCafe = cafe
                    neaiestDistance = distanceM
                } else {
                    neaiestDistance = Math.min(distanceM, neaiestDistance ?: 0)
                    if (distanceM.toInt() == neaiestDistance.toInt()) {
                        neaiestCafe = cafe
                    }
                }
            }
        }
        getContract().showNearMyCafe(neaiestCafe)
    }

    fun filterRuleChanged(fastRules: ArrayList<FilterRule.Type>, unSelectedRules: ArrayList<FilterRule.Type>) {
        if (Utility.isNotEmptyOrNull(fastRules)) {
            for (fastRuleType in fastRules) {
                if (filter.contains(fastRuleType)) {
                    val filterRule = filter.getFilterRule(fastRuleType)
                    when (fastRuleType) {
                        FilterRule.Type.WIFI -> {
                            ModelManager.get().getFilterSettingsModel().putLevelWifi(3)
                            filterRule.value = 4.0
                        }
                        FilterRule.Type.SOCKET -> {
                            ModelManager.get().getFilterSettingsModel().putSocketCountIndex(R.id.radio_socket_3)
                            filterRule.value = 1.0
                        }
                        FilterRule.Type.LIMITED_TIME -> {
                            ModelManager.get().getFilterSettingsModel().putLimitedToTimeIndex(R.id.radio_time_4)
                            filterRule.value = 2.0
                        }
                        FilterRule.Type.OPENING -> filterRule.value = 1.0
                        FilterRule.Type.HIGH_RATING -> filterRule.value = 5.0
                    }
                } else {
                    when (fastRuleType) {
                        FilterRule.Type.WIFI -> {
                            ModelManager.get().getFilterSettingsModel().putLevelWifi(3)
                            filter.addFilterRule(FilterRule(fastRuleType, 4.0))
                        }
                        FilterRule.Type.SOCKET -> {
                            ModelManager.get().getFilterSettingsModel().putSocketCountIndex(R.id.radio_socket_3)
                            filter.addFilterRule(FilterRule(fastRuleType, 1.0))
                        }
                        FilterRule.Type.LIMITED_TIME -> {
                            ModelManager.get().getFilterSettingsModel().putLimitedToTimeIndex(R.id.radio_time_4)
                            filter.addFilterRule(FilterRule(fastRuleType, 2.0))
                        }
                        FilterRule.Type.OPENING -> filter.addFilterRule(FilterRule(fastRuleType, 1.0))
                        FilterRule.Type.HIGH_RATING -> filter.addFilterRule(FilterRule(fastRuleType, 5.0))
                    }
                }

            }
        }
        if (Utility.isNotEmptyOrNull(unSelectedRules)) {
            for (unSelectedRuleType in unSelectedRules) {
                if (filter.contains(unSelectedRuleType)) {
                    filter.removeFilterRule(unSelectedRuleType)
                }
                when (unSelectedRuleType) {
                    FilterRule.Type.WIFI -> ModelManager.get().getFilterSettingsModel().putLevelWifi(0)
                    FilterRule.Type.SOCKET -> ModelManager.get().getFilterSettingsModel().putSocketCountIndex(R.id.radio_socket_1)
                    FilterRule.Type.LIMITED_TIME -> ModelManager.get().getFilterSettingsModel().putLimitedToTimeIndex(R.id.radio_time_1)
                }
            }
        }
        updateCafeList(true, true)
    }


    fun filterCafe(filterRuleArrayList: ArrayList<FilterRule>?, @Nullable start: Calendar?, @Nullable end: Calendar?, sortBy: Int) {
        filterCafe(filterRuleArrayList, true, false, start, end, sortBy)
    }


    fun filterCafe(filterRuleArrayList: ArrayList<FilterRule>?, clearMarker: Boolean, skipAlert: Boolean, @Nullable start: Calendar?, @Nullable end: Calendar?, sortBy: Int) {
        if (filterRuleArrayList != null) {
            for (rule in filterRuleArrayList) {
                if (filter.contains(rule.type)) {
                    filter.getFilterRule(rule.type).value = rule.value
                    continue
                } else {
                    filter.addFilterRule(rule)
                }
            }
        }
        filter.setStartCalendar(start)
                .setEndCalendar(end)
        updateCafeList(clearMarker, skipAlert, sortBy)
    }

    @JvmOverloads
    fun updateCafeList(clearMarker: Boolean, skipAlertWhenPoolEmpty: Boolean, sortBy: Int = 0) {
        val cafeList = ModelManager.get().getUserModel().cafeList
        if (cafeList != null) {
            compositeDisposable.add(Flowable.just(cafeList)
                    .subscribeOn(Schedulers.io())
                    .concatMapIterable { cafes -> cafes }
                    .filter {
                        if (!filter.hasFilterRules()) {
                            return@filter true
                        } else {
                            return@filter filter.doFilter(it)
                        }
                    }.toSortedList { cafe, t1 ->
                if (sortBy == 0) {
                    return@toSortedList cafe.compareToByDistance(t1)
                } else {
                    return@toSortedList cafe.compareToByRateAVG(t1)
                }
            }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ cafes ->
                        ModelManager.get().getUserModel().putDisplayingCafeList(cafes as ArrayList<Cafe>)
                        if (clearMarker)
                            getContract().clearMarker()

                        getContract().updateCafeList(city, cafes, filter.hasFilterRules())

                    }, Config::loge))

        } else {
            if (!skipAlertWhenPoolEmpty)
                getContract().onCafesDataIsEmpty()
        }
    }

    fun cancelFilter() {
        filter.clear()
        val cafeList = ModelManager.get().getUserModel().cafeList
        ModelManager.get().getUserModel().putDisplayingCafeList(cafeList)
        getContract().updateCafeList(city, cafeList, false)
    }

}
