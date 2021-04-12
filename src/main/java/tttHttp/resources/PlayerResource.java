package tttHttp.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import tttHttp.DTO.NewPlayerDTO;
import tttHttp.DTO.PlayerDTO;
import tttHttp.controllers.PlayerController;

import java.net.URI;

@Path("/players")
public class PlayerResource {

    PlayerController playerController = new PlayerController();

    @GET
    @Path("/{playerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayerInformation(@PathParam("playerId") int playerId, @Context HttpHeaders headers){
        String playerToken = headers.getHeaderString("playerToken");
        PlayerDTO player = playerController.getPlayerDTO(playerId, playerToken);
        return Response
                .ok()
                .entity(player)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postNewPlayer(String playerName, @Context UriInfo uriInfo){
        NewPlayerDTO newPlayerDTO = playerController.addNewPlayer(playerName);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newPlayerDTO.getPlayerId())).build();
        return Response
                .created(uri)
                .status(Response.Status.CREATED)
                .header("playerId", newPlayerDTO.getPlayerId())
                .header("playerToken", newPlayerDTO.getPlayerToken())
                .entity(newPlayerDTO)
                .build();
    }
}
