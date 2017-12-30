package com.revature.merlinserver.dao;

import org.hibernate.Session;

import com.revature.merlinserver.beans.PrivateUserInfo;
import com.revature.merlinserver.beans.PublicUserInfo;

/*
 * Dao for accessing public user information
 * @author Evan
 * 
 */
public class PublicInfoDao extends MerlinSessionDao<PublicUserInfo> {
	/**
	 * No-args constructor
	 */
	public PublicInfoDao() {
		// do nothing
	}

	/**
	 * Assigns a session to this dao
	 * @param session - session used to perform queries/transactions 
	 */
	public PublicInfoDao(Session session) {
		super(session);
	}
	
	public void insert(PublicUserInfo pui) {
		if(isReady()) {
			session.save(pui);
		}
	}
	
	public void deletePublicInfoByObject(PublicUserInfo pui) {
		if(isReady()) {
			session.delete(pui);
		}
	}
}
