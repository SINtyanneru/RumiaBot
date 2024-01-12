/**
 * URIパラメーターを解析してArrayにするやつ
 * @param {URIだよFuck} URI 
 * @returns 
 */

export function URI_PARAM_DECODE(URI){
	try{
		let URI_PARAM = {};

		for (let I = 0; I < URI.split("?")[1].split("&").length; I++) {
			const PARAM = URI.split("?")[1].split("&")[I].split("=");
			URI_PARAM[PARAM[0]] = PARAM[1];
		}
	
		return URI_PARAM;
	}catch(EX){
		return {};
	}
}