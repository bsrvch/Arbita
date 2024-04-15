package com.bsrvch.arbita.dto.crypto;

import com.bsrvch.arbita.model.dictionary.MarketName;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Bundle {
    String pair;
    MarketName buyName;
    float buyPrice;
    MarketName sellName;
    float sellPrice;
    float profit;

    String network;
    int confirms;
    String time;
    float spread;

    int buyVolume;
    int sellVolume;


    public String getS_profit() {
        return String.format("%.2f", profit);
    }

    public String getS_spread() {
        return String.format("%.2f %%", spread);
    }

    public void setBundle(Bundle bundle){
        setPair(bundle.getPair());
        setBuyName(bundle.getBuyName());
        setBuyPrice(bundle.getBuyPrice());
        setSellName(bundle.getSellName());
        setSellPrice(bundle.getSellPrice());
        setProfit(bundle.getProfit());
        setNetwork(bundle.getNetwork());
        setTime(bundle.getTime());
        setConfirms(bundle.getConfirms());
        setSpread(bundle.getSpread());
    }

}

