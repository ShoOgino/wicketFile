/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.examples.upload;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.PageParameters;
import wicket.examples.WicketExamplePage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.upload.FileUploadForm;
import wicket.markup.html.form.validation.IValidationFeedback;
import wicket.markup.html.form.validation.RequiredValidator;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.FeedbackPanel;

/**
 * Upload example.
 *
 * @author Eelco Hillenius
 */
public class UploadPage extends WicketExamplePage
{
    /** Log. */
    private static Log log = LogFactory.getLog(UploadPage.class);

    /** directory we are working with. */
    private File tempDir;

    /** list of files, model for file table. */
    private final List files = new ArrayList();

    /** reference to listview for easy access. */
    private FileListView fileListView;

    /**
     * Constructor.
     * @param parameters Page parameters
     */
    public UploadPage(final PageParameters parameters)
    {
        tempDir = new File(System.getProperty("java.io.tmpdir"), "WicketUploadTest");
        if(!tempDir.isDirectory())
        {
            tempDir.mkdir();
        }
        add(new UploadForm("upload", null, tempDir));
        add(new Label("dir", tempDir.getAbsolutePath()));
        files.addAll(Arrays.asList(tempDir.list()));
        fileListView = new FileListView("fileList", files);
        add(fileListView);
        add(new FeedbackPanel("feedback"));
        
    }

    /**
     * Refresh file list.
     */
    private void refreshFiles()
    {
        files.clear();
        files.addAll(Arrays.asList(tempDir.list()));
        fileListView.invalidateModel();
        
    }

    /**
     * form for uploads.
     */
    private class UploadForm extends FileUploadForm
    {
    	/** simple holder of file name. */
    	private Serializable data = new Serializable()
    	{
    		public String fileName;
    	};

        /**
         * Construct.
         * @param name component name
         * @param validationErrorHandler error handler
         * @param targetDirectory directory to save uploads
         */
        public UploadForm(String name, IValidationFeedback validationErrorHandler, File targetDirectory)
        {
            super(name, validationErrorHandler, targetDirectory);
            TextField textField = new TextField("fileName", data, "fileName");
            textField.add(new RequiredValidator());
			add(textField);
        }

        /**
         * @see wicket.markup.html.form.upload.AbstractUploadForm#finishUpload()
         */
        protected void finishUpload()
        {
            refreshFiles();
        }
    }

    /**
     * table for files.
     */
    private class FileListView extends ListView
    {
        /**
         * Construct.
         * @param name component name
         * @param object file list
         */
        public FileListView(String name, List object)
        {
            super(name, object);
        }

        /**
         * @see ListView#populateItem(ListItem)
         */
        protected void populateItem(ListItem listItem)
        {
            final String fileName = (String)listItem.getModelObject();
            listItem.add(new Label("file", fileName));
            listItem.add(new Link("delete") {
                
                public void onClick()
                {
                    File toDelete = new File(tempDir, fileName);
                    log.info("delete " + toDelete);
                    toDelete.delete();
                    try 
                    {
                        Thread.sleep(100); // wait for file lock (Win issue)
                    }
                    catch (InterruptedException e)
                    {
                    }
                    refreshFiles();
                } 
            });
        }
    }
}