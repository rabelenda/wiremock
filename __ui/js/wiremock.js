angular.module('wiremock', []).
  config(function($routeProvider) {
    $routeProvider.
      when('/requests', {controller:RequestListCtrl, templateUrl:'requestList.html'}).
      otherwise({redirectTo:'/requests'});
  });
 
 
function RequestListCtrl($scope, $http, $filter) {
  $scope.bodyDecoding = "raw";
  $scope.loading = 0;
  
  function search(query) {
    $scope.filteredRequests = $filter("filter")($scope.requests, query);
  }

  //$scope.$watch("search", search);

  function startRequest() {
    $scope.loading++;
  }

  function endRequest() {
    $scope.loading--;
    $scope.connectionError = false;
  }

  function failedRequest(data, status) {
    $scope.loading--;
    $scope.connectionError = true;
    console.log("Status: " + status + "\nData: " + data);
  }

  function decode(str, encoding) {
    switch(encoding) {
      case "raw":
        return str;
      case "latin":
        return unescape(str.replace(/\+/g, ' '));
      case "utf8":
        return decodeURIComponent(str.replace(/\+/g, ' '));
    }
  }

  $scope.decodeBodies = function() {
    var reqs = $scope.requests;
    for (var i=0,len=reqs.length; i<len; i++) {
      try {
        reqs[i].decodedBody = decode(reqs[i].body, $scope.bodyDecoding);
        reqs[i].failedDecoding = false;
      } catch (err) {
        reqs[i].decodedBody = reqs[i].body;
        reqs[i].failedDecoding = true;
      }
    }
  }

  $scope.refresh = function() {
    startRequest();
    var query = { "urlPattern" : "/.*", "method" : "ANY" };
    $http.post('/__admin/requests/find', query).success(function(data) {
      $scope.requests = data.requests;
      $scope.decodeBodies();
      search($scope.search);
      endRequest();
    }). error(function(data, status) {
      failedRequest(data, status);
    });
  };

  $scope.refresh();
  
  $scope.clear = function() {
    startRequest();
    $http.post('/__admin/requests/clear').success(function(data) {
      $scope.requests = [];
      $scope.filteredRequests = [];
      endRequest();
    }). error(function(data, status) {
      failedRequest(data, status);
    });
  };

  $scope.reload = function() {
      startRequest();
      $http.post('/__admin/mappings/reload').success(function(data) {
        endRequest();
      }). error(function(data, status) {
        failedRequest(data, status);
      });
    };
}