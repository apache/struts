/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.showcase;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.Preparable;
import com.uwyn.rife.continuations.ContinuableObject;

import java.util.Random;

// START SNIPPET: example
public class Guess extends ActionSupport implements Preparable, ContinuableObject {
    int guess;

    public void prepare() throws Exception {
        // We clear the error message state before the action.
        // That is because with continuations, the original (or cloned) action is being
        //  executed, which will still have the old errors and potentially cause problems,
        //  such as with the workflow interceptor
        clearErrorsAndMessages();
    }

    public String execute() throws Exception {
        int answer = new Random().nextInt(100) + 1;
        int tries = 5;

        while (answer != guess && tries > 0) {
            pause(Action.SUCCESS);

            if (guess > answer) {
                addFieldError("guess", "Too high!");
            } else if (guess < answer) {
                addFieldError("guess", "Too low!");
            }

            tries--;
        }

        if (answer == guess) {
            addActionMessage("You got it!");
        } else {
            addActionMessage("You ran out of tries, the answer was " + answer);
        }

        return Action.SUCCESS;
    }

    public void setGuess(int guess) {
        this.guess = guess;
    }
}
// END SNIPPET: example
