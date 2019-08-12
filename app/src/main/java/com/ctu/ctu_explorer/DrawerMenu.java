package com.ctu.ctu_explorer;

import android.app.Activity;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

public class DrawerMenu {
    private static Drawer drawer;

    public static void create(Activity activity) {
        DrawerBuilder drawerBuilder = new DrawerBuilder();
        drawerBuilder.withActivity(activity);
        drawerBuilder.addDrawerItems(
                new PrimaryDrawerItem().withName("About").withIcon(R.drawable.ic_info_black_24dp),
                new PrimaryDrawerItem().withName("Instructions").withIcon(R.drawable.ic_assignment_black_24dp),
                new PrimaryDrawerItem().withName("Buildings Information").withIcon(R.drawable.ic_business_black_24dp),
                new DividerDrawerItem(),
                new PrimaryDrawerItem().withName("Report Problem").withIcon(R.drawable.ic_report_problem_black_24dp)
        );
        drawerBuilder.withSelectedItem(-1);
        drawerBuilder.withHeader(R.layout.drawer_header);
        drawerBuilder.withStickyFooter(R.layout.drawer_footer);
        drawer = drawerBuilder.build();
    }

    public static void close() {
        drawer.closeDrawer();
    }

    public static void open() {
        drawer.openDrawer();
    }
}
