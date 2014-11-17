package kakao.data;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class SparseRow {
	
	public Map<Integer, Double> data;
	public int size;
	public int clusterId;	// clusterId for the current user
	
	public SparseRow() {
		this.data = new HashMap<Integer, Double>();
		this.size = 0;
		this.clusterId = 0;
	}
	
	public SparseRow add(int featureId, double featureValue) {
		this.data.put(featureId, featureValue);
		this.size++;
		return this;
	}
	
	// FIXME: added methods
	public boolean hasKey(int featureId) {
		if (this.data.get(featureId) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}
	// /end added methods
	
	public double get(int featureId) {
		return this.data.get(featureId);
	}
	
	public Set<Integer> getKeySet() {
		return data.keySet();
	}
	
	public List<Integer> getKeyList() {
		List<Integer> keyList = new ArrayList<Integer>();
		for (int key : this.data.keySet()) {
			keyList.add(key);
		}
		return keyList;
	}
}
