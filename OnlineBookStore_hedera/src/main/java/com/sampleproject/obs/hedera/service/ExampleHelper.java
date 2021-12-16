package com.sampleproject.obs.hedera.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;

import io.github.cdimascio.dotenv.Dotenv;

public class ExampleHelper {
	
private ExampleHelper() { }
    

    private static Dotenv getEnv() {
    	
        // Load configuration from the environment or a $projectRoot/.env file, if present
        // See .env.sample for an example of what it is looking for

    	Properties properties = new Properties();
		InputStream inputStream = ExampleHelper.class.getClassLoader().getResourceAsStream( "application.properties" );
		if(inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	if(properties.getProperty( "network" ) .equalsIgnoreCase( "mainnet" ) ) {
    		
        return Dotenv.configure().directory( "src/main/mainnet-env" ).filename( ".env" ).load();
        }
    	else if(properties.getProperty( "network" ) .equalsIgnoreCase( "testnet" ) ) {
        	
        	return Dotenv.configure().directory( "src/main/testnet-env" ).filename( ".env" ).load();
        }
    	//return Dotenv.load();
		return null;
    }

    public static AccountId getNodeId() {
        return AccountId.fromString( Objects.requireNonNull( getEnv().get( "NODE_ID" ) ) );
    }

    public static AccountId getOperatorId(String operatorId) {
        return AccountId.fromString( operatorId );
    }

    public static PrivateKey getOperatorKey(String operatorKey) {
        return PrivateKey.fromString( operatorKey);
    }

    public static byte[] parseHex(String hex) {
        var len = hex.length();
        var data = new byte[len / 2];

        var i = 0;

        //noinspection NullableProblems
        for (var c : (Iterable<Integer>) hex.chars()::iterator) {
            if ((i % 2) == 0) {
                // high nibble
                data[i / 2] = (byte) (Character.digit(c, 16) << 4);
            } else {
                // low nibble
                data[i / 2] &= (byte) Character.digit(c, 16);
            }

            i++;
        }

        return data;
    }

    public static AccountId getOperatorIdGirdManager() {
		return AccountId.fromString(Objects.requireNonNull(getEnv().get( "OPERATOR_ID_MANAGER" )));
	}

    public static AccountId getOperatorIdOperator() {
		return AccountId.fromString(Objects.requireNonNull(getEnv().get( "OPERATOR_ID_OPERATOR" )));
	}

    public static PrivateKey getOperatorKeyGirdManager() {
        return PrivateKey.fromString(Objects.requireNonNull(getEnv().get( "OPERATOR_KEY_MANAGER" )));
    }
    public static PrivateKey getOperatorKeyOperator() {
        return PrivateKey.fromString(Objects.requireNonNull(getEnv().get( "OPERATOR_KEY_OPERATOR" )));
    }
    
   
    
    public static long getContractID() {
        return Long.parseLong(getEnv().get( "smartContractId" ));
    }

}
