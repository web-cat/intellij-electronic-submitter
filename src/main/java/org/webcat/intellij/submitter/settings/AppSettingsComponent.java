package org.webcat.intellij.submitter.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private boolean isValid;
    private final JBTextField myUserNameText = new JBTextField();
    private final JBTextField urlText = new JBTextField();
    private final JBTextField smtpText = new JBTextField();
    private final JBTextField emailText = new JBTextField();
    private final JBLabel errorText = new JBLabel();

    public AppSettingsComponent() {
        errorText.setVisible(false);
        isValid = false;
        errorText.setForeground(new java.awt.Color(255, 0, 0));
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(new JBLabel("These must be filled if you are submitting your assignment using Web-CAT"))
                .addLabeledComponent(new JBLabel("Submission URL: "), urlText, 1, false)
                .addLabeledComponent(new JBLabel("Username: "), myUserNameText, 1, false)
                .addSeparator()
                .addComponent(new JBLabel("These must be filled if you are not submitting your assignment using Web-CAT"))
                .addLabeledComponent(new JBLabel("SMTP server: "), smtpText, 1, false)
                .addLabeledComponent(new JBLabel("E-mail address: "), emailText, 1, false)
                .addSeparator()
                .addComponent(errorText, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        urlText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                urlTextFieldKeyReleased(evt);
            }
        });

    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return urlText;
    }


    public String getUserNameText() {
        return myUserNameText.getText();
    }

    public void setUserNameText(String newText) {
        myUserNameText.setText(newText);
    }

    public String getUrlText() {
        return urlText.getText();
    }

    public void setUrlText(String newText) {
        urlText.setText(newText);
    }

    public String getSmtpText() {
        return smtpText.getText();
    }

    public void setSmtpText(String newText) {
        smtpText.setText(newText);
    }

    public String getEmailText() {
        return emailText.getText();
    }

    public void setEmailText(String newText) {
        emailText.setText(newText);
    }

    boolean valid()
    {
        String url = getUrlText();

        try
        {
            URL testURL = new URL(url);
            isValid = true;
        }
        catch (MalformedURLException ex)
        {
            isValid = false;
            errorText.setText("Error: You must enter a well-formed URL.");
        }

        if (isValid)
        {
            errorText.setVisible(false);
        }
        else
        {
            errorText.setVisible(true);
        }

        return isValid;
    }

    private void urlTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_urlTextFieldKeyReleased
        valid();
    }//GEN-LAST:event_urlTextFieldKeyReleased
}