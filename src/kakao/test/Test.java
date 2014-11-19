package kakao.test;
import kakao.matrix.LargeSparseMatrix;
import kakao.data.SparseRow;

import static org.junit.Assert.*;
import java.io.IOException;

// import java.lang.instrument.Instrumentation;
import net.sourceforge.sizeof.*;

/*
class ObjectSizeFetcher {
	private static Instrumentation instrumentation;
	
	public static void premain(String args, Instrumentation inst) {
		instrumentation = inst;
	}
	
	public static long getObjectSize(Object o) {
		return instrumentation.getObjectSize(o);
	}
}
*/


public class Test {
	
	private static long usedMemory() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	public static void main(String[] args) throws IOException {
		/*
		long sample = 82L;
		int integer = 4;
		SparseRow row = new SparseRow();
		row.add(2,1.0);
		row.add(10001,1.0);
		row.add(10009,1.0);
		row.add(10013,1.0);
		row.add(10018,1.0);
		row.add(10044,1.0);
		row.add(10049,1.0);
		row.add(10055,1.0);
		SizeOf.skipStaticField(true);
		SizeOf.skipFinalField(true);
		SizeOf.skipFlyweightObject(true);
		System.out.println(SizeOf.deepSizeOf(row));
		final int MAPPING_SIZE = 1 << 12;
		System.out.println(MAPPING_SIZE);
		*/
		// System.out.println(ObjectSizeFetcher.getObjectSize(sample));
		/*
		LargeSparseMatrix matrix = new LargeSparseMatrix("sparse.test", 100*100, 100*100);
		matrix.set(1, 2, (byte)11);
		System.out.println(matrix.get(1,2));
		*/

		long start = System.nanoTime();
		final long used0 = usedMemory();
		LargeSparseMatrix matrix = new LargeSparseMatrix("ldm.test", 1000*100, 1000*100);
		for (int i = 0; i < matrix.numCols(); i++) {
			matrix.set(i, i, i);
		}
		for (int i = 0; i < matrix.numCols(); i++) {
			assertEquals(i, matrix.get(i,i), 0.0);
		}
		long time = System.nanoTime() - start;
		final long used = usedMemory() - used0;
		if (used == 0) {
			System.err.println("You need to use -XX:-UseTLAB to see small changes in memory usage.");
		}
		System.out.printf("Setting the diagonal took %,d ms, Heap used is %,d KB%n", time/100/100, used/1024);
		matrix.close();
	}

}
