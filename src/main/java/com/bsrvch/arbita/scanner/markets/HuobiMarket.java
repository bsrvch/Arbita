package com.bsrvch.arbita.scanner.markets;

import com.bsrvch.arbita.dto.crypto.Coin;
import com.bsrvch.arbita.dto.crypto.Network;
import com.bsrvch.arbita.dto.crypto.TradPair;
import com.bsrvch.arbita.dto.crypto.TradPairs;
import com.bsrvch.arbita.scanner.Market;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static com.bsrvch.arbita.scanner.Scanner.allCoin;
import static com.bsrvch.arbita.scanner.Scanner.allPair;
import static com.bsrvch.arbita.util.web.WebUtil.*;

@Slf4j
public class HuobiMarket extends Market {
    Runnable initNet = () -> {
        try {
            JSONArray huobi_data = new JSONArray(new JSONObject(simpleResponse("https://api.huobi.pro/v2/reference/currencies")).getJSONArray("data"));
            List<String> pairs = new ArrayList<>();
            List<String> coins = new ArrayList<>();
            for(int i = 0; i < huobi_data.length(); i++){
                JSONObject object = huobi_data.getJSONObject(i);
                JSONArray json_networks = object.getJSONArray("chains");
                HashMap<String, Network> networks = new HashMap<>();
                for (Object obj:json_networks){
                    JSONObject network = (JSONObject) obj;
                    if(network.has("depositStatus") && network.has("withdrawStatus")){
                        if(Objects.equals(network.getString("depositStatus"), "allowed") && Objects.equals(network.getString("withdrawStatus"),"allowed") && Objects.equals(network.getString("withdrawFeeType"),"fixed")){
                            String name = "";
                            if(network.has("baseChain")){
                                name = network.getString("baseChain").toUpperCase();
                            }else if(network.has("displayName")){
                                name = network.getString("displayName").toUpperCase();
                            }
                            else continue;
                            if(name.equals("SOLANA")) name = "SOL";
                            networks.put(name,new Network(name, network.getFloat("transactFeeWithdraw"),network.getInt("numOfFastConfirmations")));
                        }
                    }
                }
                addCoins(new Coin(object.getString("currency").toUpperCase(),"",networks));
                coins.add(object.getString("currency").toUpperCase());
            }
            addToAllCoins(getCoins());
            JSONArray huobiCoins = new JSONArray(new JSONObject(simpleResponse("https://api.huobi.pro/market/tickers")).getJSONArray("data"));
            for(Object obj:huobiCoins){
                JSONObject coin = (JSONObject)obj;
                pairs.add(coin.getString("symbol").toUpperCase());
            }
            for(String pair:pairs) {
                for(String coin:coins){
                    if(pair.indexOf(coin)==0){
                        String p = pair.replace(coin,"");
                        for(String c:coins){
                            if(p.equals(c)){
                                if(allPair.get(pair)==null){
                                    allPair.put(pair,new TradPairs());
                                    allPair.get(pair).put(getName().getString(),new TradPair(pair,getCoins().get(coin),p,getName()));
                                }
                                else{
                                    allPair.get(pair).put(getName().getString(),new TradPair(pair,getCoins().get(coin),p,getName()));
                                }
                            }
                        }
                    }
                }
            }
            log.info("Size of coin: "+String.valueOf(allCoin.size()));
            log.info("Size of pair: "+String.valueOf(allPair.size()));

        }catch (Exception e) {
            initNetworks();
        }
    };

    Runnable initSocket = new Runnable() {
        @Override
        public void run() {
            if(webSocketClient!=null){
                if(webSocketClient.isOpen()){
                    webSocketClient.close();
                }
            }
            try{
                webSocketClient = new WebSocketClient(new URI( "wss://api.huobi.pro/ws" )) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        try {
                            JSONArray huobiCoins = new JSONArray(new JSONObject(simpleResponse("https://api.huobi.pro/v1/common/symbols")).getJSONArray("data"));
                            for(Object o:huobiCoins){
                                JSONObject pair = (JSONObject) o;
                                this.send(String.valueOf(new JSONObject().put("sub","market."+pair.getString("symbol")+".ticker")));
                            }
                            log.info("Huobi open connection");
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                    }

                    @Override
                    public void onMessage(String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onMessage(ByteBuffer bytes) {
                        String res = "";
                        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes.array());
                        byte[] readBuffer = new byte[4096];
                        try{
                            GZIPInputStream inputStream = new GZIPInputStream(arrayInputStream);
                            int read = inputStream.read(readBuffer, 0, readBuffer.length);
                            inputStream.close();
                            byte[] result = Arrays.copyOf(readBuffer, read);
                            res = new String(result, "UTF-8");
                        }catch (Exception e){log.error(e.toString());}
                        if(new JSONObject(res).has("ping")){
                            this.send(String.valueOf(new JSONObject().append("pong",new JSONObject(res).getInt("ping"))));
                        } else if (new JSONObject(res).has("tick")) {
                            JSONObject coin = new JSONObject(res);
                            String name = coin.getString("ch").replace("market.","").replace(".ticker","").toUpperCase();
                            TradPairs pairs = allPair.get(name);
                            if(pairs!=null){
                                TradPair pair = pairs.get(getName().getString());
                                if(pair!=null){
                                    JSONObject tick = coin.getJSONObject("tick");
                                    pair.setBidPrice(tick.getFloat("bid"));
                                    pair.setAskPrice(tick.getFloat("ask"));
                                    pair.setBitSize(tick.getFloat("bidSize"));
                                    pair.setAskSize(tick.getFloat("askSize"));
                                }
                            }
                        }
                    }

                    @Override
                    public void onClose(int i, String s, boolean b) {
                        log.error("Huobi closed connection");
                        initCoins();
                    }

                    @Override
                    public void onError(Exception e) {
                        log.error(e.toString());
                    }
                };
                webSocketClient.connect();
            }catch (Exception e){log.error(e.toString());}
        }
    };







    Runnable initCoin = () -> {
        while (true){
            JSONArray huobiCoins = null;
            try {
                huobiCoins = new JSONArray(new JSONObject(getJSON("https://api.huobi.pro/market/tickers","",null)).getJSONArray("data"));
            } catch (Exception e) {
                log.error(e.toString());
                try {
                    Thread.sleep(2000);
                } catch (Exception e2) {
                    throw new RuntimeException(e);
                }
            }
            if(huobiCoins!=null){
                for(Object obj:huobiCoins){
                    JSONObject coin = (JSONObject)obj;
                    String name = coin.getString("symbol").toUpperCase();
                    TradPairs pairs = allPair.get(name);
                    if(pairs==null) continue;
                    TradPair pair = pairs.get(getName().getString());
                    if(pair==null) continue;
                    pair.setBidPrice(coin.getFloat("bid"));
                    pair.setAskPrice(coin.getFloat("ask"));
                    pair.setBitSize(coin.getFloat("bidSize"));
                    pair.setAskSize(coin.getFloat("askSize"));
                }
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };


    @Override
    public void initNetworks() {
        new Thread(initNet).start();
        initCoins();
    }

    @Override
    public void initCoins() {
        if(thread!=null){
            if(thread.isAlive()){
                thread.stop();
            }
        }
        thread = new Thread(initSocket);
        thread.start();
    }
}
