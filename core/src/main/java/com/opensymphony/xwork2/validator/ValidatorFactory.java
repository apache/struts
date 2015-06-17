package com.opensymphony.xwork2.validator;

/**
 * ValidatorFactory
 *
 * <p>
 * <!-- START SNIPPET: javadoc -->
 * Validation rules are handled by validators, which must be registered with
 * the ValidatorFactory (using the registerValidator method). The simplest way to do so is to add a file name
 * validators.xml in the root of the classpath (/WEB-INF/classes) that declares
 * all the validators you intend to use.
 * <!-- END SNIPPET: javadoc -->
 * </p>
 *
 *
 * <p>
 * <b>INFORMATION</b>
 * <!-- START SNIPPET: information -->
 * validators.xml if being defined should be available in the classpath. However
 * this is not necessary, if no custom validator is needed. Predefined sets of validators
 * will automatically be picked up when defined in
 * com/opensymphony/xwork2/validator/validators/default.xml packaged in
 * in the xwork jar file. See ValidatorFactory static block for details.
 * <!-- END SNIPPET: information -->
 * </p>
 *
 * <p>
 * <b>WARNING</b>
 * <!-- START SNIPPET: warning -->
 * If custom validator is being defined and a validators.xml is created and
 * place in the classpath, do remember to copy all the other pre-defined validators
 * that is needed into the validators.xml as if not they will not be registered.
 * Once a validators.xml is detected in the classpath, the default one
 * (com/opensymphony/xwork2/validator/validators/default.xml) will not be loaded.
 * It is only loaded when a custom validators.xml cannot be found in the classpath.
 *  Be careful.
 * <!-- END SNIPPET: warning -->
 * </p>
 *
 * <p><b>Note:</b>
 * <!-- START SNIPPET: turningOnValidators -->
 * The default validationWorkflowStack already includes this.<br/>
 * All that is required to enable validation for an Action is to put the
 * ValidationInterceptor in the interceptor refs of the action (see xwork.xml) like so:
 * <!-- END SNIPPET: turningOnValidators -->
 * </p>
 *
 * <pre>
 * <!-- START SNIPPET: exTurnOnValidators -->
 *     &lt;interceptor name="validator" class="com.opensymphony.xwork2.validator.ValidationInterceptor"/&gt;
 * <!-- END SNIPPET: exTurnOnValidators -->
 * </pre>
 *
 * <p><b>Field Validators</b>
 * <!-- START SNIPPET: fieldValidators -->
 * Field validators, as the name indicate, act on single fields accessible through an action.
 * A validator, in contrast, is more generic and can do validations in the full action context,
 * involving more than one field (or even no field at all) in validation rule.
 * Most validations can be defined on per field basis. This should be preferred over
 * non-field validation wherever possible, as field validator messages are bound to the
 * related field and will be presented next to the corresponding input element in the
 * respecting view.
 * <!-- END SNIPPET: fieldValidators -->
 * </p>
 *
 * <p><b>Non Field Validators</b>
 * <!-- START SNIPPET: nonFieldValidators -->
 * Non-field validators only add action level messages. Non-field validators
 * are mostly domain specific and therefore offer custom implementations.
 * The most important standard non-field validator provided by XWork
 * is ExpressionValidator.
 * <!-- END SNIPPET: nonFieldValidators -->
 * </p>
 *
 * <p><b>NOTE:</b>
 * <!-- START SNIPPET: validatorsNote -->
 * Non-field validators takes precedence over field validators
 * regardless of the order they are defined in *-validation.xml. If a non-field
 * validator is short-circuited, it will causes its non-field validator to not
 * being executed. See validation framework documentation for more info.
 * <!-- END SNIPPET: validatorsNote -->
 * </p>
 *
 * <p><b>VALIDATION RULES:</b>
 * <!-- START SNIPPET: validationRules1 -->
 * Validation rules can be specified:
 * <ol>
 *  <li> Per Action class: in a file named ActionName-validation.xml</li>
 *  <li> Per Action alias: in a file named ActionName-alias-validation.xml</li>
 *  <li> Inheritance hierarchy and interfaces implemented by Action class:
 *  XWork searches up the inheritance tree of the action to find default
 *  validations for parent classes of the Action and interfaces implemented</li>
 * </ol>
 * Here is an example for SimpleAction-validation.xml:
 * <!-- END SNIPPET: validationRules1 -->
 * <p>
 *
 * <pre>
 * <!-- START SNIPPET: exValidationRules1 -->
 * &lt;!DOCTYPE validators PUBLIC "-//Apache Struts//XWork Validator 1.0.3//EN"
   		"http://struts.apache.org/dtds/xwork-validator-1.0.3.dtd"&gt;
 * &lt;validators&gt;
 *   &lt;field name="bar"&gt;
 *       &lt;field-validator type="required"&gt;
 *           &lt;message&gt;You must enter a value for bar.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *       &lt;field-validator type="int"&gt;
 *           &lt;param name="min">6&lt;/param&gt;
 *           &lt;param name="max"&gt;10&lt;/param&gt;
 *           &lt;message&gt;bar must be between ${min} and ${max}, current value is ${bar}.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;field name="bar2"&gt;
 *       &lt;field-validator type="regex"&gt;
 *           &lt;param name="expression"&gt;[0-9],[0-9]&lt;/param&gt;
 *           &lt;message&gt;The value of bar2 must be in the format "x, y", where x and y are between 0 and 9&lt;/message&gt;
 *      &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;field name="date"&gt;
 *       &lt;field-validator type="date"&gt;
 *           &lt;param name="min"&gt;12/22/2002&lt;/param&gt;
 *           &lt;param name="max"&gt;12/25/2002&lt;/param&gt;
 *           &lt;message&gt;The date must be between 12-22-2002 and 12-25-2002.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;field name="foo"&gt;
 *       &lt;field-validator type="int"&gt;
 *           &lt;param name="min"&gt;0&lt;/param&gt;
 *           &lt;param name="max"&gt;100&lt;/param&gt;
 *           &lt;message key="foo.range"&gt;Could not find foo.range!&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;validator type="expression"&gt;
 *       &lt;param name="expression"&gt;foo lt bar &lt;/param&gt;
 *       &lt;message&gt;Foo must be greater than Bar. Foo = ${foo}, Bar = ${bar}.&lt;/message&gt;
 *   &lt;/validator&gt;
 * &lt;/validators&gt;
 * <!-- END SNIPPET: exValidationRules1 -->
 * </pre>
 *
 *
 * <p>
 * <!-- START SNIPPET: validationRules2 -->
 * Here we can see the configuration of validators for the SimpleAction class.
 * Validators (and field-validators) must have a type attribute, which refers
 * to a name of an Validator registered with the ValidatorFactory as above.
 * Validator elements may also have &lt;param&gt; elements with name and value attributes
 * to set arbitrary parameters into the Validator instance. See below for discussion
 * of the message element.
 * <!-- END SNIPPET: validationRules2 -->
 * </p>
 *
 *
 *
 * <!-- START SNIPPET: validationRules3 -->
 * <p>Each Validator or Field-Validator element must define one message element inside
 * the validator element body. The message element has 1 attributes, key which is not
 * required. The body of the message tag is taken as the default message which should
 * be added to the Action if the validator fails. Key gives a message key to look up
 * in the Action's ResourceBundles using getText() from LocaleAware if the Action
 * implements that interface (as ActionSupport does). This provides for Localized
 * messages based on the Locale of the user making the request (or whatever Locale
 * you've set into the LocaleAware Action). After either retrieving the message from
 * the ResourceBundle using the Key value, or using the Default message, the current
 * Validator is pushed onto the ValueStack, then the message is parsed for \$\{...\}
 * sections which are replaced with the evaluated value of the string between the
 * \$\{ and \}. This allows you to parameterize your messages with values from the
 * Validator, the Action, or both.</p>
 *
 *
 * <p>If the validator fails, the validator is pushed onto the ValueStack and the
 * message - either the default or the locale-specific one if the key attribute is
 * defined (and such a message exists) - is parsed for ${...} sections which are
 * replaced with the evaluated value of the string between the ${ and }. This
 * allows you to parameterize your messages with values from the validator, the
 * Action, or both. </p>
 *
 * <p><b>NOTE:</b> Since validation rules are in an XML file, you must make sure
 * you escape special characters. For example, notice that in the expression
 * validator rule above we use "&amp;gt;" instead of "&gt;". Consult a resource on XML
 * for the full list of characters that must be escaped. The most commonly used
 * characters that must be escaped are: &amp; (use &amp;amp;), &gt; (user &amp;gt;), and &lt; (use &amp;lt;).</p>
 *
 * <p>Here is an example of a parameterized message:</p>
 * <p>This will pull the min and max parameters from the IntRangeFieldValidator and
 * the value of bar from the Action.</p>
 * <!-- END SNIPPET: validationRules3 -->
 *
 * <pre>
 * <!-- START SNIPPET: exValidationRules3 -->
 *    bar must be between ${min} and ${max}, current value is ${bar}.
 * <!-- END SNIPPET: exValidationRules3 -->
 * </pre>
 *
 * <!-- START SNIPPET: validationRules4 -->
 * <p>Another notable fact is that the provided message value is capable of containing OGNL expressions.
 * Keeping this in mind, it is possible to construct quite sophisticated messages.</p>
 * <p>See the following example to get an impression:</p>
 *
 * <!-- END SNIPPET: validationRules4 -->
 *
 * <pre>
 * <!-- START SNIPPET: exValidationRules4 -->
 *    <message>${getText("validation.failednotice")}! ${getText("reason")}: ${getText("validation.inputrequired")}</message>
 * <!-- END SNIPPET: exValidationRules4 -->
 * </pre>
 *
 * @version $Date$ $Id$
 * @author Jason Carreira
 * @author James House
 */
public interface ValidatorFactory {

    /**
     * Get a Validator that matches the given configuration.
     *
     * @param cfg  the configurator.
     * @return  the validator.
     */
    Validator getValidator(ValidatorConfig cfg);

    /**
     * Registers the given validator to the existing map of validators.
     * This will <b>add</b> to the existing list.
     *
     * @param name    name of validator to add.
     * @param className   the FQ classname of the validator.
     */
    void registerValidator(String name, String className);

    /**
     * Lookup to get the FQ classname of the given validator name.
     *
     * @param name   name of validator to lookup.
     * @return  the found FQ classname
     * @throws IllegalArgumentException is thrown if the name is not found.
     */
    String lookupRegisteredValidatorType(String name);
}
