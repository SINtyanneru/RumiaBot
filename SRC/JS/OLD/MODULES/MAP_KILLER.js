/**
 * Mapを殺してArrayもしくは連想配列に変換します、
 * Mapだけは許さない末代まで呪う畳の上で死ねると思うなよ
 */

//Mapだけは許されないので、MapをArrayにする
export function MAP_KILLER(MAP){
	let RESULT_ARRAY = {};

	let MAP_KEYS = Array.from(MAP.keys());

	for (let I = 0; I < MAP_KEYS.length; I++) {
		const KEY = MAP_KEYS[I];
		const VALUE = MAP.get(KEY);
		RESULT_ARRAY[KEY] = VALUE;
	}

	return RESULT_ARRAY;
}