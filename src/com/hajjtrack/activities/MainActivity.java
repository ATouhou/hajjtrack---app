package com.hajjtrack.activities;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.maxxsol.hajjtrack.Animations;
import com.maxxsol.hajjtrack.AppConstants;
import com.maxxsol.hajjtrack.GMapV2Direction;
import com.maxxsol.hajjtrack.HajjTrackLocationTracker;
import com.maxxsol.hajjtrack.HajjTrackLocationTracker.HajjTrackLocationListener;
import com.maxxsol.hajjtrack.R;
import com.maxxsol.hajjtrack.dialogs.Destination;
import com.maxxsol.hajjtrack.dialogs.Destination.FinishDestinationDialogListener;

public class MainActivity extends FragmentActivity implements
		FinishDestinationDialogListener {

	protected static final String TAG = "MainActivity";
	private GMapV2Direction _gMapV2Direction;
	private GoogleMap _gMap;
	private LatLng _sourcePosition, _destPosition;
	private Location _currentLocation;
	private FragmentManager _fragManager;
	private String _travelBy;
	private ArrayList<LatLng> _directionPoint;
	private HajjTrackLocationTracker _hajjTrackLocationTracker;
	private TextView _tv_RemainingDistance, _tv_RemainingTime;
	private ProgressDialog _pDialog;
	private LinearLayout ll_distTimeStatus;
	private MarkerOptions _myLocationMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_gMapV2Direction = new GMapV2Direction();
		_gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		_gMap.setMyLocationEnabled(true);

		_hajjTrackLocationTracker = new HajjTrackLocationTracker(this);
		_fragManager = getSupportFragmentManager();

		_myLocationMarker = new MarkerOptions().icon(BitmapDescriptorFactory
				.fromResource(R.drawable.ic_my_location));

		_hajjTrackLocationTracker
				.setOnLocationChangeListener(new HajjTrackLocationListener() {

					@Override
					public void onLocationChangeListener(Location location) {
						_currentLocation = location;
						if (_directionPoint != null) {
							for (int i = 0; i < _directionPoint.size(); i++) {
								LatLng point = _directionPoint.get(i);
							}
						}
					}
				});

		_tv_RemainingDistance = (TextView) findViewById(R.id.tv_distanceRemaining);
		_tv_RemainingTime = (TextView) findViewById(R.id.tv_timeRemaining);
		ll_distTimeStatus = (LinearLayout) findViewById(R.id.ll_distTimeStatus);

		_pDialog = new ProgressDialog(this);
		_pDialog.setMessage("Loading...");

		// Map long click listener used to get destination location from user
		// on map and ask for travel options.
		_gMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				Log.i(TAG, "New destination is set");
				_destPosition = point;
				Destination fragment = new Destination();
				fragment.show(_fragManager, "dialogfragment_destination");
			}
		});

		// loading the current location and navigating to it.
		loadMapDirection();

	}

	/** An async task to load the data asynchronously. */
	private class loadRouteAsync extends AsyncTask<Void, Integer, Document> {

		protected Document doInBackground(Void... params) {
			Document doc = _gMapV2Direction.getDocument(_sourcePosition,
					_destPosition, _travelBy);
			return doc;
		}

		protected void onPostExecute(Document doc) {
			if (_pDialog.isShowing())
				_pDialog.dismiss();

			if (doc != null) {
				Log.i(TAG, "Document received from Google Map API");

				_directionPoint = _gMapV2Direction.getDirection(doc);
				String distance = _gMapV2Direction.getDistanceInKm(doc);

				LatLng start = null, end = null;

				PolylineOptions rectLine = new PolylineOptions().width(
						AppConstants.MAP_POLYLINE_WIDTH)
						.color(R.color.map_path);

				for (int i = 0; i < _directionPoint.size(); i++) {
					if (i == 0)
						start = _directionPoint.get(0);
					else if (i == _directionPoint.size() - 1)
						end = _directionPoint.get(i);
					rectLine.add(_directionPoint.get(i));
				}

				_gMap.clear(); // clear all polylines before drawing new one.

				if (_directionPoint.size() >= 2) {
					_gMap.addMarker(new MarkerOptions().position(start).icon(
							BitmapDescriptorFactory
									.fromResource(R.drawable.ic_marker_source)));

					_gMap.addMarker(new MarkerOptions()
							.position(end)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.ic_marker_destination)));
				}

				Animations.setLayoutAnim_slidedown(ll_distTimeStatus,
						MainActivity.this);
				ll_distTimeStatus.setVisibility(View.VISIBLE);

				_tv_RemainingDistance.setText(distance);
				_tv_RemainingTime
						.setText(_gMapV2Direction.getDurationText(doc));

				Polyline polylin = _gMap.addPolyline(rectLine);

			} else {
				Toast.makeText(MainActivity.this,
						"Problem loading directions, please try again later.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected void loadMapDirection() {
		if (_hajjTrackLocationTracker.canGetLocation()) {
			_hajjTrackLocationTracker.updateGPSCoordinates();
			Location currentLocation = _hajjTrackLocationTracker.getLocation();
			_sourcePosition = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());

			if (_destPosition != null) {
				if (!_pDialog.isShowing())
					_pDialog.show();
				new loadRouteAsync().execute();
			}
		} else {
			_hajjTrackLocationTracker.showSettingsAlert();
		}
	}

	protected void navigateOnMap() {

	}

	@Override
	public void onFinishDestinationDialogListener(String travelBy) {
		_travelBy = travelBy;
		loadMapDirection();
	}

}
