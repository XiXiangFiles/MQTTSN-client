package nccu.edu.tw;

import java.net.DatagramPacket;

interface mqttsnMethod {
	
	public void connect();
	public void publish(String topic,String meg,int qos );
	
}
