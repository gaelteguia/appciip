<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>


<span><em>User </em></span>
<br />





<label for="nom">Nom <span class="requis">*</span>
</label>
<input type="text" id="nom" name="nom"
	value="<c:out value="${manifestation.nom}"/>" size="40" maxlength="40" />
<span class="erreur">${form.erreurs['nom']}</span>
<span class="tooltip">Un nom ne peut pas faire moins de 2
	caractères</span>


<br />

<label for="prenom">Prénom <span class="requis">*</span></label>
<input type="text" id="prenom" name="prenom"
	value="<c:out value="${manifestation.prenom}"/>" size="40"
	maxlength="40" />
<span class="erreur">${form.erreurs['prenom']}</span>
<span class="tooltip">Un prénom ne peut pas faire moins de 2
	caractères</span>


<br />

<label for="email">E-mail <span class="requis">*</span></label>
<input type="text" id="email" name="email"
	value="<c:out value="${manifestation.email}"/>" size="40"
	maxlength="40" />
<span class="erreur">${form.erreurs['email']}</span>

<br />


<label for="telephone">Téléphone <span class="requis">*</span></label>
<input type="text" id="telephone" name="telephone"
	value="<c:out value="${manifestation.telephone}"/>" size="40"
	maxlength="40" />
<span class="erreur">${form.erreurs['telephone']}</span>

<br />

<label for="statut">Statut </label>
<select name="statut">
	<option value="Stagiaire">Stagiaire</option>
	<option value="VIE">VIE</option>
	<option value="Temporaire">Temporaire</option>
	<option value="Externe">Externe</option>
	<option value="Fixe">Fixe</option>
</select>
<span class="erreur">${form.erreurs['statut']}</span>
<span class="tooltip">Vous devez sélectionner un statut adequat</span>



<br />

<label for="sexe">Sexe <span class="requis">*</span></label>
<INPUT type="radio" name="sexe" value="H" checked="checked">
Homme
<INPUT type="radio" name="sexe" value="F">
Femme
<span class="erreur">${form.erreurs['sexe']}</span>
<span class="tooltip">Vous devez sélectionnez votre sexe</span>

<br />
<br />


<label for="datefinservice">Date de fin de service </label>
<input type="text" id="datefinservice" name="datefinservice"
	value="<joda:format value="${manifestation.dateFinService}" pattern="MM/dd/yyyy"/>"
	size="40" maxlength="40" />
<span class="erreur">${form.erreurs['datefinservice']}</span>

<br />
<label for="datefineffective">Date de sortie </label>
<input type="text" id="datefineffective" name="datefineffective"
	value="<joda:format value="${manifestation.dateFinEffective}" pattern="MM/dd/yyyy"/>"
	size="40" maxlength="40" />
<span class="erreur">${form.erreurs['datefineffective']}</span>

<br />

<c:if test="${ sessionScope.sessionManifestation.admin }">
	<br />

	<label for="admin">Administrateur </label>
	<INPUT type="radio" name="admin" value="TRUE" checked="checked">
Oui
<INPUT type="radio" name="admin" value="FALSE">
Non
<span class="erreur">${form.erreurs['admin']}</span>
	<br />
</c:if>
<br />