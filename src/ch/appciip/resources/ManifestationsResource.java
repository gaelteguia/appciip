package ch.appciip.resources;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.appciip.bean.Address;
import ch.appciip.bean.Manifestation;
import ch.appciip.storage.ManifestationStore;

@Path("/manifestations")
public class ManifestationsResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Manifestation> getManifestations() {
		List<Manifestation> manifestations = new ArrayList<Manifestation>();
		manifestations.addAll(ManifestationStore.getStore().values());
		return manifestations;
	}

	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = ManifestationStore.getStore().size();
		return String.valueOf(count);
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newManifestation(@FormParam("id") String id, @FormParam("name") String name,
			@Context HttpServletResponse servletResponse) throws IOException {
		Manifestation c = new Manifestation(id, name, new Address());
		ManifestationStore.getStore().put(id, c);

		URI uri = uriInfo.getAbsolutePathBuilder().path(id).build();
		Response.created(uri).build();

		servletResponse.sendRedirect("../pages/new_manifestation.html");
	}

	@Path("{manifestation}")
	public ManifestationResource getManifestation(@PathParam("manifestation") String manifestation) {
		return new ManifestationResource(uriInfo, request, manifestation);
	}

}
