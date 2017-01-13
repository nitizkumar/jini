# jini

[![Join the chat at https://gitter.im/nitizkumar/jini](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nitizkumar/jini?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

#### What is Jini?

Jini is a static website generator, some thing to help you create static html website.

#### License

Jini is available under [Apache 2.0](http://opensource.org/licenses/Apache-2.0) license, which essentially means you can use it for commercial project without being bothered by lawyers. 

#### Features

* [HTML include through partial directive] (#partial-html)
* CoffeeScript compilation
* [Ajax redirection] (#ajax)
* [Lorem Tags] (#lorem-tags)
* [Layout markup](https://github.com/nitizkumar/jini/blob/master/layout.md)  (Early implementation) 

#### Download Link 

The latest version of Jini is 2.1 available [here](http://162.243.44.112/compose/Jini-2.1.jar)

### Using Jini

Jini is a java swing application and would need some form of JVM to run.. Though it is not specific to any particular version of Java, it has not been tested on lower versions of Java.

You can run Jini either by double clicking the jar file if the jar file has execute permission or you can type ```java -jar Jini-2.1.jar``` in to the command line or terminal. This would load up the Jini UI, now point Jini to the folder where your project is located either by pasting the path of the folder in the text input at top or by using browse button. Once you have pointed Jini to correct location, click the start button and this would start the Jini server on port 9090. Your converted html files would now be available at ``http://localhost:9090/index.html``


#### Partial html

Partial html files can be included in any html file using ```<%= partial "fragment" %>``` , this would search for a _fragment.html file in the same folder as parent file and replace the directive with content of fragment file.

The partial directive supports files in nested folder as well, which should enable users to modularize their html project in different folder. ```<%= partial "mymodule/fragment" %>```  would look for a file _fragment.html in the folder mymodule relative to the file path of including file, however in case the file is not present then Jini would look for the path mymodule/_fragment.html relative to the project root.

#### Ajax

When you are developing static html project, you have to deal with ajax calls to backend server. Normally you can do it using JSONP but if your html and backend are finally going to be co-located then its better to use JSON instead of JSONP. Jini lets you redirect any ajax call on localhost to any host url, this works similar to a proxy server. This enables you to develop html site without running backend server on your system. Jini would cache the response from the server and optionally can serve the cached ajax response allowing you to go offline.

You should create a file called Jini.properties in the workspace folder. An example property file is provided.
```
API_PATH = /api/
API_REMOTE_SERVER_URL = http://www.remote-server.com/myapplication/api/
API_RESOURCE_PATH = /remoteresouce/
API_REMOTE_RESOURCE_URL = http://www.remote-server.com/myapplication/remoteresource/
CACHE_SERVER=false
HEADER_PARAMS=my-token-in-request-header
```

```API_PATH``` is a pattern when matched on ajax url the request gets sent to a path specified by ```API_REMOTE_SERVER_URL```, Similarly for resources you can use ```API_RESOURCE_PATH``` and ```API_REMOTE_RESOURCE_URL```.

```CACHE_SERVER``` when set to false would connect to remote server for every request, when turned to true, Jini would serve response from cache.

```HEADER_PARAMS``` is a parameter which would be added to request sent to remote server by Jini. If you have a auth token or session token in request header, you can specify it here. 


#### Lorem Tags

Lorem tags are Jini tags to create placeholder content.

```<lorem/>``` would replace the tag with entire ```lorem ipsum``` phrase

```<lorem length="2"/>``` would replace the tag with two words of  ```lorem ipsum``` phrase

```<lorem type="name" /> ``` would replace the tag with a random name consisting of a first name and last name.

```<lorem type="firstName" /> ``` would replace the tag with a random  first name.

```<lorem type="lastName" /> ``` would replace the tag with a random  last name.

```<lorem type="image" width="100" height="100"/> ``` would replace the tag with an image tag with generated image of mentioned size. 

#### Motivation and History

Jini started out as an internal project inside [Clarice Technology] (http://claricetechnologies.com/) where we needed a simple tool to modularize large HTML projects, since HTML does not support include directive unless clubbed with ASP,JSP,PHP or some other server side technology, we ended up building our own include directive inspired from [Erubis] (http://www.kuwata-lab.com/erubis/). Once we implemented Jini is projects, we kept on adding features based on our own requirement.



[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/nitizkumar/jini/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

