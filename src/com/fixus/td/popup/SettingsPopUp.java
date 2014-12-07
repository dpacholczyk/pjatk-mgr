package com.fixus.td.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class SettingsPopUp {
	private final Context context;

	public SettingsPopUp(Context context) {
		this.context = context;
	}

	public void showPopUp(String title,String message, final String settingsType) {
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
}
