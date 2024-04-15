package com.bsrvch.arbita.service;

import com.bsrvch.arbita.model.Payment;
import com.bsrvch.arbita.model.Subscription;
import com.bsrvch.arbita.model.SubscriptionInfo;
import com.bsrvch.arbita.model.User;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface SubscriptionService {
    Subscription createSubscription(User user, Timestamp timestamp);
    Subscription addSubscriptionTime(User user, Long timestamp);
    boolean subscriptionCheck(User user);
    List<SubscriptionInfo> getAllSub();
    SubscriptionInfo getByDuration(String duration);
}
