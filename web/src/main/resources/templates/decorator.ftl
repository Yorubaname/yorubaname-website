<#macro layout headerTitle="" templateClass="default">
    <#import "spring.ftl" as spring />
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <title>Welcome To YorubaName.com</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- CSS -->
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
    <link rel='stylesheet' href='http://fonts.googleapis.com/css?family=PT+Sans:400,700'>
    <link rel='stylesheet' href='http://fonts.googleapis.com/css?family=Oleo+Script:400,700'>
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/css/style.css">
    <link rel="shortcut icon" href="assets/img/favicon.ico">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->


</head>

<body class="${templateClass}">
<div class="wrapper">
    <#nested/>
</div>
<!-- Javascript -->
<script src="assets/js/jquery-include.js"></script>
<script src="assets/js/jquery-2..1.3.min.js"></script>
<script src="assets/js/scripts.js"></script>
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>
</#macro>