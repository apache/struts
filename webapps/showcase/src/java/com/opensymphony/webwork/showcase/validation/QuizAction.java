package com.opensymphony.webwork.showcase.validation;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */

// START SNIPPET: quizAction 

public class QuizAction extends ActionSupport {
    String name;
    int age;
    String answer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

// END SNIPPET: quizAction

