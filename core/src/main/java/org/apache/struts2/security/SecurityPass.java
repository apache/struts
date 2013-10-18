package org.apache.struts2.security;

/**
 * TODO lukaszlenart: write a JavaDoc
 */
public class SecurityPass {

    private Boolean accepted;
    private final String message;

    public static SecurityPass accepted() {
        return new SecurityPass(true, null);
    }

    public static SecurityPass notAccepted(String message) {
        return new SecurityPass(false, message);
    }

    private SecurityPass(boolean accepted, String message) {
        this.accepted = accepted;
        this.message = message;
    }

    public String getGuardMessage() {
        return message;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isNotAccepted() {
        return !accepted;
    }

}
