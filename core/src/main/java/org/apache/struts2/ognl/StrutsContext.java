package org.apache.struts2.ognl;

import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.OgnlContext;
import ognl.TypeConverter;

public class StrutsContext extends OgnlContext {

    private StrutsContext(MemberAccess memberAccess, ClassResolver classResolver, TypeConverter typeConverter, OgnlContext initialContext) {
        super(memberAccess, classResolver, typeConverter, initialContext);
    }

    public static StrutsContext wrap(OgnlContext context) {
        return new StrutsContext(context.getMemberAccess(), context.getClassResolver(), context.getTypeConverter(), context);
    }
}
