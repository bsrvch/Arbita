package com.bsrvch.arbita.service;

import com.bsrvch.arbita.dto.interactiveHandler.PaymentDTO;
import com.bsrvch.arbita.model.Payment;
import com.bsrvch.arbita.model.SubscriptionInfo;
import com.bsrvch.arbita.model.User;
import org.json.JSONObject;

import java.util.List;

public interface PaymentService {
    Payment createPayment(User user, PaymentDTO dto, String shopId, String secretApi);
    JSONObject createUrl(String email, PaymentDTO dto, String dur, String shopId, String secretApi);
}
