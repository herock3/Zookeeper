package com.zyx.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.GetChildren2Request;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperClient {
	private static final String connectionString="192.168.132.198,192.168.132.199,192.168.132.200";
	private static final int sessionTimeout=2000;
	private  ZooKeeper zkClient=null;
	
	@Before
	public void init() throws IOException{
		//watch监听根节点发生变化就会调用process函数
		 zkClient=new ZooKeeper(connectionString,sessionTimeout,new Watcher(){
		 
			public void process(WatchedEvent envent) {
				//收到时间通知后的回调函数
				System.out.println(envent.getType()+"---"+envent.getPath());
				try {
					zkClient.getChildren("/", true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
		});
		 System.out.println(zkClient);
	}
	
	//创建节点数据
	@Test
	public  void createNode() throws KeeperException, InterruptedException{
		//参数：1：节点路径，2：节点数据，3，节点权限，4，节点的类型
				System.out.println("hellozk".getBytes());
				String nodeCreate=zkClient.create("/eclipse4", "hellozk".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	//获取子节点
	@Test
	public  void getChildren() throws KeeperException, InterruptedException{
		System.out.println(zkClient);
		List<String> children=zkClient.getChildren("/", new Watcher(){

			@Override
			public void process(WatchedEvent event) {
				System.out.println(event.getType()+"****/"+event.getPath());
			}
			
		});
		System.out.println("@@@@");
		for(String child:children){
			System.out.println(child);
		}
		Thread.sleep(Long.MAX_VALUE);
	}
	
	//判断节点是否存在
	@Test
	public void testExist() throws KeeperException, InterruptedException{
		Stat stat=zkClient.exists("/app1", false);
		System.out.println(stat==null?"exist":"notExist");
	}
	@Test
	//获取节点数据
	public void getData() throws KeeperException, InterruptedException{
		//参数3 stat:防止数据才不同服务器的不同版本出现不一致的情况
		byte[] data=zkClient.getData("/app1", false, null);
		System.out.println(new String(data));
	}
	
	//删除节点
	@Test
	public void deleteNode() throws KeeperException, InterruptedException{
		//参数3 stat:防止数据才不同服务器的不同版本出现不一致的情况
		byte[] data=zkClient.getData("/app1", false, null);
		//参数2指定要上出的版本，-1代表删除所有的版本
		zkClient.delete("/eclipse", -1);
	}
	
	//设置参数
	@Test
	public void setData() throws KeeperException, InterruptedException{
		zkClient.setData("/eclipse", "i am eclipse".getBytes(), -1);
	}
}
