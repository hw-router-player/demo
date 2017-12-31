package com.huawei.hilink.demo;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.huawei.hilink.openapi.plugin.PluginListener;


public class Activator implements BundleActivator,PluginListener {//ʵ�ֲ��ж�ؼ����ӿ� PluginListener
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
        //�̶�д�������ظò��bundl id
        return contextHandle.getBundle().getBundleId();
    }

    @Override
    public void unInstall() {
        //�����ж��ʱ���ڴ˴�ִ��һЩ�ָ�Ĭ�ϣ���ԭ�Ȳ�����
    }
}
