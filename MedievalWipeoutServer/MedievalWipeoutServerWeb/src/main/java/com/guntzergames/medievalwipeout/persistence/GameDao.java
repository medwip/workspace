package com.guntzergames.medievalwipeout.persistence;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.enums.GameState;

@Stateless
public class GameDao {

	private final static Logger LOGGER = Logger.getLogger(GameDao.class);
	
	@PersistenceContext(unitName = "mwpu")
    private EntityManager em;
	
	@EJB
	private AccountDao accountDao;
	
	public Game saveGame(Game game) {
		
		game.setDataDump(game.toJson());
		return mergeGame(game);
		
	}
	
	public Game mergeGame(Game game) {
		
		for ( Player player : game.getPlayers() ) {
			LOGGER.info(String.format("Just before merge, player in game: %s", player));
			if ( game.getActivePlayer() != null && game.getActivePlayer().getId() == player.getId() ) {
				game.setActivePlayer(player);
			}
		}
		
		game = em.merge(game);
		em.flush();
		return game;
		
	}
	
	public Player mergePlayer(Player player) {
		
		LOGGER.info(String.format("Merging player %s", player));
		return em.merge(player);
		
	}
	
	public DeckTemplate findDeckTemplateById(long deckId) {
		
		return em.find(DeckTemplate.class, deckId);
		
	}
	
	public List<DeckTemplate> findDeckTemplatesByAccount(Account account) {
		
		return em.createNamedQuery(DeckTemplate.NQ_FIND_BY_ACCOUNT, DeckTemplate.class)
				.setParameter("account", account)
				.getResultList();
		
	}
	
	public List<Game> findGamesByGameState(GameState gameState) {
		
		return em.createNamedQuery(Game.NQ_FIND_BY_GAME_STATE, Game.class)
				.setParameter("gameState", gameState)
				.getResultList();
		
	}
	
	public List<Game> findAllGames() {
		
		return em.createNamedQuery(Game.NQ_FIND_ALL, Game.class)
				.getResultList();
		
	}
	
	public Game findGameById(long gameId) {
		
		return em.find(Game.class, gameId);
		
	}
	
	public void deleteGame(Game game) {
		
		em.remove(game);
		
	}
	
}
