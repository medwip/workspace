package com.guntzergames.medievalwipeout.managers;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard.PlayerType;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard;
import com.guntzergames.medievalwipeout.beans.PlayerHand;
import com.guntzergames.medievalwipeout.beans.PlayerHandCard;
import com.guntzergames.medievalwipeout.beans.ResourceDeck;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.GameState;
import com.guntzergames.medievalwipeout.enums.Phase;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
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
		
		for ( Game currentGame : gameSingleton.getAllOngoingGames() ) {
			if ( currentGame.getGameState() == GameState.WAITING_FOR_JOINER ) {
				if ( !currentGame.getCreator().getAccount().getFacebookUserId().equals(player.getAccount().getFacebookUserId()) ) {
					ret = currentGame;
				}
			}
		}
		
		return ret;
		
	}
	
	public Game joinGame(Player player) throws PlayerNotInGameException {
		
		Game game = findGameToJoin(player);
		
		if ( game == null ) {
			game = new Game();
			game.setId((long) (Math.random() * 10000));
			game.setCreator(player);
			game.setGameState(GameState.WAITING_FOR_JOINER);
			gameSingleton.addGame(game);
		}
		else {
			game.setJoiner(player);
			game.setGameState(GameState.INITIALIZING_CREATOR_HAND);
			initializeGame(game);
			game.setGameState(GameState.STARTED);
		}
		
		return game;
		
	}
	
	public void initializeGame(Game game) throws PlayerNotInGameException {
		
		game.getCreator().setPlayerDeck(game.getCreator().getDeckTemplate().toDeck());
		game.getJoiner().setPlayerDeck(game.getJoiner().getDeckTemplate().toDeck());
		
		initPlayer(game.getCreator());
		initPlayer(game.getJoiner());
		
		drawInitialHand(game.getCreator(), game);
		drawInitialHand(game.getJoiner(), game);
		
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
		game.setPhase(Phase.BEFORE_CREATOR_RESOURCE_DRAW);
		nextPhase(game.getId());
		
	}
	
	public void initPlayer(Player player) {
		player.setDefense(5);
		player.setCurrentDefense(player.getDefense());
		player.setGold(10);
		player.setTrade(5);
		player.setLifePoints(100);
	}
	
	public void resolveBeforeCreatorTurn(Game game) {
		
		game.setPhase(Phase.DURING_CREATOR_PLAY);
		
	}
	
	public void resolveBeforeJoinerTurn(Game game) {
		
		game.setPhase(Phase.DURING_JOINER_PLAY);
		
	}
	
	public Game nextPhase(long gameId) {
		
		Game game = getGame(gameId);
		
		System.out.println(String.format("nextPhase : %s", game));
		
		switch (game.getPhase()) {
		
			case BEFORE_CREATOR_RESOURCE_DRAW:
				resolveBeforeResourceDraw(game.getCreator());
				game.setResourceCard1(game.getResourceDeck().pop());
				game.getResourceCard1().setId(1);
				game.setResourceCard2(game.getResourceDeck().pop());
				game.getResourceCard2().setId(2);
				game.setPhase(Phase.DURING_CREATOR_RESOURCE_DRAW);
				break;
				
			case DURING_CREATOR_RESOURCE_DRAW:
				game.setPhase(Phase.AFTER_CREATOR_RESOURCE_DRAW);
				nextPhase(gameId);
				break;
				
			case AFTER_CREATOR_RESOURCE_DRAW:
				game.setPhase(Phase.BEFORE_CREATOR_DECK_DRAW);
				nextPhase(gameId);
				break;
				
			case BEFORE_CREATOR_DECK_DRAW:
				drawPlayerDeck(game.getCreator());
				game.setPhase(Phase.DURING_CREATOR_DECK_DRAW);
				break;
				
			case DURING_CREATOR_DECK_DRAW:
				game.setPhase(Phase.AFTER_CREATOR_DECK_DRAW);
				nextPhase(gameId);
				break;
				
			case AFTER_CREATOR_DECK_DRAW:
				game.setPhase(Phase.BEFORE_CREATOR_PLAY);
				nextPhase(gameId);
				break;
				
			case BEFORE_CREATOR_PLAY:
				game.setPhase(Phase.DURING_CREATOR_PLAY);
				break;
				
			case DURING_CREATOR_PLAY:
				game.setPhase(Phase.AFTER_CREATOR_PLAY);
				nextPhase(gameId);
				break;
				
			case AFTER_CREATOR_PLAY:
				game.setPhase(Phase.BEFORE_JOINER_RESOURCE_DRAW);
				nextPhase(gameId);
				break;
				
			case BEFORE_JOINER_RESOURCE_DRAW:
				resolveBeforeResourceDraw(game.getJoiner());
				game.setResourceCard1(game.getResourceDeck().pop());
				game.getResourceCard1().setId(1);
				game.setResourceCard2(game.getResourceDeck().pop());
				game.getResourceCard2().setId(2);
				game.setPhase(Phase.DURING_JOINER_RESOURCE_DRAW);
				break;
				
			case DURING_JOINER_RESOURCE_DRAW:
				game.setPhase(Phase.AFTER_JOINER_RESOURCE_DRAW);
				nextPhase(gameId);
				break;
				
			case AFTER_JOINER_RESOURCE_DRAW:
				game.setPhase(Phase.BEFORE_JOINER_DECK_DRAW);
				nextPhase(gameId);
				break;
				
			case BEFORE_JOINER_DECK_DRAW:
				drawPlayerDeck(game.getJoiner());
				game.setPhase(Phase.DURING_JOINER_DECK_DRAW);
				break;
				
			case DURING_JOINER_DECK_DRAW:
				game.setPhase(Phase.AFTER_JOINER_DECK_DRAW);
				nextPhase(gameId);
				break;
				
			case AFTER_JOINER_DECK_DRAW:
				game.setPhase(Phase.BEFORE_JOINER_PLAY);
				nextPhase(gameId);
				break;
				
			case BEFORE_JOINER_PLAY:
				game.setPhase(Phase.DURING_JOINER_PLAY);
				break;
				
			case DURING_JOINER_PLAY:
				game.setPhase(Phase.AFTER_JOINER_PLAY);
				nextPhase(gameId);
				break;
				
			case AFTER_JOINER_PLAY:
				game.setPhase(Phase.BEFORE_CREATOR_RESOURCE_DRAW);
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
	
	public Game playCard(String userName, long gameId, String destinationLayout, int cardId) throws PlayerNotInGameException {
		
		Game game = getGame(gameId);
		Player player = playerManager.selectPlayer(game, userName);
		Player opponent = game.selectOpponent(player);
		PlayerHand hand = player.getPlayerHand();
		
		switch ( game.getPhase() ) {
			
			case DURING_CREATOR_PLAY:
			case DURING_JOINER_PLAY:
				System.out.println(String.format("cardId = %d", cardId));
				
				GameEventPlayCard playerEvent = new GameEventPlayCard(PlayerType.PLAYER);
				
				// Play card from hand
				if ( destinationLayout.equals("playerField") ) {
					PlayerHandCard card = hand.getCards().get(cardId);
					playerEvent.setSource(card);
					playerEvent.setSourceIndex(cardId);
					playerEvent.setDestination(new PlayerFieldCard(card));
					playerEvent.setDestinationIndex(player.getPlayerField().getLastIndex());
					player.setGold(player.getGold() - card.getGoldCost());
					player.getPlayerField().addCard(new PlayerFieldCard(card));
					hand.getCards().remove((int)cardId);
					player.updatePlayableHandCards();
				}
				
				// Play card from field
				if ( destinationLayout.equals("opponentField") ) {
					PlayerFieldCard card = player.getPlayerField().getCards().get(cardId);
					playerEvent.setSource(new PlayerFieldCard(card));
					playerEvent.setSourceIndex(cardId);
					playerEvent.setDestination(new PlayerFieldCard(card));
					playerEvent.setDestinationIndex(cardId);
					card.setPlayed(true);
					if ( opponent.getCurrentDefense() > card.getAttack() ) {
						opponent.removeCurrentDefense(card.getAttack());
					}
					else {
						opponent.removeLifePoints(card.getAttack() - opponent.getCurrentDefense());
						opponent.setCurrentDefense(0);
					}
				}
				
				player.getEvents().add(playerEvent);
				opponent.getEvents().add(playerEvent.duplicate());
				System.out.println(String.format("Event added: %s", playerEvent.toString()));
				
				break;
				
			case DURING_CREATOR_DECK_DRAW:
			case DURING_JOINER_DECK_DRAW:
				PlayerDeckCard playerDeckCard = ((cardId == 1) ? player.getPlayerDeckCard1() : player.getPlayerDeckCard2());
				player.getPlayerHand().getCards().add(new PlayerHandCard(playerDeckCard, player));
				game = nextPhase(gameId);
				break;
				
			case DURING_CREATOR_RESOURCE_DRAW:
			case DURING_JOINER_RESOURCE_DRAW:
				ResourceDeckCard resourceDeckCard = ((cardId == 1) ? game.getResourceCard1() : game.getResourceCard2());
				player.addTrade(resourceDeckCard.getTrade());
				player.addDefense(resourceDeckCard.getDefense());
				player.addFaith(resourceDeckCard.getFaith());
				player.updatePlayableHandCards();
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
		for ( int i = 0; i < 5; i++ ) {
			System.out.println(String.format("Player: %s, player.getHand(): %s, player.getDeck(): %s", player, player.getPlayerHand(), player.getplayerDeck()));
			player.getPlayerHand().addCard(new PlayerHandCard(player.getplayerDeck().pop(), player));
		}
		game.setGameState(GameState.INITIALIZING_JOINER_HAND);
		
	}
	
	public Game getGame(long gameId) {
		
		for ( Game game : gameSingleton.getAllOngoingGames() ) {
			if ( game.getId() == gameId ) {
				return game;
			}
		}
		
		return null;
		
	}
	
	public List<Game> getAllOngoingGames() {
		return gameSingleton.getAllOngoingGames();
	}
	
}