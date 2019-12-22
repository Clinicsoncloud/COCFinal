/*
.
 * A sliding menu for Android, very much like the Google+ and Facebook apps have.
 *
 * Copyright (C) 2012 CoboltForge
 *
 * Based upon the great work done by stackoverflow user Scirocco (http://stackoverflow.com/a/11367825/361413), thanks a lot!
 * The XML parsing code comes from https://github.com/darvds/RibbonMenu, thanks!
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abhaybmi.app.slidemenu;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.abhaybmi.app.R;
import com.abhaybmi.app.hemoglobin.MainActivity;
import com.abhaybmi.app.printer.esys.pridedemoapp.Act_Main;

public class SlideMenu extends LinearLayout {

    // keys for saving/restoring instance state
    private final static String KEY_MENUSHOWN = "menuWasShown";
    private final static String KEY_STATUSBARHEIGHT = "statusBarHeight";
    private final static String KEY_SUPERSTATE = "superState";

    LinearLayout layout;

    // String versionName =
    // context.getPackageManager().getPackageInfo(context.getPackageName(),
    // 0).versionName;

    public static class SlideMenuItem {
        public int id;
        public Drawable icon;
        public String label;

    }

    // this tells whether the menu is currently shown
    String typeOfUser = "";
    private boolean menuIsShown = false;
    // this just tells whether the menu was ever shown
    private boolean menuWasShown = false;
    private int statusHeight = -1;
    private static View menu;
    private static ViewGroup content;
    private static FrameLayout parent;
    private static int menuSize;
    private Activity act;
    String userName_;
    String userType_;
    private Drawable headerImage;
    private Typeface font;
    private TranslateAnimation slideRightAnim;
    private TranslateAnimation slideMenuLeftAnim;
    private TranslateAnimation slideContentLeftAnim;

    private ArrayList<SlideMenuItem> mMenuItemList;
    private SlideMenuInterface.OnSlideMenuItemClickListener mCallback;
    String addNewUserVisiblity = "";
    String manageUserVisibility = "";
    String fromManageVisibility = "";

    private LinearLayout mMenuManageUserLayout;
    private LinearLayout mMenuDashboardLayout;
    private LinearLayout mMenuDeviceSetupLayout;
    private LinearLayout mMenull_menu_printer_Setup;
    private LinearLayout ll_menu_hemoglobin_Setup;
    private LinearLayout ll_menu_glucose_Setup;
    private LinearLayout mMenuReminderLayout;
    private LinearLayout mMenuSettingsLayout;
    private LinearLayout mMenuHelpLayout;
    private LinearLayout mMenuSignoutLayout;

    private TextView mMenuVersionView;

//	private LinearLayout mMenuAboutLayout;
//	private LinearLayout mMenuAdduserLayout;
//	private LinearLayout ll_menu_tutorial;


    /**
     * Constructor used by the inflation apparatus. To be able to use the
     * SlideMenu, call the {@link #init init()} method.
     *
     * @param context
     */
    public SlideMenu(Context context) {
        super(context);
    }

    /**
     * Constructor used by the inflation apparatus. To be able to use the
     * SlideMenu, call the {@link #init init()} method.
     *
     * @param attrs
     */
    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructs a SlideMenu with the given menu XML.
     *
     * @param act           The calling activity.
     * @param menuResource  Menu resource identifier.
     * @param cb            Callback to be invoked on menu item click.
     * @param slideDuration Slide in/out duration in milliseconds.
     */
    public SlideMenu(Activity act, int menuResource,
                     SlideMenuInterface.OnSlideMenuItemClickListener cb,
                     int slideDuration, String userName, String userType,
                     String addNewUser, String manageUser, String fromManageVisibility) {
        super(act);
        init(act, cb, slideDuration, userName, userType, addNewUser,
                manageUser, fromManageVisibility);
    }

    /**
     * Constructs an empty SlideMenu.
     *
     * @param act           The calling activity.
     * @param cb            Callback to be invoked on menu item click.
     * @param slideDuration Slide in/out duration in milliseconds.
     */
    public SlideMenu(Activity act,
                     SlideMenuInterface.OnSlideMenuItemClickListener cb,
                     int slideDuration, String userName, String userType,
                     String addNewUser, String manageUser, String fromManageVisibility) {
        this(act, 0, cb, slideDuration, userName, userType, addNewUser,
                manageUser, fromManageVisibility);
    }

    /**
     * If inflated from XML, initializes the SlideMenu.
     *
     * @param act           The calling activity.
     * @param menuResource  Menu resource identifier, can be 0 for an empty SlideMenu.
     * @param cb            Callback to be invoked on menu item click.
     * @param slideDuration Slide in/out duration in milliseconds.
     */
    public void init(Activity act,
                     SlideMenuInterface.OnSlideMenuItemClickListener cb,
                     int slideDuration, String userName, String userType,
                     String addNewUser, String manageUser, String frommanagevisibility) {

        this.act = act;
        this.mCallback = cb;
        this.userName_ = userName;
        this.userType_ = userType;
        this.addNewUserVisiblity = addNewUser;
        this.manageUserVisibility = manageUser;
        this.fromManageVisibility = frommanagevisibility;
        // set size

        int displayWidth = ((WindowManager) act
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        int tempsWidth = (int) (displayWidth * .3f);
        menuSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                300, act.getResources().getDisplayMetrics());

        // create animations accordingly
        slideRightAnim = new TranslateAnimation(-menuSize, 0, 0, 0);
        slideRightAnim.setFillAfter(true);
        slideMenuLeftAnim = new TranslateAnimation(0, -menuSize, 0, 0);
        slideMenuLeftAnim.setFillAfter(true);
        slideContentLeftAnim = new TranslateAnimation(menuSize, 0, 0, 0);
        slideContentLeftAnim.setFillAfter(true);
        setAnimationDuration(slideDuration);
        // and get our menu
        // parseXml(menuResource);
    }

    @SuppressLint("NewApi")
    public void init(Activity act,
                     SlideMenuInterface.OnSlideMenuItemClickListener cb,
                     int slideDuration, String userName, String userType,
                     String addNewUser, String manageUser, String frommanagevisibility, ViewGroup viewgroup) {

        this.act = act;
        this.mCallback = cb;
        this.userName_ = userName;
        this.userType_ = userType;
        this.addNewUserVisiblity = addNewUser;
        this.manageUserVisibility = manageUser;
        this.fromManageVisibility = frommanagevisibility;
        // set size

        int displayWidth = ((WindowManager) act
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        int tempsWidth = (int) (displayWidth * .3f);
        menuSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                300, act.getResources().getDisplayMetrics());

        // create animations accordingly
        slideRightAnim = new TranslateAnimation(-menuSize, 0, 0, 0);
        slideRightAnim.setFillAfter(true);
        slideMenuLeftAnim = new TranslateAnimation(0, -menuSize, 0, 0);
        slideMenuLeftAnim.setFillAfter(true);
        slideContentLeftAnim = new TranslateAnimation(menuSize, 0, 0, 0);
        slideContentLeftAnim.setFillAfter(true);
        setAnimationDuration(slideDuration);
        // and get our menu
        // parseXml(menuResource);

        viewgroup.setOnTouchListener(new SidemenuSwipeTouchListener());
    }


    /**
     * Set how long slide animation should be
     *
     * @param slideDuration How long to set the slide animation
     * @see TranslateAnimation#setDuration(long)
     */
    public void setAnimationDuration(long slideDuration) {
        slideRightAnim.setDuration(slideDuration);
        slideMenuLeftAnim.setDuration(slideDuration * 3 / 2);
        slideContentLeftAnim.setDuration(slideDuration * 3 / 2);
    }

    /**
     * Set an Interpolator for the slide animation.
     *
     * @param i The {@link Interpolator} object to set.
     * @see TranslateAnimation#setInterpolator(Interpolator)
     */
    public void setAnimationInterpolator(Interpolator i) {
        slideRightAnim.setInterpolator(i);
        slideMenuLeftAnim.setInterpolator(i);
        slideContentLeftAnim.setInterpolator(i);
    }

    /**
     * Sets an optional image to be displayed on top of the menu.
     *
     * @param d
     */
    public void setHeaderImage(Drawable d) {
        headerImage = d;
    }

    /**
     * Optionally sets the font for the menu items.
     *
     * @param f A font.
     */
    public void setFont(Typeface f) {
        font = f;
    }

    /**
     * Dynamically adds a menu item.
     *
     * @param item
     */
    public void addMenuItem(SlideMenuItem item) {
        mMenuItemList.add(item);
    }

    /**
     * Empties the SlideMenu.
     */
    public void clearMenuItems() {
        mMenuItemList.clear();
    }

    /**
     * Slide the menu in.
     */
    public void show() {
        this.show(true);
    }

    /**
     * Set the menu to shown status without displaying any slide animation.
     */
    public void setAsShown() {
        this.show(false);
    }

    @SuppressLint("NewApi")
    private void show(boolean animate) {

        /*
         * We have to adopt to status bar height in most cases, but not if there
         * is a support actionbar!
         */
        try {
            Method getSupportActionBar = act.getClass().getMethod(
                    "getSupportActionBar", (Class[]) null);
            Object sab = getSupportActionBar.invoke(act, (Object[]) null);
            sab.toString(); // check for null

            if (Build.VERSION.SDK_INT >= 11) {
                // over api level 11? add the margin
                getStatusbarHeight();
            }
        } catch (Exception es) {
            // there is no support action bar!
            getStatusbarHeight();
        }

        // modify content layout params
        try {
            content = ((LinearLayout) act.findViewById(android.R.id.content)
                    .getParent());
        } catch (ClassCastException e) {
            /*
             * When there is no title bar
             * (android:theme="@android:style/Theme.NoTitleBar"), the
             * android.R.id.content FrameLayout is directly attached to the
             * DecorView, without the intermediate LinearLayout that holds the
             * titlebar plus content.
             */
            // content = (FrameLayout) act.findViewById(android.R.id.content);

            if (Build.VERSION.SDK_INT < 18)
                content = (ViewGroup) act.findViewById(android.R.id.content);
            else
                content = (ViewGroup) act.findViewById(android.R.id.content)
                        .getParent(); // FIXME? what about the corner cases
            // (fullscreen etc)
        }
        FrameLayout.LayoutParams parm = new FrameLayout.LayoutParams(-1, -1, 3);
        parm.setMargins(menuSize, 0, -menuSize, 0);
        content.setLayoutParams(parm);

        // animation for smooth slide-out
        if (animate)
            content.startAnimation(slideRightAnim);

        // quirk for sony xperia devices on ICS only, shouldn't hurt on others
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 15 && Build.MANUFACTURER.contains("Sony") && menuWasShown)
            content.setX(menuSize);

        // add the slide menu to parent
        try {
            parent = (FrameLayout) content.getParent();
        } catch (ClassCastException e) {
            /*
             * Most probably a LinearLayout, at least on Galaxy S3.
             * https://github.com/bk138/LibSlideMenu/issues/12
             */
            LinearLayout realParent = (LinearLayout) content.getParent();
            parent = new FrameLayout(act);
            realParent.addView(parent, 0); // add FrameLayout to real parent of
            // content
            realParent.removeView(content); // remove content from real parent
            parent.addView(content); // add content to FrameLayout
        }

        LayoutInflater inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menu = inflater.inflate(R.layout.slidemenu_new, null);
        FrameLayout.LayoutParams lays = new FrameLayout.LayoutParams(-1, -1, 3);
        lays.setMargins(0, statusHeight, 0, 0);
        menu.setLayoutParams(lays);
        parent.addView(menu);
        // slide menu in
        if (animate)
            menu.startAnimation(slideRightAnim);

        menu.findViewById(R.id.overlay).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SlideMenu.this.hide();
                    }
                });
        enableDisableViewGroup(content, false);
        bindItem();

        menuIsShown = true;
        menuWasShown = true;

    }

    @SuppressLint("NewApi")
    private void bindItem() {


        mMenuDashboardLayout = (LinearLayout) act.findViewById(R.id.ll_menu_dashboard);

        mMenuDeviceSetupLayout = (LinearLayout) act.findViewById(R.id.ll_menu_device_setup);

        mMenull_menu_printer_Setup = (LinearLayout) act.findViewById(R.id.ll_menu_printer_Setup);

        ll_menu_glucose_Setup = (LinearLayout) act.findViewById(R.id.ll_menu_glucose_Setup);

        mMenuSignoutLayout = (LinearLayout) act.findViewById(R.id.ll_menu_signout);
        ll_menu_hemoglobin_Setup = (LinearLayout) act.findViewById(R.id.ll_menu_hemoglobin_Setup);

        mMenuVersionView = (TextView) act.findViewById(R.id.version);

        String versionName = "";
        try {
            versionName = this.act
                    .getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(
                            this.act.getApplicationContext().getPackageName(),
                            0).versionName;
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }

        mMenuVersionView.setText(act.getString(R.string.side_menu_version) + "  " + versionName);

/** ******************************************************************************************
 * リスナー
 ********************************************************************************************* */

        // Main DashBoard Menu
        mMenuDashboardLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) mCallback.onSlideMenuItemClick(2);
                hide(false);
            }
        });

        // Device Setup Menu
        mMenuDeviceSetupLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) mCallback.onSlideMenuItemClick(3);
                hide(false);
            }
        });

        mMenull_menu_printer_Setup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) mCallback.onSlideMenuItemClick(4);
                hide(false);
            }
        });
        ll_menu_hemoglobin_Setup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) mCallback.onSlideMenuItemClick(5);
                hide(false);
            }
        });

        ll_menu_glucose_Setup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) mCallback.onSlideMenuItemClick(6);
                hide(false);
            }
        });

        // Logout Menu
        mMenuSignoutLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        act);

                // set title
                Dialog dialog = new Dialog(act);
                dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
                // set dialog message

                alertDialogBuilder
                        .setMessage(act.getString(R.string.side_menu_message_signout))
                        .setCancelable(false)
                        .setPositiveButton(act.getString(R.string.yes),
                                (dialog1, id) -> {
                                    // if this button is clicked, close
                                    // current activity
                                    if (mCallback != null) mCallback.onSlideMenuItemClick(8);
                                    hide(false);
                                    Intent objIntent = new Intent(getContext(), MainActivity.class);
                                    getContext().startActivity(objIntent);
                                })
                        .setNegativeButton(act.getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.dismiss();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
    }

    /**
     * Animated Slide the menu out.
     */
    @SuppressLint("NewApi")
    public void hide() {
        hide(true);
    }

    /**
     * Slide the menu out.
     *
     * @param isAnimated
     */
    @SuppressLint("NewApi")
    public void hide(boolean isAnimated) {
        if (isAnimated) {
            menu.startAnimation(slideMenuLeftAnim);
            content.startAnimation(slideContentLeftAnim);
        }
        parent.removeView(menu);

        FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) content
                .getLayoutParams();
        parm.setMargins(0, 0, 0, 0);
        content.setLayoutParams(parm);
        enableDisableViewGroup(content, true);

        // quirk for sony xperia devices on ICS only, shouldn't hurt on others
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 15 && Build.MANUFACTURER.contains("Sony"))
            content.setX(0);

        menuIsShown = false;
    }

    private void getStatusbarHeight() {
        // Only do this if not already set.
        // Especially when called from within onCreate(), this does not return
        // the true values.
        if (statusHeight == -1) {
            Rect r = new Rect();
            Window window = act.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(r);
            statusHeight = r.top;
        }
    }

    // originally:
    // http://stackoverflow.com/questions/5418510/disable-the-touch-events-for-all-the-views
    // modified for the needs here
    private void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view.isFocusable())
                view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            } else if (view instanceof ListView) {
                if (view.isFocusable())
                    view.setEnabled(enabled);
                ListView listView = (ListView) view;
                int listChildCount = listView.getChildCount();
                for (int j = 0; j < listChildCount; j++) {
                    if (view.isFocusable())
                        listView.getChildAt(j).setEnabled(false);
                }
            }
        }
    }

    // originally: https://github.com/darvds/RibbonMenu
    // credit where credits due!
    private void parseXml(int menu) {

        mMenuItemList = new ArrayList<SlideMenuItem>();

        // use 0 id to indicate no menu (as specified in JavaDoc)
        if (menu == 0)
            return;

        try {
            XmlResourceParser xpp = act.getResources().getXml(menu);

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {

                    String elemName = xpp.getName();

                    if (elemName.equals("item")) {

                        String textId = xpp.getAttributeValue(
                                "http://schemas.android.com/apk/res/android",
                                "title");
                        String iconId = xpp.getAttributeValue(
                                "http://schemas.android.com/apk/res/android",
                                "icon");
                        String resId = xpp.getAttributeValue(
                                "http://schemas.android.com/apk/res/android",
                                "id");

                        SlideMenuItem item = new SlideMenuItem();
                        item.id = Integer.valueOf(resId.replace("@", ""));
                        if (iconId != null) {
                            item.icon = act.getResources().getDrawable(
                                    Integer.valueOf(iconId.replace("@", "")));
                        }
                        item.label = resourceIdToString(textId);

                        mMenuItemList.add(item);
                    }

                }

                eventType = xpp.next();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String resourceIdToString(String text) {
        if (!text.contains("@")) {
            return text;
        } else {
            String id = text.replace("@", "");
            return act.getResources().getString(Integer.valueOf(id));

        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {

            if (state instanceof Bundle) {
                Bundle bundle = (Bundle) state;

                statusHeight = bundle.getInt(KEY_STATUSBARHEIGHT);

                if (bundle.getBoolean(KEY_MENUSHOWN))
                    show(false); // show without animation

                super.onRestoreInstanceState(bundle
                        .getParcelable(KEY_SUPERSTATE));

                return;
            }

            super.onRestoreInstanceState(state);

        } catch (NullPointerException e) {
            // in case the menu was not declared via XML but added from code
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SUPERSTATE, super.onSaveInstanceState());
        bundle.putBoolean(KEY_MENUSHOWN, menuIsShown);
        bundle.putInt(KEY_STATUSBARHEIGHT, statusHeight);

        return bundle;
    }

    @SuppressLint("New Class")
    public class SidemenuSwipeTouchListener implements OnTouchListener {
        // 左端として扱う、有効範囲
        private static final float leftEdgeArea = 120.0f;
        // 移動中の上下の遊び幅
        private static final float verticalMoveMargin = 20.0f;
        // スワイプだと判断する距離
        private static final float swipeDistance = 40.0f;

        // 最後にタッチされた座標
        private float startTouchX;
        private float startTouchY;
        // スワイプ中判定
        private boolean isSwipping = false;

        public SidemenuSwipeTouchListener() {
            super();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // 指が1本以上タッチされた場合、スワイプ判定を中止する
            if (event.getPointerCount() > 1) {
                isSwipping = false;
                return true;
            }

            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                isSwipping = false;
                if (event.getX() < leftEdgeArea) {
                    startTouchX = event.getX();
                    startTouchY = event.getY();
                    isSwipping = true;
                }
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (isSwipping) {
                    float moveX = event.getX() - startTouchX;
                    float moveY = event.getY() - startTouchY;

                    if (moveX < 0 || moveY < -verticalMoveMargin || verticalMoveMargin < moveY) {
                        isSwipping = false;
                    } else if (swipeDistance <= moveX) {
                        isSwipping = false;
                        show();
                        return false;
                    }

                }
            }

            return true;
        }
    }

    public boolean isShown() {
        return menuIsShown;
    }
}
