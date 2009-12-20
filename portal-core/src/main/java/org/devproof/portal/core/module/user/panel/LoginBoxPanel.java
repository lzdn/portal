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
package org.devproof.portal.core.module.user.panel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.devproof.portal.core.module.user.page.ForgotPasswordPage;
import org.devproof.portal.core.module.user.page.ReenterEmailPage;
import org.devproof.portal.core.module.user.page.RegisterPage;

/**
 * @author Carsten Hufe
 */
public class LoginBoxPanel extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;
	private PageParameters params;
	private ValueMap valueMap;
	private WebMarkupContainer titleContainer;

	public LoginBoxPanel(String id, PageParameters params) {
		super(id);
		this.params = params;
		setValueMap();
		add(createTitleContainer());
		add(createLoginForm());
		add(createRegisterLink());
		add(createForgotPasswordLink());
	}

	private StatelessForm<ValueMap> createLoginForm() {
		StatelessForm<ValueMap> form = newLoginForm();
		form.add(createUsernameField());
		form.add(createPasswordField());
		form.add(createOptParamHiddenField());
		return form;
	}

	private StatelessForm<ValueMap> newLoginForm() {
		return new StatelessForm<ValueMap>("loginForm", new CompoundPropertyModel<ValueMap>(valueMap)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				String username = valueMap.getString("username");
				String password = valueMap.getString("password");
				PortalSession session = (PortalSession) getSession();
				try {

					String message = session.authenticate(username, password);
					if (message == null) {
						info(getString("logged.in"));
						redirectToSamePage();
					} else {
						error(getString(message));
					}
				} catch (UserNotConfirmedException e) {
					setResponsePage(new ReenterEmailPage(valueMap.getString("username")));
				}
			}

			private void redirectToSamePage() {
				// redirect to the same page so that the rights will be
				// rechecked!
				String optParam = valueMap.getString("optparam");
				if (getPage() instanceof MessagePage) {
					MessagePage msgPage = (MessagePage) getPage();
					String redirectUrl = msgPage.getRedirectURLAfterLogin();
					if (redirectUrl != null) {
						setResponsePage(new RedirectPage(redirectUrl));
					} else {
						@SuppressWarnings("unchecked")
						Class<? extends Page> homePage = ((PortalApplication) getApplication()).getHomePage();
						setResponsePage(homePage);
					}
				} else {
					setResponsePage(getPage().getClass(), new PageParameters("0=" + optParam));
				}
			}
		};
	}

	private HiddenField<String> createOptParamHiddenField() {
		// View for ArticleViewPage and OtherPageViewPage
		return new HiddenField<String>("optparam");
	}

	private PasswordTextField createPasswordField() {
		return new PasswordTextField("password");
	}

	private TextField<String> createUsernameField() {
		return new RequiredTextField<String>("username");
	}

	private BookmarkablePageLink<Void> createForgotPasswordLink() {
		return new BookmarkablePageLink<Void>("forgotPasswordLink", ForgotPasswordPage.class);
	}

	private BookmarkablePageLink<Void> createRegisterLink() {
		return new BookmarkablePageLink<Void>("registerLink", RegisterPage.class);
	}

	private void setValueMap() {
		valueMap = new ValueMap();
		valueMap.add("optparam", params.getString("0"));
	}

	private WebMarkupContainer createTitleContainer() {
		titleContainer = new WebMarkupContainer("title");
		return titleContainer;
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleContainer.setVisible(visible);
	}
}
