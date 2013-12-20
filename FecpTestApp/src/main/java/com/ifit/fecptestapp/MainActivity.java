package com.ifit.fecptestapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ifit.sparky.fecp.communication.UsbComm;

import java.nio.ByteBuffer;

public class MainActivity extends Activity {

    UsbComm usbComm;

    private Handler m_handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        usbComm = new UsbComm(MainActivity.this);

        m_handler = new Handler();
        m_statusChecker.run();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usbComm.onResumeUSB(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }
    Runnable m_statusChecker = new Runnable()
    {
        private int m_interval = 1; // ms of delay

        @Override
        public void run() {
            ByteBuffer buff = ByteBuffer.allocate(64);
            for(int i = 20; i < 64; i++)
                buff.put(i, (byte)i);
            usbComm.sendCmdBuffer(buff);

            m_handler.postDelayed(m_statusChecker, m_interval);
        }
    };

}
