package com.bsrvch.arbita.repository;

import com.bsrvch.arbita.model.SubscriptionInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubscriptionInfoRepo extends CrudRepository<SubscriptionInfo, Long> {
    List<SubscriptionInfo> findAllBy();
    SubscriptionInfo findByDuration(String duration);
}
