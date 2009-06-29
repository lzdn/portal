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
package org.devproof.portal.core.module.modulemgmt.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity.LinkType;
import org.devproof.portal.core.module.modulemgmt.panel.ModuleLinkPanel;

/**
 * @author Carsten Hufe
 */
public class ModuleLinkPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	public ModuleLinkPage(final PageParameters params) {
		super(params);
		RepeatingView tableRow = new RepeatingView("repeater");
		this.add(tableRow);
		for (LinkType linkType : LinkType.values()) {
			WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
			row.add(new ModuleLinkPanel("content", linkType));
			tableRow.add(row);
		}
	}
}