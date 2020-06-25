package com.example.quarkus.app;

import com.example.rest.dtos.ItemDto;
import com.example.rest.dtos.PageDto;
import com.example.rest.services.SearchDtoService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestSearchController {

    private final SearchDtoService searchDtoService;

    public TestSearchController(SearchDtoService searchDtoService) {this.searchDtoService = searchDtoService;}

    @GET
    @Path("/search")
    public PageDto<ItemDto> searchItem(@QueryParam("pattern") String pattern,
            @QueryParam("size") Optional<Integer> size,
            @QueryParam("page") Optional<Integer> page) {
        return searchDtoService.searchForAProduct(pattern, size.orElse(10), page.orElse(0));
    }
}
