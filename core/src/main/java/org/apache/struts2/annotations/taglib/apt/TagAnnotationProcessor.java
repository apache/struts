/*
 * $Id:  $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.annotations.taglib.apt;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

public class TagAnnotationProcessor implements AnnotationProcessor {
  public static final String TAG = "org.apache.struts2.annotations.taglib.StrutsTag";
  public static final String TAG_ATTRIBUTE = "org.apache.struts2.annotations.taglib.StrutsTagAttribute";

  private AnnotationProcessorEnvironment environment;
  private AnnotationTypeDeclaration tagDeclaration;
  private AnnotationTypeDeclaration tagAttributeDeclaration;
  private Map<String, Tag> tags = new HashMap<String, Tag>();

  public TagAnnotationProcessor(AnnotationProcessorEnvironment env) {
    environment = env;
    tagDeclaration = (AnnotationTypeDeclaration) environment.getTypeDeclaration(TAG);
    tagAttributeDeclaration = (AnnotationTypeDeclaration) environment
        .getTypeDeclaration(TAG_ATTRIBUTE);
  }

  public void process() {
    //make sure all paramters were set
    checkOptions();

    // tags
    Collection<Declaration> tagDeclarations = environment
        .getDeclarationsAnnotatedWith(tagDeclaration);
    Collection<Declaration> attributesDeclarations = environment
        .getDeclarationsAnnotatedWith(tagAttributeDeclaration);

    // find Tags
    for (Declaration declaration : tagDeclarations) {
      // type
      TypeDeclaration typeDeclaration = (TypeDeclaration) declaration;
      String typeName = typeDeclaration.getQualifiedName();
      HashMap<String, Object> values = getValues(typeDeclaration, tagDeclaration);
      // create Tag and apply values found
      Tag tag = new Tag();
      tag.setDescription((String) values.get("description"));
      tag.setName((String) values.get("name"));
      tag.setTldBodyContent((String) values.get("tldBodyContent"));
      tag.setTldTagClass((String) values.get("tldTagClass"));
      tag.setDeclaredType(typeName);
      // add to map
      tags.put(typeName, tag);
    }

    // find Tags Attributes
    for (Declaration declaration : attributesDeclarations) {
      // type
      MethodDeclaration methodDeclaration = (MethodDeclaration) declaration;
      String typeName = methodDeclaration.getDeclaringType().getQualifiedName();
      HashMap<String, Object> values = getValues(methodDeclaration, tagAttributeDeclaration);
      // create Attribute and apply values found
      TagAttribute attribute = new TagAttribute();
      attribute.setDescription((String) values.get("description"));
      String name = (String) values.get("name");
      if(name == null || name.length() == 0) {
        //get name from method
        String methodName = methodDeclaration.getSimpleName();
        name = String.valueOf(Character.toLowerCase(methodName.charAt(3))) + methodName.substring(4);
      }
      methodDeclaration.getSimpleName();
      attribute.setName(name);
      attribute.setRequired((Boolean) values.get("required"));
      attribute.setRtexprvalue((Boolean) values.get("rtexprvalue"));
      // add to map
      Tag parentTag = tags.get(typeName);
      if(parentTag != null)
        tags.get(typeName).addTagAttribute(attribute);
      else {
        //an abstract or base class
        parentTag = new Tag();
        parentTag.setDeclaredType(typeName);
        parentTag.setInclude(false);
        parentTag.addTagAttribute(attribute);
        tags.put(typeName, parentTag);
      }
    }

    //we can't process the hierarchy on the first pass because
    //apt does not garantees that the base classes will be processed
    //before their subclasses
    for(Map.Entry<String, Tag> entry : tags.entrySet()) {
      processHierarchy(entry.getValue());
    }
    // save as xml
    save();
  }

  private void processHierarchy(Tag tag) {
    try {
      Class clazz = Class.forName(tag.getDeclaredType());
      while((clazz = clazz.getSuperclass()) != null) {
        Tag parentTag = tags.get(clazz.getName());
        //copy parent annotations to this tag
        if(parentTag != null) {
          for(TagAttribute attribute : parentTag.getAttributes()) {
            tag.addTagAttribute(attribute);
          }
        }
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private void checkOptions() {
    if(getOption("tlibVersion") == null)
      throw new IllegalArgumentException("'tlibVersion' is missing");
    if(getOption("jspVersion") == null)
      throw new IllegalArgumentException("'jspVersion' is missing");
    if(getOption("shortName") == null)
      throw new IllegalArgumentException("'shortName' is missing");
    if(getOption("description") == null)
      throw new IllegalArgumentException("'description' is missing");
    if(getOption("displayName") == null)
      throw new IllegalArgumentException("'displayName' is missing");
    if(getOption("uri") == null)
      throw new IllegalArgumentException("'uri' is missing");
  }

  private void save() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;

    try {
      // create xml document
      builder = factory.newDocumentBuilder();
      Document document = builder.newDocument();
      document.setXmlVersion("1.0");

      // taglib
      Element tagLib = document.createElement("taglib");
      document.appendChild(tagLib);
      // tag lib attributes
      appendTextNode(document, tagLib, "tlib-version", getOption("tlibVersion"), false);
      appendTextNode(document, tagLib, "jsp-version", getOption("jspVersion"), false);
      appendTextNode(document, tagLib, "short-name", getOption("shortName"), false);
      appendTextNode(document, tagLib, "uri", getOption("uri"), false);
      appendTextNode(document, tagLib, "display-name", getOption("displayName"), false);
      appendTextNode(document, tagLib, "description", getOption("description"), true);

      // create tags
      for (Map.Entry<String, Tag> entry : tags.entrySet()) {
        Tag tag = entry.getValue();
        if(tag.isInclude())
          createElement(document, tagLib, tag);
      }

      // save to file
      TransformerFactory tf = TransformerFactory.newInstance();
      tf.setAttribute("indent-number", new Integer(2));
      Transformer transformer = tf.newTransformer();
      //if tiger would just format it :(
      //formatting bug in tiger (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446)

      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN");
      transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

      Source source = new DOMSource(document);
      Result result = new StreamResult(new OutputStreamWriter(new FileOutputStream(getOption("out"))));
      transformer.transform(source, result);
    } catch (Exception e) {
      // oops we cannot throw checked exceptions
      throw new RuntimeException(e);
    }
  }

  private String getOption(String name) {
    // there is a bug in the 1.5 apt implementation:
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6258929
    // this is a hack-around
    if (environment.getOptions().containsKey(name))
      return environment.getOptions().get(name);

    for (Map.Entry<String, String> entry : environment.getOptions().entrySet()) {
      String key = entry.getKey();
      if (key.startsWith("-A" + name))
        return key.substring(key.indexOf("=") + 1);
    }
    return null;
  }

  private void createElement(Document doc, Element tagLibElement, Tag tag) {
    Element tagElement = doc.createElement("tag");
    tagLibElement.appendChild(tagElement);
    appendTextNode(doc, tagElement, "name", tag.getName(), false);
    appendTextNode(doc, tagElement, "tag-class", tag.getTldTagClass(), false);
    appendTextNode(doc, tagElement, "body-content", tag.getTldBodyContent(), false);
    appendTextNode(doc, tagElement, "description", tag.getDescription(), true);

    // save attributes
    for (TagAttribute attribute : tag.getAttributes()) {
      createElement(doc, tagElement, attribute);
    }

  }

  private void createElement(Document doc, Element tagElement, TagAttribute attribute) {
    Element attributeElement = doc.createElement("attribute");
    tagElement.appendChild(attributeElement);
    appendTextNode(doc, attributeElement, "name", attribute.getName(), false);
    appendTextNode(doc, attributeElement, "required", String.valueOf(attribute.isRequired()), false);
    appendTextNode(doc,
        attributeElement,
        "rtexprvalue",
        String.valueOf(attribute.isRtexprvalue()),
        false);
    appendTextNode(doc, attributeElement, "description", attribute.getDescription(), true);
  }

  private void appendTextNode(Document doc, Element element, String name, String text, boolean cdata) {
    Text textNode = cdata ? doc.createCDATASection(text) : doc.createTextNode(text);
    Element newElement = doc.createElement(name);
    newElement.appendChild(textNode);
    element.appendChild(newElement);
  }

  /**
   * Get values of annotation
   *
   * @param declaration
   * @param type
   *          The type of the annotation
   * @return name->value map of annotation values
   */
  private HashMap<String, Object> getValues(Declaration declaration, AnnotationTypeDeclaration type) {
    HashMap<String, Object> values = new HashMap<String, Object>();
    Collection<AnnotationMirror> annotations = declaration.getAnnotationMirrors();
    // iterate over the mirrors.
    for (AnnotationMirror mirror : annotations) {
      // if the mirror in this iteration is for our note declaration...
      if (mirror.getAnnotationType().getDeclaration().equals(type)) {

        // print out the goodies.
        Map<AnnotationTypeElementDeclaration, AnnotationValue> annotationValues = mirror
            .getElementValues();

        for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : annotationValues
            .entrySet()) {
          AnnotationTypeElementDeclaration key = entry.getKey();
          AnnotationValue value = entry.getValue();
          values.put(key.getSimpleName(), value.getValue());
        }
      }
    }
    return values;
  }
}
