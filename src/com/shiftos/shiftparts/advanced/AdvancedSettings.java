/*
 * Copyright (C) 2014-2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
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
package com.shiftos.shiftparts.advanced;

import android.content.Context;
import android.os.Bundle;
import android.util.ArraySet;

import com.shiftos.shiftparts.R;
import com.shiftos.shiftparts.SettingsPreferenceFragment;
import com.shiftos.shiftparts.search.BaseSearchIndexProvider;
import com.shiftos.shiftparts.search.Searchable;

import java.util.Set;

public class AdvancedSettings extends SettingsPreferenceFragment implements Searchable {

    private static final String ADVANCED_MODE = "advanced_mode";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.advanced_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static final Searchable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

        @Override
        public Set<String> getNonIndexableKeys(Context context) {
            final Set<String> result = new ArraySet<String>();

            result.add(ADVANCED_MODE);
            return result;
        }
    };
}
