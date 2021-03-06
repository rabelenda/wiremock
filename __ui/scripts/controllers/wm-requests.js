angular.module('wmRequests', []).controller('RequestListCtrl', function ($scope, $http, $filter,
$localStorage) {
  $scope.$storage = $localStorage.$default({
    bodyDecoding : "raw",
    showHeaders : true
  });
  $scope.loading = 0;

  $scope.decodings = [
    {"value" : "raw", "label" : "Raw Body"},
    {"value" : "latin","label" : "Latin Body"},
    {"value" : "utf8", "label" : "UTF-8 Body"}
  ];

  $scope.search= function() {
    $scope.filteredRequests = $filter("filter")($scope.requests, $scope.$storage.requestsQuery);
  };

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

  $scope.updateDecodedBody= function(request) {
    try {
      request.decodedBody = decode(request.body, $scope.$storage.bodyDecoding);
      request.failedDecoding = false;
    } catch (err) {
      request.decodedBody = request.body;
      request.failedDecoding = true;
    }
  };

  function getHttpRequest(req, showHeaders) {
    var ret = req.method + " " + req.url + " HTTP/1.1\n";
    if (showHeaders) {
      for (var header in req.headers) {
        ret += header + ": " + req.headers[header] + "\n";
      }
    }
    ret += "\n" + req.decodedBody;
    return ret;
  }

  $scope.updateHttpRequest= function(request) {
    request.httpRequest = getHttpRequest(request, $scope.$storage.showHeaders);
  };

  $scope.updatedDecoding= function() {
    var reqs = $scope.requests;
    for (var i=0, len=reqs.length; i<len; i++) {
      $scope.updateDecodedBody(reqs[i]);
      $scope.updateHttpRequest(reqs[i]);
    }
  };

  $scope.updatedShowHeaders= function() {
    var reqs = $scope.requests;
    for (var i=0, len=reqs.length; i<len; i++) {
      $scope.updateHttpRequest(reqs[i]);
    }
  };

  $scope.refresh = function() {
    startRequest();
    var query = { "urlPattern" : "/.*", "method" : "ANY" };
    $http.post('/__admin/requests/find', query).success(function(data) {
      $scope.requests = data.requests;
      $scope.updatedDecoding();
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
