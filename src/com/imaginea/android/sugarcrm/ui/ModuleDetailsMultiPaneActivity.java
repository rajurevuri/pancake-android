/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ModuleDetailMultiPaneActivity 
 * Description : A Multi-pane activity, consisting of a {@link ModuleListFragment}, and.
 {@link ModuleDetailFragment}.This activity requires API level 11 or greater
 ******************************************************************************/

package com.imaginea.android.sugarcrm.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.EditModuleDetailActivity;
import com.imaginea.android.sugarcrm.EditModuleDetailFragment;
import com.imaginea.android.sugarcrm.ModuleDetailActivity;
import com.imaginea.android.sugarcrm.ModuleDetailFragment;
import com.imaginea.android.sugarcrm.ModuleImageListFragment;
import com.imaginea.android.sugarcrm.ModuleImageListFragment.OnItemSelectedListener;
import com.imaginea.android.sugarcrm.ModuleListFragment;
import com.imaginea.android.sugarcrm.ModulesActivity;
import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.SugarCrmApp;
import com.imaginea.android.sugarcrm.SugarCrmSettings;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class ModuleDetailsMultiPaneActivity.
 */
public class ModuleDetailsMultiPaneActivity extends BaseMultiPaneActivity
        implements OnItemSelectedListener {
    /** The menu layout. */

    ModuleListFragment moduleListFragment;
    ModuleImageListFragment moduleImageListFragment;
    ModuleDetailFragment moduleDetailFragment;
    private TextView mSettingsView;
    private static final String TAG = ModuleDetailsMultiPaneActivity.class
            .getSimpleName();

    /* menu Drop Down */
    LinearLayout menuLayout;

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules);

        moduleDetailFragment = (ModuleDetailFragment) getSupportFragmentManager()
                .findFragmentByTag("module_detail");
        moduleListFragment = (ModuleListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_frag);

        moduleImageListFragment = (ModuleImageListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_imagefrag);

        mSettingsView = (TextView) findViewById(R.id.settingsTv);
        menuLayout = (LinearLayout) findViewById(R.id.settings_menu);
        final View transparentView = findViewById(R.id.transparent_view);
        mSettingsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Set Action to Settings Button */
                Util.startSettingsActivity(ModuleDetailsMultiPaneActivity.this);
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
                                .getSugarRestUrl(ModuleDetailsMultiPaneActivity.this);
                        try {
                            Rest.logoutSugarCRM(restUrl, sessionId);
                        } catch (final SugarCrmException e) {
                            Log.e(TAG, "Error While Logging out:" + e);
                        }
                    }
                }).start();

                Util.logout(ModuleDetailsMultiPaneActivity.this);
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
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity#
     * onSubstituteFragmentForActivityLaunch(java.lang.String)
     */
    @Override
    public FragmentReplaceInfo onSubstituteFragmentForActivityLaunch(
            final String activityClassName) {
        if (ModulesActivity.class.getName().equals(activityClassName))
            return new FragmentReplaceInfo(ModuleListFragment.class, "modules",
                    R.id.fragment_container_module_detail);
        else if (ModuleDetailActivity.class.getName().equals(activityClassName))

            return new FragmentReplaceInfo(ModuleDetailFragment.class,
                    "module_detail", R.id.fragment_container_module_detail);
        else if (EditModuleDetailActivity.class.getName().equals(
                activityClassName))

            return new FragmentReplaceInfo(EditModuleDetailFragment.class,
                    "module_detail", R.id.fragment_container_module_detail);
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
        moduleListFragment.reloadListData(moduleName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.FragmentActivity#onConfigurationChanged(android
     * .content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            moduleListFragment.onLandscapeChange();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            moduleListFragment.onPortraitChange();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        if (moduleListFragment.bSearch == true) {

            final SearchView searchView = (SearchView) findViewById(R.id.searchView);
            final ImageView backView = (ImageView) findViewById(R.id.actionbar_back);
            final ImageView logoview = (ImageView) findViewById(R.id.actionbar_logo);

            searchView.setVisibility(View.GONE);
            searchView.setQuery("", false);
            backView.setVisibility(View.GONE);
            logoview.setVisibility(View.VISIBLE);
            moduleListFragment.setUpActionBar();
            moduleListFragment.bSearch = false;
        } else {
            final Intent myIntent;
            myIntent = new Intent(this, RecentModuleMultiPaneActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            myIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);
            startActivity(myIntent);

        }
    }

}
