angular.module('wiremock', ['ngRoute','$strap.directives','wmEnter','wmRequests']).
  config(function($routeProvider) {
    $routeProvider.
      when('/requests', {controller: 'RequestListCtrl', templateUrl:'views/requestList.html'}).
      when('/mappings', {templateUrl:'views/todo.html'}).
      when('/settings', {templateUrl:'views/todo.html'}).
      when('/docs', {templateUrl:'views/todo.html'}).
      otherwise({redirectTo:'/requests'});
  });