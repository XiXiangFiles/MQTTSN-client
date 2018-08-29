package nccu.edu.tw;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


class mqttsn implements mqttsnMethod{
	String Host,Clientid;
	int Port;

	
	public mqttsn(String host,String clientid) {
		
		String[] str=host.split(":");
		this.Host=str[0];
		this.Port=Integer.parseInt(str[1]);
		this.Clientid=clientid;
		
	}
	
	@Override
	public void connect()   {
		// TODO Auto-generated method stub
		
		byte [] sendpacket=createData(CONNECT);
//		for(int i=0 ; i<sendpacket.length;i++) {
//			System.out.printf("%x ", sendpacket[i]);
//		}
		try {
			sendto(sendpacket);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Thread t1=new Thread(()->{
			try {
				DatagramSocket s =new DatagramSocket(this.Port);
				while(true) {
					DatagramPacket packet=listenPacket(s);
					String msg=new String(packet.getData(),0,packet.getLength(),"ascii");
					byte [] hexmsg=packet.getData();
					System.out.println("msg=\t"+msg);
					for(int i=0 ; i<packet.getLength();i++) {
						System.out.printf("%x ", hexmsg[i]);
					}
					System.out.println("");
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
	private byte[] createData(byte type) {
		int packetlen=0;
		byte []mqttsnpackage=null;
		switch(type) {
			case 0x04:
				packetlen=6;
				packetlen+=this.Clientid.length();
				mqttsnpackage=new byte[packetlen];
				mqttsnpackage[0]=(byte)packetlen;
				mqttsnpackage[1]=type;
//				-----------------------------flag
				mqttsnpackage[2]=0x04;
//				-----------------------------protocolID
				mqttsnpackage[3]=0x01;
//				-----------------------------Duration
				mqttsnpackage[4]=0x00;
				mqttsnpackage[5]=0x0a;
				int i=6;
				for(char x :this.Clientid.toCharArray() ) {
					mqttsnpackage[i++]= (byte)x;
				}
				break;
		}
		return mqttsnpackage;
	}
	private void sendto(byte [] data) throws SocketException, UnknownHostException {
		DatagramSocket s =new DatagramSocket();
		DatagramPacket output=new DatagramPacket(data,data.length,InetAddress.getByName(this.Host),this.Port);
		Thread t1=new Thread(()-> {
			try {
				s.send(output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		t1.start();
	}
	
}

public class mqtttest {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mqttsn n = new mqttsn("140.119.143.79:10000","mqtt-sn-tools-31231");
		n.connect();
		
	}

}
