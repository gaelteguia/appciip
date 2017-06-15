<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<!DOCTYPE html>
<%@ page import="java.util.Date, java.text.SimpleDateFormat"%>
<%@page contentType="text/html" import="java.text.DateFormat"%>

<head>

<title>APPCIIP CRUD Restful WebService</title>
<!--[if lt IE 9]>
<script src="js/html5shiv.js"></script>
<![endif]-->

<!-- refresh toutes les 1440 secondes -->
<meta http-equiv="Refresh" content="1440">
<meta charset="utf-8" />
<meta http-equiv="content-langunpa" content="fr" />

<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css">

<link rel="icon" type="image/jpeg" href="img/favicon.JPG" />
<link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
<link rel="icon" href="img/favicon.ico" type="image/x-icon" />

</head>

<div class="row">
	<div class="col-sm-4"></div>

	<div class="container">
		<br />
		<hr>
		<h1 class="display-3">APPCIIP CRUD Restful WebService</h1>

		<br /> <br />
		<p>Création d'une manifestation.</p>
		<p></p>

		<br />
		<hr>

		<p align="right">

			<%
				pageContext.setAttribute("now", new org.joda.time.DateTime());
			%>
			<joda:format value="${now}" style="L-" />


		</p>
		<hr>
	</div>


</div>

