import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;


public class CompilationEngine {
	private String directory;
	private File[] files;
	private final String keywordOpen = "<keyword> ";
	private final String keywordClose = " </keyword>\n";
	private final String identifierOpen = "<identifier> ";
	private final String identifierClose = " </identifier>\n";
	private final String symbolOpen = "<symbol> ";
	private final String symbolClose = " </symbol>\n";
	private final String integerConstantOpen = "<integerConstant> ";
	private final String integerConstantClose = " </integerConstant>\n";
	private final String stringConstantOpen = "<stringConstant> ";
	private final String stringConstantClose = " </stringConstant>\n";
	private final String classOpen = "<class>\n";
	private final String classClose = "</class>\n";
	private final String varDecOpen = "<varDec>\n";
	private final String varDecClose = "</varDec>\n";
	private final String classVarDecOpen = "<classVarDec>\n";
	private final String classVarDecClose = "</classVarDec>\n";
	private final String subroutineDecOpen = "<subroutineDec>\n";
	private final String subroutineDecClose = "</subroutineDec>\n";
	private final String parameterListOpen = "<parameterList>\n";
	private final String parameterListClose = "</parameterList>\n";
	private final String subroutineBodyOpen = "<subroutineBody>\n";
	private final String subroutineBodyClose = "</subroutineBody>\n";
	
	private final String statementsOpen = "<statements>\n";
	private final String statementsClose = "</statements>\n";
	private final String letStatementOpen = "<letStatement>\n";
	private final String letStatementClose = "</letStatement>\n";
	private final String doStatementOpen = "<doStatement>\n";
	private final String doStatementClose = "</doStatement>\n";
	private final String whileStatementOpen = "<whileStatement>\n";
	private final String whileStatementClose = "</whileStatement>\n";
	private final String ifStatementOpen = "<ifStatement>\n";
	private final String ifStatementClose = "</ifStatement>\n";
	private final String expressionOpen = "<expression>\n";
	private final String expressionClose = "</expression>\n";
	private final String expressionListOpen = "<expressionList>\n";
	private final String expressionListClose = "</expressionList>\n";
	private final String returnStatementOpen = "<returnStatement>\n";
	private final String returnStatementClose = "</returnStatement>\n";
	private final String termOpen = "<term>\n";
	private final String termClose = "</term>\n";
	private final String greaterThan = "&gt;";
	private final String lessThan = "&lt;";
	private final String ampersand = "&amp;";
	private String jackString;
	private StringTokenizer tokenizer;
	private ArrayList<String> keywords;
	private ArrayList<String> symbols;
	private ArrayList<String> unaryOps;
	private ArrayList<String> ops;
	private ArrayList<String> keywordConstants;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String token;
	
	public CompilationEngine(String inDir) throws IOException
	{
		keywords = new ArrayList<String>();
		symbols = new ArrayList<String>();
		unaryOps = new ArrayList<String>();
		ops = new ArrayList<String>();
		keywordConstants = new ArrayList<String>();
		
		//Add all keywords
		keywords.add("class");
		keywords.add("constructor");
		keywords.add("function");
		keywords.add("method");
		keywords.add("field");
		keywords.add("static");
		keywords.add("var");
		keywords.add("int");
		keywords.add("char");
		keywords.add("boolean");
		keywords.add("void");
		keywords.add("true");
		keywords.add("false");
		keywords.add("null");
		keywords.add("this");
		keywords.add("let");
		keywords.add("do");
		keywords.add("if");
		keywords.add("else");
		keywords.add("while");
		keywords.add("return");
		keywords.add("char");
		keywords.add("int");
		keywords.add("String");
		keywords.add("boolean");
		
		//Add all symbols
		symbols.add("{");
		symbols.add("}");
		symbols.add("(");
		symbols.add(")");
		symbols.add("[");
		symbols.add("]");
		symbols.add(".");
		symbols.add(",");
		symbols.add(";");
		symbols.add("+");
		symbols.add("-");
		symbols.add("*");
		symbols.add("/");
		symbols.add(ampersand);
		symbols.add("|");
		symbols.add(lessThan);
		symbols.add(greaterThan);
		symbols.add("=");
		symbols.add("~");
		symbols.add(">=");
		symbols.add("<=");

		//unaryOps
		unaryOps.add("-");
		unaryOps.add("~");
		
		//Operators
		ops.add("+");
		ops.add("-");
		ops.add("*");
		ops.add("/");
		ops.add("&");
		ops.add("|");
		ops.add("<");
		ops.add(">");
		ops.add("=");
		
		//keyword Constants
		keywordConstants.add("true");
		keywordConstants.add("false");
		keywordConstants.add("null");
		keywordConstants.add("this");
		
		directory = inDir;
		File dir = new File(directory);
		
		//I found this code on stackoverflow and altered it to pull all vm files
	    //it makes a file array of all the files in a given directory
		files = new File[dir.listFiles(new FilenameFilter() 
		{ 
	         public boolean accept(File dir, String filename)
	         { 
	        	 return filename.endsWith(".jack"); 
	         }
	    }).length];
	    //end of stackoverflow code	
	    
		
		files = dir.listFiles(new FilenameFilter() 
				{ 
	    	         public boolean accept(File dir, String filename)
	    	         { 
	    	        	 return filename.endsWith(".jack"); 
	    	         }
	    	    });
		
		//for all the files in the directory
		for(int i=0; i<files.length; i++)
		{
			//get jack file and create xml file
			File jackFile = files[i];
			File xmlFile = new File(dir,files[i].getName().replaceAll(".jack", ".xml"));
			FileInputStream inStream= new FileInputStream(jackFile);
			xmlFile.createNewFile();
			
			//create readers and writers
			reader = new BufferedReader(new InputStreamReader(inStream));
			writer = new BufferedWriter(new FileWriter(xmlFile.getAbsoluteFile()));
			//remove all comments and unneeded space in jack file
			removeComments(jackFile);
			
			//turn the file into a string
			jackString = fileToString(jackFile.getName().replace(".jack", ".tmp"));
			//tokenize file string
			tokenizer = new StringTokenizer(jackString);
			try {
				//begin compiling
				compileClass();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reader.close();
			writer.close();
		}
	}

	

	private String fileToString(String tempFileName) throws IOException
	{
		String fileString = "";
		File tempFile = new File(tempFileName);
		Scanner scanner =  new Scanner(tempFile);
		String line = "";
		while(scanner.hasNext())
		{
			line = scanner.nextLine();
			if(line.contains("()"))
			{
				line = line.replaceAll("\\(", " ( ");
				line = line.replaceAll("\\)", " ) ");
				if(line.endsWith(");"))
				{
					line = line.substring(0, line.indexOf(")")) + " ) ;";
					
				}
				else
				{
					
					line = (line.substring(0, line.indexOf("("))+ " ( ) " +line.substring(line.indexOf(")")+1));
					
				}
				
			}
			else if(line.contains("(") && line.contains(")"))
			{
				line = line.replaceAll("\\(", " ( ");
				line = line.replaceAll("\\)", " ) ");
				
				
			}
			else if(line.contains("("))
			{
				
				line = line.replaceAll("\\(", " ( ");
			}
			if(line.endsWith(");"))
			{
				line = line.substring(0, line.indexOf(")")) + " ) ;";
				
			}
			
			if(line.contains("["))
			{
				if(line.contains("];"))
				{
					line = line.substring(0, line.indexOf("[")) + " [ " + 
                            line.substring(line.indexOf("[")+1, line.indexOf("]")) + " ] ;";
				}
				else
					line = line.substring(0, line.indexOf("[")) + " [ " + 
			                            line.substring(line.indexOf("[")+1, line.indexOf("]"))+ " " + line.substring(line.indexOf("]"));
				
			}
			
			else if(line.contains(";"))
			{
				line = line.substring(0, line.indexOf(";")) + " ;";
			}
			if(line.contains(","))
			{
				line = line.replaceAll(",", " ,");
			}
			if(line.contains("\""))
			{
				line = line.replaceAll("\"", " \" ");
				
						
			}
			if(line.contains("~"))
			{
				line = line.substring(0, line.indexOf("~")+1) + " " + line.substring(line.indexOf("~")+1);
				
			}
			fileString += " " + line;
		}
		scanner.close();
		
		//System.out.println(fileString);
		return fileString;
	}

	public void removeComments(File jackFile) throws IOException
	{
	
		File tempFile = new File(directory,jackFile.getName().replaceAll(".jack", ".tmp"));
		FileInputStream inStream= new FileInputStream(jackFile);
		tempFile.createNewFile();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.getAbsoluteFile()));
		
		String line ="";
		String[] commandString;
		String finalCommandString="";
		while((line = reader.readLine())!=null)
		{
			//remove all comments at the end of lines
			
			if(line.indexOf("//") != -1)
			{
				line = line.substring(0,line.indexOf("//"));
			}
			
			line = line.trim();
			
			if(line.length() == 0)
			{
				
			}
			else if(line.startsWith("/*"))
			{
				while(!line.endsWith("*/"))
				{
					
					line = reader.readLine();
					
				}
			}
			
			else
			{
				commandString = line.split("\\s+");
				
				finalCommandString = "";
				for(String string: commandString)
				{
					finalCommandString += string + " ";
				}
					
				writer.write(finalCommandString.trim()+"\n");
			}
			
			
		}
		
		reader.close();
		writer.close();
	}
	
	//advance forward one token
	//used to save time typing
	private void advance()
	{
		token = tokenizer.nextToken();
		
	}
	
	private void compileClass() throws IOException{
		//write opening class tag 
		//<class>
		writer.write(classOpen);
		//get next token
		token = tokenizer.nextToken();
		
		//write class keyword
		//<keyword> Class <keyword>
		writer.write(keywordOpen);
		writer.write(token);
		writer.write(keywordClose);
		
		//get next token
		//should be class name
		//then compile class name
		advance();
		compileClassName();
		
		// after compiling the class name the next token should be
		// a curly brace, if not display error and exit
		if(!symbols.contains(token) || !token.equals("{"))
		{
			
			System.out.println("Class ERROR");
			System.out.println(token);
			System.exit(0);
		}
		//write the curly brace into xml file
		// then move to next token
		writeSymbol(token);
		advance();
		
		//if there is class variables the next token should be a static or field
		//continue until all class variables are compiled
		while(token.equals("static") || token.equals("field"))
		{
			compileClassVarDec();
		}
		//after the class vars have been compiled
		//do the same for all class subroutines
		while(token.equals("constructor") || token.equals("function")|| token.equals("method"))
		{
			compileClassSubroutineDec();
			
		}
		//after all variables and subroutines are compiled
		//the next token should be a closing bracket
		//if it is not exit
		if(!token.equals("}"))
		{
			System.out.println("Class ERROR");
			System.out.println(token);
			System.exit(0);
		}
		//write closing bracket
		writeSymbol(token);
		
		//end with closing class tag
		writer.write(classClose);
		
	}
	private void compileClassSubroutineDec() throws IOException {
		//write opening subroutine tag
		writer.write(subroutineDecOpen);
		//write the constructor/method/function tag
		//then get next token
		writeKeyword(token);
		advance();
		
		//the next token should be the return type
		//if the return type is a keyword (int, string, etc)
		//write keyword tag
		//else the return type is an identifier
		if(keywords.contains(token))
		{
			writeKeyword(token);
		}
		else
		{
			writeIdentifier(token);
		}
		//get next token
		advance();
		//compile the name of the subroutine
		compileSubroutineName();
		
		//the next token after compiling the subroutine name
		//should be an opening parenthesis
		if(!token.equals("("))
		{

			System.out.println("subDec ERROR");
			System.out.println(token);
			System.exit(0);
		}
		//write the symboltag for the parenthesis
		//and get next token
		writeSymbol(token);
		advance();
		//next token sould be the parameter list
		compileParameterList();
		
		//after the parameterList is compiled the next token
		//should be a closing parenthesis
		if(!token.equals(")"))
		{
			
			System.out.println("subDec ERROR");
			System.out.println(token);
			System.exit(0);
		}
		//write closing parenthesis
		writeSymbol(token);
		advance();
		//next token should be }
		if(!token.equals("{"))
		{
			
			System.out.println("subDec ERROR");
			System.out.println(token);
			System.exit(0);
		}
	    
		//compile the body
		//this will handle consuming the opening and closing braces
		compileSubroutineBody();
		
		/*if(!token.equals("}"))
		{
			System.out.println("subDec ERROR");
			System.out.println(token);
			System.exit(0);
		}*/
		
		//add closing subroutine tag
		writer.write(subroutineDecClose);
	}



	private void compileSubroutineBody() throws IOException {
		//write subroutine body opening tag
		writer.write(subroutineBodyOpen);
		
		//the first token should be an opening curly brace
		if(!token.equals("{"))
		{
			System.out.println("subBody ERROR");
			System.out.println(token);
			System.exit(0);
		}
		//write curly brace
		//get next token
		writeSymbol(token);
		advance();
		
		//compile all the local variables
		//each local starts with var
		//continue for all locals
		while(token.equals("var"))
		{
			compileVarDec();
		}
		
		//compile all statements
		while(token.equals("let") || token.equals("if") || token.equals("while") ||
				token.equals("do") || token.equals("return") )
		{
			compileStatements();
		}
		
		//when all local vars and statements are finsished
		//the last token should be a closing bracket
		if(!token.equals("}"))
		{
			System.out.println("subBody ERROR");
			System.out.println(token);
			System.exit(0);
		}
		//write closing bracket
		//and closing subroutine body tag
		writeSymbol(token);
		advance();
		writer.write(subroutineBodyClose);
	}



	private void compileStatement() throws IOException {
		
		//checks what kind of statement and calls the appropriate statement
		switch(token)
		{
			case "let":
				compileLet();
				break;
			case "if":
				compileIf();
				break;
			case "while":
				compileWhile();
				break;
			case "do":
				compileDo();
				break;
			case "return":
				compileReturn();
				break;
		}
		
	}



	private void compileReturn() throws IOException {
		//write <returnStatement>
		writer.write(returnStatementOpen);
		//the first token in a return statement should be return
		if(!token.equals("return"))
		{
			
			System.out.println("return ERROR");
			System.out.println(token);
			System.exit(0);
		}
		
		writeKeyword(token);
		advance();
		
		//if the next token is a semi-colon
		//the return type is void
		//if not then compile the return expression
		if(!token.equals(";"))
		{
			
			compileExpression();
		}
		
		//after the return expression is compiled
		//the next token should be a semi-colon
		if(!token.equals(";"))
		{
			System.out.println("return ERROR");
			System.out.println(token);
			System.exit(0);
		}
		
		//write semi-colon
		writeSymbol(token);
		advance();
		//write closing return statement tag
		writer.write(returnStatementClose);
	}



	private void compileDo() throws IOException {
		
		//write <doStatement>
		writer.write(doStatementOpen);
		
		//the first token should be do
		if(!token.equals("do"))
		{
			System.out.println("do ERROR");
			System.out.println(token);
			System.exit(0);
		}
		
		//write keyword tag for do
		writeKeyword(token);
		advance();
		
		//after do there should be a subroutine call
		compileSubroutineCall();
		
		//the token after the subroutine call should be a semicolon
		
		if(!token.equals(";"))
		{
			System.out.println("do ERROR");
			System.out.println(token);
			System.exit(0);
		}
		
		//write semicolon tag
		writeSymbol(token);
		advance();
		
		//write </doStatement>
		writer.write(doStatementClose);
	}



	private void compileSubroutineCall() throws IOException {
		
		//check subroutine call for dot operator
		if(token.contains("."))
		{
			
			//get two strings before and after do operator
			//before.after
			String[] tokeSplit = token.split("\\.");
			
			//write each to xml
			writeIdentifier(tokeSplit[0]);
			writeSymbol(".");
			writeIdentifier(tokeSplit[1]);
			advance();
			
		}
		else
		{
			//if no dot operator
			writeIdentifier(token);
			advance();
		}
		
		//after identifiers have been compiled
		//the next token should be an open parenthesis
		if(!token.equals("("))
		{
			System.out.println("call ERROR");
			System.out.println(token);
			System.exit(0);
		}
		//write parenthesis
		writeSymbol(token);
		advance();
		
		//in the parenthesis of a subroutine call
		//is the expressionlist
		compileExpressionList();
		
		//after the expressionList is compiled
		//the next token should be a semicolon
		if(token.equals(";"))
			return;
		
		//the last token should be a close parenthesis
		if(!token.equals(")"))
		{
			
			System.out.println("call ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeSymbol(token);
		advance();
		
	}



	private void compileExpressionList() throws IOException {
		
		//write opening expressionlist tag
		writer.write(expressionListOpen);
		//if the next token is a closing parenthesis
		//there is no expression list 
		if(token.equals(")"))
		{
			writer.write(expressionListClose);
			return;
		}
		//if there is an expression list
		//compile the next expression in the list
		compileExpression();
		
		//after compiling the first expression the next token
		//should be a comma or a closing parenthesis
		while(token.equals(","))
		{
			//while the next token is a comma
			//compile the next expression
			//and write comma tag
			writeSymbol(token);
			advance();
			compileExpression();
		
		}
		//</ExpressionList>
		writer.write(expressionListClose);
		
	}



	private void compileWhile() throws IOException {
		writer.write(whileStatementOpen);
		if(!token.equals("while"))
		{
			
			System.out.println("while ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeKeyword(token);
		advance();
		if(!token.equals("("))
		{
			
			System.out.println("while ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeSymbol(token);
		advance();
		compileExpression();
		if(!token.equals(")"))
		{	
			System.out.println("while ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeSymbol(token);
		advance();
		if(!token.equals("{"))
		{
			
			
			System.out.println("while ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeSymbol(token);
		advance();
		compileStatements();
		
		if(!token.equals("}"))
		{
			
			System.out.println("while ERROR");
			System.out.println(token);
			System.exit(0);
		}
		
		writeSymbol(token);
		advance();
		
		
		writer.write(whileStatementClose);
	}



	private void compileStatements() throws IOException {
		writer.write(statementsOpen);
		while(token.equals("let") || token.equals("do") || token.equals("if")
				|| token.equals("while") || token.equals("return"))
		{
			compileStatement();
		}
		writer.write(statementsClose);
	}



	private void compileIf() throws IOException {
		writer.write(ifStatementOpen);
		if(!token.equals("if"))
		{
			System.out.println("if ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeKeyword(token);
		advance();
		if(!token.equals("("))
		{
			
			System.out.println("if ERROR");
			System.out.println(token);
			System.exit(0);
		}
		
		writeSymbol(token);
		advance();
		compileExpression();
		
		if(!token.equals(")"))
		{
			System.out.println("if ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeSymbol(token);
		advance();
		if(!token.equals("{"))
		{
			System.out.println("if ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeSymbol(token);
		advance();
		compileStatements();
		if(!token.equals("}"))
		{
			System.out.println("if ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeSymbol(token);
		advance();
		if(token.equals("else"))
		{
			writeKeyword(token);
			advance();
			if(!token.equals("{"))
			{
				System.out.println("if ERROR");
				System.out.println(token);
				System.exit(0);
			}
			writeSymbol(token);
			advance();
			compileStatements();
			if(!token.equals("}"))
			{
				System.out.println("if ERROR");
				System.out.println(token);
				System.exit(0);
			}
			writeSymbol(token);
			advance();
			
		}
		
		
		writer.write(ifStatementClose);
	}



	private void compileLet() throws IOException {
		writer.write(letStatementOpen);
		if(!token.equals("let"))
		{
			System.out.println("let ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeKeyword(token);
		advance();
		writeIdentifier(token);
		advance();
		
		if(token.equals("["))
		{
			
			writeSymbol(token);
			advance();
			compileExpression();
			if(!token.equals("]"))
			{
				
				System.out.println("let ERROR");
				System.out.println(token);
				System.exit(0);
			}
			writeSymbol(token);
			advance();
		}
		if(token.equals(";"))
		{
			writeSymbol(token);
			advance();
		}
		else if(token.equals("="))
		{
			
			writeSymbol(token);
			advance();
			compileExpression();
			if(!token.equals(";"))
			{
				System.out.println("let ERROR");
				System.out.println(token);
				System.exit(0);
			}
			writeSymbol(token);
			advance();
		}
		writer.write(letStatementClose);
	}



	private void compileExpression() throws IOException {
		writer.write(expressionOpen);
		if(unaryOps.contains(token))
		{
			compileTerm();
		}
		
		compileTerm();
		
		while(ops.contains(token))
		{
			writeSymbol(token);
			advance();
			compileTerm();
		}
		writer.write(expressionClose);
	}



	private void compileTerm() throws IOException {
		
		if(token.equals(")"))
		{
			return;
		}
		writer.write(termOpen);
		if(isNumber(token))
		{
			
			writer.write(integerConstantOpen);
			writer.write(token);
			writer.write(integerConstantClose);
			advance();
			
		}
		else if(token.equals("\""))
		{
			
			String stringConst = "";
			advance();
			
			while(!token.equals("\""))
			{
				stringConst += " " +token; 
				advance();
			}
			if(!token.equals("\""))
			{
				System.out.println("string ERROR");
				System.out.println(token);
				System.exit(0);
			}
			stringConst.trim();
			stringConst = stringConst + " ";
			System.out.println(stringConst);
			writer.write(stringConstantOpen);
			writer.write(stringConst);
			writer.write(stringConstantClose);
			advance();
			
			
		}
		else if(token.equals("("))
		{
			
			writeSymbol(token);
			advance();
			compileExpression();
			if(!token.equals(")"))
			{
				System.out.println("term ERROR");
				System.out.println(token);
				System.exit(0);
			}
			
			writeSymbol(token);
			advance();
		}
		else if(unaryOps.contains(token))
		{		
			writeSymbol(token);	
			advance();
			compileTerm();
		}
		else if(keywords.contains(token))
		{
			writeKeyword(token);
			advance();
		}
		
		else
		{
			
			String tempToken = "";
			if(token.contains("."))
			{
				compileSubroutineCall();
			}
			else 
			{
				
				
				if(token.equals("["))
				{
					tempToken = token;
					advance();
					writeIdentifier(tempToken);
					writeSymbol(token);
					advance();
					compileExpression();
					if(!token.equals("]"))
					{
						
						System.out.println("Array ERROR");
						System.out.println(token);
						System.exit(0);
					}
					writeSymbol(token);
					advance();
					
				}
				else if(token.equals("("))
				{
					writeIdentifier(tempToken);
				
					writeSymbol(token);
					advance();
					compileExpression();
					
					if(!token.equals(")"))
					{
						
						System.out.println("call ERROR");
						System.out.println(token);
						System.exit(0);
					}
					writeSymbol(token);
					advance();
				}
				else
				{
					if(tokenizer.peek().equals("["))
					{
						
						writeIdentifier(token);
						advance();
						
						
						writeSymbol(token);
						advance();
						compileExpression();
						if(!token.equals("]"))
						{
							System.out.println("Array ERROR");
							System.out.println(token);
							writer.close();
							System.exit(0);
						}
						writeSymbol(token);
						advance();
					}
					else
					{
						writeIdentifier(token);
						advance();
					}
				}
				
				
			}
		}
		writer.write(termClose);
	}



	private void compileVarDec() throws IOException {
		writer.write(varDecOpen);
		if(!token.equals("var"))
		{
			System.out.println("varDec ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeKeyword(token);
		advance();
		if(keywords.contains(token))
		{
			writeKeyword(token);
		}
		else
		{
			writeIdentifier(token);
		}
		advance();
		writeIdentifier(token);
		advance();
		while(token.equals(","))
		{
			writeSymbol(token);
			advance();
			writeIdentifier(token);
			advance();
		}
		if(!token.equals(";"))
		{
			System.out.println("varDec ERROR");
			System.out.println(token);
			System.exit(0);
		}
		writeSymbol(token);
		advance();
		writer.write(varDecClose);
	}



	private void compileParameterList() throws IOException {
		writer.write(parameterListOpen);
		if(token.equals(")"))
		{
			writer.write(parameterListClose);
			return;
		}
		else
		{
			if(keywords.contains(token))
			{
				writeKeyword(token);
			}
			else
			{
				writeIdentifier(token);
			}
			advance();
			writeIdentifier(token);
			advance();
			while(token.equals(","))
			{
				writeSymbol(token);
				advance();
				if(keywords.contains(token))
				{
					writeKeyword(token);
				}
				else
				{
					writeIdentifier(token);
				}
				advance();
				writeIdentifier(token);
				advance();
			}
		}
		writer.write(parameterListClose);
		
	}



	private void compileSubroutineName() throws IOException {
		writeIdentifier(token);
		advance();
		
	}



	private void compileClassVarDec() throws IOException {
		writer.write(classVarDecOpen);
		if(!token.equals("field") || token.equals("static"))
		{
			System.out.println("ERROR");
		}
		writeKeyword(token);
		advance();
		if(keywords.contains(token))
		{
			writeKeyword(token);
		}
		else
		{
			writeIdentifier(token);
		}
		do
		{
			advance();
			writeIdentifier(token);
			advance();
			if(token.equals(","))
			{
				writeSymbol(token);
			}
		}while(token.equals(","));
		
		if(!token.equals(";"))
		{
			System.out.println("ERROR");
		}
		writeSymbol(token);
		advance();
		writer.write(classVarDecClose);
	}



	private void compileClassName() throws IOException {
		//write class name
		writeIdentifier(token);
        advance();
		
		
	}

	private void writeIdentifier(String token) throws IOException
	{
		writer.write(identifierOpen);
		writer.write(token);
		writer.write(identifierClose);
	}
	private void writeKeyword(String token) throws IOException
	{
		writer.write(keywordOpen);
		writer.write(token);
		writer.write(keywordClose);
	}
	private void writeSymbol(String token) throws IOException
	{
		if(token.equals("<"))
		{
			token =lessThan;
		}
		else if(token.equals(">"))
		{
			token = greaterThan;
		}
		else if(token.equals("&"))
		{
			token =ampersand;
		}
		
		writer.write(symbolOpen);
		writer.write(token);
		writer.write(symbolClose);
	}

	private static boolean isNumber(String string) {
	    try {
	        Integer.parseInt(string);
	    } catch (Exception e) {
	        return false;
	    }
	    return true;
	}
	public static void main(String[] args) throws IOException
	{
		CompilationEngine comp = new CompilationEngine("C:\\Users\\Isaac\\Desktop\\Desktop\\School\\Mines\\Fall2014\\ComputingElements\\CSCI410\\Compiler");
		
	}
}
