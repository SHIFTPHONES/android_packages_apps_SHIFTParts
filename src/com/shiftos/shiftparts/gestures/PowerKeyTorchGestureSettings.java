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
package com.shiftos.shiftparts.gestures;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.util.ArraySet;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.shiftos.shiftparts.R;
import com.shiftos.shiftparts.SettingsPreferenceFragment;
import com.shiftos.shiftparts.search.BaseSearchIndexProvider;
import com.shiftos.shiftparts.search.Searchable;
import com.shiftos.shiftparts.utils.DeviceUtils;

import static com.shiftos.internal.util.DeviceKeysConstants.*;

import java.util.Set;

import shiftos.providers.ShiftSettings;

public class PowerKeyTorchGestureSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Searchable {

    private static final String KEY_TORCH_LONG_PRESS_POWER_GESTURE =
            "torch_long_press_power_gesture";
    private static final String KEY_TORCH_LONG_PRESS_POWER_TIMEOUT =
            "torch_long_press_power_timeout";

    private SwitchPreference mTorchLongPressPowerGesture;
    private ListPreference mTorchLongPressPowerTimeout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.gestures_power_key_torch);

        final Resources res = getResources();
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        final boolean hasPowerKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_POWER);

        mTorchLongPressPowerGesture =
                (SwitchPreference) findPreference(KEY_TORCH_LONG_PRESS_POWER_GESTURE);
        final int torchLongPressPowerTimeout = ShiftSettings.System.getInt(resolver,
                ShiftSettings.System.TORCH_LONG_PRESS_POWER_TIMEOUT, 0);
        mTorchLongPressPowerTimeout = initList(KEY_TORCH_LONG_PRESS_POWER_TIMEOUT,
                torchLongPressPowerTimeout);

        if (!hasPowerKey || !DeviceUtils.deviceSupportsFlashLight(getActivity())) {
            prefScreen.removePreference(mTorchLongPressPowerGesture);
            prefScreen.removePreference(mTorchLongPressPowerTimeout);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mTorchLongPressPowerTimeout) {
            handleListChange(mTorchLongPressPowerTimeout, newValue,
                    ShiftSettings.System.TORCH_LONG_PRESS_POWER_TIMEOUT);
            return true;
        }
        return false;
    }

    private ListPreference initList(String key, int value) {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference(key);
        if (list == null) return null;
        list.setValue(Integer.toString(value));
        list.setSummary(list.getEntry());
        list.setOnPreferenceChangeListener(this);
        return list;
    }

    private void handleListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
        pref.setSummary(pref.getEntries()[index]);
        ShiftSettings.System.putInt(getContentResolver(), setting, Integer.valueOf(value));
    }

    public static final Searchable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

        @Override
        public Set<String> getNonIndexableKeys(Context context) {
            final Set<String> result = new ArraySet<String>();
            final boolean hasPowerKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_POWER);

            if (!hasPowerKey) {
                result.add(KEY_TORCH_LONG_PRESS_POWER_GESTURE);
                result.add(KEY_TORCH_LONG_PRESS_POWER_TIMEOUT);
            }
            return result;
        }
    };
}
