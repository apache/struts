# Struts 2 Tiles plugin
The Tiles plugin allows actions to return Tiles pages.
You will find more details in [documentation](https://struts.apache.org/plugins/tiles/).

## Installation
Just drop this plugin JAR into `WEB-INF/lib` folder or add it as a Maven dependency.

## Legacy Tiles OGNL expressions

The legacy Tiles `OGNL:` attribute-expression evaluator is deprecated in Struts 7.4.0 and disabled by default. Use
`S2:` for expressions that should be evaluated against the Struts ValueStack, or use an ordinary Tiles mechanism.

Applications that temporarily require the legacy raw evaluator can set the following Struts constant:

```xml
<constant name="struts.tiles.ognl.legacy.enabled" value="true"/>
```

Enabling the constant produces a startup warning. The compatibility constant is deprecated in Struts 7.4.0; both it
and the legacy evaluator are targeted for removal in Struts 8.0.0.
