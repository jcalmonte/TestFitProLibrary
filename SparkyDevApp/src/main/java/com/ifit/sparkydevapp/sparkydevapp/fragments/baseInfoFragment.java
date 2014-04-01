package com.ifit.sparkydevapp.sparkydevapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ifit.sparky.fecp.FecpController;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.ifit.sparkydevapp.sparkydevapp.ItemListActivity}
 * in two-pane mode (on tablets)
 */
public abstract class BaseInfoFragment extends Fragment {

    protected FecpController mFecpCntrl;
    private String mIdString;
    private String mDisplayString;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BaseInfoFragment(FecpController fecpCntrl,String displayString, String itemId) {
        this.mFecpCntrl = fecpCntrl;
        this.mIdString = itemId;
        this.mDisplayString = displayString;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * These are the commands that we will be using on the startup
     */
    public abstract void addFragmentFecpCommands();

    /**
     * These are the commands that well be removed when we switch fragments
     */
    public abstract void deleteFragmentFecpCommands();

    @Override
    public String toString() {
        return this.mDisplayString;
    }

    public String getIdString()
    {
        return this.mIdString;
    }
}
