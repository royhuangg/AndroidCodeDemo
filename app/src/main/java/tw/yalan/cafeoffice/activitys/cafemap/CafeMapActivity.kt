package tw.yalan.cafeoffice.activitys.cafemap

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.TypedArray
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import butterknife.BindArray
import butterknife.ButterKnife
import butterknife.OnClick
import com.amplitude.api.Amplitude
import com.facebook.messenger.MessengerUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.FirebaseAnalytics
import com.grasea.grandroid.actions.StoreAction
import com.grasea.grandroid.mvp.FragmentContainer
import com.grasea.grandroid.mvp.UsingPresenter
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_cafe_map.*
import kotlinx.android.synthetic.main.row_fast_filter_header.*
import kotlinx.android.synthetic.main.view_menu.*
import kotlinx.android.synthetic.main.view_menu_header.*
import org.json.JSONObject
import tw.yalan.cafeoffice.Config
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.activitys.about.AboutActivity
import tw.yalan.cafeoffice.activitys.filter.CafeFilterActivity
import tw.yalan.cafeoffice.activitys.loadtohome.LoginActivity
import tw.yalan.cafeoffice.activitys.postcafe.PostCafeActivity
import tw.yalan.cafeoffice.activitys.search.SearchActivity
import tw.yalan.cafeoffice.activitys.settings.SettingFragmentActivity
import tw.yalan.cafeoffice.adapter.FastFilterRecyclerAdapter
import tw.yalan.cafeoffice.adapter.MenuRecyclerAdapter
import tw.yalan.cafeoffice.adapter.SubMenuRecyclerAdapter
import tw.yalan.cafeoffice.common.CafeEvent
import tw.yalan.cafeoffice.common.City
import tw.yalan.cafeoffice.common.LocatorActivity
import tw.yalan.cafeoffice.filter.FilterRule
import tw.yalan.cafeoffice.fragments.CafePageDelegate
import tw.yalan.cafeoffice.fragments.list.CafeListFragment
import tw.yalan.cafeoffice.fragments.map.CafeMapFragment
import tw.yalan.cafeoffice.model.Cafe
import tw.yalan.cafeoffice.model.MenuItemObject
import tw.yalan.cafeoffice.model.MenuSubItemObject
import tw.yalan.cafeoffice.playservices.GoogleClientHandler
import tw.yalan.cafeoffice.utils.MapUtils
import tw.yalan.cafeoffice.utils.Utility
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@FragmentContainer(id = R.id.fragment)
@UsingPresenter(value = CafeMapPresenter::class, singleton = false)
class CafeMapActivity : LocatorActivity<CafeMapPresenter>() {
    var shadowHeight = 0

    @BindArray(R.array.menu_items)
    lateinit var menuItems: Array<String>
    @BindArray(R.array.sub_menu_items_n)
    lateinit var subItemsN: Array<String>
    @BindArray(R.array.sub_menu_items_n_en)
    lateinit var subEnItemsN: Array<String>
    @BindArray(R.array.sub_menu_items_e)
    lateinit var subItemsE: Array<String>
    @BindArray(R.array.sub_menu_items_e_en)
    lateinit var subEnItemsE: Array<String>
    @BindArray(R.array.sub_menu_items_c)
    lateinit var subItemsC: Array<String>
    @BindArray(R.array.sub_menu_items_c_en)
    lateinit var subEnItemsC: Array<String>
    @BindArray(R.array.sub_menu_items_s)
    lateinit var subItemsS: Array<String>
    @BindArray(R.array.sub_menu_items_s_en)
    lateinit var subEnItemsS: Array<String>
    @BindArray(R.array.sub_menu_items_other_country)
    lateinit var subItemsOther: Array<String>
    @BindArray(R.array.sub_menu_items_other_country_en)
    lateinit var subItemsOtherEn: Array<String>

    @BindArray(R.array.menu_icons)
    lateinit var icons: TypedArray
    @BindArray(R.array.fast_filter_text_color)
    lateinit var fastFilterColors: TypedArray
    @BindArray(R.array.fast_filter_item)
    lateinit var fastFilterNames: Array<String>
    @BindArray(R.array.fast_filter_icon_item)
    lateinit var fastFilterIcons: TypedArray
    @BindArray(R.array.fast_filter_bg_item)
    lateinit var fastFilterBgs: TypedArray

    internal var delegate: CafePageDelegate? = null


    private var adapter: MenuRecyclerAdapter? = null
    private lateinit var fastFilterRecyclerAdapter: FastFilterRecyclerAdapter
    private var unselectedRules = ArrayList<FilterRule.Type>()
    private var selectedRules = ArrayList<FilterRule.Type>()
    private var mCurrentLocation: Location? = null
    private var currentCity: City? = City.UNKNOW
    private val handler = Handler()
    private val alertChangeCity = false
    private val mapOnIdle = false
    private var myCurrentCity = City.UNKNOW
    private var skipItemChooseFilter = false
    private var cafeListFragment: CafeListFragment? = null
    private var mapMode = true

    private var map1: LatLng? = null
    private var map2: LatLng? = null


    val myCurrentLatLng: LatLng
        get() = if (mCurrentLocation == null) {
            LatLng(25.032727863519007, 121.56522274017334)
        } else MapUtils.parseToLatLng(mCurrentLocation!!)

    /**
     * 如果之前有選過城市,預先讀取
     */
    private var needLoadCafeDataOnInit = false

    fun updateMapViewPort(map1: LatLng, map2: LatLng) {
        this.map1 = map1
        this.map2 = map2

        getPresenter().getCafesData(myCurrentCity, map1, map2)
    }

    fun restoreParams(saveStateInstance: Bundle) {
        currentCity = saveStateInstance.getSerializable("currentCity") as City
        myCurrentCity = saveStateInstance.getSerializable("myCurrentCity") as City
        skipItemChooseFilter = saveStateInstance.getBoolean("skipItemChooseFilter")
        mCurrentLocation = saveStateInstance.getParcelable("mCurrentLocation")
        selectedRules = saveStateInstance.getSerializable("selectedRules") as ArrayList<FilterRule.Type>
        unselectedRules = saveStateInstance.getSerializable("unselectedRules") as ArrayList<FilterRule.Type>
        mapMode = saveStateInstance.getBoolean("mapMode", true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("currentCity", currentCity)
        outState.putSerializable("myCurrentCity", myCurrentCity)
        outState.putBoolean("skipItemChooseFilter", skipItemChooseFilter)
        if (mCurrentLocation != null) {
            outState.putParcelable("mCurrentLocation", mCurrentLocation)
        }
        outState.putSerializable("selectedRules", selectedRules)
        outState.putSerializable("unselectedRules", unselectedRules)
        outState.putBoolean("mapMode", mapMode)

        super.onSaveInstanceState(outState)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            restoreParams(savedInstanceState)
        }
    }

    override fun initViews() {
        setContentView(R.layout.activity_cafe_map)
        shadowHeight = Utility.dpToPixel(this, 10).toInt()
        ButterKnife.bind(this)
        changeFragment(CafeMapFragment::class.java, "Map", null)
        delegate = CafePageDelegate(supportFragmentManager)
        initFastFilterMenu()
        initSlideMenu()
        startFindMyLocation()

    }

    fun initMember(isSignedIn: Boolean, name: String?, photoUrl: String?) {
        if (isSignedIn) {
            btnLogin.visibility = View.GONE
            btnSeeProfile.visibility = View.VISIBLE
            //            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            tv_user_name.text = name
            iv_user_photo.setImageURI(photoUrl)
            iv_user_photo.setOnClickListener { view -> changeToActivity(MemberActivityKotlin::class.java, null) }
            RxView.clicks(btnSeeProfile)
                    .throttleFirst(2, TimeUnit.SECONDS)
                    .subscribe { o -> changeToActivity(MemberActivityKotlin::class.java, null) }
        } else {
            btnLogin.visibility = View.VISIBLE
            btnSeeProfile.visibility = View.GONE
            iv_user_photo.setActualImageResource(R.drawable.icon_user_avatar)
            tv_user_name.setText(R.string.text_user_name_default)
            RxView.clicks(btnLogin)
                    .throttleFirst(2, TimeUnit.SECONDS)
                    .subscribe { o -> login() }
        }
    }

    private fun login() {
        changeToActivity(LoginActivity::class.java, LoginActivity.skipDirectToMap())
    }

    @Deprecated("")
    fun alertChangeCity() {
        alert("目前所在城市與當前選擇城市不同，是否要查看當前城市的咖啡廳?", { dialog, which ->
            adapter?.setCurrentCity(myCurrentCity)
            moveToMyLocation()
            reloadCafeData(myCurrentCity)
        }) { dialog, which ->

        }
    }

    fun backToMap() {
        btnSwitchMode.setImageResource(R.drawable.list_view)
        supportFragmentManager.popBackStack()
        cafeListFragment = null
        mapMode = true
    }


    fun clearMarker() {
        Config.loge("Clear Marker")
        delegate?.clearMarker()
        //        mMarkerMap.clear();
        //        mCafeMap.clear();
    }


    private fun initFastFilterMenu() {
        fastFilterRecyclerAdapter = object : FastFilterRecyclerAdapter(this, ArrayList(), FastFilterRecyclerAdapter.SimpleItemConfig()) {
            override fun onItemChoose(holder: FastFilterRecyclerAdapter.ViewHolder, position: Int, isChoosed: Boolean) {
                super.onItemChoose(holder, position, isChoosed)

                if (skipItemChooseFilter) {
                    return
                }
                if (position != itemCount - 1) {
                    Config.loge("onItemChoose:$position / $isChoosed")
                    var type: FilterRule.Type = FilterRule.Type.WIFI
                    when (position) {
                        0 -> type = FilterRule.Type.WIFI
                        1 -> type = FilterRule.Type.SOCKET
                        2 -> type = FilterRule.Type.LIMITED_TIME
                        3 -> type = FilterRule.Type.OPENING
                        4 -> type = FilterRule.Type.HIGH_RATING
                    }
                    if (isChoosed) {
                        val json = JSONObject()
                        try {
                            json.putOpt("item", type.name)
                            Amplitude.getInstance().logEvent(getString(R.string.event_fast_filter), json)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    if (selectedRules.contains(type)) {
                        if (!isChoosed) {
                            selectedRules.remove(type)
                        }
                    } else {
                        if (isChoosed) {
                            selectedRules.add(type)
                        }
                    }

                    if (unselectedRules.contains(type)) {
                        if (isChoosed) {
                            unselectedRules.remove(type)
                        }
                    } else {
                        if (!isChoosed) {
                            unselectedRules.add(type)
                        }
                    }
                    //                    updateFastFilterItems(selectedRules, unselectedRules);
                    getPresenter().filterRuleChanged(selectedRules, unselectedRules)
                    val count = if (fastFilterRecyclerAdapter
                            .selectedItems.contains(itemCount - 1))
                        fastFilterRecyclerAdapter.selectedItemCount - 1
                    else
                        fastFilterRecyclerAdapter.selectedItemCount
                    tvSelectCount.text = count.toString()
                    if (count == 0) {
                        if (vsSelect.nextView.id == R.id.iv_icon) {
                            vsSelect.showNext()
                        }
                    } else {
                        if (vsSelect.nextView.id == R.id.tvSelectCount) {
                            vsSelect.showNext()
                        }
                    }
                }
            }

            override fun onItemClick(holder: FastFilterRecyclerAdapter.ViewHolder, index: Int, item: FastFilterRecyclerAdapter.ItemObject) {
                if (index == itemCount - 1)
                    fastFilterRecyclerAdapter.clearSelections()
            }
        }
        for (i in fastFilterNames.indices) {
            fastFilterRecyclerAdapter.getList()
                    .add(FastFilterRecyclerAdapter.ItemObject(fastFilterIcons.getResourceId(i, 0),
                            fastFilterBgs.getResourceId(i, 0),
                            fastFilterColors.getColor(i, 0),
                            fastFilterNames[i]))
        }
        recyclerFastFilter.adapter = fastFilterRecyclerAdapter
        fastFilterBgs?.recycle()
        fastFilterColors?.recycle()
        fastFilterIcons?.recycle()
    }

    //    private void updateFastFilterItems(ArrayList<FilterRule.Type> selectedRules, ArrayList<FilterRule.Type> unselectedRules) {
    //        if (selectedRules.contains(FilterRule.Type.WIFI)) {
    //            fastFilterRecyclerAdapter.toggleSelection(0, false);
    //        }
    //    }


    @SuppressLint("RestrictedApi")
    private fun initSlideMenu() {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDefaultDisplayHomeAsUpEnabled(false)
        }
        val drawerToggle = ActionBarDrawerToggle(this, drawer, null, R.string.open_str, R.string.close_str)
        drawerToggle.syncState()
        drawer.addDrawerListener(drawerToggle)
        adapter = object : MenuRecyclerAdapter(this, ArrayList(), MenuRecyclerAdapter.SimpleItemConfig()) {
            override fun onItemChoose(holder: MenuRecyclerAdapter.ViewHolder, position: Int, isChoosed: Boolean) {
                super.onItemChoose(holder, position, isChoosed)
            }

            override fun onItemClick(holder: MenuRecyclerAdapter.ViewHolder, index: Int, item: MenuRecyclerAdapter.ItemObject) {
                super.onItemClick(holder, index, item)
                if (item.dataObject.canExpand) {

                } else {
                    drawer.closeDrawers()
                    when (index) {
                        5//關於我們
                        -> changeToActivity(AboutActivity::class.java, null)
                        6 -> if (MessengerUtils.hasMessengerInstalled(this@CafeMapActivity)) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb-messenger://user-thread/255181958274780"))
                            startActivity(intent)

                        } else {
                            toast("尚未安裝Facebook Messenger，現在為您開啟 KAPI-找咖啡 的粉絲專頁")
                            startActivity(Utility.getFacebookIntent(this@CafeMapActivity, "https://www.facebook.com/KAPI.tw/"))
                        }
                        7 -> startActivity(Utility.getFacebookIntent(this@CafeMapActivity, "https://www.facebook.com/KAPI.tw/"))
                        8 -> StoreAction(this@CafeMapActivity).execute()
                        9//設定
                        -> changeToActivity(SettingFragmentActivity::class.java, null)
                    }
                }

            }

            override fun onSubItemClick(mainIndex: Int, holder: SubMenuRecyclerAdapter.ViewHolder, index: Int, item: SubMenuRecyclerAdapter.ItemObject) {
                super.onSubItemClick(mainIndex, holder, index, item)
                drawer.closeDrawers()
                //                reloadCafeData(item.getDataObject().getCity());
                setCurrentCity(item.dataObject.city)
                move(item.dataObject.city!!.latLng, DefaultZoomLevel.toFloat())
            }
        }
        //        adapter.setChooseMode(GrandroidRecyclerAdapter.ChooseMode.SINGLE_RADIO);
        recyclerMenu.layoutManager = LinearLayoutManager(this)
        recyclerMenu.adapter = adapter
        for (i in menuItems.indices) {
            val menuItem = menuItems[i]
            //            String enTitle = null;
            //            if (menuEnItems.length > i) {
            //                enTitle = menuEnItems[i];
            //            }
            val menuItemObject = MenuItemObject(icons.getResourceId(i, 0), menuItem, i < 5)

            when (i) {
                0//北部
                -> {
                    val subItemObjects = ArrayList<MenuSubItemObject>()
                    for (j in subItemsN.indices) {
                        subItemObjects.add(MenuSubItemObject(subItemsN[j] + " " + subEnItemsN[j], City.valueOf(subEnItemsN[j].toUpperCase())))
                    }
                    menuItemObject.subItems = subItemObjects
                }
                1//東部
                -> {
                    val subItemObjects = ArrayList<MenuSubItemObject>()
                    for (j in subItemsE.indices) {
                        subItemObjects.add(MenuSubItemObject(subItemsE[j] + " " + subEnItemsE[j], City.valueOf(subEnItemsE[j].toUpperCase())))
                    }
                    menuItemObject.subItems = subItemObjects
                }
                2//中部
                -> {
                    val subItemObjects = ArrayList<MenuSubItemObject>()
                    for (j in subItemsC.indices) {
                        subItemObjects.add(MenuSubItemObject(subItemsC[j] + " " + subEnItemsC[j], City.valueOf(subEnItemsC[j].toUpperCase())))
                    }
                    menuItemObject.subItems = subItemObjects
                }
                3//南部
                -> {
                    val subItemObjects = ArrayList<MenuSubItemObject>()
                    for (j in subItemsS.indices) {
                        subItemObjects.add(MenuSubItemObject(subItemsS[j] + " " + subEnItemsS[j], City.valueOf(subEnItemsS[j].toUpperCase())))
                    }
                    menuItemObject.subItems = subItemObjects
                }
                4 -> {
                    val subItemObjects = ArrayList<MenuSubItemObject>()
                    for (j in subItemsOther.indices) {
                        subItemObjects.add(MenuSubItemObject(subItemsOther[j] + " " + subItemsOtherEn[j], City.valueOf(subItemsOtherEn[j].toUpperCase())))
                    }
                    menuItemObject.subItems = subItemObjects
                }
            }
            adapter?.getList()?.add(MenuRecyclerAdapter.ItemObject(menuItemObject))
        }
    }

    @JvmOverloads
    fun move(latLng: LatLng, zoom: Float, animate: Boolean = true) {
        delegate?.move(latLng, zoom, animate)
    }

    fun moveAndShowCafe(cafe: Cafe, zoom: Boolean, animate: Boolean) {
        delegate?.moveAndShowCafe(cafe, zoom, animate)
    }


    @JvmOverloads
    fun moveToMyLocation(animate: Boolean? = true) {
        delegate?.moveToMyLocation(animate)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILTER) {
            if (resultCode == RESULT_OK && data != null) {
                val bundle = data.getBundleExtra(Config.EXTRA_DATA)
                val sortBy = bundle.getInt(Config.EXTRA_ID_CHOOSE, 0)
                val rules = bundle.getSerializable(Config.EXTRA_DATA) as ArrayList<FilterRule>
                if (rules != null) {
                    showLoadingDialog(getString(R.string.loading_filtting))
                    val selectedItems = fastFilterRecyclerAdapter.selectedItems
                    var hasWifi = false
                    var hasSocket = false
                    var hasLimitedTime = false
                    var hasOpening = false
                    var hasCurrentTimeOpen = false

                    for (rule in rules) {
                        if (rule.type == FilterRule.Type.WIFI && rule.value == 4.0) {
                            if (!selectedItems.contains(0)) {
                                skipItemChooseFilter = true
                                hasWifi = true
                                fastFilterRecyclerAdapter.toggleSelection(0)
                            } else {
                                hasWifi = true
                            }
                        }
                        if (rule.type == FilterRule.Type.SOCKET && rule.value == 1.0) {
                            if (!selectedItems.contains(1)) {
                                skipItemChooseFilter = true
                                hasSocket = true
                                fastFilterRecyclerAdapter.toggleSelection(1)
                            } else {
                                hasSocket = true
                            }
                        }
                        if (rule.type == FilterRule.Type.LIMITED_TIME && rule.value == 2.0) {
                            if (!selectedItems.contains(2)) {
                                skipItemChooseFilter = true
                                hasLimitedTime = true
                                fastFilterRecyclerAdapter.toggleSelection(2)
                            } else {
                                hasLimitedTime = true
                            }
                        }
                        if (rule.type == FilterRule.Type.OPENING && rule.value == 1.0) {
                            if (!selectedItems.contains(3)) {
                                skipItemChooseFilter = true
                                hasOpening = true
                                fastFilterRecyclerAdapter.toggleSelection(3)
                            } else {
                                hasOpening = true
                            }
                        }

                        if (rule.type == FilterRule.Type.CUSTOM_TIME_OPENING) {
                            hasCurrentTimeOpen = true
                        }
                    }
                    if (!hasWifi && selectedItems.contains(0)) {
                        fastFilterRecyclerAdapter.toggleSelection(0)
                    }
                    if (!hasSocket && selectedItems.contains(1))
                        fastFilterRecyclerAdapter.toggleSelection(1)

                    if (!hasLimitedTime && selectedItems.contains(2))
                        fastFilterRecyclerAdapter.toggleSelection(2)
                    if (!hasOpening && selectedItems.contains(3))
                        fastFilterRecyclerAdapter.toggleSelection(3)

                    if (hasCurrentTimeOpen) {
                        val timeStart = bundle.getLong(Config.EXTRA_TIME_START)
                        val timeEnd = bundle.getLong(Config.EXTRA_TIME_END)
                        val clStart = Calendar.getInstance()
                        val clEnd = Calendar.getInstance()
                        clStart.timeInMillis = timeStart
                        clEnd.timeInMillis = timeEnd
                        getPresenter().filterCafe(rules, clStart, clEnd, sortBy)

                    } else {
                        getPresenter().filterCafe(rules, null, null, sortBy)
                    }
                    skipItemChooseFilter = false
                }
            }
        } else if (requestCode == REQUEST_SEARCH) {
            if (resultCode == RESULT_OK && data != null) {
                showLoadingDialog(getString(R.string.loading_searching))
                val cafeId = data.getStringExtra(Config.EXTRA_DATA)
                getPresenter().onSearchResult(cafeId)

            }
        }
    }

    fun moveBySearch(cafe: Cafe) {
        move(cafe.position, 18f, false)
        startMove(cafe)
    }

    override fun onBackPress(): Boolean {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers()
            return false
        }
        if (!mapMode) {
            backToMap()
            return false
        }
        return super.onBackPress()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun onCafesDataIsEmpty() {
        alert(getString(R.string.alert_data_empty))
    }

    fun onClickPostCafe() {
        changeToActivity(PostCafeActivity::class.java, null)
    }


    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        MenuInflater inflater = getMenuInflater();
    //        inflater.inflate(R.menu.home_menu, menu);
    //        MenuItem menuSearchItem = menu.findItem(R.id.my_search);
    //        menuSearchItem.setOnMenuItemClickListener(menuItem -> {
    //            changeToActivityForResult(SearchActivity.class, SearchActivity.newBundle(getMyCurrentLatLng()), REQUEST_SEARCH);
    //            return true;
    //        });
    //        return true;
    //    }


    fun onMyCurrentCityGet(currentCityShortName: String) {
        val city = City.valueOfByShortName(currentCityShortName)
        parseCity(city, true)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRestart() {
        super.onRestart()
        //        getPresenter().reloadCafeDataIfNeed(currentCity);
    }

    @Deprecated("")
    fun onSearchCafeDone(cafe: Cafe?, query: String) {
        if (cafe == null) {
            alert(getString(R.string.alert_donot_search, query))
            hideLoadingDialog()
            return
        }
        moveAndShowCafe(cafe, false, false)
    }

    fun startMove(cafe: Cafe) {
        handler.postDelayed({ moveAndShowCafe(cafe, false, false) }, 1500)
    }

    fun parseCity(city: City, alert: Boolean) {
        myCurrentCity = city
        //TODO 之後以下部分可能需要刪除.
        if (city != currentCity && city != City.UNKNOW) {
            if (currentCity != City.UNKNOW && alert) {
                //                if (mapOnIdle) {
                //                    alertChangeCity();
                //                } else {
                //                    delegate.cityChangedBeforeCameraIdle();
                //                }
            } else {
                adapter?.toggleSelection(city.cityIndex)
                //                reloadCafeData(city);
            }
        }
    }

    fun parseCity(alert: Boolean) {
        parseCity(myCurrentCity, alert)
    }

    private fun reloadCafeData(selectCity: City?) {
        if (currentCity == selectCity) {
            return
        }
        val bundle = Bundle()
        if (selectCity != null) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, selectCity.name)
        }
        FirebaseAnalytics.getInstance(this).logEvent(CafeEvent.SELECT_CITY, bundle)
        currentCity = selectCity
        clearMarker()
        getPresenter().getCafesData(selectCity, map1, map2)
    }

    override fun onStart() {
        super.onStart()
        GoogleClientHandler.getInstance().onStart()
    }


    override fun onStop() {
        GoogleClientHandler.getInstance().onStop()
        super.onStop()
    }

    fun setupCityIndex(city: City) {
        if (city != City.UNKNOW) {
            currentCity = city
            needLoadCafeDataOnInit = true
            adapter?.setCurrentCity(city)
        }
    }

    fun switchToListMode(city: String, tempList: ArrayList<Cafe>) {
        val tag = "CafeListFragment"
        btnSwitchMode.setImageResource(R.drawable.map_view)
        val ft = supportFragmentManager.beginTransaction()
        cafeListFragment = CafeListFragment()
        val map = supportFragmentManager.findFragmentByTag("Map")
        ft.hide(map).add(R.id.fragment, cafeListFragment, tag)
        ft.addToBackStack(tag)
        ft.commit()
        mapMode = false
        //        changeFragment(CafeListFragment.class, "List", null);
    }


    fun updateCafeList(city: String, cafes: List<Cafe>, hasFilter: Boolean) {
        hideLoadingDialog()
        delegate?.updateCafeList(city, cafes, hasFilter)
    }

    override fun updateCurrentLocation(location: Location) {
        this.mCurrentLocation = location
        val geocoder = Geocoder(this, Locale.TAIWAN)
        try {
            val fromLocation = geocoder.getFromLocation(location.latitude, location.longitude,
                    1)
            if (fromLocation != null && fromLocation.size > 0) {
                if (fromLocation[0].adminArea != null)
                    getPresenter().onCityNameGet(fromLocation[0].adminArea)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        getPresenter().updateCurrentLocationDetail(location)
        delegate?.updateCurrentLocation(location)
    }


    @OnClick(R.id.btn_menu, R.id.tv_search, R.id.btnSwitchMode)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_menu -> {
                Amplitude.getInstance().logEvent(getString(R.string.event_click_menu))
                drawer.openDrawer(GravityCompat.START)
            }
            R.id.tv_search -> {
                Amplitude.getInstance().logEvent(getString(R.string.event_search))
                changeToActivityForResult(SearchActivity::class.java, SearchActivity.newBundle(if (mCurrentLocation != null)
                    LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)
                else
                    null), REQUEST_SEARCH)
            }
            R.id.btnSwitchMode -> {
                Amplitude.getInstance().logEvent(getString(R.string.event_switch_mode))
                if (cafeListFragment != null || !mapMode) {
                    getPresenter().switchToMap()
                } else {
                    getPresenter().switchToList()
                }
            }
        }
    }

    fun initCafesData() {
        if (needLoadCafeDataOnInit)
            getPresenter().getCafesData(this.currentCity, map1, map2)
    }

    fun getCafesData() {
        getPresenter().getCafesData(this.currentCity, map1, map2)
    }

    fun showNearMyCafe(neaiestCafe: Cafe?) {
        delegate?.showNearMyCafe(neaiestCafe)
    }


    fun cancelFilter() {
        getPresenter().cancelFilter()
        fastFilterRecyclerAdapter.clearSelections()
    }

    fun searchNearCafe(filterAll: Boolean) {
        if (mCurrentLocation == null) {
            alert(getString(R.string.alert_no_gps))
            return
        }
        val lastLocation = mCurrentLocation
        getPresenter().searchNearCafe(lastLocation, filterAll)
    }

    @OnClick(R.id.button_layout)
    fun onClickEditFilterRule() {
        changeToActivityForResult(CafeFilterActivity::class.java, null, REQUEST_FILTER)
        overridePendingTransition(R.anim.slide_in_bottom, android.R.anim.fade_out)
    }

    companion object {
        val DefaultZoomLevel = 16
        val REQUEST_FILTER = 3
        val REQUEST_SEARCH = 4
    }


}
