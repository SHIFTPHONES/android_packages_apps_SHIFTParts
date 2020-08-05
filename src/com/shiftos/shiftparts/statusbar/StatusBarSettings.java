/*
 * Copyright (C) 2014-2015 The CyanogenMod Project
 *               2017-2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shiftos.shiftparts.statusbar;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import shiftos.preference.ShiftSystemSettingListPreference;
import shiftos.providers.ShiftSettings;

import com.shiftos.shiftparts.R;
import com.shiftos.shiftparts.SettingsPreferenceFragment;
import com.shiftos.shiftparts.search.BaseSearchIndexProvider;
import com.shiftos.shiftparts.search.Searchable;
import com.shiftos.shiftparts.utils.DeviceUtils;

import java.util.Set;

public class StatusBarSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Searchable {

    private static final String ICON_BLACKLIST = "icon_blacklist";
    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";
    private static final String STATUS_BAR_QUICK_QS_PULLDOWN = "qs_quick_pulldown";

    private static final int PULLDOWN_DIR_NONE = 0;
    private static final int PULLDOWN_DIR_RIGHT = 1;
    private static final int PULLDOWN_DIR_LEFT = 2;

    private static final String NETWORK_TRAFFIC_SETTINGS = "network_traffic_settings";
    private static final String STATUS_BAR_CLOCK_SETTINGS = "status_bar_clock_key";

    private ShiftSystemSettingListPreference mQuickPulldown;
    private PreferenceScreen mNetworkTrafficPref;
    private PreferenceScreen mClockIndicatorPref;

    private boolean mHasNotch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.status_bar_settings);

        mNetworkTrafficPref = (PreferenceScreen) findPreference(NETWORK_TRAFFIC_SETTINGS);
        mClockIndicatorPref = (PreferenceScreen) findPreference(STATUS_BAR_CLOCK_SETTINGS);

        mHasNotch = DeviceUtils.hasNotch(getActivity());
        if (mHasNotch) {
            getPreferenceScreen().removePreference(mNetworkTrafficPref);
        }

        mQuickPulldown =
                (ShiftSystemSettingListPreference) findPreference(STATUS_BAR_QUICK_QS_PULLDOWN);
        mQuickPulldown.setOnPreferenceChangeListener(this);
        updateQuickPulldownSummary(mQuickPulldown.getIntValue(0));
    }

    @Override
    public void onResume() {
        super.onResume();

        final String curIconBlacklist = Settings.Secure.getString(getContext().getContentResolver(),
                ICON_BLACKLIST);

        mClockIndicatorPref.setEnabled(
                !TextUtils.delimitedStringContains(curIconBlacklist, ',', "clock"));

        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            mQuickPulldown.setEntries(R.array.status_bar_quick_qs_pulldown_entries_rtl);
            mQuickPulldown.setEntryValues(R.array.status_bar_quick_qs_pulldown_values_rtl);
        }
        // Disable network traffic preferences if clock is centered in the status bar
        updateNetworkTrafficStatus(getClockPosition());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int value = Integer.parseInt((String) newValue);
        String key = preference.getKey();
        switch (key) {
            case STATUS_BAR_QUICK_QS_PULLDOWN:
                updateQuickPulldownSummary(value);
                break;
        }
        return true;
    }

    private void updateQuickPulldownSummary(int value) {
        String summary="";
        switch (value) {
            case PULLDOWN_DIR_NONE:
                summary = getResources().getString(
                    R.string.status_bar_quick_qs_pulldown_off);
                break;

            case PULLDOWN_DIR_LEFT:
            case PULLDOWN_DIR_RIGHT:
                summary = getResources().getString(
                    R.string.status_bar_quick_qs_pulldown_summary,
                    getResources().getString(value == PULLDOWN_DIR_LEFT
                        ? R.string.status_bar_quick_qs_pulldown_summary_left
                        : R.string.status_bar_quick_qs_pulldown_summary_right));
                break;
        }
        mQuickPulldown.setSummary(summary);
    }

    private void updateNetworkTrafficStatus(int clockPosition) {
        if (mHasNotch) {
            // Unconditional no network traffic for you
            return;
        }

        boolean isClockCentered = clockPosition == 1;
        mNetworkTrafficPref.setEnabled(!isClockCentered);
        mNetworkTrafficPref.setSummary(getResources().getString(isClockCentered ?
                R.string.network_traffic_disabled_clock :
                R.string.network_traffic_settings_summary
        ));
    }

    private int getClockPosition() {
        return ShiftSettings.System.getInt(getActivity().getContentResolver(),
                STATUS_BAR_CLOCK_STYLE, 2);
    }

    public static final Searchable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

        @Override
        public Set<String> getNonIndexableKeys(Context context) {
            final Set<String> result = new ArraySet<String>();

            if (DeviceUtils.hasNotch(context)) {
                result.add(NETWORK_TRAFFIC_SETTINGS);
            }
            return result;
        }
    };
}
