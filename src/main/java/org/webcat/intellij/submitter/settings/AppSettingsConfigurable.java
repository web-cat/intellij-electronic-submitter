package org.webcat.intellij.submitter.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Web-CAT Submission Plugin";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = !mySettingsComponent.getUserNameText().equals(settings.userId);
        modified |= mySettingsComponent.getUrlText() != settings.url;
        modified |= mySettingsComponent.getSmtpText() != settings.smtp;
        modified |= mySettingsComponent.getEmailText() != settings.email;
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.url = mySettingsComponent.getUrlText();
        settings.userId = mySettingsComponent.getUserNameText();
        settings.smtp = mySettingsComponent.getSmtpText();
        settings.email = mySettingsComponent.getEmailText();
        mySettingsComponent.valid();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setUrlText(settings.url);
        mySettingsComponent.setUserNameText(settings.userId);
        mySettingsComponent.setSmtpText(settings.smtp);
        mySettingsComponent.setEmailText(settings.email);
        mySettingsComponent.valid();
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}