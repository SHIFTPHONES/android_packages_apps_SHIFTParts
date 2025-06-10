/*
 * SPDX-FileCopyrightText: SHIFT GmbH
 * SPDX-License-Identifier: Apache-2.0
 */

package eco.shift.os.parts;

import android.os.Bundle;

import org.lineageos.lineageparts.R;
import org.lineageos.lineageparts.SettingsPreferenceFragment;

public class MetricsSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.shift_metrics);
    }

}
