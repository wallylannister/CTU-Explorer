package com.ctu.ctu_explorer;

import android.app.Activity;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

public class DrawerMenu {
    private static Drawer drawer;

    public static void create(Activity activity) {
        drawer = new DrawerBuilder()
                .withActivity(activity)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIcon(R.drawable.ic_business_black_24dp),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("About")
                )
                .build();
    }

    public static void close() {
        drawer.closeDrawer();
    }

    public static void open() {
        drawer.openDrawer();
    }
}
