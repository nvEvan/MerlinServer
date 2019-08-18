package com.revature.merlinserver.rest;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.revature.merlinserver.beans.CodeList;
import com.revature.merlinserver.beans.MagicalFileUpload;
import com.revature.merlinserver.beans.MagicalUser;
import com.revature.merlinserver.beans.PrivateUserInfo;
import com.revature.merlinserver.beans.Token;
import com.revature.merlinserver.dao.MagicalFileDao;
import com.revature.merlinserver.dao.MagicalUserDao;
import com.revature.merlinserver.dao.PrivateInfoDao;
import com.revature.merlinserver.dao.TokenDao;
import com.revature.merlinserver.paramwrapper.RegisterAdeptParams;
import com.revature.merlinserver.paramwrapper.RegisterApprenticeParams;
import com.revature.merlinserver.service.TokenService;
import com.revature.merlinserver.service.UserVerificationService;

/**
 * Class holding REST calls from the front end.
 * Methods including registering a new user, logging in, etc
 * @author Alex
 */
@Path("/register")
public class Register {

	/**
	 * Rest call from angular to register a new apprentice.
	 * @param token
	 */
	@POST
	@Path("/apprentice") //this the path we want to use?
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String registerApprentice(final RegisterApprenticeParams params) {
		MagicalUser user = params.getUser();
		PrivateUserInfo pi = params.getPrivateUserInfo();
		CodeList pendingStatus = null;
		
		MagicalUserDao md = new MagicalUserDao();
		PrivateInfoDao pd = new PrivateInfoDao();
		
		md.open();
		pd.setSession(md.getSession());
		pendingStatus = pd.getStatusById(424);
		pi.setStatus(pendingStatus); //set the newly registered status to 'PENDING'
		md.insertUser(user); //insert the new user
		pi.setUser(user);
		pd.insert(pi); //insert the user's private info
		md.close();
		
		//Send verification email to user
		sendEmailToUser(user, pi);
		
		return "User has been registered, and awaiting verification";
	}

	/**
	 * Rest call from angular to register a new apprentice.
	 * @param token
	 */
	@POST
	@Path("/adept") //this the path we want to use?
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String registerAdept(final RegisterAdeptParams params) {
		MagicalUser user = params.getUser();
		PrivateUserInfo pi = params.getPrivateUserInfo();
		MagicalFileUpload magicalFileUpload = params.getMagicalFileUpload();
		CodeList pendingStatus = null;
		
		MagicalUserDao magicalUserDao = new MagicalUserDao();
		PrivateInfoDao privateInfoDao = new PrivateInfoDao();
		MagicalFileDao magicalFileDao = new MagicalFileDao();
		
		//open our session
		magicalUserDao.open();
		privateInfoDao.setSession(magicalUserDao.getSession());
		magicalFileDao.setSession(magicalUserDao.getSession());
		
		pendingStatus = privateInfoDao.getStatusById(424);
		pi.setStatus(pendingStatus); //set the newly registered status to 'PENDING'
		magicalUserDao.insertUser(user); //insert the new user
		magicalFileDao.insertFile(magicalFileUpload);
		pi.setUser(user);
		privateInfoDao.insert(pi); //insert the user's private info
		magicalUserDao.close();
		
		//Send verification email to user
		sendEmailToUser(user, pi);
		
		return "User has been registered, and awaiting verification";
	}
	
	/**
	 * When a user clicks on their email verification link, this method will find the user associated with the provided token.
	 * The method checks that this token is new and unused token, and will assign the user the token and set the user's status to 'active'.
	 * @param token provided in the url
	 */
	@GET
	@Path("/authenticate/{token}")
	@Produces(MediaType.TEXT_PLAIN)
	public String authenticate(@PathParam("token") final String token) {
	    //Grab the user from the token
	    //If there is no user of the token, then someone entered a phony token in the url.
		MagicalUser user = TokenService.getUserByToken(token);
		
		//update the user's status to 'active' status
		if (UserVerificationService.userIsNew(user)) { //check the user is new and has not already been activated
			UserVerificationService.updateStatus(user); //update their status to 'active'
			TokenService.updateTokenByToken(token); //update their token to the current time
			return "Thank you for registering " + user.getUsername() + ", your account has been verified";
		} else {
			return "Your account has already been verified!";
		}
	}
	
	/**
	 * This method will check if a username already exists in the RDS.
	 * @param username
	 * @return true if the username is unique and does not exist in the RDS
	 */
	@GET
	@Path("/unique/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isUsernameUnique(@PathParam("username") final String username) {
		boolean isUnique = false;
		MagicalUserDao magicalUserDao = new MagicalUserDao();
		
		magicalUserDao.open();
		isUnique = magicalUserDao.getMagicalUserByUsername(username) == null; //check if this username exists
		magicalUserDao.close();
		
		return isUnique;
	}
	
	/*===================================== PRIVATE METHODS ===========================================*/
	/**
	 * This method sends the new user a verification email. This method assists the method register
	 * The email includes a brand new generated token used for verification.
	 */
	private void sendEmailToUser(MagicalUser user, PrivateUserInfo userinfo) {
		Token token = TokenService.createToken(user);
		TokenDao td = new TokenDao();
		
		td.open();
		td.insertToken(token); //store the new token
		td.close();

		try {
			//send the user the verification email
			UserVerificationService.sendVerification(userinfo.getEmail(), user.getUsername(), token.getToken());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}