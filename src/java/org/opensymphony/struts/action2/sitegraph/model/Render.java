package com.opensymphony.webwork.sitegraph.model;

import java.io.IOException;

/**
 * User: plightbo
 * Date: Jun 26, 2005
 * Time: 5:06:03 PM
 */
public interface Render {
    public void render(IndentWriter writer) throws IOException;
}
