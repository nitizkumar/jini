# jini

#### What is Jini?

Jini is a static website generator, some thing to help you create static html website.

#### Features

* HTML include through partial directive 
* CoffeeScript compilation
* Ajax redirection
* [Layout markup](https://github.com/nitizkumar/jini/blob/master/layout.md)  (Early implementation) 


#### Partial html

Partial html files can be included in any html file using &lt;%= partial "fragment" %&gt; , this would search for a _fragment.html file in the same folder as parent file and replace the directive with content of fragment file.

The partial directive supports files in nested folder as well, which should enable users to modularize their html project in different folder. ```<%= partial "mymodule/fragment" %>```  would look for a file _fragment.html in the folder mymodule relative to the file path of including file, however in case the file is not present then Jini would look for the path mymodule/_fragment.html relative to the project root.


