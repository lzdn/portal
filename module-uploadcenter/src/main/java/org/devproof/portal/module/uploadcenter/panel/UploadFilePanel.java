/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.uploadcenter.panel;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.uploadcenter.UploadCenterConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Carsten Hufe
 */
@Secured(UploadCenterConstants.AUTHOR_RIGHT)
public class UploadFilePanel extends Panel {
    private static final long serialVersionUID = 1L;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    private IModel<Collection<FileUpload>> uploadModel = new CollectionModel<FileUpload>(new ArrayList<FileUpload>());
    private IModel<File> uploadFolderModel;

    public UploadFilePanel(String id, IModel<File> uploadFolderModel) {
        super(id);
        this.uploadFolderModel = uploadFolderModel;
        add(createCSSHeaderContributor());
        add(createFeedbackPanel());
        add(createUploadForm());
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css");
    }

    private Form<Collection<FileUpload>> createUploadForm() {
        Form<Collection<FileUpload>> uploadForm = newUploadForm();
        uploadForm.add(createMultiFileUploadField());
        uploadForm.add(createUploadProgressBar(uploadForm));
        uploadForm.add(createCancelButton());
        uploadForm.setMaxSize(getMaxFileSize());
        uploadForm.setMultiPart(true);
        return uploadForm;
    }

    private AjaxLink<Void> createCancelButton() {
        return new AjaxLink<Void>("cancelButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancel(target);
            }
        };
    }

    private UploadProgressBar createUploadProgressBar(Form<Collection<FileUpload>> uploadForm) {
        return new UploadProgressBar("progress", uploadForm);
    }

    private Bytes getMaxFileSize() {
        return Bytes.kilobytes(configurationService.findAsInteger(UploadCenterConstants.CONF_UPLOADCENTER_MAXSIZE));
    }

    private MultiFileUploadField createMultiFileUploadField() {
        return new MultiFileUploadField("fileInput", uploadModel, configurationService.findAsInteger(UploadCenterConstants.CONF_UPLOADCENTER_MAXFILES));
    }

    private FeedbackPanel createFeedbackPanel() {
        return new FeedbackPanel("uploadFeedback");
    }

    private Form<Collection<FileUpload>> newUploadForm() {
        return new Form<Collection<FileUpload>>("uploadForm", uploadModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                Folder uploadFolder = new Folder(uploadFolderModel.getObject().getAbsolutePath());
                Collection<FileUpload> fileUploads = uploadModel.getObject();
                for (FileUpload fileUpload : fileUploads) {
                    File newFile = new File(uploadFolder, fileUpload.getClientFileName());
                    UploadFilePanel.this.deleteFile(newFile);
                    try {
                        if (newFile.createNewFile()) {
                            fileUpload.writeTo(newFile);
                            UploadFilePanel.this.info(new StringResourceModel("msg.uploaded", UploadFilePanel.this, null, new Object[]{fileUpload.getClientFileName()}).getString());
                        } else {
                            throw new IllegalStateException("Unable to write file" + newFile);
                        }
                    } catch (Exception e) {
                        throw new UnhandledException(e);
                    }
                }
                UploadFilePanel.this.onSubmit();
                super.onSubmit();
            }
        };
    }

    private void deleteFile(File newFile) {
        if (newFile.exists()) {
            if (!Files.remove(newFile)) {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }

    /**
     * Hook methods
     */
    protected void onSubmit() {

    }

    /**
     * Hook methods
     */
    protected void onCancel(AjaxRequestTarget target) {

	}
}
