/*
 * $Id$
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
angularStrutsApp.factory('DataService', ['$http', '$q', function($http, $q) {

    var DataService = {
        urls : {
            projects : "data/projects"
        }
    };

    DataService._request = function(url, method, model){
        if(!method) {
            method = 'GET';
        }
        var def = $q.defer();
        if(method === 'GET') {
            return $http.get(url).success(function(data) {
                DataService.data = data;
                def.resolve(data);
            }).error(function() {
                def.reject("Failed to get data");
            });
        } else if(method === 'POST'){
            $http.post(url, model).success(function(data) {
                DataService.data = data;
                def.resolve(data);
            }).error(function() {
                def.reject("Failed to post data");
            });
        }
        return def.promise;
    };

    DataService.getProjects = function () {
        return this._request(this.urls.projects);
    };

    return DataService;
}]);