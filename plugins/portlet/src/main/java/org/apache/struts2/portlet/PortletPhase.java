package org.apache.struts2.portlet;

/**
 * TODO lukaszlenart: add a comment
 */
public enum PortletPhase {

    /**
     * Constant used for the render phase (
     * {@link javax.portlet.Portlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)})
     */
    RENDER_PHASE,

    /**
     * Constant used for the action phase (
     * {@link javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)})
     */
    ACTION_PHASE,

    /**
     * Constant used for the event phase
     */
    EVENT_PHASE,

    /**
     *
     */
    SERVE_RESOURCE_PHASE;

    public boolean isRender() {
        return this.equals(RENDER_PHASE);
    }

    public boolean isAction() {
        return this.equals(ACTION_PHASE);
    }

    public boolean isEvent() {
        return this.equals(EVENT_PHASE);
    }

    public boolean isResource() {
        return this.equals(SERVE_RESOURCE_PHASE);
    }

}
