package com.epishie.btradar.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.epishie.btradar.model.Beacon;
import com.epishie.btradar.R;
import com.epishie.btradar.presenter.MainPresenter;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String KEY_BEACONS = "BEACONS";
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Beacon> beacons = null;
        if (savedInstanceState != null) {
            beacons = savedInstanceState.getParcelableArrayList(KEY_BEACONS);
        }
        mPresenter = new MainPresenter(this, beacons);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onActivityStart();
    }

    @Override
    protected void onStop() {
        mPresenter.onActivityStop();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_BEACONS, (ArrayList<Beacon>)mPresenter.getBeacons());
    }

    public MainPresenter getPresenter() {
        return mPresenter;
    }
}
