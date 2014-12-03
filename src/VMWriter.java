import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class VMWriter{
	private FileWriter outputFile;
	private HashMap<String, String> arithmeticOps;
	
	//Creates a new file and prepares it for writing.
	public VMWriter(File vmFile) throws IOException{
		// TODO Auto-generated constructor stub
		outputFile = new FileWriter(vmFile.getAbsoluteFile());
		//This use of hashMap was found at stackoverflow
		//From:http://stackoverflow.com/questions/6802483/how-to-directly-initialize-a-hashmap-in-a-literal-way
		arithmeticOps = new HashMap<String, String>(){{
			put("+","add");
			put("-","sub");
			put("<","lt");
			put(">","gt");
			put("=","eq");
			put("&","and");
			put("|","or");
			put("~","not");}};
	}
	
	//Writes a VM push command
	public void writePush(String segment, int index) throws IOException{
		outputFile.write("push"+segment+" "+index+"\n");
	}
	
	//Writes a VM pop command
	public void writePop(String segment, int index) throws IOException{
		outputFile.write("pop"+segment+" "+index+"\n");
	}
	
	//Writes a VM arithmetic command
	public void writeArithmetic(String command) throws IOException{
		if(arithmeticOps.containsKey(command)){
			outputFile.write(arithmeticOps.get(command)+'\n');
		}else{
			outputFile.write(command.toLowerCase()+'\n');
		}
	}
	
	//Writes a VM label command
	public void writeLabel(String label) throws IOException{
		
	}
	
	//Writes a VM goto command
	public void writeGoto(String label) throws IOException{
		
	}
	
	//Writes a VM if-goto command
	public void writeIf(String label) throws IOException{
		
	}
	
	//Writes a VM call command
	public void writeCall(String label) throws IOException{
		
	}
	
	//Writes a VM function command
	public void writeFunction(String label) throws IOException{
		
	}
	
	//Writes a VM return command
	public void writeReturn() throws IOException{
		outputFile.write('\n');
	}
	
	//Closes the output file
	public void close() throws IOException{
		outputFile.close();
	}

}
