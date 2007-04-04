var dependencies = [
	"dojo.io.*",
	"dojo.io.BrowserIO",
	"dojo.event.*",
    "dojo.lfx.*",
    "dojo.namespaces.*",
    "dojo.widget.Editor2",
    "struts.widget.*",
];

dependencies.prefixes = [
   ["struts", "../struts/2_1/plugins/dojo/src/main/resources/org/apache/struts2/static/dojo/struts"]
];

load("getDependencyList.js");
