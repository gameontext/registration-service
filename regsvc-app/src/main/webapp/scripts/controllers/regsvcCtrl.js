/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

'use strict';

angular.module('regSvcApp')
.controller('regsvcCtrl', ['$scope', '$http', function($scope, $http) {

  console.log("RegSvc : using controller 'regsvcCtrl'");
  
  $scope.registrations = [];
  $scope.registration = {
		  eventId : "testEvent",
		  siteId : "123456789",
		  gameonId : "github:123456789"
  }
  $scope.msg = { list : {}, reg : {}}
  
  $scope.getRegistrations = function() {
	 console.log("RegSvc : getting list of registrations");
	 $http({
	     url: "/regsvc/v1/register",
	     method: "GET"
	   }).then(function (response) {
		    $scope.registrations = response.data;
		    $scope.msg.list.status = "Registration list retrieved OK";
		    $scope.msg.list.alert = undefined;
   		}, function (response) {
   			console.log("RegSvc : response code from server " + response.status);
   			$scope.msg.list.alert = "Failed to get registered list : " + response.status;
   			$scope.msg.list.status = undefined;
   		}
	 );
  }

  $scope.register = function() {
	 console.log("RegSvc : Registering room for user " + $scope.gameonId);
	 $http({
	     url: "/regsvc/v1/register",
	     method: "POST",
	     headers: {  
	                 'contentType': 'application/json; charset=utf-8"' //what is being sent to the server
	     },
	     data: JSON.stringify($scope.registration).trim()
	   }).then(function (response) {
		    console.log('RegSvc : registration successful : response from server : ' + response.status);
		    $scope.msg.reg.status = "Room registered OK";
		    $scope.msg.reg.alert = undefined;
		    $scope.registrations.push(response.data);
   		}, function (response) {
   			$scope.msg.reg.alert = "Failed to register room : " + response.status;
   			$scope.msg.reg.status = undefined;
   		}
	 );	  
  }
  
  $scope.getRegistrations();

}]);