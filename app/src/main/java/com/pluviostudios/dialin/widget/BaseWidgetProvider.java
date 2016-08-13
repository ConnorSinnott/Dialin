package com.pluviostudios.dialin.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import com.pluviostudios.dialin.action.ActionManager;

/**
 * Created by spectre on 7/26/16.
 */
public class BaseWidgetProvider extends AppWidgetProvider {

    public static final String TAG = "BaseWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ActionManager.initialize(context);
        WidgetManager.updateWidgets(context, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int x : appWidgetIds) {
            WidgetManager.removeWidgetFromDB(context, x);
        }
        super.onDeleted(context, appWidgetIds);
    }

}
