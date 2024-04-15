package com.bsrvch.arbita.service.impl;

import com.bsrvch.arbita.dto.interactiveHandler.PaymentDTO;
import com.bsrvch.arbita.model.Payment;
import com.bsrvch.arbita.model.SubscriptionInfo;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.repository.PaymentRepo;
import com.bsrvch.arbita.service.PaymentService;
import com.bsrvch.arbita.service.SubscriptionService;
import com.bsrvch.arbita.statics.locale.StaticLocale;
import com.bsrvch.arbita.util.web.WebUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final SubscriptionService subscriptionService;
//    Comparator<Payment> sortByDate = new Comparator<Payment>() {
//        public int compare(Payment p1, Payment p2) {
//            return p2.getCreatedWhen().compareTo(p1.getCreatedWhen());
//        }
//    };

    @Override
    public JSONObject createUrl(String email, PaymentDTO dto, String dur, String shopId, String secretApi){
        System.out.println(dur);
        SubscriptionInfo subscriptionInfo = subscriptionService.getByDuration(dur);
        JSONObject jo = new JSONObject(WebUtil.getPaymentUrl(
                shopId,
                secretApi,
                email,
                subscriptionInfo,
                StaticLocale.getPayDesc(subscriptionInfo.getDuration())
        ));
        dto.setDurationUnix(subscriptionInfo.getDurationUnix());
        dto.setPayId(jo.getString("id"));
        return jo;
    }



    @Override
    @Transactional
    public Payment createPayment(User user, PaymentDTO dto, String shopId, String secretApi){
        //dto.setPayId("2cbaaabb-000f-5000-a000-11eb665bd1ba");
        JSONObject paymentJson = new JSONObject(WebUtil.getPaymentById(shopId, secretApi,dto.getPayId()));
        if(paymentJson.getString("status").equals("succeeded")){
            Payment payment = Payment.builder()
                    .user(user)
                    .amount(paymentJson.getJSONObject("amount").getString("value"))
                    .currency(paymentJson.getJSONObject("amount").getString("currency"))
                    .status(paymentJson.getString("status"))
                    .paymentId(paymentJson.getString("id"))
                    .created_at(paymentJson.getString("created_at"))
                    .captured_at(paymentJson.getString("captured_at"))
                    .description(paymentJson.getString("description"))
                    .build();
            subscriptionService.addSubscriptionTime(user,dto.getDurationUnix());
            System.out.println("conf");
            return paymentRepo.save(payment);
        }
        System.out.println("not conf");
        return null;
    }
}
