/*
 * Copyright 2009 Carsten Hufe devproof.org
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
package org.devproof.portal.module.article.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.tag.panel.ContentTagPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticlePageEntity;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.devproof.portal.module.article.service.ArticleService;

/**
 * Article overview page
 * 
 * @author Carsten Hufe
 */
public class ArticleReadPage extends ArticleBasePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "articleService")
	private ArticleService articleService;
	@SpringBean(name = "articleTagService")
	private TagService<ArticleTagEntity> articleTagService;

	public ArticleReadPage(final PageParameters params) {
		super(params);
		String contentId = getContentId(params);
		int currentPage = params.getAsInteger("1", 1);
		int numberOfPages = (int) articleService.getPageCount(contentId);
		ArticlePageEntity page = articleService.findArticlePageByContentIdAndPage(contentId, currentPage);

		validateAccessRights(page);
		add(createTitleLabel(page));
		add(createMetaInfoPanel(page));
		add(createAppropriateAuthorPanel(page));
		add(createTagPanel(params, page));
		add(createContentLabel(page));
		add(createBackLink(contentId, currentPage));
		add(createForwardLink(contentId, currentPage, numberOfPages));
		addTagCloudBox(articleTagService, new PropertyModel<ArticleTagEntity>(new ArticleQuery(), "tag"),
				ArticlePage.class, params);
		setPageTitle(page.getArticle().getTitle());
	}

	private String getContentId(final PageParameters params) {
		String contentId = params.getString("0");
		if (contentId == null) {
			contentId = getRequest().getParameter("optparam");
		}
		return contentId;
	}

	private void validateAccessRights(final ArticlePageEntity page) {
		final PortalSession session = (PortalSession) getSession();
		if (page == null) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("error.page")));
		}
		if (page != null && !session.hasRight("article.read") && !session.hasRight(page.getArticle().getReadRights())) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.right"),
					getRequestURL()));
		}
	}

	private Label createTitleLabel(final ArticlePageEntity page) {
		return new Label("title", page.getArticle().getTitle());
	}

	private MetaInfoPanel createMetaInfoPanel(final ArticlePageEntity page) {
		return new MetaInfoPanel("metaInfo", page.getArticle());
	}

	private Component createAppropriateAuthorPanel(final ArticlePageEntity page) {
		if (isAuthor()) {
			return createAuthorPanel(page);
		} else {
			return createEmptyAuthorPanel();
		}
	}

	private Component createAuthorPanel(final ArticlePageEntity page) {
		return new AuthorPanel<ArticleEntity>("authorButtons", page.getArticle()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onDelete(final AjaxRequestTarget target) {
				articleService.delete(page.getArticle());
			}

			@Override
			public void onEdit(final AjaxRequestTarget target) {
				ArticleEntity article = page.getArticle();
				article = articleService.findByIdAndPrefetch(article.getId());
				final ArticleEditPage articlePage = new ArticleEditPage(article);
				setResponsePage(articlePage);
			}
		}.setRedirectPage(ArticlePage.class, new PageParameters("infoMsg=" + getString("msg.deleted")));
	}

	private Component createEmptyAuthorPanel() {
		return new WebMarkupContainer("authorButtons").setVisible(false);
	}

	private ExtendedLabel createContentLabel(final ArticlePageEntity page) {
		return new ExtendedLabel("content", page.getContent());
	}

	private ContentTagPanel<ArticleTagEntity> createTagPanel(final PageParameters params, final ArticlePageEntity page) {
		return new ContentTagPanel<ArticleTagEntity>("tags", new ListModel<ArticleTagEntity>(page.getArticle()
				.getTags()), ArticlePage.class, params);
	}

	private BookmarkablePageLink<String> createForwardLink(final String contentId, final int currentPage,
			final int pageCount) {
		final BookmarkablePageLink<String> forwardLink = new BookmarkablePageLink<String>("forwardLink",
				ArticleReadPage.class) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return pageCount > currentPage;
			}

		};
		forwardLink.setParameter("0", contentId);
		forwardLink.setParameter("1", currentPage + 1);
		return forwardLink;
	}

	private BookmarkablePageLink<String> createBackLink(final String contentId, final int currentPage) {
		BookmarkablePageLink<String> backLink = new BookmarkablePageLink<String>("backLink", ArticleReadPage.class) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return currentPage > 1;
			}
		};
		backLink.setParameter("0", contentId);
		backLink.setParameter("1", currentPage - 1);
		return backLink;
	}
}