package com.bsrvch.arbita.model.dictionary;

import com.bsrvch.arbita.scanner.Market;
import com.bsrvch.arbita.scanner.markets.BinanceMarket;
import com.bsrvch.arbita.scanner.markets.BybitMarket;
import com.bsrvch.arbita.scanner.markets.GateioMarket;
import com.bsrvch.arbita.scanner.markets.HuobiMarket;

public enum MarketName {
    BINANCE("binance", new BinanceMarket(),true),
    BYBIT("bybit", new BybitMarket(),true),
    HUOBI("huobi", new HuobiMarket(),false),
    GATEIO("gateio", new GateioMarket(),false);
    private final String string;
    private final Market market;
    private final Boolean free;

    MarketName(String string, Market market, Boolean free){
        this.string = string;
        this.market = market;
        this.free = free;
    }

    public String getString() {
        return string;
    }
    public Market getMarket() {
        return market;
    }
    public boolean getFree() {
        return free;
    }
}
