package nccu.edu.tw;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


class mqttsn implements mqttsnMethod, mqttsnPacket {
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
		
		byte [] sendpacket=createData(CONNECT,null);
//		for(int i=0 ; i<sendpacket.length;i++) {
//			System.out.printf("%x ", sendpacket[i]);
//		}
		
		Thread t=new Thread(()->{
			try {
				sendto(sendpacket);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		t.start();
		try {
			t.sleep(20);
			t.join();
			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	
		
// ------------------------------------------------------------------------------------------ listen 10000 port 
		Thread t1=new Thread(()->{
			try {
				DatagramSocket s =new DatagramSocket();
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}) ;
		t1.start();
		
	}
	
	@Override
	public void publish(String topic, String meg, int qos) {
		// TODO Auto-generated method stub
		connect();
		byte [] sendpacket=createData(REGISTER,topic);
		byte [] sendpacket2=createData(PUBLISH,meg);
		Thread t1=new Thread(()->{	
			try {
				
				sendto(sendpacket);
				sendto(sendpacket2);
			}catch(Exception e) {}
		});
		
		try {
			t1.sleep(200);
			t1.start();
			t1.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
	private byte[] createData(byte type , String ...args) {
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
				case 0x0a: // REGISTER
					packetlen=6;
					packetlen+=args[0].length();
					mqttsnpackage=new byte[packetlen];
					mqttsnpackage[0]=(byte)packetlen;
					mqttsnpackage[1]=type;
//					----------------------------- topic id
					mqttsnpackage[2]=0x00;
					mqttsnpackage[3]=0x00;
//					-----------------------------msgid
					mqttsnpackage[4]=0x00;
					mqttsnpackage[5]=0x01;
//					----------------------------topic name
					i=6;
					for(char x :args[0].toCharArray() ) {
						mqttsnpackage[i++]= (byte)x;
					}
				break;
				case 0x0c: //PUBLISH
					packetlen=7;
					packetlen+=args[0].length();
					mqttsnpackage=new byte[packetlen];
					mqttsnpackage[0]=(byte)packetlen;
					mqttsnpackage[1]=type;
//					-------------------------flags
					mqttsnpackage[2]=0x00;
//					-------------------------TopicId
					mqttsnpackage[3]=0x00;
					mqttsnpackage[4]=0x01;
//					-----------------------------msgid
					mqttsnpackage[5]=0x00;
					mqttsnpackage[6]=0x02;
					i=7;
					for(char x :args[0].toCharArray() ) {
						mqttsnpackage[i++]= (byte)x;
					}
					break;
		}
		return mqttsnpackage;
	}
	private void sendto(byte [] data) throws SocketException, UnknownHostException {
		DatagramSocket s =new DatagramSocket(this.Port);
		DatagramPacket output=new DatagramPacket(data,data.length,InetAddress.getByName(this.Host),this.Port);
		try {
			s.send(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.close();
	}
	
}

public class mqtttest {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mqttsn n = new mqttsn("140.119.143.79:10000","mqtt-sn-tools-31231");
		n.publish("testTpoic", "Testmqttsn", 0);
		
	}

}
