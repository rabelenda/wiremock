angular.module('wiremock', ['ngRoute','$strap.directives','wmEnter','wmHighlight','wmRequests',
'wmMappings','wmSettings']).
  config(function($routeProvider) {
    $routeProvider.
      when('/requests', {controller: 'RequestListCtrl', templateUrl:'views/requestList.html'}).
      when('/mappings', {controller: 'MappingListCtrl', templateUrl:'views/mappingList.html'}).
      when('/files', {templateUrl:'views/todo.html'}).
      when('/settings', {controller: 'SettingsCtrl', templateUrl:'views/settings.html'}).
      //when('/docs', {templateUrl:'views/todo.html'}).
      otherwise({redirectTo:'/requests'});
  });