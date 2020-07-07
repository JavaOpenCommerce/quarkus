package com.example.quarkus.app;

import com.example.business.models.ItemModel;
import com.example.database.services.ItemService;
import com.example.rest.dtos.ItemDetailDto;
import com.example.rest.services.CardDtoService;
import com.example.rest.services.StoreDtoService;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestController {

    private final StoreDtoService storeService;
    private final CardDtoService cardService;
    private final ItemService itemService;

    public TestController(StoreDtoService storeService,
            CardDtoService cardService,
            ItemService itemService) {
        this.storeService = storeService;
        this.cardService = cardService;
        this.itemService = itemService;
    }


    @GET
    @Path("reactive/items/{id}")
    public Uni<ItemDetailDto> getItemById(@PathParam("id") Long id) {
        return storeService.getItemById(id);
    }

    @GET
    @Path("reactive/items")
    public Uni<List<ItemModel>> getAll() {
        return itemService.getAllItems();
    }

}
