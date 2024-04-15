package com.bsrvch.arbita.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "userSettingsTable")
public class UserSettings {
    @Id
    @Column(name = "user_id")
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "show_vip")
    @ColumnDefault("true")
    private boolean showVip;
    private String filterOnlyMode;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Filter> filters;
}
