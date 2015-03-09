package com.guntzergames.medievalwipeout.managers;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard.EventType;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard.PlayerType;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.beans.PlayerField;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard.Location;
import com.guntzergames.medievalwipeout.beans.PlayerHand;
import com.guntzergames.medievalwipeout.beans.PlayerHandCard;
import com.guntzergames.medievalwipeout.beans.ResourceDeck;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.GameState;
import com.guntzergames.medievalwipeout.enums.Phase;
import com.guntzergames.medievalwipeout.exceptions.GameException;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.exceptions.UnsupportedPhaseException;
import com.guntzergames.medievalwipeout.interfaces.CommonConstants;
import com.guntzergames.medievalwipeout.persistence.AccountDao;
import com.guntzergames.medievalwipeout.persistence.GameDao;
import com.guntzergames.medievalwipeout.singletons.GameSingleton;

@Stateless
public class GameManager {

	private final static Logger LOGGER = Logger.getLogger(GameManager.class);

	@EJB
	private GameSingleton gameSingleton;
	@EJB
	private GameDao gameDao;
	@EJB
	private AccountDao accountDao;

	public Game findGameToJoin(Player player) {

		Game ret = null;

		for (Game currentGame : gameSingleton.getAllOngoingGames()) {
			if (currentGame.getGameState() == GameState.WAITING_FOR_JOINER) {
				if (!currentGame.isPlayerRegistered(player.getAccount().getFacebookUserId())) {
					ret = currentGame;
				}
			}
		}

		return ret;

	}
	
	public Game mergeGame(Game game) {
		Game dbGame = gameDao.mergeGame(game);
		dbGame.setTransientFields(game);
		gameSingleton.addGame(dbGame);
		return dbGame;
	}

	public Game joinGame(Player player) throws PlayerNotInGameException {

		Game game = findGameToJoin(player);

		if (game == null) {
			game = new Game();
			LOGGER.info(String.format("Player %s joining game: %s", player, game));
			// game.setId((long) (Math.random() * 10000));
			game.addPlayer(player);
			game.setActivePlayer(player);
			player.setGame(game);
			game = mergeGame(game);
			game.setGameState(GameState.WAITING_FOR_JOINER);
			game = gameSingleton.addGame(game);
			// player = game.getActivePlayer();
			LOGGER.info(String.format("Game created, player=%s", player));
		} else {
			game.addPlayer(player);
			game.setActivePlayer(player);
			player.setGame(game);
			LOGGER.info(String.format("Player %s joining game: %s", player, game));
			game = mergeGame(game);
			LOGGER.info(String.format("After merge, game=%s", game));
			game.setGameState(GameState.INITIALIZING_CREATOR_HAND);
			initializeGame(game);
			game.setGameState(GameState.STARTED);
			game = mergeGame(game);
		}

		LOGGER.info(String.format("Before return in join, game=%s", game));
		return game;

	}

	public void initializeGame(Game game) throws PlayerNotInGameException {

		LOGGER.info(String.format("Before initialize game, game=%s", game));
		
		for (Player player : game.getPlayers()) {
			player.setPlayerDeck(player.getDeckTemplate().toDeck());
			initPlayer(player);
			drawInitialHand(player, game);
		}

		ResourceDeck resourceDeck = game.getResourceDeck();
		resourceDeck.addCard(new ResourceDeckCard(3, 0, 0));
		resourceDeck.addCard(new ResourceDeckCard(2, 1, 0));
		resourceDeck.addCard(new ResourceDeckCard(3, 0, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 0, 1));
		resourceDeck.addCard(new ResourceDeckCard(3, 0, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 3, 0));
		resourceDeck.addCard(new ResourceDeckCard(1, 2, 0));
		resourceDeck.addCard(new ResourceDeckCard(1, 2, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 0, 1));
		resourceDeck.addCard(new ResourceDeckCard(0, 3, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 0, 1));
		Collections.shuffle(resourceDeck.getCards());
		game.setTurn(1);
		game.setPhase(Phase.BEFORE_RESOURCE_CHOOSE);
		game.setActivePlayer(game.getPlayers().get(0));
		nextPhase(game.getId());

	}

	public void initPlayer(Player player) {
		player.setDefense(5);
		player.setCurrentDefense(player.getDefense());
		player.setGold(10);
		player.setTrade(5);
		player.setLifePoints(100);
	}

	/*
	 * public void resolveBeforeCreatorTurn(Game game) {
	 * 
	 * game.setPhase(Phase.DURING_CREATOR_PLAY);
	 * 
	 * }
	 * 
	 * public void resolveBeforeJoinerTurn(Game game) {
	 * 
	 * game.setPhase(Phase.DURING_JOINER_PLAY);
	 * 
	 * }
	 */
	public Game nextPhase(long gameId) {

		Game game = getGame(gameId);

		LOGGER.info(String.format("nextPhase : %s", game));

		switch (game.getPhase()) {

			case BEFORE_RESOURCE_CHOOSE:
				resolveBeforeResourceDraw(game.getActivePlayer());
				game.setResourceCard1(game.getResourceDeck().pop());
				game.getResourceCard1().setId(1);
				game.setResourceCard2(game.getResourceDeck().pop());
				game.getResourceCard2().setId(2);
				game.setPhase(Phase.DURING_RESOURCE_CHOOSE);
				break;

			case DURING_RESOURCE_CHOOSE:
				game.setPhase(Phase.AFTER_RESOURCE_CHOOSE);
				nextPhase(gameId);
				break;

			case AFTER_RESOURCE_CHOOSE:
				game.setPhase(Phase.BEFORE_DECK_DRAW);
				nextPhase(gameId);
				break;

			case BEFORE_DECK_DRAW:
				drawPlayerDeck(game.getActivePlayer());
				game.setPhase(Phase.DURING_DECK_DRAW);
				break;

			case DURING_DECK_DRAW:
				game.setPhase(Phase.AFTER_DECK_DRAW);
				nextPhase(gameId);
				break;

			case AFTER_DECK_DRAW:
				game.setPhase(Phase.BEFORE_RESOURCE_SELECT);
				nextPhase(gameId);
				break;

			case BEFORE_RESOURCE_SELECT:
				game.setPhase(Phase.DURING_RESOURCE_SELECT);
				break;

			case DURING_RESOURCE_SELECT:
				game.setPhase(Phase.AFTER_RESOURCE_SELECT);
				nextPhase(gameId);
				break;

			case AFTER_RESOURCE_SELECT:
				game.setPhase(Phase.BEFORE_PLAY);
				nextPhase(gameId);
				break;

			case BEFORE_PLAY:
				game.setPhase(Phase.DURING_PLAY);
				break;

			case DURING_PLAY:
				game.setPhase(Phase.AFTER_PLAY);
				nextPhase(gameId);
				break;

			case AFTER_PLAY:
				game.setPhase(Phase.BEFORE_RESOURCE_CHOOSE);
				game.nextTurn();
				nextPhase(gameId);
				break;

			default:
				break;

		}

		// Save game state in the database
		game = gameDao.saveGame(game);
		
		LOGGER.info("After nextPhase : " + game);

		return game;

	}

	public void drawPlayerDeck(Player player) {

		player.setPlayerDeckCard1(player.getplayerDeck().pop());
		player.setPlayerDeckCard2(player.getplayerDeck().pop());

	}

	public DeckTemplate findDeckTemplateById(long deckId) {

		return gameDao.findDeckTemplateById(deckId);

	}

	public List<DeckTemplate> findDeckTemplatesByAccount(Account account) {

		return gameDao.findDeckTemplatesByAccount(account);

	}

	public void resolveBeforeResourceDraw(Player player) {

		player.setCurrentDefense(player.getDefense());

	}

	public Game playCard(String userName, long gameId, String sourceLayout, int sourceCardId, String destinationLayout, int destinationCardId) throws GameException {

		Game game = getGame(gameId);
		Player player = selectPlayer(game, userName);
		List<Player> opponents = game.selectOpponents(player);
		// TODO
		Player opponent = opponents.get(0);
		PlayerHand hand = player.getPlayerHand();

		switch (game.getPhase()) {

			case DURING_PLAY:
				System.out.println(String.format("cardId = %d", sourceCardId));

				GameEventPlayCard playerEvent = new GameEventPlayCard(PlayerType.PLAYER);

				// Play card from hand
				if (destinationLayout.startsWith("playerField")) {
					PlayerField playerField = null;
					PlayerHandCard card = hand.getCards().get(sourceCardId);
					PlayerFieldCard fieldCard = new PlayerFieldCard(card);
					if (destinationLayout.endsWith("Attack")) {
						playerField = player.getPlayerFieldAttack();
						fieldCard.setLocation(Location.ATTACK);
						fieldCard.setPlayed(false);
					} else {
						playerField = player.getPlayerFieldDefense();
						fieldCard.setLocation(Location.DEFENSE);
						fieldCard.setPlayed(true);
					}
					playerEvent.setSource(card);
					playerEvent.setSourceIndex(sourceCardId);
					playerEvent.setDestination(fieldCard);
					playerEvent.setDestinationIndex(playerField.getLastIndex());
					player.setGold(player.getGold() - card.getGoldCost());
					playerField.addCard(fieldCard);
					hand.getCards().remove((int) sourceCardId);
					player.updatePlayableHandCards();
				}

				// Play card from field
				if (sourceLayout.equals("playerFieldAttack")) {

					PlayerField playerField = player.getPlayerFieldAttack();
					PlayerFieldCard sourceCard = playerField.getCards().get(sourceCardId);

					if (destinationLayout.equals("opponentFieldDefense")) {
						playerEvent.setDestination(new PlayerFieldCard(sourceCard));
						playerEvent.setDestinationIndex(sourceCardId);
						playerEvent.setEventType(EventType.ATTACK_DEFENSE_FIELD);
						if (opponent.getCurrentDefense() > sourceCard.getAttack()) {
							opponent.removeCurrentDefense(sourceCard.getAttack());
						} else {
							opponent.removeLifePoints(sourceCard.getAttack() - opponent.getCurrentDefense());
							opponent.setCurrentDefense(0);
						}
					}

					if (destinationLayout.startsWith("opponentCard")) {
						PlayerFieldCard destinationCard = null;
						playerEvent.setEventType(EventType.ATTACK_ATTACK_CARD);
						PlayerField destinationField = null;
						if (destinationLayout.endsWith("Attack")) {
							destinationField = opponent.getPlayerFieldAttack();
						} else if (destinationLayout.endsWith("Defense")) {
							destinationField = opponent.getPlayerFieldDefense();
						} else {
							throw new GameException(String.format("Unknown destination layout: %s", destinationLayout));
						}
						destinationCard = destinationField.getCards().get(destinationCardId);
						if (destinationCard.getCurrentLifePoints() > sourceCard.getAttack()) {
							destinationCard.removeCurrentLifePoints(sourceCard.getAttack());
						} else {
							opponent.removeLifePoints(sourceCard.getAttack() - opponent.getCurrentDefense());
							destinationCard.setCurrentLifePoints(0);
							destinationField.getCards().remove(destinationCardId);
						}
						playerEvent.setDestination(destinationCard);
						playerEvent.setDestinationIndex(destinationCardId);
					}

					System.out.println(String.format("Source layout: %s", sourceLayout));
					playerEvent.setSource(sourceCard);
					playerEvent.setSourceIndex(sourceCardId);
					sourceCard.setPlayed(true);

				}

				player.getEvents().add(playerEvent);
				opponent.getEvents().add(playerEvent.duplicate());
				System.out.println(String.format("Event added: %s", playerEvent.toString()));

				break;

			case DURING_DECK_DRAW:
				PlayerDeckCard playerDeckCard = ((sourceCardId == 1) ? player.getPlayerDeckCard1() : player.getPlayerDeckCard2());
				player.getPlayerHand().getCards().add(new PlayerHandCard(playerDeckCard, player));
				game = nextPhase(gameId);
				break;

			case DURING_RESOURCE_CHOOSE:
				/*
				 * ResourceDeckCard playerResourceDeckCard = ((cardId == 1) ?
				 * game.getResourceCard1() : game.getResourceCard2());
				 * player.addTrade(playerResourceDeckCard.getTrade());
				 * player.addDefense(playerResourceDeckCard.getDefense());
				 * player.addFaith(playerResourceDeckCard.getFaith());
				 * player.updatePlayableHandCards();
				 */
				ResourceDeckCard gameResourceDeckCard = ((sourceCardId == 1) ? game.getResourceCard1() : game.getResourceCard2());
				game.addTrade(gameResourceDeckCard.getTrade());
				game.addDefense(gameResourceDeckCard.getDefense());
				game.addFaith(gameResourceDeckCard.getFaith());
				game = nextPhase(gameId);
				break;

			case DURING_RESOURCE_SELECT:
				if (sourceCardId == CommonConstants.GAME_RESOURCE_TRADE) {
					player.addTrade(game.getTrade());
					game.setTrade(0);
				}
				if (sourceCardId == CommonConstants.GAME_RESOURCE_DEFENSE) {
					player.addDefense(game.getDefense());
					game.setDefense(0);
				}
				if (sourceCardId == CommonConstants.GAME_RESOURCE_FAITH) {
					player.addFaith(game.getFaith());
					game.setFaith(0);
				}
				game = nextPhase(gameId);
				break;

			default:
				throw new UnsupportedPhaseException();

		}

		return game;

	}

	public void deleteGame(long gameId) {

		gameSingleton.deleteGame(gameId);
		Game game = gameDao.findGameById(gameId);
		gameDao.deleteGame(game);

	}

	public void drawInitialHand(Player player, Game game) throws PlayerNotInGameException {

		player = game.selectPlayer(player);
		for (int i = 0; i < 5; i++) {
			LOGGER.info(String.format("Player: %s, player.getHand(): %s, player.getDeck(): %s", player, player.getPlayerHand(), player.getplayerDeck()));
			player.getPlayerHand().addCard(new PlayerHandCard(player.getplayerDeck().pop(), player));
		}
		game.setGameState(GameState.INITIALIZING_JOINER_HAND);

	}
	
	public Game loadDump(Game game) {
		
		String json = game.getDataDump();
		if ( json != null ) {
			game = Game.fromJson(json);
			LOGGER.info(String.format("Loaded game %s from JSON dump", game.getId()));
		}
		
		for ( Player player : game.getPlayers() ) {
			
			DeckTemplate deckTemplate = player.getDeckTemplate();
			deckTemplate = accountDao.findDeckTemplateById(deckTemplate.getId());
			player.setDeckTemplate(deckTemplate);
			player.setGame(game);
			if ( game.getActivePlayer() != null && game.getActivePlayer().getId() == player.getId() ) {
				game.setActivePlayer(player);
			}
			LOGGER.info(String.format("Loaded deck template %s", deckTemplate));
			
		}
		LOGGER.info("game.getResourceCard2() " + game.getResourceCard2());
		LOGGER.info("game.getPlayers().get(0).getAccount() " + game.getPlayers().get(0).getAccount());
		
		return game;
		
	}

	public Game getGame(long gameId) {

		// Try to get the get from the memory cache first
		Game game = gameSingleton.getGame(gameId);
		
		// If game is not cached, load it from database
		if ( game == null ) {
			game = gameDao.findGameById(gameId);
			game = loadDump(game);
		}
		LOGGER.info("game.getResourceCard2() " + game.getResourceCard2());
		LOGGER.info("game.getPlayers().get(0).getAccount() " + game.getPlayers().get(0).getAccount());
		
		gameSingleton.addGame(game);
		
		return game;

	}

	public List<Game> getAllGames() {
		return gameDao.findAllGames();
	}

	public Player selectPlayer(Game game, String facebookUserId) throws PlayerNotInGameException {

		LOGGER.info("game.getPlayers().get(0).getAccount() " + game.getPlayers().get(0).getAccount());
		LOGGER.info("game.getPlayers().get(0).getAccount().getFacebookUserId() " + game.getPlayers().get(0).getAccount().getFacebookUserId());
		for (Player player : game.getPlayers()) {
			if (player.getAccount().getFacebookUserId().equals(facebookUserId))
				return player;
		}

		throw new PlayerNotInGameException();

	}

}
