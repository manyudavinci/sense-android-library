package nl.sense_os.service.motion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import nl.sense_os.service.constants.SensePrefs;
import nl.sense_os.service.constants.SensePrefs.Main.Motion;
import nl.sense_os.service.constants.SensorData.SensorNames;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

public class MotionSensorUtils {

    private static final String TAG = "MotionHelper";

    @SuppressWarnings("deprecation")
    public static JSONObject createJsonValue(SensorEvent event) {

        final Sensor sensor = event.sensor;
        final JSONObject json = new JSONObject();

        int axis = 0;
        try {
            for (double value : event.values) {
                // scale to three decimal precision
                if (Double.isNaN(value))
                    continue;
                value = BigDecimal.valueOf(value).setScale(3, 0).doubleValue();

                switch (axis) {
                case 0:
                    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER
                            || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                            || sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                        json.put("x-axis", value);
                    } else if (sensor.getType() == Sensor.TYPE_ORIENTATION
                            || sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                        json.put("azimuth", value);
                    } else {
                        Log.e(TAG, "Unexpected sensor type creating JSON value");
                        return null;
                    }
                    break;
                case 1:
                    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER
                            || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                            || sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                        json.put("y-axis", value);
                    } else if (sensor.getType() == Sensor.TYPE_ORIENTATION
                            || sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                        json.put("pitch", value);
                    } else {
                        Log.e(TAG, "Unexpected sensor type creating JSON value");
                        return null;
                    }
                    break;
                case 2:
                    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER
                            || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                            || sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                        json.put("z-axis", value);
                    } else if (sensor.getType() == Sensor.TYPE_ORIENTATION
                            || sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                        json.put("roll", value);
                    } else {
                        Log.e(TAG, "Unexpected sensor type creating JSON value");
                        return null;
                    }
                    break;
                default:
                    Log.w(TAG, "Unexpected sensor value! More than three axes?!");
                }
                axis++;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception creating motion JSON value", e);
            return null;
        }

        return json;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("deprecation")
    // TODO: Replace orientation sensor with modern version
    public static List<Sensor> getAvailableMotionSensors(Context context) {

        SensorManager mgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = new ArrayList<Sensor>();

        final SharedPreferences mainPrefs = context.getSharedPreferences(SensePrefs.MAIN_PREFS,
                Context.MODE_PRIVATE);

        // add accelerometer
        if (mainPrefs.getBoolean(Motion.ACCELEROMETER, true)) {
            if (null != mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {
                sensors.add(mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            }
        }
        // add orientation sensor
        if (mainPrefs.getBoolean(Motion.ORIENTATION, true)) {
            if (null != mgr.getDefaultSensor(Sensor.TYPE_ORIENTATION)) {
                sensors.add(mgr.getDefaultSensor(Sensor.TYPE_ORIENTATION));
            }
        }
        // add gyroscope
        if (mainPrefs.getBoolean(Motion.GYROSCOPE, true)) {
            if (null != mgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE)) {
                sensors.add(mgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
            }
        }
        // add linear acceleration
        if (mainPrefs.getBoolean(Motion.LINEAR_ACCELERATION, true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                // only devices with gingerbread+ have linear acceleration sensors
                if (null != mgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)) {
                    sensors.add(mgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
                }
            }
        }
        return sensors;
    }

    public static String getSensorHeader(Sensor sensor) {
        String header = "";
        switch (sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            header = "x-axis, y-axis, z-axis";
            break;
        case Sensor.TYPE_GYROSCOPE:
            header = "azimuth, pitch, roll";
            break;
        case Sensor.TYPE_LINEAR_ACCELERATION:
            header = "x-axis, y-axis, z-axis";
            break;
        default:
            Log.w(TAG, "Unexpected sensor type: " + sensor.getType());
            return null;
        }
        return header;
    }

    @SuppressWarnings("deprecation")
    public static String getSensorName(Sensor sensor) {
        String sensorName = "";
        switch (sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            sensorName = SensorNames.ACCELEROMETER;
            break;
        case Sensor.TYPE_ORIENTATION:
            sensorName = SensorNames.ORIENT;
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
            sensorName = SensorNames.MAGNETIC_FIELD;
            break;
        case Sensor.TYPE_GYROSCOPE:
            sensorName = SensorNames.GYRO;
            break;
        case Sensor.TYPE_LINEAR_ACCELERATION:
            sensorName = SensorNames.LIN_ACCELERATION;
            break;
        default:
            Log.w(TAG, "Unexpected sensor type: " + sensor.getType());
            return null;
        }
        return sensorName;
    }

    public static double[] getVector(SensorEvent event) {

        final double[] values = new double[3];

        int axis = 0;

        for (double value : event.values) {
            // scale to three decimal precision
            value = BigDecimal.valueOf(value).setScale(3, 0).doubleValue();
            values[axis] = value;
            axis++;
        }

        return values;
    }
}
