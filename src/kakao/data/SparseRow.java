package kakao.data;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class SparseRow {
	
	public int[] key;
	public double[] value;
	public int size;
	public int userId;
	
	public SparseRow(int capacity) {
		this.key = new int[capacity];
		this.value = new double[capacity];
		this.size = 0;
		this.userId = 0;
	}
	
	public void registerUser(int userId) {
		this.userId = userId;
	}
	
	public SparseRow add(int featureId, double featureValue) {
		this.key[size] = featureId;
		this.value[size] = featureValue;
		this.size++;
		return this;
	}
	
	public boolean hasKey(int featureId) {
		for (int k : key) {
			if (k == featureId) {
				return true;
			}
		}
		return false;
	}
	
	public double get(int featureId) {
		for (int i = 0; i < size; i++) {
			if (key[i] == featureId) {
				return value[i];
			}
		}
		return -1;
	}
	
	public Set<Integer> getKeySet() {
		Set<Integer> keySet = new HashSet<Integer>();
		for (int k : key) {
			keySet.add(k);
		}
		return keySet;
	}
	
	public List<Integer> getKeyList() {
		List<Integer> keyList = new ArrayList<Integer>();
		for (int k : key) {
			keyList.add(k);
		}
		return keyList;
	}
}
