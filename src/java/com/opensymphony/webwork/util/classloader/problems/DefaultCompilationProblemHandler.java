package com.opensymphony.webwork.util.classloader.problems;

import java.util.ArrayList;
import java.util.Collection;


public class DefaultCompilationProblemHandler implements CompilationProblemHandler {

    final Collection errors = new ArrayList();
    final Collection warnings = new ArrayList();

    public void onStart() {
        errors.clear();
        warnings.clear();
    }

    public void handle(final CompilationProblem pProblem) {
        if (pProblem.isError()) {
            errors.add(pProblem);
        } else {
            warnings.add(pProblem);
        }
    }

    public void onStop() {
    }

    public CompilationProblem[] getErrors() {
        final CompilationProblem[] result = new CompilationProblem[errors.size()];
        errors.toArray(result);
        return result;
    }

    public CompilationProblem[] getWarnings() {
        final CompilationProblem[] result = new CompilationProblem[warnings.size()];
        warnings.toArray(result);
        return result;
    }
}
