package com.fixus.towerdefense.tools;

import android.hardware.SensorManager;

public class Compas {	
	public static float getAzimut(float[] accelerometerMatrix,float[] magneticMatrix){
		if (accelerometerMatrix != null && magneticMatrix != null) {
	      float R[] = new float[9];
	      float I[] = new float[9];
	      boolean success = SensorManager.getRotationMatrix(R, I, accelerometerMatrix, magneticMatrix);
	      if (success) {
	        float orientation[] = new float[3];
	        SensorManager.getOrientation(R, orientation);
	        return orientation[0]; // orientation contains: azimut, pitch and roll
	      }
	    }
		return -1;
	}
	
	public static float getAzimuthInDegress(float fAzimuth){
		float azimuthInDegress = (float)Math.toDegrees(fAzimuth);
		if (azimuthInDegress < 0.0f) {
		    azimuthInDegress += 360.0f;
		}
		return azimuthInDegress;
	}
	
	public static String getKierunek(float fAnkle){
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
}
