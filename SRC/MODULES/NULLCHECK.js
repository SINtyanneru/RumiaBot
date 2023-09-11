export function NULLCHECK(VAR){
	if(VAR !== undefined && VAR !== null){
		return VAR;
	}else{
		return "Error:NullPointerException";
	}
}