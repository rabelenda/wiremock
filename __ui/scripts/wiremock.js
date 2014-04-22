//,'ui.bootstrap'
angular.module('wiremock', ['mgcrea.ngStrap','ngRoute','labsEnter','hljs','ngStorage','ui.codemirror'
,'wmRequests','wmMappings','wmSettings']).
  config(function($routeProvider) {
    $routeProvider.
      when('/requests', {controller: 'RequestListCtrl', templateUrl:'views/requestList.html'}).
      when('/mappings', {controller: 'MappingListCtrl', templateUrl:'views/mappingList.html'}).
      when('/files', {templateUrl:'views/todo.html'}).
      when('/settings', {controller: 'SettingsCtrl', templateUrl:'views/settings.html'}).
      otherwise({redirectTo:'/requests'});
  });
