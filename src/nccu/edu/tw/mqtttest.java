package nccu.edu.tw;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;



class mqttsn implements mqttsnMethod{
	String Host;
	int Port;

	
	public mqttsn(String host,String clientid) {
		
		String[] str=host.split(":");
		this.Host=str[0];
		this.Port=Integer.parseInt(str[1]);
		
	}
	
	@Override
	public void connect()   {
		// TODO Auto-generated method stub
		
		Thread t1=new Thread(()->{
			try {
				DatagramSocket s =new DatagramSocket( this.Port );
				while(true) {
					DatagramPacket packet=listenPacket(s);
					String msg=new String(packet.getData(),0,packet.getLength(),"ascii");
					
				}
				
				
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}) ;
		t1.start();
		
	}
	
	@Override
	public void publish(String topic, String meg, int qos) {
		// TODO Auto-generated method stub
	
	}

	
	private DatagramPacket listenPacket( DatagramSocket socket) {
		// TODO Auto-generated method stub
		try {
			DatagramPacket packet =new DatagramPacket( new byte [1024], 1024 );
	        socket.receive( packet );
	        return packet;
		}catch(Exception e) {
			System.out.println("error="+e);
		}
		
		return null;
	}
	private void sendData(byte[]data) {
		
	}
	
}

public class mqtttest {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mqttsn n = new mqttsn("127.0.0.1:10000","test");
		n.connect();
		
	}

}
