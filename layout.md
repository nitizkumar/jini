### Layout Markup

Layout markup simplifies the layout in HTML, it is inspired from XUL/MXML in terms of concept. Layout markup consist of two
tags &lt;hbox&gt; and &lt;vbox&gt;, hbox is a container for which all child of the container are aligned horizontally, and vbox is a container
for which all child of the container are aligned vertically. 


### vbox

VBox translates to a regular div with all its children having style display:block

### hbox

HBox translates to a div with all its children having style float:left

```html
<hbox>
  <div>first child</div>
  <div>second child</div>
  <div>third child</div>
<hbox>
```

would translate to 

```html
<div class="clearfix"> <!-- this would clear out the float -->
  <div class="pull-left">first child</div> <!--Note the use of Bootstrap style -->
  <div class="pull-left">second child</div>
  <div class="pull-left">third child</div>
<hbox>
```

#### Grid Columns

Layout markup do support grid concept through bootstrap grid system. you can specify the grid columns using col="x" on a hbox.

```html
<hbox>
  <div col="3">first child</div>
  <div col="4">second child</div>
  <div col="3">third child</div>
  <div col="2">fourth child</div>
<hbox>
```

would translate to 

```html
<div class="clearfix"> <!-- this would clear out the float -->
  <div class="pull-left col-lg-3">first child</div> <!--Note the use of Bootstrap style -->
  <div class="pull-left col-lg-4">second child</div>
  <div class="pull-left col-lg-3">third child</div>
  <div class="pull-left col-lg-2">fourth child</div>
<hbox>
```
