package com.revature.merlinserver.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.revature.merlinserver.beans.MagicalUser;
import com.revature.merlinserver.beans.PrivateUserInfo;
import com.revature.merlinserver.dao.MagicalUserDao;
import com.revature.merlinserver.dao.PrivateInfoDao;

/**
 * All REST calls relating to a wizard sending and retrieving data.
 * @author Alex
 */
@Path("/wizard")
public class Wizard {
	
	/**
	 * A wizard wanting to view all unverified adept accounts
	 * Return the private info of all adepts
	 */
	@GET
	@Path("/unverified_adepts")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PrivateUserInfo> getUnverifiedAdepts() {
		PrivateInfoDao privateInfoDao = new PrivateInfoDao();
		List<PrivateUserInfo> unverifiedAdepts = null;
		
		privateInfoDao.open();
		unverifiedAdepts = privateInfoDao.getAllUnverifiedAdepts();
		privateInfoDao.close();
		
		return unverifiedAdepts;
	}
	
	/**
	 * A wizard approving an adept account.
	 * This method will set the adept's status from 'PENDING' to 'ACTIVE'
	 */
	@PUT
	@Path("/verify_adept")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String approveAdept(MagicalUser user) {
		MagicalUserDao magicDao = new MagicalUserDao();
		PrivateInfoDao privateInfoDao = new PrivateInfoDao();
		MagicalUser adept = null;
		String username = user.getUsername();
		
		magicDao.open();
		adept = magicDao.getMagicalUserByUsername(username);
		privateInfoDao.setSession(magicDao.getSession());
		privateInfoDao.approveAdept(adept);
		magicDao.close();
		
		return username + " has been approved";
	}
}