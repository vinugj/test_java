package com.cryptix.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.cryptix.entity.Manager;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.cryptix.security.SecurityConstants.EXPIRATION_TIME;
import static com.cryptix.security.SecurityConstants.SECRET;
import static com.cryptix.security.SecurityConstants.HEADER_STRING;
import static com.cryptix.security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	
	AuthenticationManager authenticationManager;
	
	public JWTAuthenticationFilter (AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		try {
			Manager credentials = new ObjectMapper().readValue(request.getInputStream(), Manager.class);
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword(),new ArrayList<>()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

			String token = JWT.create().withSubject(((User)authResult.getPrincipal()).getUsername())
					.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					.sign(HMAC512(SECRET.getBytes()));
			
			response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
	}
	
	
	
	
}