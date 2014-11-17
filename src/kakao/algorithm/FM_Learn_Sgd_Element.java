package kakao.algorithm;
import kakao.data.Data;

import java.util.Date;

public class FM_Learn_Sgd_Element extends FM_Learn_Sgd {

	/*
	protected BasicVector sum, sumSqr;
	protected Basic2DMatrix pred_q_term;
	public DataMetaInfo meta;
	public FM_Model fm;
	public double minTarget, maxTarget;
	
	public int task;
	static final int TASK_REGRESSION = 0;
	static final int TASK_CLASSIFICATION = 1;
	
	public Data validation;
	
	public int numIter;
	public double learnRate;
	public BasicVector learnRates;
	*/

	@Override
	public void init() {
		super.init();
		//log
	}
	
	@Override
	public void learn(Data train, Data test) {
		super.learn(train, test);
		
		System.out.println("SGD: DON'T FORGET TO SHUFFLE THE ROWS IN TRAINING DATA TO GET THE BEST RESULTS.");
		// SGD
		for (int i = 0; i < numIter; i++) {
			long iterTime = new Date().getTime();
			for (int j = 1; j <= train.numRows; j++) {
				double p = fm.predict(train.sparseData.get(j), sum, sumSqr);
				double mult = 0;
				if (task == 0) {
					p = Math.min(maxTarget,p);
					p = Math.max(minTarget,p);
					mult = -(train.target.get(j) - p);
				} else if (task == 1) {
					mult = -train.target.get(j)*(1.0 - 1.0/(1.0+Math.exp(-train.target.get(j)*p)));
				}
				SGD(train.sparseData.get(j), train.sparseData, mult, sum);
			}
			iterTime = new Date().getTime() - iterTime;		// time difference (in ms)
			double rmse_train = evaluate(train);
			double rmse_test = evaluate(test);
			System.out.println("#iter=" + i + "\tTrain=" + rmse_train + "\tTest=" + rmse_test);
		}
	}
}
