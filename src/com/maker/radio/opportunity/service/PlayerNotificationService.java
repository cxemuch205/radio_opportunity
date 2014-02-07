package com.maker.radio.opportunity.service;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.maker.radio.opportunity.R;
import com.maker.radio.opportunity.StartActivity;
import com.maker.radio.opportunity.constant.App;

public class PlayerNotificationService extends Service{
	
	private static final String TAG = "PlayerNotificationService";	
    
	private MediaPlayer player = null;
	private NotificationManager notMang = null;
    private NotificationCompat.Builder notPlayerBuilder = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");
		notMang = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		initializeMediaPlayer();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		switch (intent.getFlags()) {
		case App.PLAY_MEDIA_CODE_ACTION:
			startPlaying();
			break;
		case App.STOP_MEDIA_CODE_ACTION:
			stopPlaying();
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void initNotification(){
		Log.d(TAG, "initNotification");
		notPlayerBuilder = new NotificationCompat.Builder(getApplicationContext());
		
		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(getApplicationContext(), StartActivity.class);
		notificationIntent.putExtra("finish", true);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		notPlayerBuilder//.setContent(view)
						.setContentIntent(pendingIntent)
						.setWhen(System.currentTimeMillis())
						.setSmallIcon(android.R.drawable.ic_media_play)
						.setLights(Color.GREEN, 100, 100)
						.setContentTitle(getString(R.string.radio_name))
						.setOngoing(true)
						.setSubText(getString(R.string.happy_listening));
		
		Log.i(TAG, "Show notifications");
		Notification not = notPlayerBuilder.build();
		notMang.notify(App.NOTIFYCATION_PLAYER_ID, not);
	}
	
	private void initializeMediaPlayer() {
		Log.d(TAG, "initializeMediaPlayer()");
        player = new MediaPlayer();
        try {
            player.setDataSource(App.RADIO_URL);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {
        		Log.d(TAG, "onBufferingUpdate()");
        		Log.i("Buffering", "" + percent);       	
            }
        });
        
        player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.i(TAG, "OnCompletionListener");
			}
		});
        
        player.setOnSeekCompleteListener(new OnSeekCompleteListener() {
			
			@Override
			public void onSeekComplete(MediaPlayer mp) {
				Log.i(TAG, "OnSeekCompleteListener");
			}
		});
    }
	
	public void startPlaying() {
		initNotification();
		try {
        	player.prepareAsync();
		} catch (Exception e) {
			Log.e(TAG, "Crash: prepareAsync()");
		}
		
        player.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				player.start();
			}
		});
    }

    public void stopPlaying() {
    	Log.i(TAG, "stopPlaying()");
        if (player != null) {
            player.stop();
            player.release();
            initializeMediaPlayer();
            notMang.cancel(App.NOTIFYCATION_PLAYER_ID);
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		notMang.cancel(App.NOTIFYCATION_PLAYER_ID);
		if(player != null)
		{
			player.stop();
            player.release();
		}
	}
}
