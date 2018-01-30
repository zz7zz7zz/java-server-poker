package com.poker.monitor;

import java.io.IOException;

import com.open.net.server.GServer;
import com.open.net.server.impl.udp.nio.UdpNioClient;
import com.open.net.server.impl.udp.nio.UdpNioServer;
import com.open.net.server.object.ArgsConfig;
import com.open.net.server.object.ServerConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;
import com.open.util.log.Logger;
import com.open.util.log.base.LogConfig;
import com.poker.base.ServerIds;
import com.poker.common.packet.PacketTransfer;
import com.poker.monitor.handler.ServerHandler;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :  服务器入口
 */

public class Main {

    public static void main(String [] args){

        //----------------------------------------- 一、配置初始化 ------------------------------------------
    	//1.1 服务器配置初始化:解析命令行参数
    	ArgsConfig libArgsConfig = new ArgsConfig();
    	libArgsConfig.initArgsConfig(args);
    	libArgsConfig.server_type = ServerIds.SERVER_ACCESS;
    	
    	//1.2 服务器配置初始化:解析文件配置
        ServerConfig libServerConfig = new ServerConfig();
        libServerConfig.initArgsConfig(libArgsConfig);
        libServerConfig.initFileConfig("./conf/lib.server.config");
        
        //1.3 日志配置初始化
        LogConfig libLogConfig = Logger.init("./conf/lib.log.config",libArgsConfig.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        
        //-----------------------------------------初始化全局属性-----------------------------------------------
    	PacketTransfer.init(libArgsConfig.server_type, libArgsConfig.id);
    	
        Logger.v("libArgsConfig: "+ libArgsConfig.toString()+"\r\n");
        Logger.v("libServerConfig: "+ libServerConfig.toString()+"\r\n");
        Logger.v("libLogConfig: "+ libLogConfig.toString()+"\r\n");
        
        //----------------------------------------- 三、服务器初始化 ------------------------------------------
        Logger.v("-------Server------start---------");
        try {
            //3.1 数据初始化
            GServer.init(libServerConfig, UdpNioClient.class);
            
            //3.2 服务器初始化
            UdpNioServer mBioServer = new UdpNioServer(libServerConfig,new ServerHandler(new InPacket(libServerConfig.packet_max_length_tcp), new OutPacket(libServerConfig.packet_max_length_tcp)),mLogListener);
            mBioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.v("-------Server------end---------");
        
        //----------------------------------------- 五、最终退出程序 ---------------------------------------
        System.exit(0);
    }

    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
}
