package com.fixus.towerdefense.library.position;

public class PhonePosition {
	public static boolean calibrated = false;
	public float basePosition = 0f;
	
	public static boolean checkIfFlat(float pos, float shift) {
		if(pos <= shift && pos >= (shift * -1)) {
			return true;
		}
		
		return false;
	}
	
	public void calibration(float position) {
		if(!calibrated) {
			this.basePosition = position;
			calibrated = true;
		}
	}
}
