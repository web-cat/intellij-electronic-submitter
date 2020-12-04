package org.webcat.intellij.submitter.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ide.util.PropertiesComponent;
import org.webcat.intellij.submitter.settings.AppSettingsState;
import org.webcat.intellij.submitter.ui.SubmissionTargetModel;
import org.webcat.intellij.submitter.util.Utils;
import org.webcat.submitter.SubmissionManifest;
import org.webcat.submitter.SubmittableFile;
import org.webcat.submitter.Submitter;
import org.webcat.submitter.targets.AssignmentTarget;
import org.webcat.submitter.targets.SubmissionTarget;

//--------------------------------------------------------------------------
/**
 * Model adds a layer overtop the xml storage functions performed by the plugin.
 * It also is used to make calls to the server to submit / retrive data needed
 * by the plugin.
 *
 * @author Robert Poole, Rusty Todd, Stephan McCarn
 */
public class SubmitterModel {

    // user defined variables
    private SubmissionTarget submitTarget;
    private String username;
    private String password;
    private String submitUrl;
    private String mailServer;
    private String email;
    private List<String> lastExpandedTreePaths;
    // non user defined varibles that need to be acccessed by the view
    private boolean partnerEnabled;
    private String serverResponse;
    private String lastProjectSubmitSelect;
    // fields used only by the model
    private URL url;
    private Submitter submitter;
    private static final SubmitterModel INSTANCE = new SubmitterModel();
    private File projectToSubmit;
    private boolean submitterHasResponse;

    /**
     * Private constructor for singleton.
     */
    private SubmitterModel() {
        // singleton
    }

    /**
     * Get the singleton of this model.
     * @return the singleton
     */
    public static SubmitterModel getInstance() {
        return INSTANCE;
    }

    /**
     * Gets a list of currently open projects and adds them to a map of Files.
     * List is refreshed on each call.
     * @return the map of files
     */
    private Map<String, File> getFiles() {
        Map<String, File> files = new HashMap<String, File>();
        for (Project p : ProjectManager.getInstance().getOpenProjects()) {
            File filetoAdd = new File (FileUtil.toSystemDependentName(p.getBasePath()));
            files.put(filetoAdd.getName(), filetoAdd);
        }
        return files;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        email = AppSettingsState.getInstance().email;;
        return email;
    }


    /**
     * Gets the mail server.
     *
     * @return the mail server
     */
    public String getMailServer() {
        mailServer = AppSettingsState.getInstance().smtp;
        return mailServer;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the project to submit.
     *
     * @return the project to submit
     */
    public String getProjectToSubmit() {
        return projectToSubmit.getName();
    }

    /**
     * Sets the project to submit.
     *
     * @param projectToSubmitStr the new project to submit
     */
    public void setProjectToSubmit(String projectToSubmitStr) {
        this.projectToSubmit = getFiles().get(projectToSubmitStr);
        setLastProjectSubmitSelect(projectToSubmitStr);
    }

    /**
     * Sets the last project submit select.
     *
     * @param lastProjectSubmitSelect the new last project submit select
     */
    private void setLastProjectSubmitSelect(String lastProjectSubmitSelect) {
        this.lastProjectSubmitSelect = lastProjectSubmitSelect;
        PropertiesComponent.getInstance().setValue(SubmitterPrefs.lastProjectSubmitSelect, lastProjectSubmitSelect);
    }

    /**
     * Gets the last project submit select.
     *
     * @return the last project submit select
     */
    public String getLastProjectSubmitSelect() {
        lastProjectSubmitSelect = PropertiesComponent.getInstance().getValue(SubmitterPrefs.lastProjectSubmitSelect, "");
        if (getFiles().containsKey(lastProjectSubmitSelect)) {
            return lastProjectSubmitSelect;
        }
        return "(No project selected)";
    }

    /**
     * Gets the submit target.
     *
     * @return the submit target
     */
    public SubmissionTarget getSubmitTarget() {
        return submitTarget;
    }

    /**
     * Sets the submit target.
     *
     * @param submitTarget the new submit target
     */
    public void setSubmitTarget(SubmissionTarget submitTarget) {
        this.submitTarget = submitTarget;
    }

    /**
     * Gets the submit url.
     *
     * @return the submit url
     */
    public String getSubmitUrl() {
        submitUrl = AppSettingsState.getInstance().url;
        return submitUrl;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        username = AppSettingsState.getInstance().userId;
        return username;
    }

    /**
     * Contains the names of the last selected "paths" in the tree.
     * @return the last expanded names in the tree
     */
    public List<String> getLastExpandedTreePaths() {
        String paths = PropertiesComponent.getInstance().getValue(SubmitterPrefs.lastExpandedTreePaths, "");
        if (paths.equals("") || paths.length() < 1) {
            return new ArrayList<String>();
        }
        lastExpandedTreePaths = Utils.readStringFormat(paths, "name");
        return lastExpandedTreePaths;
    }

    /**
     * Sets the last expanded tree paths.
     *
     * @param lastExpandedTreePaths the new last expanded tree paths
     */
    public void setLastExpandedTreePaths(List<String> lastExpandedTreePaths) {
        PropertiesComponent.getInstance().setValue(SubmitterPrefs.lastExpandedTreePaths, Utils.storeStringFormat(lastExpandedTreePaths, "name"));
        this.lastExpandedTreePaths = lastExpandedTreePaths;
    }

    /**
     * Initializes the submitter with the currently stored URL
     */
    private void initSubmitter() throws MalformedURLException {
        //TODO check for url there
        if (submitter == null) {
            submitter = new Submitter();
        }
        url = new URL(getSubmitUrl());

    }

    /**
     * Gets the tree model. Sets the serverResponseMessage property.
     * Adheres to the model required by the JTree class.
     *
     * @return the tree model
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public SubmissionTargetModel getTreeModel() throws IOException {
        initSubmitter();
        if (url != null) {
            submitter.readSubmissionTargets(url);
        }
        // create a model that can be read by a jtree.
        SubmissionTargetModel model =
                new SubmissionTargetModel(submitter.getRoot());
        return model;
    }

    /**
     * Called to submit the currently selected project.
     *
     * @throws IOException ioexception
     */
    public void submitProject()
    {
        try
        {
            // create the manifest
            SubmissionManifest manifest = new SubmissionManifest();
            manifest.setSubmittableItems(new SubmittableFile(projectToSubmit));
            manifest.setAssignment((AssignmentTarget) getSubmitTarget());
            manifest.setPassword(getPassword());
            manifest.setUsername(getUsername());

            initSubmitter();
            submitterHasResponse = false;

            submitter.submit(manifest);

            if (submitter.hasResponse())
            {
                submitterHasResponse = true;
                serverResponse = submitter.getResponse();
            }
        }
        catch (IOException e)
        {
            serverResponse = e.toString();
        }
    }

    /**
     * Each call returns a refreshed JList of projects.
     * @return the array of projects
     */
    @SuppressWarnings("cast")
    public String[] getProjectsList() {

        Map<String, File> projects = getFiles();
        String[] ret = new String[projects.keySet().size()];
        return (String[]) getFiles().keySet().toArray(ret);
    }

    /**
     * Gets the server response.
     *
     * @return the server response
     */
    public String getServerResponse() {
        if (this.submitterHasResponse) {
            return serverResponse;
        }
        return null;
    }
}
