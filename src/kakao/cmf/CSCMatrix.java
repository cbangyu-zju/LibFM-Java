package kakao.cmf;

import cern.colt.matrix.tint.IntFactory1D;
import cern.colt.matrix.tint.IntMatrix1D;	// dense vector (int)
import cern.colt.matrix.tint.IntFactory2D;
import cern.colt.matrix.tint.IntMatrix2D;	// dense vector (int)
import cern.colt.matrix.tdouble.DoubleFactory1D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;		// dense vector (double)
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;		// dense matrix (double)

public class CSCMatrix {
	
	public DoubleMatrix1D data;
	public IntMatrix1D indptr;
	public IntMatrix1D indices;
	
	public CSCMatrix() {
		
	}

}
