package com.bsrvch.arbita.util.utilities;

import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.dto.InlineButtonDTO;
import com.bsrvch.arbita.encoder.InlineButtonDTOEncoder;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

@UtilityClass
public class TelegramInlineButtonsUtils {
    public InlineKeyboardButton createInlineButton(String commandName, String data, int stateIndex, String text) {
        return InlineKeyboardButton.builder()
                .callbackData(InlineButtonDTOEncoder.encode(
                        InlineButtonDTO.builder()
                                .command(commandName)
                                .stateIndex(stateIndex)
                                .data(data)
                                .build()
                ))
                .text(text)
                .build();
    }

    public InlineKeyboardButton createInlineButton(Command commandObject, String data, int stateIndex) {
        return InlineKeyboardButton.builder()
                .callbackData(InlineButtonDTOEncoder.encode(
                        InlineButtonDTO.builder()
                                .command(commandObject.getCommand())
                                .stateIndex(stateIndex)
                                .data(data)
                                .build()
                ))
                .text(commandObject.getDescription())
                .build();
    }

    public InlineKeyboardButton createInlineButtonWithDescriptionOverride(Command commandObject, String descriptionOverride, String data, int stateIndex) {
        return InlineKeyboardButton.builder()
                .callbackData(InlineButtonDTOEncoder.encode(
                        InlineButtonDTO.builder()
                                .command(commandObject.getCommand())
                                .stateIndex(stateIndex)
                                .data(data)
                                .build()
                ))
                .text(descriptionOverride)
                .build();
    }
    public InlineKeyboardButton createInlineButtonWithDescriptionOverrideAndUrl(Command commandObject, String descriptionOverride, String data, int stateIndex, String paymentUrl) {
        return InlineKeyboardButton.builder()
                .callbackData(" ")
                .url(paymentUrl)
                .text(descriptionOverride)
                .build();
    }
}
