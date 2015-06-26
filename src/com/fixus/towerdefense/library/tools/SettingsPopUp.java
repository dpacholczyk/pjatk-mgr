package com.fixus.towerdefense.library.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class SettingsPopUp {
	private final Context context;

	public SettingsPopUp(Context context) {
		this.context = context;
	}

	public void showPopUpWithSettingsMenu(String title,String message, final String settingsType) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		//Tworzymy przycisk ok, ktory nazywamy Open setting
		//i w zaleznosci od parametru otwieramy odpowiednia zakladke ustawien
		alertDialog.setPositiveButton("Open settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(settingsType);
						context.startActivity(intent);
					}
				});

		//oraz przycisk kasujacy, ktory zamknie okienko
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.show();
	}
	
	public void showSimplePopup(String title,String message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);		
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		
		alertDialog.show();
	}
	
	public void showPopupWithExitButton(String title,String message,final Activity oCurrentActivity) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);	
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		
		alertDialog.setPositiveButton("Exit game",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						oCurrentActivity.finish();
				        System.exit(0);
					}
				});
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		
		alertDialog.show();
	}
}
