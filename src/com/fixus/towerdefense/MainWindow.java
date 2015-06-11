package com.fixus.towerdefense;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainWindow extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.interfejss.util.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.hide();
		}
		setContentView(R.layout.activity_main_window);
	}
	
	public void startAction(View view) {
		Intent intent = new Intent(this, SecondActivity.class);
	    //intent.putExtra(EXTRA_MESSAGE, "wiadomosc ;)");
	    startActivity(intent);
	}
	
	public void optionAction(View view) {
		
	}
	
	public void closeApplication(View view) {
		finish();
        System.exit(0);
	}
}
