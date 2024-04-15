package com.bsrvch.arbita.resolver;

import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.annotation.LiteRole;
import com.bsrvch.arbita.annotation.TextCommandType;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.model.dictionary.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LiteResolver extends Resolver {
    public LiteResolver(
            @TextCommandType @LiteRole List<Handler> textCommandHandlers,
            @InlineButtonType @LiteRole List<Handler> inlineButtonsHandlers
    ) {
        super(textCommandHandlers, inlineButtonsHandlers);
    }

    @Override
    public UserRole getResolverUserRole() {
        return UserRole.LITE;

    }
}