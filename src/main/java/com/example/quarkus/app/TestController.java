package com.example.quarkus.app;

import com.example.rest.dtos.CategoryDto;
import com.example.rest.dtos.ItemDetailDto;
import com.example.rest.dtos.PageDto;
import com.example.rest.dtos.ProducerDto;
import com.example.rest.services.StoreDtoService;
import com.example.utils.LanguageResolver;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestController {


    private final StoreDtoService service;
    private final LanguageResolver extractor;

    public TestController(StoreDtoService service, LanguageResolver extractor) {this.service = service;
        this.extractor = extractor;
    }

    @GET
    @Path("/items/{id}")
    public ItemDetailDto getItemById(@PathParam("id") Long id) {
        return service.getItemById(id);
    }

    @GET
    @Path("/items/category/{categoryId}")
    public PageDto getAllByCategory(@PathParam("categoryId") Long id,
            @QueryParam("page") int page,
            @QueryParam("size") int size) {
        return service.getItemsPageByCategory(id, page, size);
    }

    @GET
    @Path("/items/producer/{producerId}")
    public PageDto getAllByProducer(@PathParam("producerId") Long id,
            @QueryParam("page") int page,
            @QueryParam("size") int size) {
        return service.getItemsPageByProducer(id, page, size);
    }

    @GET
    @Path("/items")
    public PageDto getAll(@QueryParam("page") int page,
            @QueryParam("size") int size) {
        return service.getAllItemsPage(page, size);
    }

    @GET
    @Path("/categories")
    public List<CategoryDto> getAllCategories() {
        return service.getCategoryList();
    }

    @GET
    @Path("/producers")
    public List<ProducerDto> getAllProducers() {
        return service.getProducerList();
    }

    @GET
    @Path("/locale")
    public String getLocale() {
        return extractor.getLanguage();
    }
}
