package tw.yalan.cafeoffice.dialog

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.grasea.grandroid.mvp.UsingPresenter
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.dialog_location_pick.*
import tw.yalan.cafeoffice.R
import tw.yalan.cafeoffice.api.SelfAPI
import tw.yalan.cafeoffice.api.model.*
import tw.yalan.cafeoffice.common.BaseDialogFragmentPresenter
import tw.yalan.cafeoffice.common.BaseMVPDialogFragment
import java.util.*

@UsingPresenter(value = LocationPickPresenter::class, singleton = false)
class LocationPickDialog : BaseMVPDialogFragment<LocationPickPresenter>(), OnMapReadyCallback {

    override fun getDialogResourceId(): Int {
        return R.layout.dialog_location_pick
    }

    companion object {

        fun newInstance(): LocationPickDialog {

            val args = Bundle()

            val fragment = LocationPickDialog()
            fragment.arguments = args
            return fragment
        }
    }

    internal var mMap: GoogleMap? = null
    var onPickBody: ((Address, String) -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?, vararg args: Any?) {
        mapView.onCreate(savedInstanceState)
        if (mapView != null && mapView.findViewById<View>(Integer.parseInt("1")) != null) {
            val locationButton = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<ImageView>(Integer.parseInt("2"))
            locationButton.setImageResource(R.drawable.ic_my_locate)
        }
        initMap()
        btnPick.onClick {
            presenter?.onClickPick()
        }
        btnClose.onClick {
            dismiss()
        }
    }

    fun initMap() {
        mapView.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.google_map_style))
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        mMap?.uiSettings?.isCompassEnabled = true
        mMap?.setOnCameraIdleListener {
            var center = mMap?.projection?.visibleRegion?.latLngBounds?.center
            if (center != null) {
                var coder = Geocoder(context, Locale.getDefault())
                val addresses = coder.getFromLocation(center.latitude, center.longitude, 1)
                if (addresses != null && addresses.size > 0) {
                    presenter?.onAddressGet(addresses[0])
                    tvAddress.setText(addresses[0].getAddressLine(0))
                }
            }
        }

        presenter?.onMapReady()
    }

    fun move(latLng: LatLng) {
        move(latLng, mMap?.cameraPosition?.zoom ?: 17f, true)
    }

    fun move(latLng: LatLng, zoom: Float) {
        move(latLng, zoom, true)
    }

    fun move(latLng: LatLng, zoom: Float, animate: Boolean) {
        if (animate) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        } else {
            mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(
                    latLng, zoom)))
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroyView() {
        mapView?.onDestroy()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    fun invokePickBody(address: Address) {
        onPickBody?.invoke(address, tvAddress.text.toString())
        dismiss()
    }
}

class LocationPickPresenter : BaseDialogFragmentPresenter<LocationPickDialog>() {
    internal var address: Address? = null
    override fun onDialogCreateView(args: Bundle?, savedInstanceState: Bundle?) {
        contract.initView(savedInstanceState)
    }

    fun onMapReady() {
        var latLng = LatLng(ModelManager.get().getUserModel().lastLat, ModelManager.get().getUserModel().lastLng)
        getContract().move(latLng, 17f, false)
    }

    fun onAddressGet(address: Address) {
        this.address = address
    }

    fun onClickPick() {
        if (this.address != null) {
            getContract().invokePickBody(address!!)
        }
    }

}
