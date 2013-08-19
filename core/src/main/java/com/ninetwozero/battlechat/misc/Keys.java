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

package com.ninetwozero.battlechat.misc;

public class Keys {

    public static class Session {
        public static final String USER_ID = "userId";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
        public static final String COOKIE_NAME = "sessionName";
        public static final String COOKIE_VALUE = "sessionValue";
        public static final String CHECKSUM = "sessionChecksum";

        private Session() {
        }
    }

    public static class Settings {
        public static final String PERSISTENT_NOTIFICATION = "persistent_notification";
        public static final String CHAT_INTERVAL = "chat_refresh_interval";
        public static final String BEEP_ON_NEW = "beep_on_new_message";

        private Settings() {
        }
    }

    private Keys() {
    }
}
