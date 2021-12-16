package com.sampleproject.obs.hts.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.sampleproject.obs.hts.request.TokenBean;

import io.github.cdimascio.dotenv.Dotenv;

@Service(value = "TokenHelperService")
public class TokenHelperService {
	
	public Client client = null;

	public String netWork = "";
	
	private static final Logger logger = LoggerFactory.getLogger(TokenHelperService.class);

	public TokenHelperService() {
		Properties properties = new Properties();
		InputStream inputStream = TokenHelperService.class.getClassLoader().getResourceAsStream( "application.properties" );
		if ( Objects.nonNull(inputStream)) {
			try {
				properties.load( inputStream );
			} catch (IOException e) {
				logger.info( e.getMessage());
			}
		}
		if (properties.getProperty( "network" ).equalsIgnoreCase( "mainnet" )) {
			client = Client.forMainnet();
			netWork = "mainnet";
		} else if (properties.getProperty( "network" ).equalsIgnoreCase( "testnet" )) {
			client = Client.forTestnet();
			netWork = "testnet";
		}

	}

	private Dotenv getEnv() {
		if ( netWork.equalsIgnoreCase( "mainnet" ) ) {
			return Dotenv.configure().directory( "src/main/mainnet-env" ).filename( ".env" ).load();
		} else if ( netWork.equalsIgnoreCase( "testnet" ) ) {
			return Dotenv.configure().directory( "src/main/resources" ).filename( "env.properties" ).load();
		}
		return null;
	}

	public Client getAdminClient() {
		client.setOperator( getAdminOperatorId(), getAdminOperatorKey() );
		return client;
	}

	public AccountId getAdminOperatorId() {
		return AccountId.fromString( Objects.requireNonNull( getEnv().get( "ADMIN_OPERATOR_ID" ) ) );
	}

	public PrivateKey getAdminOperatorKey() {
		return PrivateKey.fromString( Objects.requireNonNull( getEnv().get( "ADMIN_OPERATOR_KEY" ) ) );
	}

	public Client getTreasuryClient() {
		client.setOperator( getTreasuryOperatorId(), getTreasuryOperatorKey() );
		return client;
	}

	public AccountId getTreasuryOperatorId() {
		return AccountId.fromString( Objects.requireNonNull( getEnv().get( "TREASURY_OPERATOR_ID" ) ) );
	}
	
	public String getTreasuryOperatorIdString() {
		return ( Objects.requireNonNull( getEnv().get( "TREASURY_OPERATOR_ID" ) ) );
	}

	public PrivateKey getTreasuryOperatorKey() {
		return PrivateKey.fromString( Objects.requireNonNull( getEnv().get( "TREASURY_OPERATOR_KEY" ) ) );
	}
	
	public String getTreasuryOperatorKeyString() {
		return ( Objects.requireNonNull( getEnv().get( "TREASURY_OPERATOR_KEY" ) ) );
	}
	
	public AccountId getGridOperatorId() {
		return AccountId.fromString( Objects.requireNonNull( getEnv().get( "OPERATOR_ID_OPERATOR" ) ) );
	}

	public PrivateKey getGridOperatorKey() {
		return PrivateKey.fromString( Objects.requireNonNull( getEnv().get( "OPERATOR_KEY_OPERATOR" ) ) );
	}

	public Client getCustomClient( String operatorId ) {
		client.setOperator( getCustomOperatorId( operatorId ), getCustomOperatorKey( operatorId ) );
		return client;
	}

	public AccountId getCustomOperatorId( String operatorId ) {
		return AccountId.fromString( operatorId );
	}

	public PrivateKey getCustomOperatorKey( String operatorId ) {
		return PrivateKey.fromString( operatorId );
	}

	public AccountId getCustomFromOperatorId( TokenBean token ) {
		return AccountId.fromString( token.getFromSenderId() );
	}

	public PrivateKey getCustomFromOperatorKey( TokenBean token ) {
		return PrivateKey.fromString( token.getFromSenderKey() );
	}

	public AccountId getCustomToOperatorId( TokenBean token ) {
		return AccountId.fromString( token.getToAccountId() );
	}

	public PrivateKey getCustomToOperatorKey( TokenBean token ) {
		return PrivateKey.fromString( token.getToAccountKey() );
	}

}
