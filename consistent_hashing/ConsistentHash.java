import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHash {
	
 private long hashMaxValue;
	
//Integer-> point on the circle, String-> ServerID(Physical Node). For a server, this 
//  hashmap will have three entries for the three virtual nodes.
 private final TreeMap<Double, String> point2Id = new TreeMap<Double, String>();
 
 private final TreeMap<String, ArrayList<Double>> id2Point = 
		 new TreeMap<String, ArrayList<Double>>();
 
 public ConsistentHash()
 {
    this.hashMaxValue = Long.MAX_VALUE;
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
  
 
 public static void main(String[] args)
 {
	 ConsistentHash ch = new ConsistentHash();
	 ch.add("salini");
	 System.out.println("Next");
	 ch.add("vinitha");
	 System.out.println("Next");
	 ch.add("siva");
	 System.out.println("Next");
	 ch.add("vidhya");
	 System.out.println("Next");
	 ch.add("192.164.67.89");
	 
	 System.out.println(ch.getValue_id2Pt("salini"));
	 System.out.println(ch.getValue_id2Pt("vinitha"));
	 System.out.println(ch.getValue_id2Pt("siva"));
	 System.out.println(ch.getValue_id2Pt("vidhya"));
	 System.out.println(ch.getValue_id2Pt("192.164.67.89"));
	 
	System.out.println(ch.get("shanlus"));
	 
 }

}
