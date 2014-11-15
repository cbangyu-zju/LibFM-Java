package kakao.data;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class SparseRow {
	
	public final int kmeansStartId = 10057;	 // TODO: Assuming the starting featureId of kmeans cluster no is 10057
	public int clusterId;	// The clusterId for a single user, according to featureId
	public Map<Integer, Double> data;
	public int size;
	
	public SparseRow() {
		this.data = new HashMap<Integer, Double>();
		this.size = 0;
	}
	
	public SparseRow add(int featureId, double featureValue) {
		this.data.put(featureId, featureValue);
		this.size++;
		// setting clusterId for a single user
		if (featureId >= kmeansStartId) {
			clusterId = featureId % kmeansStartId;
		}
		// /end setting
		return this;
	}
	
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
