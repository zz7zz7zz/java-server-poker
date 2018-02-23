package com.open.net.client.object;




import java.util.HashMap;

import com.open.net.base.util.CfgParser;

/**
  # name  : server name 服务器名字
  # id    : server id   服务器id
  # host  : host 主机ip
  # port  : port 端口
  
  # connect_max_count : 最大的连接数
  # connect_backlog   : 客户连接请求队列的长度
         管理客户连接请求的任务是由操作系统来完成的。
         操作系统把这些连接请求存储在一个先进先出的队列中。
         许多操作系统限定了队列的最大长度，一般为50。
         当队列中的连接请求达到了队列的最大容量时，服务器进程所在的主机会拒绝新的连接请求。
         只有当服务器进程通过ServerSocket的accept()方法从队列中取出连接请求，使队列腾出空位时，队列才能继续加入新的连接请求。


 * author       :   long
 * created on   :   2017/11/30
 * description  :   服务器配置
 *
 */

public class ClientConfig {

    //缓存配置
    public int pool_capacity_small;
    public int pool_capacity_middle;
    public int pool_capacity_large;

    public int pool_size_small;
    public int pool_size_middle;
    public int pool_size_large;

    public int pool_max_size_temporary_cache;
  
    //包大小配置
    public int packet_max_length_tcp = 16384;
    public int packet_max_length_udp = 65507;
    
    //解析文件配置参数
    public final void initFileConfig(String config_path) {
        HashMap<String,Object> map = CfgParser.parseToMap(config_path);
        initFileConfig(map);
    }
    
    protected void initFileConfig(HashMap<String,Object> map){
    	if(null !=map){

            pool_capacity_small 	= CfgParser.getInt(map,"pool","pool_capacity_small");
            pool_capacity_middle 	= CfgParser.getInt(map,"pool","pool_capacity_middle");
            pool_capacity_large 	= CfgParser.getInt(map,"pool","pool_capacity_large");

            pool_size_small 		= CfgParser.getInt(map,"pool","pool_size_small");
            pool_size_middle 		= CfgParser.getInt(map,"pool","pool_size_middle");
            pool_size_large 		= CfgParser.getInt(map,"pool","pool_size_large");

            pool_max_size_temporary_cache = CfgParser.getInt(map,"pool","pool_max_size_temporary_cache");
            
            packet_max_length_tcp 	= CfgParser.getInt(map,"packet","packet_max_length_tcp");
            packet_max_length_udp 	= CfgParser.getInt(map,"packet","packet_max_length_udp");
       }
    }

    //-------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "ClientConfig [pool_capacity_small=" + pool_capacity_small + ", pool_capacity_middle="
				+ pool_capacity_middle + ", pool_capacity_large=" + pool_capacity_large + ", pool_size_small="
				+ pool_size_small + ", pool_size_middle=" + pool_size_middle + ", pool_size_large=" + pool_size_large
				+ ", pool_max_size_temporary_cache=" + pool_max_size_temporary_cache + ", packet_max_length_tcp="
				+ packet_max_length_tcp + ", packet_max_length_udp=" + packet_max_length_udp + "]";
	}
  
}