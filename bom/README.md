# Struts 2 BOM
This is a Bill-Of-Materials to be used with Maven based project. It allows to import all the Struts 2 
dependencies at once and used them in your project where needed. 

## Installation
You must add a proper import statement into your `pom.xml` as presented below:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.apache.struts</groupId>
            <artifactId>struts2-bom</artifactId>
            <version>${struts2.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```
