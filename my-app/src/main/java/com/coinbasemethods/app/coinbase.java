package com.coinbasemethods.app;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.User;
import com.coinbase.api.entity.TransfersResponse;
import com.coinbase.api.entity.Transfer;
import com.coinbase.api.entity.PaymentMethod;
import com.coinbase.api.entity.PaymentMethodsResponse;
import com.coinbase.api.entity.OAuthTokensResponse;
import org.joda.money.Money;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.UUID;

import java.util.List;
import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class coinbase{

  public static String getCoinBaseAuthFirstTime(String users_code_coinbase, String user_id){
    System.out.println("getCoinBaseAuthFirstTime");
    JSONObject jsonCoinBaseAuth=null;
    String coinBaseAuth=getAccessTokenCoinBase(users_code_coinbase);
    if (coinBaseAuth.equals("") || coinBaseAuth.contains("expired_token")){
      return "ERROR - Getting access token returned: "+coinBaseAuth ;
    }else{
      jsonCoinBaseAuth = new JSONObject(coinBaseAuth);
      //UPDATE DATABASE HERE
      String access_token_coinbase=jsonCoinBaseAuth.getString("access_token");
      String refresh_token_coinbase=jsonCoinBaseAuth.getString("refresh_token");
      return "coinbase_authorization_success";
    }
  }

  public static String refreshAccessToken(String users_refresh_token_coinbase){
    String AJSONStr="";
    System.out.println("refreshAccessToken");
    try{
      //Exchange code for an access token
      HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/oauth/token?grant_type=refresh_token&client_id="+"YOUR_CLIENT_ID"+"&client_secret="+"YOUR_SECRET"+"&refresh_token="+users_refresh_token_coinbase).openConnection();
      con.setRequestMethod("POST");
      InputStream inputStream;
      int status = con.getResponseCode();
      if (status >= 400) {
        inputStream = con.getErrorStream();
        System.out.println("HTTP Status Of Error: "+status);
      } else {
        inputStream = con.getInputStream();
      }
      BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
      String decodedString;
      while ((decodedString = in.readLine()) != null) {
        System.out.println(decodedString);
        AJSONStr=AJSONStr+decodedString;
      }
      in.close();
    }catch(Exception e){
      System.out.println("Exception exchanging code for refresh access token E= "+e);
    }
    return AJSONStr;
  }

public static String coinBaseSendMoneyToOtherCoinbaseAccount(String users_access_token_coinbase,
                                                      String transfer_to_ethereum_address,
                                                      String users_wallet_id,
                                                      String amount,
                                                      String user_id){
  String returnedStringFromBuy="";
  String AJSONStr="";
  try{
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/v2/accounts/"+users_wallet_id+"/transactions").openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty ("Authorization", "Bearer "+users_access_token_coinbase);
    con.setRequestProperty ("CB-VERSION", "YOUR_CB_VERSION");
    con.setRequestProperty ("Content-Type", "application/json");
    con.setDoOutput(true);
    String randomIdemString=UUID.randomUUID().toString();
    String buyOrderJsonStr = "{\"type\": \"send\", \"to\": \""+transfer_to_ethereum_address+"\", \"amount\": \""+amount+"\", \"currency\":\""+"YOUR_CURRENCY"+"\", \"idem\":\""+randomIdemString+"\"}";
    OutputStream os = con.getOutputStream();
    os.write(buyOrderJsonStr.getBytes());
    os.flush();
    InputStream inputStream=null;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    if (inputStream!=null){
      BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
      String decodedString;
      while ((decodedString = in.readLine()) != null) {
        System.out.println(decodedString);
        AJSONStr=AJSONStr+decodedString;
      }
      in.close();
      return AJSONStr;
    }else{
      System.out.println("inputStream is null");
    }
    returnedStringFromBuy=AJSONStr;
  }catch(Exception e){
    System.out.println("Exception coinBaseSendMoneyEthereumToCoinFlash E= "+e);
  }
  return returnedStringFromBuy;
}

public static String coinBaseQuoteEthereum(String users_access_token_coinbase,
                                    String users_wallet_id,
                                    String users_bank_account_id_coinbase,
                                    String total_amount_to_buy){
  String returnedStringFromBuy="";
  String AJSONStr="";
  try{
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/v2/accounts/"+users_wallet_id+"/buys").openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty ("Authorization", "Bearer "+users_access_token_coinbase);
    con.setRequestProperty ("CB-VERSION", "YOUR_CB_VERSION");
    con.setRequestProperty ("Content-Type", "application/json");
    con.setDoOutput(true);
    String buyOrderJsonStr = "{\"total\": \""+total_amount_to_buy+"\", \"currency\": \""+"YOUR_CURRENCY"+"\", \"payment_method\": \""+users_bank_account_id_coinbase+"\", \"agree_btc_amount_varies\":\"true\", \"quote\":\""+"true"+"\", \"commit\":\""+"false"+"\"}";
    OutputStream os = con.getOutputStream();
    os.write(buyOrderJsonStr.getBytes());
    os.flush();
    InputStream inputStream=null;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    if (inputStream!=null){
      BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
      String decodedString;
      while ((decodedString = in.readLine()) != null) {
        AJSONStr=AJSONStr+decodedString;
      }
      in.close();
      return AJSONStr;
    }else{
      System.out.println("inputStream is null");
    }
    returnedStringFromBuy=AJSONStr;
  }catch(Exception e){
    System.out.println("Exception coinBaseQuoteEthereum E= "+e);
  }
  return returnedStringFromBuy;
}

public static String coinBaseBuyEthereum(String users_access_token_coinbase,
                                  String users_wallet_id,
                                  String users_bank_account_id_coinbase,
                                  String total_amount_to_buy,
                                  String commit,
                                  String quote){
  String returnedStringFromBuy="";
  String AJSONStr="";
  try{
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/v2/accounts/"+users_wallet_id+"/buys").openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty ("Authorization", "Bearer "+users_access_token_coinbase);
    con.setRequestProperty ("CB-VERSION", "YOUR_CB_VERSION");
    con.setRequestProperty ("Content-Type", "application/json");
    //con.setRequestProperty ("{\"total\": \"1.00\", \"currency\": \"COINBASE_CURRENCY\", \"payment_method\": \"0caa4076-f3ed-5f82-84c3-00c1b87f3d1b\", \"agree_btc_amount_varies\":\"true\", \"commit\":\"false\"}");
    con.setDoOutput(true);
    //always do 10 because they'll only charge 99 cents for that transaction, they always charge .99 even for a 1 dollar transaction
    String buyOrderJsonStr = "{\"total\": \""+total_amount_to_buy+"\", \"currency\": \""+"YOUR_CURRENCY"+"\", \"payment_method\": \""+users_bank_account_id_coinbase+"\", \"agree_btc_amount_varies\":\"true\", \"quote\":\""+quote+"\", \"commit\":\""+commit+"\"}";
    OutputStream os = con.getOutputStream();
    os.write(buyOrderJsonStr.getBytes());
    os.flush();
    InputStream inputStream=null;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    if (inputStream!=null){
      BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
      String decodedString;
      while ((decodedString = in.readLine()) != null) {
        AJSONStr=AJSONStr+decodedString;
      }
      in.close();
      return AJSONStr;
    }else{
      System.out.println("inputStream is null");
    }
    returnedStringFromBuy=AJSONStr;
  }catch(Exception e){
    System.out.println("Exception coinBaseBuyEthereum E= "+e);
  }
  return returnedStringFromBuy;
}


public static String coinBaseListPaymentMethods(String users_access_token_coinbase){
  String AJSONStr="";
  try{
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/v2/payment-methods").openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty ("Authorization", "Bearer "+users_access_token_coinbase);
    con.setRequestProperty ("CB-VERSION", "YOUR_CB_VERSION");
    InputStream inputStream;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String decodedString;
    while ((decodedString = in.readLine()) != null) {
      AJSONStr=AJSONStr+decodedString;
    }
    in.close();
  }catch(Exception e){
    System.out.println("Exception coinBaseListPaymentMethods E= "+e);
  }
  return AJSONStr;
}

public static String coinBaseListWallets(String users_access_token_coinbase){
  System.out.println("coinBaseListWallets");
  String AJSONStr="";
  try{
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/v2/accounts/").openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty ("Authorization", "Bearer "+users_access_token_coinbase);
    con.setRequestProperty ("CB-VERSION", "YOUR_CB_VERSION");
    InputStream inputStream;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String decodedString;
    while ((decodedString = in.readLine()) != null) {
      AJSONStr=AJSONStr+decodedString;
    }
    in.close();
  }catch(Exception e){
    System.out.println("Exception coinBaseListWallets E= "+e);
  }
  return AJSONStr;
}

public static String coinBaseCheckUserAuth(String users_access_token_coinbase){
  String AJSONStr="";
  try{
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/v2/user/auth").openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty ("Authorization", "Bearer "+users_access_token_coinbase);
    con.setRequestProperty ("CB-VERSION", "YOUR_CB_VERSION");
    InputStream inputStream;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String decodedString;
    while ((decodedString = in.readLine()) != null) {
      System.out.println(decodedString);
      AJSONStr=AJSONStr+decodedString;
    }
    in.close();
  }catch(Exception e){
    System.out.println("Exception coinBaseCheckUserAuth E= "+e);
  }
  return AJSONStr;
}

public static String coinBaseUser(String users_access_token_coinbase){
  String AJSONStr="";
  try{
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/v2/user").openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty ("Authorization", "Bearer "+users_access_token_coinbase);
    con.setRequestProperty ("CB-VERSION", "YOUR_CB_VERSION");
    InputStream inputStream;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String decodedString;
    while ((decodedString = in.readLine()) != null) {
      System.out.println(decodedString);
      AJSONStr=AJSONStr+decodedString;
    }
    in.close();
  }catch(Exception e){
    System.out.println("Exception coinBaseUser E= "+e);
  }
  return AJSONStr;
}

public static String revokeAccessTokenCoinBase(String users_access_token_coinbase){
  System.out.println("revokeAccessTokenCoinBase");
  String AJSONStr="";
  try{
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/oauth/revoke?token="+users_access_token_coinbase).openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty ("Authorization", "Bearer "+users_access_token_coinbase);
    con.setRequestProperty ("CB-VERSION", "YOUR_CB_VERSION");
    InputStream inputStream;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String decodedString;
    while ((decodedString = in.readLine()) != null) {
      System.out.println(decodedString);
      AJSONStr=AJSONStr+decodedString;
    }
    in.close();
  }catch(Exception e){
    System.out.println("Exception revokeAccessTokenCoinBase E= "+e);
  }
  return AJSONStr;
}


public static String getAccessTokenCoinBase(String users_code_coinbase){
  System.out.println("getAccessTokenCoinBase");
  String AJSONStr="";
  try{
    //Exchange code for an access token
    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coinbase.com/oauth/token?grant_type=authorization_code&code="+users_code_coinbase+"&client_id="+"YOUR_CLIENT_ID"+"&client_secret="+"YOUR_SECRET"+"&redirect_uri="+"YOUR REDIRECT").openConnection();
    con.setRequestMethod("POST");
    InputStream inputStream=null;
    int status = con.getResponseCode();
    if (status >= 400) {
      inputStream = con.getErrorStream();
      System.out.println("HTTP Status Of Error: "+status);
    } else {
      inputStream = con.getInputStream();
    }
    if (inputStream!=null){
      BufferedReader in = new BufferedReader(
      new InputStreamReader(
      inputStream));
      String decodedString;
      while ((decodedString = in.readLine()) != null) {
        System.out.println(decodedString);
        AJSONStr=AJSONStr+decodedString;
      }
      in.close();
    }else{
      System.out.println("inputStream is null in getAccessTokenCoinBase");
    }
  }catch(Exception e){
    System.out.println("Exception getAccessTokenCoinBase E= "+e);
  }
  return AJSONStr;
}
}
