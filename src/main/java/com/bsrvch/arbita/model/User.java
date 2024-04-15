package com.bsrvch.arbita.model;

import com.bsrvch.arbita.model.dictionary.UserRole;
import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "userDataTable")
public class User extends BaseEntity{
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private UserSettings userSettings;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "chat_id", nullable = false, unique = true)
    private Long telegramId;

    @Column(name = "bot_message_id")
    private Integer botMessageId;

    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    @OneToOne(mappedBy = "user")
    private Subscription subscription;


}
