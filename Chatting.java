import java.io.*;
import java.net.*;
import java.util.*;
class clienthandler extends Thread
{
	Map  map;
	Socket soc;
	String client_name;
	clienthandler(Socket s,Map hash,String title)
	{
		map=new HashMap(hash);
		soc=s;
		client_name=title;
	}
	public void run()
	{	
		try
		{
		InputStream is=soc.getInputStream();
		DataInputStream ds=new DataInputStream(is);
		while(true)
		{
			String msg=ds.readUTF();
			if(msg.equals("unicast") || msg.equals("multicast"))
			{
				OutputStream in=soc.getOutputStream();
				DataOutputStream data=new DataOutputStream(in);
				data.writeUTF("how many clients");
				msg=ds.readUTF();
				data.writeUTF("enter their names");
				int total=Integer.parseInt(msg);
				Vector <String> v=new Vector<String>();
				for(int j=0;j<total;j++)
				{
					msg=ds.readUTF();
					v.add(msg);
				}
				System.out.println("start"+"   unicasting");
				msg=ds.readUTF();
				System.out.println(v);	
				while(!msg.equals("end"))
				{
					Enumeration e1 = v.elements();  
          					 while(e1.hasMoreElements())
               					 {		
              					                  String element = (String)e1.nextElement();			         
						Socket soc= (Socket)map.get(element);
						OutputStream out=soc.getOutputStream();
						DataOutputStream dataout=new DataOutputStream(out);
						dataout.writeUTF(msg);							
        					 } 
					msg=ds.readUTF();		
				}
			}
			else
			{
				System.out.println("message received from"+    client_name   +msg);
				Set s = map.entrySet();
              				Iterator i = s.iterator();
           				while(i.hasNext())
          				{
                          				Map.Entry e = (Map.Entry)i.next();
                   			 	Socket soc= (Socket)e.getValue();
					OutputStream in=soc.getOutputStream();
					DataOutputStream data=new DataOutputStream(in);
					data.writeUTF(msg);	
               			 	}
			}
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}		
	}
}
class server2
{
	
	public static void main(String [] args)
	{
		try{
		int port=Integer.parseInt(args[0]);
		ServerSocket ss=new ServerSocket(port);
		Map map=new HashMap();
		List <clienthandler> v=new Vector<clienthandler>();
		int flag=0;
		write2 w=null;
		while(true)
		{
			Socket soc=ss.accept();
			InputStream is=soc.getInputStream();
			DataInputStream ds=new DataInputStream(is);
			String name=ds.readUTF();
			clienthandler clienthand=new clienthandler(soc,map,name);
			 map.put(name,soc);
			if(flag==0)
			{
				 w=new write2(map,"server");
				w.start();
				flag=1;
			}
			else
			{
				Map m2=w.map;
				m2.put(name,soc);
			}
			for(clienthandler c : v)
			{
				Map map1=c.map;
				map1.put(name,soc);
			}
			v.add(clienthand);
			System.out.println("new client added"+name);
			clienthand.start();
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
}
class client2 extends Thread
{
	client2(String name,int port)
	{
		super(name);
		try{
		Socket s=new Socket("localhost",port);
		OutputStream os=s.getOutputStream();
		DataOutputStream ds=new DataOutputStream(os);
		ds.writeUTF(name);
		read2 rc=new read2(s);
		write2 wc=new write2(s);
		rc.start();
		wc.start();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	public static void main(String [] args)
	{
		int portno=Integer.parseInt(args[1]);
		client2 c=new client2(args[0],portno);		
	}
}
class read2 extends Thread
{
	Socket socket;
	read2(Socket soc)
	{
		socket=soc;		
	}
	public void run()
	{
		try
		{
		while(true)
		{
			String msg=null;
			InputStream is=socket.getInputStream();
		                 	DataInputStream ds=new DataInputStream(is);
			msg=ds.readUTF();
			System.out.println(msg);
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}		
class write2 extends Thread
{
	Socket socket;
	String name;
	Map map;
	write2(Socket s)
	{	
		socket=s;
		name="client";
	}
	write2(Map hash,String name)
	{
		this.name=name;
		map=new HashMap(hash);
	}
	public void run()
	{	
		try
		{
		while(true)
		{	
			
			Scanner scan=new Scanner(System.in);
			String msg=scan.nextLine();	
			if(!(name.equals("server")))
			{		
			OutputStream os=socket.getOutputStream();
			DataOutputStream ds=new DataOutputStream(os);
			ds.writeUTF(msg);
			}
			else
			{
				Set s = map.entrySet();
              				Iterator i = s.iterator();
           				while(i.hasNext())
          				{
                          				Map.Entry e = (Map.Entry)i.next();
                   			 	Socket soc= (Socket)e.getValue();
					OutputStream in=soc.getOutputStream();
					DataOutputStream data=new DataOutputStream(in);
					data.writeUTF(msg);	
               			 	}
			}
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}