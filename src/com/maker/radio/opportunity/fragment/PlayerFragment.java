package com.maker.radio.opportunity.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.maker.radio.opportunity.R;
import com.maker.radio.opportunity.constant.App;
import com.maker.radio.opportunity.service.PlayerNotificationService;
import com.maker.radio.opportunity.utils.Tools;
import com.maker.radio.opportunity.utils.onDialogClickListener;

public class PlayerFragment extends SherlockFragment{
	
	private static final String TAG = "PlayerFragment";
	
	private View view = null;
	private ToggleButton tbPlayStop = null;
	private SeekBar sbVolumeControl = null;
	private TextView tvVolProgress = null;
    
    private PlayerNotificationService playerService = null;
    private Intent intentPlayerService = null;
    private AudioManager audioManager = null;
    private Timer timerUpdateVol = null;
    private ProgressDialog pd = null;
    private Handler handler = null;
    private NotificationManager notMang;
    
    private static final int TIMER_REFRESH = 100;
    private SharedPreferences pref;
    
    //private NotificationManager notMang = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		pref = Tools.getPreferences(getSherlockActivity());
		notMang = (NotificationManager)getSherlockActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		
		audioManager = ((AudioManager)getSherlockActivity().getSystemService(Context.AUDIO_SERVICE));
		pd = new ProgressDialog(getSherlockActivity());
		pd.setMessage(getString(R.string.buffering));
		pd.setCanceledOnTouchOutside(false);
		pd.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.cancel), pdClickCancelListener);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		view = inflater.inflate(R.layout.player_layout, null);
		tbPlayStop = (ToggleButton)view.findViewById(R.id.toggleButton_play_stop);
		sbVolumeControl = (SeekBar)view.findViewById(R.id.seekBar_volume);
		tvVolProgress = (TextView)view.findViewById(R.id.tv_progress_vol);
		
		playerService = new PlayerNotificationService();
		intentPlayerService = new Intent(getSherlockActivity(), playerService.getClass());
		
		timerUpdateVol = new Timer();
		handler = new Handler();
		//notMang = (NotificationManager)getSherlockActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		
		return view;
	};
	
	private OnCheckedChangeListener checkedChangeToggleBtnListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			pref.edit().putBoolean(App.PREF_PLAY_STATUS, isChecked).commit();
			if(isChecked){
				getSherlockActivity().startService(intentPlayerService.setFlags(App.STOP_MEDIA_CODE_ACTION));
			}
			else
			{
				if(Tools.checkInternetConnection(getSherlockActivity())){
					getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
					getSherlockActivity().startService(intentPlayerService.setFlags(App.PLAY_MEDIA_CODE_ACTION));
				}
				else
				{
					tbPlayStop.setChecked(true);
					Tools.showToast(getSherlockActivity(), getString(R.string.no_connection));
				}
			}
		}
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getSherlockActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		tbPlayStop.setOnCheckedChangeListener(checkedChangeToggleBtnListener);
		sbVolumeControl.setMax(15);
		int volDefValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		tvVolProgress.setText(""+volDefValue);
		sbVolumeControl.setProgress(volDefValue);
		sbVolumeControl.setOnSeekBarChangeListener(sbProgressListener);
		
		Animation animChecked = AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.alpha_revers_animation);
		tbPlayStop.setAnimation(animChecked);
		timerUpdateVol.schedule(timerTaskCheckPlaying, 500, TIMER_REFRESH);
		if(pref.contains(App.PREF_PLAY_STATUS)){
			tbPlayStop.setChecked(pref.getBoolean(App.PREF_PLAY_STATUS, false));
		}		
	}
	
	private TimerTask timerTaskCheckPlaying = new TimerTask() {
		
		@Override
		public void run() {
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					updateVolLevel();
					if(audioManager.isMusicActive() == false && tbPlayStop.isChecked() == false){
						if(Tools.checkInternetConnection(getSherlockActivity())){
							if(pd != null) pd.show();
						}
						else{
							Tools.showToast(getSherlockActivity(), getString(R.string.no_connection));
							tbPlayStop.setChecked(true);
						}
                	}
                	else
                	{
                		if(pd != null) pd.dismiss();
                	}
				}
			}, 0);
		}
	};
	
	private OnClickListener pdClickCancelListener = new onDialogClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			tbPlayStop.setChecked(true);
			
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		getSherlockActivity().stopService(intentPlayerService);
		timerUpdateVol.cancel();
		pref.edit().putBoolean(App.PREF_PLAY_STATUS, true).commit();
		notMang.cancel(App.NOTIFYCATION_PLAYER_ID);
		timerUpdateVol.cancel();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume()");
		if(pref.contains(App.PREF_PLAY_STATUS)){
			tbPlayStop.setChecked(pref.getBoolean(App.PREF_PLAY_STATUS, false));
		}
	}
	
	private OnSeekBarChangeListener sbProgressListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			tvVolProgress.setText(""+progress);
		}
	};
    
    private void updateVolLevel(){
    	int volDefValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		tvVolProgress.setText(""+volDefValue);
		sbVolumeControl.setProgress(volDefValue);
    }
}
