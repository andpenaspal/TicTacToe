package tttHttp.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import tttHttp.DTO.GameDTO;
import tttHttp.controllers.GameController;
import tttHttp.models.Point;

import java.net.URI;

@Path("/players/{playerId}/games")
public class GameResource {

    GameController gameController = new GameController();

    @GET
    @Path("/{gameId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGame(@PathParam("playerId") int playerId, @PathParam("gameId") int gameId, @Context HttpHeaders headers){
        String playerToken = headers.getHeaderString("playerToken");
        GameDTO gameDTO = gameController.getGame(playerId, gameId, playerToken);
        return Response
                .ok()
                .entity(gameDTO)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newGame(@PathParam("playerId") int playerId, @Context HttpHeaders headers, @Context UriInfo uriInfo){
        String playerToken = headers.getHeaderString("playerToken");
        GameDTO gameDTO = gameController.newGame(playerId, playerToken);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(gameDTO.getGameId())).build();
        return Response
                .created(uri)
                .entity(gameDTO)
                .build();
    }

    @PATCH
    @Path("/{gameId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeMove(@PathParam("playerId") int playerId, @PathParam("gameId") int gameId,
                             Point gameMove, @Context HttpHeaders headers){
        String playerToken = headers.getHeaderString("playerToken");
        GameDTO gameDTO = gameController.makeMove(playerId, playerToken, gameId, gameMove);
        return Response
                .accepted(gameDTO)
                .build();
    }

    @DELETE
    @Path("/{gameId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response surrender(@PathParam("playerId") int playerId, @PathParam("gameId") int gameId, @Context HttpHeaders headers){
        String playerToken = headers.getHeaderString("playerToken");
        GameDTO gameDTO = gameController.surrender(playerId, playerToken, gameId);
        return Response
                .accepted(gameDTO)
                .build();
    }
}
