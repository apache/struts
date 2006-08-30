README.txt - mailreader-bang 

This application demonstrates switching from the "bang" syntax for invoking
dynamic methods to a general-purpose wild card approach. 

To switch between approaches, edit the struts.xml file to include either the 
struts-bang.xml file OR the struts-wildcard.xml. (But not both.)

When using the -bang application, be sure that the 
struts.enable.DynamicMethodInvocation property is set to "true".

For the -wilcard application. be sure that the 
struts.enable.DynamicMethodInvocation property is set to "false". 

----------------------------------------------------------------------------