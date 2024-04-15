package com.bsrvch.arbita.scanner.markets;

import com.bsrvch.arbita.dto.crypto.Coin;
import com.bsrvch.arbita.dto.crypto.Network;
import com.bsrvch.arbita.dto.crypto.TradPair;
import com.bsrvch.arbita.dto.crypto.TradPairs;
import com.bsrvch.arbita.scanner.Market;
import com.bsrvch.arbita.util.web.WebUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static com.bsrvch.arbita.scanner.Scanner.*;
import static com.bsrvch.arbita.util.web.WebUtil.*;


@Slf4j
public class BinanceMarket extends Market {
    Runnable initNet = () -> {
        try {
            String time = new JSONObject(simpleResponse("https://api1.binance.com/api/v3/time")).get("serverTime").toString();
            String data = "&recvWindow=50000&timestamp=" + time;
            String signature = sign(secret,data);
            data+="&signature="+signature;
            Map<String, String> headers = new HashMap<>();
            headers.put("X-MBX-APIKEY",key);
            JSONArray binance_data = new JSONArray(getJSON("https://api.binance.com/sapi/v1/capital/config/getall?",data,headers));
            List<String> coins = new ArrayList<>();
            for(int i = 0; i < binance_data.length(); i++){
                JSONObject object = binance_data.getJSONObject(i);
                if(object.getBoolean("trading") && object.getBoolean("withdrawAllEnable") && object.getBoolean("depositAllEnable")){
                    JSONArray json_networks = object.getJSONArray("networkList");
                    HashMap<String, Network> networks = new HashMap<>();
                    for(int j = 0; j < json_networks.length(); j++){
                        JSONObject jsonNetwork = json_networks.getJSONObject(j);
                        Network network = new Network(jsonNetwork.getString("network"), jsonNetwork.getFloat("withdrawFee"), jsonNetwork.getInt("minConfirm"));
                        allNetwork.putIfAbsent(network.getName(), network);
                        networks.put(network.getName(),network);
                    }
                    addCoins(new Coin(object.getString("coin"),object.getString("name"),networks));
                    coins.add(object.getString("coin"));
                }
            }
            addToAllCoins(getCoins());
            List<String> pairs = new ArrayList<>();
            JSONArray binanceCoins = new JSONArray(simpleResponse("https://api.binance.com/api/v3/ticker/bookTicker"));
            for(Object obj:binanceCoins){
                JSONObject coin = (JSONObject)obj;
                pairs.add(coin.getString("symbol"));
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
            binanceCoins = null;
            try {
                binanceCoins = new JSONArray(simpleResponse("https://api.binance.com/api/v3/ticker/bookTicker"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for(Object obj:binanceCoins){
                JSONObject coin = (JSONObject)obj;
                String name = coin.getString("symbol");
                TradPairs pairs1 = allPair.get(name);
                if(pairs1==null) continue;
                TradPair pair = pairs1.get(getName().getString());
                if(pair==null) continue;
                pair.setBidPrice(coin.getFloat("bidPrice"));
                pair.setAskPrice(coin.getFloat("askPrice"));
                pair.setBitSize(coin.getFloat("bidQty"));
                pair.setAskSize(coin.getFloat("askQty"));
            }
            log.info("Size of coin: "+String.valueOf(allCoin.size()));
            log.info("Size of pair: "+String.valueOf(allPair.size()));
        } catch (Exception e) {
            log.error(e.toString());
        }
    };

    Runnable initSocket  = new Runnable() {
        @Override
        public void run() {
            if(webSocketClient!=null){
                if(webSocketClient.isOpen()){
                    webSocketClient.close();
                }
            }
            try{
                webSocketClient = new WebSocketClient(new URI( "wss://stream.binance.com:9443/stream" )) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        this.send(String.valueOf(new JSONObject().put("method","SUBSCRIBE").put("params",new JSONArray().put("!ticker@arr")).put("id", 4254)));
                        log.info("Binance open connection");
                    }

                    @Override
                    public void onMessage(String res) {
                        if(new JSONObject(res).has("stream")){
                            JSONArray binanceCoins = new JSONObject(res).getJSONArray("data");
                            for(Object obj:binanceCoins){
                                JSONObject coin = (JSONObject)obj;
                                String name = coin.getString("s");
                                TradPairs pairs = allPair.get(name);
                                if(pairs==null) continue;
                                TradPair pair = pairs.get(getName().getString());
                                if(pair==null) continue;
                                if(coin.has("b")){
                                    pair.setBidPrice(coin.getFloat("b"));
                                }
                                if(coin.has("a")){
                                    pair.setAskPrice(coin.getFloat("a"));
                                }
                                if(coin.has("B")){
                                    pair.setBitSize(coin.getFloat("B"));
                                }
                                if(coin.has("A")){
                                    pair.setAskSize(coin.getFloat("A"));
                                }
                            }
                        }
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
                        log.error(res);
                    }

                    @Override
                    public void onClose(int i, String s, boolean b) {
                        log.error( "Binance closed connection" );
                        this.connect();
                    }

                    @Override
                    public void onError(Exception e) {
                        log.error(e.toString());
                    }
                };
                webSocketClient.connect();
            }catch (Exception e){
                log.error(e.toString());
                initCoins();
            }
        }
    };


    Runnable initCoin = new Runnable() {
        public void run() {
            while (true){
                JSONArray binanceCoins = null;
                try {
                    binanceCoins = new JSONArray(simpleResponse("https://api.binance.com/api/v3/ticker/bookTicker"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for(Object obj:binanceCoins){
                    JSONObject coin = (JSONObject)obj;
                    String name = coin.getString("symbol");
                    TradPairs pairs = allPair.get(name);
                    if(pairs==null) continue;
                    TradPair pair = pairs.get(getName().getString());
                    if(pair==null) continue;
                    pair.setBidPrice(coin.getFloat("bidPrice"));
                    pair.setAskPrice(coin.getFloat("askPrice"));
                    pair.setBitSize(coin.getFloat("bidQty"));
                    pair.setAskSize(coin.getFloat("askQty"));
                }
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
