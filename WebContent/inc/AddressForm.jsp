<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>


<span><em>Adresse </em></span>
<br />
<div class="form-group row">
	<label for="nameaddress" class="col-2 col-form-label">Name</label>
	<div class="col-10">
		<input class="form-control" type="text"
			value="<c:out value="${address.name}"/>" id="nameaddress"
			name="nameaddress"> <span class="erreur">${form.erreurs['nameaddress']}</span>
	</div>
</div>
<br />
<div class="form-group row">
	<label for="city" class="col-2 col-form-label">City</label>
	<div class="col-10">
		<input class="form-control" type="text"
			value="<c:out value="${address.city}"/>" id="city" name="city">
		<span class="erreur">${form.erreurs['city']}</span>
	</div>
</div>
<br />
<div class="form-group row">
	<label for="npa" class="col-2 col-form-label">Npa</label>
	<div class="col-10">
		<input class="form-control" type="number"
			value="<c:out value="${address.npa}"/>" id="npa" name="npa">
		<span class="erreur">${form.erreurs['npa']}</span> <span
			class="tooltip">Le numero postal est incorrect</span>
	</div>
</div>

<br />
<div class="form-group row">
	<label for="street" class="col-2 col-form-label">Rue</label>
	<div class="col-10">
		<input class="form-control" type="search"
			value="<c:out value="${address.street}"/>" id="street" name="street">
		<span class="erreur">${form.erreurs['street']}</span>
	</div>
</div>




<br />
<div class="form-group row">
	<label for="number" class="col-2 col-form-label">No. Rue</label>
	<div class="col-10">
		<input class="form-control" type="number"
			value="<c:out value="${address.number}"/>" id="number" name="number">
		<span class="erreur">${form.erreurs['number']}</span>
	</div>
</div>
<br />
<div class="form-group row">
	<label for="country" class="col-2 col-form-label">Pays</label>
	<div class="col-10">
		<input class="form-control" type="search"
			value="<c:out value="${address.country}"/>" id="country"
			name="country"> <span class="erreur">${form.erreurs['country']}</span>
	</div>
</div>

<br />
<div class="form-group row">
	<label class="mr-sm-2" for="canton">Canton</label> <select
		class="custom-select mb-2 mr-sm-2 mb-sm-0" id="canton" name="canton">
		<option selected>Choose...</option>
		<option value="1">One</option>
		<option value="2">Two</option>
		<option value="3">Three</option>
	</select> <span class="erreur">${form.erreurs['canton']}</span>
</div>




<br />
<div class="form-group row">
	<label for="example-email-input" class="col-2 col-form-label">Email</label>
	<div class="col-10">
		<input class="form-control" type="email" value="bootstrap@example.com"
			id="example-email-input">
	</div>
</div>
<br />
<div class="form-group row">
	<label for="example-tel-input" class="col-2 col-form-label">Telephone</label>
	<div class="col-10">
		<input class="form-control" type="tel" value="1-(555)-555-5555"
			id="example-tel-input">
	</div>
</div>









<br />

<br />

<br />

<input type="hidden" id="id" name="id"
	value="<c:out value="${ address.id }"/>" size="20" maxlength="20" />