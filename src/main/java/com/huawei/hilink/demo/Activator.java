package com.huawei.hilink.demo;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.huawei.hilink.openapi.plugin.PluginListener;


public class Activator implements BundleActivator,PluginListener {//实现插件卸载监听接口 PluginListener
    private static BundleContext contextHandle = null;
    public void start(BundleContext context) throws Exception {
        contextHandle = context;
        System.out.println("demo start.");
    }

    public void stop(BundleContext context) throws Exception {
        contextHandle = null;
        System.out.println("demo stop.");
    }

    @Override
    public long getBundleId() {
        //固定写法，返回该插件bundl id
        return contextHandle.getBundle().getBundleId();
    }

    @Override
    public void unInstall() {
        //当插件卸载时，在此处执行一些恢复默认，还原等操作。
    }
}
