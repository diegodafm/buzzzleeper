package br.com.dafm.android.buzzzleeper.activity;


import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.R.layout;
import br.com.dafm.android.buzzzleeper.R.menu;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
