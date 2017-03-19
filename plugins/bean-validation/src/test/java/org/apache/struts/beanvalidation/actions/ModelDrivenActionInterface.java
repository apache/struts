package org.apache.struts.beanvalidation.actions;

import org.apache.struts2.interceptor.validation.SkipValidation;

public interface ModelDrivenActionInterface {
    @SkipValidation
    String skipMeByInterface();
}
