package kakao.data;

import org.la4j.vector.dense.BasicVector;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class DataMetaInfo {
	
	public BasicVector attrGroup;
	public int numAttrGroups;
	public BasicVector numAttrPerGroup;
	public int numRelations;
	
	public DataMetaInfo(int numAttributes) {
		attrGroup = new BasicVector(numAttributes+1);
		numAttrGroups = 1;
		numAttrPerGroup = new BasicVector(numAttrGroups+1);
		numAttrPerGroup.set(1, numAttributes);
	}
	
	public void loadGroupsFromFile(String filename) throws IOException {
		try {
			BufferedReader fData = new BufferedReader(new FileReader(filename));
			String line;
			int currFeatureId;
			while ((line = fData.readLine()) != null) {
				currFeatureId = Integer.parseInt(line);
				numAttrGroups = Math.max(currFeatureId, numAttrGroups);
			}
			fData.close();
			numAttrPerGroup = new BasicVector(numAttrGroups+1);
			fData = new BufferedReader(new FileReader(filename));
			while ((line = fData.readLine()) != null) {
				currFeatureId = Integer.parseInt(line);
				numAttrPerGroup.set(currFeatureId, numAttrPerGroup.get(currFeatureId)+1);
			}
			fData.close();
		} catch (IOException e) {
			System.out.println("unable to open " + filename);
		}
	}

}
