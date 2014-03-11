angular.module('wmRequests', ['wmEnter']).controller('RequestListCtrl', function ($scope, $http, $filter) {
  $scope.bodyDecoding = "raw";
  $scope.loading = 0;
  
  $scope.search= function() {
    $scope.filteredRequests = $filter("filter")($scope.requests, $scope.query);
  }

  // uncomment to search on every key hit
  // $scope.$watch("query", $scope.search);

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

  function getHttpRequest(req, body) {
    var ret = req.method + " " + req.url + " HTTP/1.1\n";
    for (var header in req.headers) {
      ret += header + ": " + req.headers[header] + "\n";
    }
    ret += "\n" + body;
    return ret;
  }

  $scope.updateHttpRequests= function() {
    var reqs = $scope.requests;
    for (var i=0, len=reqs.length; i<len; i++) {
      //need to add id to avoid issues with duplicate mappings
      var decodedBody;
      try {
        decodedBody = decode(reqs[i].body, $scope.bodyDecoding);
        reqs[i].failedDecoding = false;
      } catch (err) {
        decodedBody = reqs[i].body;
        reqs[i].failedDecoding = true;
      }
      reqs[i].httpRequest = getHttpRequest(reqs[i], decodedBody);
    }
  }

  $scope.refresh = function() {
    startRequest();
    var query = { "urlPattern" : "/.*", "method" : "ANY" };
    $http.post('/__admin/requests/find', query).success(function(data) {
      $scope.requests = data.requests;
      $scope.updateHttpRequests();
      $scope.search();
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

});
