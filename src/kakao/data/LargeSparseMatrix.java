package kakao.data;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.sparse.CompressedVector;

class sparse_entry {
	int id;
	double value;
}

class sparse_row {
	sparse_entry data;
	int size;
}

public class LargeSparseMatrix {

}
