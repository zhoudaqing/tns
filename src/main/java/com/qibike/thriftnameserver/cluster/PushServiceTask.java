package com.qibike.thriftnameserver.cluster;

import java.util.LinkedList;
import java.util.List;

import com.qibike.thriftnameserver.command.push.ThriftPushSNodeListCommand;
import com.qibike.thriftnameserver.rpc.STATE;
import com.qibike.thriftnameserver.rpc.TCNode;
import com.qibike.thriftnameserver.rpc.TSNode;
import com.qibike.thriftnameserver.service.SNodeManager;

public class PushServiceTask implements Runnable {
	private final CNodeManager cNodeManager = CNodeManager.getInstance();
	private final SNodeManager sNodeManager = SNodeManager.getInstance();

	@Override
	public void run() {
		TCNode tcnode = cNodeManager.getOne();
		if (null == tcnode) {
			return;
		}
		List<TSNode> list = new LinkedList<TSNode>();
		sNodeManager.toAllServiceNodeList(list);
		if (list.isEmpty()) {
			return;
		}
		ThriftPushSNodeListCommand command = new ThriftPushSNodeListCommand(tcnode, list);
		STATE state = command.push();
		/**
		 * 更新节点tcnode状态
		 */
		tcnode.setState(state);
		tcnode.setTimestamp(System.currentTimeMillis());
	}

}