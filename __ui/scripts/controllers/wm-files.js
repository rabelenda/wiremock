angular.module('wmFiles', []).controller('FileListCtrl', function ($scope, $http, $filter,
$localStorage) {
  $scope.$storage = $localStorage;
  $scope.loading = 0;

  $scope.search= function() {
    $scope.filteredFiles = $filter("filter")($scope.files, $scope.$storage.filesQuery);
  }

  // uncomment to search on every key hit
  // $scope.$watch("$storage.query", $scope.search);

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
    $http.get('/__admin/files').success(function(data) {
      $scope.files = data.files;
      $scope.search();
      endRequest();
    }). error(function(data, status) {
      failedRequest(data, status);
    });
  };

  $scope.openFile = function(file) {
    var link = document.createElement("a");
    link.download = file;
    link.href = '/__files/' + file;
    link.click();
  }

  $scope.refresh();

});
