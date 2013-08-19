/*
	This file is part of BattleChat

	BattleChat is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BattleChat is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
*/

package com.ninetwozero.battlechat.activities;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ninetwozero.battlechat.R;

public class AboutActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupVersion();
        setupLink();
    }

    private void setupVersion() {
        String versionNumber = "Unknown";
        try {
            versionNumber = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.version)).setText(versionNumber);
    }

    private void setupLink() {
        findViewById(R.id.link).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.ninetwozero.com")));
                    }
                }
        );
        findViewById(R.id.email).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND).putExtra(
                                Intent.EXTRA_EMAIL,
                                new String[]{"support@ninetwozero.com"}
                        ).setType("plain/text");
                        startActivity(Intent.createChooser(intent, "Send an e-mail..."));
                    }
                }
        );
    }
}
