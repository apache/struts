<#include "tigris-macros.ftl">
<@startPage pageTitle="An error occured"/>

<@errorMessageMultiple caption="An error occured" message="Unable to execute ${action.class.name}" errors="${action.actionErrors}"/>

<@endPage>
