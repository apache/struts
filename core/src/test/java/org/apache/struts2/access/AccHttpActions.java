package org.apache.struts2.access;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.httpmethod.HttpPost;

public class AccHttpActions {
    public static final java.util.concurrent.atomic.AtomicInteger RAN = new java.util.concurrent.atomic.AtomicInteger();

    public static class ExecutePost extends ActionSupport {
        @HttpPost
        @Override
        public String execute() { RAN.incrementAndGet(); return SUCCESS; }
    }
    public static class NamedPost extends ActionSupport {
        @HttpPost
        public String save() { RAN.incrementAndGet(); return SUCCESS; }
    }
    @HttpPost
    public static class ClassPost extends ActionSupport {
        @Override
        public String execute() { RAN.incrementAndGet(); return SUCCESS; }
    }
    public static class SubOfClassPost extends ClassPost { }
    public static class BaseMethodPost extends ActionSupport {
        @HttpPost
        public String save() { RAN.incrementAndGet(); return SUCCESS; }
    }
    public static class SubOverride extends BaseMethodPost {
        @Override
        public String save() { RAN.incrementAndGet(); return SUCCESS; }
    }

    public interface MethodPostMarker {
        @HttpPost
        default String save() { AccHttpActions.RAN.incrementAndGet(); return ActionSupport.SUCCESS; }
    }
    public static class ImplementsMethodMarker extends ActionSupport implements MethodPostMarker { }
    public static class SubOfImplementsMethodMarker extends ImplementsMethodMarker { }

    @HttpPost
    public interface ClassPostMarker { }
    public static class ImplementsClassMarker extends ActionSupport implements ClassPostMarker {
        @Override
        public String execute() { RAN.incrementAndGet(); return SUCCESS; }
    }
    public static class SubOfImplementsClassMarker extends ImplementsClassMarker { }
}
