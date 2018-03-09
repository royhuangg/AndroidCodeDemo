package tw.yalan.cafeoffice.activitys.editcafe

import android.Manifest
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import biz.kasual.materialnumberpicker.MaterialNumberPicker
import butterknife.ButterKnife
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.grasea.grandroid.mvp.UsingPresenter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.permissions.RxPermissions
import com.mcxiaoke.koi.ext.onClick
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_edit_cafe.*
import kotlinx.android.synthetic.main.view_post_cafe_time_selected.*
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.api.model.BusinessHour
import tw.yalan.cafeoffice.api.model.CafeDetails
import tw.yalan.cafeoffice.common.BaseActivity
import tw.yalan.cafeoffice.common.kt.onSafeClick
import tw.yalan.cafeoffice.dialog.DayOfWeekEditDialog
import tw.yalan.cafeoffice.dialog.LocationPickDialog
import tw.yalan.cafeoffice.model.Cafe
import tw.yalan.cafeoffice.model.NewCafeObject
import tw.yalan.cafeoffice.utils.PickerFactory
import java.io.IOException
import java.util.*

@UsingPresenter(value = EditCafePresenter::class, singleton = false)
class EditCafeActivity : BaseActivity<EditCafePresenter>() {

    private var stringArray: Array<String>? = null
    private var priceArray: Array<String>? = null

    private var createNumberPicker: MaterialNumberPicker? = null
    private var alertDialog: AlertDialog? = null
    private var priceAlertDialog: AlertDialog? = null

    private var mGeocoder: Geocoder? = null

    override fun initViews() {
        setContentView(R.layout.activity_edit_cafe)
        ButterKnife.bind(this)
        mGeocoder = Geocoder(this, Locale.getDefault())
        toolbar?.setNavigationIcon(R.drawable.ic_white_arrow_back)
        toolbar?.setNavigationOnClickListener { finish() }
        toolbar?.setTitle(R.string.activity_title_edit_cafe)
        btnSend.onClick {
            if (etCafeName.visibility != View.GONE) {
                val cafeName = etCafeName.text.toString()
                val phone = etPhone.text.toString()
                val city = tvCity.text.toString()
                getPresenter()?.onClickSent(cafeName, phone, city)
            } else {
                val cafeName = tvCafeName.text.toString()
                val phone = etPhone.text.toString()
                val city = tvCity.text.toString()
                getPresenter()?.onClickSent(cafeName, phone, city)
            }

        }
        stringArray = resources.getStringArray(R.array.citys)
        priceArray = resources.getStringArray(R.array.prices)

        createNumberPicker = PickerFactory.createNumberPicker(this,
                0,
                stringArray?.size?.minus(1) ?: 0,
                0
                , { index -> stringArray?.get(index) ?: "" })
        alertDialog = AlertDialog.Builder(this).setTitle(R.string.alert_title_post_cafe_city_select)
                .setItems(stringArray, { _, which -> tvCity.text = stringArray?.get(which) ?: "" })
                .setNegativeButton(R.string.alert_btn_cancel, null).create()

        priceAlertDialog = AlertDialog.Builder(this).setTitle(R.string.alert_title_post_price_range_select)
                .setItems(priceArray, { _, which ->
                    if (which != 0)
                        tvPriceRange.text = priceArray?.get(which) ?: ""
                    else
                        tvPriceRange.text = ""
                    getPresenter().onSelectPriceRange(which - 1)
                })
                .setNegativeButton(R.string.alert_btn_cancel, null).create()

        selectPriceRangeLayout.onSafeClick {
            priceAlertDialog?.show()
        }
        selectCityLayout.onSafeClick {
            alertDialog?.show()
        }

        timeLayout.onSafeClick {
            getPresenter().onClickPickTime()
        }

        tvCafeName.onSafeClick {
            try {
                val typeFilter = AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                        .build()
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .setFilter(typeFilter)
                        .build(this)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: GooglePlayServicesRepairableException) {
                Config.loge(e)
            } catch (e: GooglePlayServicesNotAvailableException) {
                Config.loge(e)
            }

        }
        btnSwitchInputType.onSafeClick {
            when (switcherCafeName.nextView) {
                etCafeName -> {
                    btnSwitchInputType.setText(R.string.keyword_input)
                }
                tvCafeName -> {
                    btnSwitchInputType.setText(R.string.self_input)
                }
            }
            switcherCafeName.showNext()
            tvCafeName.requestFocus()
        }

        radioTimeGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.radioTime1 -> getPresenter().onRadioTimeCheckedChange(1)
                R.id.radioTime2 -> getPresenter().onRadioTimeCheckedChange(3)
                R.id.radioTime3 -> getPresenter().onRadioTimeCheckedChange(2)
                R.id.radioTime4 -> getPresenter().onRadioTimeCheckedChange(4)
            }
        }
        radioSocketGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.radioSocket1 -> getPresenter().onRadioSocketCheckedChange(5)
                R.id.radioSocket2 -> getPresenter().onRadioSocketCheckedChange(3)
                R.id.radioSocket3 -> getPresenter().onRadioSocketCheckedChange(1)
                R.id.radioSocket4 -> getPresenter().onRadioSocketCheckedChange(4)
            }
        }

        cbStandUp.setOnCheckedChangeListener { _, isChecked ->
            getPresenter().onStandUpCheckedChange(isChecked)

        }
        radioSocketGroup.check(R.id.radioSocket1)
        radioTimeGroup.check(R.id.radioTime1)
        tvAddress.onSafeClick {
            var dialog = LocationPickDialog.newInstance()
            dialog.onPickBody = { address: Address, typeAddress: String ->
                val addressString = address.getAddressLine(0)
                if (typeAddress.isEmpty()) {
                    getPresenter().onAddressPick(addressString, address.latitude, address.longitude)
                    tvAddress.text = addressString
                } else {
                    getPresenter().onAddressPick(typeAddress, address.latitude, address.longitude)
                    tvAddress.text = typeAddress
                }
            }
            dialog.show(supportFragmentManager, "LocationPickDialog")
        }
    }

    fun fillData(newCafe: NewCafeObject) {
        tvCafeName.text = newCafe.name ?: ""
        tvCity.text = newCafe.city ?: ""
        if (newCafe.businessHour != null) {
            if (tvTime == null) {
                timeStub.inflate()
                ivArrowTime.visibility = View.GONE
            }
        }
        if (newCafe.priceRange != -1)
            tvPriceRange.text = priceArray?.get(newCafe.priceRange + 1) ?: ""
        else
            tvPriceRange.text = ""

        val limitTime = newCafe.limitedTime?.toIntOrNull()
        if (limitTime != null) {
            when (limitTime) {
                4 -> {
                    radioTimeGroup.check(R.id.radioTime4)
                }
                3 -> {
                    radioTimeGroup.check(R.id.radioTime2)
                }
                2 -> {
                    radioTimeGroup.check(R.id.radioTime3)
                }
                1 -> {
                    radioTimeGroup.check(R.id.radioTime1)
                }

            }
        }
        val socket = newCafe.socket?.toIntOrNull()
        if (socket != null) {
            when (socket) {
                5 -> {
                    radioSocketGroup.check(R.id.radioSocket1)
                }
                4 -> {
                    radioSocketGroup.check(R.id.radioSocket4)
                }
                3 -> {
                    radioSocketGroup.check(R.id.radioSocket2)
                }
                1 -> {
                    radioSocketGroup.check(R.id.radioSocket3)
                }
            }
        }

        cbStandUp.isChecked = newCafe.hasStandingDesk == "1"

        tvAddress.text = newCafe.address ?: ""
        etPhone.setText(newCafe.phone ?: "")
        etFacebookUrl.setText(newCafe.fbUrl ?: "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place: Place = PlaceAutocomplete.getPlace(this, data)
                    Config.loge("Place: " + place.name)
                    tvCafeName.text = place.name
                    etFacebookUrl.setText(place.websiteUri?.toString())
                    tvAddress.text = place.address?.toString()
                    etPhone.setText(place.phoneNumber?.toString())
                    val city = getCityNameByCoordinates(place.latLng.latitude, place.latLng.longitude)
                    Config.loge("City:" + city)
                    tvCity.text = city

                    getPresenter().onPlaceChoose(place.latLng.latitude
                            ,
                            place.latLng.longitude
                            ,
                            city
                            ,
                            place.address
                            ,
                            place.name
                            ,
                            place.websiteUri)
                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(this, data)
                    Config.loge(status.statusMessage)

                }
                RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun getCityNameByCoordinates(lat: Double, lon: Double): String? {

        val addresses = mGeocoder?.getFromLocation(lat, lon, 1)
        if (addresses != null && addresses.size > 0) {
            return addresses[0].adminArea
        }
        return null
    }

    companion object {
        val PLACE_AUTOCOMPLETE_REQUEST_CODE = 284
        val EXTRA_CAFE_DATA = "EXTRA_CAFE_DATA"

        fun newBundle(detail: CafeDetails): Bundle {
            var bundle = Bundle()
            bundle.putSerializable(EXTRA_CAFE_DATA, detail)
            return bundle
        }
    }

    fun showOpenTimePickDialog(time: BusinessHour?) {
        val dayOfWeekDialog = DayOfWeekEditDialog.newInstance(hour = time)
        dayOfWeekDialog.onClickSendBody = { businessHour ->
            //                Config.loge(businessHour?.toString())
            if (businessHour != null) {
                if (tvTime == null) {
                    timeStub.inflate()
                    ivArrowTime.visibility = View.GONE
                }
                getPresenter()?.onTimeSet(businessHour)
            }
        }
        dayOfWeekDialog.show(supportFragmentManager, "DayOfWeekEditDialog")
    }

    fun editSuccess() = alert(getString(R.string.alert_edit_cafe_success), { _, _ ->
        finish()
    })

    fun editFailed(message: String?) {
        alert(message)
    }

    fun cityInvalid() = alert(getString(R.string.alert_city_not_support))


    fun fieldIsEmpty(s: String) {
        when (s) {
            "City" -> {
                alert(getString(R.string.alert_city_empty))
            }
            "Name" -> {
                alert(getString(R.string.alert_cafe_name_empty))
            }
            "Address" -> {
                alert(getString(R.string.alert_address_empty))
            }
            "Phone" -> {
                alert(getString(R.string.alert_phone_empty))
            }
        }

    }


}