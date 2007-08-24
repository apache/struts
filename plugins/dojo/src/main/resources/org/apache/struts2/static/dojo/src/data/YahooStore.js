/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.YahooStore");
dojo.require("dojo.data.core.RemoteStore");
dojo.require("dojo.lang.declare");
dojo.require("dojo.io.ScriptSrcIO");
dojo.declare("dojo.data.YahooStore", dojo.data.core.RemoteStore, {_setupQueryRequest:function (result, requestKw) {
	var start = 1;
	var count = 1;
	if (result) {
		start = result.start || start;
		count = result.count || count;
	}
	var sourceUrl = "http://api.search.yahoo.com/WebSearchService/V1/webSearch?appid=dojo&language=en&query=" + result.query + "&start=" + start + "&results=" + count + "&output=json";
	requestKw.url = sourceUrl;
	requestKw.transport = "ScriptSrcTransport";
	requestKw.mimetype = "text/json";
	requestKw.jsonParamName = "callback";
}, _resultToQueryMetadata:function (json) {
	return json.ResultSet;
}, _resultToQueryData:function (json) {
	var data = {};
	for (var i = 0; i < json.ResultSet.totalResultsReturned; ++i) {
		var record = json.ResultSet.Result[i];
		var item = {};
		item["Url"] = [record.Url];
		item["Title"] = [record.Title];
		item["Summary"] = [record.Summary];
		var arrayIndex = (json.ResultSet.firstResultPosition - 1) + i;
		data[arrayIndex.toString()] = item;
	}
	return data;
}});

