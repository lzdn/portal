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
package org.devproof.portal.module.bookmark.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.component.ValidationDisplayBehaviour;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.devproof.portal.core.module.mount.panel.MountInputPanel;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.entity.Bookmark;
import org.devproof.portal.module.bookmark.entity.Bookmark.Source;
import org.devproof.portal.module.bookmark.entity.BookmarkTag;
import org.devproof.portal.module.bookmark.service.BookmarkService;
import org.devproof.portal.module.bookmark.service.BookmarkTagService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Secured(BookmarkConstants.AUTHOR_RIGHT)
public class BookmarkEditPage extends BookmarkBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "bookmarkService")
    private BookmarkService bookmarkService;
    @SpringBean(name = "bookmarkTagService")
    private BookmarkTagService bookmarkTagService;
    private IModel<Bookmark> bookmarkModel;
    private MountInputPanel mountInputPanel;


    public BookmarkEditPage(IModel<Bookmark> bookmarkModel) {
        super(new PageParameters());
        this.bookmarkModel = bookmarkModel;
        add(createBookmarkEditForm());
    }

    private Form<Bookmark> createBookmarkEditForm() {
        Form<Bookmark> form = newBookmarkEditForm();
        form.add(createTitleField());
        form.add(createDescriptionField());
        form.add(createUrlField());
        form.add(createHitsField());
        form.add(createNumberOfVotesField());
        form.add(createSumOfRatingField());
        form.add(createTagField());
        form.add(createMountInputPanel());
        form.add(createViewRightPanel());
        form.add(createVisitRightPanel());
        form.add(createVoteRightPanel());
        form.setOutputMarkupId(true);
        return form;
    }

    private FormComponent<String> createUrlField() {
        RequiredTextField<String> tf = new RequiredTextField<String>("url");
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }

    private FormComponent<String> createTitleField() {
        RequiredTextField<String> tf = new RequiredTextField<String>("title");
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }

    private FormComponent<String> createDescriptionField() {
        FullRichTextArea tf = new FullRichTextArea("description");
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }

    private FormComponent<String> createHitsField() {
        RequiredTextField<String> tf = new RequiredTextField<String>("hits");
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }

    private FormComponent<String> createNumberOfVotesField() {
        RequiredTextField<String> tf = new RequiredTextField<String>("numberOfVotes");
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }

    private FormComponent<String> createSumOfRatingField() {
        RequiredTextField<String> tf = new RequiredTextField<String>("sumOfRating");
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }

    private MountInputPanel createMountInputPanel() {
        mountInputPanel = new MountInputPanel("mountUrls", BookmarkConstants.HANDLER_KEY, createBookmarkIdModel());
        return mountInputPanel;
    }

    private IModel<String> createBookmarkIdModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1340993990243817302L;

            @Override
            public String getObject() {
                Integer id = bookmarkModel.getObject().getId();
                if(id != null) {
                    return id.toString();
                }
                return null;
            }
        };
    }

    private RightGridPanel createViewRightPanel() {
        IModel<List<Right>> rightsListModel = new PropertyModel<List<Right>>(bookmarkModel, "allRights");
        return new RightGridPanel("viewRights", "bookmark.view", rightsListModel);
    }

    private RightGridPanel createVisitRightPanel() {
        IModel<List<Right>> rightsListModel = new PropertyModel<List<Right>>(bookmarkModel, "allRights");
        return new RightGridPanel("visitRights", "bookmark.visit", rightsListModel);
    }

    private RightGridPanel createVoteRightPanel() {
        IModel<List<Right>> rightsListModel = new PropertyModel<List<Right>>(bookmarkModel, "allRights");
        return new RightGridPanel("voteRights", "bookmark.vote", rightsListModel);
    }

    private TagField<BookmarkTag> createTagField() {
        IModel<List<BookmarkTag>> listModel = new PropertyModel<List<BookmarkTag>>(bookmarkModel, "tags");
        return new TagField<BookmarkTag>("tags", listModel, bookmarkTagService);
    }

    private Form<Bookmark> newBookmarkEditForm() {
        IModel<Bookmark> compoundModel = new CompoundPropertyModel<Bookmark>(bookmarkModel);
        return new Form<Bookmark>("form", compoundModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                BookmarkEditPage.this.setVisible(false);
                Bookmark bookmark = getModelObject();
                bookmark.setBroken(Boolean.FALSE);
                bookmark.setSource(Source.MANUAL);
                bookmarkService.save(bookmark);
                mountInputPanel.storeMountPoints();
                setRedirect(false);
                info(getString("msg.saved"));
                setResponsePage(new BookmarkPage(new PageParameters("id=" + bookmark.getId())));
            }
        };
    }
}
