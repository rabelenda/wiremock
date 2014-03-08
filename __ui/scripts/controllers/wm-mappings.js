angular.module('wmMappings', []).controller('MappingListCtrl', function ($scope, $http) {
  $scope.loading = 0;

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

  $scope.refresh = function() {
    startRequest();
    $http.get('/__admin/').success(function(data) {
      $scope.mappings = prettyMappings(data.mappings);
      endRequest();
    }). error(function(data, status) {
      failedRequest(data, status);
    });
  };

  function prettyMappings(mappings) {
    var ret = [];
    for (var i=0, len=mappings.length; i<len; i++) {
        ret.push(angular.toJson(mappings[i],true));
    }
    return ret;
  }

  $scope.refresh();

});