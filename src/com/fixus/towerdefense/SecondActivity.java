package com.fixus.towerdefense;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fixus.towerdefense.tools.PhonePosition;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SecondActivity extends Activity {
	public final static String RANGE = "com.example.interfejss.RANGE";
	public final static String POINTS = "com.example.interfejss.POINTS";
	private TextView currentRange;
	private TextView numberOfPoints;

	@SuppressLint("InlinedApi")
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
		
		
		setContentView(R.layout.activity_second);
		currentRange = (TextView) findViewById(R.id.currentRange);
		numberOfPoints = (TextView) findViewById(R.id.pointView2);
		setRangeBar(R.id.rangeBarr,currentRange,"Current range: %d/%d  km");
		setRangeBar(R.id.pointBar,numberOfPoints,"Number of points: %d/%d");		
	}
	
	public void startGame(View view) {
		Intent intent = new Intent(this, RadarActivity.class);
	    intent.putExtra(RANGE, getBarProgres(R.id.rangeBarr));
	    intent.putExtra(POINTS, getBarProgres(R.id.pointBar));
	    startActivity(intent);
	}
	
	public int getBarProgres(final int seekBarId){
		SeekBar rangeBar = (SeekBar) findViewById(seekBarId);
		return rangeBar.getProgress();
	}
	
	private void setRangeBar(final int seekBarId, final TextView relatedView,final String formatter){
		SeekBar rangeBar = (SeekBar) findViewById(seekBarId);
		
		relatedView.setText(String.format(formatter, rangeBar.getProgress(),rangeBar.getMax()));
		
		rangeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			  
			  @Override
			  public void onProgressChanged(SeekBar rangeBar, int progresValue, boolean fromUser){
				  relatedView.setText(String.format(formatter, rangeBar.getProgress(),rangeBar.getMax()));
			  } 
	
			  @Override
			  public void onStartTrackingTouch(SeekBar rangeBar) {
				  //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
			  }
			
			  @Override
			  public void onStopTrackingTouch(SeekBar rangeBar) {
				  relatedView.setText(String.format(formatter, rangeBar.getProgress(),rangeBar.getMax()));
			  }
		   });
	}
}
