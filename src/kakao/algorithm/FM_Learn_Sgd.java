package kakao.algorithm;
import kakao.data.Data;
import kakao.data.SparseRow;
// import org.la4j.vector.dense.BasicVector;
import no.uib.cipr.matrix.DenseVector;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Date;


public class FM_Learn_Sgd extends FM_Learn {

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
	*/
	
	public int numIter;
	public double learnRate;
	public DenseVector learnRates;
	
	@Override
	public void init() {
		super.init();
		learnRates = new DenseVector(3);
	}
	
	@Override
	public void learn(Data train, Data test) {
		super.learn(train, test);
		System.out.println("learnRate=" + learnRate);
		System.out.println("learnRates=" + learnRates.get(0) + "," + learnRates.get(1) + "," + learnRates.get(2));
		System.out.println("#iterations=" + numIter);
	}
	
	public void SGD(SparseRow x, HashMap<Integer, Integer> userClusterMap, HashMap<Integer, ArrayList<Integer>> clusterInfo, double multiplier, DenseVector sum) {
		fm_SGD(learnRate,x,userClusterMap, clusterInfo,multiplier,sum);
	}
	
	@Override
	public void predict(Data data, DenseVector out) {
		for (int i = 1; i <= data.numRows; i++) {
			double p = predict_case(data,i);
			if (task == TASK_REGRESSION) {
				p = Math.min(maxTarget,p);
				p = Math.max(minTarget,p);
			} else if (task == TASK_CLASSIFICATION) {
				p = 1.0/(1.0 + Math.exp(-p));
			} else {
				System.out.println("task not supported");
			}
			out.set(i, p);
		}
	}
	
	public void fm_SGD(double learnRate, SparseRow x, HashMap<Integer, Integer> userClusterMap, HashMap<Integer, ArrayList<Integer>> clusterInfo, double multiplier, DenseVector sum) {
		if (fm.k0) {
			double w0 = fm.w0;
			w0 -= learnRate * (multiplier + fm.reg0*w0);
			fm.w0 = w0;
		}
		if (fm.k1) {
			for (int key : x.getKeySet()) {
				double w = fm.w.get(key);
				w -= learnRate * (multiplier * x.get(key) + fm.regw*w);
				fm.w.set(key, w);
			}
		}
		for (int f = 1; f <= fm.numFactor; f++) {
			for (int key : x.getKeySet()) {
				long iterTime = new Date().getTime();
				double v = fm.v.get(f, key);
				System.out.println("factor #" + f + ", key #" + key + ", initial v=" + v);
				double grad = sum.get(f) * x.get(key) - v*x.get(key)*x.get(key);

				// FIXME: fixed SGD 
				double clusterSumV = 0.0;
				ArrayList<Integer> currCluster;
				int currClusterSize = 1;
				
				if (fm.regu > 0 && key >= 1 && key <= 10000) {
					currCluster = clusterInfo.get(userClusterMap.get(x.userId));
					Iterator<Integer> it = currCluster.iterator();
					while (it.hasNext()) {
						int curr = it.next();
						clusterSumV += (v - fm.v.get(f, curr));
//						System.out.println("delta=" + (v-fm.v.get(f,curr)) + ", clusterSumV=" + clusterSumV);
					}
					currClusterSize = currCluster.size();
				}
				
				// v -= learnRate * (multiplier * grad + fm.regv*v);
				v -= learnRate * (multiplier * grad + fm.regv*v + fm.regu*(clusterSumV/currClusterSize));
				// /end of fixed

				fm.v.set(f,key,v);
				iterTime = new Date().getTime() - iterTime;		// time difference (in ms)
				System.out.println("factor #" + f + ", key #" + key + ", final v=" + v + "\t iterTime=" + iterTime);
			}
			
		}
	}

}
