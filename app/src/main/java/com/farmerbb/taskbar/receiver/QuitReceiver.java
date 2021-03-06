/* Copyright 2016 Braden Farmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farmerbb.taskbar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.farmerbb.taskbar.service.DashboardService;
import com.farmerbb.taskbar.service.NotificationService;
import com.farmerbb.taskbar.service.StartMenuService;
import com.farmerbb.taskbar.service.TaskbarService;
import com.farmerbb.taskbar.helper.LauncherHelper;
import com.farmerbb.taskbar.util.U;

import static com.farmerbb.taskbar.util.Constants.*;

public class QuitReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_QUIT.equals(intent.getAction())) {
            return;
        }
        SharedPreferences pref = U.getSharedPreferences(context);
        if(!pref.getBoolean(PREF_SKIP_QUIT_RECEIVER, false)) {
            Intent taskbarIntent = new Intent(context, TaskbarService.class);
            Intent startMenuIntent = new Intent(context, StartMenuService.class);
            Intent dashboardIntent = new Intent(context, DashboardService.class);
            Intent notificationIntent = new Intent(context, NotificationService.class);

            pref.edit().putBoolean(PREF_TASKBAR_ACTIVE, false).apply();

            if(!LauncherHelper.getInstance().isOnHomeScreen(context)) {
                context.stopService(taskbarIntent);
                context.stopService(startMenuIntent);
                context.stopService(dashboardIntent);

                U.clearCaches(context);
                U.sendBroadcast(context, ACTION_START_MENU_DISAPPEARING);
            }

            context.stopService(notificationIntent);
        } else
            pref.edit().remove(PREF_SKIP_QUIT_RECEIVER).apply();
    }
}
