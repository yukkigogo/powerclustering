package com.example.powerclusteringvoronoi.model;

public class Edge {
	
	private Bus start;
	private Bus end;
	
	public Edge(Bus s, Bus e) {
		this.start=s;
		this.end=e;
	}
	
	public Bus getStartBus(){ return this.start; }
	public Bus getEndBus(){ return this.end; }
	
}
