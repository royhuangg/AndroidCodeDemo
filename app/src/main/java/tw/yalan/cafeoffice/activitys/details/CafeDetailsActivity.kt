package tw.yalan.cafeoffice.activitys.details

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewStub
import android.widget.*

import com.amplitude.api.Amplitude
import com.facebook.messenger.MessengerUtils
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.grasea.grandroid.mvp.UsingPresenter
import com.jakewharton.rxbinding2.view.RxView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.permissions.RxPermissions
import com.luck.picture.lib.tools.PictureFileUtils

import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.TimeUnit

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import io.reactivex.android.schedulers.AndroidSchedulers
import tw.yalan.cafeoffice.CafeOfficeApplication
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.activitys.cafemap.StreetViewActivity
import tw.yalan.cafeoffice.activitys.editcafe.EditCafeActivity
import tw.yalan.cafeoffice.activitys.gallery.GalleryActivity
import tw.yalan.cafeoffice.activitys.gallery.ImageDisplayActivity
import tw.yalan.cafeoffice.activitys.loadtohome.LoginActivity
import tw.yalan.cafeoffice.adapter.CafeCommentRecyclerAdapter
import tw.yalan.cafeoffice.adapter.GalleryRecyclerAdapter
import tw.yalan.cafeoffice.adapter.base.ColorfulDividerItemDecoration
import tw.yalan.cafeoffice.api.model.CafeDetails
import tw.yalan.cafeoffice.api.model.Comment
import tw.yalan.cafeoffice.api.model.GetCommentResponse
import tw.yalan.cafeoffice.api.model.Photo
import tw.yalan.cafeoffice.api.model.Rate
import tw.yalan.cafeoffice.common.BaseActivity
import tw.yalan.cafeoffice.common.actions.NavigationAction
import tw.yalan.cafeoffice.dialog.RatingDialog
import tw.yalan.cafeoffice.model.Cafe
import tw.yalan.cafeoffice.model.Tag
import tw.yalan.cafeoffice.utils.FormatUtils
import tw.yalan.cafeoffice.utils.ImageHelper
import tw.yalan.cafeoffice.utils.MapUtils
import tw.yalan.cafeoffice.utils.Utility
import tw.yalan.cafeoffice.views.TagView
import tw.yalan.cafeoffice.views.TypeFaceTextView

/**
 * Created by Alan Ding on 2017/4/9.
 */
@SuppressLint("WrongConstant")
@UsingPresenter(value = CafeDetailsPresenter::class, singleton = false)
class CafeDetailsActivity : BaseActivity<CafeDetailsPresenter>(), OnMapReadyCallback {
    internal val socketStringMap = arrayOf("很多", "很少", "看座位", "不確定", "無插座")
    internal val limitedTimeStringMap = arrayOf("有限", "不限", "看情況", "不確定")
    internal val standingStringMap = arrayOf("有", "沒有", "", "不確定")

    @BindView(R.id.iv_banner)
    lateinit var ivBanner: ImageView
    @BindView(R.id.appbar)
    lateinit var appbar: AppBarLayout
    @BindView(R.id.tv_rating_avg)
    lateinit var tvRatingAvg: TypeFaceTextView
    @BindView(R.id.iv_fb)
    lateinit var ivFb: ImageView
    @BindView(R.id.content_layout)
    lateinit var contentLayout: RelativeLayout
    @BindView(R.id.swipe_refresh)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.scrollView)
    lateinit var scrollView: NestedScrollView
    @BindView(R.id.stubMorePhoto)
    lateinit var stubMorePhoto: ViewStub
    var morePhotoLayout: RelativeLayout? = null
    @BindView(R.id.stubUpload)
    lateinit var stuUpload: ViewStub
    @BindView(R.id.tagsLayout)
    lateinit var tagsLayout: FlexboxLayout

    var uploadLayout: FrameLayout? = null

    lateinit var contentViewHolder: ViewHolder
    /**
     * Map
     */
    var mMap: GoogleMap? = null
    var markerOptions: MarkerOptions? = null
    var marker: Marker? = null
    var infoWindowAdapter: GoogleMap.InfoWindowAdapter? = null
    var storeOpenTimesDialog: StoreOpenTimesDialog? = null
    var commentRecyclerAdapter: CafeCommentRecyclerAdapter? = null

    var galleryAdapter: GalleryRecyclerAdapter? = null

    internal var myRate: Rate? = null

    internal var canEditComment = false

    fun updateRateStatus(isRated: Boolean, count: Int) {
        contentViewHolder.headerLayout.btnHeaderRate?.isActivated = isRated
        contentViewHolder.headerLayout.tvRating?.text = getString(R.string.text_rate_count, count.toString())
        contentViewHolder.headerLayout.tvRatingStatus?.setText(if (isRated) R.string.text_already_rate else R.string.detail_header_rates)
    }

    fun updateFavoriteStatus(isMyFavorite: Boolean, count: Int) {
        contentViewHolder.headerLayout.btnHeaderFavorite?.isActivated = isMyFavorite
        contentViewHolder.headerLayout.tvFavorite?.text = getString(R.string.text_favorite_count, count.toString())
        contentViewHolder.headerLayout.tvFavoriteStatus?.setText(if (isMyFavorite) R.string.text_already_favorite else R.string.btn_favorite)
    }

    fun updateVisitStatus(isVisited: Boolean, count: Int) {
        contentViewHolder.headerLayout.btnHeaderCheckIn?.isActivated = isVisited
        contentViewHolder.headerLayout.tvCheckIn?.text = getString(R.string.text_check_in_count, count.toString())
        contentViewHolder.headerLayout.tvCheckInStatus?.setText(if (isVisited) R.string.text_already_visit else R.string.btn_check_in)
    }

    fun updateCommentStatus(isCommented: Boolean, count: Int) {
        contentViewHolder.headerLayout.btnHeaderReview?.isActivated = isCommented
        contentViewHolder.headerLayout.tvReview?.text = getString(R.string.text_review_count, count.toString())
        contentViewHolder.headerLayout.tvReviewStatus?.setText(if (isCommented) R.string.text_already_comment else R.string.btn_review)
    }

    fun initViews(cafe: Cafe?) {
        setContentView(R.layout.activity_cafe_details)
        ButterKnife.bind(this)
        swipeRefreshLayout.isRefreshing = true

        contentViewHolder = ViewHolder(contentLayout)
        initToolbar(toolbar)
        toolbar?.title = cafe?.name
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar?.setNavigationOnClickListener { v -> finish() }
        ImageHelper.loadSquaredImage(this, cafe?.covers?.largePic, R.drawable.loading, R.drawable.no_image, 0, 0, ivBanner)

        //        Picasso.with(this).load(cafe.getCovers().getLargePic()).into(ivBanner);
        //        ImageHelper.loadSquaredImage(this, cafe.getCovers().getGoogle_l(), 0, 0, 0, 0, ivBanner);

        commentRecyclerAdapter = object : CafeCommentRecyclerAdapter(this, ArrayList(), CafeCommentRecyclerAdapter.SimpleItemConfig()) {
            override fun onClickUpVote(isCancel: Boolean, commentId: String) {
                if (isSignedIn) {
                    getPresenter().onClickUpVote(isCancel, commentId)
                } else {
                    changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                }
            }

            override fun onClickDownVote(isCancel: Boolean, commentId: String) {
                if (isSignedIn) {
                    getPresenter().onClickDownVote(isCancel, commentId)
                } else {
                    changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                }
            }
        }
        contentViewHolder.recyclerComments?.layoutManager = LinearLayoutManager(this)
        contentViewHolder.recyclerComments?.addItemDecoration(
                ColorfulDividerItemDecoration(Utility.dpToPixel(this, 1).toInt(), ContextCompat.getColor(this, R.color.colorLineGrey2)))
        contentViewHolder.recyclerComments?.adapter = commentRecyclerAdapter
        updateCommentStatus(false, 0)
        updateFavoriteStatus(false, 0)
        updateRateStatus(false, 0)
        updateVisitStatus(false, 0)

        if (contentViewHolder != null) {
            RxView.clicks(contentViewHolder!!.btnSubmitAComment!!)
                    .throttleFirst(2, TimeUnit.SECONDS)
                    .subscribe { o ->
                        if (isSignedIn)
                            getPresenter().onClickToComment()
                        else
                            changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                    }

            RxView.clicks(contentViewHolder.headerLayout.btnHeaderFavorite!!)
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .subscribe { o ->
                        if (isSignedIn) {
                            Amplitude.getInstance().logEvent(getString(R.string.event_detail_favorite))

                            if (contentViewHolder.headerLayout.btnHeaderFavorite.isActivated) {
                                //unfavorite
                                getPresenter().onClickFavorite(false)
                            } else {
                                //favorite
                                getPresenter().onClickFavorite(true)

                            }
                        } else {
                            changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                        }
                    }
            RxView.clicks(contentViewHolder.headerLayout.btnHeaderCheckIn!!)
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .subscribe { o ->
                        if (isSignedIn) {
                            Amplitude.getInstance().logEvent(getString(R.string.event_detail_visit))
                            if (contentViewHolder.headerLayout.btnHeaderCheckIn?.isActivated == true) {
                                //unfavorite
                                getPresenter().onClickVisit(false)
                            } else {
                                //favorite
                                getPresenter().onClickVisit(true)
                            }
                        } else {
                            changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                        }
                    }

            RxView.clicks(contentViewHolder.headerLayout.btnHeaderReview!!)
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .subscribe { o ->
                        if (isSignedIn) {
                            if (!contentViewHolder.headerLayout.btnHeaderReview!!.isActivated) {
                                Amplitude.getInstance().logEvent(getString(R.string.event_detail_comment))
                                contentViewHolder.btnSubmitAComment?.callOnClick()
                            } else {
                                contentViewHolder.btnMoreComments?.callOnClick()
                            }
                        } else {
                            changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                        }
                    }

        }
    }


    fun injectRatings(details: CafeDetails) {
        contentViewHolder.tvRatesWifiV?.text = FormatUtils.DEFAULT_FORMATER.format(details.wifiAvg)
        contentViewHolder.tvRatesQuietV?.text = FormatUtils.DEFAULT_FORMATER.format(details.quietAvg)
        contentViewHolder.tvRatesCheapV?.text = FormatUtils.DEFAULT_FORMATER.format(details.cheapAvg)
        contentViewHolder.tvRatesCafeV?.text = FormatUtils.DEFAULT_FORMATER.format(details.tastyAvg)
        contentViewHolder.tvRatesMusicV?.text = FormatUtils.DEFAULT_FORMATER.format(details.musicAvg)
        contentViewHolder.tvRatesSeatV?.text = FormatUtils.DEFAULT_FORMATER.format(details.seatAvg)
        contentViewHolder.tvRatesSocketV?.text = socketStringMap[details.socket - 1]
        contentViewHolder.tvRatesLimittimeV?.text = limitedTimeStringMap[details.limited_time - 1]
        contentViewHolder.tvRatesStandV?.text = standingStringMap[details.standing_desk - 1]
    }

    fun goToFacebookPage(url: String) {
        startActivity(Utility.getFacebookIntent(this, url))
    }

    fun goToWatchStreetView(cafe: Cafe?) {
        if (cafe != null) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "street")
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, cafe.name)

            FirebaseAnalytics.getInstance(CafeOfficeApplication.getInstance())
                    .logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            changeToActivity(StreetViewActivity::class.java, StreetViewActivity.newBundle(
                    cafe))
        }
    }

    @OnClick(R.id.iv_fb)
    fun onClickFBIcon() {
        getPresenter().onClickFbIcon()
    }

    @OnClick(R.id.btn_navigation, R.id.btn_watch_street)
    fun onClickMapFooterButtons(view: View) {
        when (view.id) {
            R.id.btn_navigation -> {
                Amplitude.getInstance().logEvent(getString(R.string.event_detail_navigation))
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, toolbar!!.title.toString())

                FirebaseAnalytics.getInstance(CafeOfficeApplication.getInstance())
                        .logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                NavigationAction(this, null, markerOptions?.position, NavigationAction.DRIVING).execute()
            }
            R.id.btn_watch_street -> {
                Amplitude.getInstance().logEvent(getString(R.string.event_detail_streetview))
                getPresenter().onClickWatchStreet()
            }
        }
    }

    fun goToEditCafe(details: CafeDetails) {
        changeToActivity(EditCafeActivity::class.java, EditCafeActivity.newBundle(details))

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.cafe_detail_menu, menu)
        //        MenuItem menuShareItem = menu.findItem(R.id.menu_share);
        //        menuShareItem.setOnMenuItemClickListener(menuItem -> true);
        val menuEditItem = menu.findItem(R.id.menu_edit)
        menuEditItem.setOnMenuItemClickListener { menuItem ->
            Amplitude.getInstance().logEvent(getString(R.string.event_detail_more))
            //            toast(getString(R.string.msg_fun_not_work_now));
            getPresenter().onClickEditCafe()
            true
        }

        val menuReportItem = menu.findItem(R.id.menu_report)
        menuReportItem.setOnMenuItemClickListener { menuItem ->
            Amplitude.getInstance().logEvent(getString(R.string.event_detail_more))
            if (MessengerUtils.hasMessengerInstalled(this@CafeDetailsActivity)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb-messenger://user-thread/255181958274780"))
                startActivity(intent)

            } else {
                toast("尚未安裝Facebook Messenger，現在為您開啟 KAPI-找咖啡 的粉絲專頁")
                startActivity(Utility.getFacebookIntent(this@CafeDetailsActivity, "https://www.facebook.com/KAPI.tw/"))
            }
            true
        }

        val menuCloseItem = menu.findItem(R.id.menu_close)
        menuCloseItem.setOnMenuItemClickListener { menuItem ->
            Amplitude.getInstance().logEvent(getString(R.string.event_detail_more))
            alert(getString(R.string.alert_already_close_now), { dialogInterface, i -> getPresenter().closeReport() }) { dialogInterface, i -> }
            true
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setInfoWindowAdapter(infoWindowAdapter)
        mMap?.uiSettings?.isMapToolbarEnabled = false
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptions?.position, 15f))
        marker = mMap?.addMarker(markerOptions)
        marker?.showInfoWindow()
        val circleOptions = CircleOptions()
                .center(markerOptions?.position)
                .radius(300.0)
                .strokeColor(-0x87b2e1)
                .fillColor(0x33784d1f)
                .strokeWidth(5f) // In meters

        // Get back the mutable Circle
        val circle = mMap?.addCircle(circleOptions)
    }

    fun updateFavoriteStatus(isFavorite: Boolean) {
        contentViewHolder.headerLayout.btnHeaderFavorite?.isActivated = true
    }

    fun updateRate(rate: Rate) {
        this.myRate = rate
        getPresenter().updateMyRate(rate)
    }

    /**
     * Update details.
     *
     * @param cafe    the cafe
     * @param details the details
     * @param rate
     */
    fun updateDetails(cafe: Cafe?, details: CafeDetails, rate: Rate?, tagArrayList: ArrayList<Tag>) {
        this.myRate = rate
        val hasPhoto = details.photoCount != 0 && (details.photos != null || details.kapiPhotos != null)
        val photos = ArrayList<Photo>()

        if (hasPhoto) {
            if (details.kapiPhotos != null)
                photos.addAll(details.kapiPhotos!!)
            if (details.photos != null)
                photos.addAll(details.photos!!)
        }
        contentViewHolder.tvEmptyPhoto?.visibility = if (hasPhoto) View.GONE else View.VISIBLE
        Collections.reverse(photos)
        if (hasPhoto) {
            if (morePhotoLayout == null) {
                morePhotoLayout = stubMorePhoto?.inflate() as RelativeLayout
            }
            if (uploadLayout != null) {
                uploadLayout?.visibility = View.GONE
            }
            val ivPhotoSmall = morePhotoLayout?.findViewById<ImageView>(R.id.ivPhotoSmall)
            val tvCount = morePhotoLayout?.findViewById<TextView>(R.id.tvCount)
            tvCount?.text = "0"
            ImageHelper.loadSquaredImage(this, cafe!!.covers.smallPic, R.drawable.loading, R.drawable.no_image, 0, 0, ivPhotoSmall)

            //            Picasso.with(this).load(cafe.getCovers().getSmallPic()).into(ivPhotoSmall);
            morePhotoLayout?.setOnClickListener { view ->
                //TODO 看更多照片
                getPresenter().onClickMorePhoto()
            }

        } else {
            if (uploadLayout == null) {
                uploadLayout = stuUpload?.inflate() as FrameLayout
            }
            if (morePhotoLayout != null) {
                morePhotoLayout?.visibility = View.GONE
            }
            RxView.clicks(uploadLayout!!)
                    .throttleFirst(2, TimeUnit.SECONDS)
                    .subscribe { o ->
                        if (isSignedIn) {
                            uploadPhoto()
                        } else {
                            changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                        }
                    }
        }
        if (morePhotoLayout != null) {
            val tvCount = morePhotoLayout?.findViewById<TextView>(R.id.tvCount)
            tvCount?.text = details.photoCount.toString()
        }
        contentViewHolder.tvCafeName?.text = details.name
        if (Utility.isNotEmptyOrNull(details.phone)) {
            contentViewHolder.tvPhone?.text = details.phone
        } else {
            contentViewHolder.tvPhone?.visibility = View.GONE
        }
        contentViewHolder.tvAddress?.text = details.address
        if (Utility.isNotEmptyOrNull(cafe?.displayDistance)) {
            val distanceText = "${getString(R.string.text_distance)} ${cafe?.displayDistance ?: "".toLowerCase().replace(" ", "")}"
            contentViewHolder.tvHeaderDistance?.text = distanceText
        }

        if (cafe?.priceRange != null && cafe?.priceRange != -1) {
            contentViewHolder.tvPriceRange?.visibility = View.VISIBLE
            contentViewHolder.tvPriceRange?.text = Utility.getString(this, String.format("price_%d00", cafe.priceRange ?: 0 + 1))
        } else {
            contentViewHolder.tvPriceRange?.visibility = View.GONE
        }

        if (tagsLayout?.childCount > 0) {
            tagsLayout?.removeAllViews()
        }
        for (tag in tagArrayList) {
            val tagView = TagView(this)
            tagView.tagTextSize = resources.getDimension(R.dimen.tag_text_size)
            tagView.tag = tag.id
            tagView.tagText = tag.tag_name
            tagView.setTagTextColorStateList(R.color.tag_text_primary)
            tagsLayout?.addView(tagView)
        }

        tvRatingAvg?.text = FormatUtils.DEFAULT_FORMATER.format(details.rateAverage)
        contentViewHolder.tvStoreStatus?.isEnabled = details.isNowOpen() && details.business_hour != null
        if (details.business_hour != null) {
            contentViewHolder.tvStoreStatus?.text = getString(if (details.isNowOpen()) R.string.text_open else R.string.text_close)
            contentViewHolder.tvStoreStatus?.setTextColor(ContextCompat.getColor(this, R.color.store_status_color))
            contentViewHolder.tvTime?.text = cafe?.openTimeString
            contentViewHolder.btnWatchOtherTime?.visibility = View.VISIBLE
            contentViewHolder.btnWatchOtherTime?.setOnClickListener { view ->
                if (storeOpenTimesDialog != null) {
                    storeOpenTimesDialog?.dismiss()
                    storeOpenTimesDialog = null
                }
                val storeOpenTimesDialog = StoreOpenTimesDialog.newInstance(cafe)
                storeOpenTimesDialog.show(supportFragmentManager, "OpenTimesDialog")
            }
        } else if (Utility.isNotEmptyOrNull(cafe?.openTimeFormUser)) {
            contentViewHolder.tvStoreStatus?.text = getString(R.string.text_time_from_user)
            contentViewHolder.tvStoreStatus?.setTextColor(ContextCompat.getColor(this, R.color.colorTextBlack))
            contentViewHolder.tvTime?.text = cafe?.openTimeFormUser
            contentViewHolder.btnWatchOtherTime?.visibility = View.INVISIBLE
        } else {
            contentViewHolder.tvStoreStatus?.text = getString(R.string.text_no_time)
            contentViewHolder.tvStoreStatus?.setTextColor(ContextCompat.getColor(this, R.color.colorTextBlack))
            contentViewHolder.tvTime?.text = ""
            contentViewHolder.btnWatchOtherTime?.visibility = View.INVISIBLE
        }


        val mapFragment = MapUtils.getMapFragment(this, R.id.map)
        mapFragment.getMapAsync(this)
        if (cafe != null) {
            markerOptions = MarkerOptions()
            markerOptions!!.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cafe_location))
                    .anchor(0.5f, 0.5f)
                    .position(cafe.position)
                    .title(cafe.name)
        }
        infoWindowAdapter = object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker): View {
                if (cafe != null) {
                    val view = LayoutInflater.from(this@CafeDetailsActivity).inflate(R.layout.view_info_content, null)
                    val tvName = view.findViewById<TextView>(R.id.tv_name)
                    val tvAvgStar = view.findViewById<TextView>(R.id.tv_avg_star)
                    val ivWebsite = view.findViewById<ImageView>(R.id.iv_website)
                    if (!TextUtils.isEmpty(cafe.url)) {
                        ivWebsite.visibility = View.VISIBLE
                    }
                    tvName.text = marker.title
                    if (cafe.rateAverage != null) {
                        val rate = cafe.rateAverage
                        tvAvgStar.text = FormatUtils.DEFAULT_FORMATER.format(rate)
                    } else {
                        tvAvgStar.text = "0.0"
                    }
                    return view
                } else {
                    return Space(applicationContext)
                }
            }

            override fun getInfoWindow(marker: Marker): View? {
                return null
            }
        }
        if (Utility.isNotEmptyOrNull(details.url) && details.url!!.contains("facebook")) {
            ivFb?.visibility = View.VISIBLE
        } else {
            ivFb?.visibility = View.INVISIBLE
        }
        /**
         * 下方評分按鈕
         */
        RxView.clicks(contentViewHolder.btnRate)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe { o ->
                    if (isSignedIn) {
                        val ratingDialog = RatingDialog.newInstance(details.id, myRate)
                        ratingDialog.show(supportFragmentManager, "RatingDialog")
                    } else {
                        changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                    }
                }
        /**
         * Header 評分
         */
        RxView.clicks(contentViewHolder.headerLayout.btnHeaderRate)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe { o ->
                    if (isSignedIn) {
                        Amplitude.getInstance().logEvent(getString(R.string.event_detail_rate))
                        val ratingDialog = RatingDialog.newInstance(details.id, myRate)
                        ratingDialog.show(supportFragmentManager, "RatingDialog")
                    } else {
                        changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
                    }
                }
        injectRatings(details)

        commentRecyclerAdapter?.notifyDataSetChanged()
        contentViewHolder.btnMoreCommentsText?.text = getString(R.string.see_more_comment, details.commentCount.toString())

        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.isEnabled = false

        galleryAdapter = object : GalleryRecyclerAdapter(this, photos, GalleryRecyclerAdapter.SmallSizeItemConfig()) {
            override fun onItemClick(holder: GalleryRecyclerAdapter.ViewHolder, index: Int, item: Photo) {
                super.onItemClick(holder, index, item)
                changeToActivity(ImageDisplayActivity::class.java, ImageDisplayActivity.newBundle(contentViewHolder.tvCafeName?.text.toString(), galleryAdapter?.getList(), index))
            }
        }
        galleryAdapter?.isThumbnailMode = true
        //        galleryHeaderAdapter = new HeaderViewRecyclerAdapter(galleryAdapter);
        //        View headerLayout = LayoutInflater.from(this).inflate(R.layout.row_gallery_header, null);
        /**
         * 上傳照片Header
         */
        RxView.clicks(contentViewHolder.btnUploadPhoto)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe { o -> uploadPhoto() }
        //        galleryHeaderAdapter.addHeaderView(headerLayout);
        /**
         * 下方照片列表
         */
        contentViewHolder.recyclerPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        contentViewHolder.recyclerPhotos.addItemDecoration(ColorfulDividerItemDecoration(Utility.dpToPixel(this, 4).toInt(), Color.TRANSPARENT, LinearLayout.HORIZONTAL))
        contentViewHolder.recyclerPhotos.adapter = galleryAdapter
        /*
        查看更多頻論
        */
        RxView.clicks(contentViewHolder.btnMoreComments!!)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe { o -> getPresenter().onClickMoreComment() }
    }

    private fun uploadPhoto() {
        if (isSignedIn) {
            val rxPermissions = RxPermissions(this@CafeDetailsActivity)
            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe { granted ->
                        if (granted) {
                            PictureSelector.create(this@CafeDetailsActivity)
                                    .openGallery(PictureMimeType.ofImage())
                                    .selectionMode(PictureConfig.SINGLE)
                                    .previewImage(true)
                                    .isZoomAnim(true)
                                    .rotateEnabled(true)
                                    .scaleEnabled(true)
                                    .compressGrade(Luban.CUSTOM_GEAR)
                                    .compressMaxKB(5 * 1024)
                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                        }
                    }
        } else {
            changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
        }
    }


    fun updateComments(hasHistoryComment: Boolean, data: GetCommentResponse.DataBean) {
        canEditComment = hasHistoryComment
        updateCommentStatus(hasHistoryComment, data.commentCount)
        commentRecyclerAdapter?.getList()?.clear()
        val comments = data.comments
        if (comments != null) {
            for (comment in comments) {
                commentRecyclerAdapter?.getList()?.add(CafeCommentRecyclerAdapter.ItemObject(comment))
            }
            commentRecyclerAdapter?.notifyDataSetChanged()
        }
        contentViewHolder.tvCommentSubTitle?.text = String.format(getString(R.string.detail_sub_title_comment_count), data.commentCount)
        contentViewHolder.tvEmptyComment?.visibility = if (data.commentCount > 0) View.GONE else View.VISIBLE
        if (hasHistoryComment) {
            (contentViewHolder.btnSubmitAComment?.findViewById<View>(R.id.btn_submit_a_comment_text) as TextView).setText(R.string.btn_edit_comment)
        }
    }

    fun postSuccess() {
        toast(getString(R.string.toast_post_comment_success))
    }

    fun goToGallery(title: String, cafeId: String?, allPhotos: ArrayList<Photo>?) {
        if (allPhotos != null) {
            changeToActivity(GalleryActivity::class.java, GalleryActivity.newBundle(title, cafeId, allPhotos))

        } else if (cafeId != null) {
            changeToActivity(GalleryActivity::class.java, GalleryActivity.newBundle(title, cafeId, null))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 123) {
                getPresenter().loadComments()
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {

                alert("確定要上傳照片嗎?", { dialogInterface, i ->
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    var path: String
                    if (selectList[0].isCompressed)
                        path = selectList[0].compressPath
                    else if (selectList[0].isCut)
                        path = selectList[0].cutPath
                    else
                        path = selectList[0].path
                    Config.loge("onActivityResult:" + path)
                    getPresenter().startUpload(path)
                }) { dialogInterface, i -> PictureFileUtils.deleteCacheDirFile(this) }
            }
        }
    }

    fun uploadSuccess() {
        toast(getString(R.string.upload_photo_success))
        getPresenter().reloadDetails()
        PictureFileUtils.deleteCacheDirFile(this)

    }

    fun uploadFailed(message: String) {
        if (Utility.isNotEmptyOrNull(message)) {
            alert("上傳失敗")
            PictureFileUtils.deleteCacheDirFile(this)
        }
    }

    fun goToMoreComment(cafeId: String, commentArrayList: ArrayList<Comment>?) {
        changeToActivity(AllCommentsActivity::class.java, AllCommentsActivity.newBundle(cafeId, commentArrayList))
    }

    fun goToPostComment(cafeId: String, currentComment: Comment?) {
        if (currentComment == null)
            changeToActivityForResult(PostCommentActivity::class.java, PostCommentActivity.newBundle(cafeId, false, null), 123)
        else
            changeToActivityForResult(PostCommentActivity::class.java, PostCommentActivity.newBundle(cafeId, true, currentComment), 123)
    }

    fun closeReportSuccess() {
        alert(getString(R.string.alert_report_success))
    }

    class ViewHolder(view: View) {
        var headerLayout: DetailHeaderLayout
        @BindView(R.id.iv_favorite)
        lateinit var ivFavorite: ImageView
        @BindView(R.id.tv_favorite_status)
        lateinit var tvFavoriteStatus: TypeFaceTextView
        @BindView(R.id.tv_favorite)
        lateinit var tvFavorite: TypeFaceTextView
        @BindView(R.id.iv_check_in)
        lateinit var ivCheckIn: ImageView
        @BindView(R.id.tv_check_in_status)
        lateinit var tvCheckInStatus: TypeFaceTextView
        @BindView(R.id.tv_check_in)
        lateinit var tvCheckIn: TypeFaceTextView
        @BindView(R.id.iv_ratting)
        lateinit var ivRatting: ImageView
        @BindView(R.id.tv_rating_status)
        lateinit var tvRatingStatus: TypeFaceTextView
        @BindView(R.id.tv_rating)
        lateinit var tvRating: TypeFaceTextView
        @BindView(R.id.iv_review)
        lateinit var ivReview: ImageView
        @BindView(R.id.tv_review_status)
        lateinit var tvReviewStatus: TypeFaceTextView
        @BindView(R.id.tv_review)
        lateinit var tvReview: TypeFaceTextView
        @BindView(R.id.tv_cafe_name)
        lateinit var tvCafeName: TypeFaceTextView
        @BindView(R.id.tv_store_status)
        lateinit var tvStoreStatus: TypeFaceTextView
        @BindView(R.id.tv_time)
        lateinit var tvTime: TypeFaceTextView
        @BindView(R.id.btn_watch_other_time)
        lateinit var btnWatchOtherTime: Button
        @BindView(R.id.tv_phone)
        lateinit var tvPhone: TypeFaceTextView
        @BindView(R.id.tv_address)
        lateinit var tvAddress: TypeFaceTextView
        @BindView(R.id.tvPriceRange)
        lateinit var tvPriceRange: TextView
        @BindView(R.id.cafe_info_layout)
        lateinit var cafeInfoLayout: LinearLayout
        @BindView(R.id.tv_header_distance)
        lateinit var tvHeaderDistance: TypeFaceTextView
        @BindView(R.id.tv_header_rates_count)
        lateinit var tvHeaderRatesCount: TypeFaceTextView
        @BindView(R.id.tv_rates_wifi_v)
        lateinit var tvRatesWifiV: TypeFaceTextView
        @BindView(R.id.tv_rates_quiet_v)
        lateinit var tvRatesQuietV: TypeFaceTextView
        @BindView(R.id.tv_rates_cheap_v)
        lateinit var tvRatesCheapV: TypeFaceTextView
        @BindView(R.id.tv_rates_seat_v)
        lateinit var tvRatesSeatV: TypeFaceTextView
        @BindView(R.id.tv_rates_cafe_v)
        lateinit var tvRatesCafeV: TypeFaceTextView
        @BindView(R.id.tv_rates_music_v)
        lateinit var tvRatesMusicV: TypeFaceTextView
        @BindView(R.id.tv_rates_socket_v)
        lateinit var tvRatesSocketV: TypeFaceTextView
        @BindView(R.id.tv_rates_limittime_v)
        lateinit var tvRatesLimittimeV: TypeFaceTextView
        @BindView(R.id.tv_rates_stand_v)
        lateinit var tvRatesStandV: TypeFaceTextView
        @BindView(R.id.tv_header_reviews_count)
        lateinit var tvCommentSubTitle: TypeFaceTextView
        @BindView(R.id.btn_rate)
        lateinit var btnRate: FrameLayout
        @BindView(R.id.recycler_comments)
        lateinit var recyclerComments: RecyclerView
        @BindView(R.id.btn_more_comments_text)
        lateinit var btnMoreCommentsText: TypeFaceTextView
        @BindView(R.id.btn_more_comments)
        lateinit var btnMoreComments: FrameLayout
        @BindView(R.id.btn_submit_a_comment_text)
        lateinit var btnSubmitACommentText: TypeFaceTextView
        @BindView(R.id.btn_submit_a_comment)
        lateinit var btnSubmitAComment: FrameLayout
        @BindView(R.id.recycler_photos)
        lateinit var recyclerPhotos: RecyclerView
        @BindView(R.id.btn_upload_photo)
        lateinit var btnUploadPhoto: FrameLayout
        @BindView(R.id.tvEmptyComment)
        lateinit var tvEmptyComment: TextView
        @BindView(R.id.tvEmptyPhoto)
        lateinit var tvEmptyPhoto: TextView

        init {
            ButterKnife.bind(this, view)
            headerLayout = DetailHeaderLayout(view.findViewById<View>(R.id.tabs_layout) as LinearLayout)
        }

        class DetailHeaderLayout(var rootView: LinearLayout) {
            @BindView(R.id.iv_favorite)
            lateinit var ivFavorite: ImageView
            @BindView(R.id.tv_favorite_status)
            lateinit var tvFavoriteStatus: TypeFaceTextView
            @BindView(R.id.tv_favorite)
            lateinit var tvFavorite: TypeFaceTextView
            @BindView(R.id.btnHeaderFavorite)
            lateinit var btnHeaderFavorite: RelativeLayout
            @BindView(R.id.iv_check_in)
            lateinit var ivCheckIn: ImageView
            @BindView(R.id.tv_check_in_status)
            lateinit var tvCheckInStatus: TypeFaceTextView
            @BindView(R.id.tv_check_in)
            lateinit var tvCheckIn: TypeFaceTextView
            @BindView(R.id.btnHeaderCheckIn)
            lateinit var btnHeaderCheckIn: RelativeLayout
            @BindView(R.id.iv_ratting)
            lateinit var ivRatting: ImageView
            @BindView(R.id.tv_rating_status)
            lateinit var tvRatingStatus: TypeFaceTextView
            @BindView(R.id.tv_rating)
            lateinit var tvRating: TypeFaceTextView
            @BindView(R.id.btnHeaderRate)
            lateinit var btnHeaderRate: RelativeLayout
            @BindView(R.id.iv_review)
            lateinit var ivReview: ImageView
            @BindView(R.id.tv_review_status)
            lateinit var tvReviewStatus: TypeFaceTextView
            @BindView(R.id.tv_review)
            lateinit var tvReview: TypeFaceTextView
            @BindView(R.id.btnHeaderReview)
            lateinit var btnHeaderReview: RelativeLayout

            init {
                ButterKnife.bind(this, rootView)
            }
        }
    }

    companion object {

        fun newBundle(cafe: Cafe): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(Config.EXTRA_DATA, cafe)
            return bundle
        }
    }
}

