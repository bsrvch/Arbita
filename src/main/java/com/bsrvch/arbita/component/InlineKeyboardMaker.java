package com.bsrvch.arbita.component;

import com.bsrvch.arbita.constant.ButtonCommand;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.dto.crypto.Bundle;
import com.bsrvch.arbita.dto.interactiveHandler.FilterDTO;
import com.bsrvch.arbita.dto.interactiveHandler.PaymentDTO;
import com.bsrvch.arbita.dto.interactiveHandler.ScannerDTO;
import com.bsrvch.arbita.model.Filter;
import com.bsrvch.arbita.model.SubscriptionInfo;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.UserSettings;
import com.bsrvch.arbita.model.dictionary.MarketName;
import com.bsrvch.arbita.model.dictionary.UserRole;
import com.bsrvch.arbita.scanner.Scanner;
import com.bsrvch.arbita.statics.locale.StaticLocale;
import com.bsrvch.arbita.util.builder.InlineKeyboardBuilder;
import com.bsrvch.arbita.util.utilities.TelegramInlineButtonsUtils;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.lang.reflect.Array;
import java.util.*;

import static com.bsrvch.arbita.scanner.Scanner.*;
import static com.bsrvch.arbita.statics.locale.StaticLocale.*;
import static com.bsrvch.arbita.statics.locale.StaticLocale.getMenuScannerFilterMCN;

@Component
public class InlineKeyboardMaker {
    String nullData = "0";
    public InlineKeyboardMarkup getLocale(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder.button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SELECT_LOCALE,
                                getLocaleS("Ru"),
                                "RU",
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SELECT_LOCALE,
                                getLocaleS("En"),
                                "EN",
                                0
                        )
                )
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_SETTINGS,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getMenu(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder.button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_PROFILE,
                                getMenuProfile(),
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_SCANNER,
                                StaticLocale.getMenuScanner(),
                                nullData,
                                0
                        ))
                .row()
//                .button(
//                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
//                                InlineButtonCommand.MENU_TRACKING,
//                                StaticLocale.getMenuTrackingBundles(),
//                                "123",
//                                0
//                        ))
//                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_SETTINGS,
                                getMenuSettings(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getSettings(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder.button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_LOCALE,
                                getSettingsMenuLocale(),
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MAIN_MENU,
                                getMenuBack(),
                                nullData,
                                0
                        ))
        ;

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getEmailPay(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder.button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.GET_EMAIL,
                                getMenuProfileEmailText(),
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_SUBSCRIPTION,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getSubscription(List<SubscriptionInfo> subscriptionInfos){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        for(SubscriptionInfo info : subscriptionInfos){
            builder.button(
                    TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                            InlineButtonCommand.MENU_PAYMENT,
                            String.format(getMenuSub(info.getDuration()),info.getCost(),info.getCurrency()),
                            info.getDuration(),
                            0
                    )
            ).row();
        }
        builder
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_PROFILE,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getPayment(Integer paymentAmount, String currency, String paymentUrl){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder.button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverrideAndUrl(
                                InlineButtonCommand.MENU_PAYMENT,
                                String.format(getMenuPay(),paymentAmount,currency),
                                nullData,
                                0,
                                paymentUrl
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_PAYMENT,
                                getMenuBack(),
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_SUBSCRIPTION,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getProfile(User user){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        String mailText = getMenuProfileEmailText();
        if(user.getEmail()==null){
            mailText = getMenuProfileNewEmailText();
        }
        builder.button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_SUBSCRIPTION,
                                getMenuProfileSub(),
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.GET_EMAIL,
                                mailText,
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MAIN_MENU,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getEmail(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_PROFILE,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup getMenuScanner(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder.button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.START_SCANNER,
                                getScannerMenuStart(),
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_FILTER,
                                getMenuScannerFilter(),
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_INFO,
                                getScannerMenuInfo(),
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MAIN_MENU,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getFilterScanner(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_FILTER,
                                getMenuScannerFilterMCN("market"),
                                "market",
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_FILTER,
                                getMenuScannerFilterMCN("coin"),
                                "coin",
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_FILTER,
                                getMenuScannerFilterMCN("network"),
                                "network",
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_SCANNER,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getFilterSearch(FilterDTO dto){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        int index = dto.getLastIndex();
        int listSize = dto.getLastList().size();
        builder
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_FILTER,
                                getMenuScannerFilterShowAll(String.valueOf(dto.isAll())),
                                "|all|",
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_FILTER,
                                getMenuScannerFilterSwitchAll(String.valueOf(dto.isOnlyMode())),
                                "|switchall|",
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_FILTER,
                                getMenuScannerFilterMy(),
                                "|myfilter|",
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.SCANNER_FILTER,
                                getMenuBack(),
                                "|back|",
                                0
                        ))
                .row();
        if(index>=0){
            if(listSize>10){
                builder.button(TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                            InlineButtonCommand.NO_ACTION,
                            (index+1)+" / "+((listSize/10)+1),
                            nullData,
                            0
                        ))
                .row();
            }
            for(int i = 0; i < 10; i++){
                String s = "";
                if(listSize>index*10+i){
                    s = dto.getLastList().get(index*10+i).toUpperCase();
                    String text = "🟢";
                    if(dto.getFilter().contains(s) == !dto.isOnlyMode()){
                        text = "🔴";
                    }
//                    if(dto.isOnlyMode()){
//                        text = "\uD83D\uDD34";
//                        if(dto.getFilter().contains(s)){
//                            text = "\uD83D\uDFE2";
//                        }
//                    }
                    builder.button(
                                    TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                            InlineButtonCommand.SCANNER_FILTER,
                                            EmojiParser.parseToUnicode(s),
                                            "|switch|"+s,
                                            0
                                    ))
                            .button(
                                    TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                            InlineButtonCommand.SCANNER_FILTER,
                                            EmojiParser.parseToUnicode(text),
                                            "|switch|"+s,
                                            0
                                    ))
                        .row();
                }
            }
            if(listSize>10){
                if(index!=0)
                    builder.button(TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                            InlineButtonCommand.SCANNER_FILTER,
                            "⬅️",
                            "|prev|"+index,
                            0
                    ));
                if(listSize>(index+1)*10)
                    builder.button(TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                            InlineButtonCommand.SCANNER_FILTER,
                            "➡️",
                            "|next|"+index,
                            0
                    ));
            }
        }
        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getInfoScanner(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        builder
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.MENU_SCANNER,
                                getMenuBack(),
                                nullData,
                                0
                        ));

        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        return inlineKeyboardMarkup;
    }

    private void generateListenScanner(InlineKeyboardBuilder builder, String row1, String row2, String row3, String data){
        builder
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.START_SCANNER,
                                row1,
                                "|removelisten|"+data,
                                0
                        )
                )
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.NO_ACTION,
                                row2,
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.NO_ACTION,
                                row3,
                                nullData,
                                0
                        ))
                .row();
    }

    private void generateMainScanner(InlineKeyboardBuilder builder, String row1, String row2, String row3, boolean lis, String data){
        String text = "|addlisten|";
        if(lis){
            text = "|removelisten|";
        }
        builder
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.NO_ACTION,
                                getScannerSeparator(),
                                nullData,
                                0
                        )
                ).row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.START_SCANNER,
                                row1,
                                text+data,
                                0
                        )
                )
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.NO_ACTION,
                                row2,
                                nullData,
                                0
                        ))
                .row()
                .button(
                        TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                InlineButtonCommand.NO_ACTION,
                                row3,
                                nullData,
                                0
                        )
                ).row();
    }
    private void generateListen(List<String> listen, InlineKeyboardBuilder builder, UserRole userRole, int index){
        int numOfListen = 0;
        int j = 10;
        for(int i=index*j;i<j*(index+1);i++){
            String row1, row2, row3, row4, sep , data = "";
            if(i>=listen.size()) break;
            Bundle bundle = Scanner.listenBundle.get(listen.get(i));
            if(bundle!=null){
                //if(bundle.getBuyName()!=null){
                numOfListen++;
                if(userRole.equals(UserRole.LITE) && (!bundle.getBuyName().getFree() || !bundle.getSellName().getFree()))
                    row1 = getScannerListenRow1Red();
                else
                    row1 = String.format(getScannerListenRow1(),bundle.getPair().replace("/","♾"));
                row2 = String.format(getScannerListenRow2(),
                        bundle.getBuyName().getString().toUpperCase(),
                        bundle.getBuyPrice());
                row3 = String.format(getScannerListenRow3(),
                        bundle.getSellName().getString().toUpperCase(),
                        bundle.getSellPrice());

                sep = getScannerSeparator();
                data = bundle.getPair()+bundle.getBuyName()+bundle.getSellName()+bundle.getNetwork();
                if(numOfListen>1) {
                    builder.button(
                                    TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                            InlineButtonCommand.NO_ACTION,
                                            sep,
                                            nullData,
                                            0
                                    ))
                            .row();
                }
                generateListenScanner(builder,row1,row2,row3,data);
                //}
            }
        }
//        if(numOfListen>0){
//            builder
//                    .button(
//                            TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
//                                    InlineButtonCommand.NO_ACTION,
//                                    getEmptyString(),
//                                    nullData,
//                                    0
//                            ))
//                    .row();
//        }

    }
    private void generateMain(List<Bundle> bundles, List<String> listen, InlineKeyboardBuilder builder, UserRole userRole, boolean showVip, int index){
        int j = 10;
        for(int i=index*j;i<j*(index+1);i++){
            String empty = getEmptyString();
            String row1 = empty;
            String row2 = empty;
            String row3 = empty;
            String data = empty;
            boolean lis = false;
            if(bundles.size()>i){
                Bundle bundle = bundles.get(i);
                if(userRole.equals(UserRole.LITE) && ((!bundle.getBuyName().getFree() || !bundle.getSellName().getFree())) && showVip)
                    row1 = getScannerMainRow1Red();
                else
                    row1 = String.format(getScannerMainRow1(),
                            bundle.getPair().replace("/","♾"));
                if(listen!=null){
                    if(listen.contains(bundle.getPair()+bundle.getBuyName()+bundle.getSellName()+bundle.getNetwork())){
                        lis = true;
                        if(userRole.equals(UserRole.LITE) && ((!bundle.getBuyName().getFree() || !bundle.getSellName().getFree())) && showVip)
                            row1 = getScannerListenRow1Red();
                        else
                            row1 = String.format(getScannerListenRow1(),
                                    bundle.getPair().replace("/","♾"));
                    }
                }

                row2 = String.format(getScannerMainRow2(),
                        bundle.getBuyName().getString().toUpperCase(),
                        bundle.getSellName().getString().toUpperCase());
                row3 = String.format(getScannerMainRow3(),
                        bundle.getNetwork(),
                        bundle.getS_spread(),
                        bundle.getTime());
                data = bundle.getPair()+bundle.getBuyName()+bundle.getSellName()+bundle.getNetwork();
            }
            generateMainScanner(builder,row1,row2,row3,lis,data);
        }
    }
    private List<Bundle> filter(UserSettings userSettings, String searchText, boolean showVip){
        HashMap<String, String> marketF = new HashMap<>();
        HashMap<String, String> coinF = new HashMap<>();
        HashMap<String, String> networkF = new HashMap<>();
        ArrayList<Bundle> bundles = new ArrayList<>();
        boolean marketO = false;
        boolean coinO = false;
        boolean networkO = false;
        if(userSettings.getFilterOnlyMode()!=null){
            marketO = userSettings.getFilterOnlyMode().contains("market");
            coinO = userSettings.getFilterOnlyMode().contains("coin");
            networkO = userSettings.getFilterOnlyMode().contains("network");
        }

        for(Filter filter : userSettings.getFilters()){
            String type = filter.getType();
            String name = filter.getName();
            switch (type){
                case "market":{
                    marketF.put(name,name);
                    break;
                }
                case "coin":{
                    coinF.put(name,name);
                    break;
                }
                case "network":{
                    networkF.put(name,name);
                    break;
                }
                default: break;
            }
        }
        UserRole userRole = userSettings.getUser().getRole();
        for(Bundle bundle : bundlesList){
            boolean filter = ((marketF.containsKey(bundle.getBuyName().getString())== marketO)
                    && (marketF.containsKey(bundle.getSellName().getString()) == marketO))
                    && ((coinF.containsKey(bundle.getPair().split("/")[0]) == coinO)
                    && (coinF.containsKey(bundle.getPair().split("/")[1]) == coinO))
                    && (networkF.containsKey(bundle.getNetwork()) == networkO)
                    && !(userRole.equals(UserRole.LITE) && ((!bundle.getBuyName().getFree() || !bundle.getSellName().getFree())) && !showVip);
            if(filter){
                if(searchText!=null && searchText!=""){
                    if((bundle.getPair().startsWith(searchText) || bundle.getPair().split("/")[1].startsWith(searchText)) && !(userRole.equals(UserRole.LITE) && ((!bundle.getBuyName().getFree() || !bundle.getSellName().getFree())))){
                        bundles.add(bundle);
                    }
                }else {
                    bundles.add(bundle);
                }
//                if(userRole.equals(UserRole.LITE)){
//                    if((!bundle.getBuyName().getFree() || !bundle.getSellName().getFree()) && userSettings.isShowVip()){
//                        bundle.setPair("nigger");
//                        Bundle bnd = new Bundle();
//                        bnd.setBundle(bundle);
//                        bnd.setPair("nigger");
//                        bundles.add(bnd);
//                    }else if(bundle.getBuyName().getFree() && bundle.getSellName().getFree()){
//                        bundles.add(bundle);
//                    }
//                }else{
//                    bundles.add(bundle);
//                }
            }
        }
        return bundles;
    }

    public InlineKeyboardMarkup getScanner(ScannerDTO dto){
        User user = dto.getUser();
        int numOfLine = 4;
        UserSettings userSettings = user.getUserSettings();
        boolean showVip = dto.isShowVip();
        List<Bundle> bundles = filter(userSettings, dto.getSearchText(), showVip);
        if(user.getSubscription()!=null){
            if(System.currentTimeMillis() < user.getSubscription().getSubscriptionOst().getTime() && user.getRole().equals(UserRole.LITE)){
                user.setRole(UserRole.VIP);
            } else if (System.currentTimeMillis() > user.getSubscription().getSubscriptionOst().getTime() && user.getRole().equals(UserRole.VIP)) {
                user.setRole(UserRole.LITE);
            }
        }
        UserRole userRole = user.getRole();

//        HashMap<String, String> marketF = new HashMap<>();
//        HashMap<String, String> coinF = new HashMap<>();
//        HashMap<String, String> networkF = new HashMap<>();
//        boolean marketO = userSettings.getFilterOnlyMode().contains("market");
//        boolean coinO = userSettings.getFilterOnlyMode().contains("coin");
//        boolean networkO = userSettings.getFilterOnlyMode().contains("network");
//        for(Filter filter : userSettings.getFilters()){
//            String type = filter.getType();
//            String name = filter.getName();
//            switch (type){
//                case "market":{
//                    marketF.put(name,name);
//                    break;
//                }
//                case "coin":{
//                    coinF.put(name,name);
//                    break;
//                }
//                case "network":{
//                    networkF.put(name,name);
//                    break;
//                }
//                default: break;
//            }
//        }
//        for(Bundle bundle : bundlesU){
//            boolean filter = ((marketF.containsKey(bundle.getBuyName().getString())== marketO)
//                            && (marketF.containsKey(bundle.getSellName().getString()) == marketO))
//                            && ((coinF.containsKey(bundle.getPair().split("/")[0]) == coinO)
//                            && (coinF.containsKey(bundle.getPair().split("/")[1]) == coinO))
//                            && (networkF.containsKey(bundle.getNetwork()) == networkO);
//            if(filter){
//                if(userRole.equals(UserRole.LITE)){
//                    if((!bundle.getBuyName().getFree() || !bundle.getSellName().getFree()) && userSettings.isShowVip()){
//                        Bundle bnd = new Bundle();
//                        bnd.setBundle(bundle);
//                        bnd.setPair("nigger");
//                        bundles.add(bnd);
//                    }else if(bundle.getBuyName().getFree() && bundle.getSellName().getFree()){
//                        bundles.add(bundle);
//                    }
//                }else{
//                    bundles.add(bundle);
//                }
//            }
//        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.instance();
        if(user.getRole().equals(UserRole.LITE) && !dto.isShowListen()){
            String text = getScannerMenuShow();
            if(dto.isShowVip()) text+= "🔴";
            else text+= "🟢";
            builder.button(
                            TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                    InlineButtonCommand.START_SCANNER,
                                    text,
                                    "|showvip|",
                                    0
                            ))
                    .row();
        }
        if(!dto.isShowListen()) {
            builder.button(
                            TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                    InlineButtonCommand.START_SCANNER,
                                    "Показать все",
                                    "|showall|",
                                    0
                            ))
                    .row();
            builder.button(
                            TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                                    InlineButtonCommand.START_SCANNER,
                                    "Мои связки",
                                    "|listen|",
                                    0
                            ))
                    .row();
            builder.button(TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                            InlineButtonCommand.MENU_SCANNER,
                            StaticLocale.getMenuBack(),
                            nullData,
                            0
                    ))
                    .row();
        }else {
            builder.button(TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                            InlineButtonCommand.START_SCANNER,
                            StaticLocale.getMenuBack(),
                            "|listen|",
                            0
                    ))
                    .row();
        }
        List<String> listen =  Scanner.listenUser.get(user.getTelegramId());

        int listSize = bundles.size();
        int index = dto.getLastIndex();
        if(listen!=null && dto.isShowListen()){
            listSize = listen.size();
        }else if(dto.isShowListen()) listSize = 0;
        if(listSize>10 && (dto.isShowListen() || dto.isShowAll())){
            builder.button(TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                            InlineButtonCommand.NO_ACTION,
                            (index+1)+" / "+((listSize/10)+1),
                            nullData,
                            0
                    ))
                    .row();
        }
        if(listen!=null && dto.isShowListen()){
            generateListen(listen,builder,userRole,index);
        }
        if(!dto.isShowListen() && dto.isShowAll()){
            generateMain(bundles, listen, builder,userRole,showVip, index);
        }
        inlineKeyboardMarkup.setKeyboard(builder.build().getKeyboard());
        if(listSize>10 && (dto.isShowListen() || dto.isShowAll())){
            if(index!=0)
                builder.button(TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                        InlineButtonCommand.START_SCANNER,
                        "⬅️",
                        "|prev|",
                        0
                ));
            if(listSize>(index+1)*10)
                builder.button(TelegramInlineButtonsUtils.createInlineButtonWithDescriptionOverride(
                        InlineButtonCommand.START_SCANNER,
                        "➡️",
                        "|next|",
                        0
                ));
        }
        if(dto.getPrevMark()!=null){
            if(dto.getPrevMark().getKeyboard().equals(inlineKeyboardMarkup.getKeyboard())) {
                dto.setPrevMark(inlineKeyboardMarkup);
                return null;
            }
            dto.setPrevMark(inlineKeyboardMarkup);
        }

        return inlineKeyboardMarkup;
    }






//    public InlineKeyboardMarkup getLocale(){
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List< InlineKeyboardButton >> rowsInline = new ArrayList<>();
//        for(Locale locale : StaticLocale.getAllLocale()){
//            List<InlineKeyboardButton> row = new ArrayList<>();
//            InlineKeyboardButton btn = new InlineKeyboardButton();
//            btn.setText(locale.getLanguage());
//            btn.setCallbackData(ButtonCommand.START.getCommand());
//            row.add(btn);
//            rowsInline.add(row);
//        }
//        inlineKeyboardMarkup.setKeyboard(rowsInline);
//        return inlineKeyboardMarkup;
//    }
}
