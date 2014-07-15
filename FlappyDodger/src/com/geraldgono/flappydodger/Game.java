package com.geraldgono.flappydodger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {

	public MediaPlayer jump, eggtake, naruto;
	public GameLoop gameLoopThread;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// cancel the process if received a phone call
		final TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		TelephonyMgr.listen(new TeleListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

		// for removing the titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(new GameView(this));
	}

	public class GameView extends SurfaceView {
		public Bitmap cityskya, green, black, note1, worms, note2;
		public Bitmap normal, hit1, hit2, egg, exit, bmp, pauseb, playb;

		private SurfaceHolder holder;
		public int x = 0, z = 0, delay = 0, getx, gety, sound = 1;
		public int show = 0, sx, sy;
		public int cspeed = 0, b1speed = 0, b2speed = 0, gameover = 0;
		public int score = 0, health = 100, reset = 0;
		public int pausecount = 0, volume, power = 0, powerrun = 0,
				shieldrun = 0;

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		public GameView(Context context) {
			super(context);

			gameLoopThread = new GameLoop(this);
			holder = getHolder();

			holder.addCallback(new SurfaceHolder.Callback() {
				// @SuppressWarnings("deprecation")
				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					// for stoping the game
					gameLoopThread.setRunning(false);
					gameLoopThread.getThreadGroup().interrupt();
				}

				@SuppressLint("WrongCall")
				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					gameLoopThread.setRunning(true);
					gameLoopThread.start();

				}

				@Override
				public void surfaceChanged(SurfaceHolder holder, int format,
						int width, int height) {
				}
			});

			// getting the screen size
			final Display display = getWindowManager().getDefaultDisplay();

			sx = display.getWidth();
			sy = display.getHeight();
			;
			cspeed = sx / 2;
			b1speed = sx / 2;
			b2speed = sx / 2;
			powerrun = (3 * sx / 4);
			shieldrun = sx / 8;
			cityskya = BitmapFactory.decodeResource(getResources(),
					R.drawable.cityskya);

			normal = BitmapFactory.decodeResource(getResources(),
					R.drawable.normal);
			hit1 = BitmapFactory
					.decodeResource(getResources(), R.drawable.hit1);
			hit2 = BitmapFactory
					.decodeResource(getResources(), R.drawable.hit2);

			egg = BitmapFactory.decodeResource(getResources(), R.drawable.egg);
			exit = BitmapFactory
					.decodeResource(getResources(), R.drawable.exit);
			green = BitmapFactory.decodeResource(getResources(),
					R.drawable.green);
			black = BitmapFactory.decodeResource(getResources(),
					R.drawable.black);
			note1 = BitmapFactory.decodeResource(getResources(),
					R.drawable.note1);
			pauseb = BitmapFactory.decodeResource(getResources(),
					R.drawable.pauseb);
			worms = BitmapFactory.decodeResource(getResources(),
					R.drawable.worms);
			note2 = BitmapFactory.decodeResource(getResources(),
					R.drawable.note2);

			exit = Bitmap.createScaledBitmap(exit, 25, 25, true);
			pauseb = Bitmap.createScaledBitmap(pauseb, 25, 25, true);
			worms = Bitmap.createScaledBitmap(worms, 25, 25, true);
			note2 = Bitmap.createScaledBitmap(note2, sx, sy, true);
			normal = Bitmap.createScaledBitmap(normal, sx / 9, sy / 7, true);
			hit1 = Bitmap.createScaledBitmap(hit1, sx / 9, sy / 7, true);
			hit2 = Bitmap.createScaledBitmap(hit2, sx / 9, sy / 7, true);
			egg = Bitmap.createScaledBitmap(egg, sx / 16, sy / 18, true);

			cityskya = Bitmap.createScaledBitmap(cityskya, 2 * sx, sy, true);

			// health dec
			note1 = Bitmap.createScaledBitmap(note1, sx, sy, true);

			naruto = MediaPlayer.create(Game.this, R.raw.naruto);
			jump = MediaPlayer.create(Game.this, R.raw.jump);
			eggtake = MediaPlayer.create(Game.this, R.raw.eggtake);
		}

		// on touch method

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				show = 1;

				getx = (int) event.getX();
				gety = (int) event.getY();
				// exit
				if (getx < 25 && gety < 25) {
					// high score
					SharedPreferences pref = getApplicationContext()
							.getSharedPreferences("higher", MODE_PRIVATE);
					Editor editor = pref.edit();
					editor.putInt("score", score);
					editor.commit();
					System.exit(0);

				}
				// restart game
				if (getx > 91 && gety < 25) {
					if (health <= 0) {
						gameLoopThread.setPause(0);
						health = 100;
						score = 0;

					}
				}
				// pause game
				if ((getx > (sx - 25) && gety < 25 && pausecount == 0)) {

					gameLoopThread.setPause(1);
					naruto.stop();
					pausecount = 1;
				} else if (getx > (sx - 25) && gety < 25 && pausecount == 1) {
					gameLoopThread.setPause(0);
					naruto.start();
					pausecount = 0;
				}
			}

			return true;
		}

		@SuppressLint("WrongCall")
		@Override
		protected void onDraw(Canvas canvas) {

			// volume
			SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("higher", MODE_PRIVATE);
			Editor editor = pref.edit();
			volume = pref.getInt("volume", 0);
			if (volume == 0) {
				sound = 0;
			}

			canvas.drawColor(Color.BLACK);

			// background moving
			z = z - 15;
			if (z == -sx) {
				z = 0;
				canvas.drawBitmap(cityskya, z, 0, null);

			} else {
				canvas.drawBitmap(cityskya, z, 0, null);
			}

			// flying player

			x += 5;
			if (x == 20) {
				x = 5;
			}

			if (show == 0) {

				if (x % 2 == 0) {
					canvas.drawBitmap(hit2, sx / 16, 15 * sy / 18, null);

				} else {
					canvas.drawBitmap(normal, sx / 16, 15 * sy / 18, null);

				}

				// green hit
				if (b1speed == 20) {
					b1speed = sx;
					health -= 25;
					canvas.drawBitmap(note1, 1, 1, null);
				}

				if (b2speed == 15) {
					b2speed = sx;
					health -= 25;
					canvas.drawBitmap(note1, 1, 1, null);
				}

				// power take
				if (powerrun == 30) {
					powerrun = sx;
					health += 25;
					canvas.drawBitmap(note2, 1, 1, null);

				}

				if (powerrun == 30 && health == 100) {
					powerrun = sx;
					health += 0;
					canvas.drawBitmap(note2, 1, 1, null);

				}

				// egg take
				if (cspeed == 12) {
					cspeed = sx;
					score += 10;
					canvas.drawBitmap(note2, 1, 1, null);
				}
			}
			// power
			powerrun = powerrun - 10;
			canvas.drawBitmap(worms, powerrun, 15 * sy / 18, null);

			if (powerrun < 0) {
				powerrun = 3 * sx / 4;
			}

			// green
			b1speed = b1speed - 20;
			canvas.drawBitmap(green, b1speed, 15 * sy / 18, null); // bitmap
																	// position
			if (b1speed < 0) {
				b1speed = sx;
			}

			// black
			b2speed = b2speed - 15;
			canvas.drawBitmap(black, b2speed, 9 * sy / 18, null);
			if (b2speed < 0) {
				b2speed = sx;
			}

			// for soar

			if (show == 1) {
				if (sound == 1) {
					jump.start();
				}

				// canvas.drawBitmap(run2, sx / 16, 3 * sy / 4, null);
				canvas.drawBitmap(hit1, sx / 12, sy / 2, null); // score
				if (cspeed <= sx / 8 && cspeed >= sx / 16) {
					if (sound == 1) {
						eggtake.start();

					}
					cspeed = sx / 2;
					score += 10;

				}

				// soar-hold
				delay += 1;
				if (delay == 3) {
					show = 0;
					delay = 0;
				}
			}

			if (show == 1) {
				if (sound == 1) {
					jump.start();
				}

				canvas.drawBitmap(hit2, sx / 12, sy, null);

				delay += 0.5;
				if (delay == 2) {
					show = 0;
					delay = 0;
				}

			}

			// for egg
			cspeed = cspeed - 5;
			if (cspeed < 0) {
				cspeed = 2 * sx;
				canvas.drawBitmap(egg, cspeed, 12 * sy / 18, null);

			} else {
				canvas.drawBitmap(egg, cspeed, 12 * sy / 18, null);
			}

			// score
			final Paint paint = new Paint();
			paint.setColor(Color.BLUE);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setTextSize(30);
			paint.setTextAlign(Align.LEFT);
			canvas.drawText("Score :" + score, 3 * sx / 4, 25, paint);
			// exit
			canvas.drawBitmap(exit, 0, 0, null);
			if (sound == 1) {
				naruto.start();
				naruto.setLooping(true);
			} else {
				naruto.stop();
			}
			// health
			final Paint myPaint = new Paint();
			myPaint.setColor(Color.RED);
			myPaint.setStrokeWidth(20);
			myPaint.setAntiAlias(true);
			myPaint.setFakeBoldText(true);
			canvas.drawText("Health :" + health, 0, (sy / 6) - 4, myPaint);
			canvas.drawRect(0, sy / 6, health, sy / 6 + 8, myPaint);

			// game over
			if (health <= 0) {
				gameover = 1;

				// high score
				editor.putInt("score", score);
				editor.commit();

				canvas.drawText("GAMEOVER OVER", sx / 2, sy / 2, myPaint);
				canvas.drawText("YOUR SCORE : " + score, sx / 2, sy / 4,
						myPaint);
				canvas.drawText("Restart", 91, 25, myPaint);
				gameLoopThread.setPause(1);
				canvas.drawBitmap(cityskya, sx, sy, null);
			}
			// restart

			if (reset == 1) {
				gameLoopThread.setPause(0);
				health = 100;
				score = 0;
				naruto.start();
				jump.start();
				eggtake.start();
			}

			canvas.drawBitmap(pauseb, (sx - 25), 0, null);
		}
	}

	// phone state
	public class TeleListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				naruto.stop();
				System.exit(0);
			}
		}

	}

}
