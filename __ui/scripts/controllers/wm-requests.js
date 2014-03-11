angular.module('wmRequests', ['wmEnter', 'ui.bootstrap']).controller('RequestListCtrl', function ($scope, $http, $filter, $modal) {
  $scope.bodyDecoding = "raw";
  $scope.loading = 0;
  
  function search(query) {
    $scope.filteredRequests = $filter("filter")($scope.requests, query);
  }

  // uncomment to search on every key hit 
  // $scope.$watch("search", search);

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
    $http.post('/__admin/requests/reset').success(function(data) {
      $scope.requests = [];
      $scope.filteredRequests = [];
      endRequest();
    }). error(function(data, status) {
      failedRequest(data, status);
    });
  };

  $scope.open = function ($url, $body) {
    var modalInstance = $modal.open({
      templateUrl: 'modal.html',
      controller: ModalInstanceCtrl,
      resolve: {
        url: function () {
          return $url;
        },
        body: function () {
          return $body;
        },
      }
    });
  };
})
.filter('substring', function() {
	return function(str, start, end) {
		return str.substring(start, end);
	};
});


var ModalInstanceCtrl = function ($scope, $modalInstance, url, body) {

  $scope.url = url;
  $scope.body = body;

  $scope.ok = function () {
    $modalInstance.close();
  };

};