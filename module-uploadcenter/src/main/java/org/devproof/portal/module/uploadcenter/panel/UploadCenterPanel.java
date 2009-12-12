/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.module.uploadcenter.panel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.component.InternalDownloadLink;
import org.devproof.portal.core.module.common.factory.CommonPageFactory;
import org.devproof.portal.core.module.common.panel.ConfirmDeletePanel;
import org.devproof.portal.core.module.common.registry.SharedRegistry;
import org.devproof.portal.module.uploadcenter.UploadCenterConstants;

/**
 * @author Carsten Hufe
 */
public abstract class UploadCenterPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(UploadCenterPanel.class);

	@SpringBean(name = "sharedRegistry")
	private SharedRegistry sharedRegistry;
	private IModel<File> fileModel;
	private ModalWindow modalWindow;

	public UploadCenterPanel(String id, IModel<File> fileModel, ModalWindow modalWindow, boolean createDownload) {
		super(id, fileModel);
		this.fileModel = fileModel;
		this.modalWindow = modalWindow;
		add(createCreateDownloadLink(createDownload));
		add(createDownloadLink());
		add(createDeleteLink());
	}

	private AjaxLink<File> createDeleteLink() {
		AjaxLink<File> ajaxLink = newDeleteLink();
		ajaxLink.add(createDeleteLinkImage());
		return ajaxLink;
	}

	private Image createDeleteLinkImage() {
		return new Image("deleteImage", CommonConstants.REF_DELETE_IMG);
	}

	private AjaxLink<File> newDeleteLink() {
		final File file = fileModel.getObject();
		return new AjaxLink<File>("deleteLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				ConfirmDeletePanel<File> confirmDeletePanel = new ConfirmDeletePanel<File>(modalWindow.getContentId(),
						file, modalWindow) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onDelete(AjaxRequestTarget target, Form<?> form) {
						if (file.isDirectory()) {
							try {
								FileUtils.deleteDirectory(file);
							} catch (IOException e) {
								throw new UnhandledException(e);
							}
						} else {
							if (!file.delete()) {
								LOG.error("Error deleting file " + file);
							}
						}
						UploadCenterPanel.this.onDelete(target);
						modalWindow.close(target);
					}
				};
				modalWindow.setContent(confirmDeletePanel);
				modalWindow.show(target);
			}
		};
	}

	private InternalDownloadLink createDownloadLink() {
		File file = fileModel.getObject();
		InternalDownloadLink downloadLink = newDownloadLink();
		downloadLink.add(new Image("downloadImage", UploadCenterConstants.REF_DOWNLOAD_IMG));
		downloadLink.setVisible(file == null || file.isFile());
		return downloadLink;
	}

	private InternalDownloadLink newDownloadLink() {
		final File file = fileModel.getObject();
		return new InternalDownloadLink("downloadLink") {
			private static final long serialVersionUID = 1L;

			@Override
			protected File getFile() {
				return file;
			}
		};
	}

	private Link<File> createCreateDownloadLink(boolean createDownload) {
		File file = fileModel.getObject();
		Link<File> createDownloadLink = newCreateDownloadLink();
		createDownloadLink.setVisible((file == null || file.isFile()) && createDownload
				&& sharedRegistry.isResourceAvailable("createDownloadPage"));
		createDownloadLink.add(new Image("createDownloadImage", UploadCenterConstants.REF_GALLERY_IMG));
		return createDownloadLink;
	}

	private Link<File> newCreateDownloadLink() {
		return new Link<File>("createDownloadLink", fileModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				CommonPageFactory createDownloadPage = sharedRegistry.getResource("createDownloadPage");
				setResponsePage(createDownloadPage.newInstance(fileModel.getObject().toURI().toString()));
			}

		};
	}

	public abstract void onDelete(AjaxRequestTarget target);
}
