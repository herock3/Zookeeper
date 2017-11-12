package com.zyx.zookeeper.distributedLock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class DistributedLock {
	private static final String connectionString="192.168.132.198,192.168.132.199,192.168.132.200";
	private static final int sessionTimeout=2000;
	private ZooKeeper zk;
	private String parentNode="/locks";
	private String thisPath;
	private volatile List<String> serversList;
	//1、获取链接
	public void getconnect() throws IOException{
		zk=new ZooKeeper(connectionString, sessionTimeout, new Watcher(){

			@Override
			public void process(WatchedEvent event) {	
//				try {
//					if(event.getType()==EventType.NodeChildrenChanged && event.getPath().equals("/")){
//						getResource("mini1");
//					}
//					
//				} catch (KeeperException | InterruptedException e) {
//					
//					e.printStackTrace();
//				}
			}
			
		});
	}
	//2、注册服务
	
	public void register(String hostName) throws KeeperException, InterruptedException{
		thisPath=parentNode+"/"+hostName;
		System.out.println(thisPath+"hostName"+ hostName.getBytes());
		zk.create(thisPath, hostName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(zk.getData(thisPath, false, null));
	}
	//3、获取服务列表并监听
	public List<String> getServerList() throws KeeperException, InterruptedException{
		List<String> children=zk.getChildren(parentNode, true);
		List<String> servers=new ArrayList<>();
		for (String child : children) {
			byte[] data=zk.getData(parentNode+"/"+child, false, null);
			servers.add(new String(data));
		}
		 serversList=servers;
		 return serversList;
	}
	//4、如果只有自己一个就直接获取资源，并添加一条心得服务名
	public void  getResource(String hostName) throws KeeperException, InterruptedException{
		List<String> childrenNodes=getServerList();
		if(childrenNodes.size()==1){
			doSometing();
		}else{
			Collections.sort(childrenNodes);
			System.out.println(thisPath);
			String thisNode=thisPath.substring(("/"+parentNode+"/").length());
			if(childrenNodes.indexOf(thisNode)==0){
				doSometing();
				zk.create(parentNode+"/"+hostName, hostName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
				Thread.sleep(Long.MAX_VALUE);
			}
			
		}
	}
	
	//6、获取资源，并删除现有服务名
	public void doSometing() throws InterruptedException, KeeperException{
		System.out.println(thisPath+"正在获取资源...");
		System.out.println(thisPath);
		zk.delete(thisPath, -1);
	} 
	
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		DistributedLock lock=new DistributedLock();
		lock.getconnect();
		lock.register("mini1");
		lock.getResource("mini1");
	}
	@Test
	public void test() throws KeeperException, InterruptedException, IOException{
		getconnect();
		zk.create("/locks/mini2", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
}
