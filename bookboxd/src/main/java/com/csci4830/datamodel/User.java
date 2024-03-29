package com.csci4830.datamodel;

import java.math.BigInteger;
import java.security.MessageDigest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.codec.digest.*;
/**
 * @since J2SE-1.8 CREATE TABLE user ( 
 * 			user_id INT NOT NULL AUTO_INCREMENT,
 * 			username VARCHAR(255) NOT NULL,
 * 			password VARCHAR(255) NOT NULL,
 * 			about_desc VARCHAR(255),
 * 			privacy_setting INT,
 * 			PRIMARY KEY (user_id)
 * 		  )
 */		  

@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Integer user_id;
	
	@Column(name="username")
	private String username;
	
	@Column(name="password")
	private String password;
	
	@Column(name="about_desc")
	private String about_desc;
	
	@Column(name="privacy_setting")
	private Integer privacy_setting;
	
	public static final String DEFAULT_DESC = "";
	public static final Integer DEFAULT_PRIVACY = 0;
	
	public User() {
		
	}
	
	public User(String username, String password) {
		super();
		this.username = username;
		setPassword(password);
		this.about_desc = DEFAULT_DESC;
		this.privacy_setting = DEFAULT_PRIVACY;
	}
	
	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = encryptSHA1(password);
	}

	public String getAbout_desc() {
		return about_desc;
	}

	public void setAbout_desc(String about_desc) {
		this.about_desc = about_desc;
	}

	public Integer getPrivacy_setting() {
		return privacy_setting;
	}

	public void setPrivacy_setting(Integer privacy_setting) {
		this.privacy_setting = privacy_setting;
	}

	
	private static String encryptSHA1(String input)
    {
		return DigestUtils.sha1Hex(input);
    }
}
