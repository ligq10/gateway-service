package com.changhongit.loving.model;

import java.util.ArrayList;
import java.util.List;

public class SearchGeolocateRequest {
	
	private List<Cell> cellTowers = new ArrayList<>();
	
	public List<Cell> getCellTowers() {
		return cellTowers;
	}
	
	public void setCellTowers(List<Cell> cellTowers) {
		this.cellTowers = cellTowers;
	}
	
	public void addCell(Cell cellIn) {
		this.cellTowers.add(cellIn);
	}
	
}
