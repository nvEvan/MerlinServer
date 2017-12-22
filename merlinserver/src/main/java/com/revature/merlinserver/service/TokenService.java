package com.revature.merlinserver.service;

import java.util.UUID;

import com.revature.merlinserver.beans.MagicalUser;
import com.revature.merlinserver.beans.Token;
import com.revature.merlinserver.dao.TokenDao;

public class TokenService {

	/**
	 * 
	 * @param user
	 * @return
	 */
	public static Token createToken(MagicalUser user) {

		String tokenstr = UUID.randomUUID().toString();
		TokenDao td = new TokenDao();
		
		while (td.isTokenUnique(user, tokenstr) == false) {
			tokenstr = UUID.randomUUID().toString();
		}
		
		Token token = new Token(user, tokenstr, null);
		
		return token;
	}
}