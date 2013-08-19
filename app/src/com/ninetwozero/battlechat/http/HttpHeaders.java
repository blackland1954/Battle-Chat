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

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class HttpHeaders {

    private HttpHeaders() {
    }

    public static class Post {

        public static final int NORMAL = 0;
        public static final int AJAX = 1;
        public static final int JSON = 2;

        private Post() {
        }

        private static final Header[] NORMAL_HEADERS = new Header[]{};
        private static final Header[] AJAX_HEADERS = new Header[]{
                new BasicHeader("Host", "battlelog.battlefield.com"),
                new BasicHeader("X-Requested-With", "XMLHttpRequest"),
                new BasicHeader("Accept-Encoding", "gzip, deflate"),
                new BasicHeader("Accept", "application/json, text/javascript, */*"),
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
                new BasicHeader("X-AjaxNavigation", "1")
        };
        private static final Header[] JSON_HEADERS = new Header[]{
                new BasicHeader("Host", "battlelog.battlefield.com"),
                new BasicHeader("X-Requested-With", "XMLHttpRequest"),
                new BasicHeader("Accept-Encoding", "gzip, deflate"),
                new BasicHeader("Accept", "application/json, text/javascript, */*"),
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
                new BasicHeader("Accept-Charset", "utf-8,ISO-8859-1;")
        };

        public static final Header[] getHeaders(int id) {
            switch (id) {
                case NORMAL:
                    return NORMAL_HEADERS;
                case AJAX:
                    return AJAX_HEADERS;
                case JSON:
                    return JSON_HEADERS;
                default:
                    return NORMAL_HEADERS;
            }
        }
    }

    public static class Get {

        public static final int NORMAL = 0;
        public static final int AJAX = 1;
        public static final int JSON = 2;

        private Get() {
        }

        private static final Header[] NORMAL_HEADERS = new Header[]{};

        private static final Header[] AJAX_HEADERS = new Header[]{
                new BasicHeader("X-Requested-With", "XMLHttpRequest"),
                new BasicHeader("X-AjaxNavigation", "1"),
                new BasicHeader("Accept", "application/json, text/javascript, */*"),
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        };

        private static final Header[] JSON_HEADERS = new Header[]{
                new BasicHeader("Accept", "application/json, text/javascript, */*"),
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
                new BasicHeader("X-JSON", "1"),
        };

        public static final Header[] getHeaders(int id) {
            switch (id) {
                case NORMAL:
                    return NORMAL_HEADERS;
                case AJAX:
                    return AJAX_HEADERS;
                case JSON:
                    return JSON_HEADERS;
                default:
                    return NORMAL_HEADERS;
            }
        }
    }
}
