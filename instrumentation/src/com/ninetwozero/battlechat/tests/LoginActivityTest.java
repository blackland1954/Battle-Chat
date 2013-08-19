package com.ninetwozero.battlechat.tests;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.activities.LoginActivity;
import com.squareup.spoon.Spoon;

import static org.fest.assertions.api.ANDROID.assertThat;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Instrumentation instrumentation;
    private LoginActivity activity;

    private EditText email;
    private EditText password;
    private Button signIn;
    private CheckBox checkbox;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        instrumentation = getInstrumentation();
        activity = getActivity();

        checkbox = (CheckBox) getActivity().findViewById(R.id.checkbox_accept);
        email = (EditText) activity.findViewById(R.id.email);
        password = (EditText) getActivity().findViewById(R.id.password);
        signIn = (Button) getActivity().findViewById(R.id.sign_in_button);
    }

    public void testDisclaimer() {
        LoginActivity activity = getActivity();
        TextView disclaimerText = (TextView) getActivity().findViewById(R.id.text_disclaimer);
        TextView acceptText = (TextView) getActivity().findViewById(R.id.text_accept);
        Spoon.screenshot(activity, "initial_state");

        assertThat(disclaimerText).isVisible();
        assertThat(checkbox).isVisible();
        assertThat(checkbox).isNotChecked();
        assertThat(acceptText).isVisible();

        tickCheckBox();

        instrumentation.waitForIdleSync();

        Spoon.screenshot(activity, "checkbox_ticked");

        assertThat(disclaimerText).isNotVisible();
        assertThat(checkbox).isChecked();
        assertThat(email).isVisible();
        assertThat(password).isVisible();
        assertThat(signIn).isVisible();
    }

    public void testEmptyForm_ShowsBothErrors() {
        tickCheckBox();

        Spoon.screenshot(activity, "initial_state");

        assertThat(email).hasNoError();
        assertThat(password).hasNoError();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                signIn.performClick();
            }
        });

        instrumentation.waitForIdleSync();

        Spoon.screenshot(activity, "signin_clicked");

        assertThat(email).hasError(R.string.error_field_required);
        assertThat(password).hasError(R.string.error_field_required);
    }

    public void testBlankPassword_ShowError() {
        tickCheckBox();

        Spoon.screenshot(activity, "initial_state");

        assertThat(email).hasNoError();
        assertThat(password).hasNoError();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                email.setText("support@ninetwozero.com");
                signIn.performClick();
            }
        });
        instrumentation.waitForIdleSync();

        Spoon.screenshot(activity, "email_entered");

        assertThat(email).hasNoError();
        assertThat(password).hasError(R.string.error_field_required);
    }

    public void testBlankEmail_ShowError() {
        tickCheckBox();

        Spoon.screenshot(activity, "initial_state");

        assertThat(email).hasNoError();
        assertThat(password).hasNoError();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                password.setText("topsecret");
                signIn.performClick();
            }
        });
        instrumentation.waitForIdleSync();

        Spoon.screenshot(activity, "password_entered");

        assertThat(email).hasError(R.string.error_field_required);
        assertThat(password).hasNoError();
    }

    public void testInvalidEmail_ShowError() {
        tickCheckBox();

        Spoon.screenshot(activity, "initial_state");

        assertThat(email).hasNoError();
        assertThat(password).hasNoError();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                email.setText("support");
                password.setText("topsecret");
                signIn.performClick();
            }
        });
        instrumentation.waitForIdleSync();

        Spoon.screenshot(activity, "invalid_email_entered");

        assertThat(email).hasError(R.string.error_invalid_email);
        assertThat(password).hasNoError();
    }

    private void tickCheckBox() {
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                checkbox.performClick();
            }
        });
    }
}
