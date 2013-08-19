package com.ninetwozero.battlechat.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class LoginHtmlParserTest {

    private LoginHtmlParser mLoggedIn;
    private LoginHtmlParser mLoginError;

    public LoginHtmlParserTest() {
        try {
            mLoggedIn = new LoginHtmlParser(getFileAsString("/htm/index.htm"));
            mLoginError = new LoginHtmlParser(getFileAsString("/htm/index_error.htm"));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private String getFileAsString(String path) {
        InputStream stream = LoginHtmlParserTest.class.getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder output = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    @Test
    public void testDetectmLoginError() {
        Assert.assertFalse(mLoggedIn.hasErrorMessage());
        Assert.assertTrue(mLoginError.hasErrorMessage());
    }

    @Test
    public void testGetUserId() {
        Assert.assertEquals(mLoggedIn.getUserId(), 0);
    }

    @Test
    public void testGetUsername() {
        Assert.assertEquals(mLoggedIn.getUsername(), "ninetwozero");
    }

    @Test
    public void testGetChecksum() {
        Assert.assertThat(mLoggedIn.getChecksum(), CoreMatchers.not(""));
    }

    @Test
    public void testDetectmLoggedIn() {
        Assert.assertTrue(mLoggedIn.isLoggedIn());
        Assert.assertFalse(mLoginError.isLoggedIn());
    }
}
