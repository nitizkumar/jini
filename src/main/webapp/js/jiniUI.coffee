JiniUI = Backbone.Router.extend	
	routes:
		'':'load'	
	load:->		
		appView = new AppView();
		appView.render()

AppView = Backbone.View.extend
	el: '#swagger-ui-container'	
	initialize:->
		header = new headerView()
		header.initialize()
		return

headerView = Backbone.View.extend
	el:
		'#api_selector'
	events:
		'click #explore': 'createResource'

	createResource:->
		console.log "Create a new resource"
	
	initialize:->
		console.log "Initializing header"

router = new JiniUI()
Backbone.history.start()