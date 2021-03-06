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
package org.devproof.portal.core.module.common.component;

/**
 * <p>
 * The progress of a task is encapsulated as a <code>Progression</code> value
 * object. Currently the progress is only stored as an int percentage value (0
 * to 100) and optionally a message describing the current task.
 * </p>
 * <p/>
 * <p>
 * This class has no dependency to Wicket and could be used in the service
 * layer.
 * </p>
 *
 * @author Christopher Hlubek (hlubek)
 *         <p/>
 *         comment Carsten Hufe: nothing modified
 */
public class Progression {
    private int progress;
    private String message;

    /**
     * Create a new Progression value object from a percentage progress value.
     *
     * @param progress The progress in percent from 0 to 100, where 100 means done
     */
    public Progression(int progress) {
        this.progress = progress;
        message = null;
    }

    /**
     * Create a new Progression value object from a percentage progress value
     * and a message describing the current task
     *
     * @param progress The progress in percent from 0 to 100, where 100 means done
     * @param message  message
     */
    public Progression(int progress, String message) {
        this.progress = progress;
        this.message = message;
    }

    /**
     * @return true if the progress is done
     */
    public boolean isDone() {
        return progress >= 100;
    }

    public int getProgress() {
        return progress;
    }

    public String getProgressMessage() {
        return message;
    }
}
