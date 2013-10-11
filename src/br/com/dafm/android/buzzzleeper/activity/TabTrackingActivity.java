package br.com.dafm.android.buzzzleeper.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;

public class TabTrackingActivity extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracking_tab);

		Bundle extras = getIntent().getExtras();
		String idBlrAddress = null;
		if (extras != null) {
			idBlrAddress = extras.get("BLR_ADDRESS_ID").toString();
		}

		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec tabSpec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, TrackingActivity.class);
		intent.putExtra("BLR_ADDRESS_ID", idBlrAddress);

		tabSpec = createTab(intent,getText(R.string.info),getText(R.string.info), R.drawable.ic_tab_info_selector);
	    tabHost.addTab(tabSpec);

		intent = new Intent().setClass(this, MapTrackingActivity.class);
		intent.putExtra("BLR_ADDRESS_ID", idBlrAddress);
		
		tabSpec = createTab(intent,getText(R.string.map),getText(R.string.map), R.drawable.ic_tab_map_selector);
		tabHost.addTab(tabSpec);

		tabHost.setCurrentTab(0);
	}
	
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	
	private TabSpec createTab(final Intent intent, final CharSequence charSequence,final CharSequence charSequence2, final int drawable){
        final View tab = LayoutInflater.from(getTabHost().getContext()).inflate(R.layout.tab, null);
        ((TextView)tab.findViewById(R.id.tab_text)).setText(charSequence2);
        ((ImageView)tab.findViewById(R.id.tab_icon)).setImageResource(drawable);
        return getTabHost().newTabSpec((String) charSequence).setIndicator(tab).setContent(intent);
    }
}
