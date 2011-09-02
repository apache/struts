[
<#list category.children as node>
  {
    label: '${node.name}',
    id: '${node.id}',
    hasChildren: ${(node.children.size() > 0)?string}
  },
</#list>
] 