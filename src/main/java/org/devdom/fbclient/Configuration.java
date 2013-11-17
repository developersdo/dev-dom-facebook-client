package org.devdom.fbclient;

import facebook4j.conf.ConfigurationBuilder;

/**
 *
 * @author Carlos Vásquez Polanco
 */

public class Configuration{

    private Configuration(){ }
    
    public static ConfigurationBuilder getDoConfig(){
       return new ConfigurationBuilder()
               .setDebugEnabled(true)
               .setOAuthAppId("****************")
               .setOAuthAppSecret("********************************")
               .setOAuthAccessToken("********************************************************************************")
               .setOAuthPermissions("user_groups,user_questions");

    }
    
}