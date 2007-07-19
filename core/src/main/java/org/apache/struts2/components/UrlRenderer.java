package org.apache.struts2.components;

import java.io.Writer;

/**
 * Implementations of this interface are responsible for rendering/creating URLs for a specific
 * environment (e.g. Servlet, Portlet). 
 *
 */
public interface UrlRenderer {
	/**
	 * Render a URL.
	 * @param writer A writer that the implementation can use to write the result to.
	 * @param urlComponent The {@link URL} component that "owns" this renderer.
	 */
	void renderUrl(Writer writer, URL urlComponent);
	
	/**
	 * Render a Form URL.
	 * @param formComponent The {@link Form} component that "owns" this renderer.
	 */
	void renderFormUrl(Form formComponent);
}
