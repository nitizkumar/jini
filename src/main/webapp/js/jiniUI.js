var AppView, JiniUI, headerView, router;

JiniUI = Backbone.Router.extend({
  routes: {
    '': 'load'
  },
  load: function() {
    var appView;
    appView = new AppView();
    return appView.render();
  }
});

AppView = Backbone.View.extend({
  el: '#swagger-ui-container',
  initialize: function() {
    var header;
    header = new headerView();
    header.initialize();
  }
});

headerView = Backbone.View.extend({
  el: '#api_selector',
  events: {
    'click #explore': 'createResource'
  },
  createResource: function() {
    return console.log("Create a new resource");
  },
  initialize: function() {
    return console.log("Initializing header");
  }
});

router = new JiniUI();

Backbone.history.start();
