package org.apache.struts2.dojo.views;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dojo.views.freemarker.tags.DojoModels;
import org.apache.struts2.dojo.views.velocity.components.AnchorDirective;
import org.apache.struts2.dojo.views.velocity.components.AutocompleterDirective;
import org.apache.struts2.dojo.views.velocity.components.BindDirective;
import org.apache.struts2.dojo.views.velocity.components.DateTimePickerDirective;
import org.apache.struts2.dojo.views.velocity.components.DivDirective;
import org.apache.struts2.dojo.views.velocity.components.FormDirective;
import org.apache.struts2.dojo.views.velocity.components.HeadDirective;
import org.apache.struts2.dojo.views.velocity.components.SubmitDirective;
import org.apache.struts2.dojo.views.velocity.components.TabbedPanelDirective;
import org.apache.struts2.dojo.views.velocity.components.TreeDirective;
import org.apache.struts2.dojo.views.velocity.components.TreeNodeDirective;
import org.apache.struts2.views.TagLibrary;

import com.opensymphony.xwork2.util.ValueStack;

public class DojoTagLibrary implements TagLibrary {

    public Object getFreemarkerModels(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        
        return new DojoModels(stack, req, res);
    }

    public List<Class> getVelocityDirectiveClasses() {
        Class[] directives = new Class[] {
            DateTimePickerDirective.class,
            DivDirective.class,
            AutocompleterDirective.class,
            AnchorDirective.class,
            SubmitDirective.class,
            TabbedPanelDirective.class,
            TreeDirective.class,
            TreeNodeDirective.class,
            FormDirective.class,
            HeadDirective.class,
            BindDirective.class
        };
        return Arrays.asList(directives);
    }

}