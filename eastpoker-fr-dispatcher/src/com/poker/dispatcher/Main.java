package com.poker.dispatcher;

import java.io.IOException;

import com.open.net.server.GServer;
import com.open.net.server.impl.tcp.nio.NioServer;
import com.open.net.server.object.ArgsConfig;
import com.open.net.server.object.ServerConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;
import com.open.net.server.utils.NetUtil;
import com.open.util.log.Logger;
import com.open.util.log.base.LogConfig;
import com.poker.base.ServerIds;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.dispatcher.handler.MessageHandler;
import com.poker.dispatcher.handler.ServerMessageProcessor;
import com.poker.protocols.Monitor;


/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :  服务器入口
 */

public class Main {

    public static void main(String [] args){
    	
        //----------------------------------------- 一、配置初始化 ------------------------------------------
    	//1.1 服务器配置初始化:解析命令行参数
    	libArgsConfig = new ArgsConfig();
    	libArgsConfig.initArgsConfig(args);
    	libArgsConfig.server_type = ServerIds.SERVER_DIAPATCHER;
    	
    	//1.2 服务器配置初始化:解析文件配置
        ServerConfig libServerConfig = new ServerConfig();
        libServerConfig.initArgsConfig(libArgsConfig);
        libServerConfig.initFileConfig("./conf/lib.server.config");
        
        //1.3 日志配置初始化
        LogConfig libLogConfig = Logger.init("./conf/lib.log.config",libArgsConfig.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        
        //1.4 业务配置初始化
        Config mServerConfig = new Config();
        mServerConfig.initFileConfig("./conf/server.config");
        
        //-----------------------------------------初始化全局属性-----------------------------------------------
        initGlobalFields(libServerConfig.packet_max_length_tcp);
        
        Logger.v("libArgsConfig  : "+ libArgsConfig.toString()+"\r\n");
        Logger.v("libServerConfig: "+ libServerConfig.toString()+"\r\n");
        Logger.v("libLogConfig   : "+ libLogConfig.toString()+"\r\n");
        Logger.v("mServerConfig  : "+ mServerConfig.toString()+"\r\n");
        
        //----------------------------------------- 二、注册到关联服务器 ---------------------------------------
        register_monitor(mServerConfig);//注册到服务监听器
    	
        //----------------------------------------- 三、服务器初始化 ------------------------------------------
    	Logger.v("-------Server------start---------");
        try {
            //3.1 数据初始化
            GServer.init(libServerConfig, com.open.net.server.impl.tcp.nio.NioClient.class);
            
            //3.2 服务器初始化
            NioServer mNioServer = new NioServer(libServerConfig,new ServerMessageProcessor(new MessageHandler(),write_buff),mLogListener);
            mNioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        Logger.v("-------Server------end---------");
        
        //----------------------------------------- 四、反注册关联服务器 ---------------------------------------
        unregister_monitor(mServerConfig);//反注册到服务监听器
        
        //----------------------------------------- 五、最终退出程序 ---------------------------------------
        System.exit(0);
    }

    
    //---------------------------------------Fields----------------------------------------------------
    public static ArgsConfig libArgsConfig;
    private static byte[] write_buff ;
    
    //---------------------------------------Logger----------------------------------------------------
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
    
    //---------------------------------------初始化全局对象----------------------------------------------------
    private static void initGlobalFields(int packet_max_length_tcp){
    	write_buff = new byte[packet_max_length_tcp];
    }
    
    //---------------------------------------Monitor----------------------------------------------------
    public static void register_monitor(Config mConfig){
        Monitor.register2Monitor(write_buff,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,write_buff,0,DataPacket.getLength(write_buff));
    		}
    	}
    }
    
    public static void unregister_monitor(Config mConfig){
    	Monitor.unregister2Monitor(write_buff,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,write_buff,0,DataPacket.getLength(write_buff));
    		}
    	}
    }
}
