import java.util.HashMap;


public class SymbolTable {
	//Two hash tables, One for the class scope and another for the subroutine scope.
	//Class Scope table:
	// **SymbolRecord(String name, String type, KIND kind)**
	private HashMap<SymbolRecord,Integer> classScope;
	private int classStaticCount;
	private int classHeapCount;
	
	//Subroutine Scope
	private HashMap<SymbolRecord, Integer> subScope;
	private int subArgCount;
	private int subVarCount;
	
	
	private int ifCount;
	private int whileCount;
	
	//Creates a new empty symbol table.
	public SymbolTable() {
		classScope = new HashMap<SymbolRecord,Integer>();
		classStaticCount = 0;
		classHeapCount = 0;
		subScope = new HashMap<SymbolRecord,Integer>();
		subArgCount=0;
		subVarCount=0;
		ifCount=0;
		whileCount=0;
	}
	
	//Starts a new subroutine scope(i.e resets the subroutine's symbol table)
	public void startSubroutine(){
		subScope.clear();
		subArgCount=0;
		subVarCount=0;
	}
	
	//Defines a new identifier of a given name, type, and kind and assigns it a running index.
	//STATIC and FIELD identifiers have a class scope, while ARG and VAR identifiers have a subroutine scope.
	public void define(String name, String type, KIND kind){
		SymbolRecord temp = new SymbolRecord(name, type, kind);
		if(kind == KIND.STATIC || kind == KIND.FIELD){
			if(kind == KIND.STATIC){
				classScope.put(temp, classStaticCount);
				classStaticCount+=1;
			}else{
				classScope.put(temp, classHeapCount);
				classHeapCount+=1;
			}
		}else{
			if(kind == KIND.ARG){
				subScope.put(temp, subArgCount);
				subArgCount+=1;
			}else{
				subScope.put(temp, subVarCount);
				subVarCount+=1;
			}
		}
	}
	
	//Returns the number of variables of the given kind already defined in the current scope.
	public int varCount(KIND kind){
		int count = 0;
		if(kind == KIND.STATIC || kind == KIND.FIELD){
			count = classHeapCount;
		}else {
			for (SymbolRecord i : subScope.keySet()){
				if (i.equalsKIND(kind)){
					count +=1;
				}
			}
		}
		return count;
	}
	
	//Returns the kind of string of the named identifier in the current scope.
	//If the identifier is unknown in the current scope, returns NONE.
	//TODO
	public KIND kindOf(String name){
		if(subContains(name)){
			
		}else if(classContains(name)){
			
		}else{
			
		}
		return KIND.NONE;
	}
	
	//Returns the type of the named identifier in the current scope.
	//TODO
	public String typeOf(String name){
		if(subContains(name)){
			
		}else if(classContains(name)){
			
		}else{
			
		}
		return "";
	}
	
	//Returns the index assigned to the named identifier.
	//TODO
	public int indexOf(String name){
		//Check if identifier is in the scope of the subroutine first, then the class scope.
		if(subContains(name)){
			return 1;
		}else if(classContains(name)){
			return 0;
		}else{
			return -1;
		}
	}
	
	//Helper Functions:
	//Searches HashMap for the name as an element of the Hashmap
	private boolean classContains(String name){
		boolean flag =false;
		for (SymbolRecord i : classScope.keySet()){
			if (i.equalsName(name)){
				flag=true;
				break;
			}
		}
		return flag;
	}
	
	private boolean subContains(String name){
		boolean flag =false;
		for (SymbolRecord i : subScope.keySet()){
			if (i.equalsName(name)){
				flag=true;
				break;
			}
		}
		return flag;
	}

}
