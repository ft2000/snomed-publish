MyApp = Ember.Application.create({
  rootElement: '#refset',
  LOG_TRANSITIONS: true
});

MyApp.Router.map(function() {
  this.route('index', {path: '/'});
  this.resource("rules", {path: "/"});
  this.resource("concepts", {path: "/concepts"});
});
 
MyApp.ApplicationRoute = Ember.Route.extend({
  actions: {
    click: function(concept){
      $("#concept-title").text(concept.title);
      $("#concept-title").attr('href', 'http://browser.snomedtools.com/version/1/concept/' + concept.id);
      $("#concept-id").val(concept.id);
      var selected = $(".toggle-find-concept.selected");
      var notSelected = $(".toggle-find-concept.not-selected");
      selected.attr('class', 'toggle-find-concept selected active');
      notSelected.attr('class', 'toggle-find-concept not-selected inactive');
      $('#textSearchModal').modal('hide');
      console.log('handled click');
      return false;
    },
  },
});  

MyApp.IndexController = Ember.ObjectController.extend({

});