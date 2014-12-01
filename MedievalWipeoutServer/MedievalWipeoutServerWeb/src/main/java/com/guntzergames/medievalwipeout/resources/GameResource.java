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

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.managers.AccountManager;
import com.guntzergames.medievalwipeout.managers.GameManager;
import com.guntzergames.medievalwipeout.managers.PlayerManager;
import com.guntzergames.medievalwipeout.views.GameView;

@Stateless
@Path("/game")
public class GameResource {

	@EJB
	private GameManager gameManager;
	@EJB
	private AccountManager accountManager;
	@EJB
	private PlayerManager playerManager;
	
	private String buildGameView(Player player, Game game) throws PlayerNotInGameException {
		GameView gameView = game.buildGameView(player);
		String ret = gameView.toJson();
		player.getEvents().clear();
		return ret;
	}
	
	@GET
	@Path("join/{facebookUserId}/{deckId}")
    @Produces("text/plain")
	public String joinGame(@PathParam("facebookUserId") String facebookUserId, @PathParam("deckId") long deckId) throws PlayerNotInGameException {
		Account account = accountManager.getAccount(facebookUserId, false);
		System.out.println(String.format("Account: %s", account));
		Player player = new Player();
		player.setAccount(account);
		DeckTemplate deckTemplate = gameManager.findDeckTemplateById(deckId);
		player.setDeckTemplate(deckTemplate);
		Game game = gameManager.joinGame(player);
		System.out.println(String.format("Game joined: %s", game));
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
		Player player = playerManager.selectPlayer(game, userName);
		gameManager.drawInitialHand(player, game);
		String ret = buildGameView(player, game);
        return ret;
	}
	

	@GET
	@Path("get/{gameId}/{userName}")
    @Produces("text/plain")
	public String getGame(@PathParam("gameId") long gameId, @PathParam("userName") String userName) throws PlayerNotInGameException {
		Game game = gameManager.getGame(gameId);
		Player player = playerManager.selectPlayer(game, userName);
		String ret = buildGameView(player, game);
        return ret;
	}

	@GET
	@Path("nextPhase/{gameId}/{userName}")
    @Produces("text/plain")
	public String nextPhase(@PathParam("gameId") long gameId, @PathParam("userName") String userName) throws PlayerNotInGameException {
		Game game = gameManager.nextPhase(gameId);
		Player player = playerManager.selectPlayer(game, userName);
		String ret = buildGameView(player, game);
        return ret;
	}

	@GET
	@Path("play/{userName}/{gameId}/{destinationLayout}/{cardId}")
    @Produces("text/plain")
    public String playCard(@PathParam("userName") String userName, @PathParam("gameId") long gameId, @PathParam("destinationLayout") String destinationLayout, @PathParam("cardId") int cardId) 
	throws PlayerNotInGameException {
		Game game = gameManager.playCard(userName, gameId, destinationLayout, cardId);
		Player player = playerManager.selectPlayer(game, userName);
		String ret = buildGameView(player, game);
        return ret;
    }

	@GET
	@Path("getAll")
    @Produces("text/plain")
	public String getAllGames() {
		List<Game> games = gameManager.getAllOngoingGames();
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