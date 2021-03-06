package com.nightscout.android.settings;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.nightscout.android.R;
import com.nightscout.android.test.RobolectricTestBase;

import org.junit.Test;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.util.FragmentTestUtil;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SettingsActivityTest extends RobolectricTestBase {

    private PreferenceFragment setUpPreferenceFragment(Class<? extends PreferenceFragment> clazz) {
        PreferenceFragment instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        FragmentTestUtil.startVisibleFragment(instance);
        return instance;
    }

    @Test
    public void testValidation_RestApi_Invalid() {
        EditTextPreference editTextPreference = (EditTextPreference) setUpPreferenceFragment(
                SettingsActivity.MainPreferenceFragment.class)
                .findPreference(getShadowApplication().getString(R.string.rest_uris));
        assertThat(editTextPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(editTextPreference, "\\invalidUri"), is(false));
    }

    @Test
    public void testValidation_RestApi_MultipleInvalid() {
        EditTextPreference editTextPreference = (EditTextPreference) setUpPreferenceFragment(
                SettingsActivity.MainPreferenceFragment.class)
                .findPreference(getShadowApplication().getString(R.string.rest_uris));
        assertThat(editTextPreference.getOnPreferenceChangeListener()
                        .onPreferenceChange(editTextPreference, "http://example.com \\invalidUri"),
                is(false));
    }

    @Test
    public void testValidation_RestApi_MultipleValid() {
        EditTextPreference editTextPreference = (EditTextPreference) setUpPreferenceFragment(
                SettingsActivity.MainPreferenceFragment.class)
                .findPreference(getShadowApplication().getString(R.string.rest_uris));
        assertThat(editTextPreference.getOnPreferenceChangeListener()
                        .onPreferenceChange(editTextPreference, "http://example.com http://validUri.com"),
                is(true));
    }

    @Test
    public void testValidation_RestApi_Valid() {
        EditTextPreference editTextPreference = (EditTextPreference) setUpPreferenceFragment(
                SettingsActivity.MainPreferenceFragment.class)
                .findPreference(getShadowApplication().getString(R.string.rest_uris));
        assertThat(editTextPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(editTextPreference, "http://example.com"), is(true));
    }

    @Test
    public void testAlert_RestApi_InvalidShowsDialog() {
        EditTextPreference editTextPreference = (EditTextPreference) setUpPreferenceFragment(
                SettingsActivity.MainPreferenceFragment.class)
                .findPreference(getShadowApplication().getString(R.string.rest_uris));
        editTextPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(editTextPreference, "\\invalidUri");
        ShadowAlertDialog alertDialog = getShadowApplication().getLatestAlertDialog();
        assertThat(alertDialog, is(not(nullValue())));
        assertThat(alertDialog.getMessage().toString(),
                is(getContext().getString(R.string.invalid_rest_uri, "\\invalidUri")));
    }

    @Test
    public void testValidation_Mongo_Invalid() {
        EditTextPreference editTextPreference = (EditTextPreference) setUpPreferenceFragment(
                SettingsActivity.MainPreferenceFragment.class)
                .findPreference(getShadowApplication().getString(R.string.mongo_uri));
        assertThat(editTextPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(editTextPreference, "invalidMongo"), is(false));
    }

    @Test
    public void testValidation_Mongo_Valid() {
        EditTextPreference editTextPreference = (EditTextPreference) setUpPreferenceFragment(
                SettingsActivity.MainPreferenceFragment.class)
                .findPreference(getShadowApplication().getString(R.string.mongo_uri));
        assertThat(editTextPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(editTextPreference, "mongodb://example.com"), is(true));
    }

    @Test
    public void testAlert_Mongo_InvalidShowsDialog() {
        EditTextPreference editTextPreference = (EditTextPreference) setUpPreferenceFragment(
                SettingsActivity.MainPreferenceFragment.class)
                .findPreference(getShadowApplication().getString(R.string.mongo_uri));
        editTextPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(editTextPreference, "invalidMongo");
        ShadowAlertDialog alertDialog = getShadowApplication().getLatestAlertDialog();
        assertThat(alertDialog, is(not(nullValue())));
        assertThat(alertDialog.getMessage().toString(),
                is(getContext().getString(R.string.illegal_mongo_uri)));
    }

    @Test
    public void shouldShowGitHash() {
        Preference hash = setUpPreferenceFragment(SettingsActivity.MainPreferenceFragment.class)
                .findPreference("about_build_hash");
        assertThat(hash.getSummary().toString(), not(isEmptyString()));
    }
}
