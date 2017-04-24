<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<span><em>Manifestation </em></span>

<br />
<hr>
<div class="form-group row">
	<label for="title" class="col-2 col-form-label">Titre <span
		class="requis">*</span></label>
	<div class="col-10">
		<input class="form-control" type="text"
			value="<c:out value="${manifestation.title}"/>" id="title"
			name="title"> <span class="erreur">${form.erreurs['title']}</span>
		<span class="tooltip">Le title ne peut pas faire moins de 2
			caractères</span>
	</div>
</div>
<br />

<div class="form-group row">
	<label for="description">Description <span class="requis">*</span></label>
	<textarea class="form-control" id="description" name="description"
		rows=10 cols=50><c:out value="${manifestation.description}" /></textarea>
	<span class="erreur">${form.erreurs['description']}</span>
</div>
<br />
<div class="form-group row">
	<label for="starttime" class="col-2 col-form-label">Horaire de
		debut <span class="requis">*</span>
	</label>
	<div class="col-10">
		<input class="form-control" type="datetime-local"
			value="<joda:format value="${manifestation.startTime}" pattern="dd/MM/yyyy"/>"
			id="starttime" name="starttime"> <span class="erreur">${form.erreurs['starttime']}</span>
	</div>
</div>
<br />
<div class="form-group row">
	<label for="endtime" class="col-2 col-form-label">Horaire de
		fin <span class="requis">*</span>
	</label>
	<div class="col-10">
		<input class="form-control" type="datetime-local"
			value="<joda:format value="${manifestation.endTime}" pattern="dd/MM/yyyy"/>"
			id="endtime" name="endtime"> <span class="erreur">${form.erreurs['endtime']}</span>
	</div>
</div>
<br />

<div class="form-group row">
	<label for="image">Image</label> <input type="file"
		class="form-control-file" id="image" aria-describedby="fileHelp"
		name="image"> <span class="erreur">${form.erreurs['image']}</span><small
		id="fileHelp" class="form-text text-muted">Selectionnez une
		image illustrative</small>
</div>


<br />




<input type="hidden" id="id" name="id"
	value="<c:out value="${ manifestation.id }"/>" size="20" maxlength="20" />

