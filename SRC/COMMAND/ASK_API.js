import OpenAI from "openai";

const openai = new OpenAI({
	apiKey: "sk-eeARBS3c3689t3woabeU3TAtZ3JBfCfZblkFJkN4qpkRTnmL" // 使えないし、シャッフルしたので問題はない
});

const completion = await openai.completions.create({
	model: "gpt-3.5-turbo",
	messages: [{ role: "user", content: "Hello world" }]
});
console.log(completion.data.choices[0].message);
