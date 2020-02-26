/*package com.teamticpro.bagmetech.servicesBackground;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.teamticpro.bagmetech.SyncNotificationQuery;
import com.teamticpro.bagmetech.graphql.Base;
import com.teamticpro.bagmetech.managers.NotificationsManagerAndroid;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.OkHttpClient;

public class SyncRunnable implements Runnable {

    private NotificationsManagerAndroid parseNotification;

    public SyncRunnable(NotificationsManagerAndroid parseNotification) {
        this.parseNotification = parseNotification;
    }

    private ApolloClient getApolloClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        return ApolloClient.builder().serverUrl(Base.BASE_URL)
                .okHttpClient(okHttpClient).build();
    }
    public void setParseNotification(NotificationsManagerAndroid parseNotification){
        this.parseNotification=parseNotification;
    }

    @Override
    public void run() {
        getApolloClient().query(SyncNotificationQuery.builder().mac_address("CD-DE-EF-TD-AC").build()).enqueue(new ApolloCall.Callback<SyncNotificationQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<SyncNotificationQuery.Data> response) {
                SyncNotificationQuery.Data data = response.data();
                if (data != null) {
                    List<SyncNotificationQuery.SyncNotification> a = data.syncNotification();
                    if (a != null) {
                        for (SyncNotificationQuery.SyncNotification notification : a){
                            parseNotification.createNotifications(notification.title(), notification.description());
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });
    }
}*/
package com.teamticpro.bagmetech.servicesBackground;

import com.teamticpro.bagmetech.managers.NotificationsManagerAndroid;

public class SyncRunnable implements Runnable {

    private NotificationsManagerAndroid parseNotification;


    public SyncRunnable(NotificationsManagerAndroid parseNotification, String IP, int PORT) {
        this.parseNotification = parseNotification;

    }

    public void setParseNotification(NotificationsManagerAndroid parseNotification) {
        this.parseNotification = parseNotification;
    }

    @Override
    public void run() {

    }
}