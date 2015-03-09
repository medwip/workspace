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

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.beans.CardModelList;
import com.guntzergames.medievalwipeout.beans.CollectionElement;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.GameViewList;
import com.guntzergames.medievalwipeout.beans.Packet;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.managers.AccountManager;
import com.guntzergames.medievalwipeout.managers.GameManager;
import com.guntzergames.medievalwipeout.views.GameView;

@Stateless
@Path("/account")
public class AccountResource {
	
	private static final Logger LOGGER = Logger.getLogger(AccountResource.class);

	@EJB
	private GameManager gameManager;
	@EJB
	private AccountManager accountManager;
	
	@GET
	@Path("get/{facebookUserId}")
    @Produces("text/plain")
	public String joinGame(@PathParam("facebookUserId") String facebookUserId) throws PlayerNotInGameException {
		Account account = accountManager.getAccount(facebookUserId, false);
		LOGGER.info(String.format("Account: %s", account));
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, account);
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
	
	@GET
	@Path("getGames/{facebookUserId}")
    @Produces("text/plain")
	public String getGames(@PathParam("facebookUserId") String facebookUserId) throws PlayerNotInGameException {
		Account account = accountManager.getAccount(facebookUserId, false);
		LOGGER.info(String.format("Account: %s", account));
		
		LOGGER.info("Before findGamesByAccount");
		List<GameView> games = accountManager.findGameViewsByAccount(account);
		LOGGER.info("After findGamesByAccount, games=" + games.size());
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, new GameViewList(games));
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
	
	@GET
	@Path("addDeckTemplateElement/{facebookUserId}/{deckTemplateId}/{collectionElementId}")
    @Produces("text/plain")
	public String addDeckTemplateElement(
			@PathParam("facebookUserId") String facebookUserId,
			@PathParam("deckTemplateId") long deckTemplateId,
			@PathParam("collectionElementId") long collectionElementId) {
		
		// TODO: Verification that classes are linked
		Account account = accountManager.getAccount(facebookUserId, false);
		System.out.println("Before resource Number of elements: " + account.getCollectionElements().size());
		DeckTemplate deckTemplate = accountManager.findDeckTemplateById(deckTemplateId);
		CollectionElement collectionElement = accountManager.findCollectionElementById(collectionElementId);
		accountManager.addDeckTemplateElement(collectionElement, deckTemplate);
		
		System.out.println("After resource Number of elements: " + account.getCollectionElements().size());
		
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, account);
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
	
	@GET
	@Path("openPacket/{facebookUserId}")
    @Produces("text/plain")
	public String openPacket(@PathParam("facebookUserId") String facebookUserId) {
		
		Account account = accountManager.getAccount(facebookUserId, false);
		Packet packet = accountManager.drawPacket(5, account);
		return packet.toJson();
		
	}
	
	@GET
	@Path("getCardModels")
    @Produces("text/plain")
	public String getCardModels() {
		List<CardModel> cardModels = accountManager.findAllCardModels();
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, new CardModelList(cardModels));
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
	
}
