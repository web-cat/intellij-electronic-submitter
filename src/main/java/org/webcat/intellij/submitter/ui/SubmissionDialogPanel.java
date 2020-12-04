package org.webcat.intellij.submitter.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.webcat.intellij.submitter.model.SubmitterModel;

import javax.swing.*;
import java.awt.*;

public class SubmissionDialogPanel extends DialogWrapper {
    private Project initialProject;
    private final SubmitProjectPanel panel;
    public SubmissionDialogPanel(Project initialProject)
    {
        super(true);
        this.initialProject = initialProject;
        panel = new SubmitProjectPanel(SubmitterModel.getInstance(), initialProject);
        init();
        setTitle("Submit Assignment");
        setModal(false);
        panel.start();
        show();

    }
    @Override
    protected @Nullable JComponent createCenterPanel() {
        //JPanel dialogPanel = new JPanel(new BorderLayout());
        //JLabel label = new JLabel("testing");
        //label.setPreferredSize(new Dimension(100, 100));
        //dialogPanel.add(label, BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected void doOKAction() {
        if(getOKAction().isEnabled())
        {
            if (panel.validateInputs())
            {
                panel.doSubmission();
            }
        }
    }

    public void disableOk()
    {
        setOKActionEnabled(false);
    }

    public void enableOk()
    {
        setOKActionEnabled(true);
    }
}
