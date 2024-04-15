package com.bsrvch.arbita.scanner;


import com.bsrvch.arbita.component.InlineKeyboardMaker;
import com.bsrvch.arbita.dto.crypto.Bundle;
import com.bsrvch.arbita.dto.crypto.Network;
import com.bsrvch.arbita.dto.crypto.TradPair;
import com.bsrvch.arbita.dto.crypto.TradPairs;
import com.bsrvch.arbita.dto.interactiveHandler.ScannerDTO;
import com.bsrvch.arbita.initializer.BotInit;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.service.ScannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scanner {

    private final InlineKeyboardMaker inlineKeyboardMaker;

    public static List<Market> markets;


    public static Map<String, ScannerDTO> userInWork = new ConcurrentHashMap<>();

    public static Map<String, String> allCoin = new ConcurrentHashMap<String,String>();
    public static Map<String, Network> allNetwork = new ConcurrentHashMap<String,Network>();
    public static Map<String,Long> timesBundles = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, TradPairs> allPair = new ConcurrentHashMap<String,TradPairs>();
    public static Map<String, Bundle> bList = new ConcurrentHashMap<>();
    public static List<Bundle> bundlesList = Collections.synchronizedList(new ArrayList<>());
    public static Map<Long, List<String>> listenUser = new ConcurrentHashMap<>();
    public static Map<String, Bundle> listenBundle = new ConcurrentHashMap<>();
    public static float total = 1000;
    public static int minConfirms = 15;
    public static float minSpread = 0;
    public static float minSize = 1000;

    private final ScannerService scannerService;
    public Scanner initMarkets(){
        log.info("Scanner init");
        markets = scannerService.getMarkets();
        return this;
    }
    public Scanner initNetworks(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for(Market market : markets){
                    log.info("Init " + market.getName());
                    market.initNetworks();
                }
            }
        };
        new Thread(r).start();

        return this;
    }
//    public Scanner updateCoins(){
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                for(Market market : markets){
//                    market.initCoins();
//                }
//            }
//        };
//       new Thread(r).start();
//       return this;
//    }

    public void startChecker(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(true){
                    long time1 = System.currentTimeMillis();
                    List<Bundle> bundles = new ArrayList<>();
                    bList.clear();
                    for(TradPairs markets : allPair.values()){
                        if(markets.size()>1){
                            List<TradPair> askPair = new ArrayList<>(markets.values()).stream().sorted(TradPair.COMPARE_BY_ASK_PRICE).collect(Collectors.toList());
                            List<TradPair> bidPair = new ArrayList<>(markets.values()).stream().sorted(TradPair.COMPARE_BY_BID_PRICE).collect(Collectors.toList());
                            for (TradPair ask:askPair){
                                for (TradPair bid:bidPair){
                                    for (Network network: ask.getCoin().getNetworks().values()){
                                        if(bid.getCoin().getNetworks().get(network.getName())!=null && !ask.getMarket().getString().equals(bid.getMarket().getString())){
                                            float buy = 0;
                                            float sell = 0;
                                            float spread = 0;
                                            String fr_a = ask.getCoin().getName();
                                            String se_a = ask.getPair();
                                            String fr_b = bid.getCoin().getName();
                                            String se_b = bid.getPair();
                                            if(fr_a.equals(fr_b) && se_a.equals(se_b)){
//                                                if(!se_a.equals("USDT")){
//                                                    if(allPair.get(se_a+"USDT")!=null){
//                                                        TradPair supPair = allPair.get(se_a+"USDT").get(ask.getMarket().getString());
//                                                        TradPair supPair1 = allPair.get(se_a+"USDT").get(bid.getMarket().getString());
//                                                        if(supPair!=null && supPair1!=null){
//                                                            float a = ask.getAskPrice();
//                                                            float b = bid.getBidPrice();
//                                                            spread = (b-a)/a;
//                                                        }
//                                                    }
//                                                    float a = ask.getAskPrice();
//                                                    float b = bid.getBidPrice();
//                                                    spread = (b-a)/a;
//                                                }else {
//                                                    buy = total /ask.getAskPrice();
//                                                    sell = bid.getBidPrice();
//                                                    buy = buy-network.getFee();
//                                                    sell = sell*buy;
//                                                    spread = (sell/total - 1);
//                                                }
                                                String bundleName = ask.getName()+ask.getMarket().getString()+bid.getName()+bid.getMarket().getString()+network;//????????
                                                int conf = 0;
                                                conf = network.getConfirms();
                                                if(conf == -1){
                                                    conf = bid.getCoin().getNetworks().get(network.getName()).getConfirms();
                                                }
                                                float a = ask.getAskPrice();
                                                float b = bid.getBidPrice();
                                                spread  = (b/a)-1;
                                                String name = ask.getCoin().getName()+"/"+ask.getPair();
                                                if(listenBundle.containsKey(name+ask.getMarket()+bid.getMarket()+network.getName())){
                                                    listenBundle.remove(name+ask.getMarket()+bid.getMarket()+network.getName());
                                                    Bundle bnd = new Bundle(ask.getCoin().getName()+"/"+ask.getPair(),ask.getMarket(),ask.getAskPrice(),bid.getMarket(),bid.getBidPrice(),(float)(Math.floor(((spread+1)*total-total) * 100) / 100),network.getName(),conf,"", spread*100,(int)ask.getAskSize(),(int)bid.getBitSize());
                                                    listenBundle.put(name+ask.getMarket()+bid.getMarket()+network.getName(),bnd);
                                                }
//                                                long time = System.currentTimeMillis();
//                                                if(timesBundles.get(bundleName)!=null){
//                                                    time = time - timesBundles.get(bundleName);
//                                                }
//                                                else {
//                                                    timesBundles.put(bundleName,time);
//                                                    time = 0;
//                                                }
//                                                String t =  new SimpleDateFormat("mm:ss").format(time);
//
//                                                bundles.add(new Bundle(name,ask.getMarket(),ask.getAskPrice(),bid.getMarket(),bid.getBidPrice(),(float)(Math.floor(((spread+1)*total-total) * 100) / 100),network.getName(),conf,t, spread*100,(int)ask.getAskSize(),(int)bid.getBitSize()));
//                                                if(timesBundles.get(bundleName)!=null){
//                                                    timesBundles.remove(bundleName);
//                                                }
                                                if(spread < 1 && bid.getBidPrice()!=0 && ask.getAskPrice()!=0){
                                                //if(conf<minConfirms && spread*100>minSpread && spread < 100 && bid.getBidPrice()!=0 && ask.getAskPrice()!=0 && ask.getAskSize()>=minSize && bid.getBitSize()>=minSize){

                                                    long time = System.currentTimeMillis();
                                                    if(timesBundles.get(bundleName)!=null){
                                                        if(spread>0)
                                                            time = time - timesBundles.get(bundleName);
                                                        else timesBundles.remove(bundleName);
                                                    }
                                                    else {
                                                        timesBundles.put(bundleName,time);
                                                        time = 0;
                                                    }
                                                    String t =  new SimpleDateFormat("mm:ss").format(time);
                                                    //bList.put(name+ask.getMarket()+bid.getMarket()+network.getName(),new Bundle(name,ask.getMarket(),ask.getAskPrice(),bid.getMarket(),bid.getBidPrice(),(float)(Math.floor(((spread+1)*total-total) * 100) / 100),network.getName(),conf,t, spread*100,(int)ask.getAskSize(),(int)bid.getBitSize()));
                                                    bundles.add(new Bundle(name,ask.getMarket(),ask.getAskPrice(),bid.getMarket(),bid.getBidPrice(),(float)(Math.floor(((spread+1)*total-total) * 100) / 100),network.getName(),conf,t, spread*100,(int)ask.getAskSize(),(int)bid.getBitSize()));
                                                }else if(timesBundles.get(bundleName)!=null){
                                                    timesBundles.remove(bundleName);
                                                }
                                            }
                                        }
//                                        if(bid.getCoin().getNetworks().get(network.getName())!=null && ask.getCoin().getName().equals(bid.getCoin().getName())){
//                                            float buy = 0;
//                                            String h = "-";
//                                            if(!Objects.equals(ask.getPair(), "USDT")){
//                                                if(allPair.get(ask.getCoin().getName()+"USDT")!=null){
//                                                    TradPair supPair = allPair.get(ask.getCoin().getName()+"USDT").get(ask.getMarket());
//                                                    if(supPair!=null){
//                                                        buy = total /supPair.getAskPrice()/ask.getAskPrice();
//                                                        h = supPair.getCoin().getName()+" "+supPair.getPair() + " "+ ask.getCoin().getName() + " " + ask.getPair() + " "+ ask.getMarket();
//                                                    }
//                                                }else continue;
//                                            }else{
//                                                buy = total /ask.getAskPrice();
//                                            }
//                                            buy = total /ask.getAskPrice();//------
//                                            buy=buy-network.getFee();
//                                            float sell = 0;
//                                            if(!Objects.equals(bid.getPair(), "USDT")){
//                                                TradPair supPair = allPair.get(bid.getCoin().getName()+"USDT").get(bid.getMarket());
//                                                if(supPair!=null){
//                                                    sell = (buy * bid.getBidPrice())*supPair.getBidPrice();
//                                                    h += "-" +supPair.getName();
//                                                }
//                                            }
//                                            else{
//                                                sell = (buy * bid.getBidPrice());
//                                            }
//                                            sell = (buy * bid.getBidPrice());//-----
//                                            float spread = (sell/total - 1);
//                                            String bundleName = ask.getMarket()+bid.getMarket()+ask.getName();
//                                            int conf = 0;
//                                            conf = network.getConfirms();
//                                            if(conf == -1){
//                                                conf = bid.getCoin().getNetworks().get(network.getName()).getConfirms();
//                                            }
//                                            Bundle bnd = listenBundle.get(ask.getMarket()+ask.getName()+bid.getMarket());
//                                            if(bnd!=null){
//                                                listenBundle.remove(ask.getMarket()+ask.getName()+bid.getMarket());
//                                                bnd = new Bundle(ask.getName(),ask.getMarket(),ask.getAskPrice(),bid.getMarket(),bid.getBidPrice(),(float)(Math.floor(((spread+1)*total-total) * 100) / 100),network.getName(),conf,"", (float) (Math.floor(spread * 100000) / 100000)*100);
//                                                listenBundle.put(ask.getMarket()+ask.getName()+bid.getMarket(),bnd);
//                                            }
//                                            if(conf<minConfirms && spread*100>minSpread && spread < 100 && bid.getBidPrice()!=0 && ask.getAskPrice()!=0 && ask.getAskSize()>=minSize && bid.getBitSize()>=minSize){
//                                                long time = System.currentTimeMillis();
//                                                if(timesBundles.get(bundleName)!=null){
//                                                    time = time - timesBundles.get(bundleName);
//                                                }
//                                                else {
//                                                    timesBundles.put(bundleName,time);
//                                                    time = 0;
//                                                }
//                                                String t = String.format("%d : %d",
//                                                        TimeUnit.MILLISECONDS.toMinutes(time),
//                                                        TimeUnit.MILLISECONDS.toSeconds(time) -
//                                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
//                                                );
////                                                if(ask.getAskPrice()>bid.getBidPrice()){
////                                                    System.out.println(ask.getName());
////                                                    bundles.add(new Bundle(bid.getName(),bid.getMarket(),bid.getAskPrice(),ask.getMarket(),ask.getBidPrice(),(float)(Math.floor(((spread+1)*total-total) * 100) / 100),network.getName(),conf,h, (float) (Math.floor(spread * 100000) / 100000)*100));
////                                                }else{
////                                                    bundles.add(new Bundle(ask.getName(),ask.getMarket(),ask.getAskPrice(),bid.getMarket(),bid.getBidPrice(),(float)(Math.floor(((spread+1)*total-total) * 100) / 100),network.getName(),conf,h, (float) (Math.floor(spread * 100000) / 100000)*100));
////                                                }
//                                                bundles.add(new Bundle(ask.getCoin().getName()+"/"+ask.getPair(),ask.getMarket(),ask.getAskPrice(),bid.getMarket(),bid.getBidPrice(),(float)(Math.floor(((spread+1)*total-total) * 100) / 100),network.getName(),conf,t, (float) (Math.floor(spread * 100000) / 100000)*100));
//
//                                            }else if(timesBundles.get(bundleName)!=null){
//                                                timesBundles.remove(bundleName);
//                                            }
//                                        }
                                    }
                                }
                            }
                        }
                    }
                    bundles.sort(Comparator.comparingDouble(Bundle::getSpread));
                    Collections.reverse(bundles);
                    bundlesList = new ArrayList<>(bundles);

                    time1 = System.currentTimeMillis()-time1;
//                    System.out.println("times "+time1);
                   // System.out.println(time1);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }};
        new Thread(r).start();
    }
//    private List<Bundle> filter(User user){
//        List<Bundle> res = new ArrayList<>();
//        String text = "???/???";
//        if(user.getRole().equals(UserRole.LITE)){
//            System.out.println(user.getRole());
//            for(Bundle bundle : bundlesList){
//                if(bundle.getBuyName().equals(MarketName.GATEIO.getString()) ||
//                        bundle.getBuyName().equals(MarketName.HUOBI.getString()) ||
//                        bundle.getSellName().equals(MarketName.GATEIO.getString()) ||
//                        bundle.getSellName().equals(MarketName.HUOBI.getString())){
//
//                    res.add(new Bundle(text, bundle.getBuyName(),0f, bundle.getSellName(), 0f, bundle.getProfit(), bundle.getNetwork(), bundle.getConfirms(),bundle.getTime(), bundle.getSpread()));
//                }
//                else{
//                    res.add(bundle);
//                }
//            }
//            return res;
//        }
//        else{
//            return bundlesList;
//        }
//    }
    public void startSender(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(true){
                    List<PartialBotApiMethod<?>> methods = new ArrayList<>();
                    for(ScannerDTO dto : userInWork.values()){
                        User user = dto.getUser();
                        if(!bundlesList.isEmpty()){
                            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getScanner(dto);
                            if(inlineKeyboardMarkup!=null){
                                EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                                        .chatId(user.getTelegramId())
                                        .messageId(user.getBotMessageId())
                                        .replyMarkup(inlineKeyboardMarkup)
                                        .build();
                                methods.add(editMessageReplyMarkup);
                            }
                        }
                    }
                    for(PartialBotApiMethod<?> method: methods){
                        try {
                            BotInit.bot.executeAsync(method);
                        } catch (TelegramApiException e) {
                            log.error(e.toString());
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        new Thread(r).start();
    }

}
