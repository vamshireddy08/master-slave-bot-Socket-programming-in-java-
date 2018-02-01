import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class MasterBot extends Thread{
public static ArrayList<client_details> client_list=new ArrayList<client_details>(); 
String input;
int targetport;
static int no_of_connections; 
public void run(){
while(true){
System.out.print(">");
Scanner cc=new Scanner(System.in);
input=cc.nextLine();
//master reads command , splits it and process request
String delim = " ";
	String[ ] t=new String[6];
	int i=0;

StringTokenizer st = new StringTokenizer(input, delim, true);
boolean expectDelim = false;
while (st.hasMoreTokens()) {
    String token = st.nextToken();
	if (delim.equals(token)) 
	{
        if (expectDelim) {
            expectDelim = false;
            continue;
        } else {
            // unexpected delim means empty token
            token = null;
        }
    }
	t[i]=token;
	i=i+1;
        expectDelim = true;
}
String command5=null;
if(("list".equals(t[0])) && (i==1)){
if(client_list.size()==0)
System.out.println("no client connections at the moment");
else{
 //displays current connections
for(int z=0;z<client_list.size();z++){
System.out.println(client_list.get(z).slave_name+" "+client_list.get(z).slave_ipaddress+" "+client_list.get(z).slave_port+" "+client_list.get(z).date);
}
}
}

else if("connect".equals(t[0]))
{//sending data to client
if(i<4||i>6)
        System.out.println("invalid connect command with wrong number of arguments");
    else{
if(i==4)
no_of_connections=1;
else if(i==6)
{no_of_connections=Integer.parseInt(t[4]);
    command5=t[5];
}

else if(i==5)
{
    if(t[4].matches("^[0-9]+$"))
   no_of_connections=Integer.parseInt(t[4]);
   else if(t[4].equalsIgnoreCase("keepalive")||t[4].contains("url="))
    {
        no_of_connections=1;
    command5=t[4];
   }
}
}
int found=0;
if("all".equals(t[1]))
{ if(client_list.size()==0)
	System.out.println("No slaves connected to Master");
else{
for(int y=0;y<client_list.size();y++)
{ try{
PrintStream pp=new PrintStream(client_list.get(y).slave_socket.getOutputStream());
if(command5==null)
pp.println("connect"+" "+t[2]+" "+t[3]+" "+no_of_connections);
else
pp.println("connect"+" "+t[2]+" "+t[3]+" "+no_of_connections+" "+command5);
}catch(IOException e33){System.out.println(e33);}	
}
}
}
else
{
for(int y=0;y<client_list.size();y++)
{ if(client_list.get(y).slave_name.equals(t[1])||client_list.get(y).slave_ipaddress.equals(t[1]))
{
	found=1;
try{
PrintStream pp=new PrintStream(client_list.get(y).slave_socket.getOutputStream());
if(command5==null)
pp.println("connect"+" "+t[2]+" "+t[3]+" "+no_of_connections);
else
pp.println("connect"+" "+t[2]+" "+t[3]+" "+no_of_connections+" "+command5);

}catch(IOException e33){System.out.println(e33);}	
}
}
if(found==0)
System.out.println("Slave Address not found:");
}
}
else if("disconnect".equals(t[0]))
{//sending data to client
if(i==3)
targetport=0;
else if(i==4)
targetport=Integer.parseInt(t[3]);
else 
System.out.println("Invalid disconnect command");
int found=0;
if("all".equals(t[1]))
{
 if(client_list.size()==0)
	System.out.println("No slaves connected to Master");
else
{
for(int y=0;y<client_list.size();y++)
{ try{
PrintStream pp=new PrintStream(client_list.get(y).slave_socket.getOutputStream());
pp.println("disconnect"+" "+t[2]+" "+targetport);
}catch(IOException e33){ }	
}
}
}
else
{
for(int y=0;y<client_list.size();y++)
{ 
if(client_list.get(y).slave_name.equals(t[1])||client_list.get(y).slave_ipaddress.equals(t[1]))
{
	found=1;
try{
PrintStream pp=new PrintStream(client_list.get(y).slave_socket.getOutputStream());
pp.println("disconnect"+" "+t[2]+" "+targetport);
}catch(IOException e33){ }	
}//if
}//for
 if(found==0)
System.out.println("slave ip not found");
}//else
}//
else 
System.out.println("wrong command");
}//whiletrue
}//voidrun
public static void main(String args[]) throws Exception{
int portnumber=0;
ServerSocket ss=null;
if(args.length == 2)
{
if(args[0].equals("-p")){
 portnumber=Integer.parseInt(args[1]);}
else{
System.out.println("Invalid command:");
System.exit(0);}
}
else{
System.out.println("Invalid command:");
System.exit(0);
}
try{
ss=new ServerSocket(portnumber);
}catch(Exception e11){e11.getMessage();}
MasterBot x=new MasterBot();
x.start();

while(true){
multislave m;
try{
m=new multislave(ss.accept());
//server.accept returns a client connection

Thread t=new Thread(m);
t.start();
}catch(Exception e){
System.out.println(e);};
}
}
}
class multislave implements Runnable{
Socket client;
//constructor
multislave(Socket client)
{
this.client=client;}
public void run(){
client_details cd=new client_details();
try{
cd.slave_name=client.getInetAddress().getHostName();
cd.slave_ipaddress=client.getInetAddress().getHostAddress();
cd.slave_port=client.getPort();
cd.slave_socket=client;
cd.date=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
MasterBot.client_list.add(cd);
}catch(Exception e){ }
}
}
 class client_details{
String slave_name;
String slave_ipaddress;
int slave_port;
Socket slave_socket;
String date;
}
