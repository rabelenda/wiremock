angular.module("wmHighlight",[]).directive('wmHighlight', function() {

  function link(scope, element, attrs) {
    scope.$watch('mappings', function(value) {
      //need to use element[0] to unwrap jquery element
      hljs.highlightBlock(element[0]);
    });
  }

  return {
    link : link
  };
});