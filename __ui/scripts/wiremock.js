angular.module('wiremock', ['ngRoute','mgcrea.ngStrap','labsEnter','hljs','ngStorage','wmRequests',
'wmMappings','wmSettings']).
  config(function($routeProvider) {
    $routeProvider.
      when('/requests', {controller: 'RequestListCtrl', templateUrl:'views/requestList.html'}).
      when('/mappings', {controller: 'MappingListCtrl', templateUrl:'views/mappingList.html'}).
      when('/files', {templateUrl:'views/todo.html'}).
      when('/settings', {controller: 'SettingsCtrl', templateUrl:'views/settings.html'}).
      otherwise({redirectTo:'/requests'});
  });