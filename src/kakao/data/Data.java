package kakao.data;

// import org.la4j.matrix.sparse.CRSMatrix;
// import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;
// import kakao.matrix.LinkedSparseMatrix;
// import org.la4j.vector.dense.BasicVector;
import kakao.matrix.LargeSparseMatrix;
import no.uib.cipr.matrix.DenseVector;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class Data {

	protected int cache_size;
	protected boolean has_xt;
	protected boolean has_x;
	
	public LargeSparseMatrix data;
	public LargeSparseMatrix data_t;
	// public List<SparseRow> sparseData;
	public SparseRow[] sparseData;
	// public List<UserInfo> userInfo;
	public HashMap<Integer, ArrayList<Integer>> clusterInfo;
	public DenseVector target;
	public int numRows;	// num of rows
	public int numCols;	 // num of columns 
	public double minTarget;
	public double maxTarget;
	public String filename;

	public Data(int cache_size, boolean has_x, boolean has_xt) {
		this.data = null;
		this.data_t = null;
		this.sparseData = null;
		this.clusterInfo = null;
		this.cache_size = cache_size;
		this.has_x = has_x;
		this.has_xt = has_xt;
		this.target = null;
		this.numRows = 0;
		this.numCols = 0;
		this.minTarget = Double.MAX_VALUE;
		this.maxTarget = -Double.MAX_VALUE;
	}
	
	
	public void load(String filename) throws IOException {
		System.out.println("has x = " + has_x);
		System.out.println("has xt = " + has_xt);
		int numFeature = 0;
		int numValues = 0;
		this.filename = filename;
		
		// (1) Determine the number of rows and the maximum feature_id
		try {
			BufferedReader fData = new BufferedReader(new FileReader(filename));
			StringTokenizer st;
			String line, curr;
			String[] pair;
			int currFeatureId;
			double currTarget;
			while ((line = fData.readLine()) != null) {
                numRows++;
                System.out.println("Processing numRows=" + numRows);
				st = new StringTokenizer(line);
				while (st.hasMoreTokens()) {
					curr = st.nextToken();
					if (isDouble(curr)) {
						currTarget = Double.parseDouble(curr);
						minTarget = Math.min(currTarget, minTarget);
						maxTarget = Math.max(currTarget, maxTarget);
						// debug
						// System.out.println("numRows=" + numRows + "\tcurrTarget=" + currTarget + "\tminTarget=" + minTarget + "\tmaxTarget=" + maxTarget);
						// /debug
					} else if (curr.matches("\\d+:\\d+")) {
						numValues++;
						pair = curr.split(":");
						currFeatureId = Integer.parseInt(pair[0]);
						numFeature = Math.max(currFeatureId, numFeature);
						// debug
						// System.out.println("currFeatureId=" + currFeatureId + "\tnumFeature=" + numFeature + "\tnumValues=" + numValues);
						// /debug
					}
				}
			}
			fData.close();
		} catch (IOException e) {
			System.out.println("unable to open " + filename);
		}
		
		this.numCols = numFeature;
		this.data = new LargeSparseMatrix(filename+".lsm", numRows+1, numCols+1);
		this.target = new DenseVector(numRows+1);
		sparseData = new SparseRow[numRows+1];
		// sparseData = new ArrayList<SparseRow>(numRows+1);
		// for (int i = 0; i <= numRows; i++) { sparseData.add(null); }

		System.out.println("numRows=" + numRows + "\tnumCols=" + numCols + "\tminTarget=" + minTarget + "\tmaxTarget=" + maxTarget);

		
		// (2) Read the data
		try {
			BufferedReader fData = new BufferedReader(new FileReader(filename));
			StringTokenizer st;
			String line, curr;
			String[] pair;
			int currFeatureId;
			double currTarget, currFeatureValue;
			int rowId = 0;
			while ((line = fData.readLine()) != null) {
                rowId++;
                System.out.println("Processing numRows=" + rowId);
				st = new StringTokenizer(line);
				sparseData[rowId] = new SparseRow(30);
                // sparseData.set(rowId, new SparseRow());
				while (st.hasMoreTokens()) {
					curr = st.nextToken();
					if (isDouble(curr)) {
						currTarget = Double.parseDouble(curr);
						target.set(rowId, currTarget);
						// debug
						// System.out.println("rowId=" + rowId + "\tcurrTarget=" + currTarget);
						// /debug
					} else if (curr.matches("\\d+:\\d+")) {
						pair = curr.split(":");
						currFeatureId = Integer.parseInt(pair[0]);
						currFeatureValue = Double.parseDouble(pair[1]);
						sparseData[rowId] = sparseData[rowId].add(currFeatureId, currFeatureValue);
						//sparseData.set(rowId, sparseData.get(rowId).add(currFeatureId, currFeatureValue));
						// data.set(rowId, currFeatureId, currFeatureValue);
						// System.out.println(data.get(rowId, currFeatureId));
						// debug
						// System.out.println("currFeatureId=" + currFeatureId + "\tcurrFeatureValue=" + currFeatureValue);
						// /debug
					}
				}
			}
			fData.close();
		} catch (IOException e) {
			System.out.println("unable to open " + filename);
		}

		System.out.println("numRows=" + numRows + "\tnumCols=" + numCols + "\tminTarget=" + minTarget + "\tmaxTarget=" + maxTarget);

	}

	public void registerUsers(int startUserId, int endUserId, int startClusterId, int endClusterId) {
		clusterInfo = new HashMap<Integer, ArrayList<Integer>>();
		for (int i = 1; i <= numRows; i++) {
			for (int key : sparseData[i].getKeySet()) {
				if (key >= startUserId && key <= endUserId && !clusterInfo.containsKey(key)) {
					for (int j = startClusterId; j <= endClusterId; i++) {
						if (sparseData[i].hasKey(j)) {
							if (clusterInfo.get(j%startClusterId) == null) {
								clusterInfo.put(j%startClusterId, new ArrayList<Integer>());
							} else {
								clusterInfo.get(j%startClusterId).add(key);
							}
							sparseData[i].registerUser(key);
							System.out.println("Added User: userId=" + key + ", clusterId=" + (j%startClusterId));
						}
					}
				}
			}
		}
	}
	
	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	
	// FIXME: added setCluster method
	/*
	public void setClusterIds(int startId, int endId) {
        int rowId = 0;
       	while (rowId < this.numRows) {
        	rowId++;
       		for (int i = startId; i <= endId; i++) {
       			if (this.sparseData.get(rowId).hasKey(i)) {
       				this.sparseData.get(rowId).setClusterId(i%startId);
       				// System.out.println("rowId=" + rowId + " , Set " + i + " as clusterId=" + i%startId);
       			}
        	}
        }
	}
	*/
	// /end added setCluster method
}
