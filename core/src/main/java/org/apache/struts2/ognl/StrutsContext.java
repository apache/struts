package org.apache.struts2.ognl;

import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.TypeConverter;

import java.util.Map;

public class StrutsContext extends OgnlContext<StrutsContext> {

    private StrutsContext(
            MemberAccess<StrutsContext> memberAccess,
            ClassResolver<StrutsContext> classResolver,
            TypeConverter<StrutsContext> typeConverter,
            StrutsContext initialContext
    ) {
        super(memberAccess, classResolver, typeConverter, initialContext);
    }

    public static StrutsContext wrap(StrutsContext context) {
        return new StrutsContext(context.getMemberAccess(), context.getClassResolver(), context.getTypeConverter(), context);
    }

    public static StrutsContext wrap(Map<String, Object> context) {
        return StrutsContext.wrap(Ognl.createDefaultContext(null, context));
    }
}
