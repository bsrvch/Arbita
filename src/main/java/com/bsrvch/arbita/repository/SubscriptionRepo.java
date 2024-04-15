package com.bsrvch.arbita.repository;

import com.bsrvch.arbita.model.Subscription;
import com.bsrvch.arbita.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepo extends CrudRepository<Subscription, UUID> {
    Subscription findByUser(User user);
}
