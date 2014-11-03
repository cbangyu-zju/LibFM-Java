package kakao.mf;

import kakao.util.Predictor;

import org.la4j.vector.Vector;

public abstract class MatrixFactorization implements Predictor {
	
	/* 
	 * Training data set goes here
	 */
	
	public MatrixFactorization() {
	}
	
	@Override
	public abstract void trainModel();
	
	@Override
	public abstract double predict(int userId, int itemId, boolean bound);
	
	public abstract Vector predictItems(int userId, boolean bound);

}
