package com.bsrvch.arbita.service.impl;

import com.bsrvch.arbita.model.Payment;
import com.bsrvch.arbita.model.Subscription;
import com.bsrvch.arbita.model.SubscriptionInfo;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.repository.SubscriptionInfoRepo;
import com.bsrvch.arbita.repository.SubscriptionRepo;
import com.bsrvch.arbita.service.PaymentService;
import com.bsrvch.arbita.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepo subscriptionRepo;
    private final SubscriptionInfoRepo subscriptionInfoRepo;
    @Override
    @Transactional
    public List<SubscriptionInfo> getAllSub() {
        return subscriptionInfoRepo.findAllBy();
    }

    @Override
    @Transactional
    public SubscriptionInfo getByDuration(String duration) {
        return subscriptionInfoRepo.findByDuration(duration);
    }
    @Override
    @Transactional
    public Subscription createSubscription(User user, Timestamp timestamp) {
        Subscription subscription = Subscription.builder()
                .user(user)
                .subscriptionOst(timestamp)
                .build();
        subscriptionRepo.save(subscription);
        return subscription;
    }

    @Override
    @Transactional
    public Subscription addSubscriptionTime(User user, Long time) {
        Subscription subscription = subscriptionRepo.findByUser(user);
        if(subscription==null){
            subscription = createSubscription(user, new Timestamp(System.currentTimeMillis()));
        } else if(subscription.getSubscriptionOst().getTime()<System.currentTimeMillis()) {
            subscription.setSubscriptionOst(new Timestamp(System.currentTimeMillis()+time));
        }
        subscription.setSubscriptionOst(new Timestamp(subscription.getSubscriptionOst().getTime()+time));
        subscriptionRepo.save(subscription);
        return subscription;
    }
    @Override
    @Transactional
    public boolean subscriptionCheck(User user){
        Subscription subscription = subscriptionRepo.findByUser(user);
        return System.currentTimeMillis()<subscription.getSubscriptionOst().getTime();
    }
}
