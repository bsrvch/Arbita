package com.bsrvch.arbita.model;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "subscriptionDataTable")
public class Subscription extends BaseEntity{
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(name = "subscription_date")
    private Timestamp subscriptionOst;
}
