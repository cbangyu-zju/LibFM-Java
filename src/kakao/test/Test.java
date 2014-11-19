package kakao.test;
import kakao.matrix.LargeSparseMatrix;
import kakao.data.SparseRow;

import static org.junit.Assert.*;
import java.io.IOException;

// import java.lang.instrument.Instrumentation;
import net.sourceforge.sizeof.*;
import vanilla.java.collections.*;
import vanilla.java.collections.api.HugeArrayList;
import java.util.List;

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

		/*	Using memory-mapping via LargeSparseMatrix example
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
		*/
		
		// create a huge array of MutableBoolean
		/*
		HugeArrayList<MutableTypes> hugeList = new HugeArrayBuilder<MutableTypes>() {}.create();
		List<MutableTypes> list = hugeList;

		hugeList.setSize(500*1000*1000); // increase the capacity to 500M

		// give all the elements values.
		int i = 0;
		for (MutableTypes mb : list) {
			SparseRow sp = new SparseRow();
			sp.add(1,1.0);
		    mb.setSparseRow(sp[i % sp.size]);
		    i++;
		}

		// retrieve all the values.
		for (MutableTypes mb : list) {
		    boolean b1 = mb.getBoolean();
		    Boolean b2 = mb.getBoolean2();
		    byte b3 = mb.getByte();
		    Byte b4 = mb.getByte2();
		    char ch = mb.getChar();
		    short s = mb.getShort();
		    int i = mb.getInt();
		    float f = mb.getFloat();
		    long l = mb.getLong();
		    double d = mb.getDouble();
		    ElementType et = mb.getElementType();
		    String text = mb.getString();
		}
		*/
		
		
	}

}

interface MutableTypes {
    public void setBoolean(boolean b);
    public boolean getBoolean();

    public void setBoolean2(Boolean b);
    public Boolean getBoolean2();

    public void setByte(byte b);
    public byte getByte();

    public void setByte2(Byte b);
    public Byte getByte2();

    public void setChar(char ch);
    public char getChar();

    public void setShort(short s);
    public short getShort();

    public void setInt(int i);
    public int getInt();

    public void setFloat(float f);
    public float getFloat();

    public void setLong(long l);
    public long getLong();

    public void setDouble(double d);
    public double getDouble();

    public void setSparseRow(SparseRow elementType);
    public SparseRow getSparseRow();

    public void setString(String text);
    public String getString();
}
