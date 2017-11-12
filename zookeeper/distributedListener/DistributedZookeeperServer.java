package com.zyx.zookeeper.distributedListener;

import java.io.IOException;

import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.zyx.zookeeper.ZookeeperClient;

public class DistributedZookeeperServer {
	private static final String connectionString="192.168.132.198,192.168.132.199,192.168.132.200";
	private static final int sessionTimeout=2000;
	private ZooKeeper  zk=null;
	private String parentNode="/servers";
	
	//获取链接
	public void getconnect() throws IOException{
		zk=new ZooKeeper(connectionString, sessionTimeout, new Watcher(){

			@Override
			public void process(WatchedEvent event) {	
				
			}
			
		});
		System.out.println(zk);
	}
	//注册服务
	public void register(String hostName) throws KeeperException, InterruptedException{
		if(zk.exists(parentNode, false)==null){
			zk.create(parentNode, "server".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		zk.create(parentNode+"/server"+hostName, hostName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	//启动业务
	public void handleBussiness(String hostName) throws InterruptedException{
		System.out.println(hostName+ "starting working");
		Thread.sleep(Long.MAX_VALUE);
	}
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		//获取zk链接
		DistributedZookeeperServer server=new DistributedZookeeperServer();
		server.getconnect();
		//利用zk链接注册服务器信息
		server.register(args[0]);
		//启动业务功能
		server.handleBussiness(args[0]);
		
	}
}
