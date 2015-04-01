package com.guntzergames.medievalwipeout.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.exceptions.GameException;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.managers.AccountManager;
import com.guntzergames.medievalwipeout.managers.GameManager;
import com.guntzergames.medievalwipeout.views.GameView;

@Stateless
@Path("/game")
public class GameResource {
	
	private final static Logger LOGGER = Logger.getLogger(GameResource.class);

	@EJB
	private GameManager gameManager;
	@EJB
	private AccountManager accountManager;
	
	private String buildGameView(Player player, Game game) throws PlayerNotInGameException {
		GameView gameView = game.buildGameView(player);
		String ret = gameView.toJson();
		player.getEvents().clear();
		return ret;
	}
	
	@GET
	@Path("join/{facebookUserId}/{deckId}")
    @Produces("text/plain")
	public String joinGame(@PathParam("facebookUserId") String facebookUserId, @PathParam("deckId") long deckId) throws GameException {
		Game game = gameManager.joinGame(facebookUserId, deckId);
		Player player = gameManager.selectPlayer(game, facebookUserId);
		
		LOGGER.info(String.format("Game joined: %s", game));
		String ret = buildGameView(player, game);
        return ret;
	}
	
	@GET
	@Path("delete/{gameId}")
    @Produces("text/plain")
	public void deleteGame(@PathParam("gameId") long gameId) {
		gameManager.deleteGame(gameId);
	}
	
	@GET
	@Path("drawInitialHand/{gameId}/{userName}")
    @Produces("text/plain")
	public String drawInitialHand(@PathParam("gameId") long gameId, @PathParam("userName") String userName) throws PlayerNotInGameException {
		Game game = gameManager.getGame(gameId);
		Player player = gameManager.selectPlayer(game, userName);
		gameManager.drawInitialHand(player, game);
		String ret = buildGameView(player, game);
        return ret;
	}
	

	@GET
	@Path("get/{gameId}/{facebookUserId}")
    @Produces("application/json")
	public String getGame(@PathParam("gameId") long gameId, @PathParam("facebookUserId") String facebookUserId) throws PlayerNotInGameException {
		Game game = gameManager.getGame(gameId);
		LOGGER.info("game.getPlayers().get(0).getAccount() " + game.getPlayers().get(0).getAccount());
		Player player = gameManager.selectPlayer(game, facebookUserId);
		String ret = buildGameView(player, game);
        return ret;
	}

	@GET
	@Path("nextPhase/{gameId}/{userName}")
    @Produces("text/plain")
	public String nextPhase(@PathParam("gameId") long gameId, @PathParam("userName") String userName) throws GameException {
		Game game = gameManager.nextPhase(gameId);
		Player player = gameManager.selectPlayer(game, userName);
		String ret = buildGameView(player, game);
        return ret;
	}

	@GET
	@Path("play/{userName}/{gameId}/{sourceLayout}/{sourceCardId}/{destinationLayout}/{destinationCardId}")
    @Produces("text/plain")
    public String playCard(@PathParam("userName") String userName, @PathParam("gameId") long gameId, @PathParam("sourceLayout") String sourceLayout, @PathParam("sourceCardId") int sourceCardId, @PathParam("destinationLayout") String destinationLayout, @PathParam("destinationCardId") int destinationCardId) 
	throws GameException {
		Game game = gameManager.playCard(userName, gameId, sourceLayout, sourceCardId, destinationLayout, destinationCardId);
		Player player = gameManager.selectPlayer(game, userName);
		String ret = buildGameView(player, game);
        return ret;
    }

	@GET
	@Path("getAll")
    @Produces("text/plain")
	public String getAllGames() {
		List<Game> games = gameManager.getAllGames();
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, games);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ret = new String(out.toByteArray());
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return ret;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
}
