package org.webcat.intellij.submitter.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.help.HelpManager;
import org.jetbrains.annotations.NotNull;
import org.webcat.intellij.submitter.model.SubmitterModel;
import org.webcat.intellij.submitter.ui.SubmissionDialogPanel;
import org.webcat.intellij.submitter.ui.SubmitProjectPanel;

public class SubmitProjectAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        SubmissionDialogPanel panel = new SubmissionDialogPanel(project);
    }

    public String getName()
    {
        return "Submit Project...";
    }

    protected String iconResource()
    {
        return "/org/webcat/intellij/submitter/actions/submit.gif";
    }

    public HelpManager getHelpCtx()
    {
        return null;
    }

    public boolean enable(AnActionEvent[] activatedActions)
    {
        if (activatedActions.length == 1)
        {
            AnActionEvent node = activatedActions[0];
            return getEventProject(node) != null;
        }

        return false;
    }


    // ----------------------------------------------------------
}
