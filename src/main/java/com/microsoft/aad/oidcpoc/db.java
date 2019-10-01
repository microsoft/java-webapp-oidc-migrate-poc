package com.microsoft.aad.oidcpoc;

import java.io.IOException;
import org.apache.log4j.Logger;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.DriverManager;

public class db {
    String hostName;
    String dbName;
    String user;
    String password;
    String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);
    Connection connection = null;
    
    private static Logger logger = Logger.getLogger(db.class);

    public db(String host, String db, String msiClientId) throws Exception {
    	hostName = host;
    	dbName = db;
        url = String.format("jdbc:sqlserver://%s:1433;database=%s;authentication=ActiveDirectoryMSI;msiClientId=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, msiClientId);
        try {
			connection = DriverManager.getConnection(url);
            logger.info("Connecting DB via MSI in connection string");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public db(String host, String db, String msiEndpoint, String msiSecret, String msiClientId) throws Exception {
    	hostName = host;
    	dbName = db;
        url = String.format("jdbc:sqlserver://%s:1433;database=%s;authentication=NotSpecified;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, msiClientId);
        Properties info = new Properties();
        String value = getAccessToken(msiEndpoint, msiSecret);
        info.setProperty("accessToken", value);
        
        try {
			connection = DriverManager.getConnection(url, info);
            logger.info("Connecting DB via MSI access token in connection property");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public db(String host, String db, String usr, String pw) throws Exception {
    	hostName = host;
    	dbName = db;
    	user = usr;
    	password = pw;
        url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);
        try {
			connection = DriverManager.getConnection(url);
            logger.info("Connecting DB via sql username and password");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private String getAccessToken(String msiEndpoint, String msiSecret) throws IOException, JSONException {
    	
    	msiEndpoint += "?resource=https://vault.azure.net&api-version=2017-09-01";
    	URL url = new URL(msiEndpoint);
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    	connection.setRequestProperty("secret", msiSecret);
    	connection.setRequestMethod("GET");
    	connection.connect();
    	int responseCode = connection.getResponseCode();
    	
    	String response = HttpClientHelper.getResponseStringFromConn(connection, (responseCode==200));
    	JSONObject res = new JSONObject(response);
    	return res.getString("access_token");
    }

    public DBUser LinkAccounts(int UserId, String uniqueId) throws SQLException {
		//user hasn't had their AAD and DB accounts linked
    	//NOTE: as we are linking, we want to disable the user from logging in using local
    	//credentials in the future - so we will randomize their password now.
    	String rndpw = generateRandomHexToken(20).substring(0, 19);
		String updsql = "UPDATE [User] SET UniqueId = ?, Password = ? WHERE UserId = ?";
		PreparedStatement pstmt = connection.prepareStatement(updsql);
        pstmt.setString(1, uniqueId);
        pstmt.setString(2, rndpw);
        pstmt.setInt(3, UserId);
        pstmt.executeUpdate();
        return GetUser(uniqueId);
    }
    
    private static String generateRandomHexToken(int byteLength) {
    	//https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string#44227131
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[byteLength];
        secureRandom.nextBytes(token);
        return new BigInteger(1, token).toString(16); //hex encoding
    }

	public DBUser GetUser(String uniqueId) {
		DBUser res = null;
        try {
            String selectSql = "SELECT * FROM [User] WHERE UniqueId = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            pstmt.setString(1, uniqueId);

        	ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
            	//we have a match
        		res = new DBUser();
        		res.UserId = resultSet.getInt("UserId");
        		res.Email = resultSet.getNString("Email");
        		res.FName = resultSet.getNString("FName");
        		res.LName = resultSet.getNString("LName");
        		res.UniqueId = resultSet.getNString("UniqueId");
            }
    		if (resultSet.next()) {
    			//sanity check - we had more than one match
    			res = null;
    		}
      		connection.close();
	    }
	    catch (Exception e) {
	            e.printStackTrace();
	    }
		return res;
	}

	public DBUser ValidateUser(String userName, String password) {
		DBUser res = null;
        try {
            String selectSql = "SELECT * FROM [User] WHERE Email = ? AND Password = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
            	//we have a match
        		res = new DBUser();
        		res.UserId = resultSet.getInt("UserId");
        		res.Email = resultSet.getNString("Email");
        		res.FName = resultSet.getNString("FName");
        		res.LName = resultSet.getNString("LName");
        		res.UniqueId = resultSet.getNString("UniqueId");
            }
    		if (resultSet.next()) {
    			//we had more than one match
    			res = null;
    		}
    	    
    		connection.close();
	    }
	    catch (Exception e) {
	            e.printStackTrace();
	    }
		return res;
    }
}