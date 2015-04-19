<#import "decorator.ftl" as decorator />

<@decorator.layout>
Yoruba name dictionary app.

<h4>Dashboard</h4>
Go to <a href="/dashboardapp/index.html#/home">dashboard</a>

<h4>Template</h4>
View <a href="/template">template</a>

<hr/>

<h4>Text To Speech Stub</h4>

Enter name <br/>
<input id="nametts" type="text"/>
<input id="submittts" type="button" value="Get Pronunciation" style="margin-top:-10px">
<br/>
<div id="ttsPlayer">

</div>
</@decorator.layout>
