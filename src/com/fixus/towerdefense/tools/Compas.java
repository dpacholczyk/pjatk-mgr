package com.fixus.towerdefense.tools;

import java.math.BigDecimal;
import java.util.ArrayDeque;

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
	
	public static final float ALPHA = 0.15f;	
	private static final int QUEUE_LENGTH = 15;
	
    private static float sumSin, sumCos;
    private static ArrayDeque<Float> azimutQueue  = new ArrayDeque<Float>();
	
	public static float getAzimut(float[] accelerometerMatrix,float[] magneticMatrix){
		if (accelerometerMatrix != null && magneticMatrix != null) {
	      float R[] = new float[9];
	      float I[] = new float[9];
	      boolean success = SensorManager.getRotationMatrix(R, I, accelerometerMatrix, magneticMatrix);
	      if (success) {
	    	// orientation contains: azimut, pitch and roll
	        float orientation[] = new float[3];
	        SensorManager.getOrientation(R, orientation);
	        
	        float azimut = orientation[0];
	        addAzimutToQue(azimut);
	        
	        //zmniejszam precyzje
	        return new BigDecimal(getAziumutAverage()).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue(); 
	      }
	    }
		return -1;
	}
	
	public static float getAzimuthInDegress(float fAzimuth,boolean isLandscape){
		float azimuthInDegress = (float)Math.toDegrees(fAzimuth);
		//jak telefon jest trzymany poziomo nalezy uwzglednic przesuni�cie o 90 stopni
		float landscapeRotation = isLandscape ? 90f : 0f;
		if (azimuthInDegress < 0.0f) {
		    azimuthInDegress += 360.0f;
		}
		//zmniejszam precyzje
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
	
	/*
	 * ==============================================================
	 * 		Metody pomocnicze do wygladzania wyniku z kompasu
	 * ============================================================== 
	 */
	private static void addAzimutToQue(float radians){
        sumSin += (float) Math.sin(radians);
        sumCos += (float) Math.cos(radians);

        azimutQueue.add(radians);

        if(azimutQueue.size() > QUEUE_LENGTH){
            float old = azimutQueue.poll();
            sumSin -= Math.sin(old);
            sumCos -= Math.cos(old);
        }
    }

    private static float getAziumutAverage(){
        int size = azimutQueue.size();
        return (float) Math.atan2(sumSin / size, sumCos / size);
    }
}
