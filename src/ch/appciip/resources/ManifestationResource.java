package ch.appciip.resources;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import ch.appciip.bean.Address;
import ch.appciip.bean.Manifestation;
import ch.appciip.storage.ManifestationStore;
import ch.appciip.util.ParamUtil;

public class ManifestationResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String manifestation;

	public ManifestationResource(UriInfo uriInfo, Request request, String manifestation) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.manifestation = manifestation;
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Manifestation getManifestation() {
		Manifestation cont = ManifestationStore.getStore().get(manifestation);
		if (cont == null)
			throw new NotFoundException("No such Manifestation.");
		return cont;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putManifestation(JAXBElement<Manifestation> jaxbManifestation) {
		Manifestation c = jaxbManifestation.getValue();
		return putAndGetResponse(c);
	}

	@PUT
	public Response putManifestation(@Context HttpHeaders herders, byte[] in) {
		Map<String, String> params = ParamUtil.parse(new String(in));
		Manifestation c = new Manifestation(params.get("id"), params.get("name"), new Address());
		return putAndGetResponse(c);
	}

	private Response putAndGetResponse(Manifestation c) {
		Response res;
		if (ManifestationStore.getStore().containsKey(c.getId())) {
			res = Response.noContent().build();
		} else {
			res = Response.created(uriInfo.getAbsolutePath()).build();
		}
		ManifestationStore.getStore().put(c.getId(), c);
		return res;
	}

	@DELETE
	public void deleteManifestation() {
		Manifestation c = ManifestationStore.getStore().remove(manifestation);
		if (c == null)
			throw new NotFoundException("No such Manifestation.");
	}
}
