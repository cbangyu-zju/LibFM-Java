package kakao.data;

// import org.la4j.vector.dense.DenseVector;
import no.uib.cipr.matrix.DenseVector;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class DataMetaInfo {
	
	public DenseVector attrGroup;
	public int numAttrGroups;
	public DenseVector numAttrPerGroup;
	public int numRelations;
	
	public DataMetaInfo(int numAttributes) {
		attrGroup = new DenseVector(numAttributes+1);
		numAttrGroups = 1;
		numAttrPerGroup = new DenseVector(numAttrGroups+1);
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
			numAttrPerGroup = new DenseVector(numAttrGroups+1);
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
