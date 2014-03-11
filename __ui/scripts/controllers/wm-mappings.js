angular.module('wmMappings', []).controller('MappingListCtrl', function ($scope, $http, $filter) {
  $scope.loading = 0;

  // uncomment to search on every key hit
  // $scope.$watch("search", search);

  function search(query) {
    $scope.filteredMappings = $filter("filter")($scope.mappings, query);
  }

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

  function getWithPrettyJsonAndId(mappings) {
    var ret = [];
    for (var i=0, len=mappings.length; i<len; i++) {
      //need to add id to avoid issues with duplicate mappings
      ret.push({
        "id": i,
        "json": angular.toJson(mappings[i],true)
      });
    }
    return ret;
  }

  $scope.refresh = function() {
    startRequest();
    $http.get('/__admin/').success(function(data) {
      $scope.mappings = getWithPrettyJsonAndId(data.mappings);
      search($scope.search);
      endRequest();
    }). error(function(data, status) {
      failedRequest(data, status);
    });
  };

  $scope.reload = function() {
    startRequest();
    $http.post('/__admin/mappings/reset').success(function(data) {
      $scope.refresh();
      endRequest();
    }). error(function(data, status) {
      failedRequest(data, status);
    });
  };

  $scope.refresh();

});