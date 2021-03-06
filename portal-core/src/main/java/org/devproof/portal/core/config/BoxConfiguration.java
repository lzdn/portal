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
package org.devproof.portal.core.config;

import org.apache.wicket.Component;

import java.io.Serializable;

/**
 * Contains configuration for one box
 *
 * @author Carsten Hufe
 */
public class BoxConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Class<? extends Component> boxClass;

    public BoxConfiguration() {
    }

    /**
     * @param name     name of the box
     * @param boxClass class off the box
     */
    public BoxConfiguration(String name, Class<? extends Component> boxClass) {
        this.name = name;
        this.boxClass = boxClass;
    }

    /**
     * Gets the name from annotation
     * @param boxClass Box class
     */
    public BoxConfiguration(Class<? extends Component> boxClass) {
        NavigationBox annotation = boxClass.getAnnotation(NavigationBox.class);
        this.name = annotation.value();
        this.boxClass = boxClass;
    }

    /**
     * @return name of the box
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name of the box
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return box class (must be a wicket component)
     */
    public Class<? extends Component> getBoxClass() {
        return boxClass;
    }

    /**
     * @param boxClass box class (must be a wicket component)
     */
    public void setBoxClass(Class<? extends Component> boxClass) {
        this.boxClass = boxClass;
    }

    /**
     * @return generated key of the box, currently the simple class name
     */
    public String getKey() {
        return boxClass.getSimpleName();
    }

    @Override
    public String toString() {
        return "BoxConfiguration [boxClass=" + boxClass + ", name=" + name + "]";
	}
}
