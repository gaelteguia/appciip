<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!-- Ne pas oublier cette ligne sinon tous les tags de la JSTL seront ignorés ! -->
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<html>
<div id="header">
	<c:import url="Header.jsp" />
</div>
<head>
<meta charset="utf-8" />
<title>Consulter un utilisateur</title>

</head>
<body>
	<div id="rightcol">
		<c:import url="Menu.jsp" />
	</div>
	<div id="leftcol"></div>
	<div id="content">

		<c:if test="${!empty message}">
			<p class="erreur">${message}</p>
		</c:if>

		<div>
			<form method="post" action="consulterUtilisateur">
				<c:if test="${!succes}">
					<fieldset>
						<legend>Consultation d'un utilisateur</legend>
						<div>
							<select name="listeUtilisateurs" id="listeUtilisateurs">
								<option value="">Choisissez un utilisateur...</option>
								<%-- Boucle sur la map des utilisateurs --%>
								<c:forEach items="${ sessionScope.utilisateurs }"
									var="mapUtilisateurs">
									<%--  L'expression EL ${mapUtilisateurs.value} permet de cibler l'objet Client stocké en tant que valeur dans la Map, 
                                  et on cible ensuite simplement ses propriétés nom et prenom comme on le ferait avec n'importe quel bean. --%>
									<option value="${ mapUtilisateurs.key }">${ mapUtilisateurs.value.prenom }
										${ mapUtilisateurs.value.nom }</option>
								</c:forEach>
							</select>
						</div>


					</fieldset>
				</c:if>
				<br />
				<c:if test="${succes}">
					<fieldset>

						<legend>Champs à consulter</legend>
						<br />
						<p class="info">${ form.resultat }</p>
						<p>
							Image :
							<c:out value="${ utilisateur.image }" />

							<%-- On ne construit et affiche un lien vers l'image que si elle existe. --%>
							<c:if test="${ !empty utilisateur.image }">
								<c:set var="image">
									<c:out value="${ utilisateur.image }" />
								</c:set>
								<a href="<c:url value="/images/${ image }"/>" target="_blank"
									class="exemple-lien">Voir</a>
							</c:if>


						</p>

						<label for="login">Login </label> <input type="text" id="login"
							name="login" value="<c:out value="${utilisateur.login}"/>"
							size="40" maxlength="40" /> <span class="erreur">${form.erreurs['login']}</span>

						<br /> <label for="nom">Nom </label> <input type="text" id="nom"
							name="nom" value="<c:out value="${utilisateur.nom}"/>" size="40"
							maxlength="40" /> <span class="erreur">${form.erreurs['nom']}</span>

						<br /> <label for="prenom">Prenom </label> <input type="text"
							id="prenom" name="prenom"
							value="<c:out value="${utilisateur.prenom}"/>" size="40"
							maxlength="40" /> <span class="erreur">${form.erreurs['prenom']}</span>

						<br /> <label for="email">E-mail </label> <input type="text"
							id="email" name="email"
							value="<c:out value="${utilisateur.email}"/>" size="40"
							maxlength="40" /> <span class="erreur">${form.erreurs['email']}</span>

						<br /> <label for="telephone">Telephone </label> <input
							type="text" id="telephone" name="telephone"
							value="<c:out value="${utilisateur.telephone}"/>" size="40"
							maxlength="40" /> <span class="erreur">${form.erreurs['telephone']}</span>

						<br /> <label for="statut">Statut </label> <input type="text"
							id="statut" name="statut"
							value="<c:out value="${utilisateur.statut}"/>" size="40"
							maxlength="40" /> <span class="erreur">${form.erreurs['statut']}</span>
						<br /> <label for="sexe">Sexe </label> <input type="text"
							id="sexe" name="sexe"
							value="<c:out value="${utilisateur.sexe}"/>" size="40"
							maxlength="40" /> <br /> <br /> <label for="datedebutservice">Date
							d'entrée en service </label> <input type="text" id="datedebutservice"
							name="datedebutservice"
							value="<joda:format value="${utilisateur.dateDebutService}" pattern="MM/dd/yyyy"/>"
							size="40" maxlength="40" /> <span class="erreur">${form.erreurs['datedebutservice']}</span>

						<br /> <label for="datefinservice">Date de fin de service
						</label> <input type="text" id="datefinservice" name="datefinservice"
							value="<joda:format value="${utilisateur.dateFinService}" pattern="MM/dd/yyyy"/>"
							size="40" maxlength="40" /> <span class="erreur">${form.erreurs['datefinservice']}</span>

						<br /> <label for="datefineffective">Date de sortie </label> <input
							type="text" id="datefineffective" name="datefineffective"
							value="<joda:format value="${utilisateur.dateFinEffective}" pattern="MM/dd/yyyy"/>"
							size="40" maxlength="40" /> <span class="erreur">${form.erreurs['datefineffective']}</span>


						<br />

						<c:if test="${ sessionScope.sessionUtilisateur.admin }">

							<br />
							<label for="admin">Administrateur </label>
							<input type="text" id="admin" name="admin"
								value="<c:out value="${utilisateur.admin}"/>" size="40"
								maxlength="40" />
							<br />
						</c:if>

						<br /> <label for="nomsuperviseur">Superieur hiérarchique
						</label> <input type="text" id="nom" name="nom"
							value="${utilisateur.superviseur.prenom } ${utilisateur.superviseur.nom }"
							size="60" maxlength="60" /> <br /> <label for="nomservice">Service
						</label> <input type="text" id="nom" name="nom"
							value="${utilisateur.service.nom }" size="60" maxlength="60" />
						<br /> <label for="nomlocalisation">Localisation </label> <input
							type="text" id="nom" name="nom"
							value="${utilisateur.localisation.nom }" size="60" maxlength="60" />

						<br /> <label for="nommetier">Profil métier </label> <input
							type="text" id="nom" name="nom"
							value="${utilisateur.metier.nom }" size="60" maxlength="60" /> <br />
						<table class="none">
							<caption>Liste des profils d'accès</caption>

							<thead class="none">
								<!-- En-tête du tableau -->
								<tr class="blue">

									<th class="grise">Identifiant du profil d'accès</th>

								</tr>
							</thead>
							<tbody class="none">
								<!-- Corps du tableau -->
								<%-- Parcours de la Map des commandes en session, et utilisation de l'objet varStatus. --%>
								<c:forEach items="${ utilisateur.profils }" var="listeProfils"
									varStatus="boucle">
									<%-- Simple test de parité sur l'index de parcours, pour alterner la couleur de fond de chaque ligne du tableau. --%>
									<tr class="${boucle.index % 2 == 0 ? 'pair' : 'impair'}">
										<%-- Affichage des propriétés du bean Commande, qui est stocké en tant que valeur de l'entrée courante de la map --%>
										<td><c:out value="${ listeProfils.identifiant }" /></td>
									</tr>
								</c:forEach>

							</tbody>

						</table>
						<c:if test="${ sessionScope.sessionUtilisateur.admin }">
							<table class="none">

								<caption>Historique de l'utilisateur</caption>

								<thead class="none">
									<!-- En-tête du tableau -->
									<tr class="blue">

										<th class="grise">Titre</th>
										<th class="grise">Contenu</th>
										<th class="grise">Date</th>
									</tr>
								</thead>
								<tbody class="none">
									<!-- Corps du tableau -->
									<%-- Parcours de la Map des commandes en session, et utilisation de l'objet varStatus. --%>
									<c:forEach items="${ utilisateur.notes }" var="listeNotes"
										varStatus="boucle">
										<%-- Simple test de parité sur l'index de parcours, pour alterner la couleur de fond de chaque ligne du tableau. --%>
										<tr class="${boucle.index % 2 == 0 ? 'pair' : 'impair'}">
											<%-- Affichage des propriétés du bean Commande, qui est stocké en tant que valeur de l'entrée courante de la map --%>
											<td><c:out value="${ listeNotes.titre }" /></td>
											<td><c:out value="${ listeNotes.contenu }" /></td>
											<td><c:out value="${ listeNotes.date }" /></td>
										</tr>
									</c:forEach>
								</tbody>

							</table>
						</c:if>
					</fieldset>
				</c:if>
				<c:if test="${!succes}">
					<input type="submit" value="Valider" />
					<input type="reset" value="Remettre à zéro" />
					<br />
				</c:if>
			</form>
			<c:if test="${succes}">

				<div align="right">
					<form method="post" action="imprimerExcelUtilisateur">
						<input type="hidden" id="id" name="id"
							value="<c:out value="${ utilisateur.id }"/>" size="20"
							maxlength="20" /><input type="submit"
							value="Enregistrer au format Excel" />
					</form>
				</div>
				<div align="right">
					<form method="post" action="imprimerExcelListeProfilDuUtilisateur">
						<input type="hidden" id="id" name="id" value="${utilisateur.id}"
							size="40" maxlength="40" /> <input type="submit"
							value="Enregistrer la liste des profils d'accès au format Excel" />
					</form>
				</div>
				<div align="right">
					<form method="post" action="imprimerExcelListeNoteDuUtilisateur">
						<input type="hidden" id="id" name="id" value="${utilisateur.id}"
							size="40" maxlength="40" /> <input type="submit"
							value="Enregistrer l'historique au format Excel" />
					</form>
				</div>
			</c:if>

		</div>
	</div>
	<div id="footer">
		<c:import url="Footer.jsp" />
	</div>
</body>
</html>