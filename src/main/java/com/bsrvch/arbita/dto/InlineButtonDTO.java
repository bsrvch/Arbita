package com.bsrvch.arbita.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InlineButtonDTO {

    String command;

    int stateIndex;

    String data;
}
