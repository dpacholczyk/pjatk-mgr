package com.fixus.td.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

/**
 * 
 * @TODO
 * s¹ dwie opcje na prace z sensorem orientacji
 * 1. odebranie informacji bezpoœrednio z sensora (szybsze)(
 * 2. odebranie informacji z sensorów pola magnetycznego i akcelorometru (dok³adniejsze)
 * Z tego co wyczyta³em dochodzi siê od opcji 1 na rzecz opcji 2
 * CHcia³bym mieæ zaimplementowany switch który pozwala u¿ytkownikowi na wybór opcji. U¿ytkownik w sensie osoba, która
 * korzysta z apki a nie która pisze. Kod na bazie tego ustawienia bêdzie decydowa³ z czego ma korzystaæ. Ja na razie robiê
 * opcje 1 bo to szybsze do implementacji
 *
 */
public class Orientation extends com.fixus.td.sensors.DavidSensor {
	private int orientationSensor;
	private float headingAngle;
	private float pitchAngle;
	private float rollAngle;
	
	private TextView headingView = null;
	private TextView pitchView = null;
	private TextView rollView = null;
	
	//private static boolean display;
	
	final SensorEventListener sensorEventListener = new SensorEventListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onSensorChanged(SensorEvent event) {
			if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				headingAngle = event.values[0];
				pitchAngle = event.values[1];
				rollAngle = event.values[2];
				
				if(com.fixus.td.sensors.DavidSensor.logData) {
					Log.d(com.fixus.td.sensors.DavidSensor.logDataTag, "Heading: " + String.valueOf(headingAngle));
					Log.d(com.fixus.td.sensors.DavidSensor.logDataTag, "Pitch: " + String.valueOf(pitchAngle));
					Log.d(com.fixus.td.sensors.DavidSensor.logDataTag, "Roll: " + String.valueOf(rollAngle));
				}
				
				if(headingView != null) {
					headingView.setText(String.valueOf(headingAngle));
				}
				if(pitchView != null) {
					pitchView.setText(String.valueOf(pitchAngle));
				}
				if(rollView != null) {
					rollView.setText(String.valueOf(rollAngle));
				}
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public Orientation(Context context, ManagerEnum mode, String systemService) {
		super(context, mode, systemService);
	}
	
	public Orientation(Context context, ManagerEnum mode, String systemService, TextView hv, TextView pv, TextView rv) {
		super(context, mode, systemService);
		this.headingView = hv;
		this.pitchView = pv;
		this.rollView = rv;
	}
	
	/**
	 * @TODO
	 * SensorManager.SENSOR_DELAY_NORMAL - mo¿na ustawiæ kilka trybów. Szybsze (moco¿erne) lub wolniejsze
	 * Powinien byæ switch który tym steruje
	 */
	@SuppressWarnings("deprecation")
	public void run() {
		this.orientationSensor = Sensor.TYPE_ORIENTATION;
		this.sensorManager.registerListener(sensorEventListener, this.sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void displayHeading(TextView container) {
		container.setText(String.valueOf(String.valueOf(this.headingAngle)));
	}
	
	public float getHeadingAngle() {
		return this.headingAngle;
	}
	
	public float getPitchAngle() {
		return this.pitchAngle;
	}
	
	public float getRollAngle() {
		return this.rollAngle;
	}
}
