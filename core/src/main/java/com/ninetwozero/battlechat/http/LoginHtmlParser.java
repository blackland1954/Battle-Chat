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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

final public class LoginHtmlParser {

    private final Document mDocument;

    public LoginHtmlParser(final String html) {
        mDocument = Jsoup.parse(html);
    }

    public LoginHtmlParser(final Document document) {
        mDocument = document.clone();
    }

    public long getUserId() {
        String id = mDocument.select(".base-header-profile-dropdown-username .base-avatar-size-large").attr("rel");
        if (id.equals("")) {
            id = "0";
        }
        return Long.parseLong(id);
    }

    public String getUsername() {
        return mDocument.select(".header-profile-dropdown .profile > a > div").text();
    }

    public String getChecksum() {
        return mDocument.select("#profile-edit-form input[name=post-check-sum]").val();
    }

    public boolean hasErrorMessage() {
        Elements error = mDocument.select(".gate-login-errormsg.wfont");
        if (error.isEmpty()) {
            return false;
        } else {
            return error.hasText();
        }
    }

    public String getErrorMessage() {
        return mDocument.select(".gate-login-errormsg.wfont").first().text();
    }

    public boolean isLoggedIn() {
        return mDocument.select(".gate-login-errormsg").isEmpty();
    }

}
