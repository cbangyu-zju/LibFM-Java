package kakao.algorithm;
import kakao.data.SparseRow;

import java.util.Random;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

public class FM_Model {
	private BasicVector mSum, mSumSqr;
	public BasicVector w;
	public Basic2DMatrix v;
	public double w0;
	public int numAttr;
	public boolean k0, k1;
	public int numFactor;
	
	public double reg0;
	public double regw, regv;
	public double initStdev;
	public double initMean;
	
	public FM_Model() {
		numFactor = 0;
		initMean = 0;
		initStdev = 0.01;
		reg0 = 0.0;
		regw = 0.0;
		regv = 0.0;
		k0 = true;
		k1 = true;
	}
	
	public void init() {
		w0 = 0;
		w = new BasicVector(numAttr+1);
		v = new Basic2DMatrix(numFactor+1, numAttr+1);
		Random rand = new Random();
		for (int i = 1; i <= numFactor; i++) {
			for (int j = 1; j <= numAttr; j++) {
				v.set(i, j, rand.nextGaussian()*initStdev+initMean);
			}
		}
		mSum = new BasicVector(numFactor+1);
		mSumSqr = new BasicVector(numFactor+1);
	}
	
	public double predict(SparseRow x) {
		return predict(x, mSum, mSumSqr);
	}
	
	public double predict(SparseRow x, BasicVector sum, BasicVector sumSqr) {
		double result = 0;
		if (k0) {
			result += w0;
		}
		if (k1) {
			for (int key : x.getKeySet()) {
				result += w.get(key) * x.get(key);
			}
		}
		for (int f = 1; f <= numFactor; f++) {
			sum.set(f, 0);
			sumSqr.set(f, 0);
			for (int key : x.getKeySet()) {
				double d = v.get(f, key) * x.get(key);
				sum.set(f, sum.get(f)+d);
				sumSqr.set(f, sumSqr.get(f)+d*d);
			}
			result += 0.5*(sum.get(f)*sum.get(f) - sumSqr.get(f));
		}
		return result;
	}
}