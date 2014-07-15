package com.geraldgono.flappydodger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void play(View v) {
		Intent playIntent = new Intent(this, Game.class);
		startActivity(playIntent);

	}

	public void highscore(View v) {
		Intent scoreIntent = new Intent(this, HighScore.class);
		startActivity(scoreIntent);

	}

	public void setting(View v) {
		Intent settingIntent = new Intent(this, Setting.class);
		startActivity(settingIntent);

	}

	public void exit(View v) {
		System.exit(0);
	}

}
