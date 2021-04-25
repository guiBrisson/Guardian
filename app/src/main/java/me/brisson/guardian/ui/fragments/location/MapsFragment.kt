package me.brisson.guardian.ui.fragments.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import me.brisson.guardian.R
import me.brisson.guardian.databinding.FragmentMapsBinding
import me.brisson.guardian.ui.base.BaseFragment

class MapsFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = MapsFragment()
    }

    private lateinit var binding : FragmentMapsBinding

    private val currentLocation = LatLng(-34.0, 151.0)
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.centerLocationFAB.setOnClickListener {
            centerCamera(currentLocation)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        p0.addMarker(MarkerOptions().position(currentLocation).title("Marker in Sydney"))
        p0.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        map = p0
    }

    private fun centerCamera(location: LatLng){
            val cameraPosition = CameraPosition.Builder()
            .target(location)     // Sets the center of the map to Mountain View
            .zoom(17f)            // Sets the zoom
            .bearing(90f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }



}