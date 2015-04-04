package com.fixus.towerdefense.tools;

import java.math.BigDecimal;

import android.hardware.SensorManager;

public class Compas {	
	
	public static final String SOUTH = "S";
	public static final String SOUTH_EAST = "SE";
	public static final String SOUTH_WEST = "SW";
	public static final String NORTH = "N";
	public static final String NORTH_EAST = "NE";
	public static final String NORTH_WEST = "NW";
	public static final String EAST = "E";
	public static final String WEST = "W";
	
	public static float getAzimut(float[] accelerometerMatrix,float[] magneticMatrix){
		if (accelerometerMatrix != null && magneticMatrix != null) {
	      float R[] = new float[9];
	      float I[] = new float[9];
	      boolean success = SensorManager.getRotationMatrix(R, I, accelerometerMatrix, magneticMatrix);
	      if (success) {
	        float orientation[] = new float[3];
	        SensorManager.getOrientation(R, orientation);
	        //zmniejszam precyzje do jednego miejsca po przecinku
	        float azimut = new BigDecimal(orientation[0]).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
	        return azimut; // orientation contains: azimut, pitch and roll
	      }
	    }
		return -1;
	}
	
	public static float getAzimuthInDegress(float fAzimuth,boolean isLandscape){
		float azimuthInDegress = (float)Math.toDegrees(fAzimuth);
		float landscapeRotation = isLandscape ? 90f : 0f;
		if (azimuthInDegress < 0.0f) {
		    azimuthInDegress += 360.0f;
		}
		azimuthInDegress = new BigDecimal(azimuthInDegress).setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
		return azimuthInDegress + landscapeRotation;
	}
	
	public static String getDirection(float fAnkle){
		String sKierunek = "nie znaju";
		int iAzimut = (int) fAnkle;
		
		if(iAzimut == 0){
			sKierunek = "Polnoc(N)";
		}else if(iAzimut < 90){
			sKierunek = "Polnocny-wschod (NE)";
		}else if(iAzimut == 90){
			sKierunek = "Wschod (E)";
		}else if(iAzimut < 180){
			sKierunek = "Poludniowy-wschod (SE)";
		}else if(iAzimut == 180){
			sKierunek = "Poludnie (S)";
		}else if(iAzimut < 270 ){
			sKierunek = "Poludniowy-zachod (SW)";
		}else if(iAzimut == 270){
			sKierunek = "Zachod (W)";
		}else if(iAzimut < 360){
			sKierunek = "Polnocny-zachod (NW)";
		}
		return sKierunek;
	}

	public static String getDirection(float fAnkle, float shift){
		String sKierunek = "nie znaju";
		int iAzimut = (int) fAnkle;
		
		if(iAzimut >= (360 - shift) || iAzimut <= shift){
			sKierunek = "Polnoc(N)";
		}else if(iAzimut < (90 - shift)){
			sKierunek = "Polnocny-wschod (NE)";
		}else if(iAzimut >= (90 - shift) && iAzimut <= (90 + shift)){
			sKierunek = "Wschod (E)";
		}else if(iAzimut < (180 - shift)){
			sKierunek = "Poludniowy-wschod (SE)";
		}else if(iAzimut >= (180 - shift) && iAzimut <= (180 + shift)){
			sKierunek = "Poludnie (S)";
		}else if(iAzimut < (270-shift) ){
			sKierunek = "Poludniowy-zachod (SW)";
		}else if(iAzimut >= (270+shift) && iAzimut <= (270-shift)){
			sKierunek = "Zachod (W)";
		}else if(iAzimut < (360-shift)){
			sKierunek = "Polnocny-zachod (NW)";
		}
		return sKierunek;
	}

	public static boolean checkIfDirection(String direction, float fAnkle, float shift) {
		boolean answer = false;
		String currentDirection = "";
		
		int iAzimut = (int) fAnkle;
		
		if(iAzimut >= (360 - shift) || iAzimut <= shift) {
			currentDirection = NORTH;
		} else if(iAzimut >= (90 - shift) && iAzimut <= (90 + shift)){
			currentDirection = EAST;
		} else if(iAzimut >= (180 - shift) && iAzimut <= (180 + shift)){
			currentDirection = SOUTH;
		} else if(iAzimut >= (270+shift) && iAzimut <= (270-shift)){
			currentDirection = WEST;
		} else if(iAzimut < 90){
			currentDirection = NORTH_EAST;
		} else if(iAzimut < 180){
			currentDirection = SOUTH_EAST;
		} else if(iAzimut < 270 ){
			currentDirection = SOUTH_WEST;
		} else if(iAzimut < 360){
			currentDirection = NORTH_WEST;
		}
		
		if(direction.equals(currentDirection)) {
			return true;
		} else {
			return false;
		}
	}
}
