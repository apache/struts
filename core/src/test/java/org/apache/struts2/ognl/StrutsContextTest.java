package org.apache.struts2.ognl;

import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.TypeConverter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
class StrutsContextTest {

    @Test
    void shouldCreateContextWithRequiredMemberAccess() {
        MemberAccess<StrutsContext> memberAccess = mock(MemberAccess.class);
        var context = new StrutsContext(memberAccess);

        assertThat(context).isNotNull();
        assertThat(context.getMemberAccess()).isSameAs(memberAccess);
    }

    @Test
    void shouldCreateContextWithAllComponents() {
        MemberAccess<StrutsContext> memberAccess = mock(MemberAccess.class);
        ClassResolver<StrutsContext> classResolver = mock(ClassResolver.class);
        TypeConverter<StrutsContext> typeConverter = mock(TypeConverter.class);

        var context = new StrutsContext(memberAccess, classResolver, typeConverter);

        assertThat(context.getMemberAccess()).isSameAs(memberAccess);
        assertThat(context.getClassResolver()).isSameAs(classResolver);
        assertThat(context.getTypeConverter()).isSameAs(typeConverter);
    }

    @Test
    void shouldSupportRootObject() {
        MemberAccess<StrutsContext> memberAccess = mock(MemberAccess.class);
        var root = new Object();
        var context = new StrutsContext(memberAccess);
        context.withRoot(root);

        assertThat(context.getRoot()).isSameAs(root);
    }

    @Test
    void shouldImplementMapInterface() {
        MemberAccess<StrutsContext> memberAccess = mock(MemberAccess.class);
        var context = new StrutsContext(memberAccess);

        context.put("testKey", "testValue");
        assertThat(context.get("testKey")).isEqualTo("testValue");
    }
}
