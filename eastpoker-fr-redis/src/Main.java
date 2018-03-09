import redis.clients.jedis.Jedis;

public class Main {
	
	
	public static void main(String [] args) throws InterruptedException{
		Jedis jedis = new Jedis("localhost");
		System.out.println("连接成功");
		jedis.auth("psd");
		System.out.println("服务正在运行: "+jedis.ping());
		jedis.close();
	}
}
