
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;


public class ConsistentHashing {
	
 private long hashMaxValue;
 private AmazonEC2 ec2;
	
//Integer-> point on the circle, String-> ServerID(Physical Node). For a server, this 
//  hashmap will have three entries for the three virtual nodes.
 private final TreeMap<Double, String> point2Id = new TreeMap<Double, String>();
 
 private final TreeMap<String, ArrayList<Double>> id2Point = 
		 new TreeMap<String, ArrayList<Double>>();
 
 private HashMap<String,String> instance2IPMap = new HashMap<String,String>();
 
 public ConsistentHash()
 {
    this.hashMaxValue = Long.MAX_VALUE;
 }
 
 public void startInstances (ArrayList<String> instanceList) throws IOException
 {
	 AWSCredentials credentials = new PropertiesCredentials(
             InlineTaggingCodeSampleApp.class.getResourceAsStream("AwsCredentials.properties"));

      ec2 = new AmazonEC2Client(credentials);
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		ec2.setRegion(usEast1);
		//Start instances
		StartInstancesRequest requests = new StartInstancesRequest().withInstanceIds(instanceList);
		StartInstancesResult startresult = ec2.startInstances(requests);
		
		//Describe instances
		DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
        List<Reservation> reservations = describeInstancesRequest.getReservations();
        Set<Instance> instances = new HashSet<Instance>();
        // add all instances to a Set.
        for (Reservation reservation : reservations) {
         instances.addAll(reservation.getInstances());
       }
        
        for (Instance ins : instances){        
         String instanceId = ins.getInstanceId();
         if(instanceList.contains(instanceId)){
         	this.instance2IPMap.put(instanceId, ins.getPublicIpAddress());
         	this.add(instanceId);
         }
        }
		
		
	 
 }
 /** Helper function for calculating hash
  *  
  * @param data byte array to hash
  * @param length length of the array to hash
  * @return 64 bit hash of the given string
  */
 public static long hash64( final byte[] data, int length, int seed) {
		final long m = 0xc6a4a7935bd1e995L;
		final int r = 47;

		long h = (seed&0xffffffffl)^(length*m);

		int length8 = length/8;

		for (int i=0; i<length8; i++) {
			final int i8 = i*8;
			long k =  ((long)data[i8+0]&0xff)      +(((long)data[i8+1]&0xff)<<8)
					+(((long)data[i8+2]&0xff)<<16) +(((long)data[i8+3]&0xff)<<24)
					+(((long)data[i8+4]&0xff)<<32) +(((long)data[i8+5]&0xff)<<40)
					+(((long)data[i8+6]&0xff)<<48) +(((long)data[i8+7]&0xff)<<56);
			
			k *= m;
			k ^= k >>> r;
			k *= m;
			
			h ^= k;
			h *= m; 
		}
		
		switch (length%8) {
		case 7: h ^= (long)(data[(length&~7)+6]&0xff) << 48;
		case 6: h ^= (long)(data[(length&~7)+5]&0xff) << 40;
		case 5: h ^= (long)(data[(length&~7)+4]&0xff) << 32;
		case 4: h ^= (long)(data[(length&~7)+3]&0xff) << 24;
		case 3: h ^= (long)(data[(length&~7)+2]&0xff) << 16;
		case 2: h ^= (long)(data[(length&~7)+1]&0xff) << 8;
		case 1: h ^= (long)(data[length&~7]&0xff);
		        h *= m;
		};
	 
		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		return h;
	}
	

	/** Generates 64 bit hash from byte array with default seed value.
	 * 
	 * @param data byte array to hash
	 * @param length length of the array to hash
	 * @return 64 bit hash of the given string
	 */
	public static long hash64( final byte[] data, int length) {
		return hash64( data, length, 0xe17a1465);
	}


	/** Generates 64 bit hash from a string.
	 * 
	 * @param text string to hash
	 * @return 64 bit hash of the given string
	 */
	public static long hash64( final String text) {
		final byte[] bytes = text.getBytes(); 
		return hash64( bytes, bytes.length);
	}

/** Given a server ID, this function will map it to three points on a unit circle and store
 *  the same in the hashmap
 *  
 *  @param serverID which uniquely identifies the server
 */
 public void add(String serverID) {
	 if(id2Point.containsKey(serverID)){
		 throw new  IllegalArgumentException("serverID exists already");
	 }
		 
	 else{
		 
	 double[] fiveCandidates = new double[5];
	 String[] arr ={"one","two","three","four","five"};
	 for(int i=0;i<arr.length;i++)
	 {
		 long hashVal = hash64(serverID+arr[i]);
		 double pointOnCircle = (double) Math.abs(hashVal)/hashMaxValue;
		 fiveCandidates[i] = pointOnCircle;
	 }
	 Arrays.sort(fiveCandidates);
	 point2Id.put(fiveCandidates[0],serverID);
	 point2Id.put(fiveCandidates[2],serverID);
	 point2Id.put(fiveCandidates[4],serverID);
	 id2Point.put(serverID, new ArrayList<Double>
	 (Arrays.asList(fiveCandidates[0],fiveCandidates[2],fiveCandidates[4])));
	 }	 
	 
 }
/*
 * Given a server ID it will remove all three points which the server is mapped to on the 
 * unit circle
 */
 public void remove(String serverID) {
  if(id2Point.containsKey(serverID)){
	  ArrayList<Double> points = this.getValue_id2Pt(serverID);
	  this.id2Point.remove(serverID);
	  for(int i=0;i<points.size();i++)
		  this.point2Id.remove(points.get(i));
  }
 }
/*
 * Given a string which may be a topic or username - it identifies which physical server 
 * should store the information
 */
 public String get(String topic) {
	 if(this.id2Point.size()==0)
		 return null;
	 else{
		 long hashVal = hash64(topic);
		 double pointOnCircle = (double) Math.abs(hashVal)/hashMaxValue;
		 System.out.println(pointOnCircle);
		 String serverID = this.point2Id.higherEntry(pointOnCircle).getValue();
		 return serverID;
	 }
	 
 }
 
 public String getValue_pt2ID(Double key)
 {
	 return this.point2Id.get(key);
 }
 
 public ArrayList<Double> getValue_id2Pt(String key)
 {
	 return this.id2Point.get(key);
 }
 
 public String getIP(String instance)
 {
	 return this.instance2IPMap.get(instance);
 }
  // UP- UT-TC
 
 public static void main(String[] args) throws Exception
 {
	 ConsistentHash ch = new ConsistentHash();
	 ArrayList<String> instanceRequests = new ArrayList<String>();
	// ch.startInstances(new ArrayList(Arrays.asList("i-699c8111")));
	 
	 String sCurrentLine;
	BufferedReader br = new BufferedReader
			(new FileReader("/home/sivas/Desktop/739/mocha_webserver/mocha_1/" +
					"consistent_hashing/instances.txt"));

		while ((sCurrentLine = br.readLine()) != null) {
			instanceRequests.add(sCurrentLine);
		}
		ch.startInstances(instanceRequests);
		ServerSocket listener = new ServerSocket(8080);
		 while(true)
		 {
			 Socket socket = listener.accept();
			 BufferedReader in = 
						 new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 String input = in.readLine();
			 String[] splits = input.split(";");
			 if(splits[0].equalsIgnoreCase("s"))
			 {
				 if(splits[1].equalsIgnoreCase("tc"))
				 {
					String instanceId = ch.get(splits[3]);
					String publicIP = ch.getIP(instanceId);
					 Socket s = new Socket(publicIP, 9090);
				      PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				      out.println(input);
				      s.close();
					 
				 }
				 else 
				 {
					 String instanceId = ch.get(splits[2]);
					 String publicIP = ch.getIP(instanceId);
					 Socket s = new Socket(publicIP, 9090);
					 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
					 out.println(input);
					 s.close();
				 }
				
			 }
			 
		 }
	
	
	
	 
 }

}
