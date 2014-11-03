package kakao.mf;
import kakao.data.Data;

import java.util.Random;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

public class BasicMatrixFactorization extends MatrixFactorization {
	
	// Training data set represented by sparse matrix 
	private Data trainMatrix;
	// Indicator of training sparse matrix
	private Data logitMatrix;
	
	// User factors
	private Basic2DMatrix userFeatures;
	// Item factors
	private Basic2DMatrix itemFeatures;
	
	// Vector of a user
	private BasicVector Ri;
	// Vector of an item
	private BasicVector Rj;
	
	// Number of users
	private int userNumber;
	// Number of items
	private int itemNumber;
	

	
	/*
	 * Construct LibFM algorithm
	 * 
	 * @param
	 */
	public BasicMatrixFactorization() {
		
	}
	

	public void loadTrain() {
		System.out.println("Loading train...");
		
	}
	
	private void initModel() {
		
	}
	
	// (1) Load the data
	// (1.2) Load relational data
	// (1.3) Load meta data
	
	// (2) Setup the factorization machine
	
	// (3) Setup the learning method
	
	// (4) Init the logging
	
	// (5) Learn
	
	// (6) Prediction at the end (not for mcmc and als)
	
	// (7) Save Prediction
}
