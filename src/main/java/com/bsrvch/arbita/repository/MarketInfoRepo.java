package com.bsrvch.arbita.repository;

import com.bsrvch.arbita.model.MarketInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarketInfoRepo extends CrudRepository<MarketInfo, Long> {
    MarketInfo findMarketINFOByName(String name);
    List<MarketInfo> findAllBy();
}
