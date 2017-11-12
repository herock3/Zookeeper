package com.zyx.zookeeper.distributedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZooKeeperSaslClient.ServerSaslResponseCallback;
import org.omg.PortableInterceptor.DISCARDING;

public class DistributedZookeeperClient {
	
	private static final String connectionString="192.168.132.198,192.168.132.199,192.168.132.200";
	private static final int sessionTimeout=2000;
	private ZooKeeper  zk=null;
	private String parentNode="/servers";
	private volatile List<String> serverList;
	//获取链接
	public void getconnect() throws IOException{
		zk=new ZooKeeper(connectionString, sessionTimeout, new Watcher(){

			@Override
			public void process(WatchedEvent event) {	
				try {
					getServerList();
					System.out.println("客户端： "+serverList);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	public void getServerList() throws KeeperException, InterruptedException{
		List<String> children=zk.getChildren(parentNode, true);
		List<String> servers=new ArrayList<String>();
		for(String child:children){
			byte[] data=zk.getData(parentNode+"/"+child, false, null);
			servers.add(new String(data));
		}
		serverList=servers;
		
	}
	public void handleBussiness() throws InterruptedException{
		System.out.println("client is starting working");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		//获取zk链接
		DistributedZookeeperClient client=new DistributedZookeeperClient();
		client.getconnect();
		//获取servers的节点信息，从中获取服务器列表
		client.getServerList();
		//处理业务
		client.handleBussiness();
	}
}
