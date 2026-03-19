package com.dasumma.mobileskinav.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.dasumma.mobileskinav.R
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.StyleContract
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.eq
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.get
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.literal
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.skyLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.layers.properties.generated.SkyType
import com.mapbox.maps.extension.style.layers.properties.generated.SymbolPlacement
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.terrain
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.plugin.viewport.viewport
import com.mapbox.navigation.core.replay.route.ReplayRouteMapper
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView



class MapActivity : ComponentActivity() {
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource
    private lateinit var navigationCamera: NavigationCamera
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView
    private val navigationLocationProvider = NavigationLocationProvider()
    private val replayRouteMapper = ReplayRouteMapper()

    // Activity result launcher for location permissions
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    initializeMapComponents()
                }

                else -> {
                    Toast.makeText(
                        this,
                        "Location permissions denied. Please enable permissions in settings.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check/request location permissions
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are already granted
            initializeMapComponents()
        } else {
            // Request location permissions
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun getStyle(style: String, baseUri: String, geoJson: String) : StyleContract.StyleExtension {
        return style("mapbox://styles/dasumma1/cmlol4enc000v01qpbj7o8c4i") {
            +rasterDemSource(SOURCE) {
                url(TERRAIN_URL_TILE_RESOURCE)
                // 514 specifies padded DEM tile and provides better performance than 512 tiles.
                tileSize(514)
            }
            +terrain(SOURCE) {
            }
            +skyLayer(SKY_LAYER) {
                skyType(SkyType.ATMOSPHERE)
                skyAtmosphereSun(listOf(-50.0, 90.2))
            }
            +atmosphere { }
            +projection(ProjectionName.GLOBE)
            listOf("area", "run", "lift").forEach { type ->
                +geoJsonSource("ski-$type") {
                    data("${baseUri}${if(type=="area") "ski_areas" else "${type}s"}${geoJson}")
                }
            }
            // --- LABELS ---
            +symbolLayer("area-label", "ski-area") {
                textField(get("name")).applySkiRunStyle()
            }
            +symbolLayer("run-label-lines", "ski-run") {
                filter(eq(literal("\$type"), literal("LineString")))
                symbolPlacement(SymbolPlacement.LINE).applySkiRunStyle()
            }
            +symbolLayer("run-label-polygons", "ski-run") {
                filter(eq(literal("\$type"), literal("Polygon")))
                symbolPlacement(SymbolPlacement.POINT).applySkiRunStyle()
            }
            +symbolLayer("lift-label", "ski-lift") {
                symbolPlacement(SymbolPlacement.LINE).applySkiRunStyle()
            }

            // --- Fill ---
            +fillLayer("area-fill", "ski-area") {
                fillColor(Color.YELLOW).fillOpacity(0.3).fillOutlineColor(Color.BLACK)
            }
            +fillLayer("run-fill", "ski-run") {
                fillColor(Color.BLUE).fillOpacity(0.5).fillOutlineColor(Color.BLACK)
                filter(eq(literal("\$type"), literal("Polygon")))
            }

            // --- Line ---
            +lineLayer("run-line", "ski-run") {
                lineColor(Color.BLUE).lineWidth(3.0).lineOpacity(0.8).lineCap(LineCap.ROUND).lineJoin(LineJoin.ROUND)
            }
            +lineLayer("lift-line", "ski-lift") {
                lineColor(Color.RED).lineWidth(3.0).lineOpacity(0.8).lineCap(LineCap.ROUND).lineJoin(LineJoin.ROUND)
            }
        }
    }

    private fun initializeMapComponents() {
        val inflatedView = layoutInflater.inflate(R.layout.activity_map, null)

        val mapView = inflatedView.findViewById<MapView>(R.id.mapView)
        val style = "mapbox://styles/dasumma1/cmlol4enc000v01qpbj7o8c4i"
        val baseUri = "https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/"
        val geoJson = "_Mount%20Southington%20Ski%20Area.geojson"

        mapView.mapboxMap.loadStyle(getStyle(style, baseUri, geoJson))
        mapView.camera.easeTo(
            cameraOptions = CameraOptions.Builder().build(),
            animationOptions = MapAnimationOptions.Builder().build(),
            animatorListener = null
        )
        mapView.viewport.transitionTo(mapView.viewport.makeFollowPuckViewportState(), mapView.viewport.makeImmediateViewportTransition())
        mapView.scalebar.marginTop = 200f
        mapView.compass.marginTop = 200f
        mapView.logo.marginBottom = 140f
        mapView.attribution.marginBottom = 140f

        setContentView(inflatedView)
    }
    // A helper to apply your specific text style to any SymbolLayer
    fun SymbolLayer.applySkiRunStyle() {
        textField(get("name"))
        textColor(Color.WHITE)
        textSize(12.0)
        textHaloColor(Color.BLACK)
        textHaloWidth(1.0)
    }
    companion object {
        private const val SOURCE = "TERRAIN_SOURCE"
        private const val SKY_LAYER = "sky"
        private const val TERRAIN_URL_TILE_RESOURCE = "mapbox://mapbox.mapbox-terrain-dem-v1"
    }
}