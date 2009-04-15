package org.apache.struts2.oval.interceptor;

import net.sf.oval.configuration.xml.XMLConfigurer;
import net.sf.oval.configuration.Configurer;

import java.util.List;


public interface OValValidationManager {
    /**
     * <p>This method 'collects' all the validator configurations for a given
     * action invocation.</p>
     * <p/>
     * <p>It will traverse up the class hierarchy looking for validators for every super class
     * and directly implemented interface of the current action, as well as adding validators for
     * any alias of this invocation. Nifty!</p>
     * <p/>
     * <p>Given the following class structure:
     * <pre>
     *   interface Thing;
     *   interface Animal extends Thing;
     *   interface Quadraped extends Animal;
     *   class AnimalImpl implements Animal;
     *   class QuadrapedImpl extends AnimalImpl implements Quadraped;
     *   class Dog extends QuadrapedImpl;
     * </pre></p>
     * <p/>
     * <p>This method will look for the following config files for Dog:
     * <pre>
     *   Animal
     *   Animal-context
     *   AnimalImpl
     *   AnimalImpl-context
     *   Quadraped
     *   Quadraped-context
     *   QuadrapedImpl
     *   QuadrapedImpl-context
     *   Dog
     *   Dog-context
     * </pre></p>
     * <p/>
     * <p>Note that the validation rules for Thing is never looked for because no class in the
     * hierarchy directly implements Thing.</p>
     *
     * @param clazz     the Class to look up validators for.
     * @param context   the context to use when looking up validators.
     *                  updated.
     * @return a list of xml configurers for the given class and context.
     */
    List<Configurer> getConfigurers(Class clazz, String context, boolean validateJPAAnnotations);
}
