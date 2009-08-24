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
package org.devproof.portal.core.module.feed.component;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.feed.page.Atom1FeedPage;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;

/**
 * @author Carsten Hufe
 */
public class Atom1Link extends BookmarkablePageLink<Atom1Link> {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "feedProviderRegistry")
	private FeedProviderRegistry feedProviderRegistry;

	public Atom1Link(final String id, final Class<? extends TemplatePage> page) {
		super(id, Atom1FeedPage.class);
		String title = "";
		if (feedProviderRegistry.hasFeedSupport(page)) {
			setParameter("0", feedProviderRegistry.getPathByPageClass(page));
			title = "Feed test change";
		} else {
			setVisible(false);
		}
		add(new SimpleAttributeModifier("title", title));
		add(new SimpleAttributeModifier("type", "application/atom+xml"));
		add(new SimpleAttributeModifier("rel", "alternate"));
	}
}
