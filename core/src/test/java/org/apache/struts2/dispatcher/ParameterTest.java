package org.apache.struts2.dispatcher;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ParameterTest {

    private static final String PARAM_NAME = "param";

    @DataProvider(name = "paramValues")
    Object[][] paramValues() {
        return new Object[][] {
            {null, new String[0]},
            {"input", new String[] {"input"}},
            {Integer.valueOf(5), new String[] {"5"}},
            {new String[] {"foo"}, new String[] {"foo"}},
            {new Object[] {null}, new String[] {null}},
        };
    }

    @Test(dataProvider = "paramValues")
    public void shouldConvertRequestValuesToStringArrays(Object input, String[] expected) {
        Parameter.Request request = new Parameter.Request(PARAM_NAME, input);

        String[] result = request.getMultipleValues();

        assertEquals(result, expected);
        assertNotSame(result, input);
    }
}
