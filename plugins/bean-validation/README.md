# Struts 2 Bean Validation plugin
This plugin add support for using the Bean Validation API instead of the built-in native Struts 2 validation logic.
You will find more details in [documentation](https://struts.apache.org/plugins/bean-validation/).

## Installation
Just drop this plugin JAR into `WEB-INF/lib` folder or add it as a Maven dependency. You must also provide
a proper implementation of the Bean Validation API, eg.: [Hibernate Bean Validation](https://hibernate.org/validator/)
