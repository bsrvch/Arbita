package com.bsrvch.arbita.util.web;

import com.bsrvch.arbita.model.SubscriptionInfo;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.statics.locale.StaticLocale;
import com.mysql.cj.xdevapi.JsonArray;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@UtilityClass
@Slf4j
public class WebUtil {

    public String simpleResponse(String ur) throws IOException {
        URL url  =  new URL(ur);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        return result.toString();
    }
    public static void setHeaders(HttpURLConnection httpUrlConnection, Map<String, String> headers) {
        for (String headerKey : headers.keySet()) {
            httpUrlConnection.setRequestProperty(headerKey, headers.get(headerKey));
        }
    }
    public static String sign(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
    public static String sign512(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA512");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
    public String get_SHA_512_SecurePassword(String passwordToHash, String salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    public String getJSON(String url, String data, Map<String, String> headers) throws Exception {
        HttpsURLConnection c = null;
        try {
            URL u = new URL(url+data);
            c = (HttpsURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            if(headers!=null){
                setHeaders(c,headers);
            }
            c.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    public String getPaymentUrl(String shopId, String secretApi, String email, SubscriptionInfo info, String payInf){
        JSONObject jsonObject = new JSONObject();
        jsonObject
                .put("amount",
                        new JSONObject()
                                .put(
                                        "value", info.getCost()+".00"
                                ).put(
                                        "currency", info.getCurrency()
                                ))
                .put("description", payInf)
                .put("capture", true)
                .put("confirmation"
                        ,new JSONObject()
                                .put("type","redirect")
                                .put("return_url","https://t.me/arbitbi_bot?start=payment"))
                .put("receipt",
                        new JSONObject()
                                .put("customer",
                                        new JSONObject()
                                                .put("email",email))
                                .put("items",
                                        new JSONArray()
                                                .put(
                                                        new JSONObject()
                                                                .put("description",payInf)
                                                                .put("amount",
                                                                        new JSONObject()
                                                                                .put(
                                                                                        "value", info.getCost()+".00"
                                                                                ).put(
                                                                                        "currency", info.getCurrency()
                                                                                ))
                                                                .put("vat_code",1)
                                                                .put("quantity",1)))
                );
        return getYookassa(shopId, secretApi, jsonObject.toString());
    }

    public String getPaymentById(String shopId, String secretApi, String id){
        HttpClient client = HttpClient.newHttpClient();
        String credentials = shopId + ":" + secretApi;
        String auth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.yookassa.ru/v3/payments/"+id))
                .setHeader("Idempotence-Key", String.valueOf(UUID.randomUUID()))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", auth)
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("body" + response.body());
            return response.body();

        }catch (Exception e){
            log.error(e.toString());
            return "";
        }
    }


    public String getYookassa(String shopId, String secretApi,String data)  {
        HttpClient client = HttpClient.newHttpClient();
        String credentials = shopId + ":" + secretApi;
        String auth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.yookassa.ru/v3/payments"))
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .setHeader("Idempotence-Key", String.valueOf(UUID.randomUUID()))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", auth)
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        }catch (Exception e){
            log.error(e.toString());
            return "";
        }

    }
}
/*
{
  "id" : "2cba8b05-000f-5000-9000-189340aab9a4",
  "status" : "pending",
  "amount" : {
    "value" : "100.00",
    "currency" : "RUB"
  },
  "description" : "Заказ №1",
  "recipient" : {
    "account_id" : "262067",
    "gateway_id" : "2124833"
  },
  "created_at" : "2023-10-12T22:46:29.596Z",
  "confirmation" : {
    "type" : "redirect",
    "confirmation_url" : "https://yoomoney.ru/checkout/payments/v2/contract?orderId=2cba8b05-000f-5000-9000-189340aab9a4"
  },
  "test" : false,
  "paid" : false,
  "refundable" : false,
  "metadata" : { }
}
2023-10-13T01:46:29.292+03:00  INFO 17984 --- [legram Executor] com.bsrvch.arbita.telegram.Bot           : Reply sent to Alex

 */