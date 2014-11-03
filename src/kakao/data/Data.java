package kakao.data;

import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.sparse.CompressedVector;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class Data {

	public CRSMatrix data;
	public BasicVector target;
	private int numRows;
	public int numCols;
	public int numFeature;	// column number (starting from 1)
	public int numCases;	// row number (starting from 1)
	public double minTarget;
	public double maxTarget;

	public Data() {
		this.data = null;
	}
	
	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public void load(String filename) throws IOException {
		this.data = new CRSMatrix();
		this.target = new BasicVector();
		this.numRows = 0;
		int numValues = 0;
		this.numFeature = 0;
		boolean has_feature = false;
		this.minTarget = Float.MAX_VALUE;
		this.maxTarget = -Float.MAX_VALUE;
		
		// (1) Determine the number of rows and the maximum feature_id
		try {
			BufferedReader fData = new BufferedReader(new FileReader(filename));
			StringTokenizer st;
			String line, curr;
			String[] pair;
			int currFeatureId;
			double currTarget, currFeatureValue;
			while ((line = fData.readLine()) != null) {
                numRows++;
				st = new StringTokenizer(line);
				while (st.hasMoreTokens()) {
					curr = st.nextToken();
					if (isDouble(curr)) {
						currTarget = Double.parseDouble(curr);
						minTarget = Math.min(currTarget, minTarget);
						maxTarget = Math.max(currTarget, maxTarget);
						// debug
						System.out.println("num_rows=" + numRows + "\tcurrTarget=" + currTarget + "\tminTarget=" + minTarget + "\tmaxTarget=" + maxTarget);
						// /debug
					} else if (curr.matches("\\d+:\\d+")) {
						numValues++;
						pair = curr.split(":");
						currFeatureId = Integer.parseInt(pair[0]);
						currFeatureValue = Double.parseDouble(pair[1]);

						numFeature = Math.max(currFeatureId, numFeature);
						data.set(numRows, currFeatureId, currFeatureValue);
						/* debug */
						System.out.println("numFeature=" + numFeature + "\tcurrFeatureId=" + currFeatureId + "\tcurrFeatureValue=" + currFeatureValue);
						/*/debug */
						has_feature = true;
					}
				}
			}
			fData.close();
		} catch (IOException e) {
			System.out.println("unable to open " + filename);
		}

		if (has_feature) { numFeature++; }	
		// number of feature is bigger (by one) than the largest value
		
		// (2) Read the data
		try {
			BufferedReader fData = new BufferedReader(new FileReader(filename));
			StringTokenizer st;
			String line, curr;
			String[] pair;
			int currFeatureId;
			double currTarget, currFeatureValue;
			while ((line = fData.readLine()) != null) {
                numRows++;
				st = new StringTokenizer(line);
				while (st.hasMoreTokens()) {
					curr = st.nextToken();
					if (isDouble(curr)) {
						currTarget = Double.parseDouble(curr);
						minTarget = Math.min(currTarget, minTarget);
						maxTarget = Math.max(currTarget, maxTarget);
						System.out.println("num_rows=" + numRows + "\tcurrTarget=" + currTarget + "\tminTarget=" + minTarget + "\tmaxTarget=" + maxTarget);
/*
						while (target.length() <= numRows) {
							target.add(0.0);
							System.out.println(target.length());
						}
						*/
						target.set(numRows, currTarget);
						// debug
						// /debug
					} else if (curr.matches("\\d+:\\d+")) {
						numValues++;
						pair = curr.split(":");
						currFeatureId = Integer.parseInt(pair[0]);
						currFeatureValue = Double.parseDouble(pair[1]);

						numFeature = Math.max(currFeatureId, numFeature);
						data.set(numRows, currFeatureId, currFeatureValue);
						/* debug */
						System.out.println("numFeature=" + numFeature + "\tcurrFeatureId=" + currFeatureId + "\tcurrFeatureValue=" + currFeatureValue);
						/*/debug */
						has_feature = true;
					}
				}
			}
			fData.close();
		} catch (IOException e) {
			System.out.println("unable to open " + filename);
		}

		if (has_feature) { numFeature++; }	
		// number of feature is bigger (by one) than the largest value
		System.out.println("numRows=" + numRows + "\tnumValues=" + numValues + "\tnumFeatures=" + numFeature + "\tminTarget=" + minTarget + "\tmaxTarget=" + maxTarget);

	}
}
