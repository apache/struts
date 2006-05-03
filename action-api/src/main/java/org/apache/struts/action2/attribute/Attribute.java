package org.apache.struts.action2.attribute;

/**
 * A key/value entry from a given scope. Provides better type safety and requires less code than accessing the
 * {@code Map} view of a scope directly. For example:
 *
 * <pre>
 *   Attribute&lt;Foo> fooAttribute = new SessionAttribute&lt;Foo>("foo") {
 *     protected Foo initialValue() {
 *       return new Foo();
 *     }
 *   };
 *
 *   public String execute() {
 *     // get Foo instance from current session.
 *     Foo foo = fooAttribute.get();
 *
 *     foo.doSomething();
 *     return SUCCESS;
 *   }
 * </pre>
 *
 * <p>All operations are atomic. Implementations synchronize on underlying objects from the servlet API.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Attribute<T> {

    /**
     * Gets attribute value.
     *
     * @return value
     */
    T get();

    /**
     * Sets attribute value.
     *
     * @param t value
     * @return previous value
     */
    T set(T t);

    /**
     * Removes attribute.
     *
     * @return previous value
     */
    T remove();
}
