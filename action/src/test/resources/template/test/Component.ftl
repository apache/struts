param hello     = ${parameters.hello}
param argle     = ${parameters.argle}
param glip      = ${parameters.glip}
param obj.Class = ${parameters.obj.class}

<#list parameters.array as element>
param array[${element_index}] = ${element}
</#list>
