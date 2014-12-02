
public class SymbolRecord {
	public String name;
	public String type;
	public KIND kind;
	
	public SymbolRecord(String name, String type, KIND kind) {
		this.name = name;
		this.type = type;
		this.kind = kind;
	}
	
	public boolean equals(SymbolRecord record){
		if(record.name.equals(name) && record.type.endsWith(type) && record.kind.equals(kind)){
			return true;
		}else{
			return false;	
		}
	}
	
	public boolean equalsKIND(KIND tempkind){
		if (tempkind.equals(kind)){
			return true;
		}else {
			return false;
		}
	}
	public boolean equalsName(String tempName){
		if (tempName.equals(name)){
			return true;
		}else {
			return false;
		}
	}
	
}
