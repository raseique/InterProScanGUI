package pt.uminho.sysbio.common.bioapis.externalAPI.interpro;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import ch.qos.logback.core.net.SyslogOutputStream;

public class InterProParser {
	
	public static String jobID = "iprscan5-R20160424-232342-0215-12126963-pg";
	public static String directory = "C:\\Users\\João Sequeira\\Documents\\Escola\\Projeto Bioinformática BSIstemas\\interproscan API\\";

	List<HashMap<String, Object>> xml_information;
	
	public InterProParser(List<String> xml) throws IOException {
		
		super();
		this.xml_information = new ArrayList<HashMap<String, Object>>();
		this.xml_information = InterProParser.xml_information(xml);
	}
	
	private static HashMap<String,String> GetEc2Go() throws IOException{
	
		URL url = new URL("http://www.geneontology.org/external2go/ec2go");
		Scanner s = new Scanner(url.openStream());
		HashMap<String,String>ec2go = new HashMap<String,String>();
		while (s.next().substring(0,1).equals("!")){s.next();}
		while (s.hasNextLine()) {
			List<String> entry = Arrays.asList(s.nextLine().split(" "));
			if (!(entry.get(0).equals("!"))){
				ec2go.put(entry.get(entry.size()-1),entry.get(0));
			}
		}
		s.close();
		
		return ec2go;
	}
	
	private static HashMap<String,String> GetSl2Go() throws IOException{
		
		URL url = new URL("ftp://ftp.ebi.ac.uk/pub/databases/GO/goa/external2go/uniprotkb_sl2go");
		Scanner s = new Scanner(url.openStream());
		HashMap<String,String>sl2go = new HashMap<String,String>();
		Pattern pattern = Pattern.compile("UniProtKB-SubCell:SL-[0-9]* (.+) >");
		while (s.next().substring(0,1).equals("!")){s.next();}
		while (s.hasNextLine()) {
			Matcher matcher = pattern.matcher(s.nextLine());
			if (matcher.find()){
				List<String> entry = Arrays.asList(s.nextLine().split(" "));
				sl2go.put(entry.get(entry.size()-1),matcher.group(1));
			}
		}
		s.close();		
		return sl2go;
	}
	
	private static HashMap<String,String> GetInterPro2Go() throws IOException{
		
		URL url = new URL("http://www.geneontology.org/external2go/interpro2go");
		Scanner s = new Scanner(url.openStream());
		HashMap<String,String>interpro2go = new HashMap<String,String>();
		Pattern pattern = Pattern.compile("InterPro:IPR[0-9]* (.+) >");
		while (s.next().substring(0,1).equals("!")){s.next();}
		while (s.hasNextLine()) {
			Matcher matcher = pattern.matcher(s.nextLine());
			if (matcher.find()){
				List<String> entry = Arrays.asList(s.nextLine().split(" "));
				interpro2go.put(entry.get(entry.size()-1),matcher.group(1));
			}
		}
		s.close();
		
		return interpro2go;
	}

//	public static ArrayList<String> readFile(String jobID, String extension) throws IOException{
//		
//		Scanner s = new Scanner(new File(directory+jobID+extension));
//		ArrayList<String> list = new ArrayList<String>();
//		while (s.hasNextLine()){
//		    list.add(s.nextLine());
//		}
//		s.close();
//		
//		return list;
//	}
//
//	public static String get_protein_sequence() throws IOException{
//		
//		String protein_sequence = "";
//		
//		ArrayList<String> file = readFile(jobID,".gff.txt");
//		
//		for (int i = 0; i < file.size(); i ++) {
//			
//			if (file.get(i).startsWith(">EMBOSS_001")) {
//				
//				i += 1;
//				
//				while (!(file.get(i).startsWith(">match$"))){
//					
//					protein_sequence += file.get(i);
//					i += 1;
//				}
//			}
//		}
//		return protein_sequence;	
//	}
	
//	private static List<HashMap<String, String>> get_gff_information() throws IOException {
//		
//		ArrayList<String> file = readFile(jobID,".gff.txt");
//		
//		List<HashMap<String, String>> domains = new ArrayList<HashMap<String, String>>();
//		
//		Pattern GOPattern = Pattern.compile("Ontology_term=(.+?);");
//		Pattern IDPattern = Pattern.compile("ID=match(.+)_[0-9]+_[0-9]+;");
//		Pattern SDPattern = Pattern.compile("signature_desc=(.+?);");
//		Pattern NamePattern = Pattern.compile("Name=(.+?);");
//		Pattern StatusPattern = Pattern.compile("status=(.+?);");
//		Pattern xrefPattern = Pattern.compile("Dbxref=(.+)");
//
//		for (int i = 0; i < file.size(); i++) {
//			
//			if (file.get(i).startsWith("EMBOSS_001\t")) {
//				
//				if (!(file.get(i).startsWith("EMBOSS_001\t."))) {
//					
//					String[] parts = file.get(i).split("\t");
//					HashMap<String,String> domain = new HashMap<String,String>();
//					
//					domain.put("Source", parts[1]);      			//database from where the result is
//					domain.put("Type", parts[2]);					//type of match
//					domain.put("Start_position", parts[3]);
//					domain.put("End_position", parts[4]);
//					domain.put("E-value", parts[5]);
//					domain.put("Strand", parts[6]);
//					domain.put("Phase", parts[7]);					//phase indicates where the feature begins with reference to the reading frame
//					
//					Matcher GOMatcher = GOPattern.matcher(parts[8]);
//					if (GOMatcher.find()){domain.put("Ontology_term", GOMatcher.group(1));}
//					
//					Matcher IDMatcher = IDPattern.matcher(parts[8]);
//					if(IDMatcher.find()){domain.put("Match_ID", IDMatcher.group(1));}
//					
//					Matcher SDMatcher = SDPattern.matcher(parts[8]);
//					if(SDMatcher.find()){domain.put("Description", SDMatcher.group(1));}
//					
//					Matcher NameMatcher = NamePattern.matcher(parts[8]);
//					if(NameMatcher.find()){domain.put("Name", NameMatcher.group(1));}
//					
//					Matcher StatusMatcher = StatusPattern.matcher(parts[8]);
//					if(StatusMatcher.find()){domain.put("Status", StatusMatcher.group(1));}
//					
//					Matcher xrefMatcher = xrefPattern.matcher(parts[8]);
//					if(xrefMatcher.find()){domain.put("Dbxref", xrefMatcher.group(1));}
//					
//					domains.add(domain);
//					
//				}
//				
//			}
//				
//			if (file.get(i).startsWith(">match")) {
//							
//				Pattern SeqPattern = Pattern.compile(">match(.+?)_[0-9]+_[0-9]+");
//				Matcher SeqMatcher = SeqPattern.matcher(file.get(i));
//				SeqMatcher.matches();
//				
//				String sequence	= "";
//				
//				i++;
//				
//				while (!(file.get(i).startsWith(">match")) && i < file.size()-1) {
//					
//					sequence += file.get(i);		
//					
//					i ++;
//								
//				}
//				
//				for (HashMap<String,String>domain:domains){
//					
//					if(domain.get("Match_ID").equals(SeqMatcher.group(1))){
//						
//						domain.put("Sequence",sequence);
//					}
//				}
//				
//			i--;
//			}
//		}
//		
//		return domains;	
//	}
//	
//	private static String find_regex(String regex, String query) {
//		
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher(query);
//		
//		if (m.find()){domain.put("E-value",Float.parseFloat(m.group(1)));}
//		
//	}
	
	public List<HashMap<String, Object>> get_xml_information(List<String> xml) throws IOException {
		
		return InterProParser.xml_information(xml);
	}
	
	@SuppressWarnings("unchecked")
	private static List<HashMap<String, Object>> xml_information(List<String> file) throws IOException {
		
		HashMap<String,String> ec2go = GetEc2Go();
		HashMap<String,String> sl2go = GetSl2Go();
		HashMap<String,String> interpro2go = GetInterPro2Go();
		
		List<HashMap<String, Object>> domains = new ArrayList<HashMap<String, Object>>();
		
		Pattern matchPattern = Pattern.compile("<([^/]+)-match");
		Pattern descPattern = Pattern.compile("desc=\"(.+?)\"");
		Pattern typePattern = Pattern.compile("type=\"(.+?)\"");
		Pattern evaluePattern = Pattern.compile("evalue=\"(.+?)\"");
		Pattern scorePattern = Pattern.compile("score=\"(.+?)\"");
		Pattern familyNamePattern = Pattern.compile("familyName=\"(.+?)\"");
		Pattern acPattern = Pattern.compile("ac=\"(.+?)\"");
		Pattern namePattern = Pattern.compile("name=\"(.+?)\"");
		Pattern universalPattern = Pattern.compile("\"(.+?)\"");
		Pattern libraryPattern = Pattern.compile("library=\"(.+?)\"");
		Pattern versionPattern = Pattern.compile("version=\"(.+?)\"");
		
		for (int i = 6; i < file.size(); i++) {
			
			Matcher matchMatcher = matchPattern.matcher(file.get(i));
			
			if (matchMatcher.find()){
				
				HashMap<String,Object> domain = new HashMap<String,Object>();
				
				Matcher evalueMatcher = evaluePattern.matcher(file.get(i));
				if (evalueMatcher.find()){domain.put("E-value",Float.parseFloat(evalueMatcher.group(1)));}
				
				Matcher scoreMatcher = scorePattern.matcher(file.get(i));
				if (scoreMatcher.find()){domain.put("Score",Float.parseFloat(scoreMatcher.group(1)));}
				
				Matcher familyNameMatcher = familyNamePattern.matcher(file.get(i));
				if (familyNameMatcher.find()){domain.put("Family_Name",familyNameMatcher.group(1));}
				
				i++;
		
				Matcher acMatcher = acPattern.matcher(file.get(i));
				if (acMatcher.find()){domain.put("Signature_accession",acMatcher.group(1));}
				
				Matcher nameMatcher = namePattern.matcher(file.get(i));
				if (nameMatcher.find()){domain.put("Name",nameMatcher.group(1));}
				
				i++;
				
				if (file.get(i).trim().startsWith("<entry")) {
					
					Matcher entryAcMatcher = acPattern.matcher(file.get(i));
					if (entryAcMatcher.find()) {domain.put("Entry_accession",entryAcMatcher.group(1));}
					
					Matcher descMatcher = descPattern.matcher(file.get(i));
					if (descMatcher.find()) {domain.put("Entry_description", descMatcher.group(1));}
					
					Matcher entryNameMatcher = namePattern.matcher(file.get(i));
					if (entryNameMatcher.find()) {domain.put("Entry_name", entryNameMatcher.group(1));}
					
					Matcher typeMatcher = typePattern.matcher(file.get(i));
					if (typeMatcher.find()) {domain.put("Entry_type", entryNameMatcher.group(1));}
					
					i++;
					
					List<List<String>> allXref = new ArrayList<List<String>>();
					
					//entries with Xrefs end in </entry>, but those that lack xRefs end at the start of the models
					while(!(file.get(i).trim().startsWith("</entry>") || file.get(i).trim().startsWith("<models>")) && i < file.size()) {
						 Matcher xrefMatcher = universalPattern.matcher(file.get(i));
						 
						 List<String> xref = new ArrayList<String>();
						 
						 while (xrefMatcher.find()) {

							 xref.add(xrefMatcher.group().replace("\"", ""));
						 }
						 
						 allXref.add(xref);
							
						 i++;
					}
					
					if (allXref.size() > 0) {
						
						domain.put("Xrefs", allXref);
						
						i++;
					}
				}
				
				if (file.get(i).trim().startsWith("<models>")) {
					
					i++;
				
					List<List<String>> allModels = new ArrayList<List<String>>();
					
					while(!(file.get(i).trim().startsWith("</models>")) && i < file.size()) {
						 Matcher modelMatcher = universalPattern.matcher(file.get(i));
						 
						 List<String> Model = new ArrayList<String>();
						 
						 while (modelMatcher.find()) {

							 Model.add(modelMatcher.group().replace("\"", ""));
							 
							 if (Model.size() > 2) {
								 String t = Model.get(1);				// To permute between name and description, so that name is always 2nd element
								 Model.add(1, Model.get(2));
								 Model.add(2, t);
							 }
						 }
						 
						 allModels.add(Model);
							
						 i++;
					}
					
					domain.put("Models", allModels);
					
					i++;
					
				}
				
				Matcher libraryMatcher = libraryPattern.matcher(file.get(i));
				if (libraryMatcher.find()){domain.put("Library",libraryMatcher.group(1));}
				
				Matcher versionMatcher = versionPattern.matcher(file.get(i));
				if (versionMatcher.find()){domain.put("Library_version",versionMatcher.group(1));}
					
				i += 3;
				
				List<List<Float>> allLocations = new ArrayList<List<Float>>();
				
				while (!(file.get(i).trim().startsWith("</locations>"))){
					
					 Matcher locationMatcher = universalPattern.matcher(file.get(i));
					
					 List<Float> Location = new ArrayList<Float>();
					 
					 while (locationMatcher.find()) {

						String data = locationMatcher.group().replace("\"", "");
						 
						if (!(data.equals("NONE"))){Location.add(Float.parseFloat(data));}
						 
						if (Location.size() > 2) {
							 
							if (Location.size() == 3) {
								Float t = Location.get(0);				// To put score from 1st to last position
								Location.add(0, Location.get(1));
								Location.add(1, Location.get(2));
								Location.add(2, t);
							} else if(Location.size() == 7) {
								Float score = Location.get(0);
								Float start = Location.get(5);
								Float end = Location.get(6);
								Float hmmstart = Location.get(2);
								Float hmmend = Location.get(3);
								Float hmmlength = Location.get(4);
								Float evalue = Location.get(1);
								Location.add(0,start);
								Location.add(1,end);
								Location.add(2,score);
								Location.add(3,evalue);
								Location.add(4,hmmstart);
								Location.add(5,hmmend);
								Location.add(6,hmmlength);
							} else if(Location.size() == 9) {
								Float score = Location.get(2);
								Float start = Location.get(7);
								Float end = Location.get(8);
								Float estart = Location.get(0);
								Float eend = Location.get(1);
								Float hmmstart = Location.get(4);
								Float hmmend = Location.get(5);
								Float hmmlength = Location.get(6);
								Float evalue = Location.get(3);
								Location.add(0,start);
								Location.add(1,end);
								Location.add(2,score);
								Location.add(3,evalue);
								Location.add(4,hmmstart);
								Location.add(5,hmmend);
								Location.add(6,hmmlength);
								Location.add(7,estart);
								Location.add(8,eend);
							}
						}
					}
					 
					allLocations.add(Location);							//Location information: Start, End, Score, E-value, HMM-Start, HMM-End, HMM-Length, Env-Start, Env-End
					
//					HashMap<String,Float> location = new HashMap<String,Float>();
//					
//					Matcher locationScoreMatcher = scorePattern.matcher(file.get(i));
//					if (locationScoreMatcher.find()){location.put("Score",Float.parseFloat((locationScoreMatcher.group(1))));}
//					
//					Matcher locationEvalueMatcher = evaluePattern.matcher(file.get(i));
//					if (locationEvalueMatcher.find()){location.put("E-value",Float.parseFloat((locationEvalueMatcher.group(1))));}
//					
//					Matcher hmmStartMatcher = hmmStartPattern.matcher(file.get(i));
//					if (hmmStartMatcher.find()){location.put("HMM-Start",Float.parseFloat((hmmStartMatcher.group(1))));}
//					
//					Matcher hmmEndMatcher = hmmEndPattern.matcher(file.get(i));
//					if (hmmEndMatcher.find()){location.put("HMM-End",Float.parseFloat(hmmEndMatcher.group(1)));}
//					
//					Matcher lengthMatcher = lengthPattern.matcher(file.get(i));
//					if (lengthMatcher.find()){location.put("HMM-Length",Float.parseFloat(lengthMatcher.group(1)));}
//				
//					Matcher startMatcher = startPattern.matcher(file.get(i));
//					if (startMatcher.find()){location.put("Start",Float.parseFloat(startMatcher.group(1)));}
//					
//					Matcher endMatcher = endPattern.matcher(file.get(i));
//					if (endMatcher.find()){location.put("End",Float.parseFloat(endMatcher.group(1)));}
//					
//					if(location.size()>0) {allLocations.add(location);}
					
					i++;
				
				}
				
				if (domain.containsKey("Xrefs")){
					List<List<String>> xrefs = (List<List<String>>) domain.get("Xrefs");
					for (List<String>xref:xrefs){
						if (ec2go.containsKey(xref.get(2))){
							String ec = ec2go.get(xref.get(2));
							domain.put("EC",ec);
						}
						if (sl2go.containsKey(xref.get(2))){
							String name = sl2go.get(xref.get(2));
							domain.put("Name",name);
						}
						if (interpro2go.containsKey(xref.get(2))){
							String localization = interpro2go.get(xref.get(2));
							domain.put("Subcellular localization",localization);
						}
				
					}
				}
				
				domain.put("Locations", allLocations);
				
				System.out.println(domain);
					
				domains.add(domain);
			}
		}
		
		return domains;	
	}

	public static void main(String[] args) throws IOException{
		
//		for (HashMap<String, Object>domain:get_xml_information()){
//			
////			List<HashMap<String,Float>> Locations = new ArrayList<HashMap<String,Float>> (domain.get("Locations"));
//			
////			ArrayList<List<String>>Models = new ArrayList<List<String>>(domain.get("Models"));
//			
//			System.out.println(domain);
//		}
		System.out.println(GetSl2Go());
	}
}
