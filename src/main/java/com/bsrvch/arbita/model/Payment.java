package com.bsrvch.arbita.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity(name = "paymentDataTable")
public class Payment extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String paymentId;
    private String amount;
    private String currency;
    private String description;
    private String created_at;
    private String status;
    private String captured_at;



}
