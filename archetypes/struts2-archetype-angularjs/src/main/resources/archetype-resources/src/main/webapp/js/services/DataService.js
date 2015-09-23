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
(function() {
    'use strict';

    angular
        .module('app')
        .factory('DataService', DataService);

    function DataService($http, $log, $q) {

        /** Object to manage all backend URL's */
        var urls = {
            projects : "data/projects"
        };

        /** The DataService with all public methods */
        var service = {
            getProjects: getProjects
        };

        return service;

        /** Get all projects */
        function getProjects() {
            return _request(urls.projects);
        }


        /** A generic helper method to execute a HTTP request to the backend */
        function _request(url, method, model){
            var def = $q.defer(),
                req = {
                method: method,
                url: url
            };

            if(!method) {
                req.method = 'GET';
            }

            if(model) {
                req.data = model;
            }
            $http(req).success(function(data) {
                def.resolve(data);
            }).error(function(data, code) {
                def.reject(data);
                $log.error(data, code);
            });
            return def.promise;
        }

    }

})();