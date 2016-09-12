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
.controller('regsvcCtrl', ['$scope', '$http', '$location', function($scope, $http, $location) {

  console.log("RegSvc : using controller 'regsvcCtrl'");
  
  $scope.selected = undefined;		//siteid of the currently selected room
  $scope.score = 0;
  
  $scope.eventId = $location.search().eventId;
  $scope.registrations = [];
  $scope.registration = {
		  eventId : "",
		  siteId : "",
		  gameonId : ""
  }
  
  $scope.ratings = [];
  $scope.rating = {
		  eventId : "",
		  siteId : "",
		  gameonId : "",
		  rating : 0  
  }
  
  $scope.msg = { list : {}, reg : {}, rating: {}}
  
  $scope.setSelected = function(id) {
	  console.log("RegSvc : setting selected to " + id);
	  $scope.rating = 0; //reset rating
	  for(var i = 0; i < $scope.registrations.length; i++) {
		   if($scope.registrations[i].eventId == id) {
			   $scope.selected = $scope.registrations[i];
			   return;		//set the selected item
		   }
	   }
  }
  
  $scope.setRegistration = function() {
	  //setup the manual registration with the correct event ID
	  $scope.registration.eventId = $scope.eventId;
  }
  
  $scope.setRating = function(rating) {
	  $scope.score = rating;
  }
  
  $scope.getRegistrations = function() {
	 console.log("RegSvc : getting list of registrations");
	 $http({
	     url: "/regsvc/v1/register",
	     method: "GET"
	   }).then(function (response) {
		   if($scope.eventId) {
			   console.log("RegSvc : filtering registrations for event " + $scope.eventId);
			   $scope.registrations = [];
			   for(var i = 0; i < response.data.length; i++) {
				   if(response.data[i].eventId == $scope.eventId) {
					   $scope.registrations.push(response.data[i]);
				   }
			   }
		   } else {
			   console.log("RegSvc : showing all registrations");
			   $scope.registrations = response.data;
		   }
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

  $scope.rate = function() {
		 console.log("RegSvc : Room being rated by user " + $scope.gameonId);
		 $scope.rating.siteId = $scope.selected.siteId;
		 $scope.rating.eventId = $scope.selected.eventId;
		 $scope.rating.rating = $scope.score;
		 
		 $http({
		     url: "/regsvc/v1/rate",
		     method: "POST",
		     headers: {  
		                 'contentType': 'application/json; charset=utf-8"' //what is being sent to the server
		     },
		     data: JSON.stringify($scope.rating).trim()
		   }).then(function (response) {
			    console.log('RegSvc : rating successful : response from server : ' + response.status);
			    $scope.msg.rating.status = "Room rated OK";
			    $scope.msg.rating.alert = undefined;
	   		}, function (response) {
	   			$scope.msg.rating.alert = "Failed to rate room : " + response.status;
	   			$scope.msg.rating.status = undefined;
	   		}
		 );	  
	  }
  
  $scope.getRatings = function() {
		 console.log("RegSvc : getting list of ratings for room");
		 $http({
		     url: "/regsvc/v1/rate",
		     method: "GET"
		   }).then(function (response) {
			   if($scope.eventId) {
				   console.log("RegSvc : filtering ratings for event " + $scope.eventId);
				   $scope.ratings = [];
				   for(var i = 0; i < response.data.length; i++) {
					   if(response.data[i].eventId == $scope.eventId) {
						   $scope.ratings.push(response.data[i]);
					   }
				   }
			   } else {
				   console.log("RegSvc : showing all ratings");
				   $scope.ratings = response.data;
			   }
			   $scope.msg.rating.status = "Room ratings retrieved OK";
			   $scope.msg.rating.alert = undefined;
	   		}, function (response) {
	   			console.log("RegSvc : failed to get ratings, response code from server " + response.status);
	   			$scope.msg.rating.alert = "Failed to get ratings : " + response.status;
	   			$scope.msg.rating.status = undefined;
	   		}
		 );
	  }
  
  $scope.getRegistrations();
  $scope.getRatings();
  

}]);