package com.opensymphony.xwork2.validator;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This class serves 2 purpose :
 * <ul>
 * <li>
 * Parse the validation config file. (eg. MyAction-validation.xml, MyAction-actionAlias-validation.xml)
  * to return a List of ValidatorConfig encapsulating the validator information.
 * </li>
 * <li>
 * Parse the validator definition file, (eg. validators.xml) that defines the {@link Validator}s
 * registered with XWork.
 * </li>
 * </ul>
 *
 * @author Jason Carreira
 * @author James House
 * @author tm_jee ( tm_jee (at) yahoo.co.uk )
 * @author Rob Harrop
 * @author Rene Gielen
 *
 * @see com.opensymphony.xwork2.validator.ValidatorConfig
 */
public interface ValidatorFileParser {
    /**
     * Parse resource for a list of ValidatorConfig objects (configuring which validator(s) are
     * being applied to a particular field etc.)
     *
     * @param is input stream to the resource
     * @param resourceName file name of the resource
     * @return List list of ValidatorConfig
     */
    List<ValidatorConfig> parseActionValidatorConfigs(ValidatorFactory validatorFactory, InputStream is, String resourceName);

    /**
     * Parses validator definitions (register various validators with XWork).
     *
     * @param is The input stream
     * @param resourceName The location of the input stream
     */
    void parseValidatorDefinitions(Map<String,String> validators, InputStream is, String resourceName);
}
