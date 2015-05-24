package com.fixus.td.sensors;

import java.math.BigDecimal;

public class KalmanLatLong {
	private final float MinAccuracy = 1;

	private float metersPerSecond;
	private long milisecondsTimestamp;
	private double latitude;
	private double longitude;
	//result precision
	private int iPrecision;
	private float variance;
	/**
	 * 
	 * @param metersPerSecond - trzeba dac 3 metry albo cos kolo tego
	 * @param iPrecision - mowi jaka dokladnosc po przecinku, powinna byc dla dlugosci i szerokosci
	 */
	public KalmanLatLong(float metersPerSecond,int iPrecision) {
		this.metersPerSecond = metersPerSecond;
		this.iPrecision = iPrecision;
		variance = -1;
	}

	public long getTimestamp() {
		return milisecondsTimestamp;
	}

	public double getLatitude() {
		return new BigDecimal(latitude).setScale(iPrecision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getLongitude() {
		return new BigDecimal(longitude).setScale(iPrecision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public float get_accuracy() {
		return (float) Math.sqrt(variance);
	}
	/**
	 * Kalman filter processing for lattitude and longitude
	 * @param lat_measurement new measurement of lattidude
	 * @param lng_measurement new measurement of longitude
	 * @param accuracy measurement of 1 standard deviation error in metres
	 * @param TimeStamp_milliseconds time of measurement
	 */
	public void Process(double lat_measurement, double lng_measurement,
			float accuracy, long TimeStamp_milliseconds) {
		if (accuracy < MinAccuracy)
			accuracy = MinAccuracy;
		if (variance < 0) {
			// brak inicjalizacj czyli mamy pierwsze odpalenie
			this.milisecondsTimestamp = TimeStamp_milliseconds;
			latitude = lat_measurement;
			longitude = lng_measurement;
			variance = accuracy * accuracy;
		} else {
			//Metoda Klamana
			long TimeInc_milliseconds = TimeStamp_milliseconds
					- this.milisecondsTimestamp;
			if (TimeInc_milliseconds > 0) {
				// time has moved on, so the uncertainty in the current position
				// increases
				variance += TimeInc_milliseconds * metersPerSecond
						* metersPerSecond / 1000;
				this.milisecondsTimestamp = TimeStamp_milliseconds;
				// TO DO: USE VELOCITY INFORMATION HERE TO GET A BETTER ESTIMATE
				// OF CURRENT POSITION
			}

			// Kalman gain matrix K = Covarariance * Inverse(Covariance +
			// MeasurementVariance)
			// NB: because K is dimensionless, it doesn't matter that variance
			// has different units to lat and lng
			float K = variance / (variance + accuracy * accuracy);
			// apply K
			latitude += K * (lat_measurement - latitude);
			longitude += K * (lng_measurement - longitude);
			// new Covarariance matrix is (IdentityMatrix - K) * Covarariance
			variance = (1 - K) * variance;
		}
	}
}