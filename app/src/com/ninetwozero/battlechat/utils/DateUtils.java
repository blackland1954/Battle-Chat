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

package com.ninetwozero.battlechat.utils;

import android.content.Context;

import com.ninetwozero.battlechat.R;

final public class DateUtils {
    public static final int MINUTE_IN_SECONDS = 60;
    public static final int HOUR_IN_SECONDS = 3600;
    public static final int DAY_IN_SECONDS = 86400;
    public static final int WEEK_IN_SECONDS = 604800;
    public static final int YEAR_IN_SECONDS = 31449600;

    public static String getRelativeTimeString(final Context c, final long d) {
        long dateStart = d;
        long dateNow = System.currentTimeMillis() / 1000;
        long dateDiff = dateNow - dateStart;
        String dateString;

        if (d == 0) {
            return "N/A";
        }

        dateDiff = dateDiff < 0 ? 0 : dateDiff;
        if ((dateDiff / MINUTE_IN_SECONDS) < 1) {
            dateString = getElapsedSeconds(c, dateDiff);
        } else if ((dateDiff / HOUR_IN_SECONDS) < 1) {
            dateString = getElapsedMinutes(c, dateDiff);
        } else if ((dateDiff / DAY_IN_SECONDS) < 1) {
            dateString = getElapsedHours(c, dateDiff);
        } else if ((dateDiff / WEEK_IN_SECONDS) < 1) {
            dateString = getElapsedDays(c, dateDiff);
        } else if ((dateDiff / YEAR_IN_SECONDS) < 1) {
            dateString = getElapsedWeeks(c, dateDiff);
        } else {
            dateString = getElapsedYears(c, dateDiff);
        }
        return dateString;
    }

    private static String getElapsedSeconds(final Context c, long dateDiff) {
        if (dateDiff == 1) {
            return c.getString(R.string.text_time_second);
        } else {
            return String.format(c.getString(R.string.text_time_second_p), dateDiff % MINUTE_IN_SECONDS);
        }
    }

    private static String getElapsedMinutes(final Context c, long dateDiff) {
        if (dateDiff / MINUTE_IN_SECONDS == 1) {
            return c.getString(R.string.text_time_min);
        } else {
            return String.format(c.getString(R.string.text_time_min_p), dateDiff / MINUTE_IN_SECONDS);
        }
    }

    private static String getElapsedHours(final Context c, long dateDiff) {
        if (dateDiff / HOUR_IN_SECONDS == 1) {
            return c.getString(R.string.text_time_hour);
        } else {
            return String.format(c.getString(R.string.text_time_hour_p), dateDiff / HOUR_IN_SECONDS);
        }
    }

    private static String getElapsedDays(final Context c, long dateDiff) {
        if (dateDiff / DAY_IN_SECONDS == 1) {
            return c.getString(R.string.text_time_day);
        } else {
            return String.format(c.getString(R.string.text_time_day_p), dateDiff / DAY_IN_SECONDS);
        }
    }

    private static String getElapsedWeeks(final Context c, long dateDiff) {
        if (dateDiff / WEEK_IN_SECONDS == 1) {
            return c.getString(R.string.text_time_week);
        } else {
            return String.format(c.getString(R.string.text_time_week_p), dateDiff / WEEK_IN_SECONDS);
        }
    }

    private static String getElapsedYears(final Context c, long dateDiff) {
        if (dateDiff / YEAR_IN_SECONDS == 1) {
            return c.getString(R.string.text_time_year);
        } else {
            return String.format(c.getString(R.string.text_time_year_p), dateDiff / YEAR_IN_SECONDS);
        }
    }

    private DateUtils() {
    }

}
