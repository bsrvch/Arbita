package com.bsrvch.arbita.service.impl;

import com.bsrvch.arbita.dto.crypto.Bundle;
import com.bsrvch.arbita.dto.interactiveHandler.FilterDTO;
import com.bsrvch.arbita.model.Filter;
import com.bsrvch.arbita.model.MarketInfo;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.repository.FilterRepo;
import com.bsrvch.arbita.repository.MarketInfoRepo;
import com.bsrvch.arbita.repository.UserRepo;
import com.bsrvch.arbita.scanner.Market;
import com.bsrvch.arbita.scanner.Scanner;
import com.bsrvch.arbita.service.ScannerService;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.state.filter.FilterEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScannerServiceImpl implements ScannerService {
    private final UserService userService;
    private final MarketInfoRepo marketInfoRepo;
    private final FilterRepo filterRepo;
    private final UserRepo userRepo;
    @Override
    @Transactional
    public List<Market> getMarkets() {
        List<MarketInfo> marketInfos = marketInfoRepo.findAllBy();
        List<Market> markets = new ArrayList<>();
        for(MarketInfo marketInfo : marketInfos){
            Market market = marketInfo.getName().getMarket();
            market.setName(marketInfo.getName());
            market.setKey(marketInfo.getApiKey());
            market.setSecret(marketInfo.getSecretKey());
            markets.add(market);
        }
        return markets;
    }

    @Override
    public void filterStart(User user, String data, FilterDTO dto){
        List<String> dataList = new ArrayList<>();
        ArrayList<String> filter = new ArrayList<>(user.getUserSettings().getFilters().stream().filter(el -> el.getType().equals(data)).map(Filter::getName).toList());
        dto.setLastIndex(-1);
        switch (data){
            case "market":{
                dataList = Scanner.markets.stream().map(el -> el.getName().getString()).sorted().toList();
                dto.setLastIndex(0);
                dto.setAll(true);
                break;
            }
            case "coin":{
                dataList = Scanner.allCoin.values().stream().sorted().toList();
                break;
            }
            case "network":{
                dataList = Scanner.allNetwork.keySet().stream().sorted().toList();
                break;
            }
            default: break;
        }
        if(user.getUserSettings().getFilterOnlyMode()!=null){
            dto.setOnlyMode(user.getUserSettings().getFilterOnlyMode().contains(data));
        }else user.getUserSettings().setFilterOnlyMode("");
        dto.setFilter(filter);
        dto.setDataList(dataList);
        dto.setLastList(dataList);
        dto.setType(data);
    }
    @Override
    public void filter(User user, String data, FilterDTO dto){
        String type = dto.getType();
        if(data.contains("|all|")){
            if(dto.isAll()){
                dto.setAll(false);
                dto.setLastIndex(-1);
            }else{
                dto.setAll(true);
                dto.setLastIndex(0);
            }
            dto.setLastList(dto.getDataList());
        } else if (data.equals("|switchall|")) {
            dto.setFilter(new ArrayList<>());
            if(dto.isOnlyMode()){
                user.getUserSettings().setFilterOnlyMode(user.getUserSettings().getFilterOnlyMode().replace(type,""));
            }else{
                user.getUserSettings().setFilterOnlyMode((user.getUserSettings().getFilterOnlyMode()+type));
            }
            dto.setOnlyMode(!dto.isOnlyMode());
            dto.setLastList(dto.getDataList());
            user.getUserSettings().getFilters().removeIf(el -> el.getType().equals(type));
        } else if(data.contains("|next|")){
            dto.setLastList(dto.getDataList());
            dto.incLastIndex();
        } else if(data.contains("|prev|")){
            dto.setLastList(dto.getDataList());
            dto.decLastIndex();
        } else if(data.contains("|myfilter|")){
            dto.setAll(false);
            dto.setLastIndex(0);
            dto.setLastList(dto.getFilter());
        } else if (data.contains("|switch|")) {
            data = data.replace("|switch|","");
            if(dto.getFilter().contains(data)){
                String d = data;
                user.getUserSettings().getFilters().removeIf(el -> el.getType().equals(type) && el.getName().equals(d));
                dto.removeFilter(data);
            }else {
                user.getUserSettings().getFilters().add(new Filter(type,data));
                dto.addFilter(data);
            }
        }
    }
    @Override
    public void filterSearch(FilterDTO dto, String data){
        dto.setLastIndex(0);
        dto.setLastList(dto.getDataList().stream().
                filter(it -> it.startsWith(data.toUpperCase()))
                .collect(Collectors.toList())
        );
        dto.setAll(false);
    }
    @Override
    public void addListen(String bundleName, Long chatId){
        List<String> bundles = Scanner.listenUser.get(chatId);
        if(!Scanner.listenBundle.containsKey(bundleName)){
            Scanner.listenBundle.put(bundleName,new Bundle());
        }
        if(bundles!=null){
            if(!bundles.contains(bundleName))
                bundles.add(bundleName);
        }else{
            List<String> newBundles = new ArrayList<>();
            newBundles.add(bundleName);
            Scanner.listenUser.put(chatId,newBundles);
        }
    }
    @Override
    public void removeListen(String bundleName, Long chatId){
        List<String> bundles = Scanner.listenUser.get(chatId);
        if(Scanner.listenBundle.containsKey(bundleName) && !Scanner.listenUser.values().stream().flatMap(List::stream).toList().contains(bundleName)){
            Scanner.listenBundle.remove(bundleName);
        }
        if(bundles!=null){
            bundles.remove(bundleName);
        }
    }
    @Override
    public void showVip(User user){
        if(Scanner.userInWork.containsKey(user.getTelegramId())){
            user = Scanner.userInWork.get(user.getTelegramId()).getUser();
        }
        if(user.getUserSettings().isShowVip()){
            user.getUserSettings().setShowVip(false);
            userService.setVipShow(user,false);
        }else{
            user.getUserSettings().setShowVip(true);
            userService.setVipShow(user,true);
        }
    }


}
