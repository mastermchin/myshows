package ru.myshows.activity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 14.06.2011
 * Time: 19:12:19
 * To change this template use File | Settings | File Templates.
 */
public class ActivityStack extends ActivityGroup {

    private Stack<String> stack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (stack == null) stack = new Stack<String>();
        Class defaultClass = (Class) getBundleValue(getIntent(), "defaultStackClass", null);
        System.out.println("defaultClass = " + defaultClass.getName());
        if (defaultClass != null)
            push("defaultStackClass", new Intent(this, defaultClass));
        else
            //start default activity
            push("SearchFragment", new Intent(this, SearchFragment.class));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);    //To change body of overridden methods use File | Settings | File Templates.
        System.out.println("Activity Stack on new Intent!!!");
    }

    private Object getBundleValue(Intent intent, String key, String defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }

    @Override
    public void finishFromChild(Activity child) {
        pop();
    }

    @Override
    public void onBackPressed() {
        pop();
    }


    public void push(String id, Intent intent) {
        LocalActivityManager manager = getLocalActivityManager();
        if (!stack.isEmpty()) {
            Intent lastIntent = manager.getActivity(stack.peek()).getIntent();
            System.out.println("Intent class = " + intent.getComponent().getClassName());
            System.out.println("last Intent class = " + lastIntent.getComponent().getClassName());
            if (intent.getComponent().getClassName().equals(lastIntent.getComponent().getClassName()))
                manager.destroyActivity(stack.pop(), true);
        }
        Window window = manager.startActivity(id, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        if (window != null) {
            stack.push(id);
            setContentView(window.getDecorView());
        }
    }

    public void pop() {
        if (stack.size() == 1)
            finish();
        LocalActivityManager manager = getLocalActivityManager();
        manager.destroyActivity(stack.pop(), true);
        if (stack.size() > 0) {
            Intent lastIntent = manager.getActivity(stack.peek()).getIntent();
            Window newWindow = manager.startActivity(stack.peek(), lastIntent);
            setContentView(newWindow.getDecorView());
        }
    }

   
}


