package org.apache.struts2.views.java.simple;

import org.apache.struts2.components.DateTextField;
import org.apache.struts2.components.UIBean;

public class DateTextFieldTest extends AbstractCommonAttributesTest {

    private DateTextField tag;

    public void testRenderDateTextField() {
    	tag.setId("id");
        tag.setName("name");
        tag.setFormat("yyyy-MM-dd");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<div id='id'>" +
        		"<input type='text' class='date_year' size='4' maxlength='4' id='__year_id' name='__year_name'></input>" +
        		"-<input type='text' class='date_month' size='2' maxlength='2' id='__month_id' name='__month_name'></input>" +
        		"-<input type='text' class='date_day' size='2' maxlength='2' id='__day_id' name='__day_name'></input></div>");
        assertEquals(expected, output);
    }
    
    @Override
    public void testRenderTextFieldScriptingAttrs() throws Exception { }
    
    @Override
    public void testRenderTextFieldCommonAttrs() throws Exception { }

    @Override
    public void testRenderTextFieldDynamicAttrs() throws Exception { }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new DateTextField(stack, request, response);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "datetextfield";
    }

}
