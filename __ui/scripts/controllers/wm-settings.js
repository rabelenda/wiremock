angular.module('wmSettings', []).controller('SettingsCtrl', function ($scope, $http) {
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
    $http.post('/__admin/settings/get').success(function(data) {
      $scope.fixedDelay = data.fixedDelay;
      $scope.journalCapacity = data.journalCapacity;
      endRequest();
    }). error(function(data, status) {
      failedRequest(data, status);
    });
  };

  $scope.refresh();
});