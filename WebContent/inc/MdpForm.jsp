<%@ page pageEncoding="UTF-8"%>


<div class="form-group row">
	<label for="example-password-input" class="col-2 col-form-label">Password</label>
	<div class="col-10">
		<input class="form-control" type="password" value="hunter2"
			id="example-password-input">
	</div>
</div>
<label for="mdp">Mot de passe <span class="requis">*</span></label>
<input type="password" id="mdp" name="mdp" value="" size="40"
	maxlength="40" />
<span class="erreur">${form.erreurs['mdp']}</span>
<span class="tooltip">Le mot de passe ne doit pas faire moins de
	6 caractères</span>


<br />
<label for="confirmation">Confirmation du mot de passe <span
	class="requis">*</span></label>
<input type="password" id="confirmation" name="confirmation" value=""
	size="40" maxlength="40" />
<span class="erreur">${form.erreurs['confirmation']}</span>
<span class="tooltip">Le mot de passe de confirmation doit être
	identique à celui d'origine</span>

<br />