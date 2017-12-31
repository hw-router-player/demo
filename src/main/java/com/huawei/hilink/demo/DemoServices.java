package com.huawei.hilink.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.huawei.hilink.device.cmd.HiLinkDeviceCmdUtil;
import com.huawei.hilink.device.cmd.HiLinkDeviceCommand;
import com.huawei.hilink.device.data.HiLinkDeviceData;
import com.huawei.hilink.device.data.HiLinkServiceData;
import com.huawei.hilink.device.info.HiLinkDevice;
import com.huawei.hilink.device.info.HiLinkDeviceInfo;
import com.huawei.hilink.device.info.HiLinkServiceInfo;
import com.huawei.hilink.device.uid.HiLinkDeviceUID;
import com.huawei.hilink.event.Event;
import com.huawei.hilink.event.EventFilter;
import com.huawei.hilink.event.EventSubscriber;
import com.huawei.hilink.openapi.device.DeviceEventListner;
import com.huawei.hilink.openapi.device.HiLinkDeviceMngr;
import com.huawei.hilink.openapi.nativelauncher.LaunchedProcess;
import com.huawei.hilink.openapi.nativelauncher.NativeLauncher;
import com.huawei.hilink.openapi.nativelauncher.NativeLauncherCallback;
import com.huawei.hilink.openapi.rest.Restful;
import com.huawei.hilink.openapi.system.BandwidthInfo;
import com.huawei.hilink.openapi.system.SystemInfo;
import com.huawei.hilink.openapi.system.event.SystemRebootEvent;
import com.huawei.hilink.openapi.system.event.SystemRestoreEvent;
import com.huawei.hilink.openapi.system.event.WANDownEvent;
import com.huawei.hilink.openapi.system.event.WANUpEvent;
import com.huawei.hilink.openapi.usbstorage.USBDiskVolume;
import com.huawei.hilink.openapi.usbstorage.USBStorage;
import com.huawei.hilink.openapi.usbstorage.event.USBAddedEvent;
import com.huawei.hilink.openapi.usbstorage.event.USBRemovedEvent;
import com.huawei.hilink.util.CollectionUtil;
import com.huawei.hilink.util.LibraryUtil;
import com.huawei.hilink.util.Logger;
import com.huawei.hilink.util.LoggerFactory;

public class DemoServices implements EventSubscriber,DeviceEventListner{
    
    //��ȡ����ϵͳ��Ϣ�ӿ�
    private SystemInfo systemInfo = null;
    
    //�豸����ӿ�
    private HiLinkDeviceMngr hilinkDeviceMngr = null;
    
    //native���̹���ӿ�
    private NativeLauncher nativeLauncher = null;
    
    //restful�ӿڵ��ýӿ�
    private Restful restful = null;
    
    //USB��Ϣ��ȡ�ӿ�
    private USBStorage usbStorage = null;
    
    //native����ؽӿ�
    private LibraryUtil libraryUtil = new LibraryUtil();
    
    //��־��¼�ӿ�
    private Logger logger = LoggerFactory.getLogger(DemoServices.class.getSimpleName());
    
	// �������ĵ��¼�
	private final Set<String> eventTypes = CollectionUtil
			.newUnmodifiableSet(new String[] { USBAddedEvent.TYPE,
					USBRemovedEvent.TYPE, WANUpEvent.TYPE, WANDownEvent.TYPE, SystemRestoreEvent.TYPE, SystemRebootEvent.TYPE});

	@Override
	public Set<String> getSubscribedEventTypes() {
		return this.eventTypes;
	}
	
	@Override
	public EventFilter getEventFilter() {
		return eventFilter;
	}
	
	//�¼�������
    private EventFilter eventFilter = new EventFilter() {
        public boolean apply(Event event) {
            if (eventTypes.contains(event.getType())) {
                return true;
            }
            return false;
        }
    };
	
	//�����¼�������
	@Override
	public void receive(Event paramEvent) {
		if (paramEvent.getType().equals(USBAddedEvent.TYPE)) {
			System.out.println("USB added.");
		} else if (paramEvent.getType().equals(USBRemovedEvent.TYPE)) {
		    System.out.println("USB removed.");
		} else if (paramEvent.getType().equals(WANUpEvent.TYPE)) {
		    System.out.println("wan up.");
        } else if (paramEvent.getType().equals(WANDownEvent.TYPE)) {
            System.out.println("wan down.");
        } else if (paramEvent.getType().equals(SystemRestoreEvent.TYPE)) {
            System.out.println("system restore.");
        } else if (paramEvent.getType().equals(SystemRebootEvent.TYPE)) {
            System.out.println("system reboot.");
        } else {
			System.out.println("other event.");
		}
	}

	protected void activate() {
	    logger.info("demo activate.");
		
		/**
		 * ��ȡ���ϵͳ��Ϣ
		 */
		BandwidthInfo bandwidthInfo = systemInfo.getBandwidthInfo();
		systemInfo.getDeviceInfo();
		systemInfo.getHostInfos();
		systemInfo.getLANHostInfo();
		systemInfo.getWANInfo();
		systemInfo.getWLANInfos();
	      
        /**
         * �豸����
         */
		//����豸������
        hilinkDeviceMngr.addDeviceListner(this);
        hilinkDeviceMngr.addDeviceListnerByDevTypes("devType1,devType2,devType3", this);
        hilinkDeviceMngr.addDeviceListnerBySerTypes("serType1,serType2,serType3,serType4", this);
        
        HiLinkDeviceUID uid = new HiLinkDeviceUID("devType2:005:5:0000FC21118273645171041120370000");
        
        //��ȡ�豸
        List<HiLinkDevice> allDevices = hilinkDeviceMngr.getAllDevices();
        List<HiLinkDevice> devicesByDevType = hilinkDeviceMngr.getDevicesByDevType("devType1");
        List<HiLinkDevice> devicesBySerType = hilinkDeviceMngr.getDevicesBySerType("serType3");
        HiLinkDevice device = hilinkDeviceMngr.getDevice(uid);
        
        //��ȡ�豸����
        HiLinkDeviceData deviceData = hilinkDeviceMngr.getDeviceData(uid);
        HiLinkServiceData serviceData = hilinkDeviceMngr.getDeviceDataBySerID(uid, "serId1");
        
        //�����豸
        List<HiLinkServiceData> bodys = new ArrayList<HiLinkServiceData>();
        HiLinkServiceData cmdbody = new HiLinkServiceData("serId");
        cmdbody.addCharacteristic("������", "����ֵ");
        bodys.add(cmdbody);
        HiLinkDeviceData devicedata = new HiLinkDeviceData(uid, bodys);
        HiLinkDeviceCommand postcmd = new HiLinkDeviceCommand(HiLinkDeviceCmdUtil.ACTION_POST, null, devicedata);
        String response =  hilinkDeviceMngr.command(uid, postcmd);
		
		/**
		 * native���̹���
		 */
        //����native���̣������������˳�
        LaunchedProcess  process1 = nativeLauncher.launch("demobin", new String[]{"args1","args2","args3"});
	    //����native���̣����������˳�
        LaunchedProcess  process2 = nativeLauncher.launch("demobin", new String[]{"args1","args2","args3"}, new NativeLauncherCallback() {
            @Override
            public void onLaunchEvent(int status) {
                if(-1 == status){
                    //�����˳�
                }
            }
        });
        
		/**
		 * restful�ӿڵ���
		 */
        String responseGet = restful.request("GET", "/api/xxx/xxx", null);
        String responsePost = restful.request("POST", "/api/xxx/xxx", "{\"action\":\"create\",\"data\":{\"name1\":\"data1\",\"name2\":\"data2\"}}");
        
		/**
		 * ��ȡUSB��Ϣ
		 */
		List<USBDiskVolume> usbInfo = usbStorage.getDiskVolumes();
		
		/**
         * ��־��¼
         * ÿһ����־������ʹ��{}�������ķ�ʽ
         */
        logger.error("xxxxxx error {}!", "dddd");
        logger.debug("xxx {}.", "args1");
        logger.warn("xxx {} xx {} xxx.", "args1","args2");
        logger.info("xxxxxx.");
        
		/**
		 * ����native��
		 */
		String libPath = libraryUtil.getLibPath("demo");//���ļ�Ϊlibdemo.so
		System.load(libPath);
	}

	protected void deactivate() {
	    logger.info("demo deactivate.");
	}

	// ��ȡ SystemInfo������.
	protected void setSystemInfo(SystemInfo systemInfo) {
	    this.systemInfo = systemInfo;
	}

	// ע�� SystemInfo������.
	protected void unsetSystemInfo(SystemInfo systemInfo) {
	    this.systemInfo = null;
	}
	
    //��ȡUSBStorage������
    protected void setUSBStorage(USBStorage usbStorage) {
            this.usbStorage = usbStorage;
    }

    //ע��USBStorage������
    protected void unsetUSBStorage(USBStorage usbStorage) {
            this.usbStorage = null;
    }
    
    //��ȡNativeLauncher������
    protected void setNativeLauncher(NativeLauncher nativeLauncher) {
        this.nativeLauncher = nativeLauncher;
    }

    //ע��NativeLauncher������
    protected void unsetNativeLauncher(NativeLauncher nativeLauncher) {
        this.nativeLauncher = null;
    }
    
    //��ȡRestful������
    protected void setRestful(Restful restful) {
        this.restful = restful;
    }

    //ע��Restful������
    protected void unsetRestful(Restful restful) {
        this.restful = null;
    }
    
    //��ȡHiLinkDeviceMngr������
    protected void setHiLinkDeviceMngr(HiLinkDeviceMngr hilinkDeviceMngr) {
        this.hilinkDeviceMngr = hilinkDeviceMngr;
    }

    //ע��HiLinkDeviceMngr������
    protected void unsetHiLinkDeviceMngr(HiLinkDeviceMngr hilinkDeviceMngr) {
        this.hilinkDeviceMngr = null;
    }

    @Override
    public void onDeviceAdded(HiLinkDevice device) {
        HiLinkDeviceUID devUID = device.getDevUID();
        HiLinkDeviceInfo devInfo = device.getDeviceInfo();
        List<HiLinkServiceInfo> serviceInfos = device.getServiceInfos();
        
    }

    @Override
    public void onDeviceRemoved(HiLinkDeviceUID uid) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDeviceOnline(HiLinkDeviceUID uid) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDeviceOffline(HiLinkDeviceUID uid) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDeviceData(HiLinkDeviceData data) {
        HiLinkDeviceUID devUID = data.getDevUID();
        List<HiLinkServiceData> serviceDatas = data.getServiceDatas();
        
    }
}
