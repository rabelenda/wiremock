angular.module('wiremock', []).
  config(function($routeProvider) {
    $routeProvider.
      when('/requests', {controller:RequestsCtrl, templateUrl:'requests.html'}).
      otherwise({redirectTo:'/requests'});
  });
 
 
function RequestsCtrl($scope, $http, $filter) {
  var query = { "urlPattern" : "/.*", "method" : "ANY" };

  function search(query) {
    $scope.filteredRequests = $filter("filter")($scope.requests, query);
  }

  $scope.$watch("search", search);

  $scope.refresh = function() {
    $http.post('/__admin/requests/find', query).success(function(data) {
      $scope.requests = data.requests;
      search($scope.search);
    }). error(function(data, status) {
      alert("Error: " + data + "\n\nStatus: " + status);
    });
  };
  
  $scope.refresh();
  
  $scope.clear = function() {
    $http.post('/__admin/mappings/reset').success(function(data) {
      $scope.requests = [];
      $scope.filteredRequests = [];
    }). error(function(data, status) {
      alert("Error: " + data + "\n\nStatus: " + status);
    });
  };
}