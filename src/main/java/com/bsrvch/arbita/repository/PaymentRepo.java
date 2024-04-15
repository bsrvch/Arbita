package com.bsrvch.arbita.repository;

import com.bsrvch.arbita.model.Payment;
import com.bsrvch.arbita.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepo extends CrudRepository<Payment, UUID> {


}
