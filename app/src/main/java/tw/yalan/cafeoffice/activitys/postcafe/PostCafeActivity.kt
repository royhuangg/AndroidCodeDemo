package tw.yalan.cafeoffice.activitys.postcafe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import biz.kasual.materialnumberpicker.MaterialNumberPicker
import butterknife.ButterKnife
import com.grasea.grandroid.mvp.UsingPresenter
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.activity_post_cafe.*
import kotlinx.android.synthetic.main.view_post_cafe_rate_avg.*
import kotlinx.android.synthetic.main.view_post_cafe_time_selected.*
import tw.yalan.cafeoffice.Config

import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.common.BaseActivity
import tw.yalan.cafeoffice.dialog.DayOfWeekEditDialog
import tw.yalan.cafeoffice.dialog.RatingDialog
import tw.yalan.cafeoffice.model.Tag
import tw.yalan.cafeoffice.utils.FormatUtils
import tw.yalan.cafeoffice.utils.PickerFactory
import tw.yalan.cafeoffice.views.TagView
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.Place
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.jakewharton.rxbinding2.view.RxView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.permissions.RxPermissions
import com.mcxiaoke.koi.ext.inflate
import com.mcxiaoke.koi.ext.startActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import tw.yalan.cafeoffice.activitys.gallery.ImageDisplayActivity
import tw.yalan.cafeoffice.adapter.GalleryRecyclerAdapter
import tw.yalan.cafeoffice.adapter.base.ColorfulDividerItemDecoration
import tw.yalan.cafeoffice.adapter.base.HeaderViewRecyclerAdapter
import tw.yalan.cafeoffice.api.model.BusinessHour
import tw.yalan.cafeoffice.api.model.Photo
import tw.yalan.cafeoffice.api.model.Rate
import tw.yalan.cafeoffice.common.kt.bindString
import tw.yalan.cafeoffice.common.kt.onSafeClick
import tw.yalan.cafeoffice.dialog.LocationPickDialog
import tw.yalan.cafeoffice.model.Cafe
import tw.yalan.cafeoffice.utils.Utility
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@UsingPresenter(value = PostCafePresenter::class, singleton = false)
class PostCafeActivity : BaseActivity<PostCafePresenter>() {

    private var stringArray: Array<String>? = null
    private var priceArray: Array<String>? = null

    private var createNumberPicker: MaterialNumberPicker? = null
    private var alertDialog: AlertDialog? = null
    private var priceAlertDialog: AlertDialog? = null

    private var mGeocoder: Geocoder? = null
    private lateinit var headerAdapter: HeaderViewRecyclerAdapter
    private lateinit var galleryAdapter: GalleryRecyclerAdapter
    override fun initViews() {
        setContentView(R.layout.activity_post_cafe)
        ButterKnife.bind(this)
        mGeocoder = Geocoder(this, Locale.getDefault())
        toolbar?.setNavigationIcon(R.drawable.ic_white_arrow_back)
        toolbar?.setNavigationOnClickListener { finish() }
        toolbar?.setTitle(R.string.activity_title_post_cafe)
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
        selectCityLayout.onSafeClick {
            alertDialog?.show()
        }

        rateLayout.onSafeClick {
            getPresenter().onClickRate()
        }
        timeLayout.onSafeClick {
            getPresenter().onClickPickTime()
        }
        selectPriceRangeLayout.onSafeClick {
            priceAlertDialog?.show()
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
        galleryAdapter = object :
                GalleryRecyclerAdapter(this,
                        ArrayList(),
                        GalleryRecyclerAdapter.SmallSizeItemConfig()) {
            override fun onItemClick(holder: ViewHolder?, index: Int, item: Photo?) {
                super.onItemClick(holder, index, item)

                startActivity<ImageDisplayActivity>(
                        ImageDisplayActivity
                                .newBundle(R.string.text_preview_photo
                                        .bindString(this@PostCafeActivity)
                                        , galleryAdapter.getList()
                                        , index))
            }
        }
        galleryAdapter.isThumbnailMode = true
        galleryAdapter.radius = 5
        headerAdapter = HeaderViewRecyclerAdapter(galleryAdapter)
        var headerLayout = inflate(R.layout.row_gallery_header_96) as FrameLayout
        RxView.clicks(headerLayout)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe { o ->
                    getPresenter().prepareToPickPhoto()
                }
        headerAdapter.addHeaderView(headerLayout)
        recyclerPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerPhotos.addItemDecoration(ColorfulDividerItemDecoration(Utility.dpToPixel(this, 4).toInt(), Color.TRANSPARENT, LinearLayout.HORIZONTAL))
        recyclerPhotos.adapter = headerAdapter
    }

    fun startPickPhoto(tempSelectList: MutableList<LocalMedia>?) {
        val rxPermissions = RxPermissions(this@PostCafeActivity)
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { granted ->
                    if (granted) {

                        PictureSelector.create(this@PostCafeActivity)
                                .openGallery(PictureMimeType.ofImage())
                                .selectionMode(PictureConfig.MULTIPLE)
                                .previewImage(true)
                                .isZoomAnim(true)
                                .selectionMedia(tempSelectList)
                                .rotateEnabled(true)
                                .scaleEnabled(true)
                                .compressGrade(Luban.CUSTOM_GEAR)
                                .compressMaxKB(5 * 1024)
                                .forResult(PictureConfig.CHOOSE_REQUEST)
                    }
                }
    }

    fun updateTagList(tags: List<Tag>) {
        tags.forEach {
            var tagView = createTagView(tag = it)
            tagsLayout.addView(tagView)
        }
//        shimmerTags.stopShimmerAnimation()
    }

    private fun createTagView(tag: Tag): TagView {
        var tagView = TagView(this)
        tagView.tagText = tag.tag_name
        tagView.tag = tag.id
        tagView.tagTextSize = resources.getDimension(R.dimen.tag_text_size)
        tagView.setTagTextColorStateList(R.color.tag_text_primary)
        tagView.setTagBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.selector_button_tag))
        tagView.onClick {
            it.isSelected = !it.isSelected
            getPresenter().onTagSelectChanged(tag, it.isSelected)
        }
        return tagView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
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
                        , place.latLng.longitude
                        , city
                        , place.address
                        , place.name
                        , place.websiteUri)
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(this, data)
                Config.loge(status.statusMessage)

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                var paths = ArrayList<String>()
                val selectList = PictureSelector.obtainMultipleResult(data)
                selectList.forEach {
                    var path: String? = null
                    if (it.isCompressed)
                        path = it.compressPath
                    else if (it.isCut)
                        path = it.cutPath
                    else
                        path = it.path
                    paths.add(path)
                }
                paths.reverse()
                getPresenter().onPickPhotos(paths, selectList)
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

    }

    fun postSuccess(cafe: Cafe?) = alert(getString(R.string.alert_add_cafe_success), { _, _ ->
        finish()
    })

    fun postFailed(message: String?) {
        alert(message)
    }

    fun cityInvalid() = alert(getString(R.string.alert_city_not_support))

    fun showRateDialog(rates: Rate?) {
        var dialog = RatingDialog.newInstance(rate = rates)
        dialog.onClickButtonsBody = { clickConfirm, rate: Rate?, avg: Float? ->
            if (clickConfirm) {
                dialog.dismiss()
                if (tvRatingAvg == null)
                    ratedStub.inflate()
                tvRatingAvg?.text =
                        FormatUtils.DEFAULT_FORMATER.format(avg ?: 0f)
                ivArrowRate.visibility = View.GONE

                getPresenter().onRateConfirm(rate)
            }
        }
        dialog.show(supportFragmentManager, "RatingDialog")
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

    fun updatePickedPhoto(selectedPhotos: ArrayList<String>) {
        galleryAdapter.getList().clear()

        selectedPhotos.forEach {
            galleryAdapter.getList().add(Photo(thumbnail = it))
        }
        headerAdapter.notifyDataSetChanged()
    }
}
