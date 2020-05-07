package client;

import io.netty.channel.Channel;

public class BootStrap implements Runnable {

	
	private Channel channel;
	
	public BootStrap(Channel channel) {
		this.channel = channel;
	}
	
	
	@Override
	public void run() {
        Login sc = new Login(channel);
        sc.showUI();

    }
	
	
}
