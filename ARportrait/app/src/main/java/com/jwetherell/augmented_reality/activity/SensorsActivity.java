package com.jwetherell.augmented_reality.activity;

import android.content.Context;
import android.hardware.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.FloatMath;
import android.util.Log;
import android.view.Surface;
import com.jwetherell.augmented_reality.common.LowPassFilter;
import com.jwetherell.augmented_reality.common.Matrix;
import com.jwetherell.augmented_reality.data.ARData;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class extends Activity and processes sensor data and location data.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class SensorsActivity extends Fragment implements SensorEventListener, LocationListener {

    private static final String TAG = "SensorsActivity";
    private static final AtomicBoolean computing = new AtomicBoolean(false);

    private static final int MIN_TIME = 30 * 1000;
    private static final int MIN_DISTANCE = 10;

    private static final float temp[] = new float[9]; // Temporary rotation
                                                      // matrix in Android
                                                      // format
    private static final float rotation[] = new float[9]; // Final rotation
                                                          // matrix in Android
                                                          // format
    private static final float grav[] = new float[3]; // Gravity (a.k.a
                                                      // accelerometer data)
    private static final float mag[] = new float[3]; // Magnetic
    /*
     * Using Matrix operations instead. This was way too inaccurate, private
     * static final float apr[] = new float[3]; //Azimuth, pitch, roll
     */

    private static final Matrix worldCoord = new Matrix();
    private static final Matrix magneticCompensatedCoord = new Matrix();
    private static final Matrix xAxisRotation = new Matrix();
    private static final Matrix yAxisRotation = new Matrix();
    private static final Matrix mageticNorthCompensation = new Matrix();

    private static GeomagneticField gmf = null;
    private static float smooth[] = new float[3];
    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;
    private static LocationManager locationMgr = null;
    private static boolean portrait = false;
    private int displayRotation;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayRotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        switch (displayRotation) {
            case Surface.ROTATION_0:
                portrait = true;
                break;
            default:
                portrait = false;
//            case Surface.ROTATION_90:
//            case Surface.ROTATION_180:
//            case Surface.ROTATION_270:
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        float neg90rads = (float)Math.toRadians(-90);

        // Counter-clockwise rotation at -90 degrees around the x-axis
        // [ 1, 0, 0 ]
        // [ 0, cos, -sin ]
        // [ 0, sin, cos ]
        xAxisRotation.set(1f, 0f,                    0f, 
                          0f, (float)Math.cos(neg90rads), -(float)Math.sin(neg90rads),
                          0f, (float)Math.sin(neg90rads), (float)Math.cos(neg90rads));

        // Counter-clockwise rotation at -90 degrees around the y-axis
        // [ cos,  0,   sin ]
        // [ 0,    1,   0   ]
        // [ -sin, 0,   cos ]
        yAxisRotation.set((float)Math.cos(neg90rads),  0f, (float)Math.sin(neg90rads),
                          0f,                     1f, 0f,
                          -(float)Math.sin(neg90rads), 0f, (float)Math.cos(neg90rads));

        try {
            sensorMgr = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensors.size() > 0)
                sensorGrav = sensors.get(0);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensors.size() > 0)
                sensorMag = sensors.get(0);

            sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
            sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);

            locationMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

            try {

                try {
                    Location gps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location network = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (gps != null) onLocationChanged(gps);
                    else if (network != null) onLocationChanged(network);
                    else onLocationChanged(ARData.hardFix);
                } catch (Exception ex2) {
                    onLocationChanged(ARData.hardFix);
                }

                gmf = new GeomagneticField((float) ARData.getCurrentLocation().getLatitude(), 
                                           (float) ARData.getCurrentLocation().getLongitude(),
                                           (float) ARData.getCurrentLocation().getAltitude(), 
                                           System.currentTimeMillis());

                float dec = (float)Math.toRadians(-gmf.getDeclination());

                synchronized (mageticNorthCompensation) {
                    // Identity matrix
                    // [ 1, 0, 0 ]
                    // [ 0, 1, 0 ]
                    // [ 0, 0, 1 ]
                    mageticNorthCompensation.toIdentity();

                    // Counter-clockwise rotation at negative declination around
                    // the y-axis
                    // note: declination of the horizontal component of the
                    // magnetic field
                    // from true north, in degrees (i.e. positive means the
                    // magnetic
                    // field is rotated east that much from true north).
                    // note2: declination is the difference between true north
                    // and magnetic north
                    // [ cos, 0, sin ]
                    // [ 0, 1, 0 ]
                    // [ -sin, 0, cos ]
                    mageticNorthCompensation.set((float)Math.cos(dec),     0f, (float)Math.sin(dec),
                                                 0f,                     1f, 0f, 
                                                 -(float)Math.sin(dec), 0f, (float)Math.cos(dec));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex1) {
            try {
                if (sensorMgr != null) {
                    sensorMgr.unregisterListener(this, sensorGrav);
                    sensorMgr.unregisterListener(this, sensorMag);
                    sensorMgr = null;
                }
                if (locationMgr != null) {
                    locationMgr.removeUpdates(this);
                    locationMgr = null;
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();

        try {
            try {
                sensorMgr.unregisterListener(this, sensorGrav);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                sensorMgr.unregisterListener(this, sensorMag);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            sensorMgr = null;

            try {
                locationMgr.removeUpdates(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            locationMgr = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(SensorEvent evt) {
        if (!computing.compareAndSet(false, true)) return;

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            smooth = LowPassFilter.filter(0.5f, 1.0f, evt.values, grav);
            grav[0] = smooth[0];
            grav[1] = smooth[1];
            grav[2] = smooth[2];
        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            smooth = LowPassFilter.filter(2.0f, 4.0f, evt.values, mag);
            mag[0] = smooth[0];
            mag[1] = smooth[1];
            mag[2] = smooth[2];
        }

        //// Find real world position relative to phone location ////
        // Get rotation matrix given the gravity and geomagnetic matrices
        SensorManager.getRotationMatrix(temp, null, grav, mag);

        // Translate the rotation matrices from Y and -Z (landscape)
        switch (displayRotation) {
            case Surface.ROTATION_0:
                SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_Z, SensorManager.AXIS_Y, rotation);
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, rotation);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_Z, rotation);
                break;
            default:
                Log.e(TAG, "Error:", new RuntimeException("Orientation not yet implemented"));
        }

        /*
         * Using Matrix operations instead. This was way too inaccurate, 
         * //Get the azimuth, pitch, roll 
         * SensorManager.getOrientation(rotation,apr);
         * float floatAzimuth = (float)Math.toDegrees(apr[0]); 
         * if (floatAzimuth<0) floatAzimuth+=360; 
         * ARData.setAzimuth(floatAzimuth);
         * ARData.setPitch((float)Math.toDegrees(apr[1]));
         * ARData.setRoll((float)Math.toDegrees(apr[2]));
         */

        // Convert from float[9] to Matrix
        worldCoord.set(rotation[0], rotation[1], rotation[2], rotation[3], rotation[4], rotation[5], rotation[6], rotation[7], rotation[8]);

        //// Find position relative to magnetic north ////
        // Identity matrix
        // [ 1, 0, 0 ]
        // [ 0, 1, 0 ]
        // [ 0, 0, 1 ]
        magneticCompensatedCoord.toIdentity();

        synchronized (mageticNorthCompensation) {
            // Cross product the matrix with the magnetic north compensation
            magneticCompensatedCoord.prod(mageticNorthCompensation);
        }

        // The compass assumes the screen is parallel to the ground with the screen pointing
        // to the sky, rotate to compensate.
        magneticCompensatedCoord.prod(xAxisRotation);

        // Cross product with the world coordinates to get a mag north compensated coords
        magneticCompensatedCoord.prod(worldCoord);

        // Y axis
        magneticCompensatedCoord.prod(yAxisRotation);

        // Invert the matrix since up-down and left-right are reversed in landscape mode
        magneticCompensatedCoord.invert();

        // Set the rotation matrix (used to translate all object from lat/lon to x/y/z)
        ARData.setRotationMatrix(magneticCompensatedCoord);

        computing.set(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderDisabled(String provider) {
        // Ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProviderEnabled(String provider) {
        // Ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(Location location) {
        ARData.setCurrentLocation(location);
        gmf = new GeomagneticField((float) ARData.getCurrentLocation().getLatitude(), 
                                   (float) ARData.getCurrentLocation().getLongitude(), 
                                   (float) ARData.getCurrentLocation().getAltitude(), System.currentTimeMillis());

        float dec = (float)Math.toRadians(-gmf.getDeclination());

        synchronized (mageticNorthCompensation) {
            mageticNorthCompensation.toIdentity();

            mageticNorthCompensation.set((float)Math.cos(dec), 0f, (float)Math.sin(dec),
                                         0f,                 1f, 0f, 
                                         -(float)Math.sin(dec), 0f, (float)Math.cos(dec));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == null) throw new NullPointerException();

        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.e(TAG, "Compass data unreliable");
        }
    }
}
