package com.bsrvch.arbita.scanner.markets;

import com.bsrvch.arbita.dto.crypto.Coin;
import com.bsrvch.arbita.dto.crypto.Network;
import com.bsrvch.arbita.dto.crypto.TradPair;
import com.bsrvch.arbita.dto.crypto.TradPairs;
import com.bsrvch.arbita.scanner.Market;
import com.bsrvch.arbita.util.web.WebUtil;
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
public class BybitMarket extends Market {
    Runnable initNet = () -> {
        try {
            String data="";
            String time = new JSONObject(simpleResponse("https://api-testnet.bybit.com/v3/public/time")).getJSONObject("result").get("timeNano").toString().substring(0,13);
            String signature = sign(getSecret(),time+getKey()+"50000");
            Map<String,String> headers = new HashMap<>();
            headers.put("X-BAPI-API-KEY", getKey());
            headers.put("X-BAPI-TIMESTAMP", time);
            headers.put("X-BAPI-RECV-WINDOW", "50000");
            headers.put("X-BAPI-SIGN", signature);
            JSONArray bybit_data = new JSONObject(getJSON("https://api.bybit.com/asset/v3/private/coin-info/query",data,headers)).getJSONObject("result").getJSONArray("rows");
            List<String> pairs = new ArrayList<>();
            List<String> coins = new ArrayList<>();
            for(int i = 0; i < bybit_data.length(); i++){
                JSONObject object = bybit_data.getJSONObject(i);
                JSONArray json_networks = object.getJSONArray("chains");
                HashMap<String,Network> networks = new HashMap<>();
                for(int j = 0; j < json_networks.length(); j++){
                    JSONObject network = json_networks.getJSONObject(j);
                    String name = network.getString("chain");
                    float fee;
                    int confirm;
                    try {
                        fee = network.getFloat("withdrawFee");
                    }catch (Exception ex){
                        try {
                            fee = network.getInt("withdrawFee");
                        }catch (Exception e){
                            fee = 0f;
                        }
                    }
                    try {
                        confirm = network.getInt("withdrawFee");
                    }catch (Exception ex){
                        confirm = 0;
                    }
                    networks.put(name,new Network(name, fee, confirm));
                }
                addCoins(new Coin(object.getString("coin"),object.getString("name"),networks));
                coins.add(object.getString("coin"));
            }
            addToAllCoins(getCoins());
            JSONArray bybitCoins = new JSONObject(simpleResponse("https://api.bybit.com/v5/market/tickers?category=spot")).getJSONObject("result").getJSONArray("list");
            for(Object obj:bybitCoins){
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
            log.info("Size of coin: "+String.valueOf(allCoin.size()));
            log.info("Size of pair: "+String.valueOf(allPair.size()));
            //initCoins();//?????????????????????

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
            }//{ "op": "ping"}
            final long[] time = {0};
            try{
                webSocketClient = new WebSocketClient(new URI( "wss://stream.bybit.com/v5/public/spot" )) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {

                        try {
                            JSONArray bybitCoins = new JSONObject(simpleResponse("https://api.bybit.com/v5/market/tickers?category=spot")).getJSONObject("result").getJSONArray("list");
                            for(Object o:bybitCoins){
                                JSONObject coin = (JSONObject)o;
                                String name = coin.getString("symbol");
                                this.send(String.valueOf(new JSONObject().put("req_id","test").put("op","subscribe").put("args",new JSONArray().put("orderbook.1."+name))));
                            }
                            log.info("Bybit open connection");
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                    }

                    @Override
                    public void onMessage(String res) {
                        if(System.currentTimeMillis()/1000-150> time[0]){
                            this.send("{ \"op\": \"ping\"}");
                            time[0] = System.currentTimeMillis()/1000;
                        }
                        if(new JSONObject(res).has("data")){
                            JSONObject coin = new JSONObject(res).getJSONObject("data");
                            String name = coin.getString("s");
                            TradPairs pairs = allPair.get(name);
                            if(pairs!=null){
                                TradPair pair = pairs.get(getName().getString());
                                if(pair!=null){
                                    JSONArray b = coin.getJSONArray("b");
                                    JSONArray a = coin.getJSONArray("a");
                                    if(b.length()>0){
                                        b = b.getJSONArray(0);
                                        pair.setBidPrice(Float.parseFloat(b.get(0).toString()));
                                        pair.setBitSize(Float.parseFloat(b.get(1).toString()));
                                    }
                                    if(a.length()>0){
                                        a = a.getJSONArray(0);
                                        pair.setAskPrice(Float.parseFloat(a.get(0).toString()));
                                        pair.setAskSize(Float.parseFloat(a.get(1).toString()));
                                    }
                                }
                            }
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
                        log.error("Bybit closed connection");
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


    Runnable initCoin = new Runnable() {
        public void run() {
            while(true){
                JSONArray bybitCoins = null;
                try {
                    bybitCoins = new JSONObject(simpleResponse("https://api.bybit.com/v5/market/tickers?category=spot")).getJSONObject("result").getJSONArray("list");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for(Object obj:bybitCoins){
                    JSONObject coin = (JSONObject)obj;
                    String name = coin.getString("symbol");
                    TradPairs pairs = allPair.get(name);
                    if(pairs==null) continue;
                    TradPair pair = pairs.get(getName().getString());
                    if(pair==null) continue;
                    pair.setBidPrice(coin.getFloat("bid1Price"));
                    pair.setAskPrice(coin.getFloat("ask1Price"));
                    pair.setBitSize(coin.getFloat("bid1Size"));
                    pair.setAskSize(coin.getFloat("ask1Size"));
                }
            }
        }
    };
    //wss://stream.bybit.com/v5/public/spot
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
