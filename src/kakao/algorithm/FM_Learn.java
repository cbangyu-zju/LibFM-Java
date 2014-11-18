package kakao.algorithm;
import kakao.data.Data;
import kakao.data.DataMetaInfo;

import java.util.Date;

// import org.la4j.matrix.dense.Basic2DMatrix;
import no.uib.cipr.matrix.DenseMatrix;
// import org.la4j.vector.dense.BasicVector;
import no.uib.cipr.matrix.DenseVector;

public class FM_Learn {
	protected DenseVector sum, sumSqr;
	protected DenseMatrix pred_q_term;
	public DataMetaInfo meta;
	public FM_Model fm;
	public double minTarget, maxTarget;
	
	public int task;
	static final int TASK_REGRESSION = 0;
	static final int TASK_CLASSIFICATION = 1;
	
	public Data validation;

	public double predict_case(Data data, int rowId) {
		return fm.predict(data.sparseData[rowId]);
	}
	
	public FM_Learn() {
		this.task = 0;
		this.meta = null;
	}
	
	public void init() {
		sum = new DenseVector(fm.numFactor+1);
		sumSqr = new DenseVector(fm.numFactor+1);
		pred_q_term = new DenseMatrix(fm.numFactor,meta.numRelations+2);
	}
	
	public double evaluate(Data data) {
		double result = 0;
		if (task == TASK_REGRESSION) {
			result = evaluate_regression(data);
		} else if (task == TASK_CLASSIFICATION) {
			result = evaluate_classification(data);
		} else {
			System.out.println("unknown task");
			return -1;
		}
		return result;
	}
	
	public void learn(Data train, Data test) {}
	
	public void predict(Data data, DenseVector out) {}
	
	protected double evaluate_classification(Data data) {
		int numCorrect = 0;
		long evalTime = new Date().getTime();
		for (int i = 1; i <= data.numRows; i++) {
			double p = predict_case(data,i);
			if (((p >= 0) && (data.target.get(i) >= 0)) || ((p < 0) && (data.target.get(i) < 0))) {
				numCorrect++;
			}
		}
		evalTime = (new Date().getTime()) - evalTime;	// time difference (in ms)
		
		return (double)numCorrect / (double)data.numRows;
	}
	
	protected double evaluate_regression(Data data) {
		double rmse_sumSqr = 0.0;
		double mae_sumAbs = 0.0;
		long evalTime = new Date().getTime();
		for (int i = 1; i <= data.numRows; i++) {
			double p = predict_case(data,i);
			//System.out.println(i+"th p="+p);
			p = Math.min(maxTarget, p);
			p = Math.max(minTarget, p);
			double err = p - data.target.get(i);
			rmse_sumSqr += err*err;
			mae_sumAbs += Math.abs(err);
		}
		evalTime = (new Date().getTime()) - evalTime;	// time difference (in ms)

		return (double)Math.sqrt(rmse_sumSqr/data.numRows);
	}
	
}
