package com.example.quarkus.app;

import com.example.elasticsearch.SearchRequest;
import com.example.rest.dtos.ItemDto;
import com.example.rest.services.StoreDtoService;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/elastic")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchTestController {

    private final StoreDtoService storeDtoService;

    public SearchTestController(StoreDtoService storeDtoService) {this.storeDtoService = storeDtoService;}


    @GET
    @Path("/items")
    public Uni<List<ItemDto>> search(@BeanParam SearchRequest request) {
        return storeDtoService.getFilteredItems(request);
    }

}
