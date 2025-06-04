package su.rumishistem.rumiabot.Voicevox.Jomiage;

import java.util.LinkedList;
import java.util.function.Consumer;

public class QueueList<E> extends LinkedList<E> {
	private Consumer<E> onAdd;
	private boolean IsEventRuning = false;

	public void setOnAdd(Consumer<E> onAdd) {
		this.onAdd = onAdd;
	}

	public boolean add(E e) {
		boolean Result = super.add(e);

		if (Result && onAdd != null && !IsEventRuning) {
			IsEventRuning = true;
			try {
				onAdd.accept(e);
			} finally {
				IsEventRuning = false;
			}
		}

		return Result;
	}

	public void RemoveFirst() {
		super.removeFirst();
	}
}
