package org.webcat.intellij.submitter.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.webcat.intellij.submitter.model.SubmitterModel;
import org.webcat.intellij.submitter.settings.AppSettingsComponent;
import org.webcat.intellij.submitter.util.Utils;
import org.webcat.submitter.SubmissionTargetException;
import org.webcat.submitter.targets.AssignmentTarget;
import org.webcat.submitter.targets.SubmissionTarget;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SubmitProjectPanel extends javax.swing.JPanel {
    private SubmitterModel model;
    private SubmissionTargetModel treeModel;
    /* Variables declaration - do not modify */
    private JTextField usernameField;
    private JTree targetTree;
    private JPasswordField passwordField;
    private JTextField projectField;
    private JButton chooseProjectButton;
    private JLabel statusLabel;
    private JPanel panel;
    private JScrollPane scrollBar1;

    private static final String NO_PROJECT_SELECTED = "(No project selected)";
    /**
     * Initializes a new SubmitProjectPanel.
     *
     * @param model the submitter model
     * @param initialProject the project that will be initially selected in the
     *     dialog
     */
    protected SubmitProjectPanel(SubmitterModel model, Project initialProject)
    {
        this.model = model;
        //createUIComponents(); //not used anymore
        targetTree.setModel(null);
        statusLabel.setText("");
        usernameField.setText(model.getUsername());
        passwordField.setText(model.getPassword());

        File projDir = new File (FileUtil.toSystemDependentName(initialProject.getBasePath()));
        projectField.setText(projDir.getName());

        targetTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        targetTree.setCellRenderer(new SubmissionTargetRenderer());
        this.add(panel);
    }

    void start()
    {
        runTask("Finding assignments that can be submitted...",
                new FindSubmissionTargets());
    }
    private void runTask(String title, final SwingWorker<?, ?> task)
    {
        statusLabel.setText(title);
        task.execute();
    }

    public static void StartSubmitDialog(SubmitProjectPanel panel)
    {
        panel.start();
    }

    // ----------------------------------------------------------
    public void doSubmission()
    {
        runTask("Submitting project...", new SubmitProjectTask());
    }

    // ----------------------------------------------------------
    boolean validateInputs()
    {
        String invalidMessage = null;

        if (NO_PROJECT_SELECTED.equals(projectField.getText()))
        {
            invalidMessage = "Please select a project to submit.";
            chooseProjectButton.requestFocusInWindow();
        }
        else if (usernameField.getText().length() == 0)
        {
            invalidMessage = "Please enter your username.";
            usernameField.requestFocusInWindow();
        }
        else if (!(targetTree.getLastSelectedPathComponent()
                instanceof AssignmentTarget))
        {
            if (targetTree.getLastSelectedPathComponent() == null)
            {
                invalidMessage = "Please select an assignment.";
            }
            else
            {
                invalidMessage = "Please select an assignment, not a folder.";
            }
        }

        if (invalidMessage != null)
        {
            JOptionPane.showMessageDialog(this, invalidMessage);
            return false;
        }
        else
        {
            return true;
        }

    }

    // ----------------------------------------------------------
    /**
     * Gets the names of the currently expanded paths. These names correspond
     * with the value displayed to the user on the tree.
     *
     * @return the currently expanded paths
     */
    private List<String> getCurrentExpandedPathNames()
    {
        Object[] targets = targetTree.getSelectionPath().getPath();

        List<String> result = new ArrayList<String>();

        for (int i = 1; i < targets.length; i++)
        {
            result.add(((SubmissionTarget) targets[i]).getName());
        }

        return result;
    }


    // ----------------------------------------------------------
    /**
     * Expands the tree using the given path names. These names correspond with
     * the value displayed to the user on the tree.
     *
     * @param paths the path names
     */
    public void expandForPaths(List<String> paths)
            throws SubmissionTargetException
    {
        Object[] pathComponents = new Object[paths.size() + 1];

        SubmissionTarget target = (SubmissionTarget) treeModel.getRoot();
        pathComponents[0] = target;

        int i = 1;
        for (String path : paths)
        {
            SubmissionTarget child = null;

            for (SubmissionTarget aChild : target.getLogicalChildren())
            {
                if (aChild.getName().equals(path))
                {
                    child = aChild;
                    break;
                }
            }

            if (child == null)
            {
                pathComponents = null;
                break;
            }

            pathComponents[i++] = child;
            target = child;
        }

        if (pathComponents != null)
        {
            TreePath treePath = new TreePath(pathComponents);

            targetTree.expandPath(treePath);
            targetTree.setSelectionPath(treePath);
        }
    }



    private void createUIComponents() {
        statusLabel = new JLabel("Okay, this is a really long sentence just to make sure that everything else fits here");
        usernameField = new JTextField();
        targetTree = new JTree();
        passwordField = new JPasswordField();
        projectField = new JTextField();
        chooseProjectButton = new JButton();
        scrollBar1 = new JScrollPane();

        projectField.setEditable(false);
        statusLabel.setText("");
        chooseProjectButton.setVerifyInputWhenFocusTarget(false);
        chooseProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseProjectButtonActionPerformed(evt);
            }
        });

        targetTree.setEnabled(false);
        targetTree.setRootVisible(false);
        scrollBar1.setViewportView(targetTree);
    }


    private void chooseProjectButtonActionPerformed(
            java.awt.event.ActionEvent evt)
    {
        String[] projectsList = model.getProjectsList();

        if (projectsList.length < 1)
        {
            JOptionPane.showMessageDialog(this,
                    "You have no projects to select.");
            return;
        }
        Icon newIcon = new ImageIcon("submit_folder.gif");
        String project = (String) JOptionPane.showInputDialog(this,
                "Select a project to submit:", "Project Selection",
                JOptionPane.PLAIN_MESSAGE, newIcon,
                projectsList, projectsList[0]);

        if (project != null)
        {
            projectField.setText(project);
        }
    }
    //End of variable declaration
    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    private class SubmissionTargetRenderer extends DefaultTreeCellRenderer
    {
        // ----------------------------------------------------------
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf, int row,
                                                      boolean focused)
        {
            if (value instanceof SubmissionTarget)
            {
                SubmissionTarget target = (SubmissionTarget) value;
                value = target.getName();
            }

            return super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, focused);
        }
    }


    // ----------------------------------------------------------
    private class FindSubmissionTargets extends SwingWorker<Void, Void>
    {
        private IOException exception;


        // ----------------------------------------------------------
        public FindSubmissionTargets()
        {
            //dialogDescriptor.setValid(false);
        }


        // ----------------------------------------------------------
        @Override
        public Void doInBackground()
        {
            setProgress(1);

            try
            {
                treeModel = model.getTreeModel();
            }
            catch (IOException e)
            {
                exception = e;
            }

            setProgress(100);
            return null;
        }


        // ----------------------------------------------------------
        @Override
        public void done()
        {
            if (exception != null)
            {
                JOptionPane.showMessageDialog(
                        SubmitProjectPanel.this, "Could not access the "
                                + "submission URL because of the following error:\n\n"
                                + exception.toString());
                statusLabel.setText(
                        "Could not find any assignments to submit to.");
            }
            if (treeModel != null)
            {
                //dialogDescriptor.setValid(true);
                statusLabel.setText("");
                targetTree.setEnabled(true);
            }

            targetTree.setModel(treeModel);
            try
            {
                expandForPaths(model.getLastExpandedTreePaths());
            }
            catch (SubmissionTargetException e)
            {
                // Do nothing.
            }
        }
    }


    // ----------------------------------------------------------
    private class SubmitProjectTask extends SwingWorker<Void, Void>
    {
        private IOException exception;


        // ----------------------------------------------------------
        @Override
        public Void doInBackground()
        {
            setProgress(1);

            new AppSettingsComponent().setUserNameText(usernameField.getText());
            model.setPassword(new String(passwordField.getPassword()));
            model.setProjectToSubmit(projectField.getText());
            model.setSubmitTarget((SubmissionTarget)
                    targetTree.getLastSelectedPathComponent());
            model.setLastExpandedTreePaths(getCurrentExpandedPathNames());

            model.submitProject();

            setProgress(100);

            return null;
        }


        // ----------------------------------------------------------
        @Override
        public void done()
        {
            String response = model.getServerResponse();
            if (response != null)
            {
                try
                {
                    URI uri = Utils.writeSubmissionResults(response);
                    java.awt.Desktop.getDesktop().browse(uri);
                }
                catch (IOException e)
                {
                    // Do nothing.
                }
            }
        }
    }
}
