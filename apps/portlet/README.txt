README.txt - portlet

This is a simple example of using the portlet API with Struts applications. 

For more on getting started with Struts, see 

* http://cwiki.apache.org/WW/home.html

WARNING - Additional configuration required for deployment

Due to difference in portlet container implementations, the portlet
WAR is not ready-to-run. Extract the portlet WAR, and then copy the
contents of apps/portlet/src/main/etc/<your_portal_server>/ into the
WAR's WEB-INF directory.

I18N:
=====
Please note that this project was created with the assumption that it will be run
in an environment where the default locale is set to English. This means that
the default messages defined in package.properties are in English. If the default
locale for your server is different, then rename package.properties to package_en.properties
and create a new package.properties with proper values for your default locale.
