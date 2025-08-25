FROM debian:13

RUN apt update && \
	#gcc
	apt install -y gcc g++ make curl unzip && \
	#Rust
	curl https://sh.rustup.rs -sSf | sh -s -- -y && \
	#Bun
	curl -fsSL https://bun.sh/install | bash && \
	#クリーン
	apt clean

CMD ["bash"]
