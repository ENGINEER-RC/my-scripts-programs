/**
 * Email: engineer.rc1@gmail.com 
 * @author Tomrock D'souza, St. Francis Institute Of Technology, University of Mumbai, 2017
 * @copyright GNU General Public License v3.0
 * No reproduction in whole or part without maintaining this copyright notice
 */
 import java.net.*;
 import java.io.*;
 import java.util.Scanner;

 public class dnsSpoofDetectionProgram {
     public static void main(String[] args) throws Exception {
         String domainInput;
         Scanner sc = new Scanner(System.in);
         System.out.print("Enter Domain:");
         domainInput = sc.next();
         int returnError;
         String returnIP;

         /* Dummy Check
		  * In this Check we have 2 checks
		  * 1.Entering a fake non existant domain to the internal ip fetcher
		  * 2.Entering the input domain into the Error checker witha fake dns server ip
		  * These 2 checks are done regardless of whatever input the user provides.
		  */
         System.out.println("\n*** Performing Dummy Dns Check ***");
         if (dError(domainInput, "4.4.4.4") == 1 && rDns("nonexistdomain.tld") == "1") {
             System.out.println("Result: OK");
         } else {
             System.out.println("Result:Danger your Dns queries are redirected\n");
         }
		 

         /* Various Combos
		  * After The dummy Dns checks we verify the user input domain name.
		  */
         returnError = dError(domainInput, "8.8.8.8");
         returnIP = rDns(domainInput);
         System.out.println("\n*** Dns Validity Check & Dns Internal Dns Call Check ***");
		
         if (returnError == 0 && returnIP == "Fault") {
             System.out.println("Your dns settings are faulty\n\nEnd.");
			 //If nslookup gives the answer but your internal checkers doesn't, it means the your Dns server settings are faulty.
         } else if (returnError == 2 && returnIP == "1") {
             System.out.println("This domain name is unassigned\n\nEnd.");
			  //If nslookup does not gives the domain not assigned mark also your internal checkers does not,Dns is unassigned.
         } else if (returnError == 1 && returnIP == "1") {
             System.out.println("Your internet is disconnected\n\nEnd.");
			 //If nslookup does not givee the answer also your internal checkers does not,YOur Internet Connection is Down.
         } else if (returnError != 0 && returnIP != "1") {
             System.out.println("You may be a victim Of cache posionining\n\nEnd.");
			 //if your internal check gives a legit domain name while  nslookup doesn't it means your device is infected with dns hijacks
         } else if (returnError == 0 && returnIP != "1") {
			 //if nslookup and internal checks are ok we go to the next step of matching internal checkers and nslookup
             System.out.println("All OK\n");
             String x = eCheck(domainInput, returnIP);
             if (x == "Equal") {
                 System.out.println("\nNo dns hijack determined for this specific domain\n\nEnd.");
             } else {
				 /* if still the ips don't match the last record from ns lookup
				  * and internal ip is parsed online and the ip infos are received.
				  * if these ips match then probably they didn't match becuase of the domain being of cdn type.
				  * if they didn't match Dns hijacking exist .
				  * there is a extremely small posiblity of the ip returned from the nslookup to be only .
				  * Ivp6 which info is not still there on the api of the ip lookup hence the program may turn faulty at this step.
				  */
                 System.out.println("\n*** Performing final check: whois ***\n");
                 if (jsonCheck(x, returnIP)) {
                     System.out.println("\nThis domain is a CDN domain and no dns hijack detected\n\nEnd.");
                 } else {
				 System.out.println("\n\"DNS Hijacking is Detected\"");
				 System.out.println("There is a Extermely low posibility that this result may fail.\n\nEnd.");
				 }
             }
         }
		 
		 
         System.out.println("\n\n(c) 2016 ajmet22@gmail.com");
		 System.out.println("Created By: Tomrock D'souza, St. Francis Institute Of Technology, Mumbai University.");
     }
	 
	 
	 
		 //Function Checks the results from nslookup and compares the results if not return 1 IP from nslookup for whois lookup comparsion
     public static String eCheck(String domainName, String localIP) throws Exception {
         int varA = 0, varB = 0, varC = 0, varE = 0;
         String[] line = new String[20];
         String[] addr = new String[10];
         Process p = Runtime.getRuntime().exec("cmd /c %windir%/system32/nslookup " + domainName + " 8.8.8.8");
         BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));

         while ((line[varA] = bri.readLine()) != null) {
             if (line[varA].indexOf("Name:") != -1) {
                 varB = varA;
             }
             varA += 1;
         }
         bri.close();
         p.waitFor(); 
         p.destroy();
         if (varB != 0) {
             varB += 1;
             varA -= 2;
             System.out.println("\n*** Performing ip Matching Based on nslookup results ***");
			 System.out.println("\nNslookup gave the following results:");
             if (varA == varB) {
                 addr[0] = line[varB].substring(10, line[varB].length());
                 System.out.println(addr[0]);
                 varE = 1;
             } else if (varB < varA) {
                 addr[0] = line[varB].substring(12, line[varB].length());
                 System.out.println(addr[0]);
                 varB += 1;
                 varC = varA - varB;
                 varE = 1;
                 for (int i = 0; i <= varC; i++) {
                     addr[i + 1] = line[varB + i].substring(3, line[varB + i].length());
                     System.out.println(addr[i + 1]);
                     varE++;
                 }
             }
         }
         System.out.println("\nYour Local Returned ip Address:\n" + localIP);
         for (varA = 0; varA < varE; varA++) {
             if (cString(addr[varA], localIP)) {
                 varB = 0;
                 break;
             }
             varB = 1;
         }
         if (varB == 0) {
             return "Equal";
         } else {
             return addr[varE - 1];
         }

     }
	 
	 
	 //Function takes in 2 Strings the Domain name and test Resolver IP to get the errors from the nslookup results
     public static int dError(String domainName, String resolverIP) throws Exception {

         int errorVar = 0;
         String[] linee = new String[20];
         Process p1 = Runtime.getRuntime().exec("cmd /c %windir%/system32/nslookup " + domainName + " " + resolverIP);
         BufferedReader bre1 = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
         while ((linee[errorVar] = bre1.readLine()) != null) {
             errorVar++;
         }
         errorVar = 0;
         bre1.close();
         p1.waitFor();
         String errorLine = linee[0];
         if (linee[0] == null) {
             errorVar = 2;
         } else if (errorLine.equals("Non-authoritative answer:")) {
             errorVar = 0;
         } else if (errorLine.substring(4, 11).equals("Request")) {
             errorVar = 1;
         } else if (errorLine.substring(errorLine.indexOf(":"), errorLine.length()).equals(": Non-existent domain")) {
             errorVar = 2;
         } else if (errorLine.substring(errorLine.indexOf(":"), errorLine.length()).equals(": No response from server")) {
             errorVar = 3;
         }
         p1.destroy();
         return errorVar;

     }
	 
	 //Function Takes 2 IP Adresses and Compares Their whois information (as,org)
     public static boolean jsonCheck(String firstIP, String secondIP) throws Exception {
         URL oracle = new URL("http://ip-api.com/json/" + firstIP);
         BufferedReader in = new BufferedReader(
             new InputStreamReader(oracle.openStream()));

         String[] inputLine = new String[2];
         String jsonString, as1, org1, as2, org2;
         int haha = 0;
         while ((inputLine[haha] = in .readLine()) != null) {
             haha++;
         } in .close();
         jsonString = inputLine[0].substring(7);
         as1 = jsonString.substring(0, jsonString.indexOf("\""));
         jsonString = jsonString.substring(jsonString.indexOf("org\"") + 6);
         org1 = jsonString.substring(0, jsonString.indexOf("\""));


         URL oracle1 = new URL("http://ip-api.com/json/" + secondIP);
         BufferedReader in1 = new BufferedReader(
             new InputStreamReader(oracle1.openStream()));
         haha = 0;
         while ((inputLine[haha] = in1.readLine()) != null) {
             haha++;
         } in .close();
         jsonString = inputLine[0].substring(7);
         as2 = jsonString.substring(0, jsonString.indexOf("\""));

         jsonString = jsonString.substring(jsonString.indexOf("org") + 6);
         org2 = jsonString.substring(0, jsonString.indexOf("\""));

         if (org1.equals(org2)) {
             System.out.println("Same Org:" + org1);
         }
         if (as1.equals(as2)) {
             System.out.println("same As:" + as1);
         }

         if (as1.equals(as2) && org1.equals(org2)) {
             return true;
         } else return false;

     }
	 
	 //Function To compare 2 Strings
     public static boolean cString(String a, String b) {
         if (a.equals(b))
             return true;
         else return false;
     }
	 
	 //Function made From Dns.java
     public static String rDns(String hostname) throws IOException {
         try {
             InetAddress ipaddress = InetAddress.getByName(hostname);
             return ipaddress.getHostAddress();
         } catch (UnknownHostException e) {
             return "1";
         }
     }
 }
 
 
 
