<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<!DOCTYPE html>
<%@ page import="java.util.Date, java.text.SimpleDateFormat"%>
<%@page contentType="text/html" import="java.text.DateFormat"%>

<html lang="fr">
<c:import url="Header.jsp" />

<head>
<meta charset="utf-8" />


<title>APPCIIP</title>


</head>

<body>

	<div class="row">
		<div class="col-sm-3"></div>


		<div class="col-sm-6">
			<!-- Example row of columns -->

			<form id="Form" method="post" action="createManifestation"
				enctype="multipart/form-data">

				<c:import url="/inc/ManifestationForm.jsp" />




				<p class="${empty form.erreurs ? 'succes' : 'erreur'}">
					<br> ${form.resultat}
				</p>

				<input type="submit" value="Valider" /> <input type="reset"
					value="Remettre à zéro" /> <br />
			</form>
		</div>
		<div class="col-sm-3">
			<c:if test="${!empty message}">
				<p class="erreur">${message}</p>
			</c:if>
		</div>
	</div>

	<div class="container">
		<c:import url="Footer.jsp" />

	</div>


	<!-- /container -->
</body>
</html>
