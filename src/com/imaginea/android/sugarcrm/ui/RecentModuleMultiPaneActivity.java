/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : RecentModuleMultiPaneActivity 
 * Description : A Multi-pane activity, consisting of a {@link RecentModuleListFragment}, and.
 {@link ModuleDetailFragment}.This activity requires API level 11 or greater
 ******************************************************************************/

package com.imaginea.android.sugarcrm.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.EditModuleDetailActivity;
import com.imaginea.android.sugarcrm.EditModuleDetailFragment;
import com.imaginea.android.sugarcrm.ModuleDetailActivity;
import com.imaginea.android.sugarcrm.ModuleDetailFragment;
import com.imaginea.android.sugarcrm.ModuleImageListFragment.OnItemSelectedListener;
import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.RecentModuleActivity;
import com.imaginea.android.sugarcrm.RecentModuleListFragment;
import com.imaginea.android.sugarcrm.SugarCrmApp;
import com.imaginea.android.sugarcrm.SugarCrmSettings;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class RecentModuleMultiPaneActivity.
 */
public class RecentModuleMultiPaneActivity extends BaseMultiPaneActivity
        implements OnItemSelectedListener {

    private static final String TAG = RecentModuleMultiPaneActivity.class
            .getSimpleName();
    RecentModuleListFragment moduleListFragment;

    /* menu Drop Down */
    LinearLayout menuLayout;

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_modules);

        ModuleDetailFragment moduleDetailFragment = (ModuleDetailFragment) getSupportFragmentManager()
                .findFragmentByTag("module_detail");
        moduleListFragment = (RecentModuleListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_frag);
        menuLayout = (LinearLayout) findViewById(R.id.settings_menu);
        final TextView settingsView = (TextView) findViewById(R.id.settingsTv);
        final View transparentView = (View) findViewById(R.id.transparent_view);
        settingsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Set Action to Settings Button */
                Util.startSettingsActivity(RecentModuleMultiPaneActivity.this);
                menuLayout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
            }
        });

        final TextView logoutView = (TextView) findViewById(R.id.logoutTv);
        logoutView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Set Action to Settings Button */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String sessionId = ((SugarCrmApp) SugarCrmApp.app)
                                .getSessionId();
                        final String restUrl = SugarCrmSettings
                                .getSugarRestUrl(RecentModuleMultiPaneActivity.this);
                        try {
                            Rest.logoutSugarCRM(restUrl, sessionId);
                        } catch (final SugarCrmException e) {
                            Log.e(TAG, "Error While Logging out:" + e);
                        }
                    }
                }).start();

                Util.logout(RecentModuleMultiPaneActivity.this);
                menuLayout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
            }
        });

        if (moduleDetailFragment == null) {
            moduleDetailFragment = new ModuleDetailFragment();
            moduleDetailFragment
                    .setArguments(intentToFragmentArguments(getIntent()));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container_module_detail,
                            moduleDetailFragment, "module_detail").commit();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.imaginea.android.sugarcrm.ui.BaseActivity#onPostCreate(android.os
     * .Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();

        final ViewGroup detailContainer = (ViewGroup) findViewById(R.id.fragment_container_module_detail);
        if (detailContainer != null && detailContainer.getChildCount() > 0) {
            findViewById(R.id.fragment_container_module_detail)
                    .setBackgroundColor(0xffffffff);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity#
     * onSubstituteFragmentForActivityLaunch(java.lang.String)
     */
    @Override
    public FragmentReplaceInfo onSubstituteFragmentForActivityLaunch(
            String activityClassName) {
        if (RecentModuleActivity.class.getName().equals(activityClassName))
            // this won't happen as Module list fragment is not reloaded
            return new FragmentReplaceInfo(RecentModuleListFragment.class,
                    "modules", R.id.fragment_container_module_detail);
        else if (ModuleDetailActivity.class.getName().equals(activityClassName)) {
            findViewById(R.id.fragment_container_module_detail)
                    .setBackgroundColor(0xffffffff);
            return new FragmentReplaceInfo(ModuleDetailFragment.class,
                    "module_detail", R.id.fragment_container_module_detail);
        } else if (EditModuleDetailActivity.class.getName().equals(
                activityClassName)) {
            findViewById(R.id.fragment_container_module_detail)
                    .setBackgroundColor(0xffffffff);
            return new FragmentReplaceInfo(EditModuleDetailFragment.class,
                    "module_detail", R.id.fragment_container_module_detail);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.imaginea.android.sugarcrm.ModuleImageListFragment.OnItemSelectedListener
     * #onItemSelected(java.lang.String)
     */
    @Override
    public void onItemSelected(String moduleName) {

    }
}
