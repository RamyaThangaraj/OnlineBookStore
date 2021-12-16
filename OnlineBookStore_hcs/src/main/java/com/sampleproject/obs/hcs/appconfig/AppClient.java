package com.sampleproject.obs.hcs.appconfig;

import lombok.Data;

@Data
public class AppClient {
    private String clientName = "";
    private String clientKey = "";
    private String roles = "";
    private String paymentAccountDetails = "";
    private String color = "";
    private String appId = "";
    private int webPort = 8140;

}
