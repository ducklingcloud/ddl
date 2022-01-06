/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 *
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.duckling.ddl.service.mobile.ios;

import java.util.List;
import java.util.ListIterator;

import javapns.communication.ConnectionToAppleServer;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class IOSMessageSender {
    private static final Logger LOG = Logger.getLogger(IOSMessageSender.class);
    private static AppleNotificationServer customServer;
    @Value("${ddl.root}/WEB-INF/conf/distribution1.p12")
    private String keystore;
    @Value("Cstnet123456")
    private String password;
    @PostConstruct
    public void init() {
        if(customServer==null){
            try{
                customServer = new AppleNotificationServerBasicImpl(keystore, password, ConnectionToAppleServer.KEYSTORE_TYPE_PKCS12, "gateway.sandbox.push.apple.com", 2195);
            }catch(Exception e){
                LOG.error("",e);
            }
        }
    }

    public void sendMessage(IOSMessageBean bean) throws JSONException{
        if(bean==null){
            return;
        }
        String message ="";
        if(StringUtils.isNotEmpty(bean.getMessage())){
            message = bean.getMessage();
        }
        PushNotificationPayload payload = PushNotificationPayload.alert(message);
        if(StringUtils.isNotEmpty(bean.getSound())){
            payload.addSound(bean.getSound());
        }
        payload.addBadge(bean.getNoticeCount());
        List<Device> deviceList = Devices.asDevices(bean.getDeviceToken());
        /* 创建一个 push notification manager */
        PushNotificationManager pushManager = new PushNotificationManager();

        /* 初始化连接 */
        try {
            pushManager.initializeConnection(customServer);
            List<PushedNotification> notifications = pushManager.sendNotifications(payload, deviceList);
            dealNotice(notifications, bean);
        } catch (Exception e) {
            LOG.error(bean, e);
        }

    }

    private void dealNotice(List<PushedNotification> notice,IOSMessageBean bean){
        ListIterator<PushedNotification> it = notice.listIterator();
        while(it.hasNext()){
            PushedNotification no = it.next();
            if(!no.isSuccessful()){
                LOG.error("send message "+bean+" error!" , no.getException());
            }
        }
    }

}
