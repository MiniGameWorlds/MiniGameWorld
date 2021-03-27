package com.wbm.minigamemaker.util;

public class Counter
{
	private int count=0;
	
	public Counter() {
		this(0);
	}
	
	public Counter(int count) {
		this.count = count;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count=count;
	}

	public void addCount(int amount) {
		this.count+= amount;
	}
	
	public void removeCount(int amount) {
		this.count-= amount;
	}
}
