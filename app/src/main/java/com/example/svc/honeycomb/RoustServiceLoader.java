package com.example.svc.honeycomb;

import route.module.Route_chatserviceimpl;
import route.module.Route_loginserviceimpl;
import router.air.annotation.info.RouteInfo;

import java.util.Map;

public class RoustServiceLoader {
    public static void init(Map<String, RouteInfo> infoMap){
        Route_loginserviceimpl.init(infoMap);
        Route_chatserviceimpl.init(infoMap);
    }
}
