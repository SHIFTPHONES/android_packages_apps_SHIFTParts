/*
 * Copyright (C) 2020 Shift GmbH
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
import android.text.format.DateFormat;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;

import shiftos.preference.ShiftSystemSettingListPreference;
import shiftos.preference.SecureSettingSwitchPreference;
import shiftos.providers.ShiftSettings;

import com.shiftos.shiftparts.R;
import com.shiftos.shiftparts.SettingsPreferenceFragment;
import com.shiftos.shiftparts.search.BaseSearchIndexProvider;
import com.shiftos.shiftparts.search.Searchable;
import com.shiftos.shiftparts.utils.DeviceUtils;

import java.util.Set;

public class StatusBarClockSettings extends SettingsPreferenceFragment
        implements Searchable {

    private static final String ICON_BLACKLIST = "icon_blacklist";
    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";
    private static final String STATUS_BAR_CLOCK_SECONDS = "clock_seconds";
    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";

    private ShiftSystemSettingListPreference mStatusBarClock;
    private SecureSettingSwitchPreference mStatusBarClockSeconds;
    private ShiftSystemSettingListPreference mStatusBarAmPm;

    private boolean mHasNotch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.status_bar_clock_settings);

        mHasNotch = DeviceUtils.hasNotch(getActivity());

        mStatusBarAmPm =
                (ShiftSystemSettingListPreference) findPreference(STATUS_BAR_AM_PM);
        mStatusBarClockSeconds =
                (SecureSettingSwitchPreference) findPreference(STATUS_BAR_CLOCK_SECONDS);
        mStatusBarClock =
                (ShiftSystemSettingListPreference) findPreference(STATUS_BAR_CLOCK_STYLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePrefsStatus();
    }

    private void updatePrefsStatus() {
        final String curIconBlacklist = Settings.Secure.getString(getContext().getContentResolver(),
                ICON_BLACKLIST);

        final boolean available = !TextUtils.delimitedStringContains(
                curIconBlacklist, ',', "clock");
        mStatusBarClock.setEnabled(available);
        mStatusBarClockSeconds.setEnabled(available);
        final boolean is24h = DateFormat.is24HourFormat(getActivity());
        mStatusBarAmPm.setEnabled(available && !is24h);
        if (is24h) {
            mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
        }

        final boolean disallowCenteredClock = mHasNotch || getNetworkTrafficStatus() != 0;

        // Adjust status bar preferences for RTL
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            if (disallowCenteredClock) {
                mStatusBarClock.setEntries(R.array.status_bar_clock_position_entries_notch_rtl);
                mStatusBarClock.setEntryValues(R.array.status_bar_clock_position_values_notch_rtl);
            } else {
                mStatusBarClock.setEntries(R.array.status_bar_clock_position_entries_rtl);
                mStatusBarClock.setEntryValues(R.array.status_bar_clock_position_values_rtl);
            }
        } else if (disallowCenteredClock) {
            mStatusBarClock.setEntries(R.array.status_bar_clock_position_entries_notch);
            mStatusBarClock.setEntryValues(R.array.status_bar_clock_position_values_notch);
        } else {
            mStatusBarClock.setEntries(R.array.status_bar_clock_position_entries);
            mStatusBarClock.setEntryValues(R.array.status_bar_clock_position_values);
        }
    }

    private int getNetworkTrafficStatus() {
        return ShiftSettings.Secure.getInt(getActivity().getContentResolver(),
                ShiftSettings.Secure.NETWORK_TRAFFIC_MODE, 0);
    }

    public static final Searchable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

        @Override
        public Set<String> getNonIndexableKeys(Context context) {
            return new ArraySet<String>();
        }
    };
}
