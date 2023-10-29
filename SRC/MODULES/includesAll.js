/**
 *
 * @param {string} str
 * @param  {...string} words
 * @returns { { detected:boolean,value:(string|undefined) } }
 */
export function includesAll(str, ...words) {
	for (const word of words) {
		if (str.includes(word)) {
			console.log("detected:" + word);
			return { value: word, detected: true };
		}
	}
	return { detected: false };
}
