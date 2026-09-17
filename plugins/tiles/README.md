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

The plugin resolves this constant from the Struts configuration of the web application that owns the Tiles
container, on the first `OGNL:` evaluation, and caches the result for that evaluator lifecycle. Enabling the constant
produces a one-time migration warning when the legacy evaluator is first used. The compatibility constant and the
legacy evaluator are deprecated for removal.
