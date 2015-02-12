# jini

#### What is Jini?

Jini is a static website generator, some thing to help you create static html website.

#### Features

* [HTML include through partial directive] (#partial-html)
* CoffeeScript compilation
* Ajax redirection
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


#### Lorem Tags

Lorem tags are Jini tags to create placeholder content.

```<lorem/>``` would replace the tag with entire ```lorem ipsum``` phrase

```<lorem length="2"/>``` would replace the tag with two words of  ```lorem ipsum``` phrase

```<lorem type="name" /> ``` would replace the tag with a random name consisting of a first name and last name.

```<lorem type="firstName" /> ``` would replace the tag with a random  first name.

```<lorem type="lastName" /> ``` would replace the tag with a random  last name.

```<lorem type="image" width="100" height="100"/> ``` would replace the tag with an image tag with generated image of mentioned size. 


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/nitizkumar/jini/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

