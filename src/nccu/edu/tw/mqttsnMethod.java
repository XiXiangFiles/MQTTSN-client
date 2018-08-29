package nccu.edu.tw;

import java.net.DatagramPacket;

interface mqttsnMethod {
	public final byte CONNECT=0x04; 
	public final byte PUBLISH=0x0c; 
	public void connect();
	public void publish(String topic,String meg,int qos );
	
}
