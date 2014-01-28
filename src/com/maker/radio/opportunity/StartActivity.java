package com.maker.radio.opportunity;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.maker.radio.opportunity.constant.App;
import com.maker.radio.opportunity.fragment.InfoFragment;
import com.maker.radio.opportunity.fragment.PlayerFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

public class StartActivity extends SherlockFragmentActivity {
	
	private FragmentManager fragmentManager = null;
	private FrameLayout framePlayer = null;
	private FrameLayout frameInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		getSupportActionBar().hide();
		framePlayer = (FrameLayout)findViewById(R.id.frame_player);
		frameInfo = (FrameLayout)findViewById(R.id.frame_info);
		
		fragmentManager = getSupportFragmentManager();
		
		fragmentManager.beginTransaction().replace(R.id.frame_info, new InfoFragment(), "INFO_FRAGMENT").commit();
		fragmentManager.beginTransaction().add(R.id.frame_info, new PlayerFragment(), "PLAYER_FRAGMENT").commit();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(App.ACTION_GROUP_DEFAULT, App.ACTION_LINKS, 0, getString(R.string.action_links)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(App.ACTION_GROUP_DEFAULT, App.ACTION_DONATE, 0, getString(R.string.action_donate_money)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(App.ACTION_GROUP_DEFAULT, App.ACTION_ABOUT_US, 0, getString(R.string.action_about_us)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(App.ACTION_GROUP_DEFAULT, App.ACTION_SETTING, 0, getString(R.string.action_settings)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case App.ACTION_ABOUT_US:
			
			break;
		case App.ACTION_DONATE:
			
			break;
		case App.ACTION_LINKS:
			
			break;
		case App.ACTION_SETTING:
			
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
