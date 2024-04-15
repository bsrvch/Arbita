package com.bsrvch.arbita.dto.crypto;



import com.bsrvch.arbita.model.dictionary.MarketName;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
public class TradPair {
    private String name;
    private Coin coin;
    private String pair;
    private MarketName market;
    private float bidPrice;
    private float askPrice;
    private float bitSize;
    private float askSize;

    public TradPair(String name, Coin coin, String pair, MarketName market) {
        this.name = name;
        this.coin = coin;
        this.pair = pair;
        this.market = market;
    }

    public TradPair(String name, float bidPrice, float askPrice, float bitSize, float askSize) {
        this.name = name;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
        this.bitSize = bitSize;
        this.askSize = askSize;
    }
    public static final Comparator<TradPair> COMPARE_BY_BID_PRICE = new Comparator<TradPair>() {
        @Override
        public int compare(TradPair lhs, TradPair rhs) {
            if(lhs.getBidPrice()>rhs.getBidPrice()) return -1;
            else if(lhs.getBidPrice()==rhs.getBidPrice()) return 0;
            else return 1;
        }
    };
    public static final Comparator<TradPair> COMPARE_BY_ASK_PRICE = new Comparator<TradPair>() {
        @Override
        public int compare(TradPair lhs, TradPair rhs) {
            if(lhs.getAskPrice()>rhs.getAskPrice()) return 1;
            else if(lhs.getAskPrice()==rhs.getAskPrice()) return 0;
            else return -1;
        }
    };

}

