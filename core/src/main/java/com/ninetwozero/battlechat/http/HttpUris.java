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

package com.ninetwozero.battlechat.http;

public class HttpUris {

    public static final String MAIN = "http://battlelog.battlefield.com/bf3";
    public static final String MAIN_SECURE = "https://battlelog.battlefield.com/bf3";
    public static final String LOGIN = MAIN_SECURE + "/gate/login";
    public static final String LOGOUT = MAIN + "/session/logout/";

    public class Chat {
        public static final String FRIENDS = MAIN + "/comcenter/sync/";
        public static final String MESSAGES = MAIN + "/comcenter/getChatId/{USER_ID}/";
        public static final String SEND = MAIN + "/comcenter/sendChatMessage/";
        public static final String CLOSE = MAIN + "/comcenter/hideChat/{CHAT_ID}/";

        private Chat() {
        }
    }

    private HttpUris() {
    }
}
