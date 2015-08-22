<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html lang="en" ng-app="app">
<head>
    <meta charset="utf-8">
    <title>My AngularJS Struts2 App</title>

    <base href="<s:url forceAddSchemeHostAndPort="true" includeContext="true" value="/" namespace="/" />">
</head>
<body>

<h2><s:property value="message"/></h2>

<div>
    <a href="/home">Home</a> - <a href="/projects">Projects</a>
</div>

<div ng-controller="AppController as app">
    <div ng-view></div>
</div>

<s:if test="useMinifiedResources">
    <script src="<s:url value="js/external.js" />"></script>
    <script src="<s:url value="js/application.js" />"></script>
</s:if>
<s:else>
    <script src="<s:url value="js/lib/angular/angular.min.js" />"></script>
    <script src="<s:url value="js/lib/angular/angular-route.min.js" />"></script>
    <script src="<s:url value="js/app.js" />"></script>
    <script src="<s:url value="js/config.js" />"></script>
    <script src="<s:url value="js/services/DataService.js" />"></script>
    <script src="<s:url value="js/controllers/AppController.js" />"></script>
    <script src="<s:url value="js/controllers/HomeController.js" />"></script>
    <script src="<s:url value="js/controllers/ApacheProjectsController.js" />"></script>
</s:else>
</body>
</html>
