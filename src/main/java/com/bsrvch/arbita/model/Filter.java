package com.bsrvch.arbita.model;

import jakarta.persistence.Entity;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity(name = "filterTable")
public class Filter extends BaseEntity{
    private String type;
    private String name;
}
