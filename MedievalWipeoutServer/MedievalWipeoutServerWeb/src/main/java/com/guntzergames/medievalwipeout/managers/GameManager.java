package com.guntzergames.medievalwipeout.managers;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard;
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
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.interfaces.CommonConstants;
import com.guntzergames.medievalwipeout.persistence.GameDao;
import com.guntzergames.medievalwipeout.singletons.GameSingleton;

@Stateless
public class GameManager {

	@EJB
	private GameSingleton gameSingleton;
	@EJB
	private PlayerManager playerManager;
	@EJB
	private GameDao gameDao;

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

	public Game joinGame(Player player) throws PlayerNotInGameException {

		Game game = findGameToJoin(player);

		if (game == null) {
			game = new Game();
			game.setId((long) (Math.random() * 10000));
			game.addPlayer(player);
			game.setActivePlayer(player);
			game.setGameState(GameState.WAITING_FOR_JOINER);
			gameSingleton.addGame(game);
		} else {
			game.addPlayer(player);
			game.setGameState(GameState.INITIALIZING_CREATOR_HAND);
			initializeGame(game);
			game.setGameState(GameState.STARTED);
		}

		return game;

	}

	public void initializeGame(Game game) throws PlayerNotInGameException {

		for (Player player : game.getPlayers()) {
			player.setPlayerDeck(player.getDeckTemplate().toDeck());
			initPlayer(player);
			drawInitialHand(player, game);
		}

		ResourceDeck resourceDeck = game.getResourceDeck();
		resourceDeck.addCard(new ResourceDeckCard(1, 0, 0));
		resourceDeck.addCard(new ResourceDeckCard(1, 0, 0));
		resourceDeck.addCard(new ResourceDeckCard(1, 0, 0));
		resourceDeck.addCard(new ResourceDeckCard(1, 0, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 1, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 1, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 1, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 1, 0));
		resourceDeck.addCard(new ResourceDeckCard(0, 0, 1));
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

		System.out.println(String.format("nextPhase : %s", game));

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

		System.out.println("After nextPhase : " + game);

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

	public Game playCard(String userName, long gameId, String sourceLayout, int sourceCardId, String destinationLayout, int destinationCardId) throws PlayerNotInGameException {

		Game game = getGame(gameId);
		Player player = playerManager.selectPlayer(game, userName);
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
					} else {
						playerField = player.getPlayerFieldDefense();
						fieldCard.setLocation(Location.DEFENSE);
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
					
					if ( destinationLayout.equals("opponentFieldDefense") ) {
						playerEvent.setDestination(new PlayerFieldCard(sourceCard));
						playerEvent.setDestinationIndex(sourceCardId);
						if (opponent.getCurrentDefense() > sourceCard.getAttack()) {
							opponent.removeCurrentDefense(sourceCard.getAttack());
						} else {
							opponent.removeLifePoints(sourceCard.getAttack() - opponent.getCurrentDefense());
							opponent.setCurrentDefense(0);
						}
					}
					
					if ( destinationLayout.startsWith("opponentCard") ) {
						PlayerFieldCard destinationCard = null;
						if ( destinationLayout.endsWith("Attack") ) {
							destinationCard = opponent.getPlayerFieldAttack().getCards().get(destinationCardId);
						}
						if ( destinationLayout.endsWith("Defense") ) {
							destinationCard = opponent.getPlayerFieldDefense().getCards().get(destinationCardId);							
						}
						if ( destinationCard.getCurrentLifePoints() > sourceCard.getAttack() ) {
							destinationCard.removeCurrentLifePoints(sourceCard.getAttack());
						}
						else {
							opponent.removeLifePoints(sourceCard.getAttack() - opponent.getCurrentDefense());
							destinationCard.setCurrentLifePoints(0);
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
				break;

		}

		return game;

	}

	public void deleteGame(long gameId) {

		gameSingleton.deleteGame(gameId);

	}

	public void drawInitialHand(Player player, Game game) throws PlayerNotInGameException {

		player = game.selectPlayer(player);
		for (int i = 0; i < 5; i++) {
			System.out.println(String.format("Player: %s, player.getHand(): %s, player.getDeck(): %s", player, player.getPlayerHand(), player.getplayerDeck()));
			player.getPlayerHand().addCard(new PlayerHandCard(player.getplayerDeck().pop(), player));
		}
		game.setGameState(GameState.INITIALIZING_JOINER_HAND);

	}

	public Game getGame(long gameId) {

		for (Game game : gameSingleton.getAllOngoingGames()) {
			if (game.getId() == gameId) {
				return game;
			}
		}

		return null;

	}

	public List<Game> getAllOngoingGames() {
		return gameSingleton.getAllOngoingGames();
	}

}
