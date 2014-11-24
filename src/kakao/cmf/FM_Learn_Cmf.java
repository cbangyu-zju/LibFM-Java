package kakao.cmf;
import kakao.cmf.CSCMatrix;

import cern.colt.matrix.tint.IntFactory1D;
import cern.colt.matrix.tint.IntMatrix1D;	// dense vector (int)
import cern.colt.matrix.tint.IntFactory2D;
import cern.colt.matrix.tint.IntMatrix2D;	// dense vector (int)
import cern.colt.matrix.tdouble.DoubleFactory1D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;		// dense vector (double)
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;		// dense matrix (double)
import cern.colt.matrix.tobject.ObjectFactory1D;
import cern.colt.matrix.tobject.ObjectMatrix1D;		// dense matrix (object)
import java.util.ArrayList;

/*
 * implements relational als that is the core algorithm for all models in this package
 */
public class FM_Learn_Cmf {
	
	private double update(DoubleMatrix1D Us, ObjectMatrix1D Xs, ObjectMatrix1D Xts, IntMatrix2D rc_schema, DoubleMatrix1D r0s, DoubleMatrix1D r1s, DoubleMatrix1D alphas, ObjectMatrix1D modes, IntMatrix1D Ns, int t, double C, int K, int step) {
		assert(t <= Ns.size() && t >= 0);
		DoubleMatrix2D eyeK = DoubleFactory2D.dense.diagonal(DoubleFactory1D.dense.make(K,C));
		int N = Ns.get(t);	// number of instances for type t
		CSCMatrix X;
		double U;
		double V = Us.get(t);
		DoubleMatrix2D A = DoubleFactory2D.dense.make(K,K,0);	 // placeholders for Hessian
		DoubleMatrix1D b = DoubleFactory1D.dense.make(K,0);
		ObjectMatrix1D UtUs = ObjectFactory1D.dense.make((int)Xs.size());
		double change = 0;
		
		for (int j = 0; j < Xs.size(); j++) {
			if (modes.get(j).equals("densemf")) {
				if (rc_schema.get(0,j) == t) {
					U = Us.get(rc_schema.get(1,j));
				} else {
					U = Us.get(rc_schema.get(0,j));
				}
				// TODO: UtUs[j] = numpy.dot(U.T,U)
				UtUs.set(j,U*U);
			}
		}
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < Xs.size(); j++) {
				if (alphas.get(j) == 0) {
					continue;
				}
				if (rc_schema.get(0,j) == t || rc_schema.get(1,j) == t) {
					if (rc_schema.get(0,j) == t) {
						X = (CSCMatrix)Xts.get(j);
						U = Us.get(rc_schema.get(1,j));
					} else {
						X = (CSCMatrix)Xs.get(j);
						U = Us.get(rc_schema.get(0,j));
					}
					DoubleMatrix1D data = X.data;
					IntMatrix1D indptr = X.indptr;
					IntMatrix1D indices = X.indices;
					
					int ind_i0 = indptr.get(i);
					int ind_i1 = indptr.get(i+1);
					if (ind_i0 == ind_i1) {
						continue;
					}
					
					IntMatrix1D inds_i = IntFactory1D.dense.make(ind_i1-ind_i0);
					DoubleMatrix1D data_i = DoubleFactory1D.dense.make(ind_i1-ind_i0);
					for (int i2 = ind_i0; i2 < ind_i1; i2++) {
						inds_i.set(i2-ind_i0, indices.get(i2));
						data_i.set(i2-ind_i0, data.get(i2));
					}
					
					if (((String)modes.get(j)).equals("densemf")) {	 // square loss, dense binary representation
						Object UtU = UtUs.get(j);
						
					}
					

				}
			}
			
		}

		

		
	}

}
