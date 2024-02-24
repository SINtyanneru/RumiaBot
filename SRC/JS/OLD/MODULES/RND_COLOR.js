/**
 * @returns {`#${string}`}

 */
export function RND_COLOR() {
	const randomColor = Math.floor(Math.random() * 16777215).toString(16);

	//生成された色が6桁未満なら足りない桁を0で埋める
	return "#" + "0".repeat(6 - randomColor.length) + randomColor;
}
