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
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static com.bsrvch.arbita.scanner.Scanner.allCoin;
import static com.bsrvch.arbita.scanner.Scanner.allPair;
import static com.bsrvch.arbita.util.web.WebUtil.*;

@Slf4j
public class GateioMarket extends Market {
    Runnable initNet = () -> {
        try {
            String code = get_SHA_512_SecurePassword("","");
            String data = "";
            Long time = new JSONObject(simpleResponse("https://api.gateio.ws/api/v4/spot/time")).getLong("server_time")/1000;
            String s = "GET\n"+"/api/v4/wallet/withdraw_status\n"+"\n"+code+"\n"+time;
            String sign = sign512(getSecret(), s);
            Map<String,String> headers = new HashMap<>();
            headers.put("KEY", getKey());
            headers.put("Timestamp", String.valueOf(time));
            headers.put("SIGN", sign);
            JSONArray gateio_data = new JSONArray(getJSON("https://api.gateio.ws/api/v4/wallet/withdraw_status",data,headers));
            List<String> pairs = new ArrayList<>();
            List<String> coins = new ArrayList<>();
            for(int i = 0; i < gateio_data.length(); i++){
                JSONObject object = gateio_data.getJSONObject(i);
                if(object.has("withdraw_fix_on_chains")){
                    JSONObject json_networks = object.getJSONObject("withdraw_fix_on_chains");
                    HashMap<String,Network> networks = new HashMap<>();
                    for (Iterator<String> it = json_networks.keys(); it.hasNext(); ) {
                        String key = it.next();
                        networks.put(key,new Network(key, json_networks.getFloat(key), -1));
                    }
                    addCoins(new Coin(object.getString("currency"),object.getString("name"),networks));
                    coins.add(object.getString("currency"));
                }
            }
            addToAllCoins(getCoins());
            JSONArray gateioCoins = new JSONArray(simpleResponse("https://api.gateio.ws/api/v4/spot/tickers"));
            for(Object obj:gateioCoins){
                JSONObject coin = (JSONObject)obj;
                pairs.add(coin.getString("currency_pair"));
            }
            for(String pair:pairs) {
                for(String coin:coins){
                    if(pair.indexOf(coin)==0){
                        pair = pair.replace("_","");
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


        } catch (Exception e) {
            log.error(e.toString());
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
            final long[] time = {0};
            try{
                webSocketClient = new WebSocketClient(new URI( "wss://api.gateio.ws/ws/v4/" )) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        try {
                            JSONArray  gateioCoins = new JSONArray(simpleResponse("https://api.gateio.ws/api/v4/spot/tickers"));
                            JSONArray payloads = new JSONArray();
                            for(Object o:gateioCoins){
                                JSONObject coin = (JSONObject)o;
                                String name = coin.getString("currency_pair");
                                payloads.put(name);
                                this.send(String.valueOf(new JSONObject().put("time",System.currentTimeMillis()/1000).put("event","subscribe").put("channel","spot.book_ticker").put("payload",new JSONArray().put(name))));
                            }
                            log.info("Gateio open connection");
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                    }
//{"time" : 1697656840, "channel" : "spot.ping"}
                    @Override
                    public void onMessage(String res) {
                        try{
                            if(new JSONObject(res).getLong("time")-180> time[0]){
                                this.send("{\"time\" : "+System.currentTimeMillis()/1000+", \"channel\" : \"spot.ping\"}");
                                time[0] = System.currentTimeMillis()/1000;
                            }
                            if(new JSONObject(res).has("result")){
                                if(new JSONObject(res).get("result") instanceof JSONObject){
                                    JSONObject coin = new JSONObject(res).getJSONObject("result");
                                    if(coin.has("s")){
                                        String name = coin.getString("s").replace("_","");
                                        TradPairs pairs = allPair.get(name);
                                        if(pairs!=null){
                                            TradPair pair = pairs.get(getName().getString());
                                            if(pair!=null){
                                                if(coin.has("b")){
                                                    pair.setBidPrice(coin.getFloat("b"));
                                                    //pair.setBidPrice(Float.parseFloat(coin.getString("b")));
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
                                }
                            }
                        }catch (Exception e){
                            log.error(e.toString());
                        }

                    }

//                    @Override
//                    public void onMessage(ByteBuffer bytes) {
//                        String res = "";
//                        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes.array());
//                        byte[] readBuffer = new byte[4096];
//                        try{
//                            GZIPInputStream inputStream = new GZIPInputStream(arrayInputStream);
//                            int read = inputStream.read(readBuffer, 0, readBuffer.length);
//                            inputStream.close();
//                            byte[] result = Arrays.copyOf(readBuffer, read);
//                            res = new String(result, "UTF-8");
//                        }catch (Exception e){log.error(e.toString());}
//                        log.error(res);
//                    }

                    @Override
                    public void onClose(int i, String s, boolean b) {
                        log.error("Gateio closed connection");
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
        while(true){
            JSONArray gateioCoins = null;
            try {
                gateioCoins = new JSONArray(simpleResponse("https://api.gateio.ws/api/v4/spot/tickers"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for(Object obj:gateioCoins){
                JSONObject coin = (JSONObject)obj;
                String name = coin.getString("currency_pair").replace("_","");
                TradPairs pairs = allPair.get(name);
                if(pairs==null) continue;
                TradPair pair = pairs.get(getName().getString());
                if(pair==null) continue;
                float f = 0;
                try {
                    f = coin.getFloat("highest_bid");
                }catch (Exception ex){
                    try {
                        f = coin.getInt("highest_bid");
                    }catch (Exception e){
                        f = 0f;
                    }
                }
                pair.setBidPrice(f);
                try {
                    f = coin.getFloat("lowest_ask");
                }catch (Exception ex){
                    try {
                        f = coin.getInt("lowest_ask");
                    }catch (Exception e){
                        f = 0f;
                    }
                }
                pair.setAskPrice(f);
                try {
                    f = coin.getFloat("quote_volume");
                }catch (Exception ex){
                    try {
                        f = coin.getInt("quote_volume");
                    }catch (Exception e){
                        f = 0f;
                    }
                }
                pair.setBitSize(f);
                pair.setAskSize(f);
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
