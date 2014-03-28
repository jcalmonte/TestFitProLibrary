/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 3/27/2014
 * @version 1
 * Details.
 */
package com.ifit.sparkydevapp.sparkydevapp.fragments;

public interface FecpFragmentCmdHandler {


    /**
     * These are the commands that we will be using on the startup
     */
    void addFragmentFecpCommands();

    /**
     * These are the commands that well be removed when we switch fragments
     */
    void deleteFragmentFecpCommands();
}
