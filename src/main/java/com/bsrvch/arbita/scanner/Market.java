package com.bsrvch.arbita.scanner;

import com.bsrvch.arbita.dto.crypto.Coin;
import com.bsrvch.arbita.dto.crypto.Coins;
import com.bsrvch.arbita.model.dictionary.MarketName;
import lombok.Getter;
import lombok.Setter;
import org.java_websocket.client.WebSocketClient;

@Getter
@Setter
public class Market {

    public MarketName name = null;
    public String key = null;
    public String secret = null;
    public WebSocketClient webSocketClient = null;
    public Thread thread = null;
    public Coins coins = new Coins();
    public void addCoins(Coin coin){
        this.coins.put(coin.getName(), coin);
    }
    public void addToAllCoins(Coins coins){
        for(Coin coin:coins.values()){
            Scanner.allCoin.computeIfAbsent(coin.getName(), k -> coin.getName());
        }
    }
    public void initNetworks() {

    }


    public void initCoins() {

    }

}
