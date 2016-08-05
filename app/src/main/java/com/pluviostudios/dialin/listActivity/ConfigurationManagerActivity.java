package com.pluviostudios.dialin.listActivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.common.primitives.Ints;
import com.pluviostudios.dialin.R;
import com.pluviostudios.dialin.data.StorageManager;
import com.pluviostudios.dialin.mainActivity.MainActivity;
import com.pluviostudios.dialin.widget.WidgetManager;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by spectre on 8/2/16.
 */
public class ConfigurationManagerActivity extends AppCompatActivity implements ConfigurationListFragment.OnConfigurationSelected {

    public static final String TAG = "ConfigManagerActivity";

    private static final String STATE_PAGE_INDEX = "extra_page";

    @BindView(R.id.activity_configuration_manager_viewpager) ViewPager mViewPager;
    @BindView(R.id.activity_configuration_manager_titles) TitlePageIndicator mTitlePageIndicator;

    private int mWidgetId = 0;
    private int mWidgetButtonCount = 0;

    // If this activity has been launched as a configuration activity, I only want to show configurations with the same button count
    // Otherwise, I would want to display all the options.
    // Due to this you'll see some annoying code snippits such as within getPageTitle() where I figure out whether to show 2 pages or 1. 5x1 or 4x1 or both.
    private static final int[] POSSIBLE_SIZES = new int[]{4, 5};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_manager);
        ButterKnife.bind(this);

        // Check to see if this activity was started by a widget
        if (getIntent().hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {

            //Get mWidgetId (Will increment every time a widget is added to the home screen)
            mWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

            //Determine button count
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            Bundle options = appWidgetManager.getAppWidgetOptions(mWidgetId);
            int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            mWidgetButtonCount = (width + 30) / 70; // Homescreen tile = n * 70 - 30

        }

        // Set the pager adapter. Each page will contain a list of Configurations of a specific button count
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public CharSequence getPageTitle(int position) {
                return ((mWidgetButtonCount != 0) ? mWidgetButtonCount : POSSIBLE_SIZES[position]) + "x1";
            }

            @Override
            public Fragment getItem(int position) {
                return ConfigurationListFragment.buildConfigListFragment((mWidgetButtonCount == 0) ? POSSIBLE_SIZES[position] : mWidgetButtonCount);
            }

            @Override
            public int getCount() {
                return mWidgetButtonCount != 0 ? 1 : POSSIBLE_SIZES.length;
            }

        });

        mTitlePageIndicator.setViewPager(mViewPager);
        mTitlePageIndicator.setTextColor(getResources().getColor(android.R.color.primary_text_light)); // TODO find out how to do this in XML
        mTitlePageIndicator.setSelectedColor(getResources().getColor(android.R.color.primary_text_light)); // TODO find out how to do this in XML
        mViewPager.addOnPageChangeListener(mTitlePageIndicator);

    }

    // Called when the user is attempting to edit a configuration
    @Override
    public void onConfigurationEdit(String configurationTitle, long configurationId) {

        // Launch the configuration activity
        Intent intent = MainActivity.buildMainActivity(this,
                configurationTitle,
                configurationId,
                (mWidgetButtonCount != 0) ? mWidgetButtonCount : POSSIBLE_SIZES[mViewPager.getCurrentItem()]);
        startActivityForResult(intent, MainActivity.EDIT_CONFIG_RESULT_CODE);

    }

    // Called then the user is attempting to create a new configuration
    @Override
    public void onNewConfiguration() {

        int buttonCount = (mWidgetButtonCount != 0) ? mWidgetButtonCount : POSSIBLE_SIZES[mViewPager.getCurrentItem()];

        Intent intent = MainActivity.buildMainActivityForNewConfiguration(this,
                buttonCount + "x1 New Configuration",
                buttonCount);
        startActivityForResult(intent, MainActivity.EDIT_CONFIG_RESULT_CODE);

    }

    // Called when the user has selected a configuration for their widget
    @Override
    public void onConfigurationSelected(long configurationId) {

        // If the application was started by a widget
        if (mWidgetId != 0) {
            // Add this widget to the database and attach it to this configuration
            WidgetManager.addWidgetToDB(this, mWidgetId, configurationId);
            Intent data = new Intent();
            data.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
            setResult(RESULT_OK, data);
            finish();
        }

    }

    // Called when the user is done configuring a layout
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If RESULT_OK
        if (requestCode == MainActivity.EDIT_CONFIG_RESULT_CODE && resultCode == RESULT_OK) {

            ArrayList<Integer> affectedWidgetIds = data.getExtras().getIntegerArrayList(StorageManager.EXTRA_AFFECTED_APPWIDGETIDS);
            if (affectedWidgetIds != null) {
                WidgetManager.updateWidgets(this, Ints.toArray(affectedWidgetIds));
            }

        }

    }

}
