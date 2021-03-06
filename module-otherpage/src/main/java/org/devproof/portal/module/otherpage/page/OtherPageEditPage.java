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
package org.devproof.portal.module.otherpage.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.devproof.portal.core.module.mount.panel.MountInputPanel;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.module.otherpage.OtherPageConstants;
import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.service.OtherPageService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Secured(OtherPageConstants.AUTHOR_RIGHT)
public class OtherPageEditPage extends OtherPageBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "otherPageService")
    private OtherPageService otherPageService;
    private IModel<OtherPage> otherPageModel;
    private MountInputPanel mountInputPanel;

    public OtherPageEditPage(IModel<OtherPage> otherPageModel) {
        super(new PageParameters());
        this.otherPageModel = otherPageModel;
        add(createOtherPageEditForm());
    }

    private Form<OtherPage> createOtherPageEditForm() {
        Form<OtherPage> form = newOtherPageEditForm();
        form.add(createMountInputPanel());
        form.add(createContentField());
        form.add(createViewRightPanel());
        form.setOutputMarkupId(true);
        return form;
    }

    private MountInputPanel createMountInputPanel() {
        mountInputPanel = new MountInputPanel("mountUrls", OtherPageConstants.HANDLER_KEY, createOtherPageIdModel());
        return mountInputPanel;
    }

    private IModel<String> createOtherPageIdModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1340993990243817302L;

            @Override
            public String getObject() {
                Integer id = otherPageModel.getObject().getId();
                if(id != null) {
                    return id.toString();
                }
                return null;
            }
        };
    }

    private FormComponent<String> createContentField() {
        return new FullRichTextArea("content");
    }

    private RightGridPanel createViewRightPanel() {
        IModel<List<Right>> allRightsModel = new PropertyModel<List<Right>>(otherPageModel, "allRights");
        return new RightGridPanel("viewright", "otherPage.view", allRightsModel);
    }

    private Form<OtherPage> newOtherPageEditForm() {
        IModel<OtherPage> compoundModel = new CompoundPropertyModel<OtherPage>(otherPageModel);
        return new Form<OtherPage>("form", compoundModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                OtherPage otherPage = otherPageModel.getObject();
                otherPageService.save(otherPage);
                mountInputPanel.storeMountPoints();
                setRedirect(false);
                info(OtherPageEditPage.this.getString("msg.saved"));
                setResponsePage(new OtherPageViewPage(new PageParameters("0=" + otherPage.getId())));
            }
        };
    }
}
