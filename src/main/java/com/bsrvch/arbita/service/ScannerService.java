package com.bsrvch.arbita.service;

import com.bsrvch.arbita.dto.interactiveHandler.FilterDTO;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.scanner.Market;

import java.util.List;

public interface ScannerService {
    List<Market> getMarkets();
    void filterStart(User user, String data, FilterDTO dto);
    void filter(User user, String data, FilterDTO dto);
    void filterSearch(FilterDTO dto, String data);
    void addListen(String bundleName, Long chatId);
    void removeListen(String bundleName, Long chatId);
    void showVip(User user);
}
