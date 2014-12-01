package com.guntzergames.medievalwipeout.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.managers.AccountManager;
import com.guntzergames.medievalwipeout.managers.GameManager;

@Stateless
@Path("/account")
public class AccountResource {

	@EJB
	private GameManager gameManager;
	@EJB
	private AccountManager accountManager;
	
	@GET
	@Path("get/{facebookUserId}")
    @Produces("text/plain")
	public String joinGame(@PathParam("facebookUserId") String facebookUserId) throws PlayerNotInGameException {
		Account account = accountManager.getAccount(facebookUserId, false);
		System.out.println(String.format("Account: %s", account));
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
	
}