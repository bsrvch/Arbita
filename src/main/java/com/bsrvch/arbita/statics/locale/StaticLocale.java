package com.bsrvch.arbita.statics.locale;
import com.vdurmont.emoji.EmojiParser;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class StaticLocale {
    public static void setLocaleRU(){
        Locale.setDefault(Locale.forLanguageTag("RU"));
    }
    public static void setLocaleEN(){
        Locale.setDefault(Locale.forLanguageTag("EN"));
    }
    public static List<Locale> getAllLocale(){
        return List.of(
                Locale.forLanguageTag("RU"),
                Locale.forLanguageTag("EN")
        );
    }
    public static String getGreeting(){
        return ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("greeting");
    }
    public static String getMenuText(){
        return ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_text");
    }
    public static String getMenuBack(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_back"));
    }
    public static String getMenuProfile(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_profile"));
    }
    public static String getMenuProfileSub(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_profile_sub"));
    }
    public static String getEmailText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("email_text"));
    }
    public static String getBadEmailText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("bad_email_text"));
    }
    public static String getGoodEmailText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("good_email_text"));
    }
    public static String getMenuProfileEmailText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_profile_email_text"));
    }
    public static String getMenuProfileNewEmailText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_profile_new_email_text"));
    }
    public static String getMenuScannerFilter(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_scanner_filter"));
    }
    public static String getFilterText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("filter_text"));
    }
    public static String getFilterSearchText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("filter_search_text"));
    }
    public static String getMenuScannerFilterMCN(String s){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_filter_"+s));
    }
    public static String getMenuScannerFilterShowAll(String s){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_filter_showall_"+s));
    }
    public static String getMenuScannerFilterSwitchAll(String s){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_filter_switchall_"+s));
    }
    public static String getMenuScannerFilterMy(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_filter_my"));
    }
    public static String getSubText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("subscription_text"));
    }
    public static String getSubNoText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("subscription_no_text"));
    }
    public static String getMenuSub(String dur){
        try{
            return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_sub_"+dur));
        }catch (Exception e){
            return EmojiParser.parseToUnicode("%s %s");
        }
    }
    public static String getMenuPay(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_pay"));
    }
    public static String getPayText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("payment_text"));
    }
    public static String getPayEmailText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("payment_email_text"));
    }
    public static String getPayDesc(String dur){
        try{
            return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("pay_desc_"+dur));
        }catch (Exception e){
            return EmojiParser.parseToUnicode("null");
        }
    }
    public static String getPayTitle(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("pay_title"));
    }
    public static String getPayLabel(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("pay_label"));
    }
    public static String getScannerText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_text"));
    }
    public static String getMenuScanner(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_scanner"));
    }
    public static String getScannerMenuStart(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_menu_start"));
    }
    public static String getScannerMenuInfo(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_menu_info"));
    }
    public static String getScannerHiddenPair(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_hidden_pair"));
    }
    public static String getScannerInfoText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_info_text"));
    }
    public static String getScannerStartText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_start_text"));
    }
    public static String getEmptyString(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("empty_string"));
    }
    public static String getScannerSeparator(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_separator"));
    }
    public static String getScannerListenRow1(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_listen_row1"));
    }
    public static String getScannerListenRow1Red(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_listen_row1_red"));
    }
    public static String getScannerListenRow2(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_listen_row2"));
    }
    public static String getScannerListenRow3(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_listen_row3"));
    }
    public static String getScannerMainRow1(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_main_row1"));
    }
    public static String getScannerMainRow1Red(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_main_row1_red"));
    }
    public static String getScannerMainRow2(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_main_row2"));
    }
    public static String getScannerMainRow3(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_main_row3"));
    }
    public static String getScannerMenuShow(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("scanner_menu_show"));
    }
    public static String getMenuTrackingBundles(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_tracking_bundles"));
    }
    public static String getSettingsText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("settings_text"));
    }
    public static String getMenuSettings(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("menu_settings"));
    }
    public static String getSettingsMenuLocale(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("settings_menu_locale"));
    }
    public static String getLocaleText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("locale_text"));
    }

    public static String textSm(String text){
        String res = "";
        String[] ss = text.split("\n");
        int p = 26;
        for(String s : ss){
            int op = EmojiParser.replaceAllEmojis(s,"0").length();
            System.out.println(op +"  "+(p-op)/2);
            String y = "";
            for(int i = 0; i < (p-op)/2; i++){
                y+="\t ";
            }
            res += y+ s + "\n";
        }
        return res;
    }


    public static String getProfileText(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("profile_text"));
    }
    public static String getProfileTextWithEmail(){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("profile_text_with_email"));
    }



    public static String getGreetingChoose(){
        return ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("greetingChoose");
    }
    public static String getGreetingAgain(){
        return ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("greetingAgain");
    }
    public static String getLocaleS(String locale){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString("locale"+locale));
    }
    public static String getRole(String role){
        return EmojiParser.parseToUnicode(ResourceBundle.getBundle("locale/loc",Locale.getDefault()).getString(role+"_role"));
    }

}
